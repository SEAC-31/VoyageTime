package com.example.voyagetime.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val VoyageDarkColorScheme = darkColorScheme(
    primary = VoyageOrange,
    onPrimary = TextPrimary,

    secondary = VoyageSky,
    onSecondary = BackgroundBlack,

    tertiary = VoyageSkyDark,
    onTertiary = BackgroundBlack,

    background = BackgroundBlack,
    onBackground = TextPrimary,

    surface = SurfaceDark,
    onSurface = TextSecondary,

    surfaceVariant = SurfaceVariantDark,
    onSurfaceVariant = TextMuted,

    outline = VoyageSkyDark.copy(alpha = 0.45f)
)

private val VoyageLightColorScheme = lightColorScheme(
    primary = VoyageOrange,
    onPrimary = TextPrimary,

    secondary = VoyageSkyDark,
    onSecondary = BackgroundBlack,

    tertiary = VoyageSky,
    onTertiary = BackgroundBlack,

    background = Color.White,
    onBackground = BackgroundBlack,

    surface = Color(0xFFF7F7F7),
    onSurface = Color(0xFF111111),

    surfaceVariant = Color(0xFFEAF6FC),
    onSurfaceVariant = Color(0xFF4B5563),

    outline = VoyageOrange.copy(alpha = 0.35f)
)

@Composable
fun VoyageTimeTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) VoyageDarkColorScheme else VoyageLightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}