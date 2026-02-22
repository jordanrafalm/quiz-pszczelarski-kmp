package pl.quizpszczelarski.app.presentation.home

import pl.quizpszczelarski.app.platform.ImpactType

/**
 * One-off effects emitted by HomeViewModel.
 */
sealed interface HomeEffect {
    data class NavigateToQuiz(val level: String, val questionCount: Int) : HomeEffect
    data object NavigateToLeaderboard : HomeEffect
    data class PlayHaptic(val type: ImpactType) : HomeEffect
    /** Toggle haptics on/off — handled by AppNavigation via SettingsRepository. */
    data object ToggleHaptics : HomeEffect
    /** Toggle sound on/off — handled by AppNavigation via SettingsRepository. */
    data object ToggleSound : HomeEffect
    /** Toggle notifications on/off — handled by AppNavigation via SettingsRepository + NotificationScheduler. */
    data object ToggleNotifications : HomeEffect
}
