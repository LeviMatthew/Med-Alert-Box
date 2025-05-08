package com.app.medalertbox.alarmandnotifications

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Vibrator
import android.util.Log
import android.view.WindowManager
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.app.medalertbox.R

class FullScreenAlarmActivity : AppCompatActivity() {

    private lateinit var alarmId: String
    private lateinit var vibrator: Vibrator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_full_screen_alarm)

        // Full-screen mode
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        window.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        // Get alarm ID
        alarmId = intent.getStringExtra("alarm_id") ?: "default_id"
        Log.d("FullScreenAlarmActivity", "Alarm ID: $alarmId")

        // Init vibrator
        vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

        // Buttons
        val btnSnooze: Button = findViewById(R.id.btnSnooze)
        val btnStop: Button = findViewById(R.id.btnStop)

        btnSnooze.setOnClickListener {
            stopAlarmSound() // stop the ringing
            snoozeAlarm(5)   // snooze for 5 minutes
            finish()         // close the full-screen UI
        }

        btnStop.setOnClickListener {
            Log.d("FullScreenAlarmActivity", "Stop pressed")
            stopAlarmCompletely()
            finish()
        }
    }

    @SuppressLint("ScheduleExactAlarm")
    private fun snoozeAlarm(minutes: Int) {
        val snoozeTimeInMillis = System.currentTimeMillis() + (minutes * 60 * 1000)

        val snoozeIntent = Intent(this, AlarmReceiver::class.java).apply {
            action = "SNOOZED_ALARM"
            putExtra("alarm_id", alarmId)
        }

        val snoozePendingIntent = PendingIntent.getBroadcast(
            this,
            alarmId.hashCode(),
            snoozeIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            snoozeTimeInMillis,
            snoozePendingIntent
        )

        Toast.makeText(this, "Snoozed for $minutes minutes", Toast.LENGTH_SHORT).show()
        Log.d("FullScreenAlarmActivity", "Snooze set for $minutes minutes.")
    }


    private fun stopAlarmSound() {
        val serviceIntent = Intent(this, AlarmRingtoneService::class.java)
        stopService(serviceIntent)
        Log.d("FullScreenAlarmActivity", "Alarm sound stopped.")
    }

    private fun stopAlarmCompletely() {
        stopAlarmSound()

        val cancelIntent = Intent(this, AlarmReceiver::class.java).apply {
            action = "STOP_ALARM"
            putExtra("alarm_id", alarmId)
        }

        val cancelPendingIntent = PendingIntent.getBroadcast(
            this,
            alarmId.hashCode(),
            cancelIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(cancelPendingIntent)

        Toast.makeText(this, "Alarm stopped", Toast.LENGTH_SHORT).show()
        Log.d("FullScreenAlarmActivity", "Alarm stopped and pending intents canceled.")
    }
}
