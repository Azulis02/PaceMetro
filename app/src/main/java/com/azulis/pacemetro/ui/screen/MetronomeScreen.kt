package com.azulis.pacemetro.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.azulis.pacemetro.viewmodel.PaceMetroViewModel

@Composable
fun MetronomeScreen(
    modifier: Modifier = Modifier,
    vm: PaceMetroViewModel = viewModel()
) {

    val bpm by vm.bpm.collectAsState()
    val isPlaying by vm.isPlaying.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // BPM display
        Text(
            text = "$bpm",
            fontSize = 80.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = "BPM",
            fontSize = 18.sp,
            color = MaterialTheme.colorScheme.secondary
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Slider de BPM
        Slider(
            value = bpm.toFloat(),
            onValueChange = { vm.setBpm(it.toInt()) },
            valueRange = 40f..220f,
            modifier = Modifier.fillMaxWidth()
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("40", fontSize = 12.sp)
            Text("220", fontSize = 12.sp)
        }

        Spacer(modifier = Modifier.height(48.dp))

        // Botón Play / Stop
        Button(
            onClick = { vm.togglePlayStop() },
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
        ) {
            Text(
                text = if (isPlaying) "⏹ Stop" else "▶ Play",
                fontSize = 22.sp
            )
        }
    }
}