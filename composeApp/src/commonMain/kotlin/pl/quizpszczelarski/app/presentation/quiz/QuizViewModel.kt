package pl.quizpszczelarski.app.presentation.quiz

import pl.quizpszczelarski.app.presentation.base.MviViewModel
import pl.quizpszczelarski.shared.domain.model.Question
import pl.quizpszczelarski.shared.domain.usecase.GetRandomQuestionsUseCase

/**
 * ViewModel for the Quiz screen.
 * Manages question progression: select answer → next → result.
 */
class QuizViewModel(
    private val getRandomQuestions: GetRandomQuestionsUseCase,
) : MviViewModel<QuizState, QuizIntent, QuizEffect>(QuizState()) {

    init {
        loadQuestions()
    }

    private fun loadQuestions() {
        val questions = getRandomQuestions()
        onIntent(LoadQuestions(questions))
    }

    override fun reduce(state: QuizState, intent: QuizIntent): QuizState {
        return when (intent) {
            is QuizIntent.SelectAnswer -> state.copy(
                selectedAnswerIndex = intent.index,
            )

            is QuizIntent.NextQuestion -> {
                val selectedIndex = state.selectedAnswerIndex ?: return state
                val currentQuestion = state.currentQuestion ?: return state

                val newScore = if (selectedIndex == currentQuestion.correctAnswerIndex) {
                    state.score + 1
                } else {
                    state.score
                }

                if (state.isLastQuestion) {
                    emitEffect(
                        QuizEffect.NavigateToResult(
                            score = newScore,
                            total = state.totalQuestions,
                        ),
                    )
                    state.copy(score = newScore)
                } else {
                    state.copy(
                        currentQuestionIndex = state.currentQuestionIndex + 1,
                        selectedAnswerIndex = null,
                        score = newScore,
                    )
                }
            }

            is LoadQuestions -> state.copy(
                questions = intent.questions,
                isLoading = false,
                currentQuestionIndex = 0,
                selectedAnswerIndex = null,
                score = 0,
            )
        }
    }
}

/**
 * Internal intent used only by ViewModel to load questions.
 * Not exposed to UI.
 */
internal data class LoadQuestions(val questions: List<Question>) : QuizIntent
