package com.android.sun.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.sun.domain.calculator.PlanetType
import java.text.SimpleDateFormat
import java.util.*

/**
 * Card COMPACT pentru Planetary Hours cu Expand/Collapse
 * ✅ COLLAPSED: afișează doar ora curentă
 * ✅ EXPANDED: afișează toate orele cu auto-highlight la ora curentă
 */
@Composable
fun PlanetaryHoursCard(
    sunrise: Calendar,
    sunset: Calendar,
    nextSunrise: Calendar,
    currentPlanetIndex: Int,  // Index global (0-23) al orei curente
    modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(false) }
    
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { isExpanded = !isExpanded },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // ✅ HEADER: Afișează ÎNTOTDEAUNA (collapsed sau expanded)
            CurrentPlanetaryHourHeader(
                sunrise = sunrise,
                sunset = sunset,
                nextSunrise = nextSunrise,
                currentPlanetIndex = currentPlanetIndex,
                isExpanded = isExpanded
            )
            
            // ✅ EXPANDED CONTENT: Toate orele
            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Spacer(modifier = Modifier.height(8.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f))
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    // Date range
                    val dateFormat = SimpleDateFormat("dd MMMM yyyy", Locale.ENGLISH)
                    Text(
                        text = "${dateFormat.format(sunrise.time)} - ${dateFormat.format(nextSunrise.time)}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                        fontSize = 14.sp
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    // Scrollable list of all hours
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 400.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        // ✅ DAY: Hours 0-11
                        PlanetaryHourSection(
                            title = "☀️ DAY (Sunrise - Sunset)",
                            startTime = sunrise,
                            endTime = sunset,
                            hourStartIndex = 0,
                            hourCount = 12,
                            currentPlanetIndex = currentPlanetIndex,
                            dayOfWeek = sunrise.get(Calendar.DAY_OF_WEEK) - 1
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // ✅ NIGHT: Hours 12-23
                        PlanetaryHourSection(
                            title = "🌙 NIGHT (Sunset - Sunrise)",
                            startTime = sunset,
                            endTime = nextSunrise,
                            hourStartIndex = 12,
                            hourCount = 12,
                            currentPlanetIndex = currentPlanetIndex,
                            dayOfWeek = sunrise.get(Calendar.DAY_OF_WEEK) - 1
                        )
                    }
                }
            }
        }
    }
}

/**
 * Header compact: Afișează doar ora curentă + expand/collapse icon
 * ✅ Font mai mare (20sp pentru planetă)
 * ✅ Fără pictogramă ceas
 * ✅ Planetă aliniată la dreapta
 */
