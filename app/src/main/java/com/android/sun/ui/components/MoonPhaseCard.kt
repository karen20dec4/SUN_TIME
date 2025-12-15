package com.android.sun.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.sun.domain.calculator.MoonPhaseResult
import java.text.SimpleDateFormat
import java.util.*

/**
 * Card pentru afișarea fazelor lunii
 */
@Composable
fun MoonPhaseCard(
    moonSign: String,          // "29° Scorpion"
    moonPhase: MoonPhaseResult,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header:  Moon sign + Illumination
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Moon:  $moonSign",
                    style = MaterialTheme.typography.titleMedium,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Text(
                    text = "${moonPhase.illuminationPercent}%",
                    style = MaterialTheme.typography.titleMedium,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            // ✅ DEBUG: Afișează ora curentă și timezone-ul
            val currentTime = Calendar.getInstance(TimeZone.getTimeZone("Europe/Bucharest"))
            val debugFormat = SimpleDateFormat("HH:mm:ss z (Z)", Locale.getDefault())
            debugFormat.timeZone = TimeZone.getTimeZone("Europe/Bucharest")
            
            Text(
                text = "🕐 Ora acum: ${debugFormat.format(currentTime.time)}",
                style = MaterialTheme.typography.bodySmall,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.error
            )
            
            HorizontalDivider(
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f),
                thickness = 1.dp
            )
            
            // Tripura Sundari
            MoonEventRow(
                label = "Tripura Sundari:",
                date = moonPhase.nextTripuraSundari
            )
            
            // Full Moon
            MoonEventRow(
                label = "Full moon:",
                date = moonPhase.nextFullMoon
            )
            
            // New Moon
            MoonEventRow(
                label = "New moon:",
                date = moonPhase.nextNewMoon
            )
        }
    }
}

/**
 * ✅ FIX: Convertește Calendar-ul la timezone București pentru afișare
 */
@Composable
private fun MoonEventRow(
    label:  String,
    date: Calendar
) {
    // ✅ Convertim explicit la timezone București
    val bucharestTimeZone = TimeZone.getTimeZone("Europe/Bucharest")
    val dateFormat = SimpleDateFormat("d MMM - HH:mm z", Locale.getDefault())
    dateFormat.timeZone = bucharestTimeZone
    
    // ✅ DEBUG: Log timezone-ul Calendar-ului primit
    android.util.Log.d("MoonPhaseCard", "🕐 $label Calendar TZ: ${date.timeZone.id}, millis: ${date.timeInMillis}")
    
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Text(
            text = dateFormat.format(date.time),
            style = MaterialTheme.typography.bodyMedium,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}