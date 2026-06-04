package com.soundletter.app.core.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri

actual class AudioPlayer(private val context: Context) {
    private var mediaPlayer: MediaPlayer? = null

    actual fun play(url: String, onFinished: () -> Unit) {
        if (url.isBlank()) {
            println("AUDIO_LOG: URL is blank")
            onFinished()
            return
        }
        
        try {
            stop()
            println("AUDIO_LOG: Preparing to play $url")

            mediaPlayer = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
                )
                setDataSource(url)
                prepareAsync()
                setOnPreparedListener { 
                    println("AUDIO_LOG: Started playing")
                    it.start() 
                }
                setOnCompletionListener { 
                    println("AUDIO_LOG: Playback finished")
                    onFinished()
                }
                setOnErrorListener { _, what, extra ->
                    println("AUDIO_LOG: Error $what, $extra")
                    onFinished()
                    false
                }
            }
        } catch (e: Exception) {
            println("AUDIO_LOG: Exception: ${e.message}")
            onFinished()
        }
    }

    actual fun pause() {
        try {
            mediaPlayer?.pause()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    actual fun stop() {
        try {
            mediaPlayer?.apply {
                if (isPlaying) stop()
                release()
            }
            mediaPlayer = null
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    actual fun isPlaying(): Boolean = mediaPlayer?.isPlaying ?: false

    actual fun release() {
        stop()
    }
}
