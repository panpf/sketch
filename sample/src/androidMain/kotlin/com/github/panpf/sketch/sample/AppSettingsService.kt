/*
 * Copyright (C) 2022 panpf <panpfpanpf@outlook.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.panpf.sketch.sample

import android.content.Context
import android.graphics.ColorSpace
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import androidx.annotation.RequiresApi
import com.github.panpf.sketch.cache.CachePolicy
import com.github.panpf.sketch.cache.CachePolicy.DISABLED
import com.github.panpf.sketch.cache.CachePolicy.ENABLED
import com.github.panpf.sketch.decode.BitmapConfig
import com.github.panpf.sketch.request.ImageOptions
import com.github.panpf.sketch.request.bitmapConfig
import com.github.panpf.sketch.request.colorSpace
import com.github.panpf.sketch.request.pauseLoadWhenScrolling
import com.github.panpf.sketch.request.preferQualityOverSpeed
import com.github.panpf.sketch.request.saveCellularTraffic
import com.github.panpf.sketch.resize.LongImageClipPrecisionDecider
import com.github.panpf.sketch.resize.LongImageStartCropScaleDecider
import com.github.panpf.sketch.resize.Precision
import com.github.panpf.sketch.resize.Precision.SAME_ASPECT_RATIO
import com.github.panpf.sketch.resize.PrecisionDecider
import com.github.panpf.sketch.resize.Scale
import com.github.panpf.sketch.resize.ScaleDecider
import com.github.panpf.sketch.sample.model.LayoutMode.GRID
import com.github.panpf.sketch.sample.util.SettingsStateFlow
import com.github.panpf.sketch.util.Logger
import com.github.panpf.zoomimage.zoom.AlignmentCompat
import com.github.panpf.zoomimage.zoom.ContentScaleCompat
import com.github.panpf.zoomimage.zoom.name
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class AppSettingsService(val context: Context) {

    private val preferences = context.getSharedPreferences("app_settings", Context.MODE_PRIVATE)

    /*
     * list config
     */
    val showMimeTypeLogoInLIst by lazy {
        SettingsStateFlow(
            "showMimeTypeLogoInLIst",
            true,
            preferences
        )
    }
    val showProgressIndicatorInList by lazy {
        SettingsStateFlow("showProgressIndicatorInList", true, preferences)
    }
    val showDataFromLogo by lazy {
        SettingsStateFlow("showDataFrom", true, preferences)
    }
    val pauseLoadWhenScrollInList by lazy {
        SettingsStateFlow("pauseLoadWhenScrollInList", false, preferences)
    }

    /*
     * image load
     */
    val bitmapQuality by lazy {
        SettingsStateFlow("bitmapQuality", "Default", preferences)
    }
    val colorSpace by lazy {
        SettingsStateFlow("colorSpace", "Default", preferences)
    }
    val inPreferQualityOverSpeed by lazy {
        SettingsStateFlow("inPreferQualityOverSpeed", false, preferences)
    }

    val disabledMemoryCache by lazy {
        SettingsStateFlow("disabledBitmapMemoryCache", false, preferences)
    }
    val disabledResultCache by lazy {
        SettingsStateFlow("disabledBitmapResultCache", false, preferences)
    }
    val disabledDownloadCache by lazy {
        SettingsStateFlow("disabledDownloadCache", false, preferences)
    }

    val precision by lazy {
        SettingsStateFlow("precision", "LongImageClipMode", preferences)
    }
    val scale by lazy {
        SettingsStateFlow("scale", "LongImageMode", preferences)
    }
    val longImageScale by lazy {
        SettingsStateFlow("longImageScale", Scale.START_CROP.name, preferences)
    }
    val otherImageScale by lazy {
        SettingsStateFlow("otherImageScale", Scale.CENTER_CROP.name, preferences)
    }

    val ignoreExifOrientation by lazy {
        SettingsStateFlow("ignoreExifOrientation", false, preferences)
    }
    val saveCellularTrafficInList by lazy {
        SettingsStateFlow("saveCellularTrafficInList", false, preferences)
    }
    val disallowAnimatedImageInList by lazy {
        SettingsStateFlow("disallowAnimatedImageInList", false, preferences)
    }

    /*
     * view config
     */
    val contentScale by lazy {
        SettingsStateFlow("contentScale", ContentScaleCompat.Fit.name, preferences)
    }
    val alignment by lazy {
        SettingsStateFlow("alignment", AlignmentCompat.Center.name, preferences)
    }
    val scrollBarEnabled by lazy {
        SettingsStateFlow("scrollBarEnabled", true, preferences)
    }
    val readModeEnabled by lazy {
        SettingsStateFlow("readModeEnabled", true, preferences)
    }
    val showTileBounds by lazy {
        SettingsStateFlow("showTileBounds", false, preferences)
    }

    /*
     * other
     */
    val photoListLayoutMode by lazy {
        SettingsStateFlow("photoListLayoutMode", GRID.name, preferences)
    }
    val showOriginImage by lazy {
        SettingsStateFlow("showOriginImage", false, preferences)
    }
    val logLevel by lazy {
        val defaultState =
            if (BuildConfig.DEBUG) Logger.Level.DEBUG.name else Logger.Level.INFO.name
        SettingsStateFlow("logLevel", defaultState, preferences)
    }

    private val bitmapQualityValue: BitmapConfig?
        get() = when (bitmapQuality.value) {
            "LOW" -> BitmapConfig.LowQuality
            "HIGH" -> BitmapConfig.HighQuality
            else -> null
        }

    @get:RequiresApi(VERSION_CODES.O)
    private val colorSpaceValue: ColorSpace.Named?
        get() = if (VERSION.SDK_INT >= VERSION_CODES.O) {
            when (val value = colorSpace.value) {
                "Default" -> null
                else -> ColorSpace.Named.valueOf(value)
            }
        } else {
            null
        }
    private val disabledMemoryCacheValue: CachePolicy
        get() = if (disabledMemoryCache.value) DISABLED else ENABLED
    private val disabledDownloadCacheValue: CachePolicy
        get() = if (disabledDownloadCache.value) DISABLED else ENABLED
    private val disabledResultCacheValue: CachePolicy
        get() = if (disabledResultCache.value) DISABLED else ENABLED
    private val precisionValue: PrecisionDecider
        get() = when (precision.value) {
            "LongImageClipMode" -> LongImageClipPrecisionDecider(longImage = SAME_ASPECT_RATIO)
            else -> PrecisionDecider(Precision.valueOf(precision.value))
        }
    private val scaleValue: ScaleDecider
        get() = when (scale.value) {
            "LongImageMode" -> LongImageStartCropScaleDecider(
                longImage = Scale.valueOf(value = longImageScale.value),
                otherImage = Scale.valueOf(value = otherImageScale.value)
            )

            else -> ScaleDecider(Scale.valueOf(value = scale.value))
        }

    private val listFlows = listOf(
        bitmapQuality,
        colorSpace,
        inPreferQualityOverSpeed,

        disabledMemoryCache,
        disabledResultCache,
        disabledDownloadCache,

        precision,
        scale,
        longImageScale,
        otherImageScale,

        ignoreExifOrientation,
        saveCellularTrafficInList,
        disallowAnimatedImageInList,
    )

    val listsCombinedFlow: Flow<Any> = combine(listFlows) { it.joinToString() }

    private val viewerFlows = listOf(
        bitmapQuality,
        colorSpace,
        inPreferQualityOverSpeed,

        disabledMemoryCache,
        disabledResultCache,
        disabledDownloadCache,

        ignoreExifOrientation,
    )
    val viewersCombinedFlow: Flow<Any> =
        combine(viewerFlows) { it.joinToString() }

    fun buildListImageOptions(): ImageOptions = ImageOptions {
        pauseLoadWhenScrolling(pauseLoadWhenScrollInList.value)

        bitmapConfig(bitmapQualityValue)
        if (VERSION.SDK_INT >= VERSION_CODES.O) {
            colorSpace(colorSpaceValue)
        }
        preferQualityOverSpeed(VERSION.SDK_INT <= VERSION_CODES.M && inPreferQualityOverSpeed.value)

        memoryCachePolicy(disabledMemoryCacheValue)
        resultCachePolicy(disabledResultCacheValue)
        downloadCachePolicy(disabledDownloadCacheValue)

        precision(precisionValue)
        scale(scaleValue)

        ignoreExifOrientation(ignoreExifOrientation.value)
        saveCellularTraffic(saveCellularTrafficInList.value)
        disallowAnimatedImage(disallowAnimatedImageInList.value)
    }

    fun buildViewerImageOptions(): ImageOptions = ImageOptions {
        bitmapConfig(bitmapQualityValue)
        if (VERSION.SDK_INT >= VERSION_CODES.O) {
            colorSpace(colorSpaceValue)
        }
        preferQualityOverSpeed(VERSION.SDK_INT <= VERSION_CODES.M && inPreferQualityOverSpeed.value)

        memoryCachePolicy(disabledMemoryCacheValue)
        resultCachePolicy(disabledResultCacheValue)
        downloadCachePolicy(disabledDownloadCacheValue)

        ignoreExifOrientation(ignoreExifOrientation.value)
    }
}