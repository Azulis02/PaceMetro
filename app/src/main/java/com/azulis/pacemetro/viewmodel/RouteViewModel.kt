package com.azulis.pacemetro.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.azulis.pacemetro.audio.MetronomeEngine
import com.azulis.pacemetro.model.RouteSegment
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

class RouteViewModel(application: Application) : AndroidViewModel(application) {

    private val engine = MetronomeEngine(application)
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private var routeJob: Job? = null

    // Lista de segmentos que el usuario construye
    private val _segments = MutableStateFlow<List<RouteSegment>>(emptyList())
    val segments: StateFlow<List<RouteSegment>> = _segments.asStateFlow()

    // Segmento activo durante la reproducción
    private val _activeSegmentIndex = MutableStateFlow<Int>(-1)
    val activeSegmentIndex: StateFlow<Int> = _activeSegmentIndex.asStateFlow()

    // Segundos restantes del segmento actual
    private val _secondsRemaining = MutableStateFlow(0)
    val secondsRemaining: StateFlow<Int> = _secondsRemaining.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    fun addSegment(segment: RouteSegment) {
        _segments.value = _segments.value + segment
    }

    fun removeSegment(index: Int) {
        _segments.value = _segments.value.toMutableList().also { it.removeAt(index) }
    }

    fun moveSegmentUp(index: Int) {
        if (index <= 0) return
        val list = _segments.value.toMutableList()
        val temp = list[index]
        list[index] = list[index - 1]
        list[index - 1] = temp
        _segments.value = list
    }

    fun moveSegmentDown(index: Int) {
        val list = _segments.value.toMutableList()
        if (index >= list.size - 1) return
        val temp = list[index]
        list[index] = list[index + 1]
        list[index + 1] = temp
        _segments.value = list
    }

    fun playRoute() {
        if (_segments.value.isEmpty()) return
        routeJob?.cancel()
        _isPlaying.value = true

        routeJob = scope.launch {
            for ((index, segment) in _segments.value.withIndex()) {
                _activeSegmentIndex.value = index
                engine.bpm = segment.bpm
                engine.start()

                // Cuenta regresiva del segmento
                for (s in segment.durationSeconds downTo 1) {
                    _secondsRemaining.value = s
                    delay(1000)
                }
                engine.stop()
            }
            // Ruta terminada
            _isPlaying.value = false
            _activeSegmentIndex.value = -1
            _secondsRemaining.value = 0
        }
    }

    fun stopRoute() {
        routeJob?.cancel()
        engine.stop()
        _isPlaying.value = false
        _activeSegmentIndex.value = -1
        _secondsRemaining.value = 0
    }

    override fun onCleared() {
        super.onCleared()
        engine.release()
        scope.cancel()
    }
}