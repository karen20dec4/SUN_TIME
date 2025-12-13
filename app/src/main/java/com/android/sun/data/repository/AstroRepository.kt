package com.android.sun.data.repository

import android.content.Context
import androidx.compose.ui.graphics.Color
import com.android.sun.data.model.AstroData
import com.android.sun.data.model.SubTattvaItem
import com.android.sun.data.model.TattvaDayItem
import com.android.sun.domain.calculator.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

/**
 * Repository pentru calcule astronomice
 */
class AstroRepository(private val context: Context) {

    private val swissEphWrapper = SwissEphWrapper(context)
    private val astroCalculator = AstroCalculator(swissEphWrapper)
    private val tattvaCalculator = TattvaCalculator()
    private val subTattvaCalculator = SubTattvaCalculator()
    private val planetCalculator = PlanetCalculator()
    private val nityaCalculator = NityaCalculator()
    private val polarityCalculator = PolarityCalculator()  // ✅ ADĂUGAT

    /**
     * Calculează toate datele astro pentru locația și timpul curent
     */
    suspend fun calculateAstroData(
        latitude: Double,
        longitude: Double,
        timeZone:  Double,
        locationName: String,
        isGPSLocation: Boolean = false
    ): AstroData = withContext(Dispatchers.Default) {
        val currentTime = Calendar.getInstance()
        calculateAstroDataForTime(currentTime, latitude, longitude, timeZone, locationName, isGPSLocation)
    }

    /**
     * Flow pentru actualizări în timp real (fiecare secundă)
     */
    fun getRealtimeAstroData(
        latitude: Double,
        longitude: Double,
        timeZone:  Double,
        locationName: String
    ): Flow<AstroData> = flow {
        while (true) {
            val currentTime = Calendar.getInstance()
            val data = calculateAstroDataForTime(
                currentTime, latitude, longitude, timeZone, locationName
            )
            emit(data)
            kotlinx.coroutines.delay(1000)
        }
    }

    /**
     * ✅ Determină începutul "Tattva Day" (de la răsărit la răsărit)
     */
    private fun getTattvaDayStart(
        currentTime: Calendar,
        latitude: Double,
        longitude: Double,
        timeZone: Double
    ): Calendar {
        val todayYear = currentTime.get(Calendar.YEAR)
        val todayMonth = currentTime.get(Calendar.MONTH) + 1
        val todayDay = currentTime.get(Calendar.DAY_OF_MONTH)
        
        val todaySunrise = astroCalculator.calculateSunrise(
            todayYear, todayMonth, todayDay,
            longitude, latitude, timeZone
        )
        
        if (currentTime.timeInMillis < todaySunrise.timeInMillis) {
            val yesterdayDate = currentTime.clone() as Calendar
            yesterdayDate.add(Calendar.DAY_OF_MONTH, -1)
            
            val yesterdayYear = yesterdayDate.get(Calendar.YEAR)
            val yesterdayMonth = yesterdayDate.get(Calendar.MONTH) + 1
            val yesterdayDay = yesterdayDate.get(Calendar.DAY_OF_MONTH)
            
            return astroCalculator.calculateSunrise(
                yesterdayYear, yesterdayMonth, yesterdayDay,
                longitude, latitude, timeZone
            )
        }
        
        return todaySunrise
    }

    /**
     * ✅ Calculează datele astro pentru un moment specific
     * ✅ OPTIMIZAT: calculează previousSunset și nextSunrise pentru Planetary Hours
     * ✅ ADĂUGAT: calculează polaritatea la răsărit și apus
     */
    private fun calculateAstroDataForTime(
        calendar: Calendar,
        latitude: Double,
        longitude: Double,
        timeZone: Double,
        locationName: String,
        isGPSLocation: Boolean = false
    ): AstroData {
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)
        val second = calendar.get(Calendar.SECOND)

        val sunrise = getTattvaDayStart(calendar, latitude, longitude, timeZone)
        
        val sunriseYear = sunrise.get(Calendar.YEAR)
        val sunriseMonth = sunrise.get(Calendar.MONTH) + 1
        val sunriseDay = sunrise.get(Calendar.DAY_OF_MONTH)

        val sunset = astroCalculator.calculateSunset(
            sunriseYear, sunriseMonth, sunriseDay,
            longitude, latitude, timeZone
        )

