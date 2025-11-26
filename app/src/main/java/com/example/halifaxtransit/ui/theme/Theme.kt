package com.example.halifaxtransit.ui.theme

import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val HalifaxTransitColorScheme = lightColorScheme(
    primary = MintLeaf,
    onPrimary = Color.White,
    secondary = LightGreen,
    onSecondary = RegalNavy,
    tertiary = Verdigris,
    background = FrostedMint,
    onBackground = RegalNavy,
    surface = FrostedMint,
    onSurface = RegalNavy
)

@Composable
fun HalifaxTransitTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = HalifaxTransitColorScheme,
        typography = Typography,
        content = content
    )
}