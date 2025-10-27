package com.example.tron.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tron.data.GameState
import com.example.tron.data.Player

@Composable
fun WinnerScreen(
    gameState: GameState,
    onPlayAgain: () -> Unit,
    onExit: () -> Unit
) {
    val finalWinnerName = gameState.finalWinner?.name ?: "Nadie"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "¡CAMPEÓN!",
            fontSize = 40.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = finalWinnerName,
            fontSize = 32.sp,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(48.dp))
        Button(onClick = onPlayAgain) {
            Text(text = "Jugar de Nuevo", fontSize = 18.sp)
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onExit) {
            Text(text = "Salir", fontSize = 18.sp)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun WinnerScreenPreview() {
    WinnerScreen(
        gameState = GameState(finalWinner = Player(name = "Jugador Naranja")),
        onPlayAgain = {},
        onExit = {}
    )
}
