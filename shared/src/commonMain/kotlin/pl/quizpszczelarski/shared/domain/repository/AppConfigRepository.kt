package pl.quizpszczelarski.shared.domain.repository

import pl.quizpszczelarski.shared.domain.model.AppConfig

/**
 * Repository for fetching and caching Firebase Remote Config values.
 */
interface AppConfigRepository {
    /**
     * Fetch fresh config from Firebase and activate it.
     * Falls back to cached/default values on error.
     */
    suspend fun fetchConfig(): AppConfig

    /**
     * Returns the last successfully activated config, or default values if no fetch has run.
     * Non-suspending — safe to call synchronously from any context.
     */
    fun getCachedConfig(): AppConfig
}
