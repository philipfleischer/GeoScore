package no.uio.ifi.in2000.team20.team20app.ui.screens.saved

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import no.uio.ifi.in2000.team20.team20app.domain.model.Location
import no.uio.ifi.in2000.team20.team20app.ui.sharedViewModels.AppViewModel

@Composable
fun SavedScreen(
    modifier: Modifier = Modifier,
    sharedViewModel: AppViewModel,
    savedViewModel: SavedViewModel,
    onSavedClick: (Location) -> Unit
) {
    val saved by savedViewModel.saved.collectAsStateWithLifecycle()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (saved.isEmpty()) {
            Text(
                text = "Du har ingen lagrede steder enda.",
                style = MaterialTheme.typography.bodyMedium
            )
        } else {
            Text(
                text = "Lagrede steder",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold
            )

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(saved) { area ->
                    SavedLocationCard(
                        location = area,
                        savedList = saved,
                        onOpenReport = {
                            sharedViewModel.setSelectedArea(area)
                            onSavedClick(area)
                        },
                        onSavedToggle = { isSaved ->
                            //TODO make unsaving a location less sudden. give some visual feedback
                            if (isSaved) {
                                savedViewModel.removeSaved(area)
                            } else {
                                savedViewModel.addSaved(area)
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun SavedLocationCard(
    location: Location,
    savedList: List<Location>,
    onOpenReport: () -> Unit,
    onSavedToggle: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            GradeBadge(location = location, savedList = savedList)

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = location.address,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )

                Text(
                    text = "Sist oppdatert: 02.05.2026",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Row(
                modifier = Modifier
                    .align(Alignment.Top)
                    .padding(top = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(0.dp)
            ) {
                IconButton(
                    onClick = { },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Del",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                }

                IconButton(
                    onClick = { },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Download,
                        contentDescription = "Last ned",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                }

                IconButton(
                    onClick = { onSavedToggle(true) },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Bookmark,
                        contentDescription = "Fjern fra lagret",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.End
        ) {
            Button(
                onClick = onOpenReport,
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Åpne rapport")
            }
        }
    }
}

//TODO dont hardcode this, should obviously be based on the actual algorithm but fine for brukertest
@Composable
private fun GradeBadge(location: Location, savedList: List<Location>) {
    val gradeList = listOf('A', 'B', 'C', 'D', 'E', 'F', 'G')
    val index = savedList.indexOf(location)
    val grade = if (index >= 0) gradeList[index % 7] else 'A'

    val gradeColor = when (grade) {
        'A' -> Color(0xFF4CAF50)
        'B' -> Color(0xFF8BC34A)
        'C' -> Color(0xFFFFC107)
        'D' -> Color(0xFFFFC56B)
        'E' -> Color(0xFFEFA066)
        'F' -> Color(0xFFE36C5C)
        'G' -> Color(0xFFB64545)
        else -> Color(0xFFBDBDBD)
    }

    Text(
        text = grade.toString(),
        style = MaterialTheme.typography.headlineSmall,
        fontWeight = FontWeight.Bold,
        color = gradeColor,
        fontSize = 28.sp
    )
}
