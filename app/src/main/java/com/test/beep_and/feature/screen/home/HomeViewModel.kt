package com.test.beep_and.feature.screen.home

import android.app.Activity
import android.content.Context
import android.nfc.NdefRecord
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.Ndef
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.test.beep_and.BeepApplication
import com.test.beep_and.feature.network.attend.AttendRequest
import com.test.beep_and.feature.network.core.NetworkErrorHandler
import com.test.beep_and.feature.network.core.remote.RetrofitClient
import com.test.beep_and.feature.network.user.room.RoomRequest
import com.test.beep_and.feature.screen.home.model.CancelPendingUiState
import com.test.beep_and.feature.screen.home.model.CancelUiState
import com.test.beep_and.feature.screen.home.model.HomePendingUiState
import com.test.beep_and.feature.screen.home.model.HomeUiState
import com.test.beep_and.feature.screen.home.model.RoomPendingUiState
import com.test.beep_and.feature.screen.home.model.RoomUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.experimental.and

class HomeViewModel : ViewModel(), NfcAdapter.ReaderCallback {

    private val _isNfcScannerVisible = MutableStateFlow(false)
    val isNfcScannerVisible: StateFlow<Boolean> = _isNfcScannerVisible

    private val _isScanningNow = MutableStateFlow(false)
    val isScanningNow: StateFlow<Boolean> = _isScanningNow

    private val _nfcScanResult = MutableStateFlow<String?>(null)

    private val _attendanceStatus = MutableStateFlow(HomeUiState())
    val attendanceStatus = _attendanceStatus.asStateFlow()

    private val _roomStatus = MutableStateFlow(RoomUiState())
    val roomState = _roomStatus.asStateFlow()

    private val _cancelStatus = MutableStateFlow(CancelUiState())
    val cancelUiState = _cancelStatus.asStateFlow()


    private var nfcAdapter: NfcAdapter? = null

    fun initNfcAdapter(context: Context): Boolean {
        nfcAdapter = NfcAdapter.getDefaultAdapter(context)
        return nfcAdapter != null && nfcAdapter!!.isEnabled
    }

