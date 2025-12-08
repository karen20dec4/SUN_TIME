package com.android.sun.ui.screens

import androidx.compose.animation.animateContentSize
import androidx. compose.foundation.clickable
import androidx. compose.foundation.layout.*
import androidx. compose.foundation.lazy.LazyColumn
import androidx.compose. foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose. material. icons.Icons
import androidx.compose.material.icons.automirrored.filled. ArrowBack
import androidx.compose.material. icons.filled.Add
import androidx.compose.material. icons.filled.Check
import androidx.compose.material.icons. filled.Delete
import androidx.compose.material.icons. filled.KeyboardArrowDown
import androidx.compose. material.icons.filled.KeyboardArrowUp
import androidx. compose.material.icons.filled.LocationOn
import androidx. compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose. ui.Alignment
import androidx.compose. ui.Modifier
import androidx.compose. ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx. compose.ui.unit.dp
import androidx.compose.ui. window.Dialog
import android.util.Log
import com.android.sun. data.model.LocationData
import com. android.sun.viewmodel.LocationViewModel

private const val TAG = "LocationScreen"

/**
 * Ecran pentru selectarea și gestionarea locațiilor
 * Listă compactă cu expand/collapse pentru detalii (Lat/Lon/Alt)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationScreen(
    viewModel: LocationViewModel,
    onLocationSelected: (LocationData) -> Unit,
    onBack: () -> Unit
) {
    val locations by viewModel.locations. collectAsState()
    val currentGPSLocation by viewModel.currentGPSLocation.collectAsState()
    val isLoading by viewModel. isLoading.collectAsState()
    val error by viewModel. error.collectAsState()
    
    // State pentru dialogul Add Location
    var showAddDialog by rememberSaveable { mutableStateOf(false) }

    Log.d(TAG, "LocationScreen: rendering, locations count = ${locations.size}")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Select Location") },
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
                . fillMaxSize()
                .padding(paddingValues)
                .padding(16. dp)
        ) {
            // Eroare (dacă există)
            error?.let { errorMessage ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme. colorScheme.errorContainer
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16. dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment. CenterVertically
                    ) {
                        Text(
                            text = errorMessage,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme. colorScheme.onErrorContainer,
                            modifier = Modifier.weight(1f)
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
                onUseGPS = { onLocationSelected(it) },
                onLoadGPS = { viewModel.loadGPSLocation() }
            )

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(16.dp))

            // Header cu titlu și buton Add
            Row(
                modifier = Modifier. fillMaxWidth(),
                horizontalArrangement = Arrangement. SpaceBetween,
                verticalAlignment = Alignment. CenterVertically
            ) {
                Text(
                    text = "Saved Locations",
                    style = MaterialTheme.typography.titleMedium
                )
                
                // Buton Add Location
                FilledTonalButton(
                    onClick = { showAddDialog = true }
                ) {
                    Icon(
                        imageVector = Icons. Filled.Add,
                        contentDescription = "Add Location",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text("Add Location")
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))

            // Lista de locații salvate
            when {
                isLoading && locations.isEmpty() -> {
                    Box(
                        modifier = Modifier. fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) { CircularProgressIndicator() }
                }
                locations.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("No saved locations", style = MaterialTheme.typography.bodyLarge)
                            Spacer(Modifier.height(8.dp))
                            Text(
                                text = "Use GPS location or add manually",
                                style = MaterialTheme. typography.bodySmall,
                                color = MaterialTheme. colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(
                            items = locations,
                            key = { it.id }  // Folosim ID-ul ca key (mai stabil decât name)
                        ) { location ->
                            LocationItemCompact(
                                location = location,
                                onSelect = { 
                                    Log.d(TAG, "Select clicked for: ${location.name}")
                                    onLocationSelected(location) 
                                },
                                onDelete = { 
                                    Log.d(TAG, "Delete clicked for: ${location.name}")
                                    viewModel.deleteLocation(location)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
    
    // Dialog pentru adăugare locație nouă
    if (showAddDialog) {
        AddLocationDialog(
            onDismiss = { showAddDialog = false },
            onSave = { locationData ->
                viewModel.saveLocation(locationData)
                showAddDialog = false
            }
        )
    }
}

/**
 * Dialog pentru adăugarea unei locații noi
 */
