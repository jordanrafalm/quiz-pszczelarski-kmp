package pl.quizpszczelarski.shared.domain.model

/**
 * App-wide dynamic config fetched from Firebase Remote Config.
 * All fields have safe defaults — the app works normally when Remote Config is unavailable.
 */
data class AppConfig(
    /** When true AND current version < [forceUpdateMinVersion], block app with ForceUpdate screen. */
    val forceUpdateRequired: Boolean = false,
    /** Minimum version string that must be installed to pass the force-update check (e.g. "1.2.0"). */
    val forceUpdateMinVersion: String = "0.0.0",
    /** When true, shows a "🆕 Nowe pytania!" badge on the Quiz action card. */
    val newQuestionsAvailable: Boolean = false,
    /** When true, app is in maintenance — can be used for future maintenance screen. */
    val maintenanceMode: Boolean = false,
)
