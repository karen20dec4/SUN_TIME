package com.android.sun.data.model

import com.android.sun.domain.calculator.PlanetResult
import java.util.*

/**
 * Model UI pentru Planeta Orară
 */
data class PlanetInfo(
    val name: String,
    val code: String,
    val startTime: Calendar,
    val hour: Int
)

/**
 * Extensie pentru conversie din domain model
 */
fun PlanetResult.toPlanetInfo(): PlanetInfo {
    return PlanetInfo(
        name = planet.displayName,
        code = planet.code,
        startTime = startTime,
        hour = hourNumber
    )
}



/**
 * Helper functions pentru formatare
 */
fun PlanetInfo.getFormattedRemainingTime(): String {
    val now = Calendar.getInstance()
    val diff = startTime.timeInMillis - now.timeInMillis + 3600000 // +1 oră (durata planetei)
    
    if (diff <= 0) return "00:00:00"
    
    val hours = (diff / 3600000).toInt()
    val minutes = ((diff % 3600000) / 60000).toInt()
    val seconds = ((diff % 60000) / 1000).toInt()
    
    return String.format("%02d:%02d:%02d", hours, minutes, seconds)
}

// Alias pentru compatibilitate
val PlanetInfo.stopTime: Calendar
    get() {
        val stop = Calendar.getInstance()
        stop.timeInMillis = startTime.timeInMillis + 3600000 // +1 oră
        return stop
    }

val PlanetInfo.remaining: String
    get() = getFormattedRemainingTime()