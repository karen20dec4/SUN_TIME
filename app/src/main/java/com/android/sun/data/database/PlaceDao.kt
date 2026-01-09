package com.android.sun.data.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object pentru operațiuni cu locațiile
 * Room generează automat implementarea
 */
@Dao
interface PlaceDao {
    
    /**
     * Obține toate locațiile (cu actualizare live)
     * Flow = se actualizează automat când se schimbă datele
     */
    @Query("SELECT * FROM places ORDER BY name ASC")
    fun getAllPlaces(): Flow<List<PlaceEntity>>
    
    /**
     * Obține toate locațiile (o singură dată, fără live update)
     */
    @Query("SELECT * FROM places ORDER BY name ASC")
    suspend fun getAllPlacesList(): List<PlaceEntity>
    
    /**
     * Obține o locație după ID
     */
    @Query("SELECT * FROM places WHERE id = :id")
    suspend fun getPlaceById(id: Int): PlaceEntity?
    
    /**
     * Obține o locație după nume
     */
    @Query("SELECT * FROM places WHERE name = :name LIMIT 1")
    suspend fun getPlaceByName(name: String): PlaceEntity?
    
    /**
     * Inserează o locație nouă
     * @return ID-ul locației inserate
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlace(place: PlaceEntity): Long
    
    /**
     * Actualizează o locație existentă
     */
    @Update
    suspend fun updatePlace(place: PlaceEntity)
    
    /**
     * Șterge o locație
     */
    @Delete
    suspend fun deletePlace(place: PlaceEntity)
    
    /**
     * Șterge toate locațiile (folosit pentru reset)
     */
    @Query("DELETE FROM places")
    suspend fun deleteAllPlaces()
    
    /**
     * Numără câte locații sunt salvate
     */
    @Query("SELECT COUNT(*) FROM places")
    suspend fun getPlacesCount(): Int
    
    /**
     * Verifică dacă o locație cu acest nume există deja
     */
    @Query("SELECT COUNT(*) FROM places WHERE name = :name")
    suspend fun placeNameExists(name: String): Int
}