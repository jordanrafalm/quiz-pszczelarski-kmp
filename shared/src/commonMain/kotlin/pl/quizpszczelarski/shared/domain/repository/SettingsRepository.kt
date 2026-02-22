package pl.quizpszczelarski.shared.domain.repository

import kotlinx.coroutines.flow.Flow
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
}
