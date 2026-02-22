package pl.quizpszczelarski.app.presentation.result

import kotlin.math.roundToInt

/**
 * Immutable state for the Result screen.
 */
data class ResultState(
    val score: Int = 0,
    val totalQuestions: Int = 0,
    val showNicknamePrompt: Boolean = false,
    val nicknameInput: String = "",
) {
    val percentage: Int
        get() = if (totalQuestions == 0) 0
        else ((score.toFloat() / totalQuestions) * 100).roundToInt()

    val message: String
        get() = when {
            percentage == 100 -> "Doskonale!"
            percentage >= 80 -> "Świetna robota!"
            percentage >= 60 -> "Dobrze Ci poszło!"
            percentage >= 40 -> "Nieźle, ale możesz lepiej!"
            else -> "Spróbuj jeszcze raz!"
        }

    val isHighScore: Boolean get() = percentage >= 80
}
