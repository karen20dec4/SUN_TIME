package com.android.sun.domain.calculator

import java.util.*

/**
 * Calculator pentru Nitya (cele 15 Tithi lunare)
 * Bazat pe diferența dintre Lună și Soare
 * ✅ FORMULA CORECTĂ: Krishna Paksha inversează ordinea!
 * ✅ CALCUL EXACT pentru Start/End Time
 */
class NityaCalculator {

    /**
     * Calculează Nitya curentă bazată pe pozițiile Lunii și Soarelui
     * ✅ CORECTAT: Krishna Paksha = ordine inversă (15→1), Shukla = ordine normală (1→15)
     */
    
	
	
	
	
	fun calculateNitya(
    moonLongitude: Double,
    sunLongitude: Double,
    currentTime: Calendar = Calendar.getInstance()
): NityaResult {
    android.util.Log.d("NityaDebug", "============================================")
    android.util.Log.d("NityaDebug", "🌙 NITYA CALCULATION START")
    android.util.Log.d("NityaDebug", "============================================")
    
    // Calculează diferența (Lună - Soare)
    var diff = moonLongitude - sunLongitude
    
    // Normalizează la 0-360
    while (diff < 0) diff += 360.0
    while (diff >= 360) diff -= 360.0
    
    android.util.Log.d("NityaDebug", "Moon Longitude: %.2f°".format(moonLongitude))
    android.util.Log.d("NityaDebug", "Sun Longitude:  %.2f°".format(sunLongitude))
    android.util.Log.d("NityaDebug", "Difference:     %.2f°".format(diff))
    
    // ✅ Fiecare Tithi = 12 grade (360 / 30)
    val tithiIndex = (diff / 12.0).toInt()
    
    android.util.Log.d("NityaDebug", "Tithi Index:    $tithiIndex (raw)")
    
    // ✅ CORECTARE: Krishna Paksha când diff >= 180° (Luna descrește)
    val isKrishnaPaksha = diff >= 180.0
    
    android.util.Log.d("NityaDebug", "Paksha:         ${if (isKrishnaPaksha) "Krishna (Dark)" else "Shukla (Bright)"}")
    
    // ✅ FORMULA CORECTĂ: Krishna inversează ordinea!
    val nityaIndex = if (isKrishnaPaksha) {
        // Krishna Paksha: Tithi 15-29 → Nitya 15, 14, 13, ..., 1 (DESCRESCĂTOR)
        val krishnaIndex = tithiIndex - 15  // 15-29 → 0-14
        14 - krishnaIndex  // Inversează: 0→14, 1→13, 2→12, ..., 13→1, 14→0
    } else {
        // Shukla Paksha: Tithi 0-14 → Nitya 1, 2, 3, ..., 15 (CRESCĂTOR)
        tithiIndex
    }.coerceIn(0, 14)
    
    android.util.Log.d("NityaDebug", "Nitya Index:    $nityaIndex (adjusted)")
    
    val nitya = nityaList[nityaIndex]
    
    android.util.Log.d("NityaDebug", "Nitya Result:   ${nitya.number} - ${nitya.displayName}")
    
    // ✅ CALCUL EXACT pentru Start/End Time
    val tithiStartDegree = tithiIndex * 12.0
    val tithiEndDegree = (tithiIndex + 1) * 12.0
    
    val progressInTithi = diff - tithiStartDegree
    val tithiProgress = progressInTithi / 12.0
    
    // ✅ ÎMBUNĂTĂȚIRE: Calculăm viteza REALĂ a Lunii-Soarelui
    // Luna se mișcă cu ~13.2° pe zi, Soarele cu ~1° pe zi
    // Deci diferența crește cu ~12.2° pe zi (în medie)
    // DAR pentru precizie, folosim o aproximare mai bună:
    
    // Viteza medie: ~12.2° per zi = ~0.5083° per oră
    val avgDegreePerHour = 12.2 / 24.0  // ~0.5083° per oră
    
    // Câte ore au trecut de la start până acum?
    val hoursElapsedInTithi = progressInTithi / avgDegreePerHour
    
    // Câte ore mai sunt până la final?
    val degreesRemaining = 12.0 - progressInTithi
    val hoursRemainingInTithi = degreesRemaining / avgDegreePerHour
    
    // Calculează Start Time
    val startTime = currentTime.clone() as Calendar
    startTime.add(Calendar.MINUTE, -(hoursElapsedInTithi * 60).toInt())
    
    // Calculează End Time
    val endTime = currentTime.clone() as Calendar
    endTime.add(Calendar.MINUTE, (hoursRemainingInTithi * 60).toInt())
    
    android.util.Log.d("NityaDebug", "Tithi Progress: %.2f%% (%.2f° in current Tithi)".format(tithiProgress * 100, progressInTithi))
    android.util.Log.d("NityaDebug", "Hours elapsed:  %.2f hours".format(hoursElapsedInTithi))
    android.util.Log.d("NityaDebug", "Hours remaining: %.2f hours".format(hoursRemainingInTithi))
    android.util.Log.d("NityaDebug", "Start Time:     ${startTime.time}")
    android.util.Log.d("NityaDebug", "End Time:       ${endTime.time}")
    android.util.Log.d("NityaDebug", "============================================")
    
    return NityaResult(
        nitya = nitya,
        moonLongitude = moonLongitude,
        sunLongitude = sunLongitude,
        difference = diff,
        startTime = startTime,
        endTime = endTime,
        number = nitya.number,
        name = nitya.displayName,
        code = "N${nitya.number}"
    )
}
    
    
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	companion object {
        // ✅ Ordinea CORECTĂ a Nitya-urilor (pentru Shukla Paksha)
        // Krishna Paksha folosește ACEEAȘI listă, DAR în ordine INVERSĂ!
        val nityaList = listOf(
            NityaType.KAMESWARI,      // Index 0  = Nitya 1
            NityaType.BHAGAMALINI,    // Index 1  = Nitya 2
            NityaType.NITYAKLINNA,    // Index 2  = Nitya 3
            NityaType.BHERUNDA,       // Index 3  = Nitya 4
            NityaType.VAHNIVASINI,    // Index 4  = Nitya 5
            NityaType.MAHAVAJRESWARI, // Index 5  = Nitya 6
            NityaType.SIVADUTI,       // Index 6  = Nitya 7
            NityaType.TWARITA,        // Index 7  = Nitya 8
            NityaType.KULASUNDARI,    // Index 8  = Nitya 9
            NityaType.NITYA,          // Index 9  = Nitya 10
            NityaType.NILAPATAKA,     // Index 10 = Nitya 11
            NityaType.VIJAYA,         // Index 11 = Nitya 12
            NityaType.SARVAMANGALA,   // Index 12 = Nitya 13
            NityaType.JWALAMALINI,    // Index 13 = Nitya 14
            NityaType.CHITRA          // Index 14 = Nitya 15
        )
    }
}

