package pl.quizpszczelarski.app.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import io.github.alexzhirkevich.compottie.LottieCompositionSpec
import io.github.alexzhirkevich.compottie.rememberLottieComposition
import io.github.alexzhirkevich.compottie.rememberLottiePainter
import pl.quizpszczelarski.app.ui.theme.AppTheme
import kotlin.math.PI
import kotlin.math.sin

@Composable
fun BonusOverlay(
    onAnimationEnd: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val bonusComposition by rememberLottieComposition {
        val jsonBytes = quiz_pszczelarski_kmp.composeapp.generated.resources.Res.readBytes("files/bonus.json")
        LottieCompositionSpec.JsonString(jsonBytes.decodeToString())
    }

    BoxWithConstraints(
        modifier = modifier.fillMaxSize(),
    ) {
        val screenHeightPx = constraints.maxHeight.toFloat()
        val screenWidthPx = constraints.maxWidth.toFloat()
        // Single progress drives both Y (linear up) and X (sinusoidal wave)
        val progress = remember { Animatable(0f) }

        LaunchedEffect(Unit) {
            progress.animateTo(
                targetValue = 1f,
                animationSpec = tween(
                    durationMillis = 4500,
                    easing = LinearEasing,
                ),
            )
            onAnimationEnd()
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset {
                    val p = progress.value
                    // Y: start below screen (screenHeightPx), exit above (-screenHeightPx)
                    val offsetY = screenHeightPx * (1f - 2f * p)
                    // X: 3 sinusoidal waves, amplitude ~22% of screen width
                    val waveAmplitude = screenWidthPx * 0.22f
                    val offsetX = sin(p * 3.0 * 2.0 * PI).toFloat() * waveAmplitude
                    IntOffset(offsetX.toInt(), offsetY.toInt())
                },
        ) {
            Image(
                painter = rememberLottiePainter(
                    composition = bonusComposition,
                    iterations = 3,
                ),
                contentDescription = "Bonus bee animation",
                modifier = Modifier.size(160.dp),
            )

            Spacer(modifier = Modifier.height(AppTheme.spacing.sm))

            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                ),
                shape = MaterialTheme.shapes.extraLarge,
            ) {
                Text(
                    text = "ALE ŻĄDLISZ WIEDZĄ!",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.ExtraBold,
                    ),
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(
                        horizontal = AppTheme.spacing.xl,
                        vertical = AppTheme.spacing.sm,
                    ),
                )
            }
        }
    }
}
