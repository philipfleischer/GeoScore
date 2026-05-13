package no.uio.ifi.in2000.team20.team20app.ui.screens.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RichTooltip
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.team20.team20app.ui.screens.search.SearchBarObject
import no.uio.ifi.in2000.team20.team20app.ui.sharedViewModels.AppViewModel
import no.uio.ifi.in2000.team20.team20app.ui.sharedViewModels.FrostViewModel
import no.uio.ifi.in2000.team20.team20app.ui.sharedViewModels.SavedViewModel
import no.uio.ifi.in2000.team20.team20app.util.Constants.DEFAULT_PADDING_DP
import no.uio.ifi.in2000.team20.team20app.util.Constants.MEDIUM_SCREEN_WIDTH
import no.uio.ifi.in2000.team20.team20app.util.LocalWindowSizeClass

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    onOpenSearch: () -> Unit,
    sharedViewModel: AppViewModel = hiltViewModel(),
    savedViewModel: SavedViewModel = hiltViewModel(),
    frostViewModel: FrostViewModel = hiltViewModel(),
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
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
        ,
        columns = GridCells.Fixed(if(compactScreenWidth) 1 else 2),
        contentPadding = PaddingValues(
            start = (DEFAULT_PADDING_DP*2).dp,
            end = if (compactScreenWidth) (DEFAULT_PADDING_DP*2).dp else (DEFAULT_PADDING_DP*5).dp,
            top = (DEFAULT_PADDING_DP*5).dp,
            bottom = (DEFAULT_PADDING_DP*2).dp,
        ),
        verticalArrangement = Arrangement.spacedBy(25.dp),
        horizontalArrangement = Arrangement.spacedBy(25.dp)
    ) {
        item {
            //Column for header and infotext
            Column(
                verticalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier
                    .fillMaxWidth(if (compactScreenWidth) 1f else 0.5f)
                    .semantics {
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
            SearchBarObject(onOpenSearch = onOpenSearch, text  = "Søk etter en adresse...")
        }
    }
}

@Composable
fun ExpandableInfoBox(
    title: String,
    modifier: Modifier = Modifier,
    rightContent: @Composable (() -> Unit)? = null,
    initiallyExpanded: Boolean = false,
    content: @Composable () -> Unit,
) {
    var isExpanded by remember { mutableStateOf(initiallyExpanded) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClickLabel = if (!isExpanded) "utvid informasjonsboks" else "lukk informasjonsboks") {
                isExpanded = !isExpanded
            }
        ,
        shape = if (!isExpanded) RoundedCornerShape(24) else RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding((DEFAULT_PADDING_DP*0.5).dp), verticalArrangement = Arrangement.Center) {
            Row(
                modifier = Modifier.fillMaxWidth().padding((DEFAULT_PADDING_DP*0.5).dp),
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
                    contentDescription = if (isExpanded) "Åpner $title informasjonsboks" else "Lukker $title informasjonsboks",
                    modifier = Modifier.size(28.dp)
                )
            }

            AnimatedVisibility(visible = isExpanded) {
                Column(modifier = Modifier.padding(DEFAULT_PADDING_DP.dp)) {
                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                    Spacer(modifier = Modifier.height(12.dp))
                    content()
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GeomarkingBadge(
    grade: String,
    modifier: Modifier = Modifier,
    iconStyle: Boolean = true,
    showTooltip: Boolean = false,
) {
    val badgeColor = when (grade.uppercase()) {
        "A" -> Color(0xFF4CAF50)
        "B" -> Color(0xFF8BC34A)
        "C" -> Color(0xFFFFC107)
        "D" -> Color(0xFFFFC56B)
        "E" -> Color(0xFFEFA066)
        "F" -> Color(0xFFE36C5C)
        "?" -> Color(0xFFFFC107)
        else -> Color(0xFFBDBDBD)
    }

    val tooltipState = rememberTooltipState(isPersistent = true)
    val scope = rememberCoroutineScope()

    TooltipBox(
        positionProvider = TooltipDefaults.rememberTooltipPositionProvider(TooltipAnchorPosition.Above),
        tooltip = {
            RichTooltip(
                title = { Text("Karakter ${grade.uppercase()}") }
            ) {
                Text("Merkingen er gitt utifra fra en skala fra A-F, der A betyr minst samlet risiko")
            }
        },
        state = tooltipState,
        enableUserInput = false
    ) {
        if (iconStyle) {
            Card(
                modifier = if (showTooltip) modifier.clickable { scope.launch { tooltipState.show() } } else modifier,
                shape = CircleShape,
                colors = CardDefaults.cardColors(containerColor = badgeColor)
            ) {
                Box(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = grade.uppercase(),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        } else {
            Text(
                text = grade.uppercase(),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                fontSize = 40.sp,
                color = badgeColor,
                modifier = if (showTooltip) modifier.clickable { scope.launch { tooltipState.show() } } else modifier
            )
        }
    }

}

