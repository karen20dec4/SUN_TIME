package com.android.sun.domain.calculator

import kotlin.math.*

/**
 * Funcții matematice auxiliare pentru calcule astronomice
 */
class Supplement {

    /**
     * Convertește grade în radiani
     */
    fun rad(degrees: Double): Double {
        return degrees * PI / 180.0
    }

    /**
     * Convertește radiani în grade
     */
    fun deg(radians: Double): Double {
        return radians * 180.0 / PI
    }

    /**
     * Convertește grade zecimale în format grad (pentru DB)
     */
    fun grad(degrees: Double): Double {
        val d = degrees.toInt()
        val m = ((degrees - d) * 60).toInt()
        val s = ((degrees - d - m / 60.0) * 3600)
        return d + m / 100.0 + s / 10000.0
    }

    /**
     * Normalizează unghi la intervalul 0-360
     */
    fun normalize(angle: Double): Double {
        var result = angle % 360.0
        if (result < 0) result += 360.0
        return result
    }

    /**
     * Calculează distanța unghiulară între două puncte
     */
    fun angularDistance(angle1: Double, angle2: Double): Double {
        var diff = abs(angle1 - angle2)
        if (diff > 180.0) diff = 360.0 - diff
        return diff
    }

    /**
     * Interpolează între două valori
     */
    fun interpolate(value1: Double, value2: Double, factor: Double): Double {
        return value1 + (value2 - value1) * factor
    }

    /**
     * Calculează fracția zilei din ore
     */
    fun dayFraction(hour: Int, minute: Int, second: Int): Double {
        return (hour + minute / 60.0 + second / 3600.0) / 24.0
    }

    /**
     * Formatare timp HH:MM:SS
     */
    fun formatTime(hours: Double): String {
        val h = hours.toInt()
        val m = ((hours - h) * 60).toInt()
        val s = ((hours - h - m / 60.0) * 3600).toInt()
        return String.format("%02d:%02d:%02d", h, m, s)
    }
}