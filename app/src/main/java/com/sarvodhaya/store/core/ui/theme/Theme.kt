package com.sarvodhaya.store.core.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = BrandOrange,
    onPrimary = Color.White,
    primaryContainer = BrandOrangeContainer,
    onPrimaryContainer = BrandOrangeDark,
    secondary = BrandInk,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFE8EAF0),
    onSecondaryContainer = BrandInk,
    tertiary = BrandAmber,
    onTertiary = BrandInk,
    background = BrandCream,
    onBackground = BrandInk,
    surface = BrandSurface,
    onSurface = BrandInk,
    surfaceVariant = Color(0xFFF3ECE5),
    onSurfaceVariant = BrandInkSoft,
    outline = Color(0xFFD8CFC7),
    error = BrandRed,
    onError = Color.White
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFFFF8A5C),
    onPrimary = BrandInk,
    primaryContainer = Color(0xFF5A2410),
    onPrimaryContainer = Color(0xFFFFD9C6),
    secondary = Color(0xFFC7CBD6),
    onSecondary = BrandInk,
    secondaryContainer = Color(0xFF343945),
    onSecondaryContainer = Color(0xFFE4E7EE),
    tertiary = BrandAmber,
    onTertiary = BrandInk,
    background = BrandCreamDark,
    onBackground = Color(0xFFEDEFF3),
    surface = BrandSurfaceDark,
    onSurface = Color(0xFFEDEFF3),
    surfaceVariant = Color(0xFF2D313A),
    onSurfaceVariant = Color(0xFFB9BFCC),
    outline = Color(0xFF4A505C),
    error = Color(0xFFFF9A94),
    onError = Color(0xFF3A0906)
)

@Composable
fun SarvodhayaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = SarvodhayaTypography,
        content = content
    )
}
