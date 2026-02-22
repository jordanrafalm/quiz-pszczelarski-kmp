package pl.quizpszczelarski.shared.data.settings

import com.russhwolf.settings.NSUserDefaultsSettings
import com.russhwolf.settings.Settings

actual class SettingsFactory {
    actual fun create(): Settings {
        return NSUserDefaultsSettings.Factory().create("quiz_settings")
    }
}
