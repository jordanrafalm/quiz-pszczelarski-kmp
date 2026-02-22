package pl.quizpszczelarski.app.presentation.quiz

import kotlinx.coroutines.launch
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
        scope.launch {
            try {
                val questions = getRandomQuestions()
                onIntent(LoadQuestions(questions))
            } catch (_: Exception) {
                onIntent(ShowLoadError("Nie udało się załadować pytań"))
            }
        }
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
                errorMessage = null,
                currentQuestionIndex = 0,
                selectedAnswerIndex = null,
                score = 0,
            )

            is ShowLoadError -> state.copy(
                isLoading = false,
                errorMessage = intent.message,
            )
        }
    }
}

/** Internal intent: questions loaded successfully. */
internal data class LoadQuestions(val questions: List<Question>) : QuizIntent

/** Internal intent: question loading failed. */
internal data class ShowLoadError(val message: String) : QuizIntent
