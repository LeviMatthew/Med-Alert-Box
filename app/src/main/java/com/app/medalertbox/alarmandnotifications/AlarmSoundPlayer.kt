package com.app.medalertbox.alarmandnotifications

import android.content.Context
import android.media.MediaPlayer
import android.os.Build
import android.os.Vibrator
import android.os.VibrationEffect
import android.util.Log
import com.app.medalertbox.R

object AlarmSoundPlayer {
    private var mediaPlayer: MediaPlayer? = null
    private var vibrator: Vibrator? = null

    fun play(context: Context) {
        stop(context)

        mediaPlayer = MediaPlayer.create(context, R.raw.alarm_clock).apply {
            isLooping = true
            start()
        }

        vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator?.vibrate(
                VibrationEffect.createWaveform(longArrayOf(0, 500, 500, 500), 0)
            )
        } else {
            vibrator?.vibrate(longArrayOf(0, 500, 500, 500), 0)
        }

        Log.d("AlarmSoundPlayer", "Alarm sound and vibration started")
    }

    fun stop(context: Context) {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null

        vibrator?.cancel()
        vibrator = null

        Log.d("AlarmSoundPlayer", "Alarm sound and vibration stopped")
    }
}