package com.soundletter.app.core.audio

expect class AudioPlayer {
    fun play(url: String, onFinished: () -> Unit = {})
    fun pause()
    fun stop()
    fun isPlaying(): Boolean
    fun release()
}
