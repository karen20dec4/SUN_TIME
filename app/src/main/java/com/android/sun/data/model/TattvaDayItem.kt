package com.android.sun.data.model

import androidx.compose.ui.graphics.Color
import java.util.Calendar

/**
 * Item pentru All Day View - reprezentează o Tattva din timeline
 */
data class TattvaDayItem(
    val tattvaName: String,
    val tattvaCode: String,
    val tattvaColor: Color,
    val startTime: Calendar,
    val endTime: Calendar,
    val subTattvas: List<SubTattvaItem>,
    val cycleNumber: Int,  // 1-12
    val isCurrent: Boolean = false
)

/**
 * Item pentru SubTattva în All Day View
 */
data class SubTattvaItem(
    val name: String,
    val code: String,
    val startTime: Calendar,
    val color: Color,
    val isCurrent: Boolean = false
)