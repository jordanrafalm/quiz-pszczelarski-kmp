package pl.quizpszczelarski.shared.data.config

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.remoteconfig.remoteConfig
import pl.quizpszczelarski.shared.domain.model.AppConfig
import pl.quizpszczelarski.shared.domain.repository.AppConfigRepository
import kotlin.time.Duration.Companion.hours

/**
 * Firebase Remote Config implementation of [AppConfigRepository].
 *
 * Firebase Console parameter keys:
 * - `force_update_required`    (Boolean, default: false)
 * - `force_update_min_version` (String,  default: "0.0.0")
 * - `new_questions_available`  (Boolean, default: false)
 * - `maintenance_mode`         (Boolean, default: false)
 *
 * Defaults are set in-app so the app works correctly before any fetch completes.
 */
class FirebaseAppConfigRepository : AppConfigRepository {

    /** In-memory cache, updated on every successful fetch. */
    private var cachedConfig = AppConfig()

    override suspend fun fetchConfig(): AppConfig {
        return try {
            val remoteConfig = Firebase.remoteConfig
            remoteConfig.settings {
                minimumFetchInterval = 1.hours
            }
            remoteConfig.setDefaults(
                KEY_FORCE_UPDATE_REQUIRED to false,
                KEY_FORCE_UPDATE_MIN_VERSION to "0.0.0",
                KEY_NEW_QUESTIONS_AVAILABLE to false,
                KEY_MAINTENANCE_MODE to false,
            )
            remoteConfig.fetchAndActivate()
            val config = AppConfig(
                forceUpdateRequired = remoteConfig.getValue(KEY_FORCE_UPDATE_REQUIRED).asBoolean(),
                forceUpdateMinVersion = remoteConfig.getValue(KEY_FORCE_UPDATE_MIN_VERSION).asString(),
                newQuestionsAvailable = remoteConfig.getValue(KEY_NEW_QUESTIONS_AVAILABLE).asBoolean(),
                maintenanceMode = remoteConfig.getValue(KEY_MAINTENANCE_MODE).asBoolean(),
            )
            cachedConfig = config
            config
        } catch (_: Exception) {
            getCachedConfig()
        }
    }

    override fun getCachedConfig(): AppConfig = cachedConfig

    companion object {
        private const val KEY_FORCE_UPDATE_REQUIRED = "force_update_required"
        private const val KEY_FORCE_UPDATE_MIN_VERSION = "force_update_min_version"
        private const val KEY_NEW_QUESTIONS_AVAILABLE = "new_questions_available"
        private const val KEY_MAINTENANCE_MODE = "maintenance_mode"
    }
}
