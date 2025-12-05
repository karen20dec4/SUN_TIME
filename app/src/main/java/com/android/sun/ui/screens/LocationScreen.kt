package com.android.sun.ui. screens

import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx. compose.foundation.lazy.items
import androidx.compose.material. icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose. material. icons.filled.Check
import androidx.compose.material. icons.filled.Delete
import androidx.compose.material. icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.android.sun.data.model.LocationData
import com.android.sun.viewmodel.LocationViewModel

/**
 * Ecran pentru selectarea și gestionarea locațiilor
 * Versiune simplificată - fără formulare complexe
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationScreen(
    viewModel: LocationViewModel,
    onLocationSelected: (LocationData) -> Unit,
    onBack: () -> Unit
) {
    // Observă state-urile din ViewModel
    val locations by viewModel.locations.collectAsState()
    val currentGPSLocation by viewModel.currentGPSLocation.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Selectează Locația") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // Afișează eroarea dacă există
            error?.let { errorMessage ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = errorMessage,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            modifier = Modifier. weight(1f)
                        )
                        TextButton(onClick = { viewModel.clearError() }) {
                            Text("OK")
                        }
                    }
                }
            }

            // Secțiunea GPS
            GPSLocationSection(
                currentGPSLocation = currentGPSLocation,
                isLoading = isLoading,
                onUseGPS = { location ->
                    onLocationSelected(location)
                },
                onLoadGPS = { viewModel.loadGPSLocation() }
            )

            Spacer(modifier = Modifier. height(16.dp))

            HorizontalDivider()

            Spacer(modifier = Modifier. height(16.dp))

            // Lista de locații salvate
            Text(
                text = "Locații Salvate",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            if (isLoading && locations.isEmpty()) {
                Box(
                    modifier = Modifier. fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (locations.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Nu există locații salvate",
                            style = MaterialTheme.typography. bodyLarge
                        )
                        Spacer(modifier = Modifier. height(8.dp))
                        Text(
                            text = "Folosește locația GPS sau adaugă manual",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(
                        items = locations,
                        key = { location -> location. name }
                    ) { location ->
                        LocationItem(
                            location = location,
                            onSelect = { onLocationSelected(location) },
                            onDelete = { viewModel.deleteLocation(location) }
                        )
                    }
                }
            }
        }
    }
}

/**
 * Secțiunea pentru locația GPS
 */
@Composable
private fun GPSLocationSection(
    currentGPSLocation: LocationData?,
    isLoading: Boolean,
    onUseGPS: (LocationData) -> Unit,
    onLoadGPS: () -> Unit
) {
    Card(
        modifier = Modifier. fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme. colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16. dp)
        ) {
            Row(
                modifier = Modifier. fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "📍 Locație GPS",
                    style = MaterialTheme. typography.titleMedium,
                    color = MaterialTheme. colorScheme.onPrimaryContainer
                )

                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier. size(24.dp),
                        strokeWidth = 2.dp
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (currentGPSLocation != null) {
                // Afișează coordonatele GPS
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    InfoRow(
                        label = "Lat:",
                        value = String.format("%.4f°", currentGPSLocation.latitude)
                    )
                    Spacer(modifier = Modifier. height(4.dp))
                    InfoRow(
                        label = "Lon:",
                        value = String.format("%.4f°", currentGPSLocation.longitude)
                    )
                    Spacer(modifier = Modifier. height(4.dp))
                    InfoRow(
                        label = "Alt:",
                        value = "${currentGPSLocation.altitude. toInt()}m"
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Buton pentru folosirea locației GPS
                    Button(
                        onClick = { onUseGPS(currentGPSLocation) },
                        modifier = Modifier. fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Folosește această locație")
                    }
                }
            } else {
                // Buton pentru obținerea locației GPS
                Button(
                    onClick = onLoadGPS,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading
                ) {
                    Icon(
                        imageVector = Icons. Default.LocationOn,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Obține Locația GPS")
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Apasă pentru a obține coordonatele GPS curente",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer. copy(alpha = 0.7f)
                )
            }
        }
    }
}

/**
 * Item pentru o locație salvată
 */
@Composable
private fun LocationItem(
    location: LocationData,
    onSelect: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier. fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme. colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Informațiile locației
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = location.name,
                    style = MaterialTheme. typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Lat: ${String.format("%.4f", location.latitude)}°",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant. copy(alpha = 0.7f)
                )

                Text(
                    text = "Lon: ${String.format("%.4f", location.longitude)}°",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )

                Text(
                    text = location.getFormattedAltitude(),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
            }

            // Butoane de acțiune
            Row {
                // Buton selectare
                IconButton(
                    onClick = onSelect,
                    colors = IconButtonDefaults.iconButtonColors(
                        contentColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Selectează locația"
                    )
                }

                // Buton ștergere
                IconButton(
                    onClick = onDelete,
                    colors = IconButtonDefaults.iconButtonColors(
                        contentColor = MaterialTheme. colorScheme.error
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Șterge locația"
                    )
                }
            }
        }
    }
}

/**
 * Component helper pentru afișarea info (label: valoare)
 */
@Composable
private fun InfoRow(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier. fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onPrimaryContainer. copy(alpha = 0.8f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
        )
    }
}