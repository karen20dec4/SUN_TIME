package com.android.sun.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.android.sun.data.model.LocationData
import com.android.sun.data.repository.LocationRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

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
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // State pentru locația curentă GPS
    private val _currentGPSLocation = MutableStateFlow<LocationData?>(null)
    val currentGPSLocation: StateFlow<LocationData?> = _currentGPSLocation.asStateFlow()

    // State pentru loading
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // State pentru erori
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        loadGPSLocation()
    }

    /**
     * Încarcă locația GPS curentă
     */
    fun loadGPSLocation() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            try {
                val location = locationRepository.getCurrentLocation()
                _currentGPSLocation.value = location
            } catch (e: Exception) {
                _error.value = "Eroare la obținerea locației GPS: ${e.message}"
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Salvează o locație nouă
     */
    fun saveLocation(location: LocationData) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            try {
                locationRepository.saveLocation(location)
            } catch (e: Exception) {
                _error.value = "Eroare la salvarea locației: ${e.message}"
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Șterge o locație
     */
    fun deleteLocation(location: LocationData) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            try {
                locationRepository.deleteLocation(location)
            } catch (e: Exception) {
                _error.value = "Eroare la ștergerea locației: ${e.message}"
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
                locationRepository.updateLocation(location)
            } catch (e: Exception) {
                _error.value = "Eroare la actualizarea locației: ${e.message}"
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
        // Această verificare se face în repository
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