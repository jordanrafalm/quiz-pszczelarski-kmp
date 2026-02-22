package pl.quizpszczelarski.app.platform

import platform.UIKit.UIImpactFeedbackGenerator
import platform.UIKit.UIImpactFeedbackStyle
import platform.UIKit.UINotificationFeedbackGenerator
import platform.UIKit.UINotificationFeedbackType

/**
 * iOS haptic feedback using UIKit feedback generators.
 * Generators are cached per Apple's recommendation for better performance.
 */
class IosHaptics : Haptics {

    private val lightGenerator = UIImpactFeedbackGenerator(
        style = UIImpactFeedbackStyle.UIImpactFeedbackStyleLight,
    )
    private val mediumGenerator = UIImpactFeedbackGenerator(
        style = UIImpactFeedbackStyle.UIImpactFeedbackStyleMedium,
    )
    private val notificationGenerator = UINotificationFeedbackGenerator()

    override fun impact(type: ImpactType) {
        when (type) {
            ImpactType.Light -> {
                lightGenerator.prepare()
                lightGenerator.impactOccurred()
            }
            ImpactType.Medium -> {
                mediumGenerator.prepare()
                mediumGenerator.impactOccurred()
            }
            ImpactType.Success -> {
                notificationGenerator.prepare()
                notificationGenerator.notificationOccurred(
                    UINotificationFeedbackType.UINotificationFeedbackTypeSuccess,
                )
            }
            ImpactType.Error -> {
                notificationGenerator.prepare()
                notificationGenerator.notificationOccurred(
                    UINotificationFeedbackType.UINotificationFeedbackTypeError,
                )
            }
        }
    }
}
