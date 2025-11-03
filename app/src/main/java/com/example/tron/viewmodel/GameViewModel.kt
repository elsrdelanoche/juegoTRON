
package com.example.tron.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tron.data.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.random.Random

private const val TICK_RATE_MS = 200L
private const val ROUND_DURATION_SECONDS = 60
private const val TRAIL_GROWTH_INTERVAL_SECONDS = 5
private const val INITIAL_TRAIL_LENGTH = 5
private const val TRAIL_GROWTH_AMOUNT = 2
private const val GRID_WIDTH = 50
private const val GRID_HEIGHT = 30
private const val BORDER_MARGIN = 5


class GameViewModel : ViewModel() {

    private val _gameState = MutableStateFlow(GameState(gameGrid = GameGrid(GRID_WIDTH, GRID_HEIGHT)))
    val gameState = _gameState.asStateFlow()

    private var gameLoopJob: Job? = null

    fun onPlayerNameChanged(newName: String) {
        _gameState.update { it.copy(player1 = it.player1.copy(name = newName)) }
    }

    fun setGameMode(isAi: Boolean) {
        _gameState.update { it.copy(isAiOpponent = isAi) }
    }

    fun startScan() {
        // Lógica para escanear dispositivos Bluetooth
    }

    fun connectToDevice(device: BluetoothDevice) {
        // Lógica para conectar a un dispositivo
        _gameState.update { it.copy(connectionState = ConnectionState.CONNECTED, pairedDevice = device) }
    }

    fun selectTeam(playerColor: PlayerColor) {
        val currentState = _gameState.value
        val grid = currentState.gameGrid

        // Genera posiciones y direcciones aleatorias para ambos jugadores
        val (p1Pos, p1Dir) = generateRandomInitialState(grid)
        val (p2Pos, p2Dir) = generateRandomInitialState(grid, listOf(p1Pos))

        val player1 = currentState.player1.copy(
            color = playerColor,
            bikePosition = p1Pos,
            direction = p1Dir,
            maxTrailLength = INITIAL_TRAIL_LENGTH,
            trail = mutableListOf(p1Pos)
        )

        val player2Color = if (playerColor == PlayerColor.ORANGE) PlayerColor.BLUE else PlayerColor.ORANGE
        val player2 = Player(
            name = if (currentState.isAiOpponent) "IA" else "Player 2",
            color = player2Color,
            bikePosition = p2Pos,
            direction = p2Dir,
            maxTrailLength = INITIAL_TRAIL_LENGTH,
            trail = mutableListOf(p2Pos)
        )
        _gameState.update {
            it.copy(
                player1 = player1,
                player2 = player2,
            )
        }
    }
    private fun generateRandomInitialState(grid: GameGrid, excludedPositions: List<Position> = emptyList()): Pair<Position, Direction> {
        var position: Position
        val direction = Direction.values().random() // Dirección inicial aleatoria

        do {
            position = Position(
                x = Random.nextInt(BORDER_MARGIN, grid.width - BORDER_MARGIN),
                y = Random.nextInt(BORDER_MARGIN, grid.height - BORDER_MARGIN)
            )
        } while (excludedPositions.any { it.x == position.x && it.y == position.y }) // Asegura que no se solapen

        return Pair(position, direction)
    }


    fun startGameLoop() {
        val ticksPerGrowth = (TRAIL_GROWTH_INTERVAL_SECONDS * 1000) / TICK_RATE_MS.toInt()
        _gameState.update { it.copy(ticksUntilNextGrowth = ticksPerGrowth, roundTimeLeft = ROUND_DURATION_SECONDS) }
        gameLoopJob?.cancel()
        gameLoopJob = viewModelScope.launch {
            while (true) {
                updateGame()
                delay(TICK_RATE_MS)
            }
        }
    }

