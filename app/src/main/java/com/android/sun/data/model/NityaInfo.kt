package com.android.sun.data.model

import com.android.sun.domain.calculator.NityaResult

/**
 * Model UI pentru Nitya
 */
data class NityaInfo(
    val name: String,
    val number: Int,
    val code: String,
    val moonLongitude: Double,
    val paksha: String
)

/**
 * Extensie pentru conversie din domain model
 */
fun NityaResult.toNityaInfo(): NityaInfo {
    // Calculează Paksha (Shukla = Crescătoare, Krishna = Descrescătoare)
    val paksha = if (difference < 180.0) "Shukla" else "Krishna"
    
    return NityaInfo(
        name = nitya.displayName,
        number = nitya.number,
        code = "N${nitya.number}",
        moonLongitude = moonLongitude,
        paksha = paksha
    )
}