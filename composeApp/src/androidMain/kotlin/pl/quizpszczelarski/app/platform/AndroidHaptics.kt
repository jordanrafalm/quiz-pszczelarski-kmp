package pl.quizpszczelarski.app.platform

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

/**
 * Android haptic feedback using Vibrator API.
 * API 26+ (VibrationEffect). Our minSdk is 26, so no compat fallback needed.
 */
class AndroidHaptics(context: Context) : Haptics {

    private val vibrator: Vibrator = if (Build.VERSION.SDK_INT >= 31) {
        val mgr = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
        mgr.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    }

    override fun impact(type: ImpactType) {
        if (!vibrator.hasVibrator()) return

        val effect = when (type) {
            ImpactType.Light -> VibrationEffect.createOneShot(30, 80)
            ImpactType.Medium -> VibrationEffect.createOneShot(50, 150)
            ImpactType.Success -> VibrationEffect.createOneShot(60, 200)
            ImpactType.Error -> VibrationEffect.createWaveform(
                longArrayOf(0, 40, 30, 40),
                intArrayOf(0, 200, 0, 200),
                -1,
            )
        }
        vibrator.vibrate(effect)
    }
}
