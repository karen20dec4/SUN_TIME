package com.android.sun.ui.screens

import com.android.sun.data.model.toTattvaInfo
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.sun.data.model.AstroData
import com.android.sun.ui.components.CombinedTattvaCard
import com.android.sun.ui.components.PlanetaryHoursCard
import com.android.sun.ui.components.NityaExpandableCard
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*

/**
 * Ecranul principal al aplicației
 * - Fără TopAppBar
 * - Card de info clickabil (navighează la Locații)
 */
@Composable
fun MainScreen(
    modifier: Modifier = Modifier,           // recomandat să fie primul parametru opțional
    astroData: AstroData?,
    isLoading: Boolean,
    codeMode: Boolean,
    onCodeModeChange: (Boolean) -> Unit,
    onRefresh: () -> Unit,
    onNavigateToLocation: () -> Unit,
    onNavigateToAllDay: () -> Unit = {}
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center)
            )
        } else if (astroData != null) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(
                    top = 16.dp,
                    bottom = 80.dp
                )
            ) {
                // Card compact cu gradient Tattva
                item(key = "info_compact") {
                    CompactInfoCard(
                        astroData = astroData,
                        onNavigateToLocation = onNavigateToLocation,
                        onRefresh = onRefresh,
                        onSettings = {
                            // TODO: navigare către Settings dacă dorești
                        }
                    )
                }

                // Card combinat Tattva + SubTattva
                item(key = "combined_tattva") {
                    CombinedTattvaCard(
                        tattva = astroData.tattva.toTattvaInfo(),
                        subTattva = astroData.subTattva.toTattvaInfo(),
                        onAllDayClick = onNavigateToAllDay
                    )
                }

                // Planetary Hours Card
                item(key = "planetary_hours") {
                    val nextSunrise = astroData.sunrise.clone() as Calendar
                    nextSunrise.add(Calendar.DAY_OF_MONTH, 1)

                    PlanetaryHoursCard(
                        sunrise = astroData.sunrise,
                        sunset = astroData.sunset,
                        nextSunrise = nextSunrise,
                        currentPlanetIndex = astroData.planet.hourNumber - 1
                    )
                }

                // Nitya Card (o singură linie)
                item(key = "nitya") {
                    NityaExpandableCard(
                        currentNitya = astroData.nitya
                        // dacă vei folosi codurile, poți reintroduce: showCode = codeMode
                    )
                }
            }
        } else {
            // Mesaj când nu sunt date
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Loading, please wait...",
                    style = MaterialTheme.typography.headlineSmall
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = onRefresh) {
                    Text("Actualizează")
                }
            }
        }
    }
}

/**
 * Card compact cu gradient Tattva + Data/Ora LIVE + Settings/Refresh
 * Cardul este clickabil (click oriunde pe card pentru a schimba locația)
 */
@Composable
private fun CompactInfoCard(
    astroData: AstroData,
    onNavigateToLocation: () -> Unit,
    onRefresh: () -> Unit,
    onSettings: () -> Unit
) {
    var currentTime by remember { mutableStateOf(Calendar.getInstance()) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(1000)
            currentTime = Calendar.getInstance()
        }
    }

    val tattvaColor = astroData.tattva.toTattvaInfo().color

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                android.util.Log.d("CompactInfoCard", "Card clicked - navigating to location")
                onNavigateToLocation()
            },
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            tattvaColor.copy(alpha = 0.7f),
                            tattvaColor.copy(alpha = 0.5f)
                        )
                    )
                )
                .padding(16.dp)
        ) {
            // Primul rând: Data/Ora + Settings/Refresh
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val dateFormat = SimpleDateFormat("dd-MMM-yyyy", Locale.US)
                    Text(
                        text = dateFormat.format(currentTime.time).uppercase(),
                        style = MaterialTheme.typography.titleMedium,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
                    Text(
                        text = timeFormat.format(currentTime.time),
                        style = MaterialTheme.typography.titleMedium,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                // Butoane Settings + Refresh
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Settings Button
                    IconButton(
                        onClick = {
                            android.util.Log.d("CompactInfoCard", "Settings clicked")
                            onSettings()
                        },
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Setări",
                            tint = Color.White
                        )
                    }

                    // Refresh Button
                    IconButton(
                        onClick = {
                            android.util.Log.d("CompactInfoCard", "Refresh clicked")
                            onRefresh()
                        },
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Actualizează",
                            tint = Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = Color.White.copy(alpha = 0.3f), thickness = 1.dp)
            Spacer(modifier = Modifier.height(12.dp))

            // Al doilea rând: Locație + icon (decorativ)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "📍 ${astroData.locationName.ifEmpty { "Locație necunoscută" }}",
                    style = MaterialTheme.typography.titleMedium,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.weight(1f)
                )

                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = "Schimbă locația",
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Al treilea rând: Sunrise/Sunset
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "↑",
                        style = MaterialTheme.typography.bodyMedium,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = astroData.sunriseFormatted,
                        style = MaterialTheme.typography.bodyMedium,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "↓",
                        style = MaterialTheme.typography.bodyMedium,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = astroData.sunsetFormatted,
                        style = MaterialTheme.typography.bodyMedium,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                }
            }
        }
    }
}