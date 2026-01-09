package com.android.sun.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * Gradient overlay pentru navigation bar
 * Se pune în Box cu Alignment.BottomCenter
 */
@Composable
fun GradientNavigationBar(
    isDarkTheme: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(60.dp)
            .background(
                brush = Brush.verticalGradient(
                    colors = if (isDarkTheme) {
                        listOf(
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.5f),  // 0.7f Mai opac
                            Color.Black.copy(alpha = 0.9f)   // 0.95f Aproape solid
                        )
                    } else {
                        listOf(
                            Color.Transparent,
                            Color.White.copy(alpha = 0.5f),
                            Color.White.copy(alpha = 0.9f)
                        )
                    }
                )
            )
    )
}