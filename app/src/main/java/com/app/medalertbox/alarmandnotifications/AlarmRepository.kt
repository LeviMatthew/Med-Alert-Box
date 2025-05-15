package com.app.medalertbox.alarmandnotifications


import kotlinx.coroutines.flow.Flow


class AlarmRepository(private val alarmNotificationDao: AlarmNotificationDao) {
    val allAlarms: Flow<List<AlarmNotification>> = alarmNotificationDao.getAllAlarms()


    suspend fun insert(alarm: AlarmNotification) {
        alarmNotificationDao.insert(alarm)
    }


    suspend fun update(alarm: AlarmNotification) {
        alarmNotificationDao.update(alarm)
    }


    suspend fun delete(alarm: AlarmNotification) {
        alarmNotificationDao.delete(alarm)
    }
}