        // ✅ CORECT: Calculează previousSunset bazat pe SUNRISE (Tattva Day start!)
        val yesterdayDate = sunrise.clone() as Calendar
        yesterdayDate.add(Calendar.DAY_OF_MONTH, -1)
        val yesterdayYear = yesterdayDate.get(Calendar.YEAR)
        val yesterdayMonth = yesterdayDate.get(Calendar.MONTH) + 1
        val yesterdayDay = yesterdayDate.get(Calendar.DAY_OF_MONTH)

        val previousSunset = astroCalculator.calculateSunset(
            yesterdayYear, yesterdayMonth, yesterdayDay,
            longitude, latitude, timeZone
        )

        // ✅ CORECT: Calculează nextSunrise bazat pe SUNRISE (Tattva Day start!)
        val tomorrowDate = sunrise.clone() as Calendar
        tomorrowDate.add(Calendar.DAY_OF_MONTH, 1)

        val tomorrowYear = tomorrowDate.get(Calendar.YEAR)
        val tomorrowMonth = tomorrowDate.get(Calendar.MONTH) + 1
        val tomorrowDay = tomorrowDate.get(Calendar.DAY_OF_MONTH)
        
        val nextSunrise = astroCalculator.calculateSunrise(
            tomorrowYear, tomorrowMonth, tomorrowDay,
            longitude, latitude, timeZone
        )

        val tattvaResult = tattvaCalculator.calculateCurrentTattva(
            calendar, sunrise, sunset
        )

        val subTattvaResult = subTattvaCalculator.calculateCurrentSubTattva(
            calendar, sunrise, sunset
        )

        val moonLongitude = astroCalculator.calculateMoonLongitude(
            year, month, day, hour, minute, second
        )
        val sunLongitude = astroCalculator.calculateSunLongitude(
            year, month, day, hour, minute, second
        )

        // ✅ MODIFICAT: apelează calculatePlanetaryHour cu previousSunset și nextSunrise
        val planet = planetCalculator.calculatePlanetaryHour(
            calendar, sunrise, sunset, previousSunset, nextSunrise
        )
        
        val nitya = nityaCalculator.calculateNitya(
            moonLongitude, sunLongitude, calendar
        )

        
		
		
        // ✅ ADĂUGAT:  Calculează polaritatea la răsărit
        val sunriseHour = sunrise.get(Calendar.HOUR_OF_DAY)
        val sunriseMinute = sunrise. get(Calendar. MINUTE)
        val sunriseSecond = sunrise.get(Calendar.SECOND)
        
        val moonLongitudeAtSunrise = astroCalculator.calculateMoonLongitude(
            sunriseYear, sunriseMonth, sunriseDay, sunriseHour, sunriseMinute, sunriseSecond
        )
        val sunLongitudeAtSunrise = astroCalculator.calculateSunLongitude(
            sunriseYear, sunriseMonth, sunriseDay, sunriseHour, sunriseMinute, sunriseSecond
        )
        
        // ✅ FIX: Apelează cu 2 parametri Double, nu Calendar
        val sunrisePolarity = polarityCalculator.calculateSunrisePolarity(
            moonLongitudeAtSunrise, sunLongitudeAtSunrise
        )

        // ✅ ADĂUGAT: Calculează polaritatea la apus
        val sunsetYear = sunset.get(Calendar.YEAR)
        val sunsetMonth = sunset.get(Calendar.MONTH) + 1
        val sunsetDay = sunset.get(Calendar.DAY_OF_MONTH)
        val sunsetHour = sunset.get(Calendar.HOUR_OF_DAY)
        val sunsetMinute = sunset.get(Calendar. MINUTE)
        val sunsetSecond = sunset.get(Calendar.SECOND)
        
        val moonLongitudeAtSunset = astroCalculator. calculateMoonLongitude(
            sunsetYear, sunsetMonth, sunsetDay, sunsetHour, sunsetMinute, sunsetSecond
        )
        val sunLongitudeAtSunset = astroCalculator.calculateSunLongitude(
            sunsetYear, sunsetMonth, sunsetDay, sunsetHour, sunsetMinute, sunsetSecond
        )
        
