package pl.quizpszczelarski.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable

/**
 * Root theme composable for Quiz Pszczelarski.
 *
 * Wraps [MaterialTheme] with Figma-derived colors, typography, and shapes,
 * and provides [AppSpacing] + [ExtendedColors] via CompositionLocal.
 */
@Composable
fun AppTheme(
    content: @Composable () -> Unit,
) {
    val colorScheme = appLightColorScheme()
    val typography = appTypography()
    val shapes = appShapes()
    val spacing = AppSpacing()
    val extendedColors = ExtendedColors()

    CompositionLocalProvider(
        LocalAppSpacing provides spacing,
        LocalExtendedColors provides extendedColors,
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = typography,
            shapes = shapes,
            content = content,
        )
    }
}

/**
 * Convenience accessor for theme extensions not in [MaterialTheme].
 *
 * Usage:
 * ```
 * val spacing = AppTheme.spacing
 * val colors = AppTheme.extendedColors
 * ```
 */
object AppTheme {

    val spacing: AppSpacing
        @Composable
        @ReadOnlyComposable
        get() = LocalAppSpacing.current

    val extendedColors: ExtendedColors
        @Composable
        @ReadOnlyComposable
        get() = LocalExtendedColors.current
}
