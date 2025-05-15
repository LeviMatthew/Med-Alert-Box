package com.app.medalertbox.alarmandnotifications


import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.app.medalertbox.R


class AlarmService : Service() {


    private val notificationChannelId = "AlarmServiceChannel"
    private val notificationId = 1


    override fun onCreate() {
        super.onCreate()
        Log.d("AlarmService", "Foreground service started for alarm handling")
        startForegroundService()
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("AlarmService", "Service is running in foreground mode")
        return START_STICKY
    }


    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                notificationChannelId,
                "Alarm Service",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(channel)
        }
    }


    private fun startForegroundService() {
        createNotificationChannel()


        val notification: Notification = NotificationCompat.Builder(this, notificationChannelId)
            .setContentTitle("Medication Reminder")
            .setContentText("Your alarms are running in the background.")
            .setSmallIcon(R.drawable.ic_alarm)
            .setPriority(NotificationCompat.PRIORITY_LOW) // Set priority
            .build()


        startForeground(notificationId, notification)
    }


    override fun onBind(intent: Intent?): IBinder? {
        return null
    }


    override fun onDestroy() {
        Log.d("AlarmService", "Foreground service stopped")
        super.onDestroy()
    }
}