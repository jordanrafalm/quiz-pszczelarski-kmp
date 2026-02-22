package pl.quizpszczelarski.app.presentation.home

/**
 * User actions on the Home screen.
 */
sealed interface HomeIntent {
    data object StartQuiz : HomeIntent
    data object ViewLeaderboard : HomeIntent
    data object ToggleHaptics : HomeIntent
    data object ToggleSound : HomeIntent
    data object ToggleNotifications : HomeIntent
    data class SelectLevel(val level: String) : HomeIntent
    data object BackFromLevelSelect : HomeIntent
    data class SetQuestionCount(val count: Int) : HomeIntent
}
