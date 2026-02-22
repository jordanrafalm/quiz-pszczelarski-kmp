package pl.quizpszczelarski.app.presentation.home

/**
 * Immutable state for the Home screen.
 */
data class HomeState(
    val appTitle: String = "Quiz Pszczelarski",
    val appDescription: String = "Sprawdź swoją wiedzę o pszczołach i pszczelarstwie!",
    val showLevelSelect: Boolean = false,
    val selectedQuestionCount: Int = 5,
)
