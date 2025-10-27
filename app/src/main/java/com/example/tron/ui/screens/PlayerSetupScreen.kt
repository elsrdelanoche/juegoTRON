package com.example.tron.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.tron.data.GameState

@Composable
fun PlayerSetupScreen(
    gameState: GameState,
    onPlayerNameChange: (String) -> Unit,
    onContinueClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Ingresa tu nombre")
        Spacer(modifier = Modifier.height(16.dp))
        TextField(
            value = gameState.player1.name,
            onValueChange = onPlayerNameChange,
            singleLine = true
        )
        Spacer(modifier = Modifier.height(32.dp))
        Button(onClick = onContinueClick) {
            Text(text = "Continuar")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PlayerSetupScreenPreview() {
    PlayerSetupScreen(
        gameState = GameState(player1 = com.example.tron.data.Player(name = "Player 1")),
        onPlayerNameChange = {},
        onContinueClick = {}
    )
}
