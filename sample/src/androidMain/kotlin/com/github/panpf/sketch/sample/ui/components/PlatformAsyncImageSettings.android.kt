package com.github.panpf.sketch.sample.ui.components

import android.graphics.ColorSpace
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.github.panpf.sketch.decode.BitmapConfig
import com.github.panpf.sketch.request.ComposableImageOptions
import com.github.panpf.sketch.request.ImageOptions
import com.github.panpf.sketch.request.bitmapConfig
import com.github.panpf.sketch.request.colorSpace
import com.github.panpf.sketch.request.preferQualityOverSpeed
import com.github.panpf.sketch.sample.AppSettings

@Suppress("NOTHING_TO_INLINE")
@Composable
actual inline fun composablePlatformAsyncImageSettings(appSettings: AppSettings): ImageOptions {
    return ComposableImageOptions {
        val bitmapQuality by appSettings.bitmapQuality.collectAsState()
        bitmapConfig(bitmapQualityValue(bitmapQuality))
        if (VERSION.SDK_INT >= VERSION_CODES.O) {
            val colorSpace by appSettings.colorSpace.collectAsState()
            colorSpace(colorSpaceValue(colorSpace))
        }
        val inPreferQualityOverSpeed by appSettings.inPreferQualityOverSpeed.collectAsState()
        @Suppress("DEPRECATION")
        preferQualityOverSpeed(VERSION.SDK_INT <= VERSION_CODES.M && inPreferQualityOverSpeed)
    }
}

fun bitmapQualityValue(value: String): BitmapConfig? {
    return when (value) {
        "LOW" -> BitmapConfig.LowQuality
        "HIGH" -> BitmapConfig.HighQuality
        else -> null
    }
}

fun colorSpaceValue(value: String): ColorSpace.Named? {
    return if (VERSION.SDK_INT >= VERSION_CODES.O) {
        when (value) {
            "Default" -> null
            else -> ColorSpace.Named.valueOf(value)
        }
    } else {
        null
    }
}