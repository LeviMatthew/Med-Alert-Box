package com.app.medalertbox.alarmandnotifications


import androidx.room.*
import kotlinx.coroutines.flow.Flow


@Dao
interface AlarmNotificationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(alarm: AlarmNotification)


    @Update
    suspend fun update(alarm: AlarmNotification)


    @Delete
    suspend fun delete(alarm: AlarmNotification)


    // Fetch alarms as a Flow (used in ViewModel & Live UI)
    @Query("SELECT * FROM alarm_notifications ORDER BY date ASC, time ASC")
    fun getAllAlarms(): Flow<List<AlarmNotification>>


    // Fetch alarms as a List (used in BootReceiver for rescheduling)
    @Query("SELECT * FROM alarm_notifications WHERE isActive = 1 ORDER BY date ASC, time ASC")
    suspend fun getAllAlarmsList(): List<AlarmNotification>


}