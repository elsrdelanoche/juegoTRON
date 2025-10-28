
package com.example.tron.data

import androidx.compose.ui.graphics.Color

// Represents a position on the game grid
data class Position(val x: Int, val y: Int) {
    fun move(direction: Direction): Position {
        return when (direction) {
            Direction.UP -> copy(y = y - 1)
            Direction.DOWN -> copy(y = y + 1)
            Direction.LEFT -> copy(x = x - 1)
            Direction.RIGHT -> copy(x = x + 1)
        }
    }
}

// Represents the direction of movement
enum class Direction {
    UP,
    DOWN,
    LEFT,
    RIGHT;

    fun opposite(): Direction {
        return when (this) {
            UP -> DOWN
            DOWN -> UP
            LEFT -> RIGHT
            RIGHT -> LEFT
        }
    }
}

// Represents the two possible teams/colors
enum class PlayerColor(val color: Color) {
    ORANGE(Color(0xFFFFA500)),
    BLUE(Color(0xFF00BFFF))
}

// Represents a player in the game
data class Player(
    val name: String = "",
    val score: Int = 0,
    val bikePosition: Position? = null,
    val trail: List<Position> = emptyList(),
    val direction: Direction = Direction.RIGHT,
    val color: PlayerColor? = null,
    var maxTrailLength: Int = 5 // New: To control the snake's length
)

// Represents the game board dimensions
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
    val isAiOpponent: Boolean = false,
    val gameGrid: GameGrid = GameGrid(40, 20),
    val roundTimeLeft: Int = 60, // in seconds
    val ticksUntilNextGrowth: Int = 25, // New: 5 seconds at 200ms delay per tick
    val powerUps: List<PowerUp> = emptyList(),
    val isRoundOver: Boolean = false,
    val roundWinner: Player? = null,
    val isGameFinished: Boolean = false,
    val finalWinner: Player? = null,

    // Connection Info
    val connectionState: ConnectionState = ConnectionState.IDLE,
    val discoveredDevices: List<BluetoothDevice> = emptyList(),
    val pairedDevice: BluetoothDevice? = null
)
