package no.uio.ifi.in2000.team20.team20app.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import no.uio.ifi.in2000.team20.team20app.ui.theme.LocalTheme
import no.uio.ifi.in2000.team20.team20app.ui.theme.lightThemeColors

/*
 * Gjenbrukbar loading-komponent.
 *
 * Viser en sirkulær spinner og valgfri tekst mens data lastes inn.
 * Brukes på tvers av skjermer i appen.
 */
@Composable
fun LoadingState(
    modifier: Modifier = Modifier,
    message: List<String> = listOf("Laster data...")
) {
    val theme = LocalTheme.current
    var currentIndex by remember { mutableIntStateOf(0) }

    if (message.size > 1) {
        LaunchedEffect(message) {
            while (true) {
                for (i in message.indices) {
                    currentIndex = i
                    delay(900L)
                }
            }
        }
    }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator(color = theme.button)
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = message[currentIndex],
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Preview(showBackground = true, name = "LoadingState – lys")
@Composable
private fun LoadingStatePreviewLight() {
    CompositionLocalProvider(LocalTheme provides lightThemeColors) {
        MaterialTheme {
            LoadingState(modifier = Modifier.fillMaxSize())
        }
    }
}
