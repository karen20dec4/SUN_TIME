package com.android.sun.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx. lifecycle.viewModelScope
import com.android.sun. data.model.LocationData
import com.android.sun.data.repository.LocationPreferences
import com.android.sun.data. repository.LocationRepository
import kotlinx.coroutines.flow.*
import kotlinx. coroutines.launch

/**
 * ViewModel pentru gestionarea locațiilor
 */
class LocationViewModel(application: Application) : AndroidViewModel(application) {

    private val locationRepository = LocationRepository(application)
    private val locationPreferences = LocationPreferences(application)
    private val context = application.applicationContext

    // State pentru lista de locații
    val locations:  StateFlow<List<LocationData>> = locationRepository
        .getAllLocations()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // State pentru locația curentă GPS
    private val _currentGPSLocation = MutableStateFlow<LocationData?>(null)
    val currentGPSLocation: StateFlow<LocationData?> = _currentGPSLocation. asStateFlow()

    // State pentru loading
    private val _isLoading = MutableStateFlow(false)
    val isLoading:  StateFlow<Boolean> = _isLoading.asStateFlow()

    // State pentru erori
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        // ✅ La inițializare, încarcă ultima locație GPS salvată
        loadSavedGPSLocation()
        loadGPSLocation()
    }

    /**
     * Încarcă locația GPS curentă
     */
    fun loadGPSLocation() {
        android.util.Log. d("LocationViewModel", "🔵 loadGPSLocation() CALLED")
        
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            android.util.Log. d("LocationViewModel", "🔵 Starting GPS location request...")
            
            try {
                val location = locationRepository.getCurrentLocation()
                android.util.Log. d("LocationViewModel", "🔵 GPS result: $location")
                
                if (location != null) {
                    _currentGPSLocation.value = location
                    // ✅ Salvează locația GPS pentru utilizare ulterioară
                    saveGPSLocationToPrefs(location)
                    android.util. Log.d("LocationViewModel", "✅ GPS Location set and saved:  ${location.latitude}, ${location.longitude}")
                } else {
                    // ✅ GPS nu e disponibil - încercăm să folosim ultima locație salvată
                    if (_currentGPSLocation.value == null) {
                        loadSavedGPSLocation()
                        if (_currentGPSLocation.value != null) {
                            android.util.Log.d("LocationViewModel", "✅ Using saved GPS location")
                        } else {
                            _error.value = "Could not get GPS location.  Check permissions."
                            android. util.Log.e("LocationViewModel", "❌ GPS returned null and no saved location")
                        }
                    }
                }
            } catch (e: Exception) {
                // ✅ La eroare, încercăm să folosim ultima locație salvată
                if (_currentGPSLocation.value == null) {
                    loadSavedGPSLocation()
                    if (_currentGPSLocation.value != null) {
                        android.util.Log. d("LocationViewModel", "✅ GPS failed, using saved location")
                    } else {
                        _error.value = "GPS Error: ${e.message}"
                        android.util.Log. e("LocationViewModel", "❌ GPS Exception: ${e.message}")
                    }
                }
                e.printStackTrace()
            } finally {
                _isLoading.value = false
                android.util.Log.d("LocationViewModel", "🔵 GPS request finished")
            }
        }
    }

    /**
     * ✅ Salvează locația GPS în SharedPreferences
     */
    private fun saveGPSLocationToPrefs(location: LocationData) {
        try {
            val prefs = context.getSharedPreferences("sun_gps_prefs", Context.MODE_PRIVATE)
            prefs.edit()
                .putFloat("gps_latitude", location.latitude. toFloat())
                .putFloat("gps_longitude", location.longitude.toFloat())
                .putFloat("gps_altitude", location.altitude.toFloat())
                .putFloat("gps_timezone", location.timeZone.toFloat())
                .putLong("gps_timestamp", System.currentTimeMillis())
                .apply()
            
            android.util. Log.d("LocationViewModel", "✅ GPS location saved to prefs:  ${location.latitude}, ${location.longitude}")
        } catch (e: Exception) {
            android.util.Log.e("LocationViewModel", "❌ Error saving GPS to prefs: ${e.message}")
        }
    }

    /**
     * ✅ Încarcă ultima locație GPS din SharedPreferences
     */
    private fun loadSavedGPSLocation() {
        try {
            val prefs = context.getSharedPreferences("sun_gps_prefs", Context.MODE_PRIVATE)
            val latitude = prefs.getFloat("gps_latitude", 0f)
            val longitude = prefs.getFloat("gps_longitude", 0f)
            
            if (latitude != 0f && longitude != 0f) {
                val savedLocation = LocationData(
                    id = -1,
                    name = "GPS",
                    latitude = latitude. toDouble(),
                    longitude = longitude.toDouble(),
                    altitude = prefs.getFloat("gps_altitude", 0f).toDouble(),
                    timeZone = prefs.getFloat("gps_timezone", 2f).toDouble(),
                    isCurrentLocation = true
                )
                _currentGPSLocation.value = savedLocation
                
                val timestamp = prefs.getLong("gps_timestamp", 0)
                val ageMinutes = (System. currentTimeMillis() - timestamp) / 60000
                android.util.Log. d("LocationViewModel", "✅ Loaded saved GPS location (${ageMinutes} min old): ${savedLocation.latitude}, ${savedLocation. longitude}")
            } else {
                android.util.Log. d("LocationViewModel", "ℹ️ No saved GPS location found")
            }
        } catch (e: Exception) {
            android.util. Log.e("LocationViewModel", "❌ Error loading saved GPS:  ${e.message}")
        }
    }

    /**
     * Salvează o locație nouă
     */
    fun saveLocation(location:  LocationData) {
        viewModelScope. launch {
            _isLoading.value = true
            _error.value = null
            
            try {
                locationRepository.saveLocation(location)
            } catch (e:  Exception) {
                _error.value = "Error saving location: ${e.message}"
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * ✅ Șterge o locație
     * Dacă locația ștearsă este cea curent selectată, resetează la București
     */
    fun deleteLocation(location: LocationData) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            try {
                // ✅ Verifică dacă locația ștearsă este cea curent selectată
                val currentSavedName = locationPreferences.getSavedLocationName()
                val isCurrentLocation = currentSavedName == location.name
                
                // Șterge locația din DB
                locationRepository.deleteLocation(location)
                
                // ✅ Dacă am șters locația curentă, resetează la București
                if (isCurrentLocation) {
                    android.util.Log.d("LocationViewModel", "⚠️ Deleted current location '${location. name}', resetting to București")
                    
                    // Salvează București ca locație curentă
                    locationPreferences. saveSelectedLocation(
                        id = 0,
                        name = "București",
                        latitude = 44.4268,
                        longitude = 26.1025,
                        altitude = 80.0,
                        timeZone = 2.0,
                        isGPS = false
                    )
                }
                
            } catch (e: Exception) {
                _error.value = "Error deleting location:  ${e.message}"
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Actualizează o locație
     */
    fun updateLocation(location: LocationData) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            try {
                locationRepository. updateLocation(location)
            } catch (e: Exception) {
                _error. value = "Error updating location: ${e. message}"
                e.printStackTrace()
            } finally {
                _isLoading. value = false
            }
        }
    }

    /**
     * ✅ Încarcă locațiile predefinite (toate orașele din România)
     */
    fun loadDefaultLocations() {
        android.util. Log.d("LocationViewModel", "🔵 loadDefaultLocations() CALLED")
        
        viewModelScope.launch {
            _isLoading.value = true
            _error. value = null
            
            try {
                locationRepository.loadDefaultLocations()
                android.util. Log.d("LocationViewModel", "✅ Default locations loaded")
            } catch (e: Exception) {
                _error. value = "Error loading defaults: ${e. message}"
                android.util.Log. e("LocationViewModel", "❌ Error:  ${e.message}")
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    
	
	 /**
     * ✅ Șterge toate locațiile salvate, păstrând doar București
     */
    fun clearSavedLocations() {
        android.util.Log. d("LocationViewModel", "🗑️ clearSavedLocations() CALLED")
        
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            try {
                locationRepository.clearSavedLocations()
                android.util.Log. d("LocationViewModel", "✅ Saved locations cleared")
            } catch (e: Exception) {
                _error.value = "Error clearing locations: ${e.message}"
                e.printStackTrace()
            } finally {
                _isLoading. value = false
            }
        }
    }
	
	
	
	
	
	
	
	
	
	
	/**
     * Verifică dacă are permisiuni GPS
     */
    fun hasLocationPermission(): Boolean {
        return try {
            _currentGPSLocation.value != null
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Curăță eroarea
     */
    fun clearError() {
        _error. value = null
    }
}