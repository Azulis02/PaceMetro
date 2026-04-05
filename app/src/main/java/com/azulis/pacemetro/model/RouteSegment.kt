package com.azulis.pacemetro.model

data class RouteSegment(
    val bpm: Int,
    val durationSeconds: Int,
    val label: String = ""       // ej: "Carrera", "Caminata"
)