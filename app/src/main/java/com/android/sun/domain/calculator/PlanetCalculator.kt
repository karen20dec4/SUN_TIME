package com.android.sun.domain.calculator

import java.text.SimpleDateFormat
import java.util.*

/**
 * Calculator pentru Orele Planetare (Planetary Hours)
 * ✅ VERSIUNE OPTIMIZATĂ: primește previousSunset și nextSunrise pentru precizie maximă
 * ✅ CORECTATĂ: folosește globalHourIndex (0-23) pentru secvența continuă de planete
 */
class PlanetCalculator {

    /**
     * Calculează Planeta dominantă pentru ora curentă
     * ✅ OPTIMIZAT: primește previousSunset și nextSunrise ca parametri pentru precizie maximă
     */
    fun calculatePlanetaryHour(
        currentTime: Calendar,
        sunrise: Calendar,
        sunset: Calendar,
        previousSunset: Calendar,  // ✅ ADĂUGAT: sunset de IERI
        nextSunrise: Calendar      // ✅ ADĂUGAT: sunrise de MÂINE
    ): PlanetResult {
        val timeFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        
        android.util.Log.d("PlanetDebug", "============================================")
        android.util.Log.d("PlanetDebug", "🔍 PLANETARY HOUR CALCULATION START")
        android.util.Log.d("PlanetDebug", "============================================")
        android.util.Log.d("PlanetDebug", "📅 currentTime:     ${timeFormat.format(currentTime.time)}")
        android.util.Log.d("PlanetDebug", "🌅 sunrise:         ${timeFormat.format(sunrise.time)}")
        android.util.Log.d("PlanetDebug", "🌇 sunset:          ${timeFormat.format(sunset.time)}")
        android.util.Log.d("PlanetDebug", "🌇 previousSunset:  ${timeFormat.format(previousSunset.time)}")
        android.util.Log.d("PlanetDebug", "🌅 nextSunrise:     ${timeFormat.format(nextSunrise.time)}")
        
        val currentMillis = currentTime.timeInMillis
        val sunriseMillis = sunrise.timeInMillis
        val sunsetMillis = sunset.timeInMillis
        
        val isDayTime = currentMillis in sunriseMillis..sunsetMillis
        
        android.util.Log.d("PlanetDebug", "☀️ isDayTime: $isDayTime")
        
        // Calculează interval
        val startMillis: Long
        val endMillis: Long
        
        if (isDayTime) {
            // ✅ ZI: de la sunrise la sunset
            startMillis = sunriseMillis
            endMillis = sunsetMillis
            android.util.Log.d("PlanetDebug", "🌞 DAY TIME: from sunrise to sunset")
        } else {
            if (currentMillis < sunriseMillis) {
                // ✅ NOAPTE ÎNAINTE DE SUNRISE: de la previousSunset (ieri) la sunrise (azi)
                startMillis = previousSunset.timeInMillis
                endMillis = sunriseMillis
                android.util.Log.d("PlanetDebug", "🌙 NIGHT (before sunrise): from previousSunset to sunrise")
            } else {
                // ✅ NOAPTE DUPĂ SUNSET: de la sunset (azi) la nextSunrise (mâine)
                startMillis = sunsetMillis
                endMillis = nextSunrise.timeInMillis
                android.util.Log.d("PlanetDebug", "🌙 NIGHT (after sunset): from sunset to nextSunrise")
            }
        }
        
        android.util.Log.d("PlanetDebug", "⏰ startMillis: $startMillis (${timeFormat.format(Date(startMillis))})")
        android.util.Log.d("PlanetDebug", "⏰ endMillis:   $endMillis (${timeFormat.format(Date(endMillis))})")
        
        // 12 ore planetare
        val totalDuration = endMillis - startMillis
        val planetaryHourDuration = totalDuration / 12
        val elapsedTime = currentMillis - startMillis
        
        android.util.Log.d("PlanetDebug", "⏱ totalDuration: ${totalDuration / 60000.0} minutes")
        android.util.Log.d("PlanetDebug", "⏱ planetaryHourDuration: ${planetaryHourDuration / 60000.0} minutes")
        android.util.Log.d("PlanetDebug", "⏱ elapsedTime: ${elapsedTime / 60000.0} minutes")
        
        val hourIndex = (elapsedTime / planetaryHourDuration).toInt().coerceIn(0, 11)
        
        android.util.Log.d("PlanetDebug", "🔢 hourIndex: $hourIndex (hour ${hourIndex + 1} of 12)")
        
        // ✅ CORECTARE: Calculează index-ul GLOBAL (0-23) pentru secvența continuă!
        val globalHourIndex: Int
        val referenceDay: Calendar

        if (isDayTime) {
            // Zi: orele 0-11 (ora 1-12)
            globalHourIndex = hourIndex
            referenceDay = sunrise
            android.util.Log.d("PlanetDebug", "🌞 DAY: globalHourIndex = $globalHourIndex (hours 1-12 of day)")
        } else {
            // Noapte: orele 12-23 (ora 13-24)
            globalHourIndex = 12 + hourIndex
            // ✅ Pentru noapte, folosește sunrise (nu previousSunset!)
            // Fiindcă planeta zilei e determinată de sunrise!
            referenceDay = sunrise
            android.util.Log.d("PlanetDebug", "🌙 NIGHT: globalHourIndex = $globalHourIndex (hours 13-24 of day)")
        }

        val dayOfWeek = referenceDay.get(Calendar.DAY_OF_WEEK) - 1
        
        val hourStartMillis = startMillis + (hourIndex * planetaryHourDuration)
        val hourEndMillis = hourStartMillis + planetaryHourDuration
        
        val hourStart = Calendar.getInstance()
        hourStart.timeInMillis = hourStartMillis
        
        val hourEnd = Calendar.getInstance()
        hourEnd.timeInMillis = hourEndMillis
        
        android.util.Log.d("PlanetDebug", "⏰ hourStart: ${timeFormat.format(hourStart.time)}")
        android.util.Log.d("PlanetDebug", "⏰ hourEnd:   ${timeFormat.format(hourEnd.time)}")
        
        val dayNames = listOf("SUN", "MON", "TUE", "WED", "THU", "FRI", "SAT")
        val planetRulers = listOf("Sun", "Moon", "Mars", "Mercury", "Jupiter", "Venus", "Saturn")
        
        android.util.Log.d("PlanetDebug", "📅 referenceDay: ${timeFormat.format(referenceDay.time)}")
        android.util.Log.d("PlanetDebug", "📅 dayOfWeek: $dayOfWeek (${dayNames[dayOfWeek]})")
        android.util.Log.d("PlanetDebug", "📅 dayRuler: ${planetRulers[dayOfWeek]}")
        android.util.Log.d("PlanetDebug", "🔢 globalHourIndex: $globalHourIndex (hour ${globalHourIndex + 1} of 24)")
        
        // ✅ Secvența planetelor folosind globalHourIndex
        val planet = getPlanetForHour(dayOfWeek, globalHourIndex)
        
        android.util.Log.d("PlanetDebug", "🪐 RESULT: ${planet.displayName} (${planet.code})")
        android.util.Log.d("PlanetDebug", "============================================")
        
        return PlanetResult(
            planet = planet,
            startTime = hourStart,
            endTime = hourEnd,
            hourNumber = globalHourIndex + 1,  // ✅ 1-24 (nu 1-12!)
            isDayTime = isDayTime
        )
    }
    
