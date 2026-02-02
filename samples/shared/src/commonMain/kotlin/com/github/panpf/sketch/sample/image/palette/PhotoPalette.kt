package com.github.panpf.sketch.sample.image.palette

import androidx.compose.material3.ColorScheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.github.panpf.sketch.sample.image.SimplePalette

fun PhotoPalette(palette: SimplePalette): PhotoPalette {
    return PhotoPalette(
        palette = palette,
        primaryColor = 0xFFFFFF,
        primaryContainerColor = 0xFFFFFF
    )
}

fun PhotoPalette(palette: SimplePalette?, colorScheme: ColorScheme): PhotoPalette {
    return PhotoPalette(
        palette = palette,
        primaryColor = colorScheme.primary.toArgb(),
        primaryContainerColor = colorScheme.primaryContainer.toArgb()
    )
}

fun PhotoPalette(colorScheme: ColorScheme): PhotoPalette {
    return PhotoPalette(
        palette = null,
        colorScheme = colorScheme
    )
}


data class PhotoPalette(
    private val palette: SimplePalette?,
    private val primaryColor: Int,
    private val primaryContainerColor: Int
) {

    val containerColor: Color by lazy {
        val preferredSwatch = palette?.run {
            listOfNotNull(
                darkMutedSwatch,
                mutedSwatch,
                lightMutedSwatch,
                darkVibrantSwatch,
                vibrantSwatch,
                lightVibrantSwatch,
            ).firstOrNull()
        }
        if (preferredSwatch != null) {
            Color(preferredSwatch.rgb)
        } else {
            Color(primaryContainerColor)
        }.copy(0.6f)
    }

    val containerColorInt: Int by lazy { containerColor.toArgb() }

    val accentColor: Color by lazy {
        val preferredSwatch = palette?.run {
            listOfNotNull(
                lightVibrantSwatch,
                vibrantSwatch,
                darkVibrantSwatch,
                lightMutedSwatch,
                mutedSwatch,
                darkMutedSwatch,
            ).firstOrNull()
        }
        if (preferredSwatch != null) {
            Color(preferredSwatch.rgb)
        } else {
            Color(primaryColor)
        }.copy(0.6f)
    }

    val accentColorInt: Int by lazy { accentColor.toArgb() }

    val contentColor: Color = Color.White
    val contentColorInt: Int = contentColor.toArgb()
}