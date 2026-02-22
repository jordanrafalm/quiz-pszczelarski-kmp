package pl.quizpszczelarski.app.platform

import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.allocArrayOf
import kotlinx.cinterop.memScoped
import platform.AVFAudio.AVAudioPlayer
import platform.Foundation.NSData
import platform.Foundation.create
import platform.posix.memcpy

/**
 * iOS implementation of [SplashSoundPlayer] using [AVAudioPlayer].
 */
class IosSplashSoundPlayer : SplashSoundPlayer {

    private var audioPlayer: AVAudioPlayer? = null

    @OptIn(ExperimentalForeignApi::class)
    override fun play(resourceBytes: ByteArray) {
        try {
            val nsData = resourceBytes.toNSData()
            audioPlayer = AVAudioPlayer(data = nsData, error = null)
            audioPlayer?.prepareToPlay()
            audioPlayer?.play()
        } catch (_: Exception) {
            // Don't block app startup if sound fails
        }
    }

    override fun release() {
        audioPlayer?.stop()
        audioPlayer = null
    }

    @OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
    private fun ByteArray.toNSData(): NSData = memScoped {
        NSData.create(
            bytes = allocArrayOf(this@toNSData),
            length = this@toNSData.size.toULong(),
        )
    }
}
