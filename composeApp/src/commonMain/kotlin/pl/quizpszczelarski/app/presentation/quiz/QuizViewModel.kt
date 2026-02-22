package pl.quizpszczelarski.app.presentation.quiz

import kotlinx.coroutines.launch
import pl.quizpszczelarski.app.platform.ImpactType
import pl.quizpszczelarski.app.presentation.base.MviViewModel
import pl.quizpszczelarski.shared.domain.model.Question
import pl.quizpszczelarski.shared.domain.repository.QuestionSyncService
import pl.quizpszczelarski.shared.domain.repository.SyncResult
import pl.quizpszczelarski.shared.domain.usecase.GetRandomQuestionsUseCase

/**
 * ViewModel for the Quiz screen.
 * Manages question progression: select answer → next → result.
 * Supports cache-first loading with background sync.
 */
class QuizViewModel(
    private val getRandomQuestions: GetRandomQuestionsUseCase,
    private val syncService: QuestionSyncService,
    private val level: String = "easy",
    private val questionCount: Int = 5,
) : MviViewModel<QuizState, QuizIntent, QuizEffect>(QuizState()) {

    init {
        loadQuestions()
    }

    private fun loadQuestions() {
        scope.launch {
            try {
                // Step 1: Load from cache (or remote if first launch)
                val questions = getRandomQuestions(count = questionCount, level = level)

                if (questions.isEmpty()) {
                    // No questions available (empty cache + offline)
                    emitEffect(QuizEffect.NoQuestionsAvailable)
                    return@launch
                }

                onIntent(LoadQuestions(questions))

                // Step 2: Background sync (doesn't replace current quiz questions)
                onIntent(SyncStarted)
                val syncResult = syncService.syncQuestionsIfNeeded()
                onIntent(SyncCompleted(syncResult))

                when (syncResult) {
                    SyncResult.UPDATED ->
                        emitEffect(QuizEffect.ShowSnackbar("Zaktualizowano pytania"))
                    SyncResult.FAILED ->
                        emitEffect(QuizEffect.ShowSnackbar("Tryb offline — używam zapisanych pytań"))
                    else -> { /* no user notification needed */ }
                }
            } catch (_: Exception) {
                onIntent(ShowLoadError("Nie udało się załadować pytań"))
            }
        }
    }

    override fun reduce(state: QuizState, intent: QuizIntent): QuizState {
        return when (intent) {
            is QuizIntent.SelectAnswer -> {
                emitEffect(QuizEffect.PlayHaptic(ImpactType.Light))
                state.copy(
                    selectedAnswerIndex = intent.index,
                )
            }

            is QuizIntent.RetryLoad -> {
                loadQuestions()
                state.copy(isLoading = true, errorMessage = null)
            }

            is QuizIntent.ExitQuiz -> {
                emitEffect(QuizEffect.NavigateToHome)
                state
            }

            is QuizIntent.NextQuestion -> {
                val selectedIndex = state.selectedAnswerIndex ?: return state
                val currentQuestion = state.currentQuestion ?: return state

                val newScore = if (selectedIndex == currentQuestion.correctAnswerIndex) {
                    state.score + 1
                } else {
                    state.score
                }

                if (state.isLastQuestion) {
                    val hapticType = if (newScore > state.totalQuestions / 2) {
                        ImpactType.Success
                    } else {
                        ImpactType.Medium
                    }
                    emitEffect(QuizEffect.PlayHaptic(hapticType))
                    emitEffect(
                        QuizEffect.NavigateToResult(
                            score = newScore,
                            total = state.totalQuestions,
                        ),
                    )
                    state.copy(score = newScore)
                } else {
                    emitEffect(QuizEffect.PlayHaptic(ImpactType.Light))
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

            is SyncStarted -> state.copy(isRefreshing = true)

            is SyncCompleted -> state.copy(
                isRefreshing = false,
                isOffline = intent.result == SyncResult.FAILED,
            )
        }
    }
}

/** Internal intent: questions loaded successfully. */
internal data class LoadQuestions(val questions: List<Question>) : QuizIntent

/** Internal intent: question loading failed. */
internal data class ShowLoadError(val message: String) : QuizIntent

/** Internal intent: sync started. */
internal data object SyncStarted : QuizIntent

/** Internal intent: sync completed. */
internal data class SyncCompleted(val result: SyncResult) : QuizIntent
