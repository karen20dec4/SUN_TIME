package com.android.sun.data. repository

import android. Manifest
import android. content.Context
import android.content.pm. PackageManager
import android.location.Location
import androidx.core.content. ContextCompat
import com.android. sun.data.database.PlaceDao
import com.android.sun. data.database.PlaceDatabase
import com.android.sun.data. database.PlaceEntity
import com.android.sun.data. model.LocationData
import com. android.sun.domain.calculator. Supplement
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android. gms.tasks.CancellationTokenSource
import kotlinx.coroutines. Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx. coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlin.coroutines. resume
import kotlin.coroutines.resumeWithException

/**
 * Repository pentru gestionarea locațiilor
 * Accesează baza de date și serviciile GPS
 */
class LocationRepository(private val context: Context) {

    private val supplement = Supplement()
    private val placeDao: PlaceDao = PlaceDatabase.getDatabase(context).placeDao()
    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices. getFusedLocationProviderClient(context)

    /**
     * Obține toate locațiile salvate din baza de date
     */
    fun getAllLocations(): Flow<List<LocationData>> {
        return placeDao.getAllPlaces().map { places ->
            places. map { placeEntityToLocationData(it) }
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
			android.util.Log. d("LocationRepository", "⚠️ București not found, adding it...")
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
		android.util.Log. d("LocationRepository", "🔍 Location '$name' exists:  $exists")
		exists
	}
	
	
	
	
	
	
	
	
	
    /**
     * ✅ Încarcă locațiile predefinite (toate orașele din România)
     * Folosește REPLACE pentru a nu duplica dacă există deja
     */
    suspend fun loadDefaultLocations() = withContext(Dispatchers.IO) {
        android.util.Log. d("LocationRepository", "🔵 Loading default locations...")
        
		// ✅ Șterge TOATE locațiile existente
		placeDao.deleteAllPlaces()
		
        val defaultCities = listOf(
            PlaceEntity(name = "Arad", longitude = 21.3123, latitude = 46.1866, altitude = 117.0, timeZone = 2.0, dst = 0),
            PlaceEntity(name = "Bacău", longitude = 26.9253, latitude = 46.5711, altitude = 165.0, timeZone = 2.0, dst = 0),
            PlaceEntity(name = "Baia Mare", longitude = 23.5795, latitude = 47.6530, altitude = 225.0, timeZone = 2.0, dst = 0),
            PlaceEntity(name = "Băile Herculane", longitude = 22.4152, latitude = 44.8802, altitude = 168.0, timeZone = 2.0, dst = 0),
            PlaceEntity(name = "Brăila", longitude = 27.9575, latitude = 45.2692, altitude = 20.0, timeZone = 2.0, dst = 0),
            PlaceEntity(name = "Brașov", longitude = 25.6012, latitude = 45.6579, altitude = 625.0, timeZone = 2.0, dst = 0),
            PlaceEntity(name = "București", longitude = 26.1025, latitude = 44.4268, altitude = 80.0, timeZone = 2.0, dst = 0),
            PlaceEntity(name = "Buzău", longitude = 26.8333, latitude = 45.1500, altitude = 95.0, timeZone = 2.0, dst = 0),
            PlaceEntity(name = "Cluj-Napoca", longitude = 23.6236, latitude = 46.7712, altitude = 360.0, timeZone = 2.0, dst = 0),
            PlaceEntity(name = "Constanța", longitude = 28.6348, latitude = 44.1598, altitude = 25.0, timeZone = 2.0, dst = 0),
            PlaceEntity(name = "Costinești", longitude = 28.6012, latitude = 43.9509, altitude = 10.0, timeZone = 2.0, dst = 0),
            PlaceEntity(name = "Craiova", longitude = 23.7949, latitude = 44.3302, altitude = 100.0, timeZone = 2.0, dst = 0),
            PlaceEntity(name = "Galați", longitude = 28.0079, latitude = 45.4353, altitude = 55.0, timeZone = 2.0, dst = 0),
            PlaceEntity(name = "Iași", longitude = 27.6014, latitude = 47.1585, altitude = 95.0, timeZone = 2.0, dst = 0),
            PlaceEntity(name = "Oradea", longitude = 21.9217, latitude = 47.0722, altitude = 150.0, timeZone = 2.0, dst = 0),
            PlaceEntity(name = "Pitești", longitude = 24.8692, latitude = 44.8565, altitude = 287.0, timeZone = 2.0, dst = 0),
            PlaceEntity(name = "Ploiești", longitude = 26.0236, latitude = 44.9417, altitude = 150.0, timeZone = 2.0, dst = 0),
            PlaceEntity(name = "Râmnicu Vâlcea", longitude = 24.3693, latitude = 45.0997, altitude = 243.0, timeZone = 2.0, dst = 0),
            PlaceEntity(name = "Satu Mare", longitude = 22.8850, latitude = 47.7928, altitude = 123.0, timeZone = 2.0, dst = 0),
            PlaceEntity(name = "Sibiu", longitude = 24.1521, latitude = 45.7928, altitude = 415.0, timeZone = 2.0, dst = 0),
            PlaceEntity(name = "Șirnea", longitude = 25.2547, latitude = 45.4714, altitude = 1150.0, timeZone = 2.0, dst = 0),
            PlaceEntity(name = "Târgu Mureș", longitude = 24.5575, latitude = 46.5425, altitude = 308.0, timeZone = 2.0, dst = 0),
            PlaceEntity(name = "Timișoara", longitude = 21.2087, latitude = 45.7489, altitude = 90.0, timeZone = 2.0, dst = 0),
			PlaceEntity(name = "Tulcea", longitude = 28.8050, latitude = 45.1787, altitude = 50.0, timeZone = 2.0, dst = 0)
        )
        
        defaultCities.forEach { city ->
            placeDao.insertPlace(city)
        }
        
        android.util.Log.d("LocationRepository", "✅ Loaded ${defaultCities.size} default locations")
    }

    /**
     * Obține locația curentă de la GPS
     */
    suspend fun getCurrentLocation(): LocationData? = withContext(Dispatchers. IO) {
        android.util.Log.d("LocationRepository", "🟡 getCurrentLocation() called")
        
        if (! hasLocationPermission()) {
            android. util.Log.e("LocationRepository", "❌ No location permission!")
            return@withContext null
        }
        
        android.util.Log. d("LocationRepository", "✅ Permission OK, requesting GPS...")

        try {
            val location = getCurrentLocationInternal()
            android.util. Log.d("LocationRepository", "✅ Got GPS: ${location.latitude}, ${location.longitude}")
            
            LocationData(
                id = 0,
                name = "GPS",
                latitude = location. latitude,
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
            Manifest.permission. ACCESS_FINE_LOCATION
        ) == PackageManager. PERMISSION_GRANTED ||
        ContextCompat. checkSelfPermission(
            context,
            Manifest. permission.ACCESS_COARSE_LOCATION
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
                    Priority. PRIORITY_HIGH_ACCURACY,
                    cancellationTokenSource.token
                ). addOnSuccessListener { location ->
                    if (location != null) {
                        continuation.resume(location)
                    } else {
                        continuation.resumeWithException(
                            Exception("Nu s-a putut obține locația GPS")
                        )
                    }
                }. addOnFailureListener { exception ->
                    continuation.resumeWithException(exception)
                }
            } catch (e: SecurityException) {
                continuation.resumeWithException(e)
            }

            continuation.invokeOnCancellation {
                cancellationTokenSource. cancel()
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
            name = place. name,
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
            longitude = location. longitude,
            latitude = location.latitude,
            timeZone = location. timeZone,
            altitude = location. altitude,
            dst = 0
        )
    }
}