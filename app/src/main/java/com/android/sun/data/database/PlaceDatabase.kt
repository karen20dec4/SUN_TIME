package com. android.sun.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx. room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx. coroutines.CoroutineScope
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
        private var INSTANCE: PlaceDatabase?  = null
        
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
     * Adaugă toate orașele din România (ordonate alfabetic)
     */
    private class DatabaseCallback : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            
            // Populează DB-ul cu orașele din România
            INSTANCE?. let { database ->
                CoroutineScope(Dispatchers. IO).launch {
                    populateDatabase(database.placeDao())
                }
            }
        }
        
        /**
         * Adaugă toate orașele din România
         * Lista este ordonată alfabetic
         */
        suspend fun populateDatabase(placeDao: PlaceDao) {
            // Verifică dacă DB-ul este gol
            if (placeDao.getPlacesCount() == 0) {
                // Lista orașelor din România - ordonate alfabetic
                val romanianCities = listOf(
                    PlaceEntity(
                        name = "Arad",
                        longitude = 21.3123,
                        latitude = 46.1866,
                        altitude = 117.0,
                        timeZone = 2.0,
                        dst = 0
                    ),
                    PlaceEntity(
                        name = "Bacău",
                        longitude = 26.9253,
                        latitude = 46.5711,
                        altitude = 165.0,
                        timeZone = 2.0,
                        dst = 0
                    ),
                    PlaceEntity(
                        name = "Baia Mare",
                        longitude = 23.5795,
                        latitude = 47.6530,
                        altitude = 225.0,
                        timeZone = 2.0,
                        dst = 0
                    ),
                    PlaceEntity(
                        name = "Băile Herculane",
                        longitude = 22.4152,
                        latitude = 44.8802,
                        altitude = 168.0,
                        timeZone = 2.0,
                        dst = 0
                    ),
                    PlaceEntity(
                        name = "Brăila",
                        longitude = 27.9575,
                        latitude = 45.2692,
                        altitude = 20.0,
                        timeZone = 2.0,
                        dst = 0
                    ),
                    PlaceEntity(
                        name = "Brașov",
                        longitude = 25.6012,
                        latitude = 45.6579,
                        altitude = 625.0,
                        timeZone = 2.0,
                        dst = 0
                    ),
                    PlaceEntity(
                        name = "București",
                        longitude = 26.1025,
                        latitude = 44.4268,
                        altitude = 80.0,
                        timeZone = 2.0,
                        dst = 0
                    ),
                    PlaceEntity(
                        name = "Buzău",
                        longitude = 26.8333,
                        latitude = 45.1500,
                        altitude = 95.0,
                        timeZone = 2.0,
                        dst = 0
                    ),
                    PlaceEntity(
                        name = "Cluj-Napoca",
                        longitude = 23.6236,
                        latitude = 46.7712,
                        altitude = 360.0,
                        timeZone = 2.0,
                        dst = 0
                    ),
                    PlaceEntity(
                        name = "Constanța",
                        longitude = 28.6348,
                        latitude = 44.1598,
                        altitude = 25.0,
                        timeZone = 2.0,
                        dst = 0
                    ),
                    PlaceEntity(
                        name = "Costinești",
                        longitude = 28.6012,
                        latitude = 43.9509,
                        altitude = 10.0,
                        timeZone = 2.0,
                        dst = 0
                    ),
                    PlaceEntity(
                        name = "Craiova",
                        longitude = 23.7949,
                        latitude = 44.3302,
                        altitude = 100.0,
                        timeZone = 2.0,
                        dst = 0
                    ),
                    PlaceEntity(
                        name = "Galați",
                        longitude = 28.0079,
                        latitude = 45.4353,
                        altitude = 55.0,
                        timeZone = 2.0,
                        dst = 0
                    ),
                    PlaceEntity(
                        name = "Iași",
                        longitude = 27.6014,
                        latitude = 47.1585,
                        altitude = 95.0,
                        timeZone = 2.0,
                        dst = 0
                    ),
                    PlaceEntity(
                        name = "Oradea",
                        longitude = 21.9217,
                        latitude = 47.0722,
                        altitude = 150.0,
                        timeZone = 2.0,
                        dst = 0
                    ),
                    PlaceEntity(
                        name = "Pitești",
                        longitude = 24.8692,
                        latitude = 44.8565,
                        altitude = 287.0,
                        timeZone = 2.0,
                        dst = 0
                    ),
                    PlaceEntity(
                        name = "Ploiești",
                        longitude = 26.0236,
                        latitude = 44.9417,
                        altitude = 150.0,
                        timeZone = 2.0,
                        dst = 0
                    ),
                    PlaceEntity(
                        name = "Râmnicu Vâlcea",
                        longitude = 24.3693,
                        latitude = 45.0997,
                        altitude = 243.0,
                        timeZone = 2.0,
                        dst = 0
                    ),
                    PlaceEntity(
                        name = "Satu Mare",
                        longitude = 22.8850,
                        latitude = 47.7928,
                        altitude = 123.0,
                        timeZone = 2.0,
                        dst = 0
                    ),
                    PlaceEntity(
                        name = "Sibiu",
                        longitude = 24.1521,
                        latitude = 45.7928,
                        altitude = 415.0,
                        timeZone = 2.0,
                        dst = 0
                    ),
                    PlaceEntity(
                        name = "Șirnea",
                        longitude = 25.2547,
                        latitude = 45.4714,
                        altitude = 1150.0,
                        timeZone = 2.0,
                        dst = 0
                    ),
                    PlaceEntity(
                        name = "Târgu Mureș",
                        longitude = 24.5575,
                        latitude = 46.5425,
                        altitude = 308.0,
                        timeZone = 2.0,
                        dst = 0
                    ),
                    PlaceEntity(
                        name = "Timișoara",
                        longitude = 21.2087,
                        latitude = 45.7489,
                        altitude = 90.0,
                        timeZone = 2.0,
                        dst = 0
                    )
                )
                
                // Inserează toate orașele în baza de date
                romanianCities.forEach { city ->
                    placeDao. insertPlace(city)
                }
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
            database = PlaceDatabase. getDatabase(context)
        }
        return database!!
    }
}