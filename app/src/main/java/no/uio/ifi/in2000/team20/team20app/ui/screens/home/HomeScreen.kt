package no.uio.ifi.in2000.team20.team20app.ui.screens.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.window.core.layout.WindowSizeClass
import no.uio.ifi.in2000.team20.team20app.ui.screens.search.SearchBarObject
import no.uio.ifi.in2000.team20.team20app.ui.screens.saved.SavedViewModel
import no.uio.ifi.in2000.team20.team20app.ui.sharedViewModels.AppViewModel
import no.uio.ifi.in2000.team20.team20app.ui.sharedViewModels.FrostViewModel
import no.uio.ifi.in2000.team20.team20app.util.LocalWindowSizeClass
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.traversalIndex
import no.uio.ifi.in2000.team20.team20app.ui.theme.LocalTheme
import no.uio.ifi.in2000.team20.team20app.util.Constants.DEFAULT_PADDING_DP
import no.uio.ifi.in2000.team20.team20app.util.Constants.LARGE_PADDING_DP
import no.uio.ifi.in2000.team20.team20app.util.Constants.MEDIUM_SCREEN_WIDTH

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    onOpenSearch: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel(),
    sharedViewModel: AppViewModel = hiltViewModel(),
    savedViewModel: SavedViewModel,
    frostViewModel: FrostViewModel,
    theme: MaterialTheme = MaterialTheme,
) {
    val location by sharedViewModel.selectedLocation.collectAsStateWithLifecycle()

    // Calculates window width and returns true if the size width class is compact, and false for everything else.
    val compactScreenWidth = !LocalWindowSizeClass.current.isWidthAtLeastBreakpoint(MEDIUM_SCREEN_WIDTH)

    LaunchedEffect(location) {
        if(location != null) {
            frostViewModel.loadFrostStats(location!!)
            savedViewModel.checkIfSaved(location!!)
        }
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(if(compactScreenWidth) 1 else 2),
        modifier = modifier
            .fillMaxSize()
        ,
        // Orginal:
        // contentPadding = PaddingValues(horizontal = 32.dp, vertical = 32.dp),
        // Attemt to get the text slightly lower in portrait:
        contentPadding = PaddingValues(
            start = (DEFAULT_PADDING_DP*2).dp,
            end = if (compactScreenWidth) (DEFAULT_PADDING_DP*2).dp else (DEFAULT_PADDING_DP*5).dp,
            top = (DEFAULT_PADDING_DP*5).dp,
            bottom = (DEFAULT_PADDING_DP*2).dp,
        ),
        verticalArrangement = Arrangement.spacedBy(25.dp),
        horizontalArrangement = Arrangement.spacedBy(25.dp)
    ) {
        //TODO: Update InfoBox component (rename to WelcomeInfoBox) so it fit current design
        //TODO: Remove hardcoded info box section and replace with updated InfoBox component
        item {
            //Column for header and infotext
            Column(
                verticalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier
                    .fillMaxWidth(if (compactScreenWidth) 1f else 0.5f)
                    .semantics{
                        isTraversalGroup = !compactScreenWidth
                    }
            ){
                Text(
                    text = "Vit hva du kjøper - før du kjøper det",
                    fontSize = 40.sp,
                    lineHeight = 48.sp,
                    color = theme.colorScheme.secondary,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.semantics{ heading() }
                )
                Text(
                    text = "Få innsikt i grunnforhold og naturfare. Søk opp en adresse og få en risikovurdering " +
                            "basert på geologisk og meterologisk data.",
                    style = MaterialTheme.typography.bodyMedium,
                    fontSize = 20.sp,
                    color = theme.colorScheme.onSurface
                )
            }
        }
        item {
            SearchBarObject(onOpenSearch = onOpenSearch)
        }
    }
}

@Composable
fun ExpandableInfoBox(
    title: String,
    modifier: Modifier = Modifier,
    rightContent: @Composable (() -> Unit)? = null,
    initiallyExpanded: Boolean = false,
    cardColor: Color = MaterialTheme.colorScheme.surfaceContainerHigh,
    theme: MaterialTheme = MaterialTheme,
    content: @Composable () -> Unit,
) {
    var isExpanded by remember { mutableStateOf(initiallyExpanded) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClickLabel = if(!isExpanded) "expand information box" else "close information box") { isExpanded = !isExpanded },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f)
                )

                if (rightContent != null) {
                    rightContent()
                    Spacer(modifier = Modifier.width(8.dp))
                }

                Icon(
                    imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = if (isExpanded) "Opened $title information box" else "Closed $title information box",
                    modifier = Modifier.size(28.dp)
                )
            }

            AnimatedVisibility(visible = isExpanded) {
                Column {
                    Spacer(modifier = Modifier.height(12.dp))
                    Divider(color = theme.colorScheme.outlineVariant)
                    Spacer(modifier = Modifier.height(12.dp))
                    content()
                }
            }
        }
    }
}

@Composable
fun GeomarkingInfoBox(
    selectedLocation: String,
    geomarking: String,
    riskLabel: String,
    expandedText: String,
    modifier: Modifier = Modifier,
    theme: MaterialTheme = MaterialTheme
) {
    ExpandableInfoBox(
        title = selectedLocation,
        modifier = modifier,
        cardColor = theme.colorScheme.secondaryContainer,
        rightContent = {
            GeomarkingBadge(
                grade = geomarking
            )
        }
    ) {
        Text(
            text = "Samlet vurdering",
            style = theme.typography.labelLarge,
            color = theme.colorScheme.onSecondaryContainer.copy(alpha = 0.75f)
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = riskLabel,
            style = theme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = expandedText,
            style = theme.typography.bodyMedium
        )
    }
}

@Composable
fun GeomarkingBadge(
    grade: String,
    modifier: Modifier = Modifier,
    theme: MaterialTheme = MaterialTheme
) {
    val badgeColor = when (grade.uppercase()) {
        "A" -> Color(0xFFDFF5E1)
        "B" -> Color(0xFFBFE7A1)
        "C" -> Color(0xFFF1E38A)
        "D" -> Color(0xFFF3C56B)
        "E" -> Color(0xFFEFA066)
        "F" -> Color(0xFFE36C5C)
        "G" -> Color(0xFFB64545)
        else -> theme.colorScheme.surfaceVariant
    }

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = badgeColor)
    ) {
        Box(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = grade.uppercase(),
                style = theme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun HomeScreenLayoutPreview() {
    MaterialTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            GeomarkingInfoBox(
                selectedLocation = "Oslo",
                geomarking = "C",
                riskLabel = "Moderat georisiko",
                expandedText = "Dette området har moderate historiske risikofaktorer knyttet til naturhendelser."
            )
        }
    }
}