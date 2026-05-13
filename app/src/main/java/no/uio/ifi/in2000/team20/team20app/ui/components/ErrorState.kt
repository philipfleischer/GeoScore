package no.uio.ifi.in2000.team20.team20app.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import no.uio.ifi.in2000.team20.team20app.ui.theme.LocalTheme
import no.uio.ifi.in2000.team20.team20app.ui.theme.lightThemeColors

/*
 * Gjenbrukbar error-komponent.
 *
 * Viser et advarselsikon, en feilmelding og valgfri "prøv igjen"-knapp.
 * Brukes på tvers av skjermer i appen.
 */
@Composable
fun ErrorState(
    message: String,
    modifier: Modifier = Modifier,
    onRetry: (() -> Unit)? = null
) {
    val theme = LocalTheme.current

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Warning,
            contentDescription = null,
            tint = theme.error,
            modifier = Modifier.size(48.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge
        )
        if (onRetry != null) {
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onRetry,
                colors = ButtonDefaults.buttonColors(containerColor = theme.button)
            ) {
                Text("Prøv igjen")
            }
        }
    }
}

@Preview(showBackground = true, name = "ErrorState – lys, med retry")
@Composable
private fun ErrorStatePreviewLight() {
    CompositionLocalProvider(LocalTheme provides lightThemeColors) {
        MaterialTheme {
            ErrorState(
                message = "Kunne ikke laste inn data.",
                modifier = Modifier.fillMaxSize(),
                onRetry = {}
            )
        }
    }
}
