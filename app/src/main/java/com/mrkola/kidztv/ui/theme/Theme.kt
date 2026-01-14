package com.mrkola.kidztv.ui.theme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val ColorScheme = lightColorScheme(
    primary = Color(0xFFAB47BC),
    secondary = Color(0xFFEC407A),
    tertiary = Color(0xFFEF5350)
)

@Composable
fun KidzTVTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = ColorScheme,
        content = content
    )
}