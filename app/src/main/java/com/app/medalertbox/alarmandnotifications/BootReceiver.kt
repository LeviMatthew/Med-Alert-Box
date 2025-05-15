package com.app.medalertbox.alarmandnotifications


import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class BootReceiver : BroadcastReceiver() {


    // This method is called when the device is rebooted
    override fun onReceive(context: Context, intent: Intent) {
        // Check if the broadcast is ACTION_BOOT_COMPLETED (i.e., device rebooted)
        if (Intent.ACTION_BOOT_COMPLETED == intent.action) {
            Log.d("BootReceiver", "Device rebooted. Rescheduling alarms...")


            // Get the database instance from the application context
            val database = (context.applicationContext as AlarmApplication).database
            val alarmDao = database.alarmNotificationDao()
            val alarmManagerHelper = AlarmScheduler(context)


            // Use coroutines to handle background operations (database fetching and scheduling alarms)
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    // Fetch all active alarms from the database
                    val alarms = alarmDao.getAllAlarmsList() // Ensure this function exists in DAO
                    for (alarm in alarms) {
                        // Only reschedule active alarms
                        if (alarm.isActive) {
                            alarmManagerHelper.scheduleAlarm(alarm) // Schedule the alarm using AlarmManager
                            Log.d("BootReceiver", "Rescheduled alarm for: ${alarm.medicationName} at ${alarm.time}")
                        }
                    }
                } catch (e: Exception) {
                    Log.e("BootReceiver", "Error rescheduling alarms: ${e.message}")
                }
            }
        }
    }
}