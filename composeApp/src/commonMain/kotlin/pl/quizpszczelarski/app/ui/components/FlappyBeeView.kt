package pl.quizpszczelarski.app.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

private data class FlappyPipe(
    val x: Float,
    val gapTop: Float,
    val gapBottom: Float,
    val scored: Boolean = false,
)

@Composable
fun FlappyBeeView(
    onGameOver: (score: Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    var canvasSize by remember { mutableStateOf(IntSize.Zero) }
    var beeY by remember { mutableStateOf(0f) }
    var velocity by remember { mutableStateOf(0f) }
    var pipes by remember { mutableStateOf(listOf<FlappyPipe>()) }
    var score by remember { mutableStateOf(0) }
    var gameRunning by remember { mutableStateOf(false) }

    LaunchedEffect(gameRunning) {
        if (!gameRunning) return@LaunchedEffect
        while (canvasSize == IntSize.Zero) { delay(16) }

        val h = canvasSize.height.toFloat()
        val w = canvasSize.width.toFloat()
        val beeX = w * 0.25f
        val beeR = h * 0.04f
        val pipeW = w * 0.14f
        val pipeSpeed = w * 0.007f
        val gapH = h * 0.30f
        val gravity = h * 0.0007f
        val flapPower = -(h * 0.017f)
        val pipeEveryFrames = (w / pipeSpeed * 0.65f).toInt().coerceAtLeast(60)
        val rng = kotlin.random.Random.Default

        beeY = h / 2f
        velocity = 0f
        pipes = emptyList()
        score = 0

        var frame = 0
        while (true) {
            delay(16L)
            frame++

            velocity += gravity
            beeY += velocity

            if (frame % pipeEveryFrames == 0) {
                val gapTop = h * 0.12f + rng.nextFloat() * (h * 0.48f)
                pipes = pipes + FlappyPipe(
                    x = w + pipeW,
                    gapTop = gapTop,
                    gapBottom = gapTop + gapH,
                )
            }

            pipes = pipes
                .map { it.copy(x = it.x - pipeSpeed) }
                .filter { it.x + pipeW > 0f }

            pipes = pipes.map { pipe ->
                if (!pipe.scored && pipe.x + pipeW < beeX - beeR) {
                    score++
                    pipe.copy(scored = true)
                } else pipe
            }

            // Wall collision
            if (beeY - beeR < 0f || beeY + beeR > h) break

            // Pipe collision — AABB with 20% forgiveness on each edge
            val shrink = beeR * 0.2f
            val hit = pipes.any { pipe ->
                val overlapX = beeX + beeR - shrink > pipe.x && beeX - beeR + shrink < pipe.x + pipeW
                val inGap = beeY - beeR + shrink > pipe.gapTop && beeY + beeR - shrink < pipe.gapBottom
                overlapX && !inGap
            }
            if (hit) break
        }

        onGameOver(score)
    }

    Box(modifier = modifier.fillMaxSize()) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .onSizeChanged { size ->
                    canvasSize = size
                    if (beeY == 0f && size.height > 0) {
                        beeY = size.height / 2f
                    }
                }
                .pointerInput(Unit) {
                    detectTapGestures {
                        if (!gameRunning && canvasSize != IntSize.Zero) {
                            gameRunning = true
                        } else if (gameRunning) {
                            val h = canvasSize.height.toFloat()
                            velocity = -(h * 0.017f)
                        }
                    }
                },
        ) {
            val w = size.width
            val h = size.height
            val beeX = w * 0.25f
            val beeR = h * 0.04f
            val pipeW = w * 0.14f
            val groundH = h * 0.07f

            // Sky
            drawRect(color = Color(0xFFB3E5FC))

            // Pipes (honey amber)
            for (pipe in pipes) {
                drawRect(
                    color = Color(0xFFF9A825),
                    topLeft = Offset(pipe.x, 0f),
                    size = Size(pipeW, pipe.gapTop),
                )
                drawRect(
                    color = Color(0xFFE65100),
                    topLeft = Offset(pipe.x - pipeW * 0.1f, pipe.gapTop - h * 0.03f),
                    size = Size(pipeW * 1.2f, h * 0.03f),
                )
                drawRect(
                    color = Color(0xFFF9A825),
                    topLeft = Offset(pipe.x, pipe.gapBottom),
                    size = Size(pipeW, h - pipe.gapBottom),
                )
                drawRect(
                    color = Color(0xFFE65100),
                    topLeft = Offset(pipe.x - pipeW * 0.1f, pipe.gapBottom),
                    size = Size(pipeW * 1.2f, h * 0.03f),
                )
            }

            // Ground
            drawRect(
                color = Color(0xFF4CAF50),
                topLeft = Offset(0f, h - groundH),
                size = Size(w, groundH * 0.35f),
            )
            drawRect(
                color = Color(0xFF795548),
                topLeft = Offset(0f, h - groundH * 0.65f),
                size = Size(w, groundH * 0.65f),
            )

            // Bee
            if (beeY > 0f) {
                // Wings
                drawOval(
                    color = Color(0xCCE3F2FD),
                    topLeft = Offset(beeX - beeR * 1.0f, beeY - beeR * 1.5f),
                    size = Size(beeR * 0.9f, beeR * 0.85f),
                )
                drawOval(
                    color = Color(0xCCE3F2FD),
                    topLeft = Offset(beeX + beeR * 0.15f, beeY - beeR * 1.5f),
                    size = Size(beeR * 0.9f, beeR * 0.85f),
                )
                // Body
                drawCircle(
                    color = Color(0xFFFFD600),
                    radius = beeR,
                    center = Offset(beeX, beeY),
                )
                // Stripe
                drawRect(
                    color = Color(0xFF1A1A1A),
                    topLeft = Offset(beeX - beeR * 0.35f, beeY - beeR * 0.5f),
                    size = Size(beeR * 0.7f, beeR),
                )
                // Eye
                drawCircle(
                    color = Color(0xFF1A1A1A),
                    radius = beeR * 0.2f,
                    center = Offset(beeX + beeR * 0.5f, beeY - beeR * 0.25f),
                )
            }

            // Dark overlay before game starts
            if (!gameRunning) {
                drawRect(color = Color(0x55000000))
            }
        }

        // Live score
        if (gameRunning) {
            Text(
                text = "$score",
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 16.dp),
                style = MaterialTheme.typography.headlineLarge,
                color = Color.White,
            )
        }

        // Start hint
        if (!gameRunning) {
            Text(
                text = "Dotknij ekranu, aby zacząć!",
                modifier = Modifier.align(Alignment.Center),
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White,
            )
        }
    }
}
