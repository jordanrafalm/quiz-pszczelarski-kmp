package pl.quizpszczelarski.app.platform

import androidx.compose.runtime.staticCompositionLocalOf

/**
 * CompositionLocal providing the platform-specific [Haptics] implementation.
 * Set at the root composable (App.kt) via CompositionLocalProvider.
 */
val LocalHaptics = staticCompositionLocalOf<Haptics> {
    // Default no-op; overridden by platform provider
    object : Haptics {
        override fun impact(type: ImpactType) { /* no-op */ }
    }
}
