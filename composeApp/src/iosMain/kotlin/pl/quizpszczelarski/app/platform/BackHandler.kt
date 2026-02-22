package pl.quizpszczelarski.app.platform

import androidx.compose.runtime.Composable

/** iOS doesn't have a hardware back button — swipe gesture is handled by the OS. */
@Composable
actual fun BackHandler(enabled: Boolean, onBack: () -> Unit) {
    // no-op on iOS
}
