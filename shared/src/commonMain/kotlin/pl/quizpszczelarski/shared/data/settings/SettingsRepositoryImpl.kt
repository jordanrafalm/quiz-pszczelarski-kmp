package pl.quizpszczelarski.shared.data.settings

import com.russhwolf.settings.Settings
import com.russhwolf.settings.get
import com.russhwolf.settings.set
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import pl.quizpszczelarski.shared.domain.model.NotificationPhase
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
        private const val KEY_GOD_NOTIFICATION_PHASE = "god_notification_phase"
        private const val KEY_SAVED_APP_VERSION = "saved_app_version"
        private const val KEY_SEEN_GOD_INTRO = "seen_god_intro"
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

    override fun getGameOfDayNotificationPhase(): NotificationPhase {
        val name = settings[KEY_GOD_NOTIFICATION_PHASE, NotificationPhase.INITIAL.name]
        return NotificationPhase.entries.firstOrNull { it.name == name } ?: NotificationPhase.INITIAL
    }

    override suspend fun setGameOfDayNotificationPhase(phase: NotificationPhase) {
        settings[KEY_GOD_NOTIFICATION_PHASE] = phase.name
    }

    override fun getSavedAppVersion(): String = settings[KEY_SAVED_APP_VERSION, ""]

    override suspend fun setSavedAppVersion(version: String) {
        settings[KEY_SAVED_APP_VERSION] = version
    }

    override fun hasSeenGameOfDayIntro(): Boolean = settings[KEY_SEEN_GOD_INTRO, false]

    override suspend fun setSeenGameOfDayIntro(seen: Boolean) {
        settings[KEY_SEEN_GOD_INTRO] = seen
    }

    private fun readFromDisk(): SettingsState {
        return SettingsState(
            hapticsEnabled = settings[KEY_HAPTICS_ENABLED, true],
            soundEnabled = settings[KEY_SOUND_ENABLED, true],
            notificationsEnabled = settings[KEY_NOTIFICATIONS_ENABLED, true],
        )
    }
}
