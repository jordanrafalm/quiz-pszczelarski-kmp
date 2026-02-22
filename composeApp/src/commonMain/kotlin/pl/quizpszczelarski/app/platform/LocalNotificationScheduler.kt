package pl.quizpszczelarski.app.platform

import androidx.compose.runtime.staticCompositionLocalOf

/**
 * CompositionLocal providing the platform-specific [NotificationScheduler] implementation.
 * Set at the root composable (App.kt) via CompositionLocalProvider.
 */
val LocalNotificationScheduler = staticCompositionLocalOf<NotificationScheduler> {
    NoOpNotificationScheduler
}
