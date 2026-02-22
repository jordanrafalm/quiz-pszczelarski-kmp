package pl.quizpszczelarski.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import pl.quizpszczelarski.app.navigation.AppNavigation
import pl.quizpszczelarski.app.platform.Haptics
import pl.quizpszczelarski.app.platform.ImpactType
import pl.quizpszczelarski.app.platform.LocalHaptics
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
) {
    AppTheme {
        CompositionLocalProvider(LocalHaptics provides haptics) {
            AppNavigation(driverFactory, settingsFactory, splashSoundPlayer)
        }
    }
}
