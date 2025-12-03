package com.android.sun.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Room Database pentru aplicația SUN
 * Versiune 1: tabel 'places'
 */
@Database(
    entities = [PlaceEntity::class],
    version = 1,
    exportSchema = false
)
abstract class PlaceDatabase : RoomDatabase() {
    
    /**
     * Accesează DAO-ul pentru operațiuni cu locațiile
     */
    abstract fun placeDao(): PlaceDao
    
    companion object {
        // Singleton pattern - o singură instanță de DB
        @Volatile
        private var INSTANCE: PlaceDatabase? = null
        
        /**
         * Obține instanța database-ului (Singleton)
         */
        fun getDatabase(context: Context): PlaceDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PlaceDatabase::class.java,
                    "sun_places_database"
                )
                    .addCallback(DatabaseCallback())
                    .build()
                
                INSTANCE = instance
                instance
            }
        }
    }
    
    /**
     * Callback pentru a popula DB-ul cu date inițiale
     * Adaugă București ca locație default
     */
    private class DatabaseCallback : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            
            // Populează DB-ul cu locația default
            INSTANCE?.let { database ->
                CoroutineScope(Dispatchers.IO).launch {
                    populateDatabase(database.placeDao())
                }
            }
        }
        
        /**
         * Adaugă locația default (București)
         */
        suspend fun populateDatabase(placeDao: PlaceDao) {
            // Verifică dacă DB-ul este gol
            if (placeDao.getPlacesCount() == 0) {
                val defaultPlace = PlaceEntity(
                    name = "Bucuresti",
                    longitude = 26.05,
                    latitude = 44.26,
                    altitude = 80.0,
                    timeZone = 2.0,
                    dst = 0
                )
                placeDao.insertPlace(defaultPlace)
            }
        }
    }
}

/**
 * Helper pentru a accesa database-ul mai ușor
 */
object DatabaseProvider {
    private var database: PlaceDatabase? = null
    
    fun getDatabase(context: Context): PlaceDatabase {
        if (database == null) {
            database = PlaceDatabase.getDatabase(context)
        }
        return database!!
    }
}