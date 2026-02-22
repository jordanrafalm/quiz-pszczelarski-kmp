package pl.quizpszczelarski.app.platform

import platform.Foundation.NSBundle

actual fun getAppVersion(): String =
    NSBundle.mainBundle.infoDictionary
        ?.get("CFBundleShortVersionString") as? String
        ?: "0.0.0"

actual fun getStoreUrl(): String =
    "https://apps.apple.com/app/quiz-pszczelarski/id0000000000"