        // ✅ FIX: Apelează cu 2 parametri Double, nu Calendar
        val sunsetPolarity = polarityCalculator.calculateSunsetPolarity(
            moonLongitudeAtSunset, sunLongitudeAtSunset
        )











        val timeFormat = SimpleDateFormat("HH:mm: ss", Locale.getDefault())
        val sunriseFormatted = timeFormat.format(sunrise.time)
        val sunsetFormatted = timeFormat.format(sunset.time)

        val sunSign = getZodiacSign(sunLongitude)
        val moonSign = getZodiacSign(moonLongitude)

        return AstroData(
            timestamp = calendar.timeInMillis,
            currentTime = calendar,
            sunrise = sunrise,
            sunset = sunset,
            sunriseFormatted = sunriseFormatted,
            sunsetFormatted = sunsetFormatted,
            tattva = tattvaResult,
            subTattva = subTattvaResult,
            planet = planet,
            nitya = nitya,
            moonLongitude = moonLongitude,
            sunLongitude = sunLongitude,
            sunSign = sunSign,
            moonSign = moonSign,
            locationName = locationName,
            isGPSLocation = isGPSLocation,
            latitude = latitude,
            longitude = longitude,
            timeZone = timeZone,
            // ✅ ADĂUGAT:  Polaritate
            sunrisePolarity = sunrisePolarity,
            sunsetPolarity = sunsetPolarity,
            sunrisePolaritySymbol = polarityCalculator.getPolaritySymbol(sunrisePolarity),
            sunsetPolaritySymbol = polarityCalculator.getPolaritySymbol(sunsetPolarity)
        )
    }

    /**
     * ✅ HIBRID OPTIMIZAT:  Folosește TattvaCalculator DOAR pentru prima SubTattva, 
     * apoi calculează restul direct (reduce 300 apeluri → 60 apeluri = de 5 ori mai rapid!)
     * Garantează precizie perfectă cu MainScreen! 
     */
    fun generateTattvaDaySchedule(
        sunriseTime: Calendar,
        latitude: Double,
        longitude: Double,
        timeZone:  Double,
        currentTime: Calendar = Calendar.getInstance()
    ): List<TattvaDayItem> {
        val tattvaList = mutableListOf<TattvaDayItem>()
        
        // Calculează răsăritul de mâine
        val tomorrowDate = sunriseTime.clone() as Calendar
        tomorrowDate.add(Calendar.DAY_OF_MONTH, 1)
        
        val nextSunriseYear = tomorrowDate.get(Calendar.YEAR)
        val nextSunriseMonth = tomorrowDate.get(Calendar.MONTH) + 1
        val nextSunriseDay = tomorrowDate.get(Calendar.DAY_OF_MONTH)
        
        val nextSunrise = astroCalculator.calculateSunrise(
            nextSunriseYear, nextSunriseMonth, nextSunriseDay,
            longitude, latitude, timeZone
        )
        
        val timeFormat = SimpleDateFormat("HH:mm: ss", Locale.getDefault())
        
        android.util.Log.d("TattvaDebug", "============================================")
        android.util.Log.d("TattvaDebug", "🚀 HYBRID OPTIMIZED (1 calc per Tattva! )")
        android.util.Log.d("TattvaDebug", "============================================")
        android.util.Log.d("TattvaDebug", "🌅 Sunrise:  ${timeFormat.format(sunriseTime.time)}")
        android.util.Log.d("TattvaDebug", "🌅 Next Sunrise: ${timeFormat.format(nextSunrise.time)}")
        
        val startTime = System.currentTimeMillis()
        
        val tattvaSequence = listOf("A", "V", "T", "Ap", "P")
        var cycleNumber = 1
        
        // ✅ Calculează duratele pentru verificări rapide
        val totalMillis = nextSunrise.timeInMillis - sunriseTime.timeInMillis
        val tattvaDurationMillis = totalMillis / 60L
        
        // ✅ Generează 60 Tattva-uri
        for (tattvaIndex in 0 until 60) {
            val tattvaCode = tattvaSequence[tattvaIndex % 5]
            
            // ✅ Calculează timpul aproximativ pentru Tattva
            val tattvaTimeMillis = sunriseTime.timeInMillis + (tattvaDurationMillis * tattvaIndex)
            val tattvaTime = Calendar.getInstance().apply {
                timeInMillis = tattvaTimeMillis
            }
            
            // ✅ Folosește TattvaCalculator pentru PRIMA SubTattva (garantează precizie!)
            val firstSubTime = tattvaTime.clone() as Calendar
            val firstSubResult = subTattvaCalculator.calculateCurrentSubTattva(
                firstSubTime, sunriseTime, nextSunrise
            )
            
            // ✅ Folosește timpul EXACT din SubTattvaCalculator pentru Tattva
            val tattvaStartTime = firstSubResult.startTime.clone() as Calendar
            
            // ✅ Calculează durata EXACTĂ a SubTattva din rezultat
            val subTattvaDurationMillis = firstSubResult.endTime.timeInMillis - firstSubResult.startTime.timeInMillis
            
            // ✅ Calculează Tattva End Time EXACT
            val tattvaEndTime = Calendar.getInstance().apply {
                timeInMillis = tattvaStartTime.timeInMillis + (subTattvaDurationMillis * 5)
            }
            
            // ✅ Generează SubTattva-uri folosind durata EXACTĂ
            val subTattvas = mutableListOf<SubTattvaItem>()
            
            for (subIndex in 0 until 5) {
                val subCode = tattvaSequence[subIndex]
                
                // ✅ Calculează START EXACT
                val subStartMillis = tattvaStartTime.timeInMillis + (subTattvaDurationMillis * subIndex)
                val subEndMillis = subStartMillis + subTattvaDurationMillis
                
                val subStartTime = Calendar.getInstance().apply {
                    timeInMillis = subStartMillis
                }
                
                // Verifică dacă e curentă
                val isSubCurrent = currentTime.timeInMillis >= subStartMillis &&
                                   currentTime.timeInMillis < subEndMillis
                
                subTattvas.add(
                    SubTattvaItem(
                        name = getTattvaName(subCode),
                        code = subCode,
                        startTime = subStartTime,
                        color = getTattvaColor(subCode),
                        isCurrent = isSubCurrent
                    )
                )
            }
            
            // Verifică dacă Tattva e curentă
            val isTattvaCurrent = currentTime.timeInMillis >= tattvaStartTime.timeInMillis &&
                                  currentTime.timeInMillis < tattvaEndTime.timeInMillis
            
            tattvaList.add(
                TattvaDayItem(
                    tattvaName = getTattvaName(tattvaCode),
                    tattvaCode = tattvaCode,
                    tattvaColor = getTattvaColor(tattvaCode),
                    startTime = tattvaStartTime,
                    endTime = tattvaEndTime,
                    subTattvas = subTattvas,
                    cycleNumber = cycleNumber,
                    isCurrent = isTattvaCurrent
                )
            )
            
            if ((tattvaIndex + 1) % 5 == 0) {
                cycleNumber++
            }
        }
        
        val endTime = System.currentTimeMillis()
        val duration = endTime - startTime
        
        android.util.Log.d("TattvaDebug", "✅ Generated ${tattvaList.size} Tattvas in ${duration}ms!")
        android.util.Log.d("TattvaDebug", "🎯 Using EXACT times from SubTattvaCalculator!")
        android.util.Log.d("TattvaDebug", "============================================")
        
        return tattvaList
    }

    private fun getZodiacSign(longitude: Double): String {
        val signs = listOf(
            "Berbec", "Taur", "Gemeni", "Rac", "Leu", "Fecioară",
            "Balanță", "Scorpion", "Săgetător", "Capricorn", "Vărsător", "Pești"
        )
        val index = (longitude / 30.0).toInt() % 12
        return signs[index]
    }
    
    private fun getTattvaName(code: String): String {
        return when (code) {
            "A" -> "Akasha"
            "V" -> "Vayu"
            "T" -> "Tejas"
            "Ap" -> "Apas"
            "P" -> "Prithivi"
            else -> "Unknown"
        }
    }
    
    private fun getTattvaColor(code: String): Color {
        return when (code) {
            "A" -> Color(0xFF4A00D3)
            "V" -> Color(0xFF009AD3)
            "T" -> Color(0xFFFF0000)
            "Ap" -> Color(0xFF8A8A8A)
            "P" -> Color(0xFFDFCD00)
            else -> Color.Gray
        }
    }
}