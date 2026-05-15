package no.uio.ifi.in2000.team20.team20app.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
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
 * Reusable empty state component.
 *
 * Displayed when no location is selected or no data exists for the area.
 * Used across screens in the app.
 */
@Composable
fun EmptyState(
    modifier: Modifier = Modifier,
    message: String = "Ingen lokasjon valgt"
) {
    val theme = LocalTheme.current

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.LocationOn,
            contentDescription = null,
            tint = theme.detail,
            modifier = Modifier.size(48.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Preview(showBackground = true, name = "EmptyState – lys")
@Composable
private fun EmptyStatePreviewLight() {
    CompositionLocalProvider(LocalTheme provides lightThemeColors) {
        MaterialTheme {
            EmptyState(modifier = Modifier.fillMaxSize())
        }
    }
}
