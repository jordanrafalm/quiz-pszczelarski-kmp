package pl.quizpszczelarski.app.navigation

/**
 * Sealed route definitions for the app navigation graph.
 */
sealed interface Route {
    data object Splash : Route
    data object Home : Route
    data class Quiz(val level: String, val questionCount: Int = 5) : Route
    data class Result(val score: Int, val total: Int) : Route
    data object Leaderboard : Route
    /** Blocking force-update screen — shown when Remote Config requires a newer app version. */
    data object ForceUpdate : Route
}
