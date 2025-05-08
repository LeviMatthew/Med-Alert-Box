package com.app.medalertbox.alarmandnotifications

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import com.app.medalertbox.R

object AlarmSoundPlayer {
    private var mediaPlayer: MediaPlayer? = null
    private var isPlaying = false

    fun start(context: Context) {
        if (isPlaying) return
        mediaPlayer = MediaPlayer.create(context, R.raw.alarm_clock)
        mediaPlayer?.isLooping = true
        mediaPlayer?.start()
        isPlaying = true
    }

    fun stop(context: Context) {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
        isPlaying = false
    }
}
