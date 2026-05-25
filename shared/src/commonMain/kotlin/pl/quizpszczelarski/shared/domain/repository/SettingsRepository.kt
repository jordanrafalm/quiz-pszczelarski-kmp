package pl.quizpszczelarski.shared.domain.repository

import kotlinx.coroutines.flow.Flow
import pl.quizpszczelarski.shared.domain.model.NotificationPhase
import pl.quizpszczelarski.shared.domain.model.SettingsState

/**
 * Repository for reading and writing app-level settings.
 */
interface SettingsRepository {
    /** Observe settings changes as a Flow. */
    fun getSettingsFlow(): Flow<SettingsState>

    /** Get current settings snapshot (non-suspending). */
    fun getSettings(): SettingsState

    /** Toggle haptics on/off. */
    suspend fun setHapticsEnabled(enabled: Boolean)

    /** Toggle sound on/off. */
    suspend fun setSoundEnabled(enabled: Boolean)

    /** Check if user has set a custom nickname. */
    fun hasCustomNickname(): Boolean

    /** Mark that user has set a custom nickname. */
    suspend fun setHasCustomNickname(value: Boolean)

    /** Toggle notifications on/off. */
    suspend fun setNotificationsEnabled(enabled: Boolean)

    /** How many times the app has been launched. */
    fun getAppLaunchCount(): Int

    /** Increment app launch counter by 1. */
    suspend fun incrementAppLaunchCount()

    // ── Notification phase tracking ─────────────────────────────────────────

    /** Current phase of Game of Day notification scheduling. */
    fun getGameOfDayNotificationPhase(): NotificationPhase

    /** Persist the current Game of Day notification phase. */
    suspend fun setGameOfDayNotificationPhase(phase: NotificationPhase)

    // ── Intro dialog tracking ────────────────────────────────────────────────

    /** Saved app version string (used to detect fresh install / update). */
    fun getSavedAppVersion(): String

    /** Persist the app version seen by the user. */
    suspend fun setSavedAppVersion(version: String)

    /** Whether the "Game of Day intro" dialog has already been shown. */
    fun hasSeenGameOfDayIntro(): Boolean

    /** Mark that the intro dialog has been shown. */
    suspend fun setSeenGameOfDayIntro(seen: Boolean)
}
