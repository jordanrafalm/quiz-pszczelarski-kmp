package pl.quizpszczelarski.app.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import dev.gitlive.firebase.firestore.firestore
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import pl.quizpszczelarski.app.platform.LocalHaptics
import pl.quizpszczelarski.app.platform.LocalSettingsState
import pl.quizpszczelarski.app.platform.LocalSoundPlayer
import pl.quizpszczelarski.app.platform.SplashSoundPlayer
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
import pl.quizpszczelarski.shared.data.settings.SettingsFactory
import pl.quizpszczelarski.shared.data.settings.SettingsRepositoryImpl
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
fun AppNavigation(driverFactory: DatabaseDriverFactory, settingsFactory: SettingsFactory, splashSoundPlayer: SplashSoundPlayer? = null) {
    // Custom Saver for Route to survive configuration changes
    val routeSaver = Saver<Route, List<Any>>(
        save = { route ->
            when (route) {
                is Route.Splash -> listOf("Splash")
                is Route.Home -> listOf("Home")
                is Route.Quiz -> listOf("Quiz", route.level, route.questionCount)
                is Route.Result -> listOf("Result", route.score, route.total)
                is Route.Leaderboard -> listOf("Leaderboard")
            }
        },
        restore = { list ->
            when (list[0] as String) {
                "Splash" -> Route.Splash
                "Home" -> Route.Home
                "Quiz" -> Route.Quiz(list[1] as String, list[2] as Int)
                "Result" -> Route.Result(list[1] as Int, list[2] as Int)
                "Leaderboard" -> Route.Leaderboard
                else -> Route.Home // fallback
            }
        }
    )

    var currentRoute by rememberSaveable(stateSaver = routeSaver) { mutableStateOf<Route>(Route.Splash) }

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

    // Settings
    val settingsRepo = remember { SettingsRepositoryImpl(settingsFactory.create()) }
    val settingsState by settingsRepo.getSettingsFlow()
        .collectAsState(initial = settingsRepo.getSettings())

    val haptics = LocalHaptics.current

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

    CompositionLocalProvider(
        LocalSettingsState provides settingsState,
        LocalSoundPlayer provides splashSoundPlayer,
    ) {
        // Handle system back button/gesture
        BackHandler(enabled = currentRoute !is Route.Splash && currentRoute !is Route.Home) {
            when (currentRoute) {
                is Route.Quiz -> currentRoute = Route.Home
                is Route.Result -> currentRoute = Route.Home
                is Route.Leaderboard -> currentRoute = Route.Home
                else -> { /* Do nothing, let system handle */ }
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
                    .padding(innerPadding),
            ) {
                AnimatedContent(
                    targetState = currentRoute,
                    transitionSpec = {
                        when {
                            initialState is Route.Splash -> AppTransitions.splashTransition()
                            targetState is Route.Home -> AppTransitions.screenTransitionBack()
                            else -> AppTransitions.screenTransition()
                        }
                    },
                    label = "ScreenTransition",
                ) { route ->
                    when (route) {
                        Route.Splash -> {
                            SplashScreen()
                            LaunchedEffect(Unit) {
                                // Play bee sound on splash (only if sound enabled)
                                if (settingsState.soundEnabled) {
                                    splashSoundPlayer?.let { player ->
                                        try {
                                            val soundBytes = quiz_pszczelarski_kmp.composeapp.generated.resources.Res.readBytes("files/bee_sound.mp3")
                                            player.play(soundBytes)
                                        } catch (_: Exception) { }
                                    }
                                }

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
                                // Ensure splash shows for at least 3s, but don't block forever
                                delay(3000L)
                                // Give auth and sync a few more seconds, then proceed regardless
                                withTimeoutOrNull(4000L) { bootstrapJob.await() }
                                withTimeoutOrNull(2000L) { syncJob.await() }

                                // Release sound player
                                splashSoundPlayer?.release()

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
                                        is HomeEffect.NavigateToQuiz -> {
                                            currentRoute = Route.Quiz(
                                                level = effect.level,
                                                questionCount = effect.questionCount,
                                            )
                                        }
                                        HomeEffect.NavigateToLeaderboard -> {
                                            currentRoute = Route.Leaderboard
                                        }
                                        is HomeEffect.PlayHaptic -> {
                                            if (settingsState.hapticsEnabled) {
                                                haptics.impact(effect.type)
                                            }
                                        }
                                        HomeEffect.ToggleHaptics -> {
                                            coroutineScope.launch {
                                                settingsRepo.setHapticsEnabled(!settingsState.hapticsEnabled)
                                            }
                                        }
                                        HomeEffect.ToggleSound -> {
                                            coroutineScope.launch {
                                                settingsRepo.setSoundEnabled(!settingsState.soundEnabled)
                                            }
                                        }
                                    }
                                }
                            }

                            HomeScreen(state = state, onIntent = vm::onIntent)
                        }

                        is Route.Quiz -> {
                            val vm = remember { QuizViewModel(getRandomQuestions, questionSyncService, route.level, route.questionCount) }
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
                                        is QuizEffect.PlayHaptic -> {
                                            if (settingsState.hapticsEnabled) {
                                                haptics.impact(effect.type)
                                            }
                                        }
                                        is QuizEffect.NavigateToHome -> {
                                            currentRoute = Route.Home
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
                                    userRepository = userRepository,
                                    settingsRepository = settingsRepo,
                                )
                            }
                            DisposableEffect(Unit) { onDispose { vm.onCleared() } }
                            val state by vm.state.collectAsState()

                            LaunchedEffect(Unit) {
                                vm.effect.collect { effect ->
                                    when (effect) {
                                        ResultEffect.NavigateToHome -> currentRoute = Route.Home
                                        ResultEffect.NavigateToLeaderboard -> currentRoute = Route.Leaderboard
                                        is ResultEffect.ShowError -> {
                                            showSnackbar(effect.message)
                                        }
                                        is ResultEffect.PlayHaptic -> {
                                            if (settingsState.hapticsEnabled) {
                                                haptics.impact(effect.type)
                                            }
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
                                    userRepository = userRepository,
                                )
                            }
                            DisposableEffect(Unit) { onDispose { vm.onCleared() } }
                            val state by vm.state.collectAsState()

                            LaunchedEffect(Unit) {
                                vm.effect.collect { effect ->
                                    when (effect) {
                                        LeaderboardEffect.NavigateBack -> currentRoute = Route.Home
                                        is LeaderboardEffect.ShowSnackbar -> showSnackbar(effect.message)
                                    }
                                }
                            }

                            LeaderboardScreen(state = state, onIntent = vm::onIntent)
                        }
                    }
                }
            }
        }
    } // CompositionLocalProvider
}
