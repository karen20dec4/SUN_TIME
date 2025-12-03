package com.android.sun.data.model

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
    val latitude: Double,
    val longitude: Double,
    val timeZone: Double
)