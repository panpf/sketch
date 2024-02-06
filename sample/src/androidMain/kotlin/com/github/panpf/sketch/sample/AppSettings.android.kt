package com.github.panpf.sketch.sample

import android.graphics.ColorSpace
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import androidx.annotation.RequiresApi
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import com.github.panpf.sketch.PlatformContext
import com.github.panpf.sketch.decode.BitmapConfig
import com.github.panpf.sketch.request.ImageOptions
import com.github.panpf.sketch.request.bitmapConfig
import com.github.panpf.sketch.request.colorSpace
import com.github.panpf.sketch.request.preferQualityOverSpeed
import okio.Path.Companion.toOkioPath

actual fun createDataStore(context: PlatformContext): DataStore<Preferences> {
    return PreferenceDataStoreFactory.createWithPath {
        context.filesDir.resolve("dice.preferences_pb").toOkioPath()
    }
}

actual fun isDebugMode(): Boolean = BuildConfig.DEBUG


private val AppSettings.bitmapQualityValue: BitmapConfig?
    get() = when (bitmapQuality.value) {
        "LOW" -> BitmapConfig.LowQuality
        "HIGH" -> BitmapConfig.HighQuality
        else -> null
    }

@get:RequiresApi(VERSION_CODES.O)
private val AppSettings.colorSpaceValue: ColorSpace.Named?
    get() = if (VERSION.SDK_INT >= VERSION_CODES.O) {
        when (val value = colorSpace.value) {
            "Default" -> null
            else -> ColorSpace.Named.valueOf(value)
        }
    } else {
        null
    }

actual fun ImageOptions.Builder.platformBuildImageOptions(appSettings: AppSettings) {
    bitmapConfig(appSettings.bitmapQualityValue)
    if (VERSION.SDK_INT >= VERSION_CODES.O) {
        colorSpace(appSettings.colorSpaceValue)
    }
    preferQualityOverSpeed(VERSION.SDK_INT <= VERSION_CODES.M && appSettings.inPreferQualityOverSpeed.value)
}