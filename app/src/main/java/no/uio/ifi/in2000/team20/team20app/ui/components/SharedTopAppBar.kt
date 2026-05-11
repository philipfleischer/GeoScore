package no.uio.ifi.in2000.team20.team20app.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SharedTopAppBar(
    title: String,
    onBackClick: (() -> Unit)? = null,
    onOpenSettings: (() -> Unit)? = null,
) {
    CenterAlignedTopAppBar(
        title = { Text(title) },
        navigationIcon = {
            if (onBackClick != null) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Gå tilbake"
                    )
                }
            }
        },
        actions = {
            if (onOpenSettings != null) {
                IconButton(onClick = onOpenSettings) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Åpne innstillinger"
                    )
                }
            }
        }
    )
}