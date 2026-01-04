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
import com.android.sun.ui.components.MoonPhaseCard

/**
 * Ecranul principal al aplicației
 * - Fără TopAppBar
 * - Card de info clickabil (navighează la Locații)
 */
@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
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

                // Moon Phase Card
                item(key = "moon_phase") {
                    MoonPhaseCard(
                        moonSign = astroData.moonSign,
                        moonPhase = astroData.moonPhase
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
                    Text("Refresh")
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
    onNavigateToLocation:  () -> Unit,
    onRefresh:  () -> Unit,
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
    
    // ✅ Calculează timezone-ul locației
    val locationOffsetMillis = (astroData.timeZone * 3600 * 1000).toInt()
    val locationTimeZone = SimpleTimeZone(locationOffsetMillis, "Location")
    
    // ✅ Calculează timezone-ul telefonului
    val phoneTimeZone = TimeZone.getDefault()
    val phoneOffsetHours = phoneTimeZone.rawOffset / (1000.0 * 60.0 * 60.0)
    
    // ✅ Verifică dacă timezone-urile sunt diferite
    val isDifferentTimeZone = kotlin.math.abs(astroData.timeZone - phoneOffsetHours) > 0.1

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
            
            // Primul rând:  Data/Ora + Settings/Refresh
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
                            contentDescription = "Settings",
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
                            contentDescription = "Refresh",
                            tint = Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = Color.White.copy(alpha = 0.3f), thickness = 1.dp)
            Spacer(modifier = Modifier.height(12.dp))

            // Al doilea rând: Locație + icon
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // ✅ Coloană pentru nume locație + ora locală (dacă e diferit timezone-ul)
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    // ✅ Afișează GPS, numele locației, sau "No location"
                    Text(
                        text = when {
                            astroData.isGPSLocation -> "GPS"
                            astroData.locationName.isNotEmpty() -> astroData.locationName
                            else -> "No location"
                        },
                        style = MaterialTheme.typography.titleMedium,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    
                    // ✅ NOU:  Afișează ora locală DOAR dacă timezone-ul e diferit de cel al telefonului
                    if (isDifferentTimeZone) {
                        Spacer(modifier = Modifier.height(4.dp))
                        
                        // ✅ Formatăm ora curentă în timezone-ul locației
                        val localTimeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).apply {
                            timeZone = locationTimeZone
                        }
                        
                        // ✅ Formatăm GMT offset (ex: GMT-05:00)
                        val gmtOffsetHours = astroData.timeZone.toInt()
                        val gmtOffsetMinutes = ((kotlin.math.abs(astroData.timeZone) % 1) * 60).toInt()
                        val gmtSign = if (astroData.timeZone >= 0) "+" else "-"
                        val gmtOffsetFormatted = String.format("GMT%s%02d:%02d", gmtSign, kotlin.math.abs(gmtOffsetHours), gmtOffsetMinutes)
                        
                        // ✅ Formatăm offset-ul scurt pentru paranteze (ex: -0500)
                        val offsetShort = String.format("%s%02d%02d", gmtSign, kotlin.math.abs(gmtOffsetHours), gmtOffsetMinutes)
                        
						/**
						Spacer(modifier = Modifier.height(4.dp))
						HorizontalDivider(color = Color.White.copy(alpha = 0.3f), thickness = 1.dp)
						Spacer(modifier = Modifier.height(6.dp))
						*/
						
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // ✅ Icon de ceas (folosim emoji sau text)
                            
                            
                            Spacer(modifier = Modifier.width(4.dp))
                            
                            // ✅ Ora locală + GMT offset
                            Text(
                                text = "Local time: ${localTimeFormat.format(currentTime.time)} $gmtOffsetFormatted",
                                style = MaterialTheme.typography.bodySmall,
                                fontSize = 12.sp,
                                color = Color.White.copy(alpha = 0.9f)
                            )
                        }
                    }
                }

                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = "Change location",
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // ✅ Al treilea rând: Sunrise/Sunset AZI (font mare)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Răsărit AZI cu polaritate
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = astroData.sunrisePolaritySymbol,
                        style = MaterialTheme.typography.bodyMedium,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.width(4.dp))
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

                // Apus AZI cu polaritate
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = astroData.sunsetPolaritySymbol,
                        style = MaterialTheme.typography.bodyMedium,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.width(4.dp))
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

            // ✅ Al patrulea rând: Sunrise/Sunset MÂINE (font 16, transparență)
            Spacer(modifier = Modifier.height(4.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Răsărit MÂINE
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = astroData.nextSunrisePolaritySymbol,
                        style = MaterialTheme.typography.bodySmall,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = "↑",
                        style = MaterialTheme.typography.bodySmall,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                    Text(
                        text = astroData.nextSunriseFormatted,
                        style = MaterialTheme.typography.bodySmall,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                }

                // Apus MÂINE
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = astroData.nextSunsetPolaritySymbol,
                        style = MaterialTheme.typography.bodySmall,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = "↓",
                        style = MaterialTheme.typography.bodySmall,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                    Text(
                        text = astroData.nextSunsetFormatted,
                        style = MaterialTheme.typography.bodySmall,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                }
            }
        }
    }
}