@Composable
private fun CurrentPlanetaryHourHeader(
    sunrise: Calendar,
    sunset: Calendar,
    nextSunrise: Calendar,
    currentPlanetIndex: Int,
    isExpanded: Boolean
) {
    // Calculează ora curentă
    val isDayTime = currentPlanetIndex < 12
    val startTime = if (isDayTime) sunrise else sunset
    val endTime = if (isDayTime) sunset else nextSunrise
    val hourIndexInPeriod = currentPlanetIndex % 12
    
    val totalDuration = endTime.timeInMillis - startTime.timeInMillis
    val planetaryHourDuration = totalDuration / 12
    
    val hourStartMillis = startTime.timeInMillis + (hourIndexInPeriod * planetaryHourDuration)
    val hourEndMillis = hourStartMillis + planetaryHourDuration
    
    val hourStart = Calendar.getInstance().apply { timeInMillis = hourStartMillis }
    val hourEnd = Calendar.getInstance().apply { timeInMillis = hourEndMillis }
    
    val dayOfWeek = sunrise.get(Calendar.DAY_OF_WEEK) - 1
    val planet = getPlanetForGlobalIndex(dayOfWeek, currentPlanetIndex)
    
    val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
    
    Column(modifier = Modifier.fillMaxWidth()) {
        // Titlu
        Text(
            text = "Planetary Hours",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            fontSize = 21.sp
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Row cu ora și planetă
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // ✅ ORA (stânga)
            Text(
                text = "${timeFormat.format(hourStart.time)} - ${timeFormat.format(hourEnd.time)}",
                style = MaterialTheme.typography.bodyLarge,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
            
            // ✅ PLANETĂ + ICON (dreapta)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = planet.code,
                    style = MaterialTheme.typography.titleLarge,
                    fontSize = 24.sp,
                    color = getPlanetColor(planet)
                )
                
                Text(
                    text = planet.displayName,
                    style = MaterialTheme.typography.titleMedium,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = getPlanetColor(planet)
                )
                
                Icon(
                    imageVector = if (isExpanded) {
                        Icons.Default.KeyboardArrowUp
                    } else {
                        Icons.Default.KeyboardArrowDown
                    },
                    contentDescription = if (isExpanded) "Collapse" else "Expand",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    }
}

/**
 * Secțiune pentru ZI sau NOAPTE
 */
@Composable
private fun PlanetaryHourSection(
    title: String,
    startTime: Calendar,
    endTime: Calendar,
    hourStartIndex: Int,
    hourCount: Int,
    currentPlanetIndex: Int,
    dayOfWeek: Int
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        // Section title
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            fontSize = 16.sp
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Calculează durata unei ore planetare
        val totalDuration = endTime.timeInMillis - startTime.timeInMillis
        val planetaryHourDuration = totalDuration / 12
        
        val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        
        // Afișează fiecare oră planetară
        repeat(hourCount) { index ->
            val globalIndex = hourStartIndex + index
            val planet = getPlanetForGlobalIndex(dayOfWeek, globalIndex)
            
            val hourStartMillis = startTime.timeInMillis + (index * planetaryHourDuration)
            val hourEndMillis = hourStartMillis + planetaryHourDuration
            
            val hourStart = Calendar.getInstance().apply {
                timeInMillis = hourStartMillis
            }
            val hourEnd = Calendar.getInstance().apply {
                timeInMillis = hourEndMillis
            }
            
            val isCurrent = globalIndex == currentPlanetIndex
            
            PlanetaryHourRow(
                planet = planet,
                startTime = timeFormat.format(hourStart.time),
                endTime = timeFormat.format(hourEnd.time),
                isCurrent = isCurrent
            )
        }
    }
}

/**
 * Rând individual pentru o oră planetară
 * ✅ Font +2 (de la 12sp la 14sp)
 */
@Composable
private fun PlanetaryHourRow(
    planet: PlanetType,
    startTime: String,
    endTime: String,
    isCurrent: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = if (isCurrent) {
                    getPlanetColor(planet).copy(alpha = 0.2f)
                } else {
                    Color.Transparent
                },
                shape = RoundedCornerShape(4.dp)
            )
            .padding(vertical = 6.dp, horizontal = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Timp
        Text(
            text = "$startTime - $endTime",
            style = MaterialTheme.typography.bodySmall,
            fontSize = 14.sp,  // ✅ Era 12sp, acum 14sp (+2)
            fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
            modifier = Modifier.weight(1f)
        )
        
        // Planetă
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = planet.code,
                style = MaterialTheme.typography.bodyMedium,
                fontSize = 18.sp,  // ✅ Era 16sp, acum 18sp (+2)
                color = getPlanetColor(planet)
            )
            Text(
                text = planet.displayName,
                style = MaterialTheme.typography.bodySmall,
                fontSize = 14.sp,  // ✅ Era 12sp, acum 14sp (+2)
                fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
                color = if (isCurrent) {
                    getPlanetColor(planet)
                } else {
                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                }
            )
            
            if (isCurrent) {
                Text(
                    text = "◄ NOW",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,  // ✅ Era 10sp, acum 12sp (+2)
                    color = getPlanetColor(planet)
                )
            }
        }
    }
}

/**
 * Obține planeta pentru index global (0-23)
 */
private fun getPlanetForGlobalIndex(dayOfWeek: Int, globalIndex: Int): PlanetType {
    val chaldeanOrder = listOf(
        PlanetType.SATURN, PlanetType.JUPITER, PlanetType.MARS,
        PlanetType.SUN, PlanetType.VENUS, PlanetType.MERCURY, PlanetType.MOON
    )
    
    val dayRuler = listOf(
        PlanetType.SUN,     // Sunday (0)
        PlanetType.MOON,    // Monday (1)
        PlanetType.MARS,    // Tuesday (2)
        PlanetType.MERCURY, // Wednesday (3)
        PlanetType.JUPITER, // Thursday (4)
        PlanetType.VENUS,   // Friday (5)
        PlanetType.SATURN   // Saturday (6)
    )
    
    val startPlanet = dayRuler[dayOfWeek]
    val startIndex = chaldeanOrder.indexOf(startPlanet)
    val planetIndex = (startIndex + globalIndex) % 7
    
    return chaldeanOrder[planetIndex]
}

/**
 * Culori pentru planete
 */
private fun getPlanetColor(planet: PlanetType): Color {
    return when (planet) {
        PlanetType.SUN -> Color(0xFFFFD700)      // Gold
        PlanetType.MOON -> Color(0xFFC0C0C0)     // Silver
        PlanetType.MERCURY -> Color(0xFF808080)  // Gray
        PlanetType.VENUS -> Color(0xFF00CED1)    // Turquoise
        PlanetType.MARS -> Color(0xFFFF4500)     // Red-Orange
        PlanetType.JUPITER -> Color(0xFF4169E1)  // Royal Blue
        PlanetType.SATURN -> Color(0xFF2F4F4F)   // Dark Slate Gray
    }
}