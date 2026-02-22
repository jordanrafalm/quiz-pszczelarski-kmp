package pl.quizpszczelarski.app.platform

import pl.quizpszczelarski.app.BuildConfig

actual fun getAppVersion(): String = BuildConfig.VERSION_NAME

actual fun getStoreUrl(): String =
    "https://play.google.com/store/apps/details?id=pl.quizpszczelarski.app"
