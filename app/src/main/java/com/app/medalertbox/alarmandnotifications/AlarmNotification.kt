package com.app.medalertbox.alarmandnotifications

// AlarmNotification.kt
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "alarm_notifications")
data class AlarmNotification(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val medicationName: String,
    val time: String, // Format: "hh:mm a" (e.g., "02:30 PM")
    val date: String, // Format: "MM/dd/yyyy"
    val isActive: Boolean = true
)