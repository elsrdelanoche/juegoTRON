package com.example.tron.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tron.data.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.random.Random

class GameViewModel : ViewModel() {

    private val _gameState = MutableStateFlow(GameState())
    val gameState = _gameState.asStateFlow()
    private var gameLoopJob: Job? = null

    fun onPlayerNameChanged(name: String) {
        _gameState.value = _gameState.value.copy(
            player1 = _gameState.value.player1.copy(name = name)
        )
    }

    // --- Bluetooth Simulation ---
    fun startScan() {
        viewModelScope.launch {
            _gameState.value = _gameState.value.copy(connectionState = ConnectionState.SCANNING)
            delay(2000) // Simulate scanning
            _gameState.value = _gameState.value.copy(
                discoveredDevices = listOf(BluetoothDevice("Player 2's Phone", "00:11:22:33:FF:EE")),
                connectionState = ConnectionState.IDLE
            )
        }
    }

    fun connectToDevice(device: BluetoothDevice) {
        viewModelScope.launch {
            _gameState.value = _gameState.value.copy(connectionState = ConnectionState.CONNECTING)
            delay(2000) // Simulate connecting
            _gameState.value = _gameState.value.copy(connectionState = ConnectionState.CONNECTED, player2 = Player(name = device.name))
        }
    }

    // --- Team & Game Setup ---
    fun selectTeam(player1Color: PlayerColor) {
        val player2Color = if (player1Color == PlayerColor.ORANGE) PlayerColor.BLUE else PlayerColor.ORANGE
        val grid = _gameState.value.gameGrid

        val p1Start = Position(Random.nextInt(grid.width / 4), Random.nextInt(grid.height))
        val p2Start = Position(Random.nextInt(grid.width / 4) + grid.width / 2, Random.nextInt(grid.height))

        _gameState.value = _gameState.value.copy(
            player1 = _gameState.value.player1.copy(color = player1Color, bikePosition = p1Start, trail = listOf(p1Start), direction = Direction.RIGHT),
            player2 = _gameState.value.player2?.copy(color = player2Color, bikePosition = p2Start, trail = listOf(p2Start), direction = Direction.LEFT)
        )
    }

    fun startNewRound() {
        gameLoopJob?.cancel()
        val currentState = _gameState.value
        val grid = currentState.gameGrid

        // Reset positions and trails, keep scores and colors
        val p1Start = Position(Random.nextInt(grid.width / 4), Random.nextInt(grid.height))
        val p2Start = Position(Random.nextInt(grid.width / 4) + grid.width / 2, Random.nextInt(grid.height))

        _gameState.value = currentState.copy(
            isRoundOver = false,
            roundWinner = null,
            player1 = currentState.player1.copy(bikePosition = p1Start, trail = listOf(p1Start), direction = Direction.RIGHT),
            player2 = currentState.player2?.copy(bikePosition = p2Start, trail = listOf(p2Start), direction = Direction.LEFT)
        )

        startGameLoop()
    }
    
    fun resetGame() {
        gameLoopJob?.cancel()
        _gameState.value = GameState() // Reset to initial state
    }

    // --- Game Logic ---
    fun startGameLoop() {
        gameLoopJob?.cancel()
        gameLoopJob = viewModelScope.launch {
            while (_gameState.value.isRoundOver.not() && _gameState.value.isGameOver.not()) {
                delay(200) // Game speed
                updateGame()
            }
        }
    }

    private fun updateGame() {
        val currentState = _gameState.value
        val p1 = currentState.player1
        val p2 = currentState.player2 ?: return

        // Calculate next positions
        val nextP1Pos = getNextPosition(p1.bikePosition!!, p1.direction)
        val nextP2Pos = getNextPosition(p2.bikePosition!!, p2.direction)

        // Check for collisions
        val p1Loses = isCollision(nextP1Pos, p1.trail, p2.trail, currentState.gameGrid)
        val p2Loses = isCollision(nextP2Pos, p2.trail, p1.trail, currentState.gameGrid)
        val headOnCollision = nextP1Pos == nextP2Pos

        if (headOnCollision || (p1Loses && p2Loses)) {
            endRound(winner = null)
            return
        }
        if (p1Loses) {
            endRound(winner = p2)
            return
        }
        if (p2Loses) {
            endRound(winner = p1)
            return
        }

        // No collision, update state
        val newP1Trail = p1.trail + nextP1Pos
        val newP2Trail = p2.trail + nextP2Pos

        _gameState.value = currentState.copy(
            player1 = p1.copy(bikePosition = nextP1Pos, trail = newP1Trail),
            player2 = p2.copy(bikePosition = nextP2Pos, trail = newP2Trail)
        )
    }

    private fun isCollision(pos: Position, ownTrail: List<Position>, opponentTrail: List<Position>, grid: GameGrid): Boolean {
        if (pos.x < 0 || pos.x >= grid.width || pos.y < 0 || pos.y >= grid.height) return true
        if (ownTrail.contains(pos) || opponentTrail.contains(pos)) return true
        return false
    }

    private fun endRound(winner: Player?) {
        gameLoopJob?.cancel()
        val currentState = _gameState.value
        var p1Score = currentState.player1.score
        var p2Score = currentState.player2?.score ?: 0
        var finalWinner: Player? = null

        if (winner != null) {
            if (winner.name == currentState.player1.name) {
                p1Score++
                if (p1Score >= 10) finalWinner = currentState.player1.copy(score = p1Score)
            } else {
                p2Score++
                if (p2Score >= 10) finalWinner = currentState.player2?.copy(score = p2Score)
            }
        }

        _gameState.value = currentState.copy(
            isRoundOver = true,
            roundWinner = winner,
            player1 = currentState.player1.copy(score = p1Score),
            player2 = currentState.player2?.copy(score = p2Score),
            isGameOver = finalWinner != null,
            finalWinner = finalWinner
        )
    }

    fun changeDirection(newDirection: Direction) {
        val p1 = _gameState.value.player1
        if (_gameState.value.isRoundOver) return
        if (p1.direction.isOpposite(newDirection)) return

        _gameState.value = _gameState.value.copy(
            player1 = p1.copy(direction = newDirection)
        )
    }

    private fun Direction.isOpposite(other: Direction): Boolean {
        return when (this) {
            Direction.UP -> other == Direction.DOWN
            Direction.DOWN -> other == Direction.UP
            Direction.LEFT -> other == Direction.RIGHT
            Direction.RIGHT -> other == Direction.LEFT
        }
    }

    private fun getNextPosition(current: Position, direction: Direction): Position {
        return when (direction) {
            Direction.UP -> current.copy(y = current.y - 1)
            Direction.DOWN -> current.copy(y = current.y + 1)
            Direction.LEFT -> current.copy(x = current.x - 1)
            Direction.RIGHT -> current.copy(x = current.x + 1)
        }
    }
}
