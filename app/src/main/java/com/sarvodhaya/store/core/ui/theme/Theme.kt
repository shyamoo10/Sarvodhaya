package com.sarvodhaya.store.core.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

private val LightColors = lightColorScheme(
    primary = BrandNavy,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFDCE6EF),
    onPrimaryContainer = BrandNavyDeep,
    secondary = BrandCharcoal,
    onSecondary = Color.White,
    secondaryContainer = BrandConcrete,
    onSecondaryContainer = BrandCharcoal,
    tertiary = BrandOrange,
    onTertiary = Color.White,
    tertiaryContainer = BrandOrangeContainer,
    onTertiaryContainer = Color(0xFF5A2400),
    background = BrandWarmWhite,
    onBackground = BrandCharcoal,
    surface = BrandSurface,
    onSurface = BrandCharcoal,
    surfaceVariant = Color(0xFFEFEAE1),
    onSurfaceVariant = BrandSteel,
    outline = Color(0xFFC6BDB0),
    outlineVariant = Color(0xFFE2DCD2),
    error = BrandRed,
    onError = Color.White
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFF9CC1DE),
    onPrimary = Color(0xFF06202F),
    primaryContainer = Color(0xFF22445F),
    onPrimaryContainer = Color(0xFFD6E7F5),
    secondary = Color(0xFFC7CCD4),
    onSecondary = BrandCharcoal,
    secondaryContainer = Color(0xFF2A3038),
    onSecondaryContainer = Color(0xFFE3E7EE),
    tertiary = Color(0xFFFF9A57),
    onTertiary = Color(0xFF2A1200),
    tertiaryContainer = Color(0xFF5A2A0A),
    onTertiaryContainer = Color(0xFFFFDCC4),
    background = BrandCreamDark,
    onBackground = Color(0xFFE9ECF1),
    surface = BrandSurfaceDark,
    onSurface = Color(0xFFE9ECF1),
    surfaceVariant = Color(0xFF252A32),
    onSurfaceVariant = Color(0xFFB3BAC5),
    outline = Color(0xFF4A515B),
    outlineVariant = Color(0xFF2F353D),
    error = Color(0xFFFF9A94),
    onError = Color(0xFF3A0906)
)

private val SarvodhayaShapes = Shapes(
    extraSmall = RoundedCornerShape(4.dp),
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(12.dp),
    large = RoundedCornerShape(16.dp),
    extraLarge = RoundedCornerShape(20.dp)
)

@Composable
fun SarvodhayaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = SarvodhayaTypography,
        shapes = SarvodhayaShapes,
        content = content
    )
}
