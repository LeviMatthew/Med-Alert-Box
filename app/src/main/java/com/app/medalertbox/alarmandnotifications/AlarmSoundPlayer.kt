package com.app.medalertbox.alarmandnotifications

import android.content.Context
import android.media.Ringtone
import android.media.RingtoneManager
import android.net.Uri
import android.util.Log

object AlarmSoundPlayer {
    private var ringtone: Ringtone? = null

    fun play(context: Context) {
        try {
            val alarmUri: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
                ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

            ringtone = RingtoneManager.getRingtone(context, alarmUri)
            ringtone?.play()

            Log.d("AlarmSoundPlayer", "Alarm ringtone playing.")
        } catch (e: Exception) {
            Log.e("AlarmSoundPlayer", "Failed to play alarm sound", e)
        }
    }

    fun stop(context: Context) {
        try {
            ringtone?.stop()
            ringtone = null
            Log.d("AlarmSoundPlayer", "Alarm ringtone stopped.")
        } catch (e: Exception) {
            Log.e("AlarmSoundPlayer", "Failed to stop alarm sound", e)
        }
    }
}
