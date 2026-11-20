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
    primary = BrandForest,
    onPrimary = Color.White,
    primaryContainer = BrandMist,
    onPrimaryContainer = BrandForestDeep,
    secondary = BrandSage,
    onSecondary = BrandInk,
    secondaryContainer = BrandMist,
    onSecondaryContainer = BrandForestDeep,
    tertiary = BrandAmber,
    onTertiary = BrandForestDeep,
    tertiaryContainer = BrandAmberSoft,
    onTertiaryContainer = BrandAmberDeep,
    background = BrandPaper,
    onBackground = BrandInk,
    surface = BrandSurface,
    onSurface = BrandInk,
    surfaceVariant = BrandMist,
    onSurfaceVariant = BrandMuted,
    outline = BrandOutline,
    outlineVariant = BrandLine,
    error = BrandRed,
    onError = Color.White,
    errorContainer = BrandAccentSoft,
    onErrorContainer = Color(0xFF7A2A18)
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFFA8CBBD),
    onPrimary = Color(0xFF10231E),
    primaryContainer = Color(0xFF2C463F),
    onPrimaryContainer = Color(0xFFD8E8E1),
    secondary = Color(0xFF9DB2A9),
    onSecondary = Color(0xFF1A2622),
    secondaryContainer = BrandMistDark,
    onSecondaryContainer = Color(0xFFDDE6E1),
    tertiary = BrandAmber,
    onTertiary = Color(0xFF2B2410),
    tertiaryContainer = Color(0xFF4A3A12),
    onTertiaryContainer = Color(0xFFFBE9B4),
    background = BrandPaperDark,
    onBackground = Color(0xFFE8EDEA),
    surface = BrandSurfaceDark,
    onSurface = Color(0xFFE8EDEA),
    surfaceVariant = BrandMistDark,
    onSurfaceVariant = Color(0xFFA3B0AB),
    outline = Color(0xFF3B4642),
    outlineVariant = Color(0xFF2A3330),
    error = Color(0xFFFFB4A8),
    onError = Color(0xFF5A1508),
    errorContainer = Color(0xFF5F2418),
    onErrorContainer = Color(0xFFFFDAD4)
)

/* Corner radii live here so every card, sheet, field and button in
 * the app draws the same geometry. */
val SarvodhayaShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(22.dp),
    extraLarge = RoundedCornerShape(28.dp)
)

/** Default surface card: 18dp corners with a soft drop shadow. */
val CardShape = RoundedCornerShape(18.dp)

/** Larger grouped panels and hero surfaces. */
val PanelShape = RoundedCornerShape(24.dp)

/** Text fields and compact tiles. */
val FieldShape = RoundedCornerShape(14.dp)

/** Fully rounded buttons, chips and pills. */
val PillShape = RoundedCornerShape(percent = 50)

/** Soft ambient shadow used by cards instead of hard borders. */
val CardShadow = 6.dp

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
