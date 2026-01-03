package com.android.sun.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat
import android.util.Log
import com.android.sun.data.model.LocationData
import com.android.sun.viewmodel.LocationViewModel
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search

private const val TAG = "LocationScreen"

/**
 * Ecran pentru selectarea și gestionarea locațiilor
 * Listă compactă cu expand/collapse pentru detalii (Lat/Lon/Alt)
 */










@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationScreen(
    viewModel: LocationViewModel,
    mainViewModel: com.android.sun.viewmodel.MainViewModel,
    onLocationSelected: (LocationData) -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val locations by viewModel.locations.collectAsState()
    val currentGPSLocation by viewModel.currentGPSLocation. collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error. collectAsState()
    
    // ✅ State pentru filtrul de căutare
    var searchQuery by rememberSaveable { mutableStateOf("") }
    
    // ✅ Filtrează locațiile în funcție de query
    val filteredLocations = remember(locations, searchQuery) {
        if (searchQuery.isBlank()) {
            locations
        } else {
            locations.filter { location ->
                location.name.contains(searchQuery, ignoreCase = true)
            }
        }
    }
    
    // State pentru dialogul Add Location
    var showAddDialog by rememberSaveable { mutableStateOf(false) }
    
    // Permission launcher pentru GPS
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineLocationGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
        val coarseLocationGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false
        
        Log.d(TAG, "Permission result: fine=$fineLocationGranted, coarse=$coarseLocationGranted")
        
        if (fineLocationGranted || coarseLocationGranted) {
            Log.d(TAG, "✅ Permission granted, loading GPS...")
            viewModel.loadGPSLocation()
        } else {
            Log.e(TAG, "❌ Permission denied by user")
        }
    }
    
    // Funcție pentru a cere permisiuni și încărca GPS
    fun requestGPSLocation() {
        Log.d(TAG, "🔵 requestGPSLocation() called")
        
        val hasFinePermission = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        
        val hasCoarsePermission = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        
        Log.d(TAG, "Current permissions: fine=$hasFinePermission, coarse=$hasCoarsePermission")
        
        if (hasFinePermission || hasCoarsePermission) {
            Log.d(TAG, "✅ Already has permission, loading GPS...")
            viewModel.loadGPSLocation()
        } else {
            Log. d(TAG, "🔵 Requesting permissions from user...")
            permissionLauncher. launch(
                arrayOf(
                    Manifest. permission.ACCESS_FINE_LOCATION,
                    Manifest. permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    Log.d(TAG, "LocationScreen: rendering, locations count = ${locations.size}, filtered = ${filteredLocations.size}")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Select Location") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored. Filled.ArrowBack,
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
                .padding(16.dp)
        ) {
            // Eroare (dacă există)
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
                onUseGPS = { onLocationSelected(it) },
                onLoadGPS = { requestGPSLocation() },
                onRefresh = { requestGPSLocation() }  // ✅ ADĂUGAT
            )

            Spacer(modifier = Modifier. height(16.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier. height(16.dp))

            // ✅ Header cu câmp de căutare și buton Add
            Row(
                modifier = Modifier. fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // ✅ Search Field
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    label = { Text("Search location") },
                    placeholder = { Text("Type city name...") },
                    singleLine = true,
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Filled. Search,
                            contentDescription = "Search"
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(
                                    imageVector = Icons. Filled.Clear,
                                    contentDescription = "Clear search"
                                )
                            }
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp)
                )
                
                // Buton Add Location
                FilledTonalIconButton(
                    onClick = { showAddDialog = true }
                ) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "Add Location"
                    )
                }
            }
            
            Spacer(modifier = Modifier. height(8.dp))

            // ✅ Afișare număr rezultate
            if (searchQuery.isNotEmpty()) {
                Text(
                    text = "${filteredLocations.size} location(s) found",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier. padding(bottom = 8.dp)
                )
            }

            // ✅ Lista de locații filtrate
            when {
                isLoading && locations.isEmpty() -> {
                    Box(
                        modifier = Modifier. fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) { CircularProgressIndicator() }
                }
                filteredLocations.isEmpty() && searchQuery.isNotEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Filled.Search,
                                contentDescription = null,
                                modifier = Modifier.size(48.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(Modifier.height(16.dp))
                            Text(
                                "No locations found",
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Spacer(Modifier.height(8.dp))
                            Text(
                                text = "Try a different search term",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
                locations.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("No saved locations", style = MaterialTheme.typography. bodyLarge)
                            Spacer(Modifier.height(8.dp))
                            Text(
                                text = "Use GPS location or add manually",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier. fillMaxSize(),
                        verticalArrangement = Arrangement. spacedBy(8.dp)
                    ) {
                        items(
                            items = filteredLocations,
                            key = { it.id }
                        ) { location ->
                            LocationItemCompact(
                                location = location,
                                onSelect = { 
                                    android.util.Log.d("LocationScreen", "═══════════════════════════════════════")
                                    android.util. Log.d("LocationScreen", "🟢 SELECT BUTTON CLICKED")
                                    android.util. Log.d("LocationScreen", "🟢 Selected location: ${location.name}")
                                    android.util.Log. d("LocationScreen", "🟢   Lat: ${location.latitude}, Lon: ${location.longitude}")
                                    android.util. Log.d("LocationScreen", "🟢 Calling onLocationSelected...")
                                    onLocationSelected(location)
                                    android.util.Log.d("LocationScreen", "🟢 Calling mainViewModel.refresh()...")
                                    mainViewModel.refresh()
                                    android.util.Log.d("LocationScreen", "🟢 Refresh called!")
                                    android.util.Log.d("LocationScreen", "═══════════════════════════════════════")
                                },
                                onDelete = { 
                                    android.util. Log.d("LocationScreen", "❌ DELETE clicked for: ${location.name}")
                                    viewModel.deleteLocation(location)
                                }
                            )
                        }
                        
                        // Buton Load Defaults la sfârșitul listei (doar dacă nu e căutare activă)
                        if (searchQuery.isEmpty()) {
                            item(key = "load_defaults") {
                                Spacer(modifier = Modifier. height(16.dp))
                                
                                Button(
                                    onClick = {
                                        Log.d(TAG, "🗑️ Clear saved locations clicked")
                                        viewModel.clearSavedLocations()
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = ButtonDefaults. buttonColors(
                                        containerColor = MaterialTheme.colorScheme.secondary
                                    )
                                ) {
                                    Text("Clear saved locations")
                                }
                                
                                Spacer(modifier = Modifier.height(8.dp))
                            }
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
    
    // ✅ TimeZone - default din telefon (cu DST inclus)
    val defaultTimeZone = java.util.TimeZone.getDefault().getOffset(System.currentTimeMillis()) / (1000.0 * 60.0 * 60.0)
    var timeZone by rememberSaveable { mutableStateOf(String.format("%.1f", defaultTimeZone)) }
    
    // Validare
    val isValid = cityName.isNotBlank() && 
                  latitude.toDoubleOrNull() != null && 
                  longitude.toDoubleOrNull() != null &&
                  altitude.toDoubleOrNull() != null &&
                  timeZone.toDoubleOrNull() != null

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = MaterialTheme.shapes.large
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
                    fontWeight = FontWeight.Bold
                )
                
                // City Name
                OutlinedTextField(
                    value = cityName,
                    onValueChange = { cityName = it },
                    label = { Text("City Name") },
                    placeholder = { Text("e.g. Bucharest") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                
                // Latitude
                OutlinedTextField(
                    value = latitude,
                    onValueChange = { latitude = it },
                    label = { Text("Latitude") },
                    placeholder = { Text("e.g.44.4268") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
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
                    placeholder = { Text("e.g.26.1025") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    isError = longitude.isNotEmpty() && longitude.toDoubleOrNull() == null,
                    supportingText = {
                        if (longitude.isNotEmpty() && longitude.toDoubleOrNull() == null) {
                            Text("Enter a valid number")
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
                
                // ✅ Altitude și TimeZone pe același rând
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Altitude
                    OutlinedTextField(
                        value = altitude,
                        onValueChange = { altitude = it },
                        label = { Text("Alt (m)") },
                        placeholder = { Text("85") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        isError = altitude.isNotEmpty() && altitude.toDoubleOrNull() == null,
                        modifier = Modifier.weight(1f)
                    )
                    
                    // TimeZone
                    OutlinedTextField(
                        value = timeZone,
                        onValueChange = { timeZone = it },
                        label = { Text("UTC±") },
                        placeholder = { Text("2.0") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        isError = timeZone.isNotEmpty() && timeZone.toDoubleOrNull() == null,
                        supportingText = {
                            if (timeZone.isNotEmpty() && timeZone.toDoubleOrNull() == null) {
                                Text("Invalid")
                            }
                        },
                        modifier = Modifier.weight(1f)
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Butoane Cancel / Save
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                    Spacer(Modifier.width(8.dp))
                    Button(
                        onClick = {
                            val newLocation = LocationData(
                                id = 0,
                                name = cityName.trim(),
                                latitude = latitude.toDouble(),
                                longitude = longitude.toDouble(),
                                altitude = altitude.toDoubleOrNull() ?: 0.0,
                                timeZone = timeZone.toDoubleOrNull() ?: 2.0,  // ✅ Folosește valoarea introdusă
                                isCurrentLocation = false
                            )
                            onSave(newLocation)
                        },
                        enabled = isValid
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Check,
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
    onUseGPS:  (LocationData) -> Unit,
    onLoadGPS: () -> Unit,
    onRefresh:  () -> Unit  // ✅ ADĂUGAT
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
                .padding(16.dp)
        ) {
            // ✅ Header cu titlu și buton refresh
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "GPS Location",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement. spacedBy(8.dp)
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier. size(24.dp),
                            strokeWidth = 2.dp
                        )
                    }
                    
                    // ✅ Buton refresh GPS
                    if (currentGPSLocation != null && !isLoading) {
                        IconButton(
                            onClick = onRefresh,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled. Refresh,
                                contentDescription = "Refresh GPS location",
                                tint = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (currentGPSLocation != null) {
                Column(Modifier.fillMaxWidth()) {
                    InfoRow(label = "Lat:", value = String.format("%.4f°", currentGPSLocation.latitude))
                    Spacer(Modifier.height(4.dp))
                    InfoRow(label = "Lon:", value = String. format("%.4f°", currentGPSLocation.longitude))
                    Spacer(Modifier.height(4.dp))
                    InfoRow(label = "Alt:", value = "${currentGPSLocation.altitude. toInt()}m")

                    Spacer(Modifier.height(12.dp))

                    Button(
                        onClick = { onUseGPS(currentGPSLocation) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Icon(imageVector = Icons.Filled.Check, contentDescription = null, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Use this location")
                    }
                }
            } else {
                Button(
                    onClick = {
                        Log.d(TAG, "🔵 Get GPS Location button clicked")
                        onLoadGPS()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading
                ) {
                    Icon(imageVector = Icons.Filled. LocationOn, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Get GPS Location")
                }

                Spacer(Modifier.height(8.dp))

                Text(
                    text = "Tap to get current GPS coordinates",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer. copy(alpha = 0.7f)
                )
            }
        }
    }
}






		/**
		 * Item compact cu expand/collapse intern
		 * ✅ București nu poate fi șters (nu afișăm butonul Delete)
		 */
		@Composable
		private fun LocationItemCompact(
			location:  LocationData,
			onSelect: () -> Unit,
			onDelete: () -> Unit
		) {
			var expanded by rememberSaveable(location.id) { mutableStateOf(false) }
			
			// ✅ București nu poate fi șters
			val canDelete = location.name != "București"

			Log.d(TAG, "LocationItemCompact rendering:  ${location.name}, canDelete=$canDelete")

			Card(
				modifier = Modifier
					.fillMaxWidth()
					.animateContentSize(),
				colors = CardDefaults.cardColors(
					containerColor = MaterialTheme.colorScheme.surfaceVariant
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
							Log.d(TAG, "Expand/Collapse clicked:  ${location.name}")
							expanded = !expanded 
						}) {
							Icon(
								imageVector = if (expanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
								contentDescription = if (expanded) "Collapse" else "Expand"
							)
						}

						// ✅ Delete Button - doar dacă NU este București
						if (canDelete) {
							IconButton(
								onClick = {
									Log.d(TAG, "DELETE BUTTON CLICKED for:  ${location.name}")
									onDelete()
								},
								colors = IconButtonDefaults.iconButtonColors(
									contentColor = MaterialTheme.colorScheme.error
								)
							) {
								Icon(imageVector = Icons.Filled.Delete, contentDescription = "Delete location")
							}
						} else {
							// ✅ Placeholder pentru a păstra alinierea (sau poți să nu pui nimic)
							Spacer(modifier = Modifier.size(48.dp))
						}

						// Select Button
						TextButton(
							onClick = {
								Log.d(TAG, "SELECT BUTTON CLICKED for: ${location.name}")
								onSelect()
							},
							colors = ButtonDefaults.textButtonColors(
								contentColor = MaterialTheme.colorScheme.primary
							)
						) {
							Text("Select")
						}
					}

					// Detalii expandate
					if (expanded) {
						Column(
							modifier = Modifier
								.fillMaxWidth()
								.padding(horizontal = 16.dp, vertical = 8.dp)
						) {
							InfoRow(label = "Lat:", value = String.format("%.4f°", location.latitude))
							Spacer(Modifier.height(4.dp))
							InfoRow(label = "Lon:", value = String.format("%.4f°", location.longitude))
							Spacer(Modifier.height(4.dp))
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
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.Bold
        )
    }
}