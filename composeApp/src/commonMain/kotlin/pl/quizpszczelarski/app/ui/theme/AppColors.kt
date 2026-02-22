package pl.quizpszczelarski.app.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

/**
 * Figma-derived color palette for Quiz Pszczelarski.
 *
 * Light theme only (MVP). Dark theme can be added later via ADR.
 */
fun appLightColorScheme(): ColorScheme = lightColorScheme(
    background = Color(0xFFFFF9ED),          // warm cream
    onBackground = Color(0xFF2C2C2C),        // dark gray
    surface = Color(0xFFFFFFFF),             // card white
    onSurface = Color(0xFF2C2C2C),
    primary = Color(0xFFFFC933),             // honey gold
    onPrimary = Color(0xFF2C2C2C),           // dark on gold
    secondary = Color(0xFF8BA888),           // sage green
    onSecondary = Color(0xFFFFFFFF),
    tertiary = Color(0xFFA67C52),            // warm brown
    onTertiary = Color(0xFFFFFFFF),
    error = Color(0xFFD4183D),
    onError = Color(0xFFFFFFFF),
    surfaceVariant = Color(0xFFFFF9ED),      // muted
    onSurfaceVariant = Color(0xFF6B6B6B),
    outline = Color(0x1A2C2C2C),             // rgba(44,44,44,0.1)
    primaryContainer = Color(0x33FFC933),    // 20% alpha gold
    onPrimaryContainer = Color(0xFF2C2C2C),
    secondaryContainer = Color(0x1A8BA888),  // 10% alpha green
    onSecondaryContainer = Color(0xFF2C2C2C),
)

/**
 * Extended colors not covered by Material 3 ColorScheme.
 * Accessed via [AppTheme.extendedColors].
 */
@Immutable
data class ExtendedColors(
    val correctAnswer: Color = Color(0xFF4CAF50),
    val wrongAnswer: Color = Color(0xFFD4183D),
    val selectedAnswerBorder: Color = Color(0xFFFFC933),
    val selectedAnswerBackground: Color = Color(0x0DFFC933),  // 5% alpha
)

val LocalExtendedColors = staticCompositionLocalOf { ExtendedColors() }
