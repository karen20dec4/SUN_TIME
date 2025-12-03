package com.android.sun.domain.calculator

import android.content.Context
import swisseph.SweDate
import swisseph.SwissEph
import swisseph.DblObj
import java.io.File
import java.io.FileOutputStream

/**
 * Wrapper pentru Swiss Ephemeris - CU fișiere reale
 */
class SwissEphWrapper(private val context: Context) {

    private val swissEph: SwissEph = SwissEph()
    private val ephePath: String

    init {
        ephePath = copyEphemerisFiles()
        swissEph.swe_set_ephe_path(ephePath)
        android.util.Log.d("SwissEphWrapper", "Swiss Ephemeris initialized at: $ephePath")
    }

    private fun copyEphemerisFiles(): String {
        val epheDir = File(context.filesDir, "sweph/data")
        if (!epheDir.exists()) {
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

    fun getJulianDay(year: Int, month: Int, day: Int, hour: Double): Double {
        val sweDate = SweDate(year, month, day, hour)
        return sweDate.julDay
    }

    fun calculateRiseTransSet(
        julianDay: Double,
        body: Int,
        longitude: Double,
        latitude: Double,
        riseSetFlag: Int
    ): Double {
        val geopos = DoubleArray(3)
        geopos[0] = longitude
        geopos[1] = latitude
        geopos[2] = 0.0

        val tret = DblObj()
        val serr = StringBuffer()

        val result = swissEph.swe_rise_trans(
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
            android.util.Log.e("SwissEphWrapper", "Rise/Set error: $serr")
            throw RuntimeException("Error calculating rise/set: $serr")
        }

        return tret.`val`
    }

    fun calculateBodyPosition(julianDay: Double, body: Int): Double {
        val xx = DoubleArray(6)
        val serr = StringBuffer()

        val result = swissEph.swe_calc_ut(
            julianDay,
            body,
            SEFLG_SWIEPH,
            xx,
            serr
        )

        if (result < 0) {
            android.util.Log.e("SwissEphWrapper", "Body position error: $serr")
            throw RuntimeException("Error calculating body position: $serr")
        }

        return xx[0]
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