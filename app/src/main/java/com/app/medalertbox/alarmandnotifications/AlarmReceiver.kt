package com.app.medalertbox.alarmandnotifications

import android.app.PendingIntent
import android.content.*
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.app.medalertbox.R

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val alarmIdStr = intent.getStringExtra("alarm_id") ?: return
        val medicationName = intent.getStringExtra("medication_name") ?: "Medication"
        val alarmId = alarmIdStr.toIntOrNull() ?: return

        Log.d("AlarmReceiver", "Alarm triggered: $alarmId - $medicationName")

        AlarmSoundPlayer.play(context)
        showNotification(context, alarmId, medicationName)

        // Launch full-screen alarm activity
        val fullScreenIntent = Intent(context, FullScreenAlarmActivity::class.java).apply {
            putExtra("alarm_id", alarmIdStr)
            putExtra("medication_name", medicationName)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        context.startActivity(fullScreenIntent)
    }

    private fun showNotification(context: Context, alarmId: Int, medicationName: String) {
        NotificationHelper.createChannel(context)

        val stopIntent = Intent(context, AlarmActionReceiver::class.java).apply {
            action = "STOP"
            putExtra("alarm_id", alarmId)
        }

        val snoozeIntent = Intent(context, AlarmActionReceiver::class.java).apply {
            action = "SNOOZE"
            putExtra("alarm_id", alarmId)
            putExtra("medication_name", medicationName)
        }

        val stopPendingIntent = PendingIntent.getBroadcast(
            context, alarmId + 10, stopIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val snoozePendingIntent = PendingIntent.getBroadcast(
            context, alarmId + 20, snoozeIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val fullScreenIntent = Intent(context, FullScreenAlarmActivity::class.java).apply {
            putExtra("alarm_id", alarmId.toString())
            putExtra("medication_name", medicationName)
        }

        val fullScreenPendingIntent = PendingIntent.getActivity(
            context, alarmId + 30, fullScreenIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, NotificationHelper.CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notif)
            .setContentTitle("Medication Reminder")
            .setContentText("Time to take: $medicationName")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setVibrate(longArrayOf(0, 500, 500, 500))
            .setAutoCancel(true)
            .setFullScreenIntent(fullScreenPendingIntent, true)
            .addAction(R.drawable.ic_stop, "Stop", stopPendingIntent)
            .addAction(R.drawable.ic_snooze, "Snooze", snoozePendingIntent)
            .build()

        NotificationManagerCompat.from(context).notify(alarmId, notification)
    }
}