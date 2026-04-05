package com.azulis.pacemetro.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.azulis.pacemetro.audio.MetronomeEngine
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class PaceMetroViewModel(application: Application) : AndroidViewModel(application) {

    private val engine = MetronomeEngine(application)

    private val _bpm = MutableStateFlow(120)
    val bpm: StateFlow<Int> = _bpm.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    fun setBpm(value: Int) {
        _bpm.value = value
        engine.bpm = value
    }

    fun togglePlayStop() {
        if (_isPlaying.value) {
            engine.stop()
            _isPlaying.value = false
        } else {
            engine.start()
            _isPlaying.value = true
        }
    }

    override fun onCleared() {
        super.onCleared()
        engine.release() // Libera recursos cuando se destruye el ViewModel
    }
}