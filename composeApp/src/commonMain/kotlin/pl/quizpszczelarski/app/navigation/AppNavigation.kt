package pl.quizpszczelarski.app.navigation

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import dev.gitlive.firebase.firestore.firestore
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import pl.quizpszczelarski.app.presentation.home.HomeEffect
import pl.quizpszczelarski.app.presentation.home.HomeViewModel
import pl.quizpszczelarski.app.presentation.leaderboard.LeaderboardEffect
import pl.quizpszczelarski.app.presentation.leaderboard.LeaderboardViewModel
import pl.quizpszczelarski.app.presentation.quiz.QuizEffect
import pl.quizpszczelarski.app.presentation.quiz.QuizViewModel
import pl.quizpszczelarski.app.presentation.result.ResultEffect
import pl.quizpszczelarski.app.presentation.result.ResultViewModel
import pl.quizpszczelarski.app.ui.screens.HomeScreen
import pl.quizpszczelarski.app.ui.screens.LeaderboardScreen
import pl.quizpszczelarski.app.ui.screens.QuizScreen
import pl.quizpszczelarski.app.ui.screens.ResultScreen
import pl.quizpszczelarski.app.ui.screens.SplashScreen
import pl.quizpszczelarski.shared.data.leaderboard.FirebaseLeaderboardRepository
import pl.quizpszczelarski.shared.data.local.DatabaseDriverFactory
import pl.quizpszczelarski.shared.data.local.SqlDelightPendingScoreDataSource
import pl.quizpszczelarski.shared.data.local.SqlDelightQuestionsDataSource
import pl.quizpszczelarski.shared.data.local.db.QuizDatabase
import pl.quizpszczelarski.shared.data.questions.CachingQuestionsRepository
import pl.quizpszczelarski.shared.data.remote.FirestoreQuestionsDataSource
import pl.quizpszczelarski.shared.data.user.FirebaseUserRepository
import pl.quizpszczelarski.shared.domain.usecase.EnsureUserUseCase
import pl.quizpszczelarski.shared.domain.usecase.GetRandomQuestionsUseCase
import pl.quizpszczelarski.shared.domain.usecase.SubmitScoreUseCase
import pl.quizpszczelarski.shared.domain.usecase.flushPendingScores

/**
 * Simple state-based navigation using [AnimatedContent].
 *
 * MVP approach: no third-party navigation library.
 * ViewModels are created directly (no DI yet — Koin in Phase 3+).
 */
