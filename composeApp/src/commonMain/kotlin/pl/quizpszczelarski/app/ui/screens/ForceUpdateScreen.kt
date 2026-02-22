package pl.quizpszczelarski.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import pl.quizpszczelarski.app.platform.getStoreUrl
import pl.quizpszczelarski.app.ui.components.AppButton
import pl.quizpszczelarski.app.ui.components.AppButtonVariant
import pl.quizpszczelarski.app.ui.theme.AppTheme

/**
 * Blocking force-update screen.
 *
 * Shown when Firebase Remote Config signals that the installed version is below the minimum
 * required version. Back navigation is disabled — the only action is opening the store.
 */
@Composable
fun ForceUpdateScreen(
    modifier: Modifier = Modifier,
) {
    val spacing = AppTheme.spacing
    val uriHandler = LocalUriHandler.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = spacing.xl, vertical = spacing.xxxl),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = "🐝",
            style = MaterialTheme.typography.displayMedium,
        )

        Spacer(modifier = Modifier.height(spacing.xl))

        Text(
            text = "Aktualizacja wymagana",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center,
        )

        Spacer(modifier = Modifier.height(spacing.md))

        Text(
            text = "Dostępna jest nowa wersja aplikacji. Zaktualizuj, aby kontynuować.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )

        Spacer(modifier = Modifier.height(spacing.xxl))

        AppButton(
            text = "Aktualizuj",
            onClick = { uriHandler.openUri(getStoreUrl()) },
            variant = AppButtonVariant.Primary,
        )
    }
}
