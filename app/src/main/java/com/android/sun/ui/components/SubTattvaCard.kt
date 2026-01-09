package com.android.sun.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.sun.data.model.TattvaInfo
import com.android.sun.data.model.getFormattedStartTime
import com.android.sun.data.model.getFormattedStopTime
import kotlinx.coroutines.delay
import java.util.*

/**
 * Card pentru afișarea SubTattva-ului curent
 */
@Composable
fun SubTattvaCard(
    subTattva: TattvaInfo,
    showCode: Boolean,
    modifier: Modifier = Modifier
) {
    // ✅ Actualizează timpul curent la fiecare secundă
    var currentTime by remember { mutableStateOf(Calendar.getInstance()) }
    
    LaunchedEffect(Unit) {
        while (true) {
            delay(1000)
            currentTime = Calendar.getInstance()
        }
    }
    
    // ✅ Calculează timpul rămas LIVE
    val remainingMillis = (subTattva.endTime.timeInMillis - currentTime.timeInMillis).coerceAtLeast(0)
    val remainingMinutes = (remainingMillis / 60000).toInt()
    val remainingSeconds = ((remainingMillis % 60000) / 1000).toInt()
    
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header cu culoare
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = subTattva.color,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .border(
                        width = 2.dp,
                        color = subTattva.color.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "SUBTATTVA",
                    style = MaterialTheme.typography.labelLarge,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Numele sau codul SubTattva-ului
            Box(
                modifier = Modifier
                    .background(
                        color = subTattva.color.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(12.dp)
                    )
                    .padding(20.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (showCode) subTattva.code else subTattva.name,
                    style = MaterialTheme.typography.headlineMedium,
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold,
                    color = subTattva.color
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Informații despre timp
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.Start
            ) {
                // Ora de început
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Start:",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = subTattva.getFormattedStartTime(),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Ora de sfârșit
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Stop:",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = subTattva.getFormattedStopTime(),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // ✅ Timpul rămas LIVE
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = subTattva.color.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Rămas: ",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = String.format("%02d:%02d", remainingMinutes, remainingSeconds),
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold,
                            color = subTattva.color
                        )
                    }
                }
            }
        }
    }
}