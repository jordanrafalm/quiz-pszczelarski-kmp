package pl.quizpszczelarski.app.platform

/** Returns the current app version string (e.g. "0.1.0"). */
expect fun getAppVersion(): String

/** Returns the platform-specific store URL for the update button. */
expect fun getStoreUrl(): String
