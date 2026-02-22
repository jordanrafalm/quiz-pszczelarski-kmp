package pl.quizpszczelarski.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.Image
import io.github.alexzhirkevich.compottie.Compottie
import io.github.alexzhirkevich.compottie.LottieCompositionSpec
import io.github.alexzhirkevich.compottie.rememberLottieComposition
import io.github.alexzhirkevich.compottie.rememberLottiePainter
import pl.quizpszczelarski.app.ui.theme.AppTheme

/**
 * Splash screen — Lottie bee animation, title, subtitle. Auto-navigates after 3s.
 */
@Composable
fun SplashScreen(
    modifier: Modifier = Modifier,
) {
    val spacing = AppTheme.spacing

    // Lottie composition from compose resources
    val composition by rememberLottieComposition {
        val jsonBytes = quiz_pszczelarski_kmp.composeapp.generated.resources.Res.readBytes("files/bee_animation.json")
        LottieCompositionSpec.JsonString(jsonBytes.decodeToString())
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // Bee Lottie animation — wider to create edge effect
        Image(
            painter = rememberLottiePainter(
                composition = composition,
                iterations = Compottie.IterateForever,
            ),
            contentDescription = "Bee animation",
            modifier = Modifier
                .fillMaxWidth(1.5f)
                .height(400.dp),
        )

        Spacer(modifier = Modifier.height(spacing.xl))

        Text(
            text = "Quiz Pszczelarski",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center,
        )

        Spacer(modifier = Modifier.height(spacing.sm))

        Text(
            text = "Sprawdź swoją wiedzę o pszczołach",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
    }
}
