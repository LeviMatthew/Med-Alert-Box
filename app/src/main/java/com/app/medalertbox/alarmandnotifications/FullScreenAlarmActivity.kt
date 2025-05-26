package com.app.medalertbox.alarmandnotifications

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.*
import android.os.*
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationManagerCompat
import com.app.medalertbox.R

class FullScreenAlarmActivity : AppCompatActivity() {

    private var alarmId: Int = -1
    private lateinit var medicationName: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_full_screen_alarm)

        window.addFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN or
                    WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON or
                    WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                    WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
        )

        alarmId = intent.getStringExtra("alarm_id")?.toIntOrNull() ?: -1
        medicationName = intent.getStringExtra("medication_name") ?: "Medication"

        findViewById<TextView>(R.id.medicationTextView).text = "💊 Time to take: $medicationName"

        findViewById<Button>(R.id.btnSnooze).setOnClickListener { snoozeAlarm() }
        findViewById<Button>(R.id.btnStop).setOnClickListener { stopAlarm() }
    }

    private fun snoozeAlarm() {
        AlarmSoundPlayer.stop(this)
        NotificationManagerCompat.from(this).cancel(alarmId)

        val snoozeTime = System.currentTimeMillis() + 5 * 60 * 1000
        AlarmScheduler(this).scheduleSnooze(alarmId, medicationName, snoozeTime)

        Toast.makeText(this, "Snoozed for 5 minutes", Toast.LENGTH_SHORT).show()
        finish()
    }

    private fun stopAlarm() {
        AlarmSoundPlayer.stop(this)
        NotificationManagerCompat.from(this).cancel(alarmId)

        AlarmScheduler(this).cancelAlarm(alarmId)
        Toast.makeText(this, "Alarm stopped", Toast.LENGTH_SHORT).show()
        finish()
    }
}
