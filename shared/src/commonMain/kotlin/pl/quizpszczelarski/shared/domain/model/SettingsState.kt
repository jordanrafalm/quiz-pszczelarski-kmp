package pl.quizpszczelarski.shared.domain.model

/**
 * Global app settings. Immutable snapshot.
 */
data class SettingsState(
    val hapticsEnabled: Boolean = true,
    val soundEnabled: Boolean = true,
    val notificationsEnabled: Boolean = true,
)