    private fun updateGame() {
        val currentState = _gameState.value

        // --- Lógica del Temporizador ---
        val newTime = (currentState.roundTimeLeft - (TICK_RATE_MS / 1000.0)).coerceAtLeast(0.0)

        // Comprueba si el tiempo se ha agotado en este tick
        if (currentState.roundTimeLeft > 0 && newTime <= 0) {
            endRound(null) // Empate si se acaba el tiempo
            return
        }

        var newTicksUntilGrowth = currentState.ticksUntilNextGrowth - 1

        // --- Lógica de la IA ---
        // Decide la dirección de la IA ANTES de mover a los jugadores
        val p2Direction = if (currentState.isAiOpponent && currentState.player2 != null) {
            determineAiNextDirection(currentState.player2, currentState.player1, currentState.gameGrid)
        } else {
            currentState.player2?.direction
        }

        // --- Actualización de Posiciones ---
        // Crea una copia del jugador 2 con la dirección que acabamos de decidir
        val player2WithNewDirection = currentState.player2?.copy(direction = p2Direction ?: currentState.player2.direction)

        // Mueve ambos jugadores a sus nuevas posiciones y actualiza sus estelas
        val updatedPlayer1 = updatePlayer(currentState.player1)
        val updatedPlayer2 = player2WithNewDirection?.let { updatePlayer(it) }

        // --- Lógica de Crecimiento de Estela ---
        var finalPlayer1 = updatedPlayer1
        var finalPlayer2 = updatedPlayer2
        if (newTicksUntilGrowth <= 0) {
            finalPlayer1 = updatedPlayer1.copy(maxTrailLength = updatedPlayer1.maxTrailLength + TRAIL_GROWTH_AMOUNT)
            if (updatedPlayer2 != null) {
                finalPlayer2 = updatedPlayer2.copy(maxTrailLength = updatedPlayer2.maxTrailLength + TRAIL_GROWTH_AMOUNT)
            }
            newTicksUntilGrowth = (TRAIL_GROWTH_INTERVAL_SECONDS * 1000) / TICK_RATE_MS.toInt()
        }

        // --- Actualización de Estado Global ---
        // Actualiza el estado del juego con todos los cambios de este tick en una sola operación
        _gameState.update {
            it.copy(
                player1 = finalPlayer1,
                player2 = finalPlayer2,
                roundTimeLeft = newTime.toInt(),
                ticksUntilNextGrowth = newTicksUntilGrowth
            )
        }

        // --- Detección de Colisiones ---
        // Comprueba las colisiones DESPUÉS de que el estado se haya actualizado completamente
        checkCollisions()
    }
    private fun determineAiNextDirection(aiPlayer: Player, opponent: Player, grid: GameGrid): Direction {
        // La IA ya comprueba si va a chocar contra un muro o contra sí misma a través de getSafeDirections.
        // Esta función devuelve solo las direcciones que no resultan en una colisión inmediata.
        val safeDirections = getSafeDirections(aiPlayer, grid, opponent.trail)

        // Si no hay ninguna dirección segura, no hay nada que hacer más que seguir y chocar.
        if (safeDirections.isEmpty()) {
            return aiPlayer.direction
        }

        val opponentNextPos = opponent.bikePosition!!.move(opponent.direction)

        // De las direcciones seguras, filtramos las que causarían un choque frontal con el oponente.
        val directionsWithoutHeadOnCollision = safeDirections.filter { direction ->
            val aiNextPos = aiPlayer.bikePosition!!.move(direction)
            aiNextPos != opponentNextPos
        }

        // Se prioriza seguir recto si es una opción segura y no causa choque frontal.
        if (aiPlayer.direction in directionsWithoutHeadOnCollision) {
            return aiPlayer.direction
        }

        // Si no se puede seguir recto, se elige una de las otras opciones seguras de forma aleatoria.
        // Esto hace que la IA sea menos predecible.
        // Si todas las opciones seguras causan un choque frontal, se elige una de ellas al azar para forzar el empate.
        return directionsWithoutHeadOnCollision.shuffled().firstOrNull()
            ?: safeDirections.shuffled().firstOrNull()
            ?: aiPlayer.direction // Como fallback final, aunque no debería llegar aquí si safeDirections no está vacío.
    }


    private fun getSafeDirections(player: Player, grid: GameGrid, opponentTrail: List<Position>): List<Direction> {
        val possibleDirections = Direction.values().filter { it != player.direction.opposite() }
        val safe = mutableListOf<Direction>()

        for (direction in possibleDirections) {
            val nextPos = player.bikePosition!!.move(direction)
            if (isPositionSafe(nextPos, grid, player.trail, opponentTrail)) {
                safe.add(direction)
            }
        }
        return safe
    }

    private fun isPositionSafe(pos: Position, grid: GameGrid, ownTrail: List<Position>, opponentTrail: List<Position>): Boolean {
        return pos.x >= 0 && pos.x < grid.width &&
                pos.y >= 0 && pos.y < grid.height &&
                !ownTrail.contains(pos) &&
                !opponentTrail.contains(pos)
    }

    private fun updatePlayer(player: Player): Player {
        val newPosition = player.bikePosition!!.move(player.direction)
        val newTrail = player.trail.toMutableList()
        newTrail.add(0, newPosition) // Añade la nueva posición al frente

        // Recorta la estela si supera la longitud máxima
        while (newTrail.size > player.maxTrailLength) {
            newTrail.removeLast()
        }
        return player.copy(bikePosition = newPosition, trail = newTrail)
    }


    private fun checkCollisions() {
        val currentState = _gameState.value
        val p1 = currentState.player1
        val p2 = currentState.player2 ?: return

        val p1Head = p1.bikePosition!!
        val p2Head = p2.bikePosition!!

        val p1Crashed = !isPositionSafe(p1Head, currentState.gameGrid, p1.trail.drop(1), p2.trail)
        val p2Crashed = !isPositionSafe(p2Head, currentState.gameGrid, p2.trail.drop(1), p1.trail)

        if (p1Head == p2Head) {
            endRound(null)
        }
        else if (p1Crashed && p2Crashed) {
            endRound(null) // Empate
        } else if (p1Crashed) {
            endRound(p2) // Gana jugador 2
        } else if (p2Crashed) {
            endRound(p1) // Gana jugador 1
        }
    }


    private fun endRound(winner: Player?) {
        gameLoopJob?.cancel()
        val currentState = _gameState.value
        val updatedP1 = if (winner?.name == currentState.player1.name) currentState.player1.copy(score = currentState.player1.score + 1) else currentState.player1
        val updatedP2 = if (winner?.name == currentState.player2?.name) currentState.player2?.copy(score = currentState.player2.score + 1) else currentState.player2

        val isGameFinished = updatedP1.score >= 3 || updatedP2?.score ?: 0 >= 3

        _gameState.update {
            it.copy(
                player1 = updatedP1,
                player2 = updatedP2,
                roundWinner = winner,
                isRoundOver = true,
                isGameFinished = isGameFinished
            )
        }
    }

    fun startNewRound() {
        val currentState = _gameState.value
        selectTeam(currentState.player1.color!!)
        _gameState.update { it.copy(isRoundOver = false, roundWinner = null) }
        startGameLoop()
    }

    fun changeDirection(newDirection: Direction) {
        val currentDirection = _gameState.value.player1.direction
        if (newDirection.opposite() != currentDirection) {
            _gameState.update {
                it.copy(player1 = it.player1.copy(direction = newDirection))
            }
        }
    }

    fun resetGame() {
        _gameState.value = GameState(gameGrid = GameGrid(GRID_WIDTH, GRID_HEIGHT))
    }
}
