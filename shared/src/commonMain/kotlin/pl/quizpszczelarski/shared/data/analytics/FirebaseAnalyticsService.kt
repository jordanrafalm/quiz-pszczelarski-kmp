package pl.quizpszczelarski.shared.data.analytics

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.analytics.analytics
import dev.gitlive.firebase.crashlytics.crashlytics
import pl.quizpszczelarski.shared.domain.service.AnalyticsService

/**
 * Firebase-backed implementation of [AnalyticsService].
 * Uses GitLive KMP wrappers for Analytics + Crashlytics.
 */
class FirebaseAnalyticsService : AnalyticsService {

    private val analytics = Firebase.analytics
    private val crashlytics = Firebase.crashlytics

    override fun logQuizStarted(mode: String, level: String, questionCount: Int, quizRunIndex: Int) {
        analytics.logEvent("quiz_started", mapOf(
            "mode" to mode,
            "level" to level,
            "question_count" to questionCount,
            "quiz_run_index" to quizRunIndex,
        ))
        crashlytics.setCustomKey("quiz_mode", mode)
        crashlytics.setCustomKey("quiz_level", level)
    }

    override fun logQuizCompleted(
        mode: String,
        level: String,
        questionCount: Int,
        score: Int,
        durationMs: Long,
        quizRunIndex: Int,
    ) {
        analytics.logEvent("quiz_completed", mapOf(
            "mode" to mode,
            "level" to level,
            "question_count" to questionCount,
            "score" to score,
            "duration_ms" to durationMs,
            "quiz_run_index" to quizRunIndex,
        ))
    }

    override fun logQuizAbandoned(
        mode: String,
        level: String,
        questionCount: Int,
        questionsAnswered: Int,
        durationMs: Long,
        quizRunIndex: Int,
    ) {
        analytics.logEvent("quiz_abandoned", mapOf(
            "mode" to mode,
            "level" to level,
            "question_count" to questionCount,
            "questions_answered" to questionsAnswered,
            "duration_ms" to durationMs,
            "quiz_run_index" to quizRunIndex,
        ))
    }

    override fun logTermsClicked(sourceScreen: String) {
        analytics.logEvent("terms_clicked", mapOf(
            "source_screen" to sourceScreen,
        ))
    }

    override fun recordNonFatal(exception: Throwable, context: Map<String, String>) {
        context.forEach { (key, value) -> crashlytics.setCustomKey(key, value) }
        crashlytics.recordException(exception)
    }

    override fun setCustomKey(key: String, value: String) {
        crashlytics.setCustomKey(key, value)
    }

    override fun setUserId(uid: String) {
        crashlytics.setUserId(uid)
        analytics.setUserId(uid)
    }
}
