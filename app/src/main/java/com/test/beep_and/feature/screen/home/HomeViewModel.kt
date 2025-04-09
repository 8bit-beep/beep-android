package com.test.beep_and.feature.screen.home

import android.app.Activity
import android.content.Context
import android.nfc.NdefRecord
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.Ndef
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.test.beep_and.BeepApplication
import com.test.beep_and.feature.network.attend.AttendRequest
import com.test.beep_and.feature.network.core.NetworkErrorHandler
import com.test.beep_and.feature.network.core.remote.NoConnectivityException
import com.test.beep_and.feature.network.core.remote.RetrofitClient
import com.test.beep_and.feature.network.user.room.RoomRequest
import com.test.beep_and.feature.screen.home.model.HomePendingUiState
import com.test.beep_and.feature.screen.home.model.RoomPendingUiState
import kotlinx.coroutines.launch
import retrofit2.HttpException
import kotlin.experimental.and

class HomeViewModel : ViewModel(), NfcAdapter.ReaderCallback {

    private val _isNfcScannerVisible = MutableLiveData(false)
    val isNfcScannerVisible: LiveData<Boolean> = _isNfcScannerVisible

    private val _isScanningNow = MutableLiveData(false)
    val isScanningNow: LiveData<Boolean> = _isScanningNow

    private val _nfcScanResult = MutableLiveData<String>()

    private val _attendanceStatus = MutableLiveData<HomePendingUiState?>(null)
    val attendanceStatus: LiveData<HomePendingUiState?> = _attendanceStatus

    private val _roomStatus = MutableLiveData<RoomPendingUiState?>(null)
    val roomStatus: LiveData<RoomPendingUiState?> = _roomStatus

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
        if (_isScanningNow.value == true) {
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
                        _nfcScanResult.postValue("텍스트 레코드: $textContent")
                        submitAttendance(textContent)
                    } else {
                        _nfcScanResult.postValue("NFC 태그 ID: $tagId (텍스트 레코드 없음)")
                        submitAttendance(tagId)
                    }
                } else {
                    _nfcScanResult.postValue("NFC 태그 ID: $tagId (NDEF 메시지 없음)")
                    submitAttendance(tagId)
                }

                ndef.close()
            } catch (e: Exception) {
                Log.e("NFC", "NDEF 태그 읽기 오류", e)
                _nfcScanResult.postValue("NFC 태그 ID: $tagId (읽기 오류: ${e.message})")
                submitAttendance(tagId)
            }
        } else {
            _nfcScanResult.postValue("NFC 태그 ID: $tagId (NDEF 미지원)")
            submitAttendance(tagId)
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
        _attendanceStatus.postValue(HomePendingUiState.Loading)
        viewModelScope.launch {
            try {
                Log.d("nfc", "submitAttendance: $tagData")
                RetrofitClient.attendService.attend(AttendRequest(tagData))
                _attendanceStatus.postValue(HomePendingUiState.Success(tagData))
            } catch (e: Exception) {
                Log.e("NFC", "출석 처리 중 오류 발생", e)
                _attendanceStatus.postValue(HomePendingUiState.Error("출석 처리 중 오류가 발생했습니다: ${e.message}"))
            }
        }
    }

    fun resetAttendanceStatus() {
        _attendanceStatus.value = null
    }

    fun room(roomName: String) {
        _roomStatus.postValue(RoomPendingUiState.Loading)

        viewModelScope.launch {
            try {
                RetrofitClient.roomService.room(RoomRequest(roomName))
                _roomStatus.postValue(RoomPendingUiState.Success)
            } catch (e: NoConnectivityException) {
                Log.e("Network", "No connectivity: ${e.message}")
            } catch (e: HttpException) {
                _roomStatus.postValue(RoomPendingUiState.Error(e.message()))
                val error = NetworkErrorHandler.handle(BeepApplication.getContext(), e)
                Log.d("Network", "HTTP error: $error")
            } catch (e: Exception) {
                _attendanceStatus.postValue(HomePendingUiState.Error(e.message ?: "알 수 없는 오류가 발생했습니다."))
                Log.e("Network", "Error: ${e.message}", e)
            }
        }
    }

    fun cancelAttend() {
        _attendanceStatus.postValue(HomePendingUiState.Loading)

        viewModelScope.launch {
            try {
                RetrofitClient.attendService.cancelAttend()
                _roomStatus.postValue(RoomPendingUiState.Success)
            } catch (e: HttpException) {
                _roomStatus.postValue(RoomPendingUiState.Error(e.message()))
                val error = NetworkErrorHandler.handle(BeepApplication.getContext(), e)
                Log.d("Network", "HTTP error: $error")
            } catch (e: Exception) {
                _roomStatus.postValue(RoomPendingUiState.Error(e.message ?: "알 수 없는 오류가 발생했습니다."))
                Log.e("Network", "Error: ${e.message}", e)
            }
        }
    }
}