    fun startNfcScan(activity: Activity) {
        if (nfcAdapter == null || !nfcAdapter!!.isEnabled) {
            Toast.makeText(activity, "NFC를 사용할 수 없습니다. NFC 설정을 확인해주세요.", Toast.LENGTH_SHORT).show()
            return
        }

        _isNfcScannerVisible.value = true
        _isScanningNow.value = true

        try {
            nfcAdapter?.enableReaderMode(
                activity,
                this,
                NfcAdapter.FLAG_READER_NFC_A or
                        NfcAdapter.FLAG_READER_NFC_B or
                        NfcAdapter.FLAG_READER_NFC_F or
                        NfcAdapter.FLAG_READER_NFC_V,
                null
            )
        } catch (e: Exception) {
            Log.e("NFC", "NFC Reader Mode 활성화 오류", e)
            stopNfcScan(activity)
            Toast.makeText(activity, "NFC 스캐너를 시작할 수 없습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    fun stopNfcScan(activity: Activity) {
        if (_isScanningNow.value) {
            try {
                nfcAdapter?.disableReaderMode(activity)
            } catch (e: Exception) {
                Log.e("NFC", "NFC Reader Mode 비활성화 오류", e)
            }
            _isScanningNow.value = false
            _isNfcScannerVisible.value = false
        }
    }

    override fun onTagDiscovered(tag: Tag) {
        val tagId = tag.id?.let { bytesToHexString(it) } ?: "Unknown"

        val ndef = Ndef.get(tag)
        if (ndef != null) {
            try {
                ndef.connect()

                val ndefMessage = ndef.cachedNdefMessage ?: ndef.ndefMessage
                if (ndefMessage != null) {
                    val records = ndefMessage.records
                    val textContent = readTextFromNdefRecords(records)

                    if (textContent.isNotEmpty()) {
                        _nfcScanResult.value = "텍스트 레코드: $textContent"
                        submitAttendance(textContent)
                    } else {
                        _nfcScanResult.value = "NFC 태그 ID: $tagId (텍스트 레코드 없음)"
                        submitAttendance(tagId)
                    }
                } else {
                    _nfcScanResult.value = "NFC 태그 ID: $tagId (NDEF 메시지 없음)"
                    submitAttendance(tagId)
                }

                ndef.close()
            } catch (e: Exception) {
                Log.e("NFC", "NDEF 태그 읽기 오류", e)
                _nfcScanResult.value = "NFC 태그 ID: $tagId (읽기 오류: ${e.message})"
                submitAttendance(tagId)
            }
        } else {
            _nfcScanResult.value = "NFC 태그 ID: $tagId (NDEF 미지원)"
            submitAttendance(tagId)
        }
    }

    fun resetAttendanceStatus() {
        _attendanceStatus.update {
            it.copy(homeUiState = HomePendingUiState.Default)
        }
    }

    private fun readTextFromNdefRecords(records: Array<NdefRecord>): String {
        val result = StringBuilder()

        for (record in records) {
            if (record.tnf == NdefRecord.TNF_WELL_KNOWN &&
                record.type.contentEquals(NdefRecord.RTD_TEXT)
            ) {

                val payload = record.payload
                val textEncoding = if ((payload[0] and 0x80.toByte()) == 0.toByte()) "UTF-8" else "UTF-16"
                val languageCodeLength = payload[0] and 0x3f.toByte()

                try {
                    val text = String(
                        payload,
                        languageCodeLength + 1,
                        payload.size - languageCodeLength - 1,
                        charset(textEncoding)
                    )

                    if (result.isNotEmpty()) {
                        result.append("\n")
                    }
                    result.append(text)
                } catch (e: Exception) {
                    Log.e("NFC", "텍스트 레코드 파싱 오류", e)
                }
            }
        }

        return result.toString()
    }

    private fun bytesToHexString(bytes: ByteArray): String {
        val sb = StringBuilder()
        for (b in bytes) {
            sb.append(String.format("%02X", b))
        }
        return sb.toString()
    }

    private fun submitAttendance(tagData: String) {
        _attendanceStatus.update {
            it.copy(homeUiState = HomePendingUiState.Loading)
        }
        viewModelScope.launch {
            try {
                RetrofitClient.attendService.attend(AttendRequest(tagData))
                _attendanceStatus.update {
                    it.copy(homeUiState = HomePendingUiState.Success(tagData))
                }

            } catch (e: Exception) {
                val error = NetworkErrorHandler.handle(BeepApplication.getContext(), e)
                _attendanceStatus.update {
                    it.copy(homeUiState = HomePendingUiState.Error(error?: "알수없는 에러가 발생했습니다"))
                }
            }
        }
    }

    fun room(roomName: String) {
        _roomStatus.update { it.copy(roomUiState = RoomPendingUiState.Loading) }

        viewModelScope.launch {
            try {
                RetrofitClient.roomService.room(RoomRequest(roomName))
                _roomStatus.update { it.copy(roomUiState = RoomPendingUiState.Success) }
            } catch (e: Exception) {
                _roomStatus.update { it.copy(roomUiState = RoomPendingUiState.Error) }
                NetworkErrorHandler.handle(BeepApplication.getContext(), e, true)
            }
        }
    }

    fun cancelAttend() {
        _cancelStatus.update {
            it.copy(cancelUiState = CancelPendingUiState.Loading)
        }

        viewModelScope.launch {
            try {
                RetrofitClient.attendService.cancelAttend()
                _cancelStatus.update {
                    it.copy(cancelUiState = CancelPendingUiState.Success)
                }
            } catch (e: Exception) {
                _cancelStatus.update {
                    NetworkErrorHandler.handle(BeepApplication.getContext(), e, true)
                    it.copy(cancelUiState = CancelPendingUiState.Error)
                }
            }
        }
    }
}