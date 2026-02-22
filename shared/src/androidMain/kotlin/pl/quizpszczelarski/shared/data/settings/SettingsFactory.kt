package pl.quizpszczelarski.shared.data.settings

import android.content.Context
import com.russhwolf.settings.Settings
import com.russhwolf.settings.SharedPreferencesSettings

actual class SettingsFactory(private val context: Context) {
    actual fun create(): Settings {
        return SharedPreferencesSettings(
            context.getSharedPreferences("quiz_settings", Context.MODE_PRIVATE),
        )
    }
}
