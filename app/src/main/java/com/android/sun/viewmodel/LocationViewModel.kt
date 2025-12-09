package com.android.sun.viewmodel

import android.app.Application
import androidx.lifecycle. AndroidViewModel
import androidx. lifecycle.viewModelScope
import com.android.sun. data.model.LocationData
import com.android.sun.data. repository.LocationRepository
import kotlinx.coroutines. flow.*
import kotlinx. coroutines.launch

/**
 * ViewModel pentru gestionarea locațiilor
 */
class LocationViewModel(application: Application) : AndroidViewModel(application) {

    private val locationRepository = LocationRepository(application)

    // State pentru lista de locații
    val locations: StateFlow<List<LocationData>> = locationRepository
        .getAllLocations()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted. WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // State pentru locația curentă GPS
    private val _currentGPSLocation = MutableStateFlow<LocationData?>(null)
    val currentGPSLocation: StateFlow<LocationData?> = _currentGPSLocation. asStateFlow()

    // State pentru loading
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // State pentru erori
    private val _error = MutableStateFlow<String? >(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
		
		// ✅ Asigură-te că București există mereu
		viewModelScope.launch {
			try {
				locationRepository.ensureBucharestExists()
			} catch (e: Exception) {
				android.util.Log. e("LocationViewModel", "Error ensuring București exists: ${e. message}")
			}
		}
	
        loadGPSLocation()
    }

    /**
     * Încarcă locația GPS curentă
     */
    fun loadGPSLocation() {
        android.util.Log. d("LocationViewModel", "🔵 loadGPSLocation() CALLED")
        
        viewModelScope. launch {
            _isLoading.value = true
            _error.value = null
            
            android.util.Log. d("LocationViewModel", "🔵 Starting GPS location request...")
            
            try {
                val location = locationRepository.getCurrentLocation()
                android.util.Log. d("LocationViewModel", "🔵 GPS result: $location")
                
                if (location != null) {
                    _currentGPSLocation.value = location
                    android.util.Log.d("LocationViewModel", "✅ GPS Location set: ${location.latitude}, ${location.longitude}")
                } else {
                    _error.value = "Could not get GPS location.  Check permissions."
                    android. util.Log.e("LocationViewModel", "❌ GPS returned null")
                }
            } catch (e: Exception) {
                _error.value = "GPS Error: ${e.message}"
                android. util.Log.e("LocationViewModel", "❌ GPS Exception: ${e.message}")
                e.printStackTrace()
            } finally {
                _isLoading.value = false
                android.util.Log.d("LocationViewModel", "🔵 GPS request finished")
            }
        }
    }

    /**
     * Salvează o locație nouă
     */
    fun saveLocation(location: LocationData) {
        viewModelScope.launch {
            _isLoading.value = true
            _error. value = null
            
            try {
                locationRepository.saveLocation(location)
            } catch (e: Exception) {
                _error.value = "Error saving location: ${e.message}"
                e.printStackTrace()
            } finally {
                _isLoading. value = false
            }
        }
    }

    /**
     * Șterge o locație
     */
    fun deleteLocation(location: LocationData) {
        viewModelScope.launch {
            _isLoading. value = true
            _error.value = null
            
            try {
                locationRepository.deleteLocation(location)
            } catch (e: Exception) {
                _error.value = "Error deleting location: ${e.message}"
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
        viewModelScope. launch {
            _isLoading.value = true
            _error.value = null
            
            try {
                locationRepository.updateLocation(location)
            } catch (e: Exception) {
                _error.value = "Error updating location: ${e.message}"
                e.printStackTrace()
            } finally {
                _isLoading.value = false
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
                android.util.Log. d("LocationViewModel", "✅ Default locations loaded")
            } catch (e: Exception) {
                _error.value = "Error loading defaults: ${e.message}"
                android.util.Log. e("LocationViewModel", "❌ Error: ${e.message}")
                e.printStackTrace()
            } finally {
                _isLoading.value = false
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
        _error.value = null
    }
}