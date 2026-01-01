package com.android.sun.domain.calculator

import android.content. Context
import swisseph.SweDate
import swisseph. SwissEph
import swisseph.DblObj
import java.io.File
import java. io.FileOutputStream

/**
 * Wrapper pentru Swiss Ephemeris - CU fișiere reale
 * ✅ AUTO-RECOVERY: Reinițializează automat fișierele corupte
 */
class SwissEphWrapper(private val context: Context) {

    private var swissEph: SwissEph?  = null
    private var ephePath: String = ""
    private var isInitialized = false
    private var retryCount = 0
    private val maxRetries = 2

    init {
        initializeSwissEph()
    }

    /**
     * ✅ Inițializează Swiss Ephemeris
     */
    private fun initializeSwissEph() {
        try {
            ephePath = copyEphemerisFiles()
            swissEph = SwissEph()
            swissEph?.swe_set_ephe_path(ephePath)
            isInitialized = true
            retryCount = 0
            android.util.Log.d("SwissEphWrapper", "✅ Swiss Ephemeris initialized at: $ephePath")
        } catch (e: Exception) {
            android.util.Log. e("SwissEphWrapper", "❌ Failed to initialize Swiss Ephemeris", e)
            isInitialized = false
            throw e
        }
    }

    /**
     * ✅ Reinițializează Swiss Ephemeris după detectarea corupției
     */
    private fun reinitializeSwissEph() {
        android.util.Log.w("SwissEphWrapper", "⚠️ Reinitializing Swiss Ephemeris (attempt ${retryCount + 1}/$maxRetries)...")
        
        // Închide instanța curentă
        swissEph?.swe_close()
        swissEph = null
        isInitialized = false
        
        // Șterge fișierele corupte
        val epheDir = File(context.filesDir, "sweph/data")
        if (epheDir.exists()) {
            epheDir.listFiles()?.forEach { file ->
                if (file.name.endsWith(".se1")) {
                    val deleted = file.delete()
                    android.util.Log.d("SwissEphWrapper", "Deleted corrupted file: ${file. name} -> $deleted")
                }
            }
        }
        
        // Reinițializează
        try {
            initializeSwissEph()
            android.util.Log.d("SwissEphWrapper", "✅ Swiss Ephemeris successfully reinitialized!")
        } catch (e: Exception) {
            android.util.Log.e("SwissEphWrapper", "❌ Failed to reinitialize Swiss Ephemeris", e)
            throw RuntimeException("Failed to reinitialize Swiss Ephemeris after corruption", e)
        }
    }

    /**
     * ✅ Copiază fișierele ephemeris din assets
     */
    private fun copyEphemerisFiles(): String {
        val epheDir = File(context.filesDir, "sweph/data")
        if (!epheDir. exists()) {
            epheDir.mkdirs()
        }

        val assetManager = context.assets
        val files = arrayOf("seas_18.se1", "semo_18.se1", "sepl_18.se1")

        files.forEach { fileName ->
            val outFile = File(epheDir, fileName)
            
            // ✅ FORȚEAZĂ re-copierea pentru a înlocui fișierele corupte
            if (outFile.exists()) {
                android.util.Log.d("SwissEphWrapper", "Deleting old $fileName")
                outFile.delete()
            }
            
            try {
                assetManager.open("sweph/data/$fileName").use { input ->
                    FileOutputStream(outFile).use { output ->
                        input.copyTo(output)
                    }
                }
                
                val size = outFile.length()
                android.util.Log.d("SwissEphWrapper", "✓ Copied $fileName ($size bytes)")
                
                if (size == 0L) {
                    throw RuntimeException("File $fileName is EMPTY after copy!")
                }
                
            } catch (e: Exception) {
                android.util.Log.e("SwissEphWrapper", "✗ Failed to copy $fileName", e)
                throw RuntimeException("Failed to copy ephemeris file: $fileName", e)
            }
        }

        return epheDir.absolutePath
    }

    /**
     * ✅ Verifică dacă eroarea indică fișiere corupte
     */
    private fun isCorruptionError(error: String? ): Boolean {
        if (error == null) return false
        return error.contains("damaged", ignoreCase = true) ||
               error.contains("error in ephemeris", ignoreCase = true) ||
               error.contains("file error", ignoreCase = true) ||
               error.contains("coefficients instead", ignoreCase = true) ||
               error.contains("wrong", ignoreCase = true) ||
               error.contains("rename to", ignoreCase = true)
    }

    fun getJulianDay(year:  Int, month: Int, day:  Int, hour: Double): Double {
        val sweDate = SweDate(year, month, day, hour)
        return sweDate. julDay
    }