@Composable
private fun AddLocationDialog(
    onDismiss: () -> Unit,
    onSave: (LocationData) -> Unit
) {
    var cityName by rememberSaveable { mutableStateOf("") }
    var latitude by rememberSaveable { mutableStateOf("") }
    var longitude by rememberSaveable { mutableStateOf("") }
    var altitude by rememberSaveable { mutableStateOf("0") }
    
    // Validare
    val isValid = cityName.isNotBlank() && 
                  latitude. toDoubleOrNull() != null && 
                  longitude.toDoubleOrNull() != null &&
                  altitude. toDoubleOrNull() != null

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16. dp),
            shape = MaterialTheme.shapes. large
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Titlu
                Text(
                    text = "Add New Location",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight. Bold
                )
                
                // City Name
                OutlinedTextField(
                    value = cityName,
                    onValueChange = { cityName = it },
                    label = { Text("City Name") },
                    placeholder = { Text("e.g.  Bucharest") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                
                // Latitude
                OutlinedTextField(
                    value = latitude,
                    onValueChange = { latitude = it },
                    label = { Text("Latitude") },
                    placeholder = { Text("e.g. 44.4268") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType. Decimal),
                    isError = latitude.isNotEmpty() && latitude.toDoubleOrNull() == null,
                    supportingText = {
                        if (latitude.isNotEmpty() && latitude.toDoubleOrNull() == null) {
                            Text("Enter a valid number")
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
                
                // Longitude
                OutlinedTextField(
                    value = longitude,
                    onValueChange = { longitude = it },
                    label = { Text("Longitude") },
                    placeholder = { Text("e.g. 26.1025") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType. Decimal),
                    isError = longitude.isNotEmpty() && longitude.toDoubleOrNull() == null,
                    supportingText = {
                        if (longitude.isNotEmpty() && longitude.toDoubleOrNull() == null) {
                            Text("Enter a valid number")
                        }
                    },
                    modifier = Modifier. fillMaxWidth()
                )
                
                // Altitude
                OutlinedTextField(
                    value = altitude,
                    onValueChange = { altitude = it },
                    label = { Text("Altitude (m)") },
                    placeholder = { Text("e.g.  85") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType. Number),
                    isError = altitude. isNotEmpty() && altitude.toDoubleOrNull() == null,
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Butoane Cancel / Save
                Row(
                    modifier = Modifier. fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment. CenterVertically
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                    Spacer(Modifier.width(8.dp))
                    Button(
                        onClick = {
                            val newLocation = LocationData(
                                id = 0,  // Room va genera ID-ul automat
                                name = cityName. trim(),
                                latitude = latitude.toDouble(),
                                longitude = longitude.toDouble(),
                                altitude = altitude.toDoubleOrNull() ?: 0.0,
                                timeZone = java.util.TimeZone.getDefault().rawOffset / (1000.0 * 60.0 * 60.0),
                                isCurrentLocation = false
                            )
                            onSave(newLocation)
                        },
                        enabled = isValid
                    ) {
                        Icon(
                            imageVector = Icons. Filled.Check,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(Modifier.width(4.dp))
                        Text("Save")
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
        colors = CardDefaults. cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "📍 GPS Location",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (currentGPSLocation != null) {
                Column(Modifier.fillMaxWidth()) {
                    InfoRow(label = "Lat:", value = String.format("%.4f°", currentGPSLocation.latitude))
                    Spacer(Modifier.height(4.dp))
                    InfoRow(label = "Lon:", value = String.format("%.4f°", currentGPSLocation.longitude))
                    Spacer(Modifier.height(4.dp))
                    InfoRow(label = "Alt:", value = "${currentGPSLocation.altitude. toInt()}m")

                    Spacer(Modifier.height(12.dp))

                    Button(
                        onClick = { onUseGPS(currentGPSLocation) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults. buttonColors(containerColor = MaterialTheme.colorScheme. primary)
                    ) {
                        Icon(imageVector = Icons. Filled.Check, contentDescription = null, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Use this location")
                    }
                }
            } else {
                Button(
                    onClick = onLoadGPS,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading
                ) {
                    Icon(imageVector = Icons. Filled.LocationOn, contentDescription = null, modifier = Modifier. size(20.dp))
                    Spacer(Modifier.width(8. dp))
                    Text("Get GPS Location")
                }

                Spacer(Modifier.height(8.dp))

                Text(
                    text = "Tap to get current GPS coordinates",
                    style = MaterialTheme. typography.bodySmall,
                    color = MaterialTheme.colorScheme. onPrimaryContainer. copy(alpha = 0.7f)
                )
            }
        }
    }
}

/**
 * Item compact cu expand/collapse intern
 */
@Composable
private fun LocationItemCompact(
    location: LocationData,
    onSelect: () -> Unit,
    onDelete: () -> Unit
) {
    var expanded by rememberSaveable(location. id) { mutableStateOf(false) }

    Log.d(TAG, "LocationItemCompact rendering: ${location.name}")

    Card(
        modifier = Modifier
            .fillMaxWidth()
            . animateContentSize(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme. colorScheme.surfaceVariant
        )
    ) {
        Column(Modifier.fillMaxWidth()) {
            // Bara compactă
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Nume oraș (click → expand/collapse)
                Text(
                    text = location.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp)
                        .clickable { 
                            Log.d(TAG, "City name clicked: ${location.name}")
                            expanded = !expanded 
                        }
                )

                // Expand / Collapse
                IconButton(onClick = { 
                    Log.d(TAG, "Expand/Collapse clicked: ${location.name}")
                    expanded = !expanded 
                }) {
                    Icon(
                        imageVector = if (expanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                        contentDescription = if (expanded) "Collapse" else "Expand"
                    )
                }

                // Delete Button
                IconButton(
                    onClick = {
                        Log. d(TAG, "DELETE BUTTON CLICKED for: ${location.name}")
                        onDelete()
                    },
                    colors = IconButtonDefaults.iconButtonColors(
                        contentColor = MaterialTheme.colorScheme. error
                    )
                ) {
                    Icon(imageVector = Icons. Filled.Delete, contentDescription = "Delete location")
                }

                // Select Button
                TextButton(
                    onClick = {
                        Log. d(TAG, "SELECT BUTTON CLICKED for: ${location. name}")
                        onSelect()
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme. primary
                    )
                ) {
                    Text("Select")
                }
            }

            // Detalii expandate
            if (expanded) {
                Column(
                    modifier = Modifier
                        . fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    InfoRow(label = "Lat:", value = String.format("%.4f°°", location.latitude))
                    Spacer(Modifier.height(4.dp))
                    InfoRow(label = "Lon:", value = String. format("%.4f°", location.longitude))
                    Spacer(Modifier.height(4. dp))
                    InfoRow(label = "Alt:", value = location.getFormattedAltitude())
                    Spacer(Modifier.height(8.dp))
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
        horizontalArrangement = Arrangement. SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme. onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme. typography.bodyMedium,
            color = MaterialTheme.colorScheme. onSurfaceVariant,
            fontWeight = FontWeight. Bold
        )
    }
}