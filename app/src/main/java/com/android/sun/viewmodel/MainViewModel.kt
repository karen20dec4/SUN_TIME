package com.android.sun.viewmodel

import android.app.Application
import androidx.lifecycle. AndroidViewModel
import androidx.lifecycle.viewModelScope
import com. android.sun.data.model.AstroData
import com.android.sun. data.model.LocationData
import com.android.sun.data.repository.AstroRepository
import com.android.sun.data.repository.LocationRepository
import kotlinx.coroutines.Job
import kotlinx. coroutines.delay
import kotlinx. coroutines.flow.MutableStateFlow
import kotlinx. coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.*

/**
 * ViewModel pentru ecranul principal
 */
class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val astroRepository = AstroRepository(application)
    private val locationRepository = LocationRepository(application)

    // State pentru date astrologice
    private val _astroData = MutableStateFlow<AstroData?>(null)
    val astroData: StateFlow<AstroData?> = _astroData.asStateFlow()

    // State pentru loading
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // State pentru erori
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    // State pentru locația curentă
    private val _currentLocation = MutableStateFlow(getDefaultLocation())
    val currentLocation: StateFlow<LocationData> = _currentLocation. asStateFlow()

    // State pentru code mode
    private val _codeMode = MutableStateFlow(false)
    val codeMode: StateFlow<Boolean> = _codeMode.asStateFlow()

    // Job pentru actualizare automată
    private var updateJob: Job?  = null

    init {
        // Încarcă datele inițiale
        loadCurrentLocation()
        startRealtimeUpdates()
    }

    /**
     * Încarcă locația curentă și calculează datele
     */
    private fun loadCurrentLocation() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Încearcă să obții locația GPS
                val gpsLocation = locationRepository.getCurrentLocation()
                if (gpsLocation != null) {
                    _currentLocation.value = gpsLocation
                }
                
                // Calculează datele astrologice
                calculateAstroData()
            } catch (e: Exception) {
                _error. value = "Eroare la încărcarea locației: ${e.message}"
                e.printStackTrace()
            } finally {
                _isLoading. value = false
            }
        }
    }

    /**
	 * Calculează datele astrologice pentru locația curentă
	 * ✅ Dacă nu există locație validă, folosește București ca default
	 */
	fun calculateAstroData() {
		viewModelScope.launch {
			_isLoading.value = true
			_error.value = null
			
			try {
				var location = _currentLocation.value
				
				// ✅ Verifică dacă locația este validă (coordonate non-zero)
				if (location.latitude == 0.0 && location.longitude == 0.0) {
					android.util.Log. w("MainViewModel", "⚠️ Invalid location, using București as default")
					location = getDefaultLocation()
					_currentLocation.value = location
				}
				
				val data = astroRepository.calculateAstroData(
					latitude = location.latitude,
					longitude = location.longitude,
					timeZone = location. timeZone,
					locationName = location.name,
					isGPSLocation = location.isCurrentLocation
				)
				_astroData.value = data
			} catch (e:  Exception) {
				_error.value = "Eroare la calcularea datelor:  ${e.message}"
				android.util.Log. e("MainViewModel", "❌ Error:  ${e.message}")
				e.printStackTrace()
			} finally {
				_isLoading. value = false
			}
		}
	}
	
	
	
	
	

    /**
     * ✅ OPTIMIZAT: Pornește actualizările în timp real
     * Recalculează DOAR când se schimbă Tattva/SubTattva, NU la fiecare secundă!
     */
    fun startRealtimeUpdates() {
        updateJob?.cancel()
        updateJob = viewModelScope.launch {
            var lastTattvaTime: Calendar?  = null
            var lastSubTattvaTime: Calendar? = null
            
            while (true) {
                delay(1000) // Verifică la fiecare secundă
                
                val currentData = _astroData. value
                
                if (currentData != null) {
                    val currentTime = Calendar.getInstance()
                    
                    // ✅ Recalculează DOAR dacă Tattva sau SubTattva s-a schimbat
                    val tattvaChanged = lastTattvaTime == null || 
                        currentTime.timeInMillis >= currentData.tattva.endTime. timeInMillis
                    
                    val subTattvaChanged = lastSubTattvaTime == null || 
                        currentTime.timeInMillis >= currentData.subTattva.endTime.timeInMillis
                    
                    if (tattvaChanged || subTattvaChanged) {
                        // Doar când se schimbă Tattva/SubTattva, recalculăm
                        calculateAstroData()
                        lastTattvaTime = currentTime
                        lastSubTattvaTime = currentTime
                    }
                    // Altfel, NU facem nimic!  Timpul rămas se actualizează automat în UI
                }
            }
        }
    }

    /**
     * Oprește actualizările în timp real
     */
    fun stopRealtimeUpdates() {
        updateJob?. cancel()
        updateJob = null
    }

    /**
     * Schimbă modul de afișare (cod/nume)
     */
    fun toggleCodeMode() {
        _codeMode.value = !_codeMode.value
    }

    /**
     * Setează o nouă locație
     */
    fun setLocation(location: LocationData) {
        _currentLocation.value = location
        calculateAstroData()
    }

    /**
     * Actualizează manual datele
     */
    fun refresh() {
        calculateAstroData()
    }

    /**
     * Obține locația default (București)
     */
    private fun getDefaultLocation(): LocationData {
        return LocationData(
            id = 0,
            name = "București",
            latitude = 44.4268,
            longitude = 26.1025,
            timeZone = 2.0,
            isCurrentLocation = false
        )
    }

    override fun onCleared() {
        super. onCleared()
        stopRealtimeUpdates()
    }
}