@Composable
fun AppNavigation(driverFactory: DatabaseDriverFactory) {
    var currentRoute by remember { mutableStateOf<Route>(Route.Splash) }

    // Firebase instances (GitLive singletons)
    val auth = remember { Firebase.auth }
    val firestore = remember { Firebase.firestore }

    // Database + data sources
    val database = remember { QuizDatabase(driverFactory.create()) }
    val localDataSource = remember { SqlDelightQuestionsDataSource(database) }
    val remoteDataSource = remember { FirestoreQuestionsDataSource(firestore) }
    val pendingScoreDataSource = remember { SqlDelightPendingScoreDataSource(database) }

    // Repositories
    val questionRepository = remember { CachingQuestionsRepository(localDataSource, remoteDataSource) }
    val questionSyncService: pl.quizpszczelarski.shared.domain.repository.QuestionSyncService = questionRepository
    val userRepository = remember { FirebaseUserRepository(auth, firestore) }
    val leaderboardRepository = remember { FirebaseLeaderboardRepository(firestore) }

    // Use cases
    val getRandomQuestions = remember { GetRandomQuestionsUseCase(questionRepository) }
    val submitScore = remember { SubmitScoreUseCase(userRepository) }
    val ensureUser = remember { EnsureUserUseCase(userRepository) }

    // User session state (shared across screens)
    var currentUid by remember { mutableStateOf<String?>(null) }

    // Snackbar state (shared across all screens)
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    /** Show a snackbar message from any screen. */
    fun showSnackbar(message: String) {
        coroutineScope.launch {
            snackbarHostState.currentSnackbarData?.dismiss()
            snackbarHostState.showSnackbar(message)
        }
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { data ->
                Snackbar(
                    snackbarData = data,
                    containerColor = MaterialTheme.colorScheme.inverseSurface,
                    contentColor = MaterialTheme.colorScheme.inverseOnSurface,
                )
            }
        },
        containerColor = MaterialTheme.colorScheme.background,
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .statusBarsPadding(),
        ) {
        AnimatedContent(targetState = currentRoute) { route ->
            when (route) {
                Route.Splash -> {
                    SplashScreen(
                        onSplashFinished = { /* handled by LaunchedEffect */ },
                    )
                    LaunchedEffect(Unit) {
                        val bootstrapJob = async {
                            try {
                                val (uid, _) = ensureUser()
                                currentUid = uid
                            } catch (_: Exception) {
                                // Proceed without auth (offline / error fallback)
                            }
                        }
                        // Background: sync questions while splash shows
                        val syncJob = async {
                            try {
                                questionRepository.syncQuestionsIfNeeded()
                            } catch (_: Exception) { }
                        }
                        // Ensure splash shows for at least 2s, but don't block forever
                        delay(2000L)
                        // Give auth and sync a few more seconds, then proceed regardless
                        withTimeoutOrNull(4000L) { bootstrapJob.await() }
                        withTimeoutOrNull(2000L) { syncJob.await() }

                        // Submit pending scores from previous offline sessions.
                        // Uses coroutineScope (not LaunchedEffect) so it survives route change.
                        val flushUid = currentUid
                        if (flushUid != null) {
                            coroutineScope.launch {
                                try {
                                    flushPendingScores(flushUid, submitScore, pendingScoreDataSource)
                                } catch (_: Exception) { }
                            }
                        }

                        currentRoute = Route.Home
                    }
                }

                Route.Home -> {
                    val vm = remember { HomeViewModel() }
                    DisposableEffect(Unit) { onDispose { vm.onCleared() } }
                    val state by vm.state.collectAsState()

                    LaunchedEffect(Unit) {
                        vm.effect.collect { effect ->
                            when (effect) {
                                HomeEffect.NavigateToQuiz -> currentRoute = Route.Quiz
                                HomeEffect.NavigateToLeaderboard -> currentRoute = Route.Leaderboard
                            }
                        }
                    }

                    HomeScreen(state = state, onIntent = vm::onIntent)
                }

                Route.Quiz -> {
                    val vm = remember { QuizViewModel(getRandomQuestions, questionSyncService) }
                    DisposableEffect(Unit) { onDispose { vm.onCleared() } }
                    val state by vm.state.collectAsState()

                    LaunchedEffect(Unit) {
                        vm.effect.collect { effect ->
                            when (effect) {
                                is QuizEffect.NavigateToResult -> {
                                    currentRoute = Route.Result(
                                        score = effect.score,
                                        total = effect.total,
                                    )
                                }
                                is QuizEffect.ShowSnackbar -> {
                                    showSnackbar(effect.message)
                                }
                                is QuizEffect.NoQuestionsAvailable -> {
                                    currentRoute = Route.Home
                                    showSnackbar("Połącz się z internetem, aby pobrać pytania")
                                }
                            }
                        }
                    }

                    QuizScreen(state = state, onIntent = vm::onIntent)
                }

                is Route.Result -> {
                    val vm = remember {
                        ResultViewModel(
                            score = route.score,
                            totalQuestions = route.total,
                            submitScore = submitScore,
                            uid = currentUid,
                            pendingScoreDataSource = pendingScoreDataSource,
                        )
                    }
                    DisposableEffect(Unit) { onDispose { vm.onCleared() } }
                    val state by vm.state.collectAsState()

                    LaunchedEffect(Unit) {
                        vm.effect.collect { effect ->
                            when (effect) {
                                ResultEffect.NavigateToQuiz -> currentRoute = Route.Quiz
                                ResultEffect.NavigateToLeaderboard -> currentRoute = Route.Leaderboard
                                is ResultEffect.ShowError -> {
                                    showSnackbar(effect.message)
                                }
                            }
                        }
                    }

                    ResultScreen(state = state, onIntent = vm::onIntent)
                }

                Route.Leaderboard -> {
                    val vm = remember {
                        LeaderboardViewModel(
                            leaderboardRepository = leaderboardRepository,
                            currentUid = currentUid,
                        )
                    }
                    DisposableEffect(Unit) { onDispose { vm.onCleared() } }
                    val state by vm.state.collectAsState()

                    LaunchedEffect(Unit) {
                        vm.effect.collect { effect ->
                            when (effect) {
                                LeaderboardEffect.NavigateBack -> currentRoute = Route.Home
                            }
                        }
                    }

                    LeaderboardScreen(state = state, onIntent = vm::onIntent)
                }
            }
        }
        }
    }
}
