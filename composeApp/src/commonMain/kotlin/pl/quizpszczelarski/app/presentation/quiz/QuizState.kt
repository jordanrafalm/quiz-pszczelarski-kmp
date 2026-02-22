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
) {
    val currentQuestion: Question? get() = questions.getOrNull(currentQuestionIndex)
    val totalQuestions: Int get() = questions.size
    val progress: Float
        get() = if (totalQuestions == 0) 0f
        else (currentQuestionIndex + 1).toFloat() / totalQuestions
    val isLastQuestion: Boolean get() = currentQuestionIndex + 1 >= totalQuestions
    val canProceed: Boolean get() = selectedAnswerIndex != null
}