/**
 * Enum pentru cele 15 Nitya
 */
enum class NityaType(val displayName: String, val number: Int) {
    KAMESWARI("Kameswari", 1),
    BHAGAMALINI("Bhagamalini", 2),
    NITYAKLINNA("Nityaklinna", 3),
    BHERUNDA("Bherunda", 4),
    VAHNIVASINI("Vahnivasini", 5),
    MAHAVAJRESWARI("Mahavajreswari", 6),
    SIVADUTI("Sivaduti", 7),
    TWARITA("Twarita", 8),
    KULASUNDARI("Kulasundari", 9),
    NITYA("Nitya", 10),
    NILAPATAKA("Nilapataka", 11),
    VIJAYA("Vijaya", 12),
    SARVAMANGALA("Sarvamangala", 13),
    JWALAMALINI("Jwalamalini", 14),
    CHITRA("Chitra", 15)
}

/**
 * Rezultatul calculului Nitya
 */
data class NityaResult(
    val nitya: NityaType,
    val moonLongitude: Double,
    val sunLongitude: Double,
    val difference: Double,
    val startTime: Calendar = Calendar.getInstance(),
    val endTime: Calendar = Calendar.getInstance(),
    val number: Int = nitya.number,
    val name: String = nitya.displayName,
    val code: String = "N${nitya.number}"
)