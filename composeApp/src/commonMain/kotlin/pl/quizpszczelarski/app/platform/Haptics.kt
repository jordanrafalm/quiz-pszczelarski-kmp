package pl.quizpszczelarski.app.platform

/**
 * Cross-platform haptic feedback abstraction.
 * Implementations in androidMain and iosMain.
 *
 * Called from UI layer only, gated by SettingsState.hapticsEnabled.
 */
interface Haptics {
    fun impact(type: ImpactType)
}

enum class ImpactType {
    Light,
    Medium,
    Success,
    Error,
}
