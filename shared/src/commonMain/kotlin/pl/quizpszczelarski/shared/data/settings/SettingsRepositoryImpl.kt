package pl.quizpszczelarski.shared.data.settings

import com.russhwolf.settings.Settings
import com.russhwolf.settings.get
import com.russhwolf.settings.set
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import pl.quizpszczelarski.shared.domain.model.SettingsState
import pl.quizpszczelarski.shared.domain.repository.SettingsRepository

class SettingsRepositoryImpl(
    private val settings: Settings,
) : SettingsRepository {

    companion object {
        private const val KEY_HAPTICS_ENABLED = "haptics_enabled"
        private const val KEY_SOUND_ENABLED = "sound_enabled"
        private const val KEY_HAS_CUSTOM_NICKNAME = "has_custom_nickname"
        private const val KEY_NOTIFICATIONS_ENABLED = "notifications_enabled"
        private const val KEY_APP_LAUNCH_COUNT = "app_launch_count"
    }

    private val _state = MutableStateFlow(readFromDisk())

    override fun getSettingsFlow(): Flow<SettingsState> = _state.asStateFlow()

    override fun getSettings(): SettingsState = _state.value

    override suspend fun setHapticsEnabled(enabled: Boolean) {
        settings[KEY_HAPTICS_ENABLED] = enabled
        _state.value = _state.value.copy(hapticsEnabled = enabled)
    }

    override suspend fun setSoundEnabled(enabled: Boolean) {
        settings[KEY_SOUND_ENABLED] = enabled
        _state.value = _state.value.copy(soundEnabled = enabled)
    }

    override fun hasCustomNickname(): Boolean = settings[KEY_HAS_CUSTOM_NICKNAME, false]

    override suspend fun setHasCustomNickname(value: Boolean) {
        settings[KEY_HAS_CUSTOM_NICKNAME] = value
    }

    override suspend fun setNotificationsEnabled(enabled: Boolean) {
        settings[KEY_NOTIFICATIONS_ENABLED] = enabled
        _state.value = _state.value.copy(notificationsEnabled = enabled)
    }

    override fun getAppLaunchCount(): Int = settings[KEY_APP_LAUNCH_COUNT, 0]

    override suspend fun incrementAppLaunchCount() {
        val newCount = getAppLaunchCount() + 1
        settings[KEY_APP_LAUNCH_COUNT] = newCount
    }

    private fun readFromDisk(): SettingsState {
        return SettingsState(
            hapticsEnabled = settings[KEY_HAPTICS_ENABLED, true],
            soundEnabled = settings[KEY_SOUND_ENABLED, true],
            notificationsEnabled = settings[KEY_NOTIFICATIONS_ENABLED, true],
        )
    }
}
