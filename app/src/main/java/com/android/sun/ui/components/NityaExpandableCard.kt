package com.android.sun.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.sun.domain.calculator.NityaResult

/**
 * Card SIMPLU pentru Nitya - o singură linie
 * Afișează: Nitya | 2/15 - Bhagamalini | 10% (rămas)
 */
@Composable
fun NityaExpandableCard(
    currentNitya: NityaResult,
    //showCode: Boolean = false,
    modifier: Modifier = Modifier
) {
    // ✅ Calculează procentul RĂMAS
    val tithiStartDegree = (currentNitya.difference / 12.0).toInt() * 12.0
    val progressInTithi = currentNitya.difference - tithiStartDegree
    val percentRemaining = ((12.0 - progressInTithi) / 12.0 * 100).toInt()
    
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // ✅ Titlu (stânga)
            Text(
                text = "Nitya",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                fontSize = 21.sp
            )
            
            // ✅ Informații Nitya (dreapta)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Număr curent/total
                Text(
                    text = "${currentNitya.number}/15",
                    style = MaterialTheme.typography.bodyLarge,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                Text(
                    text = "-",
                    style = MaterialTheme.typography.bodyLarge,
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
                
                // Nume Nitya
                Text(
                    text = currentNitya.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                
                // Procent rămas
                Text(
                    text = "$percentRemaining%",
                    style = MaterialTheme.typography.bodyLarge,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }
    }
}