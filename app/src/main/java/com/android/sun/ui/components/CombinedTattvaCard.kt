package com.android.sun.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.sun.data.model.TattvaInfo
import kotlinx.coroutines.delay
import java.util.*
import androidx.compose.foundation.border


/**
 * Card combinat pentru Tattva si SubTattva (design compact)
 */
@Composable
fun CombinedTattvaCard(
    tattva: TattvaInfo,
    subTattva: TattvaInfo,
    onAllDayClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Timp curent pentru countdown
    var currentTime by remember { mutableStateOf(Calendar.getInstance()) }
    
    LaunchedEffect(Unit) {
        while (true) {
            delay(1000)
            currentTime = Calendar.getInstance()
        }
    }
    
    // Calcul timp ramas Tattva
    val tattvaRemainingMillis = (tattva.endTime.timeInMillis - currentTime.timeInMillis).coerceAtLeast(0)
    val tattvaMinutes = (tattvaRemainingMillis / 60000).toInt()
    val tattvaSeconds = ((tattvaRemainingMillis % 60000) / 1000).toInt()
    
    // Calcul timp ramas SubTattva
    val subTattvaRemainingMillis = (subTattva.endTime.timeInMillis - currentTime.timeInMillis).coerceAtLeast(0)
    val subTattvaMinutes = (subTattvaRemainingMillis / 60000).toInt()
    val subTattvaSeconds = ((subTattvaRemainingMillis % 60000) / 1000).toInt()
    
    Card(
        modifier = modifier
            .fillMaxWidth()
            .shadow(8.dp, RoundedCornerShape(20.dp)),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            // -----------------------------------
            // TATTVA (MARE)
            // -----------------------------------
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                tattva.color,
                                tattva.color.copy(alpha = 0.7f)
                            )
                        ),
                        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
                    )
                    .padding(vertical = 28.dp, horizontal = 20.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Numele Tattva
                    Text(
                        text = tattva.name.uppercase(),
                        style = MaterialTheme.typography.headlineSmall,
                        fontSize = 64.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp
                    )
                    
                    Spacer(modifier = Modifier.height(1.dp)) //Spatiu sub tattva
                    
                    /*
                    // ? COMENTAT: Intervalul de timp Begin/End pentru Tattva
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(horizontalAlignment = Alignment.Start) {
                            Text(
                                text = formatTime(tattva.startTime),
                                style = MaterialTheme.typography.bodyLarge,
                                fontSize = 16.sp,
                                color = Color.White,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "- Begin -",
                                style = MaterialTheme.typography.bodySmall,
                                fontSize = 16.sp,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                        }
                        
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = formatTime(tattva.endTime),
                                style = MaterialTheme.typography.bodyLarge,
                                fontSize = 16.sp,
                                color = Color.White,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "- End -",
                                style = MaterialTheme.typography.bodySmall,
                                fontSize = 16.sp,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(26.dp))
                    */
                    
                    // Timpul ramas
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = String.format("%02d:%02d", tattvaMinutes, tattvaSeconds),
                            style = MaterialTheme.typography.headlineLarge,
                            fontSize = 48.sp,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(56.dp)) //Spatiu sub cronometru timp ramas tattva
                    
                    // ? Buton ALL DAY - centrat
                    Button(
                        onClick = onAllDayClick,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White.copy(alpha = 0.3f),
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .border(
                                width = 2.dp,
                                color = Color.Gray.copy(alpha = 0.6f),
                                shape = RoundedCornerShape(14.dp)
                            ),
                        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp)
                    ) {
                        Text(
                            text = "ALL DAY",
                            style = MaterialTheme.typography.labelLarge,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.5.sp,
                            color = Color.White
                        )
                    }
                }
            }
            
            // Separator
            HorizontalDivider(
                modifier = Modifier.fillMaxWidth(),
                thickness = 2.dp,
                color = MaterialTheme.colorScheme.surfaceVariant
            )
            
            // -----------------------------------
            // SUBTATTVA (MICA)
            // -----------------------------------
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                subTattva.color.copy(alpha = 0.25f),
                                subTattva.color.copy(alpha = 0.10f)
                            )
                        ),
                        shape = RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp)
                    )
                    .padding(vertical = 20.dp, horizontal = 20.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Numele SubTattva
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = subTattva.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontSize = 36.sp,
                            color = subTattva.color,
                            fontWeight = FontWeight.Bold
                        )
                        
                        // Indicator culoare + timp ramas
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(14.dp)
                                    .background(
                                        color = subTattva.color,
                                        shape = RoundedCornerShape(7.dp)
                                    )
                            )
                            
                            Spacer(modifier = Modifier.width(10.dp))
                            
                            Text(
                                text = String.format("%02d:%02d", subTattvaMinutes, subTattvaSeconds),
                                style = MaterialTheme.typography.titleLarge,
                                fontSize = 28.sp,
                                color = subTattva.color,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    
                    /*
                    // ? COMENTAT: Begin/End pentru SubTattva
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Start
                        Text(
                            text = "Begin: ${formatTime(subTattva.startTime)}",
                            style = MaterialTheme.typography.bodyMedium,
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                            fontWeight = FontWeight.Bold
                        )
                        
                        // Stop
                        Text(
                            text = "End: ${formatTime(subTattva.endTime)}",
                            style = MaterialTheme.typography.bodyMedium,
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                            fontWeight = FontWeight.Bold
                        )
                    }
                    */
                }
            }
        }
    }
}

/**
 * Formateaza timpul �n HH:mm:ss (cu secunde)
 */
private fun formatTime(calendar: Calendar): String {
    return String.format(
        "%02d:%02d:%02d",
        calendar.get(Calendar.HOUR_OF_DAY),
        calendar.get(Calendar.MINUTE),
        calendar.get(Calendar.SECOND)
    )
}