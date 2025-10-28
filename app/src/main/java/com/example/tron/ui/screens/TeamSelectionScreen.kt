package com.example.tron.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tron.data.PlayerColor

@Composable
fun TeamSelectionScreen(onTeamSelected: (PlayerColor) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Elige tu bando", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(32.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            TeamChoice(teamName = "Bando Naranja", color = PlayerColor.ORANGE.color, onClick = { onTeamSelected(PlayerColor.ORANGE) })
            TeamChoice(teamName = "Bando Azul", color = PlayerColor.BLUE.color, onClick = { onTeamSelected(PlayerColor.BLUE) })
        }
    }
}

@Composable
fun RowScope.TeamChoice(teamName: String, color: Color, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .height(150.dp)
            .weight(1f)
            .padding(8.dp)
            .clip(MaterialTheme.shapes.medium)
            .background(color)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = teamName,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp
        )
    }
}

@Preview(showBackground = true)
@Composable
fun TeamSelectionScreenPreview() {
    TeamSelectionScreen(onTeamSelected = {})
}
