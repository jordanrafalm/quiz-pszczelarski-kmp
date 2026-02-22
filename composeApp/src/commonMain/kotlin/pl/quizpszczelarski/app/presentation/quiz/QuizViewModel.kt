package pl.quizpszczelarski.app.presentation.quiz

import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import pl.quizpszczelarski.app.platform.ImpactType
import pl.quizpszczelarski.app.presentation.base.MviViewModel
import pl.quizpszczelarski.app.presentation.quiz.QuizIntent.LoadQuestions
import pl.quizpszczelarski.app.presentation.quiz.QuizIntent.ShowLoadError
import pl.quizpszczelarski.app.presentation.quiz.QuizIntent.SyncCompleted
import pl.quizpszczelarski.app.presentation.quiz.QuizIntent.SyncStarted
import pl.quizpszczelarski.shared.data.analytics.hashPrefix
import pl.quizpszczelarski.shared.data.util.currentTimeMillis
import pl.quizpszczelarski.shared.domain.model.Question
import pl.quizpszczelarski.shared.domain.repository.QuestionSyncService
import pl.quizpszczelarski.shared.domain.repository.SyncResult
import pl.quizpszczelarski.shared.domain.service.AnalyticsService
import pl.quizpszczelarski.shared.domain.usecase.GetRandomQuestionsUseCase

/**
 * ViewModel for the Quiz screen.
 * Manages question progression: select answer → next → result.
 * Supports cache-first loading with background sync.
 */
class QuizViewModel(
    private val getRandomQuestions: GetRandomQuestionsUseCase,
    private val syncService: QuestionSyncService,
    private val analyticsService: AnalyticsService,
    private val level: String = "easy",
    private val questionCount: Int = 5,
    private val quizRunIndex: Int = 1,
) : MviViewModel<QuizState, QuizIntent, QuizEffect>(QuizState()) {

    private val mode = "quiz" // MVP — single mode; future: "learning", "exam"
    private var quizStartTimeMs: Long = 0L
    private var autoAdvanceJob: Job? = null

    /** Schedules NextQuestion after [delayMs], e.g. to let feedback animations play out. */
    private fun scheduleAutoAdvance(delayMs: Long) {
        autoAdvanceJob?.cancel()
        autoAdvanceJob = scope.launch {
            delay(delayMs)
            onIntent(QuizIntent.NextQuestion)
        }
    }

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

                // Analytics: quiz started
                quizStartTimeMs = currentTimeMillis()
                analyticsService.logQuizStarted(mode, level, questions.size, quizRunIndex)

                // Crashlytics: set quiz ID hash from concatenated question IDs
                val quizIdHash = hashPrefix(questions.joinToString(",") { it.id })
                analyticsService.setCustomKey("quiz_id_hash", quizIdHash)

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
            } catch (e: Exception) {
                analyticsService.recordNonFatal(e, mapOf(
                    "context" to "load_questions",
                    "level" to level,
                ))
                onIntent(ShowLoadError("Nie udało się załadować pytań"))
            }
        }
    }

    override fun reduce(state: QuizState, intent: QuizIntent): QuizState {
        return when (intent) {
            is QuizIntent.SelectAnswer -> {
                if (state.isAnswerChecked) return state // blocked after check
                emitEffect(QuizEffect.PlayHaptic(ImpactType.Light))
                // Crashlytics: track current question hash
                val question = state.currentQuestion
                if (question != null) {
                    analyticsService.setCustomKey("question_id_hash", hashPrefix(question.id))
                }
                state.copy(
                    selectedAnswerIndex = intent.index,
                )
            }

            is QuizIntent.CheckAnswer -> {
                val selectedIndex = state.selectedAnswerIndex ?: return state
                if (state.isAnswerChecked) return state // already checked
                val currentQuestion = state.currentQuestion ?: return state

                val isCorrect = selectedIndex == currentQuestion.correctAnswerIndex
                val newScore = if (isCorrect) state.score + 1 else state.score
                val newStreak = if (isCorrect) state.consecutiveCorrect + 1 else 0
                val triggerBonus = newStreak >= 3 && !state.bonusShown

                if (isCorrect) {
                    emitEffect(QuizEffect.PlayHaptic(ImpactType.Success))
                } else {
                    emitEffect(QuizEffect.PlayHaptic(ImpactType.Error))
                }

                state.copy(
                    score = newScore,
                    answerFeedback = if (isCorrect) AnswerFeedback.Correct else AnswerFeedback.Wrong,
                    correctAnswerIndex = if (!isCorrect) currentQuestion.correctAnswerIndex else null,
                    consecutiveCorrect = newStreak,
                    showBonus = triggerBonus,
                    bonusShown = if (triggerBonus) true else state.bonusShown,
                    currentInfotip = if (!isCorrect) currentQuestion.infotip.takeIf { it.isNotBlank() } else null,
                ).also {
                    // Correct + no bonus: auto-advance so user doesn't need a second button tap
                    if (isCorrect && !triggerBonus) scheduleAutoAdvance(900)
                }
            }

            is QuizIntent.DismissBonus -> {
                // After bonus overlay, auto-advance with a short delay
                scheduleAutoAdvance(600)
                state.copy(showBonus = false)
            }

            is QuizIntent.RetryLoad -> {
                loadQuestions()
                state.copy(isLoading = true, errorMessage = null)
            }

            is QuizIntent.ExitQuiz -> {
                autoAdvanceJob?.cancel()
                // Analytics: quiz abandoned
                val durationMs = if (quizStartTimeMs > 0) currentTimeMillis() - quizStartTimeMs else 0L
                analyticsService.logQuizAbandoned(
                    mode = mode,
                    level = level,
                    questionCount = state.totalQuestions,
                    questionsAnswered = state.currentQuestionIndex,
                    durationMs = durationMs,
                    quizRunIndex = quizRunIndex,
                )
                emitEffect(QuizEffect.NavigateToHome)
                state
            }

            is QuizIntent.NextQuestion -> {
                autoAdvanceJob?.cancel()
                if (!state.isAnswerChecked) return state // must check first

                if (state.isLastQuestion) {
                    // Analytics: quiz completed
                    val durationMs = if (quizStartTimeMs > 0) currentTimeMillis() - quizStartTimeMs else 0L
                    analyticsService.logQuizCompleted(
                        mode = mode,
                        level = level,
                        questionCount = state.totalQuestions,
                        score = state.score,
                        durationMs = durationMs,
                        quizRunIndex = quizRunIndex,
                    )

                    val hapticType = if (state.score > state.totalQuestions / 2) {
                        ImpactType.Success
                    } else {
                        ImpactType.Medium
                    }
                    emitEffect(QuizEffect.PlayHaptic(hapticType))
                    emitEffect(
                        QuizEffect.NavigateToResult(
                            score = state.score,
                            total = state.totalQuestions,
                        ),
                    )
                    state
                } else {
                    // Crashlytics: track current question index
                    analyticsService.setCustomKey("current_question_index", (state.currentQuestionIndex + 1).toString())

                    emitEffect(QuizEffect.PlayHaptic(ImpactType.Light))
                    state.copy(
                        currentQuestionIndex = state.currentQuestionIndex + 1,
                        selectedAnswerIndex = null,
                        answerFeedback = null,
                        correctAnswerIndex = null,
                        currentInfotip = null,
                        showBonus = false,
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
                answerFeedback = null,
                correctAnswerIndex = null,
                consecutiveCorrect = 0,
                bonusShown = false,
                showBonus = false,
                currentInfotip = null,
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
