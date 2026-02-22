package pl.quizpszczelarski.app.platform

import androidx.compose.runtime.Composable

/**
 * Multiplatform back-press handler.
 * - Android: intercepts the hardware/system back button via androidx.activity.
 * - iOS: no-op (navigation back is handled by swipe gesture at the OS level).
 */
@Composable
expect fun BackHandler(enabled: Boolean = true, onBack: () -> Unit)
