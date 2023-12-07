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
import com.github.panpf.sketch.request.pauseLoadWhenScrolling
import com.github.panpf.sketch.request.saveCellularTraffic
import com.github.panpf.sketch.resize.LongImageClipPrecisionDecider
import com.github.panpf.sketch.resize.LongImageScaleDecider
import com.github.panpf.sketch.resize.Precision
import com.github.panpf.sketch.resize.Precision.SAME_ASPECT_RATIO
import com.github.panpf.sketch.resize.PrecisionDecider
import com.github.panpf.sketch.resize.Scale
import com.github.panpf.sketch.resize.ScaleDecider
import com.github.panpf.sketch.sample.model.LayoutMode.GRID
import com.github.panpf.sketch.sample.util.BooleanMmkvData
import com.github.panpf.sketch.sample.util.StringMmkvData
import com.github.panpf.sketch.util.Logger
import com.github.panpf.zoomimage.zoom.AlignmentCompat
import com.github.panpf.zoomimage.zoom.ContentScaleCompat
import com.github.panpf.zoomimage.zoom.name
import com.tencent.mmkv.MMKV
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.merge

class PrefsService(val context: Context) {

    private val mmkv = MMKV.defaultMMKV()

    /*
     * list config
     */
    val showMimeTypeLogoInLIst by lazy {
        BooleanMmkvData(mmkv, "showMimeTypeLogoInLIst", true)
    }
    val showProgressIndicatorInList by lazy {
        BooleanMmkvData(mmkv, "showProgressIndicatorInList", true)
    }
    val showDataFromLogo by lazy {
        BooleanMmkvData(mmkv, "showDataFrom", true)
    }
    val pauseLoadWhenScrollInList by lazy {
        BooleanMmkvData(mmkv, "pauseLoadWhenScrollInList", false)
    }

    /*
     * image load
     */
    val bitmapQuality by lazy {
        StringMmkvData(mmkv, "bitmapQuality", "Default")
    }
    val colorSpace by lazy {
        StringMmkvData(mmkv, "colorSpace", "Default")
    }
    val inPreferQualityOverSpeed by lazy {
        BooleanMmkvData(mmkv, "inPreferQualityOverSpeed", false)
    }

    val disabledMemoryCache by lazy {
        BooleanMmkvData(mmkv, "disabledBitmapMemoryCache", false)
    }
    val disabledResultCache by lazy {
        BooleanMmkvData(mmkv, "disabledBitmapResultCache", false)
    }
    val disabledDownloadCache by lazy {
        BooleanMmkvData(mmkv, "disabledDownloadCache", false)
    }
    val disallowReuseBitmap by lazy {
        BooleanMmkvData(mmkv, "disallowReuseBitmap", false)
    }

    val resizePrecision by lazy {
        StringMmkvData(mmkv, "resizePrecision", "LongImageClipMode")
    }
    val resizeScale by lazy {
        StringMmkvData(mmkv, "resizeScale", "LongImageMode")
    }
    val longImageResizeScale by lazy {
        StringMmkvData(mmkv, "longImageResizeScale", Scale.START_CROP.name)
    }
    val otherImageResizeScale by lazy {
        StringMmkvData(mmkv, "otherImageResizeScale", Scale.CENTER_CROP.name)
    }

    val ignoreExifOrientation by lazy {
        BooleanMmkvData(mmkv, "ignoreExifOrientation", false)
    }
    val saveCellularTrafficInList by lazy {
        BooleanMmkvData(mmkv, "saveCellularTrafficInList", false)
    }
    val disallowAnimatedImageInList by lazy {
        BooleanMmkvData(mmkv, "disallowAnimatedImageInList", false)
    }

    /*
     * view config
     */
    val contentScale by lazy {
        StringMmkvData(mmkv, "contentScale", ContentScaleCompat.Fit.name)
    }
    val alignment by lazy {
        StringMmkvData(mmkv, "alignment", AlignmentCompat.Center.name)
    }
    val scrollBarEnabled by lazy {
        BooleanMmkvData(mmkv, "scrollBarEnabled", true)
    }
    val readModeEnabled by lazy {
        BooleanMmkvData(mmkv, "readModeEnabled", true)
    }
    val showTileBounds by lazy {
        BooleanMmkvData(mmkv, "showTileBounds", false)
    }

    /*
     * other
     */
    val photoListLayoutMode by lazy {
        StringMmkvData(mmkv, "photoListLayoutMode", GRID.name)
    }
    val showOriginImage by lazy {
        BooleanMmkvData(mmkv, "showOriginImage", false)
    }
    val logLevel by lazy {
        StringMmkvData(
            mmkv,
            "logLevel",
            if (BuildConfig.DEBUG) Logger.Level.DEBUG.name else Logger.Level.INFO.name
        )
    }

//    val bitmapQualityState = bitmapQuality.stateFlow.map { value ->
//        when (value) {
//            "LOW" -> BitmapConfig.LowQuality
//            "HIGH" -> BitmapConfig.HighQuality
//            else -> null
//        }
//    }

