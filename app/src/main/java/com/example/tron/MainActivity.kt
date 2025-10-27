package com.example.tron

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.tron.data.ConnectionState
import com.example.tron.navigation.Screen
import com.example.tron.ui.screens.BluetoothConnectionScreen
import com.example.tron.ui.screens.GameScreen
import com.example.tron.ui.screens.PlayerSetupScreen
import com.example.tron.ui.screens.RoundResultsScreen
import com.example.tron.ui.screens.TeamSelectionScreen
import com.example.tron.ui.screens.WinnerScreen
import com.example.tron.ui.theme.TRONTheme
import com.example.tron.viewmodel.GameViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TRONTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    TronApp()
                }
            }
        }
    }
}

@Composable
fun TronApp(gameViewModel: GameViewModel = viewModel()) {
    val navController = rememberNavController()
    val gameState by gameViewModel.gameState.collectAsState()
    val activity = (LocalContext.current as? Activity)

    NavHost(navController = navController, startDestination = Screen.PlayerSetup.route) {
        composable(Screen.PlayerSetup.route) {
            PlayerSetupScreen(
                gameState = gameState,
                onPlayerNameChange = gameViewModel::onPlayerNameChanged,
                onContinueClick = { navController.navigate(Screen.BluetoothConnection.route) }
            )
        }
        composable(Screen.BluetoothConnection.route) {
            BluetoothConnectionScreen(
                gameState = gameState,
                onStartScan = gameViewModel::startScan,
                onConnectToDevice = gameViewModel::connectToDevice
            )

            LaunchedEffect(gameState.connectionState) {
                if (gameState.connectionState == ConnectionState.CONNECTED) {
                    navController.navigate(Screen.TeamSelection.route)
                }
            }
        }
        composable(Screen.TeamSelection.route) {
            TeamSelectionScreen(onTeamSelected = gameViewModel::selectTeam)

            LaunchedEffect(gameState.player1.color, gameState.player2?.color) {
                if (gameState.player1.color != null && gameState.player2?.color != null) {
                    gameViewModel.startGameLoop()
                    navController.navigate(Screen.Game.route)
                }
            }
        }
        composable(Screen.Game.route) {
            GameScreen(
                gameState = gameState,
                onDirectionChange = gameViewModel::changeDirection,
                onRoundEnd = {}
            )

            LaunchedEffect(gameState.isRoundOver) {
                if (gameState.isRoundOver) {
                    navController.navigate(Screen.RoundResult.route)
                }
            }
        }
        composable(Screen.RoundResult.route) {
            RoundResultsScreen(
                gameState = gameState,
                onNextRound = gameViewModel::startNewRound
            )

            LaunchedEffect(gameState.isGameOver) {
                if (gameState.isGameOver) {
                    navController.navigate(Screen.FinalWinner.route) {
                        popUpTo(Screen.PlayerSetup.route)
                    }
                }
            }

            LaunchedEffect(gameState.isRoundOver, gameState.player1.color) {
                if (!gameState.isRoundOver && gameState.player1.color != null) {
                    navController.navigate(Screen.Game.route) {
                        popUpTo(Screen.Game.route) { inclusive = true }
                    }
                }
            }
        }
        composable(Screen.FinalWinner.route) {
            WinnerScreen(
                gameState = gameState,
                onPlayAgain = {
                    gameViewModel.resetGame()
                    navController.navigate(Screen.PlayerSetup.route) {
                        popUpTo(Screen.PlayerSetup.route) { inclusive = true }
                    }
                },
                onExit = { activity?.finish() }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    TRONTheme {
        TronApp()
    }
}