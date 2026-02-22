package pl.quizpszczelarski.shared.domain.service

/**
 * Analytics + crash reporting abstraction.
 * No Firebase imports — pure Kotlin interface.
 */
interface AnalyticsService {
    fun logQuizStarted(mode: String, level: String, questionCount: Int, quizRunIndex: Int)
    fun logQuizCompleted(mode: String, level: String, questionCount: Int, score: Int, durationMs: Long, quizRunIndex: Int)
    fun logQuizAbandoned(mode: String, level: String, questionCount: Int, questionsAnswered: Int, durationMs: Long, quizRunIndex: Int)
    fun logTermsClicked(sourceScreen: String)
    fun recordNonFatal(exception: Throwable, context: Map<String, String> = emptyMap())
    fun setCustomKey(key: String, value: String)
    fun setUserId(uid: String)
}
