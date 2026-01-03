package com.android.sun.data.repository

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import androidx.core.content.ContextCompat
import com.android.sun.data.database.PlaceDao
import com.android.sun.data.database.PlaceDatabase
import com.android.sun.data.database.PlaceEntity
import com.android.sun.data.model.LocationData
import com.android.sun.domain.calculator.Supplement
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * Repository pentru gestionarea locațiilor
 * Accesează baza de date și serviciile GPS
 */
class LocationRepository(private val context: Context) {

    private val supplement = Supplement()
    private val placeDao: PlaceDao = PlaceDatabase.getDatabase(context).placeDao()
    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    /**
     * Obține toate locațiile salvate din baza de date
     */
    fun getAllLocations(): Flow<List<LocationData>> {
        return placeDao.getAllPlaces().map { places ->
            places.map { placeEntityToLocationData(it) }
        }
    }

    /**
     * Salvează o locație nouă în baza de date
     */
    suspend fun saveLocation(location: LocationData) = withContext(Dispatchers.IO) {
        val entity = locationDataToPlace(location)
        placeDao.insertPlace(entity)
    }

    /**
     * Șterge o locație din baza de date
     */
    suspend fun deleteLocation(location: LocationData) = withContext(Dispatchers.IO) {
        val entity = locationDataToPlace(location)
        placeDao.deletePlace(entity)
    }

    /**
     * Actualizează o locație existentă
     */
    suspend fun updateLocation(location: LocationData) = withContext(Dispatchers.IO) {
        val entity = locationDataToPlace(location)
        placeDao.updatePlace(entity)
    }
	
	/**
	 * ✅ Verifică dacă București există în DB, dacă nu, îl adaugă
	 * Apelat la pornirea aplicației pentru a garanta că avem mereu o locație default
	 */
	suspend fun ensureBucharestExists() = withContext(Dispatchers.IO) {
		val bucharest = placeDao.getPlaceByName("București")
		if (bucharest == null) {
			android.util.Log.d("LocationRepository", "⚠️ București not found, adding it...")
			placeDao.insertPlace(
				PlaceEntity(
					name = "București",
					longitude = 26.1025,
					latitude = 44.4268,
					altitude = 80.0,
					timeZone = 2.0,
					dst = 0
				)
			)
			android.util.Log.d("LocationRepository", "✅ București added")
		}
	}
	
	
	
	/**
	 * ✅ Verifică dacă o locație cu acest nume există în DB
	 */
	suspend fun locationExistsByName(name: String): Boolean = withContext(Dispatchers.IO) {
		val place = placeDao.getPlaceByName(name)
		val exists = place != null
		android.util.Log.d("LocationRepository", "🔍 Location '$name' exists:  $exists")
		exists
	}
	
	
	
	
	
	
	
	
	
        /**
     * ✅ Încarcă DOAR București ca locație default
     * Apelat la prima pornire a aplicației
     */
    suspend fun loadDefaultLocations() = withContext(Dispatchers.IO) {
        android.util.Log. d("LocationRepository", "🔵 Loading default location (București only)...")
        
        // Verifică dacă București există deja
        val bucharest = placeDao.getPlaceByName("București")
        if (bucharest == null) {
            placeDao.insertPlace(
                PlaceEntity(
                    name = "București",
                    longitude = 26.1025,
                    latitude = 44.4268,
                    altitude = 80.0,
                    timeZone = 2.0,
                    dst = 0
                )
            )
            android.util.Log.d("LocationRepository", "✅ București added as default")
        } else {
            android.util.Log. d("LocationRepository", "✅ București already exists")
        }
    }

    /**
     * ✅ Șterge toate locațiile salvate, păstrând DOAR București
     * Apelat când utilizatorul apasă "Clear saved locations"
     */
    suspend fun clearSavedLocations() = withContext(Dispatchers.IO) {
        android.util.Log. d("LocationRepository", "🗑️ Clearing all saved locations...")
        
        // Șterge TOATE locațiile
        placeDao. deleteAllPlaces()
        
        // Adaugă înapoi doar București
        placeDao.insertPlace(
            PlaceEntity(
                name = "București",
                longitude = 26.1025,
                latitude = 44.4268,
                altitude = 80.0,
                timeZone = 2.0,
                dst = 0
            )
        )
        
        android.util.Log.d("LocationRepository", "✅ Cleared!  Only București remains")
    }

    
	
	
	
	
	
	
	
	/**
     * Obține locația curentă de la GPS
     */
    suspend fun getCurrentLocation(): LocationData? = withContext(Dispatchers.IO) {
        android.util.Log.d("LocationRepository", "🟡 getCurrentLocation() called")
        
        if (! hasLocationPermission()) {
            android.util.Log.e("LocationRepository", "❌ No location permission!")
            return@withContext null
        }
        
        android.util.Log.d("LocationRepository", "✅ Permission OK, requesting GPS...")

        try {
            val location = getCurrentLocationInternal()
            android.util.Log.d("LocationRepository", "✅ Got GPS: ${location.latitude}, ${location.longitude}")
            
            LocationData(
                id = 0,
                name = "GPS",
                latitude = location.latitude,
                longitude = location.longitude,
                altitude = location.altitude,
                timeZone = getTimeZoneOffset(),
                isCurrentLocation = true
            )
        } catch (e: Exception) {
            android.util.Log.e("LocationRepository", "❌ GPS Exception: ${e.message}")
            e.printStackTrace()
            null
        }
    }

    /**
     * Verifică dacă aplicația are permisiune de locație
     */
    private fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED ||
        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    /**
     * Obține locația GPS actuală (implementare internă)
     */
    private suspend fun getCurrentLocationInternal(): Location =
        suspendCancellableCoroutine { continuation ->
            val cancellationTokenSource = CancellationTokenSource()

            try {
                fusedLocationClient.getCurrentLocation(
                    Priority.PRIORITY_HIGH_ACCURACY,
                    cancellationTokenSource.token
                ).addOnSuccessListener { location ->
                    if (location != null) {
                        continuation.resume(location)
                    } else {
                        continuation.resumeWithException(
                            Exception("Nu s-a putut obține locația GPS")
                        )
                    }
                }.addOnFailureListener { exception ->
                    continuation.resumeWithException(exception)
                }
            } catch (e: SecurityException) {
                continuation.resumeWithException(e)
            }

            continuation.invokeOnCancellation {
                cancellationTokenSource.cancel()
            }
        }

    /**
     * Calculează offset-ul timezone-ului local (ore față de UTC)
     */
    private fun getTimeZoneOffset(): Double {
        val timeZone = java.util.TimeZone.getDefault()
        val offsetMillis = timeZone.rawOffset
        return offsetMillis / (1000.0 * 60.0 * 60.0)
    }

    /**
     * Convertește PlaceEntity (din DB) în LocationData
     */
    private fun placeEntityToLocationData(place: PlaceEntity): LocationData {
        return LocationData(
            id = place.id,
            name = place.name,
            longitude = place.longitude,
            latitude = place.latitude,
            altitude = place.altitude,
            timeZone = place.timeZone,
            isCurrentLocation = false
        )
    }

    /**
     * Convertește LocationData în PlaceEntity (pentru DB)
     */
    private fun locationDataToPlace(location: LocationData): PlaceEntity {
        return PlaceEntity(
            id = location.id,
            name = location.name,
            longitude = location.longitude,
            latitude = location.latitude,
            timeZone = location.timeZone,
            altitude = location.altitude,
            dst = 0
        )
    }
}