    /**
     * ✅ Calculează rise/transit/set cu AUTO-RECOVERY
     */
    fun calculateRiseTransSet(
        julianDay: Double,
        body: Int,
        longitude: Double,
        latitude: Double,
        riseSetFlag:  Int
    ): Double {
        if (!isInitialized || swissEph == null) {
            android.util.Log.e("SwissEphWrapper", "❌ Swiss Ephemeris not initialized!")
            throw RuntimeException("Swiss Ephemeris not initialized")
        }

        try {
            val geopos = DoubleArray(3)
            geopos[0] = longitude
            geopos[1] = latitude
            geopos[2] = 0.0

            val tret = DblObj()
            val serr = StringBuffer()

            val result = swissEph!! .swe_rise_trans(
                julianDay,
                body,
                null,
                SEFLG_SWIEPH,
                riseSetFlag,
                geopos,
                1013.25,
                10.0,
                tret,
                serr
            )

            if (result < 0) {
                val errorMsg = serr.toString()
                android.util.Log.e("SwissEphWrapper", "Rise/Set error: $errorMsg")
                
                // ✅ Detectează corupție și reinițializează
                if (isCorruptionError(errorMsg) && retryCount < maxRetries) {
                    retryCount++
                    reinitializeSwissEph()
                    
                    // Reîncearcă calculul
                    return calculateRiseTransSet(julianDay, body, longitude, latitude, riseSetFlag)
                }
                
                throw RuntimeException("Error calculating rise/set: $errorMsg")
            }

            // Reset retry counter pe succes
            retryCount = 0
            return tret.`val`
            
        } catch (e: NullPointerException) {
            android.util.Log.e("SwissEphWrapper", "❌ NullPointerException - corrupted ephemeris!", e)
            
            if (retryCount < maxRetries) {
                retryCount++
                reinitializeSwissEph()
                
                // Reîncearcă calculul
                return calculateRiseTransSet(julianDay, body, longitude, latitude, riseSetFlag)
            }
            
            throw RuntimeException("NullPointerException after $maxRetries retries", e)
        } catch (e: Exception) {
            android.util.Log.e("SwissEphWrapper", "❌ Unexpected error in calculateRiseTransSet", e)
            
            // Verifică dacă mesajul de eroare indică corupție
            if (isCorruptionError(e. message) && retryCount < maxRetries) {
                retryCount++
                reinitializeSwissEph()
                
                // Reîncearcă calculul
                return calculateRiseTransSet(julianDay, body, longitude, latitude, riseSetFlag)
            }
            
            throw RuntimeException("Error calculating rise/set: ${e. message}", e)
        }
    }

    /**
     * ✅ Calculează poziția corpului ceresc cu AUTO-RECOVERY
     */
    fun calculateBodyPosition(julianDay: Double, body: Int): Double {
        if (!isInitialized || swissEph == null) {
            android.util.Log.e("SwissEphWrapper", "❌ Swiss Ephemeris not initialized!")
            throw RuntimeException("Swiss Ephemeris not initialized")
        }

        try {
            val xx = DoubleArray(6)
            val serr = StringBuffer()

            val result = swissEph!!.swe_calc_ut(
                julianDay,
                body,
                SEFLG_SWIEPH,
                xx,
                serr
            )

            if (result < 0) {
                val errorMsg = serr.toString()
                android.util.Log. e("SwissEphWrapper", "Body position error: $errorMsg")
                
                // ✅ Detectează corupție și reinițializează
                if (isCorruptionError(errorMsg) && retryCount < maxRetries) {
                    retryCount++
                    reinitializeSwissEph()
                    
                    // Reîncearcă calculul
                    return calculateBodyPosition(julianDay, body)
                }
                
                throw RuntimeException("Error calculating body position: $errorMsg")
            }

            // Reset retry counter pe succes
            retryCount = 0
            return xx[0]
            
        } catch (e: NullPointerException) {
            android.util. Log.e("SwissEphWrapper", "❌ NullPointerException - corrupted ephemeris!", e)
            
            if (retryCount < maxRetries) {
                retryCount++
                reinitializeSwissEph()
                
                // Reîncearcă calculul
                return calculateBodyPosition(julianDay, body)
            }
            
            throw RuntimeException("NullPointerException after $maxRetries retries", e)
        } catch (e: Exception) {
            android.util.Log.e("SwissEphWrapper", "❌ Unexpected error in calculateBodyPosition", e)
            
            // Verifică dacă mesajul de eroare indică corupție
            if (isCorruptionError(e. message) && retryCount < maxRetries) {
                retryCount++
                reinitializeSwissEph()
                
                // Reîncearcă calculul
                return calculateBodyPosition(julianDay, body)
            }
            
            throw RuntimeException("Error calculating body position: ${e.message}", e)
        }
    }

    /**
     * ✅ Închide Swiss Ephemeris când nu mai este nevoie
     */
    fun close() {
        swissEph?.swe_close()
        swissEph = null
        isInitialized = false
        android.util.Log.d("SwissEphWrapper", "Swiss Ephemeris closed")
    }

    companion object {
        const val SEFLG_SWIEPH = 2
        
        const val SE_SUN = 0
        const val SE_MOON = 1
        const val SE_MERCURY = 2
        const val SE_VENUS = 3
        const val SE_MARS = 4
        const val SE_JUPITER = 5
        const val SE_SATURN = 6
    }
}