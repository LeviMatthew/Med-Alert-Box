package com.app.medalertbox.alarmandnotifications

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationManagerCompat
import com.app.medalertbox.R

class FullScreenAlarmActivity : AppCompatActivity() {

    private lateinit var alarmId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_full_screen_alarm)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        window.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        alarmId = intent.getStringExtra("alarm_id") ?: "default_id"
        Log.d("FullScreenAlarmActivity", "Alarm ID: $alarmId")

        val btnSnooze: Button = findViewById(R.id.btnSnooze)
        val btnStop: Button = findViewById(R.id.btnStop)

        btnSnooze.setOnClickListener { performSnooze() }
        btnStop.setOnClickListener { performStop() }
    }

    @SuppressLint("ScheduleExactAlarm")
    private fun performSnooze() {
        AlarmSoundPlayer.stop(this)

        NotificationManagerCompat.from(this).cancel(alarmId.hashCode())

        val snoozeTime = System.currentTimeMillis() + 5 * 60 * 1000 // 5 minutes
        AlarmHelper.setSnoozeAlarm(this, snoozeTime, alarmId)

        Toast.makeText(this, "Snoozed for 5 minutes", Toast.LENGTH_SHORT).show()
        Log.d("FullScreenAlarmActivity", "Alarm snoozed for 5 minutes")

        finish()
    }

    private fun performStop() {
        AlarmSoundPlayer.stop(this)

        NotificationManagerCompat.from(this).cancel(alarmId.hashCode())

        cancelAlarm()

        Toast.makeText(this, "Alarm stopped", Toast.LENGTH_SHORT).show()
        Log.d("FullScreenAlarmActivity", "Alarm stopped")

        finish()
    }

    private fun cancelAlarm() {
        val cancelIntent = Intent(this, AlarmReceiver::class.java).apply {
            action = "STOP_ALARM"
            putExtra("alarm_id", alarmId)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            this,
            alarmId.hashCode(),
            cancelIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(pendingIntent)

        sendBroadcast(cancelIntent)

        Log.d("FullScreenAlarmActivity", "Canceled alarm $alarmId from AlarmManager")
    }
}