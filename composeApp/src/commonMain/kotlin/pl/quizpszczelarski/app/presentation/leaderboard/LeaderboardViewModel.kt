package pl.quizpszczelarski.app.presentation.leaderboard

import pl.quizpszczelarski.app.presentation.base.MviViewModel
import pl.quizpszczelarski.shared.domain.model.LeaderboardEntry

/**
 * ViewModel for the Leaderboard screen.
 * Uses hardcoded sample data for MVP.
 */
class LeaderboardViewModel : MviViewModel<LeaderboardState, LeaderboardIntent, LeaderboardEffect>(
    LeaderboardState(),
) {

    init {
        loadSampleData()
    }

    private fun loadSampleData() {
        val entries = listOf(
            LeaderboardEntry(rank = 1, name = "Pszczółka Maja", score = 95),
            LeaderboardEntry(rank = 2, name = "Bartnik Jan", score = 88),
            LeaderboardEntry(rank = 3, name = "ApisMaster", score = 82),
            LeaderboardEntry(rank = 4, name = "HoneyBee22", score = 76),
            LeaderboardEntry(rank = 5, name = "Ty", score = 60, isCurrentUser = true),
            LeaderboardEntry(rank = 6, name = "BeeKeeper", score = 55),
            LeaderboardEntry(rank = 7, name = "Kwiatowa", score = 48),
        )
        val currentUser = entries.first { it.isCurrentUser }
        onIntent(LoadEntries(entries, currentUser.rank, currentUser.score))
    }

    override fun reduce(state: LeaderboardState, intent: LeaderboardIntent): LeaderboardState {
        return when (intent) {
            is LeaderboardIntent.SelectTab -> state.copy(selectedTabIndex = intent.index)
            is LeaderboardIntent.GoBack -> {
                emitEffect(LeaderboardEffect.NavigateBack)
                state
            }
            is LoadEntries -> state.copy(
                entries = intent.entries,
                userRank = intent.userRank,
                userScore = intent.userScore,
            )
        }
    }
}

/**
 * Internal intent used only by ViewModel to load leaderboard entries.
 */
internal data class LoadEntries(
    val entries: List<LeaderboardEntry>,
    val userRank: Int,
    val userScore: Int,
) : LeaderboardIntent
