package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = RacingBlueLight,
    onPrimary = Color.White,
    primaryContainer = DarkBlueContainer,
    onPrimaryContainer = Color.White,
    secondary = AmberAccent,
    onSecondary = CarbonDarkBackground,
    tertiary = MetallicSilver,
    background = CarbonDarkBackground,
    onBackground = TitleLight,
    surface = DarkSurface,
    onSurface = TitleLight,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = Color(0xFFCBD5E1)
)

private val LightColorScheme = lightColorScheme(
    primary = RacingBluePrimary,
    onPrimary = Color.White,
    primaryContainer = LightBlueContainer,
    onPrimaryContainer = RacingBlueDark,
    secondary = AmberAccent,
    onSecondary = TitleDark,
    tertiary = MetallicSilver,
    background = PureWhiteBackground,
    onBackground = TitleDark,
    surface = LightSurface,
    onSurface = TitleDark,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = SubtitleGray
)

@Composable
fun DiecastTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Keep consistent branding colors
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    DiecastTheme(darkTheme = darkTheme, dynamicColor = dynamicColor, content = content)
}

