package com.example.tron.navigation

sealed class Screen(val route: String) {
    object PlayerSetup : Screen("player_setup")
    object GameModeSelection : Screen("game_mode_selection")
    object BluetoothConnection : Screen("bluetooth_connection")
    object TeamSelection : Screen("team_selection")
    object Game : Screen("game")
    object RoundResult : Screen("round_result")
    object FinalWinner : Screen("final_winner")
}
