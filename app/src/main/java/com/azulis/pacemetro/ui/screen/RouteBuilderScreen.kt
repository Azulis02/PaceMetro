package com.azulis.pacemetro.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.azulis.pacemetro.model.RouteSegment
import com.azulis.pacemetro.viewmodel.RouteViewModel

@Composable
fun RouteBuilderScreen(
    modifier: Modifier = Modifier,
    vm: RouteViewModel = viewModel()
) {
    val segments by vm.segments.collectAsState()
    val isPlaying by vm.isPlaying.collectAsState()
    val activeIndex by vm.activeSegmentIndex.collectAsState()
    val secondsRemaining by vm.secondsRemaining.collectAsState()

    // Estado del formulario para añadir segmento
    var newBpm by remember { mutableIntStateOf(150) }
    var newMinutesText by remember { mutableStateOf("3") }
    var newSecondsText by remember { mutableStateOf("0") }
    var newLabel by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text("Mi ruta", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(16.dp))

        // ── Formulario para añadir segmento ──────────────────────────
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp)) {

                Text("Añadir tramo", fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(8.dp))

                OutlinedTextField(
                    value = newLabel,
                    onValueChange = { newLabel = it },
                    label = { Text("Etiqueta (ej: Carrera)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(Modifier.height(8.dp))

                // BPM
                Text("BPM: $newBpm")
                Slider(
                    value = newBpm.toFloat(),
                    onValueChange = { newBpm = it.toInt() },
                    valueRange = 40f..220f
                )

                // Duración
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = newMinutesText,
                        onValueChange = { input ->
                            if (input.isEmpty() || (input.toIntOrNull() != null && input.toInt() <= 59)) {
                                newMinutesText = input
                            }
                        },
                        label = { Text("Min") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = newSecondsText,
                        onValueChange = { input ->
                            if (input.isEmpty() || (input.toIntOrNull() != null && input.toInt() <= 59)) {
                                newSecondsText = input
                            }
                        },
                        label = { Text("Seg") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }

                Spacer(Modifier.height(8.dp))
                Button(
                    onClick = {
                        val totalSeconds = (newMinutesText.toIntOrNull() ?: 0) * 60 + (newSecondsText.toIntOrNull() ?: 0)
                        if (totalSeconds > 0) {
                            vm.addSegment(
                                RouteSegment(
                                    bpm = newBpm,
                                    durationSeconds = totalSeconds,
                                    label = newLabel.ifBlank { "${newBpm} BPM" }
                                )
                            )
                            newLabel = ""
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("+ Añadir tramo")
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // ── Lista de segmentos ────────────────────────────────────────
        LazyColumn(modifier = Modifier.weight(1f)) {
            itemsIndexed(segments) { index, segment ->
                val isActive = isPlaying && index == activeIndex
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isActive)
                            MaterialTheme.colorScheme.primaryContainer
                        else
                            MaterialTheme.colorScheme.surface
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(segment.label, fontWeight = FontWeight.Bold)
                            val min = segment.durationSeconds / 60
                            val sec = segment.durationSeconds % 60
                            Text(
                                "${segment.bpm} BPM  ·  ${min}m ${sec}s",
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.secondary
                            )
                            if (isActive) {
                                Text(
                                    "⏱ ${secondsRemaining}s restantes",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        if (!isPlaying) {
                            IconButton(onClick = { vm.moveSegmentUp(index) }) {
                                Icon(Icons.Default.KeyboardArrowUp, contentDescription = "Subir")
                            }
                            IconButton(onClick = { vm.moveSegmentDown(index) }) {
                                Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Bajar")
                            }
                            IconButton(onClick = { vm.removeSegment(index) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Borrar")
                            }
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // ── Botón Play / Stop ruta ────────────────────────────────────
        Button(
            onClick = { if (isPlaying) vm.stopRoute() else vm.playRoute() },
            enabled = segments.isNotEmpty(),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Text(
                text = if (isPlaying) "⏹ Detener ruta" else "▶ Iniciar ruta",
                fontSize = 18.sp
            )
        }
    }
}