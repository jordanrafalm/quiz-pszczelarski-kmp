package pl.quizpszczelarski.app.presentation.quiz

import pl.quizpszczelarski.shared.domain.model.Question

/**
 * Immutable state for the Quiz screen.
 */
data class QuizState(
    val questions: List<Question> = emptyList(),
    val currentQuestionIndex: Int = 0,
    val selectedAnswerIndex: Int? = null,
    val score: Int = 0,
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val isOffline: Boolean = false,
    val errorMessage: String? = null,
    // Answer feedback
    val answerFeedback: AnswerFeedback? = null,
    val correctAnswerIndex: Int? = null,
    val consecutiveCorrect: Int = 0,
    val bonusShown: Boolean = false,
    val showBonus: Boolean = false,
    val currentInfotip: String? = null,
) {
    val currentQuestion: Question? get() = questions.getOrNull(currentQuestionIndex)
    val totalQuestions: Int get() = questions.size
    val progress: Float
        get() = if (totalQuestions == 0) 0f
        else (currentQuestionIndex + 1).toFloat() / totalQuestions
    val isLastQuestion: Boolean get() = currentQuestionIndex + 1 >= totalQuestions
    val isAnswerChecked: Boolean get() = answerFeedback != null
    val canProceed: Boolean get() = selectedAnswerIndex != null && answerFeedback != null
}
