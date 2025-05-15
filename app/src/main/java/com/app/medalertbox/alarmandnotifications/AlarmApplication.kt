package com.app.medalertbox.alarmandnotifications


import android.app.Application
import android.util.Log
import androidx.room.Room


class AlarmApplication : Application() {
    companion object {
        private const val TAG = "AlarmApplication"
        private const val DATABASE_NAME = "alarm_database"


        @Volatile
        private var INSTANCE: AlarmDatabase? = null


        fun getDatabase(application: Application): AlarmDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    application.applicationContext,
                    AlarmDatabase::class.java,
                    DATABASE_NAME
                )
                    .fallbackToDestructiveMigration() // Handles schema changes
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }


    val database: AlarmDatabase by lazy {
        getDatabase(this)
    }


    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "Application onCreate - Database initialized successfully")
    }
}