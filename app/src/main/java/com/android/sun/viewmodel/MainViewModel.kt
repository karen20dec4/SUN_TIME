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
				
				android.util.Log.d("MainViewModel", "───────────────────────────────────────")
				android.util.Log. d("MainViewModel", "🔵 calculateAstroData() START")
				android.util.Log.d("MainViewModel", "🔵 Using location: ${location.name}")
				android.util.Log. d("MainViewModel", "🔵   Lat: ${location.latitude}, Lon: ${location.longitude}")
				android.util.Log. d("MainViewModel", "🔵   TimeZone: ${location.timeZone}")
				
				val data = astroRepository.calculateAstroData(
					latitude = location.latitude,
					longitude = location.longitude,
					timeZone = location.timeZone,
					locationName = location.name,
					isGPSLocation = location.isCurrentLocation
				)
				_astroData.value = data
				
				android.util.Log.d("MainViewModel", "✅ Calculation completed!")
				android.util.Log. d("MainViewModel", "✅ Result: sunrise=${data.sunriseFormatted}")
				android.util.Log. d("MainViewModel", "✅ Location in result: ${data.locationName}")
				
				// ✅ ADAUGĂ: Trimite broadcast DUPĂ calculul complet
				try {
					val intent = android. content.Intent("com.android. sun.LOCATION_CHANGED")
					getApplication<Application>().sendBroadcast(intent)
					android.util. Log.d("MainViewModel", "📍 Broadcast sent after calculation complete")
				} catch (e:  Exception) {
					android.util.Log.e("MainViewModel", "Error sending broadcast", e)
				}
				
				android.util.Log.d("MainViewModel", "───────────────────────────────────────")
			} catch (e: Exception) {
				_error.value = "Error:  ${e.message}"
				android.util.Log.e("MainViewModel", "❌ Error:  ${e.message}")
				e.printStackTrace()
			}
			
			_isLoading.value = false
			android.util.Log.d("MainViewModel", "🔵 isLoading set to false")
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
		android.util.Log.d("MainViewModel", "═══════════════════════════════════════")
		android.util.Log.d("MainViewModel", "🟢 setLocation() called")
		android.util.Log.d("MainViewModel", "🟢 NEW Location: ${location.name}")
		android.util.Log.d("MainViewModel", "🟢   Lat: ${location.latitude}, Lon: ${location.longitude}")
		android.util.Log.d("MainViewModel", "🟢   TimeZone: ${location.timeZone}")
		android.util.Log.d("MainViewModel", "🟢   IsGPS: ${location.isCurrentLocation}")
		
		val oldLocation = _currentLocation.value
		android.util.Log.d("MainViewModel", "🟡 OLD Location: ${oldLocation. name}")
		
		_currentLocation.value = location
		
		android.util.Log. d("MainViewModel", "✅ _currentLocation updated to: ${_currentLocation.value.name}")
		
		// Salvează în SharedPreferences
		locationPreferences.saveSelectedLocation(
			id = location.id,
			name = location.name,
			latitude = location.latitude,
			longitude = location.longitude,
			altitude = location.altitude,
			timeZone = location.timeZone,
			isGPS = location.isCurrentLocation
		)
		
		android.util.Log.d("MainViewModel", "✅ Location saved to SharedPreferences")
		android.util.Log.d("MainViewModel", "🟢 Calling calculateAstroData()...")
		calculateAstroData()
		android.util.Log.d("MainViewModel", "═══════════════════════════════════════")
	}
	
	
	
	
	
	
	
    /**
	 * ✅ Actualizează manual datele
	 * ✅ FIX: Verifică dacă locația curentă încă există în DB
	 */
	fun refresh() {
		android.util. Log.d("MainViewModel", "═══════════════════════════════════════")
		android.util.Log.d("MainViewModel", "🔵 refresh() called")
		
		val currentLoc = _currentLocation.value
		android.util.Log. d("MainViewModel", "🔵 Current location in memory: ${currentLoc.name}")
		android.util.Log.d("MainViewModel", "🔵   Lat: ${currentLoc. latitude}, Lon: ${currentLoc.longitude}")
		android.util.Log.d("MainViewModel", "🔵   TimeZone: ${currentLoc.timeZone}")
		android.util.Log.d("MainViewModel", "🔵   IsGPS: ${currentLoc.isCurrentLocation}")
		
		viewModelScope.launch {
			// ✅ Dacă e locație GPS, folosește-o direct
			if (currentLoc. isCurrentLocation) {
				android.util.Log.d("MainViewModel", "✅ GPS location, using directly")
				calculateAstroData()
				android.util.Log.d("MainViewModel", "═══════════════════════════════════════")
				return@launch
			}
			
			// ✅ Verifică dacă locația încă există în DB
			android.util.Log.d("MainViewModel", "🔍 Checking if location exists in DB...")
			val existsInDB = locationRepository.locationExistsByName(currentLoc.name)
			android.util.Log.d("MainViewModel", "🔍 Location '${currentLoc.name}' exists in DB: $existsInDB")
			
			if (existsInDB) {
				android.util.Log.d("MainViewModel", "✅ Location exists, calculating astro data...")
				calculateAstroData()
			} else {
				android.util.Log.w("MainViewModel", "⚠️ Location '${currentLoc.name}' not found in DB!")
				android.util.Log.w("MainViewModel", "⚠️ Resetting to București...")
				resetToDefaultLocation()
				calculateAstroData()
			}
			
			android.util.Log. d("MainViewModel", "═══════════════════════════════════════")
		}
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