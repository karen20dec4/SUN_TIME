package com.android.sun.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.android.sun.data.model.AstroData
import com.android.sun.data.model.TattvaDayItem
import com.android.sun.data.repository.AstroRepository
import java.util.Calendar

/**
 * ViewModel dedicat pentru calcule astrologice și All Day View
 */
class AstroViewModel(application: Application) : AndroidViewModel(application) {
    
    private val repository = AstroRepository(application)
    
    /**
     * Generează schedule-ul complet pentru All Day View
     */
    fun generateTattvaDaySchedule(astroData: AstroData): List<TattvaDayItem> {
        return repository.generateTattvaDaySchedule(
            sunriseTime = astroData.sunrise,
            latitude = astroData.latitude,
            longitude = astroData.longitude,
            timeZone = astroData.timeZone,
            currentTime = astroData.currentTime
        )
    }
    
    /**
     * ✅ Generează schedule-ul cu timpul CURENT specificat (nu cel din astroData)
     */
    fun generateTattvaDayScheduleWithCurrentTime(
        astroData: AstroData,
        currentTime: Calendar
    ): List<TattvaDayItem> {
        return repository.generateTattvaDaySchedule(
            sunriseTime = astroData.sunrise,
            latitude = astroData.latitude,
            longitude = astroData.longitude,
            timeZone = astroData.timeZone,
            currentTime = currentTime  // ✅ Timpul CURENT, nu cel vechi!
        )
    }
    
    /**
     * Generează schedule pentru ziua următoare
     */
    fun generateNextDaySchedule(astroData: AstroData): List<TattvaDayItem> {
        val nextDaySunrise = astroData.sunrise.clone() as Calendar
        nextDaySunrise.add(Calendar.DAY_OF_MONTH, 1)
        
        return repository.generateTattvaDaySchedule(
            sunriseTime = nextDaySunrise,
            latitude = astroData.latitude,
            longitude = astroData.longitude,
            timeZone = astroData.timeZone,
            currentTime = astroData.currentTime
        )
    }
}