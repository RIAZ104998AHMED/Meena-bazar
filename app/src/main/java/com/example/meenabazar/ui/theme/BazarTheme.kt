package com.example.meenabazar.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val Forest = Color(0xFF254D3E)
val Linen = Color(0xFFF8F5EF)
val Ink = Color(0xFF242E29)
val Terracotta = Color(0xFF9C583E)
val Muted = Color(0xFF626B63)

private val LightPalette = lightColorScheme(
    primary = Forest,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFDCE8DB),
    onPrimaryContainer = Forest,
    secondary = Terracotta,
    secondaryContainer = Color(0xFFE4EADC),
    onSecondaryContainer = Forest,
    surfaceContainer = Color(0xFFF0EEE6),
    surfaceContainerLow = Color(0xFFF4F1E9),
    surfaceContainerHigh = Color(0xFFECEAE2),
    surfaceContainerHighest = Color(0xFFE8E6DE),
    surfaceTint = Forest,
    background = Linen,
    onBackground = Ink,
    surface = Linen,
    onSurface = Ink,
    surfaceVariant = Color(0xFFECEAE2),
    onSurfaceVariant = Muted,
    outline = Color(0xFF7B857C)
)

private val DarkPalette = darkColorScheme(
    primary = Color(0xFFB3CFBA),
    onPrimary = Color(0xFF163728),
    primaryContainer = Forest,
    onPrimaryContainer = Color(0xFFDCE8DB),
    secondary = Color(0xFFE7B59A),
    secondaryContainer = Color(0xFF334C3C),
    onSecondaryContainer = Color(0xFFDCE8DB),
    surfaceContainer = Color(0xFF232D26),
    surfaceContainerLow = Color(0xFF202922),
    surfaceContainerHigh = Color(0xFF2C372F),
    surfaceContainerHighest = Color(0xFF354138),
    surfaceTint = Color(0xFFB3CFBA),
    background = Color(0xFF161D19),
    onBackground = Color(0xFFE8EBE3),
    surface = Color(0xFF1D2520),
    onSurface = Color(0xFFE8EBE3),
    surfaceVariant = Color(0xFF303D33),
    onSurfaceVariant = Color(0xFFBDC7BC),
    outline = Color(0xFF89998D)
)

@Composable
fun MeenaBazarTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = if (darkTheme) DarkPalette else LightPalette, typography = Typography, content = content)
}
