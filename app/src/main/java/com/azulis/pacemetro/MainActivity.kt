package com.azulis.pacemetro

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.azulis.pacemetro.ui.screen.MetronomeScreen
import com.azulis.pacemetro.ui.screen.RouteBuilderScreen
import com.azulis.pacemetro.ui.theme.PaceMetroTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PaceMetroTheme {
                var selectedTab by remember { mutableIntStateOf(0) }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        TabRow(
                            selectedTabIndex = selectedTab,
                            modifier = Modifier.statusBarsPadding()
                        ) {
                            Tab(
                                selected = selectedTab == 0,
                                onClick = { selectedTab = 0 },
                                text = { Text("Metrónomo") }
                            )
                            Tab(
                                selected = selectedTab == 1,
                                onClick = { selectedTab = 1 },
                                text = { Text("Ruta") }
                            )
                        }
                    }
                ) { innerPadding ->
                    when (selectedTab) {
                        0 -> MetronomeScreen(modifier = Modifier.padding(innerPadding))
                        1 -> RouteBuilderScreen(modifier = Modifier.padding(innerPadding))
                    }
                }
            }
        }
    }
}