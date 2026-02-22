package pl.quizpszczelarski.app.platform

import android.media.MediaPlayer
import java.io.File
import java.io.FileOutputStream

/**
 * Android implementation of [SplashSoundPlayer] using [MediaPlayer].
 */
class AndroidSplashSoundPlayer(private val cacheDir: File) : SplashSoundPlayer {

    private var mediaPlayer: MediaPlayer? = null

    override fun play(resourceBytes: ByteArray) {
        try {
            val tempFile = File(cacheDir, "splash_sound.mp3")
            FileOutputStream(tempFile).use { it.write(resourceBytes) }

            mediaPlayer = MediaPlayer().apply {
                setDataSource(tempFile.absolutePath)
                prepare()
                start()
            }
        } catch (_: Exception) {
            // Don't block app startup if sound fails
        }
    }

    override fun release() {
        mediaPlayer?.release()
        mediaPlayer = null
        // Clean up temp file
        File(cacheDir, "splash_sound.mp3").delete()
    }
}
