package pl.quizpszczelarski.app.navigation

import androidx.compose.animation.ContentTransform
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith

/**
 * Centralized transition specs for AnimatedContent screen switching.
 * iOS-smooth: gentle fade + slight slide, short duration.
 */
object AppTransitions {

    private const val DURATION_MS = 300

    /**
     * Default screen transition: fade + slide from bottom (matches level-select style).
     */
    fun screenTransition(): ContentTransform {
        val enterTransition = fadeIn(
            animationSpec = tween(durationMillis = DURATION_MS),
        ) + slideInVertically(
            initialOffsetY = { it / 4 },
            animationSpec = tween(durationMillis = DURATION_MS),
        )

        val exitTransition = fadeOut(
            animationSpec = tween(durationMillis = DURATION_MS / 2),
        ) + slideOutVertically(
            targetOffsetY = { -it / 4 },
            animationSpec = tween(durationMillis = DURATION_MS / 2),
        )

        return enterTransition togetherWith exitTransition
    }

    /**
     * Backward navigation: slide from top (reverse of forward).
     */
    fun screenTransitionBack(): ContentTransform {
        val enterTransition = fadeIn(
            animationSpec = tween(durationMillis = DURATION_MS),
        ) + slideInVertically(
            initialOffsetY = { -it / 4 },
            animationSpec = tween(durationMillis = DURATION_MS),
        )

        val exitTransition = fadeOut(
            animationSpec = tween(durationMillis = DURATION_MS / 2),
        ) + slideOutVertically(
            targetOffsetY = { it / 4 },
            animationSpec = tween(durationMillis = DURATION_MS / 2),
        )

        return enterTransition togetherWith exitTransition
    }

    /**
     * Splash -> Home: simple crossfade (no slide).
     */
    fun splashTransition(): ContentTransform {
        return fadeIn(
            animationSpec = tween(durationMillis = 500),
        ) togetherWith fadeOut(
            animationSpec = tween(durationMillis = 300),
        )
    }
}
