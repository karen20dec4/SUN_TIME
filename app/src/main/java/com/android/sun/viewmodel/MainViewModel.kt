package com.android.sun.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.android.sun.data.model.AstroData
import com.android.sun.data.model. LocationData
import com.android.sun.data.repository.AstroRepository
import com.android.sun.data.repository.LocationPreferences
import com.android.sun.data.repository.LocationRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.*

/**
 * ViewModel pentru ecranul principal
 */
class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val astroRepository = AstroRepository(application)
    private val locationRepository = LocationRepository(application)
    private val locationPreferences = LocationPreferences(application)

    // State pentru date astrologice
    private val _astroData = MutableStateFlow<AstroData?>(null)
    val astroData: StateFlow<AstroData?> = _astroData.asStateFlow()

    // State pentru loading
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // State pentru erori
    private val _error = MutableStateFlow<String? >(null)
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
        android.util.Log. d("MainViewModel", "🔵 init called")
        
        // ✅ Încarcă locația salvată SINCRON (fără coroutine)
        loadSavedLocationSync()
        
        // ✅ Calculează datele astro
        calculateAstroData()
        
        // ✅ Pornește actualizările
        startRealtimeUpdates()
    }

    /**
     * ✅ Încarcă locația salvată SINCRON din SharedPreferences
     * Nu verifică DB-ul aici - verificarea se face în reloadSavedLocation()
     */
    private fun loadSavedLocationSync() {
        android.util.Log.d("MainViewModel", "🔵 loadSavedLocationSync() called")
        
        if (locationPreferences. hasSavedLocation()) {
            val savedLocation = LocationData(
                id = locationPreferences. getSavedLocationId(),
                name = locationPreferences.getSavedLocationName(),
                latitude = locationPreferences.getSavedLatitude(),
                longitude = locationPreferences. getSavedLongitude(),
                altitude = locationPreferences. getSavedAltitude(),
                timeZone = locationPreferences.getSavedTimeZone(),
                isCurrentLocation = locationPreferences.isSavedLocationGPS()
            )
            
            android.util.Log. d("MainViewModel", "✅ Loaded saved location: ${savedLocation.name} (${savedLocation.latitude}, ${savedLocation.longitude})")
            _currentLocation.value = savedLocation
        } else {
            android.util.Log. d("MainViewModel", "⚠️ No saved location, using București")
            _currentLocation.value = getDefaultLocation()
        }
    }

    
	
		/**
		 * ✅ Reîncarcă locația salvată (apelat când revii la ecranul principal)
		 */
		fun reloadSavedLocation() {
			android.util.Log.d("MainViewModel", "🔵 reloadSavedLocation() called")
			
			viewModelScope. launch {
				val oldLocation = _currentLocation. value
				
				if (locationPreferences. hasSavedLocation()) {
					val savedName = locationPreferences.getSavedLocationName()
					val savedIsGPS = locationPreferences. isSavedLocationGPS()
					
					if (savedIsGPS) {
						// GPS location
						_currentLocation.value = LocationData(
							id = locationPreferences.getSavedLocationId(),
							name = savedName,
							latitude = locationPreferences. getSavedLatitude(),
							longitude = locationPreferences. getSavedLongitude(),
							altitude = locationPreferences.getSavedAltitude(),
							timeZone = locationPreferences.getSavedTimeZone(),
							isCurrentLocation = true
						)
					} else {
						// Verifică DB
						val existsInDB = locationRepository.locationExistsByName(savedName)
						
						if (existsInDB) {
							_currentLocation.value = LocationData(
								id = locationPreferences.getSavedLocationId(),
								name = savedName,
								latitude = locationPreferences. getSavedLatitude(),
								longitude = locationPreferences. getSavedLongitude(),
								altitude = locationPreferences.getSavedAltitude(),
								timeZone = locationPreferences.getSavedTimeZone(),
								isCurrentLocation = false
							)
						} else {
							android.util.Log. w("MainViewModel", "⚠️ '$savedName' not in DB, resetting")
							resetToDefaultLocation()
						}
					}
				} else {
					_currentLocation.value = getDefaultLocation()
				}
				
				// ✅ Recalculează dacă locația s-a schimbat
				val newLocation = _currentLocation.value
				if (oldLocation. name != newLocation. name || 
					oldLocation. latitude != newLocation. latitude ||
					oldLocation. longitude != newLocation. longitude) {
					android.util.Log. d("MainViewModel", "🔄 Location changed, recalculating...")
					calculateAstroData()
				}
			}
		}


	/**
	 * ✅ Resetează la București și salvează în preferences
	 */
	private fun resetToDefaultLocation() {
		val defaultLocation = getDefaultLocation()
		_currentLocation.value = defaultLocation
		
		locationPreferences.saveSelectedLocation(
			id = 0,
			name = defaultLocation.name,
			latitude = defaultLocation.latitude,
			longitude = defaultLocation. longitude,
			altitude = defaultLocation.altitude,
			timeZone = defaultLocation. timeZone,
			isGPS = false
		)
		
		android.util.Log.d("MainViewModel", "✅ Reset to București and saved to preferences")
	}




	/**
	 * Calculează datele astrologice pentru locația curentă
	 */
	fun calculateAstroData() {
		viewModelScope.launch {
			_isLoading.value = true
			_error.value = null
			
			try {
				val location = _currentLocation.value
				
				android.util.Log.d("MainViewModel", "🔵 Calculating for:  ${location.name} (${location.latitude}, ${location.longitude})")
				
				val data = astroRepository.calculateAstroData(
					latitude = location.latitude,
					longitude = location.longitude,
					timeZone = location.timeZone,
					locationName = location.name,
					isGPSLocation = location.isCurrentLocation
				)
				_astroData.value = data
				
				android.util.Log. d("MainViewModel", "✅ Done:  sunrise=${data.sunriseFormatted}")
			} catch (e:  Exception) {
				_error.value = "Error: ${e.message}"
				android. util.Log.e("MainViewModel", "❌ Error:  ${e.message}")
				e.printStackTrace()
			}
			
			// ✅ ÎNTOTDEAUNA setează loading = false, chiar și după eroare
			_isLoading.value = false
			android.util.Log. d("MainViewModel", "🔵 isLoading set to false")
		}
	}





    /**
     * Pornește actualizările în timp real
     */
    fun startRealtimeUpdates() {
        updateJob?.cancel()
        updateJob = viewModelScope.launch {
            var lastTattvaTime:  Calendar? = null
            var lastSubTattvaTime: Calendar?  = null
            
            while (true) {
                delay(1000)
                
                val currentData = _astroData.value
                
                if (currentData != null) {
                    val currentTime = Calendar.getInstance()
                    
                    val tattvaChanged = lastTattvaTime == null || 
                        currentTime.timeInMillis >= currentData.tattva.endTime. timeInMillis
                    
                    val subTattvaChanged = lastSubTattvaTime == null || 
                        currentTime.timeInMillis >= currentData.subTattva.endTime. timeInMillis
                    
                    if (tattvaChanged || subTattvaChanged) {
                        calculateAstroData()
                        lastTattvaTime = currentTime
                        lastSubTattvaTime = currentTime
                    }
                }
            }
        }
    }

    /**
     * Oprește actualizările în timp real
     */
    fun stopRealtimeUpdates() {
        updateJob?.cancel()
        updateJob = null
    }

    /**
     * Schimbă modul de afișare (cod/nume)
     */
    fun toggleCodeMode() {
        _codeMode.value = !_codeMode.value
    }

    /**
     * Setează o nouă locație și o salvează în SharedPreferences
     */
    fun setLocation(location: LocationData) {
        android.util.Log.d("MainViewModel", "🔵 setLocation: ${location.name} (${location.latitude}, ${location.longitude})")
        
        _currentLocation.value = location
        
        // Salvează în SharedPreferences
        locationPreferences.saveSelectedLocation(
            id = location.id,
            name = location. name,
            latitude = location.latitude,
            longitude = location.longitude,
            altitude = location.altitude,
            timeZone = location.timeZone,
            isGPS = location.isCurrentLocation
        )
        
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
            altitude = 80.0,
            timeZone = 2.0,
            isCurrentLocation = false
        )
    }

    override fun onCleared() {
        super.onCleared()
        stopRealtimeUpdates()
    }
}