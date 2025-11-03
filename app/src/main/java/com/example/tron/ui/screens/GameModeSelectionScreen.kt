
package com.example.tron.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun GameModeSelectionScreen(
    onVsAiClick: () -> Unit,
    onVsPlayerClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Selecciona el modo de juego", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(32.dp))
        Button(onClick = onVsAiClick, modifier = Modifier.width(200.dp)) {
            Text("Jugar contra IA")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onVsPlayerClick, modifier = Modifier.width(200.dp)) {
            Text("Jugar por Bluetooth")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GameModeSelectionScreenPreview() {
    GameModeSelectionScreen(onVsAiClick = {}, onVsPlayerClick = {})
}
