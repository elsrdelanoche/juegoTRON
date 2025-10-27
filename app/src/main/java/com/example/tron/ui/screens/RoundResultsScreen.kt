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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tron.data.GameState
import com.example.tron.data.Player

@Composable
fun RoundResultsScreen(
    gameState: GameState,
    onNextRound: () -> Unit
) {
    val winner = gameState.roundWinner
    val player1 = gameState.player1
    val player2 = gameState.player2

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = if (winner != null) "¡Ganador de la Ronda!" else "¡Empate!",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(16.dp))

        if (winner != null) {
            Text(
                text = winner.name,
                fontSize = 28.sp,
                color = MaterialTheme.colorScheme.primary
            )
            // You can add a reason here if you pass it in the game state
            Spacer(modifier = Modifier.height(32.dp))
        }

        Text(
            text = "Puntuación Acumulada",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )
        Text("${player1.name}: ${player1.score}", fontSize = 20.sp)
        player2?.let {
            Text("${it.name}: ${it.score}", fontSize = 20.sp)
        }

        Spacer(modifier = Modifier.height(48.dp))
        Button(onClick = onNextRound) {
            Text(text = "Siguiente Ronda", fontSize = 18.sp)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RoundResultsScreenPreview() {
    RoundResultsScreen(
        gameState = GameState(
            player1 = Player(name = "Player 1", score = 5),
            player2 = Player(name = "Player 2", score = 4),
            roundWinner = Player(name = "Player 1")
        ),
        onNextRound = {}
    )
}

@Preview(showBackground = true)
@Composable
fun RoundResultsScreenDrawPreview() {
    RoundResultsScreen(
        gameState = GameState(
            player1 = Player(name = "Player 1", score = 5),
            player2 = Player(name = "Player 2", score = 5),
            roundWinner = null // Draw
        ),
        onNextRound = {}
    )
}
