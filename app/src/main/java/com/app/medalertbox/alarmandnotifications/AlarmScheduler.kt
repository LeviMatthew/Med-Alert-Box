package com.app.medalertbox.alarmandnotifications

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import java.util.*

class AlarmScheduler(private val context: Context) {

    fun scheduleAlarm(alarm: AlarmNotification) {
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("alarm_id", alarm.id.toString())
            putExtra("medication_name", alarm.medicationName)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            alarm.id,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val calendar = Calendar.getInstance().apply {
            val (hourStr, minutePart) = alarm.time.split(":")
            val hour = hourStr.toInt()
            val minute = minutePart.substring(0, 2).toInt()
            val amPm = minutePart.substring(3).trim().uppercase(Locale.ROOT)

            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            set(Calendar.MINUTE, minute)

            when {
                amPm == "PM" && hour < 12 -> set(Calendar.HOUR_OF_DAY, hour + 12)
                amPm == "AM" && hour == 12 -> set(Calendar.HOUR_OF_DAY, 0)
                else -> set(Calendar.HOUR_OF_DAY, hour)
            }

            if (before(Calendar.getInstance())) {
                add(Calendar.DATE, 1)
            }
        }

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            pendingIntent
        )
    }

    fun scheduleSnooze(alarmId: Int, medicationName: String, snoozeMillis: Long) {
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("alarm_id", alarmId.toString())
            putExtra("medication_name", medicationName)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            alarmId + 1000,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            snoozeMillis,
            pendingIntent
        )
    }

    fun cancelAlarm(alarmId: Int) {
        val intent = Intent(context, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            alarmId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(pendingIntent)
    }
}