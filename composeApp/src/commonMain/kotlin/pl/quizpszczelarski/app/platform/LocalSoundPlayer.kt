package pl.quizpszczelarski.app.platform

import androidx.compose.runtime.compositionLocalOf

/**
 * CompositionLocal providing a [SplashSoundPlayer] for playing one-shot sounds.
 */
val LocalSoundPlayer = compositionLocalOf<SplashSoundPlayer?> { null }
