package com.android.sun.data. model

import com.android.sun.domain.calculator.*
import java.util.*

/**
 * Model de date pentru toate informațiile astronomice
 */
data class AstroData(
    val timestamp: Long,
    val currentTime: Calendar,
    val sunrise: Calendar,
    val sunset: Calendar,
    val sunriseFormatted: String,
    val sunsetFormatted: String,
    val tattva: TattvaResult,
    val subTattva: SubTattvaResult,
    val planet: PlanetResult,
    val nitya: NityaResult,
    val moonLongitude: Double,
    val sunLongitude: Double,
    val sunSign: String,
    val moonSign: String,
    val locationName: String,
    val isGPSLocation:  Boolean = false,
    val latitude: Double,
    val longitude: Double,
    val timeZone: Double,
    // ✅ POLARITATE - Ida/Pingala
    val sunrisePolarity:  Int = 1,              // +1 = Pingala, -1 = Ida
    val sunsetPolarity: Int = -1,              // +1 = Pingala, -1 = Ida
    val sunrisePolaritySymbol: String = "(+)", // "(+)" sau "(-)"
    val sunsetPolaritySymbol: String = "(-)"   // "(+)" sau "(-)"
)