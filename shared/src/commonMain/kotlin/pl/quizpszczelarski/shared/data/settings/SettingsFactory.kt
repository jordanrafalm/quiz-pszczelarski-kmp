package pl.quizpszczelarski.shared.data.settings

import com.russhwolf.settings.Settings

expect class SettingsFactory {
    fun create(): Settings
}
