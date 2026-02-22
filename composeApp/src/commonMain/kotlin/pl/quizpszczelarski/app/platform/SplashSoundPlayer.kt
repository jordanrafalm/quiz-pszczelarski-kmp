package pl.quizpszczelarski.app.platform

/**
 * One-shot sound player for the splash screen.
 * Each platform implements this using native audio APIs.
 */
interface SplashSoundPlayer {
    /** Play sound from the given resource bytes. */
    fun play(resourceBytes: ByteArray)
    /** Release audio resources after splash finishes. */
    fun release()
}
