package pl.quizpszczelarski.app

import androidx.compose.ui.window.ComposeUIViewController
import pl.quizpszczelarski.shared.data.local.DatabaseDriverFactory

fun MainViewController() = ComposeUIViewController(
    configure = {
        // Disable strict plist sanity check for CADisableMinimumFrameDurationOnPhone
        // This key cannot be added via GENERATE_INFOPLIST_FILE in Xcode
        enforceStrictPlistSanityCheck = false
    }
) { App(driverFactory = DatabaseDriverFactory()) }
