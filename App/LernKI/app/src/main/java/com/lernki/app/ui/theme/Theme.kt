package com.lernki.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColors = lightColorScheme(
    primary = Indigo40,
    onPrimary = Surface,
    secondary = Teal40,
    tertiary = Amber40,
    background = Background,
    surface = Surface,
    onSurface = OnSurface,
    error = Error
)

private val DarkColors = darkColorScheme(
    primary = Indigo80,
    secondary = Teal80,
    tertiary = Amber40,
    background = IndigoDark,
    surface = IndigoDark
)

@Composable
fun LernKiTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) DarkColors else LightColors
    MaterialTheme(
        colorScheme = colors,
        typography = LernKiTypography,
        content = content
    )
}