    /**
     * Obține planeta pentru ora specifică
     * ✅ MODIFICAT: folosește globalHourIndex (0-23) pentru secvența continuă
     */
    private fun getPlanetForHour(dayOfWeek: Int, globalHourIndex: Int): PlanetType {
        // Secvența Chaldeeană: Saturn, Jupiter, Mars, Sun, Venus, Mercury, Moon
        val chaldeanOrder = listOf(
            PlanetType.SATURN, PlanetType.JUPITER, PlanetType.MARS,
            PlanetType.SUN, PlanetType.VENUS, PlanetType.MERCURY, PlanetType.MOON
        )
        
        // Planeta care domnește ziua
        val dayRuler = listOf(
            PlanetType.SUN,     // Duminică (0)
            PlanetType.MOON,    // Luni (1)
            PlanetType.MARS,    // Marți (2)
            PlanetType.MERCURY, // Miercuri (3)
            PlanetType.JUPITER, // Joi (4)
            PlanetType.VENUS,   // Vineri (5)
            PlanetType.SATURN   // Sâmbătă (6)
        )
        
        val startPlanet = dayRuler[dayOfWeek]
        val startIndex = chaldeanOrder.indexOf(startPlanet)
        
        android.util.Log.d("PlanetDebug", "🔍 getPlanetForHour: dayOfWeek=$dayOfWeek, globalHourIndex=$globalHourIndex")
        android.util.Log.d("PlanetDebug", "🔍 startPlanet: ${startPlanet.displayName} (index $startIndex in chaldeanOrder)")
        
        // ✅ Calculează index-ul planetei curente folosind globalHourIndex
        val planetIndex = (startIndex + globalHourIndex) % 7
        
        android.util.Log.d("PlanetDebug", "🔍 planetIndex: ($startIndex + $globalHourIndex) % 7 = $planetIndex")
        android.util.Log.d("PlanetDebug", "🔍 result: ${chaldeanOrder[planetIndex].displayName}")
        
        return chaldeanOrder[planetIndex]
    }
}

/**
 * Enum pentru planete
 */
enum class PlanetType(val displayName: String, val code: String) {
    SUN("Soare", "☉"),
    MOON("Lună", "☽"),
    MERCURY("Mercur", "☿"),
    VENUS("Venus", "♀"),
    MARS("Marte", "♂"),
    JUPITER("Jupiter", "♃"),
    SATURN("Saturn", "♄")
}

/**
 * Rezultatul calculului Planetar
 */
data class PlanetResult(
    val planet: PlanetType,
    val startTime: Calendar,
    val endTime: Calendar,
    val hourNumber: Int,  // ✅ ACUM: 1-24 (nu 1-12!)
    val isDayTime: Boolean
)