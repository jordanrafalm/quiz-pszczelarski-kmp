package pl.quizpszczelarski.app.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

/**
 * Shape overrides derived from Figma rounded corners.
 */
fun appShapes(): Shapes = Shapes(
    small = RoundedCornerShape(8.dp),       // badges, chips
    medium = RoundedCornerShape(12.dp),     // buttons, answer options, tabs
    large = RoundedCornerShape(16.dp),      // cards (rounded-2xl equivalent)
    extraLarge = RoundedCornerShape(24.dp), // large cards, sheets
)
