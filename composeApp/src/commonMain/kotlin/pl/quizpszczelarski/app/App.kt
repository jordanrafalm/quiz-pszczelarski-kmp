package pl.quizpszczelarski.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import pl.quizpszczelarski.app.navigation.AppNavigation
import pl.quizpszczelarski.app.platform.Haptics
import pl.quizpszczelarski.app.platform.ImpactType
import pl.quizpszczelarski.app.platform.LocalHaptics
import pl.quizpszczelarski.app.platform.LocalNotificationScheduler
import pl.quizpszczelarski.app.platform.NoOpNotificationScheduler
import pl.quizpszczelarski.app.platform.NotificationScheduler
import pl.quizpszczelarski.app.platform.SplashSoundPlayer
import pl.quizpszczelarski.app.ui.theme.AppTheme
import pl.quizpszczelarski.shared.data.local.DatabaseDriverFactory
import pl.quizpszczelarski.shared.data.settings.SettingsFactory

@Composable
fun App(
    driverFactory: DatabaseDriverFactory,
    settingsFactory: SettingsFactory,
    haptics: Haptics = object : Haptics {
        override fun impact(type: ImpactType) { /* no-op fallback */ }
    },
    splashSoundPlayer: SplashSoundPlayer? = null,
    notificationScheduler: NotificationScheduler = NoOpNotificationScheduler,
) {
    AppTheme {
        CompositionLocalProvider(
            LocalHaptics provides haptics,
            LocalNotificationScheduler provides notificationScheduler,
        ) {
            AppNavigation(driverFactory, settingsFactory, splashSoundPlayer)
        }
    }
}
