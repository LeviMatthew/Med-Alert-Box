package com.app.medalertbox.alarmandnotifications

import android.content.*
import android.widget.Toast
import androidx.core.app.NotificationManagerCompat

class AlarmActionReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action ?: return
        val alarmId = intent.getIntExtra("alarm_id", -1)
        val medicationName = intent.getStringExtra("medication_name") ?: "Medication"

        if (alarmId == -1) return

        when (action) {
            "STOP" -> {
                AlarmSoundPlayer.stop(context)
                NotificationManagerCompat.from(context).cancel(alarmId)
                AlarmScheduler(context).cancelAlarm(alarmId)
                Toast.makeText(context, "Alarm stopped", Toast.LENGTH_SHORT).show()
            }

            "SNOOZE" -> {
                AlarmSoundPlayer.stop(context)
                NotificationManagerCompat.from(context).cancel(alarmId)
                val snoozeTime = System.currentTimeMillis() + 5 * 60 * 1000
                AlarmScheduler(context).scheduleSnooze(alarmId, medicationName, snoozeTime)
                Toast.makeText(context, "Snoozed for 5 minutes", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
