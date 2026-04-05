package com.azulis.pacemetro.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import com.azulis.pacemetro.R
import kotlinx.coroutines.*

class MetronomeEngine(context: Context) {

    private val soundPool: SoundPool
    private val soundId: Int
    private val scope = CoroutineScope(Dispatchers.Default)
    private var job: Job? = null

    var bpm: Int = 120
        set(value) { field = value.coerceIn(40, 220) }

    var isPlaying: Boolean = false
        private set

    init {
        val attributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_MEDIA)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        soundPool = SoundPool.Builder()
            .setMaxStreams(1)
            .setAudioAttributes(attributes)
            .build()

        soundId = soundPool.load(context, R.raw.click, 1)
    }

    fun start() {
        if (isPlaying) return
        isPlaying = true
        job = scope.launch {
            // Usamos nanoTime para compensar el drift acumulado
            var nextBeat = System.nanoTime()
            while (isActive) {
                soundPool.play(soundId, 1f, 1f, 0, 0, 1f)
                nextBeat += 60_000_000_000L / bpm
                val delayMs = (nextBeat - System.nanoTime()) / 1_000_000
                if (delayMs > 0) delay(delayMs)
            }
        }
    }

    fun stop() {
        isPlaying = false
        job?.cancel()
        job = null
    }

    fun release() {
        stop()
        soundPool.release()
        scope.cancel()
    }
}