    val bitmapQualityValue: BitmapConfig?
        get() = when (bitmapQuality.value) {
            "LOW" -> BitmapConfig.LowQuality
            "HIGH" -> BitmapConfig.HighQuality
            else -> null
        }

//    val colorSpaceState = colorSpace.stateFlow.map { value ->
//        if (VERSION.SDK_INT >= VERSION_CODES.O) {
//            when (value) {
//                "Default" -> null
//                else -> ColorSpace.get(ColorSpace.Named.valueOf(value))
//            }
//        } else {
//            null
//        }
//    }

    @get:RequiresApi(VERSION_CODES.O)
    val colorSpaceValue: ColorSpace?
        get() = if (VERSION.SDK_INT >= VERSION_CODES.O) {
            when (val value = colorSpace.value) {
                "Default" -> null
                else -> ColorSpace.get(ColorSpace.Named.valueOf(value))
            }
        } else {
            null
        }

//    val disabledMemoryCacheState = disabledMemoryCache.stateFlow.map { value ->
//        if(value) DISABLED else ENABLED
//    }

    val disabledMemoryCacheValue: CachePolicy
        get() = if (disabledMemoryCache.value) DISABLED else ENABLED

//    val disabledDownloadCacheState = disabledDownloadCache.stateFlow.map { value ->
//        if(value) DISABLED else ENABLED
//    }

    val disabledDownloadCacheValue: CachePolicy
        get() = if (disabledDownloadCache.value) DISABLED else ENABLED

//    val disabledResultCacheState = disabledResultCache.stateFlow.map { value ->
//        if(value) DISABLED else ENABLED
//    }

    val disabledResultCacheValue: CachePolicy
        get() = if (disabledResultCache.value) DISABLED else ENABLED

    private val listFlows = listOf(
        bitmapQuality,
        colorSpace,
        inPreferQualityOverSpeed,

        disabledMemoryCache,
        disabledResultCache,
        disabledDownloadCache,
        disallowReuseBitmap,

        resizePrecision,
        resizeScale,
        longImageResizeScale,
        otherImageResizeScale,

        ignoreExifOrientation,
        saveCellularTrafficInList,
        disallowAnimatedImageInList,
    )

    val listsMergedFlow: Flow<Any> = listFlows.map { it.sharedFlow }.merge()
    val listsCombinedFlow: Flow<Any> = combine(listFlows.map { it.stateFlow }) { it.joinToString() }

    private val viewerFlows = listOf(
        bitmapQuality,
        colorSpace,
        inPreferQualityOverSpeed,

        disabledMemoryCache,
        disabledResultCache,
        disabledDownloadCache,
        disallowReuseBitmap,

        ignoreExifOrientation,
    )
    val viewersMergedFlow: Flow<Any> = viewerFlows.map { it.sharedFlow }.merge()
    val viewersCombinedFlow: Flow<Any> =
        combine(viewerFlows.map { it.stateFlow }) { it.joinToString() }

    fun buildViewerImageOptions(): ImageOptions = ImageOptions {
        memoryCachePolicy(disabledMemoryCacheValue)
        downloadCachePolicy(disabledDownloadCacheValue)
        disallowReuseBitmap(disallowReuseBitmap.value)
        ignoreExifOrientation(ignoreExifOrientation.value)
        preferQualityOverSpeed(VERSION.SDK_INT <= VERSION_CODES.M && inPreferQualityOverSpeed.value)
        bitmapConfig(bitmapQualityValue)
        if (VERSION.SDK_INT >= VERSION_CODES.O) {
            colorSpace(colorSpaceValue)
        }
    }

    fun buildListImageOptions(): ImageOptions = ImageOptions {
        memoryCachePolicy(disabledMemoryCacheValue)
        downloadCachePolicy(disabledDownloadCacheValue)
        disallowReuseBitmap(disallowReuseBitmap.value)
        ignoreExifOrientation(ignoreExifOrientation.value)
        preferQualityOverSpeed(VERSION.SDK_INT <= VERSION_CODES.M && inPreferQualityOverSpeed.value)
        bitmapConfig(bitmapQualityValue)
        if (VERSION.SDK_INT >= VERSION_CODES.O) {
            colorSpace(colorSpaceValue)
        }

        resizePrecision(
            when (resizePrecision.value) {
                "LongImageClipMode" -> LongImageClipPrecisionDecider(precision = SAME_ASPECT_RATIO)
                else -> PrecisionDecider(Precision.valueOf(resizePrecision.value))
            }
        )
        resizeScale(
            when (resizeScale.value) {
                "LongImageMode" -> LongImageScaleDecider(
                    longImage = Scale.valueOf(value = longImageResizeScale.value),
                    otherImage = Scale.valueOf(value = otherImageResizeScale.value)
                )

                else -> ScaleDecider(Scale.valueOf(value = resizeScale.value))
            }
        )
        disallowAnimatedImage(disallowAnimatedImageInList.value)
        pauseLoadWhenScrolling(pauseLoadWhenScrollInList.value)
        saveCellularTraffic(saveCellularTrafficInList.value)
    }
}