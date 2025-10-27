package com.example.tron.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.tron.data.BluetoothDevice
import com.example.tron.data.ConnectionState
import com.example.tron.data.GameState

@Composable
fun BluetoothConnectionScreen(
    gameState: GameState,
    onStartScan: () -> Unit,
    onConnectToDevice: (BluetoothDevice) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val statusText = when (gameState.connectionState) {
            ConnectionState.IDLE -> "Toca para buscar dispositivos"
            ConnectionState.SCANNING -> "Buscando..."
            ConnectionState.CONNECTING -> "Conectando a ${gameState.pairedDevice?.name ?: "dispositivo"}..."
            ConnectionState.CONNECTED -> "¡Conectado a ${gameState.pairedDevice?.name}!"
            ConnectionState.FAILED -> "Fallo en la conexión"
        }

        Text(text = statusText)
        Spacer(modifier = Modifier.height(16.dp))

        if (gameState.connectionState == ConnectionState.SCANNING || gameState.connectionState == ConnectionState.CONNECTING) {
            CircularProgressIndicator()
        }

        LazyColumn(modifier = Modifier.weight(1f).fillMaxWidth()) {
            items(gameState.discoveredDevices) { device ->
                DeviceItem(deviceName = device.name, onConnect = { onConnectToDevice(device) })
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = onStartScan,
            enabled = gameState.connectionState == ConnectionState.IDLE || gameState.connectionState == ConnectionState.FAILED
        ) {
            Text("Buscar Dispositivos")
        }
    }
}

@Composable
fun DeviceItem(deviceName: String, onConnect: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onConnect)
            .padding(vertical = 12.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Text(text = deviceName)
    }
}

@Preview(showBackground = true)
@Composable
fun BluetoothConnectionScreenPreview_Scanning() {
    val sampleDevices = listOf(
        BluetoothDevice("TRON_Player_2", "00:11:22:33:FF:EE"),
        BluetoothDevice("Pixel_7_Pro", "AA:BB:CC:DD:EE:FF"),
    )
    val gameState = GameState(
        connectionState = ConnectionState.SCANNING,
        discoveredDevices = sampleDevices
    )
    BluetoothConnectionScreen(
        gameState = gameState,
        onStartScan = {},
        onConnectToDevice = {}
    )
}

@Preview(showBackground = true)
@Composable
fun BluetoothConnectionScreenPreview_Idle() {
    BluetoothConnectionScreen(
        gameState = GameState(),
        onStartScan = {},
        onConnectToDevice = {}
    )
}
