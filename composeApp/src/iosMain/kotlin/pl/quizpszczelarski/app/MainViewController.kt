package pl.quizpszczelarski.app

import androidx.compose.ui.window.ComposeUIViewController
import pl.quizpszczelarski.app.platform.IosHaptics
import pl.quizpszczelarski.app.platform.IosNotificationScheduler
import pl.quizpszczelarski.app.platform.IosSplashSoundPlayer
import pl.quizpszczelarski.shared.data.local.DatabaseDriverFactory
import pl.quizpszczelarski.shared.data.settings.SettingsFactory

fun MainViewController() = ComposeUIViewController(
    configure = {
        // Disable strict plist sanity check for CADisableMinimumFrameDurationOnPhone
        // This key cannot be added via GENERATE_INFOPLIST_FILE in Xcode
        enforceStrictPlistSanityCheck = false
    }
) {
    App(
        driverFactory = DatabaseDriverFactory(),
        settingsFactory = SettingsFactory(),
        haptics = IosHaptics(),
        splashSoundPlayer = IosSplashSoundPlayer(),
        notificationScheduler = IosNotificationScheduler(),
    )
}
