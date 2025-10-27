package com.example.tron.data

import androidx.compose.ui.graphics.Color

// Represents a position on the game grid
data class Position(val x: Int, val y: Int)

// Represents the direction of movement
enum class Direction {
    UP,
    DOWN,
    LEFT,
    RIGHT
}

// Represents the two possible teams/colors
enum class PlayerColor(val color: Color) {
    ORANGE(Color(0xFFFF8C00)), // Naranja Neón (de la especificación)
    BLUE(Color(0xFF00FFFF))   // Azul Eléctrico (de la especificación)
}

// Represents a player in the game
data class Player(
    val name: String = "",
    val score: Int = 0,
    val bikePosition: Position? = null,
    val trail: List<Position> = emptyList(),
    val direction: Direction = Direction.RIGHT,
    val color: PlayerColor? = null
)

// Represents the game board dimensions
// 40 de ancho, 20 de alto para landscape (coincide con la spec 20x40)
data class GameGrid(val width: Int, val height: Int)

// Represents the types of power-ups available
enum class PowerUpType {
    LENGTHEN_TRAIL,
    CUT_TRAIL,
    ENABLE_ACCELERATOR
}

// Represents a power-up on the grid
data class PowerUp(
    val position: Position,
    val type: PowerUpType
)

// --- Bluetooth Connection State ---
data class BluetoothDevice(val name: String, val address: String)

enum class ConnectionState {
    IDLE,
    SCANNING,
    CONNECTING,
    CONNECTED,
    FAILED
}

// Represents the overall state of the game round
data class GameState(
    // Player and Game Info
    val player1: Player = Player(),
    val player2: Player? = null,
    val gameGrid: GameGrid = GameGrid(40, 20),
    val roundTimeLeft: Int = 60, // in seconds
    val powerUps: List<PowerUp> = emptyList(),
    val isRoundOver: Boolean = false,
    val roundWinner: Player? = null,
    // CORRECCIÓN: Renombrado de isGameFinished a isGameOver
    val isGameOver: Boolean = false,
    val finalWinner: Player? = null,

    // Connection Info
    val connectionState: ConnectionState = ConnectionState.IDLE,
    val discoveredDevices: List<BluetoothDevice> = emptyList(),
    val pairedDevice: BluetoothDevice? = null
)