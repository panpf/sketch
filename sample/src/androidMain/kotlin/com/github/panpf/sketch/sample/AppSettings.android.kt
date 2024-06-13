package com.github.panpf.sketch.sample

import android.graphics.ColorSpace
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import androidx.annotation.RequiresApi
import com.github.panpf.sketch.decode.BitmapConfig
import com.github.panpf.sketch.request.ImageOptions
import com.github.panpf.sketch.request.bitmapConfig
import com.github.panpf.sketch.request.colorSpace
import com.github.panpf.sketch.request.preferQualityOverSpeed

actual fun isDebugMode(): Boolean = BuildConfig.DEBUG

fun AppSettings.Companion.bitmapQualityValue(value: String): BitmapConfig? {
    return when (value) {
        "LOW" -> BitmapConfig.LowQuality
        "HIGH" -> BitmapConfig.HighQuality
        else -> null
    }
}

fun AppSettings.Companion.colorSpaceValue(value: String): ColorSpace.Named? {
    return if (VERSION.SDK_INT >= VERSION_CODES.O) {
        when (value) {
            "Default" -> null
            else -> ColorSpace.Named.valueOf(value)
        }
    } else {
        null
    }
}

private val AppSettings.bitmapQualityValue: BitmapConfig?
    get() = AppSettings.bitmapQualityValue(bitmapQuality.value)

@get:RequiresApi(VERSION_CODES.O)
private val AppSettings.colorSpaceValue: ColorSpace.Named?
    get() = AppSettings.colorSpaceValue(colorSpace.value)

actual fun ImageOptions.Builder.platformBuildImageOptions(appSettings: AppSettings) {
    bitmapConfig(appSettings.bitmapQualityValue)
    if (VERSION.SDK_INT >= VERSION_CODES.O) {
        colorSpace(appSettings.colorSpaceValue)
    }
    @Suppress("DEPRECATION")
    preferQualityOverSpeed(VERSION.SDK_INT <= VERSION_CODES.M && appSettings.inPreferQualityOverSpeed.value)
}