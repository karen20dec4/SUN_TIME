package com.android.sun.domain.calculator

import java.util.*

class AstroCalculator(private val swissEph: SwissEphWrapper) {

    fun calculateSunrise(
        year: Int,
        month: Int,
        day:  Int,
        longitude: Double,
        latitude: Double,
        timeZone:  Double
    ): Calendar {
        val jd = swissEph.getJulianDay(year, month, day, 0.0)
        
        val sunriseJD = swissEph.calculateRiseTransSet(
            jd,
            SwissEphWrapper. SE_SUN,
            longitude,
            latitude,
            1
        )
        
        // ✅ FIX: Pasăm timezone-ul locației pentru conversie corectă
        return julianDayToCalendar(sunriseJD, timeZone)
    }

    fun calculateSunset(
        year: Int,
        month:  Int,
        day: Int,
        longitude: Double,
        latitude: Double,
        timeZone: Double
    ): Calendar {
        val jd = swissEph.getJulianDay(year, month, day, 0.0)
        
        val sunsetJD = swissEph.calculateRiseTransSet(
            jd,
            SwissEphWrapper. SE_SUN,
            longitude,
            latitude,
            2
        )
        
        // ✅ FIX: Pasăm timezone-ul locației pentru conversie corectă
        return julianDayToCalendar(sunsetJD, timeZone)
    }

    /**
     * ✅ FIX: Convertește Julian Day la Calendar folosind timezone-ul LOCAȚIEI
     * 
     * PROBLEMA VECHE: 
     * - Swiss Ephemeris returnează ora în UTC (Julian Day)
     * - Calendar. getInstance() folosea timezone-ul TELEFONULUI
     * - Dacă telefonul era în București (+2) și locația New York (-5),
     *   ora de răsărit era afișată greșit (diferență de 7 ore!)
     * 
     * SOLUȚIA:
     * - Creăm un Calendar cu timezone-ul LOCAȚIEI selectate
     * - Astfel ora afișată este corectă pentru locația aleasă
     * 
     * @param julianDay - Julian Day returnat de Swiss Ephemeris (în UTC)
     * @param timeZoneOffset - Offset-ul timezone-ului locației (ex: -5. 0 pentru New York, +2.0 pentru București)
     * @return Calendar cu ora corectă în timezone-ul locației
     */
    private fun julianDayToCalendar(julianDay: Double, timeZoneOffset: Double): Calendar {
        // JD 2440587.5 = 1 ianuarie 1970, 00:00:00 UTC (Unix epoch)
        val unixMillis = ((julianDay - 2440587.5) * 86400000.0).toLong()
        
        // ✅ FIX: Creează timezone bazat pe offset-ul LOCAȚIEI (nu al telefonului!)
        // timeZoneOffset este în ore (ex: -5.0 pentru New York EST)
        // Trebuie convertit în milisecunde:  ore * 3600 * 1000
        val offsetMillis = (timeZoneOffset * 3600.0 * 1000.0).toInt()
        val locationTimeZone = SimpleTimeZone(offsetMillis, "LocationTZ")
        
        // ✅ Creează Calendar cu timezone-ul locației
        val calendar = Calendar.getInstance(locationTimeZone)
        calendar.timeInMillis = unixMillis
        
        // ✅ DEBUG LOG - poți să-l ștergi după ce confirmi că funcționează
        android.util.Log. d("AstroCalculator", "═══════════════════════════════════════")
        android.util.Log.d("AstroCalculator", "📍 TimeZone offset: $timeZoneOffset ore")
        android.util.Log. d("AstroCalculator", "📍 TimeZone offset in ms: $offsetMillis")
        android.util.Log. d("AstroCalculator", "📍 Calendar TimeZone: ${calendar.timeZone. id}")
        android.util.Log. d("AstroCalculator", "📍 Ora rezultată: ${calendar.get(Calendar.HOUR_OF_DAY)}:${calendar.get(Calendar. MINUTE)}:${calendar.get(Calendar. SECOND)}")
        android.util.Log. d("AstroCalculator", "═══════════════════════════════════════")
        
        return calendar
    }

    fun calculateMoonLongitude(
        year: Int, month: Int, day: Int,
        hour: Int, minute: Int, second: Int
    ): Double {
        val dayFraction = hour + minute / 60.0 + second / 3600.0
        val jd = swissEph.getJulianDay(year, month, day, dayFraction)
        return swissEph.calculateBodyPosition(jd, SwissEphWrapper. SE_MOON)
    }

    fun calculateSunLongitude(
        year: Int, month: Int, day:  Int,
        hour: Int, minute:  Int, second: Int
    ): Double {
        val dayFraction = hour + minute / 60.0 + second / 3600.0
        val jd = swissEph.getJulianDay(year, month, day, dayFraction)
        return swissEph. calculateBodyPosition(jd, SwissEphWrapper.SE_SUN)
    }
}