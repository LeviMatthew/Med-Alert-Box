package com.app.medalertbox.alarmandnotifications


import androidx.room.Database
import androidx.room.RoomDatabase


@Database(entities = [AlarmNotification::class], version = 1, exportSchema = false)
abstract class AlarmDatabase : RoomDatabase() {
    abstract fun alarmNotificationDao(): AlarmNotificationDao


}
