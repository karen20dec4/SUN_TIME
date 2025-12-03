package com.android.sun.domain.calculator

import java.util.*

class AstroCalculator(private val swissEph: SwissEphWrapper) {

    fun calculateSunrise(
        year: Int,
        month: Int,
        day: Int,
        longitude: Double,
        latitude: Double,
        timeZone: Double
    ): Calendar {
        val jd = swissEph.getJulianDay(year, month, day, 0.0)
        
        val sunriseJD = swissEph.calculateRiseTransSet(
            jd,
            SwissEphWrapper.SE_SUN,
            longitude,
            latitude,
            1
        )
        
        // ✅ NU pasăm timezone, Swiss Ephemeris îl calculează intern
        return julianDayToCalendar(sunriseJD)
    }

    fun calculateSunset(
        year: Int,
        month: Int,
        day: Int,
        longitude: Double,
        latitude: Double,
        timeZone: Double
    ): Calendar {
        val jd = swissEph.getJulianDay(year, month, day, 0.0)
        
        val sunsetJD = swissEph.calculateRiseTransSet(
            jd,
            SwissEphWrapper.SE_SUN,
            longitude,
            latitude,
            2
        )
        
        return julianDayToCalendar(sunsetJD)
    }

    /**
     * Convertește Julian Day la Calendar
     * Swiss Ephemeris returnează Local Solar Time bazat pe longitudine
     */
    private fun julianDayToCalendar(julianDay: Double): Calendar {
        // JD 2440587.5 = 1 ianuarie 1970, 00:00:00 UTC (Unix epoch)
        val unixMillis = ((julianDay - 2440587.5) * 86400000.0).toLong()
        
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = unixMillis
        
        return calendar
    }

    fun calculateMoonLongitude(
        year: Int, month: Int, day: Int,
        hour: Int, minute: Int, second: Int
    ): Double {
        val dayFraction = hour + minute / 60.0 + second / 3600.0
        val jd = swissEph.getJulianDay(year, month, day, dayFraction)
        return swissEph.calculateBodyPosition(jd, SwissEphWrapper.SE_MOON)
    }

    fun calculateSunLongitude(
        year: Int, month: Int, day: Int,
        hour: Int, minute: Int, second: Int
    ): Double {
        val dayFraction = hour + minute / 60.0 + second / 3600.0
        val jd = swissEph.getJulianDay(year, month, day, dayFraction)
        return swissEph.calculateBodyPosition(jd, SwissEphWrapper.SE_SUN)
    }
}