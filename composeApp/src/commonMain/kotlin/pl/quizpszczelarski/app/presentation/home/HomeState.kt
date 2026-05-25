package pl.quizpszczelarski.app.presentation.home

import pl.quizpszczelarski.app.presentation.gameofday.GameOfDayType

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
    /** True if the user already completed today's Game of Day — shows ✅ badge on the card. */
    val gameOfDayCompleted: Boolean = false,
    /** Score from today's Game of Day run (0 if not yet completed). */
    val gameOfDayScore: Int = 0,
    /** Today's game type — shown in the "Gra Dnia" card description. */
    val todayGameType: GameOfDayType = GameOfDayType.FlappyBee,
)
