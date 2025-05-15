package com.app.medalertbox.alarmandnotifications

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.app.medalertbox.R

class AlarmRingtoneService : Service() {

    private val handler = Handler()
    private val channelId = "ALARM_RINGTONE_SERVICE"
    private val notificationId = 2

    override fun onCreate() {
        super.onCreate()
        Log.d("AlarmRingtoneService", "Service created")
        createNotificationChannel()
        startForegroundServiceWithNotification()
        startAlarmPlayback()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Alarm Sound",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Plays alarm sound in foreground"
                enableLights(true)
                enableVibration(true)
            }

            val manager = getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(channel)
        }
    }

    private fun startForegroundServiceWithNotification() {
        val notification: Notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("Alarm Playing")
            .setContentText("Tap stop or snooze to turn off alarm")
            .setSmallIcon(R.drawable.ic_alarm)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setOngoing(true)
            .build()

        startForeground(notificationId, notification)
    }

    private fun startAlarmPlayback() {
        AlarmSoundPlayer.play(this)
        Log.d("AlarmRingtoneService", "Alarm sound started. Will auto-stop in 3 minutes.")

        // Auto-stop after 3 minutes
        handler.postDelayed({
            Log.d("AlarmRingtoneService", "3 minutes passed, stopping service.")
            stopSelf()
        }, 3 * 60 * 1000)
    }

    override fun onDestroy() {
        super.onDestroy()
        AlarmSoundPlayer.stop(this)
        handler.removeCallbacksAndMessages(null)
        Log.d("AlarmRingtoneService", "Alarm stopped and service destroyed.")
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    companion object {
        fun stopSound(context: Context) {
            AlarmSoundPlayer.stop(context)
            Log.d("AlarmRingtoneService", "stopSound() called from companion object")
        }
    }
}