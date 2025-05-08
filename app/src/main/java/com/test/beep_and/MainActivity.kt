package com.test.beep_and

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.app.NotificationCompat
import androidx.core.view.WindowCompat
import androidx.lifecycle.ViewModelProvider
import com.test.beep_and.feature.data.core.nfc.NFC
import com.test.beep_and.feature.data.core.nfc.dataStore
import com.test.beep_and.feature.screen.home.HomeViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking


class MainActivity : ComponentActivity() {
    private var nfcAdapter: NfcAdapter? = null
    private var pendingIntent: PendingIntent? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        val insetsController = WindowCompat.getInsetsController(window, window.decorView)
        insetsController.isAppearanceLightStatusBars = true
        insetsController.isAppearanceLightNavigationBars = true

        window.setBackgroundDrawable(null)
        window.decorView.setBackgroundColor(android.graphics.Color.TRANSPARENT)

        setContent { App() }

        if (isNfcIntent(intent)) {
            checkNfcSettingAndProcess(intent)
        } else {
            handleIntent(intent)
        }
    }

    private fun isNfcIntent(intent: Intent?): Boolean {
        val action = intent?.action
        return action == NfcAdapter.ACTION_TAG_DISCOVERED ||
                action == NfcAdapter.ACTION_TECH_DISCOVERED ||
                action == NfcAdapter.ACTION_NDEF_DISCOVERED ||
                intent?.hasExtra("nfc_tag") == true
    }

    private fun checkNfcSettingAndProcess(intent: Intent?) {
        val nfcEnabled = getNfcBlocking(this)

        if (nfcEnabled) {
            handleIntent(intent)
        } else {
            Toast.makeText(this, "NFC 기능이 비활성화되어 있습니다.", Toast.LENGTH_SHORT).show()
            Log.d("NFC_CHECK", "NFC is disabled in settings, ignoring NFC intent")

            if (isTaskRoot) {
                moveTaskToBack(true)
            } else {
                finish()
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)

        if (isNfcIntent(intent)) {
            checkNfcSettingAndProcess(intent)
        } else {
            handleIntent(intent)
        }
    }

    private fun handleIntent(intent: Intent?) {
        val action = intent?.action

        if (action == NfcAdapter.ACTION_TAG_DISCOVERED ||
            action == NfcAdapter.ACTION_TECH_DISCOVERED ||
            action == NfcAdapter.ACTION_NDEF_DISCOVERED ||
            intent?.hasExtra("nfc_tag") == true
        ) {
            val serialFromTag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                intent.getParcelableExtra(NfcAdapter.EXTRA_TAG, Tag::class.java)
            } else {
                @Suppress("DEPRECATION")
                intent.getParcelableExtra(NfcAdapter.EXTRA_TAG)
            }?.id?.joinToString(":") { "%02X".format(it) }


            val serialFromExtra = intent.getStringExtra("nfc_tag")
            val serial = serialFromTag ?: serialFromExtra

            Log.d("NFC_INTENT", "handleIntent called with serial: $serial")

            if (serial == null) {
                Toast.makeText(this, "NFC 태그 없음", Toast.LENGTH_SHORT).show()
                return
            }

            val serviceIntent = Intent(this, NfcService::class.java).apply {
                putExtra("nfc_tag", serial)
            }
            startService(serviceIntent)


            val viewModel = ViewModelProvider(
                this,
                ViewModelProvider.AndroidViewModelFactory.getInstance(application)
            )[HomeViewModel::class.java]

            when (serial) {
                "1D:94:A0:84:07:10:80" -> {
                    viewModel.submitAttendance("PROJECT3", true)
                }
                "1D:93:A0:84:07:10:80" -> {
                    viewModel.submitAttendance("PROJECT4", true)
                }
                "1D:8D:A0:84:07:10:80" -> {
                    viewModel.submitAttendance("PROJECT5", true)
                }
                "1D:90:A0:84:07:10:80" -> {
                    viewModel.submitAttendance("PROJECT6", true)
                }
                "1D:8F:A0:84:07:10:80" -> {
                    viewModel.submitAttendance("LAB15_16", true)
                }
                "1D:8E:A0:84:07:10:80" -> {
                    viewModel.submitAttendance("LAB17_18", true)
                }
                "1D:8C:A0:84:07:10:80" -> {
                    viewModel.submitAttendance("LAB19_20", true)
                }
                "1D:91:A0:84:07:10:80" -> {
                    viewModel.submitAttendance("LAB21_22", true)
                }
                else -> {
                    Toast.makeText(this, "알 수 없는 태그입니다.", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            Log.d("NFC_INTENT", "Non-NFC intent received")
        }
    }

    private fun getNfcBlocking(context: Context): Boolean {
        return runBlocking {
            try {
                val preferences = context.dataStore.data.first()
                preferences[NFC] ?: false
            } catch (e: Exception) {
                Log.d("NFC_ERROR", "$e")
                false
            }
        }
    }
}


class NfcService : Service() {
    private val NOTIFICATION_ID = 1001
    private val CHANNEL_ID = "nfc_service_channel"

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val serial = intent?.getStringExtra("nfc_tag")
        Log.d("NFC_SERVICE", "Received tag: $serial")

        if (serial != null) {
            showNotificationWithIntent(serial)
        }
        return START_NOT_STICKY
    }

    private fun showNotificationWithIntent(tagSerial: String) {
        val tapIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("nfc_tag", tagSerial)
        }

        val pendingIntent = PendingIntent.getActivity(
            this, 0, tapIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("NFC 태그 감지됨")
            .setContentText("탭하여 앱 열기")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        startForeground(NOTIFICATION_ID, notification)
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "NFC 서비스 채널",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)
    }

    override fun onBind(intent: Intent?): IBinder? = null
}

