package pl.quizpszczelarski.app.presentation.leaderboard

import kotlinx.coroutines.launch
import pl.quizpszczelarski.app.presentation.base.MviViewModel
import pl.quizpszczelarski.shared.domain.model.LeaderboardEntry
import pl.quizpszczelarski.shared.domain.repository.LeaderboardRepository
import pl.quizpszczelarski.shared.domain.repository.UserRepository
import pl.quizpszczelarski.shared.domain.service.AnalyticsService

/**
 * ViewModel for the Leaderboard screen.
 * Observes real-time leaderboard data from Firestore.
 */
class LeaderboardViewModel(
    private val leaderboardRepository: LeaderboardRepository,
    private val currentUid: String?,
    private val userRepository: UserRepository,
    private val analyticsService: AnalyticsService,
) : MviViewModel<LeaderboardState, LeaderboardIntent, LeaderboardEffect>(
    LeaderboardState(isLoading = true),
) {

    init {
        observeLeaderboard()
    }

    private fun observeLeaderboard() {
        scope.launch {
            try {
                leaderboardRepository.observeTopUsers(
                    limit = 50,
                    currentUid = currentUid ?: "",
                )
                    .collect { entries ->
                        val userEntry = entries.firstOrNull { it.isCurrentUser }
                        onIntent(
                            LoadEntries(
                                entries = entries,
                                userRank = userEntry?.rank ?: 0,
                                userScore = userEntry?.score ?: 0,
                            ),
                        )
                    }
            } catch (_: Exception) {
                analyticsService.recordNonFatal(
                    Exception("Failed to load leaderboard"),
                    mapOf("context" to "observe_leaderboard"),
                )
                onIntent(
                    ShowLoadError("Nie udało się załadować rankingu"),
                )
            }
        }
    }

    override fun reduce(state: LeaderboardState, intent: LeaderboardIntent): LeaderboardState {
        return when (intent) {
            is LeaderboardIntent.SelectTab -> state.copy(selectedTabIndex = intent.index)
            is LeaderboardIntent.GoBack -> {
                emitEffect(LeaderboardEffect.NavigateBack)
                state
            }
            is LeaderboardIntent.StartEditNickname -> state.copy(isEditingNickname = true)
            is LeaderboardIntent.UpdateNicknameInput -> state.copy(nicknameInput = intent.text)
            is LeaderboardIntent.CancelEditNickname -> state.copy(isEditingNickname = false, nicknameInput = "")
            is LeaderboardIntent.ConfirmNickname -> {
                val nick = state.nicknameInput.trim()
                if (nick.isNotEmpty() && currentUid != null) {
                    scope.launch {
                        try {
                            userRepository.updateNickname(currentUid, nick)
                            emitEffect(LeaderboardEffect.ShowSnackbar("Nick zmieniony!"))
                        } catch (_: Exception) {
                            emitEffect(LeaderboardEffect.ShowSnackbar("Nie udało się zmienić nicku"))
                        }
                    }
                }
                state.copy(isEditingNickname = false, nicknameInput = "")
            }
            is LoadEntries -> state.copy(
                entries = intent.entries,
                userRank = intent.userRank,
                userScore = intent.userScore,
                isLoading = false,
                errorMessage = null,
            )

            is ShowLoadError -> state.copy(
                isLoading = false,
                errorMessage = intent.message,
            )
        }
    }
}

/** Internal intent: leaderboard entries loaded. */
internal data class LoadEntries(
    val entries: List<LeaderboardEntry>,
    val userRank: Int,
    val userScore: Int,
) : LeaderboardIntent

/** Internal intent: leaderboard loading failed. */
internal data class ShowLoadError(val message: String) : LeaderboardIntent
