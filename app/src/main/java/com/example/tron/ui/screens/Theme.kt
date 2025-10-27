package com.example.tron.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Paleta de colores basada en tu especificación (TRON)
private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFFF8C00),   // Naranja Neón (#FF8C00)
    secondary = Color(0xFF00FFFF), // Azul Eléctrico (#00FFFF)
    background = Color(0xFF000000), // Fondo Negro
    surface = Color(0xFF1C1C1E),     // Superficie oscura
    onPrimary = Color.Black,
    onSecondary = Color.Black,
    onBackground = Color(0xFFCCCCCC), // Texto/Líneas tenues
    onSurface = Color(0xFFCCCCCC)
)

// Dejamos una paleta clara por si acaso, pero la app usará la oscura
private val LightColorScheme = lightColorScheme(
    primary = Color(0xFFFF8C00),
    secondary = Color(0xFF00FFFF),
    background = Color(0xFFFFFFFF),
    surface = Color(0xFFF0F0F0),
    onPrimary = Color.Black,
    onSecondary = Color.Black,
    onBackground = Color.Black,
    onSurface = Color.Black
)

@Composable
fun TRONTheme( // <-- Esta es la función que faltaba
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = DarkColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = MaterialTheme.typography,
        content = content
    )
}