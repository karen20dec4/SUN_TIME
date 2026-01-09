package com.android.sun.data. model

/**
 * Date despre locație
 */
data class LocationData(
    val id: Int = 0,            // ID-ul din baza de date (pentru operații CRUD)
    val name: String,           // Numele locației (ex: "București")
    val latitude: Double,       // Latitudine
    val longitude: Double,      // Longitudine
    val altitude: Double = 0.0, // Altitudine în metri
    val timeZone: Double,       // Fus orar (ore UTC offset)
    val isCurrentLocation: Boolean = false  // Dacă e locația curentă GPS
) {
    /**
     * Formatează coordonatele
     */
    fun getFormattedCoordinates(): String {
        val latDir = if (latitude >= 0) "N" else "S"
        val lonDir = if (longitude >= 0) "E" else "V"
        return "${String.format("%.4f", kotlin.math.abs(latitude))}°$latDir, " +
               "${String.format("%.4f", kotlin.math.abs(longitude))}°$lonDir"
    }
    
    /**
     * Formatează fusul orar
     */
    fun getFormattedTimeZone(): String {
        val sign = if (timeZone >= 0) "+" else ""
        return "UTC$sign${String.format("%.1f", timeZone)}"
    }
    
    /**
     * Formatează altitudinea
     */
    fun getFormattedAltitude(): String {
        return "${altitude. toInt()}m"
    }
}