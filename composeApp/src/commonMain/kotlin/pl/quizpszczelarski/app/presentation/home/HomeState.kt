package pl.quizpszczelarski.app.presentation.home

/**
 * Immutable state for the Home screen.
 */
data class HomeState(
    val appTitle: String = "Quiz Pszczelarski",
    val appDescription: String = "Sprawdź swoją wiedzę o pszczołach i pszczelarstwie!",
    val showLevelSelect: Boolean = false,
    val selectedQuestionCount: Int = 5,
    /** Whether Remote Config has flagged new questions as available — shows a badge on Zagraj card. */
    val newQuestionsAvailable: Boolean = false,
)
