package pl.quizpszczelarski.shared.data.gameofday

import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn

/**
 * Repository for persisting Game of Day results.
 * Uses MultiplatformSettings for date-keyed storage.
 */
interface GameOfDayRepository {
    fun saveGameResult(score: Int)
    fun getLastScore(): Int
    fun isCompletedToday(): Boolean
}

/**
 * Implementation using MultiplatformSettings.
 */
class GameOfDayRepositoryImpl(
    private val settings: com.russhwolf.settings.Settings,
) : GameOfDayRepository {
    companion object {
        private fun todayKey(): String {
            val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
            return "gra_dnia_${today}"
        }
    }

    override fun saveGameResult(score: Int) {
        val key = todayKey()
        settings.putInt("score_$key", score)
    }

    override fun getLastScore(): Int {
        val key = todayKey()
        return settings.getInt("score_$key", 0)
    }

    override fun isCompletedToday(): Boolean {
        val key = todayKey()
        return settings.getInt("score_$key", -1) >= 0
    }
}
