package com.github.panpf.sketch.sample

import androidx.compose.ui.Alignment
import androidx.compose.ui.layout.ContentScale
import com.github.panpf.sketch.PlatformContext
import com.github.panpf.sketch.cache.CachePolicy
import com.github.panpf.sketch.cache.CachePolicy.DISABLED
import com.github.panpf.sketch.cache.CachePolicy.ENABLED
import com.github.panpf.sketch.resize.LongImagePrecisionDecider
import com.github.panpf.sketch.resize.LongImageScaleDecider
import com.github.panpf.sketch.resize.Precision
import com.github.panpf.sketch.resize.Precision.SAME_ASPECT_RATIO
import com.github.panpf.sketch.resize.PrecisionDecider
import com.github.panpf.sketch.resize.Scale
import com.github.panpf.sketch.resize.ScaleDecider
import com.github.panpf.sketch.sample.ui.util.stateCombine
import com.github.panpf.sketch.sample.ui.util.stateMap
import com.github.panpf.sketch.sample.ui.util.valueOf
import com.github.panpf.sketch.sample.util.SettingsStateFlow
import com.github.panpf.sketch.sample.util.booleanSettingsStateFlow
import com.github.panpf.sketch.sample.util.enumSettingsStateFlow
import com.github.panpf.sketch.sample.util.intSettingsStateFlow
import com.github.panpf.sketch.sample.util.isDebugMode
import com.github.panpf.sketch.sample.util.stringSettingsStateFlow
import com.github.panpf.sketch.util.Logger
import kotlinx.coroutines.flow.StateFlow

expect val PlatformContext.appSettings: AppSettings

// TODO expect class AppSettings
class AppSettings(val context: PlatformContext) {

    // -------------------------------------- image --------------------------------------

    val memoryCacheName: SettingsStateFlow<Boolean> by lazy {
        booleanSettingsStateFlow(context, "memoryCache", true)
    }
    val memoryCache: StateFlow<CachePolicy> =
        memoryCacheName.stateMap { if (it) ENABLED else DISABLED }

    val resultCacheName: SettingsStateFlow<Boolean> by lazy {
        booleanSettingsStateFlow(context, "resultCache", true)
    }
    val resultCache: StateFlow<CachePolicy> =
        resultCacheName.stateMap { if (it) ENABLED else DISABLED }

    val downloadCacheName: SettingsStateFlow<Boolean> by lazy {
        booleanSettingsStateFlow(context, "downloadCache", true)
    }
    val downloadCache: StateFlow<CachePolicy> =
        downloadCacheName.stateMap { if (it) ENABLED else DISABLED }

    val bitmapQuality: SettingsStateFlow<String> by lazy {
        stringSettingsStateFlow(context, "bitmapQuality", "Default")
    }
    // TODO actual class AppSettings.android {
    //     val bitmapQualityName
    //     val bitmapQuality
    //    }

    val colorSpace: SettingsStateFlow<String> by lazy {
        stringSettingsStateFlow(context, "colorSpace", "Default")
    }

    val inPreferQualityOverSpeed: SettingsStateFlow<Boolean> by lazy {
        booleanSettingsStateFlow(context, "inPreferQualityOverSpeed", false)
    }

    val cacheDecodeTimeoutFrame: SettingsStateFlow<Boolean> by lazy {
        booleanSettingsStateFlow(context, "cacheDecodeTimeoutFrame", true)
    }


    // -------------------------------------- list image --------------------------------------

    val precisionName: SettingsStateFlow<String> by lazy {
        stringSettingsStateFlow(context, "precision1", "LongImageMode")
    }
    val precision: StateFlow<PrecisionDecider> = precisionName.stateMap {
        if (it == "LongImageMode") {
            LongImagePrecisionDecider(longImage = SAME_ASPECT_RATIO)
        } else {
            PrecisionDecider(Precision.valueOf(it))
        }
    }

    val scaleName: SettingsStateFlow<String> by lazy {
        stringSettingsStateFlow(context, "scale", "LongImageMode")
    }
    val longImageScale: SettingsStateFlow<Scale> by lazy {
        enumSettingsStateFlow(
            context = context,
            key = "longImageScale",
            initialize = Scale.START_CROP,
            convert = { Scale.valueOf(it) },
        )
    }
    val otherImageScale: SettingsStateFlow<Scale> by lazy {
        enumSettingsStateFlow(
            context = context,
            key = "otherImageScale",
            initialize = Scale.CENTER_CROP,
            convert = { Scale.valueOf(it) },
        )
    }
    val scale: StateFlow<ScaleDecider> =
        stateCombine(listOf(scaleName, longImageScale, otherImageScale)) {
            val scaleName: String = it[0] as String
            val longImageScale: Scale = it[1] as Scale
            val otherImageScale: Scale = it[2] as Scale
            if (scaleName == "LongImageMode") {
                LongImageScaleDecider(
                    longImage = longImageScale,
                    otherImage = otherImageScale
                )
            } else {
                ScaleDecider(Scale.valueOf(value = scaleName))
            }
        }

    val saveCellularTrafficInList: SettingsStateFlow<Boolean> by lazy {
        booleanSettingsStateFlow(context, "saveCellularTrafficInList", false)
    }

    val disallowAnimatedImageInList: SettingsStateFlow<Boolean> by lazy {
        booleanSettingsStateFlow(context, "disallowAnimatedImageInList", false)
    }

    val showMimeTypeLogoInList: SettingsStateFlow<Boolean> by lazy {
        booleanSettingsStateFlow(context, "showMimeTypeLogoInList", true)
    }

    val showProgressIndicatorInList: SettingsStateFlow<Boolean> by lazy {
        booleanSettingsStateFlow(context, "showProgressIndicatorInList", true)
    }

    val showDataFromLogoInList: SettingsStateFlow<Boolean> by lazy {
        booleanSettingsStateFlow(context, "showDataFromLogoInList", true)
    }

    val pauseLoadWhenScrollInList: SettingsStateFlow<Boolean> by lazy {
        booleanSettingsStateFlow(context, "pauseLoadWhenScrollInList", false)
    }


    // -------------------------------------- viewer image --------------------------------------

    val contentScaleName: SettingsStateFlow<String> by lazy {
        stringSettingsStateFlow(context, "contentScale", "Fit")
    }
    val contentScale: StateFlow<ContentScale> =
        contentScaleName.stateMap { ContentScale.valueOf(it) }

    val alignmentName: SettingsStateFlow<String> by lazy {
        stringSettingsStateFlow(context, "alignment", "Center")
    }
    val alignment: StateFlow<Alignment> = alignmentName.stateMap { Alignment.valueOf(it) }

    val scrollBarEnabled: SettingsStateFlow<Boolean> by lazy {
        booleanSettingsStateFlow(context, "scrollBarEnabled", true)
    }

    val readModeEnabled: SettingsStateFlow<Boolean> by lazy {
        booleanSettingsStateFlow(context, "readModeEnabled", true)
    }

    val showTileBounds: SettingsStateFlow<Boolean> by lazy {
        booleanSettingsStateFlow(context, "showTileBounds", false)
    }

    val showOriginImage: SettingsStateFlow<Boolean> by lazy {
        booleanSettingsStateFlow(context, "showOriginImage", false)
    }


    // -------------------------------------- other --------------------------------------

    val staggeredGridMode: SettingsStateFlow<Boolean> by lazy {
        booleanSettingsStateFlow(
            context = context,
            key = "staggeredGridMode",
            initialize = false,
        )
    }

    val logLevel: SettingsStateFlow<Logger.Level> by lazy {
        val defaultState = if (isDebugMode()) Logger.Level.Debug else Logger.Level.Info
        enumSettingsStateFlow(context, "newNewLogLevel2", defaultState) { Logger.Level.valueOf(it) }
    }

    val httpClient: SettingsStateFlow<String> by lazy {
        stringSettingsStateFlow(context, key = "httpClient", initialize = "Ktor")
    }

    val videoFrameDecoder: SettingsStateFlow<String> by lazy {
        stringSettingsStateFlow(context, key = "videoFrameDecoder", initialize = "FFmpeg")
    }

    val gifDecoder: SettingsStateFlow<String> by lazy {
        stringSettingsStateFlow(context, key = "gifDecoder", initialize = "KoralGif")
    }

    val networkParallelismLimited: SettingsStateFlow<Int> by lazy {
        intSettingsStateFlow(context, key = "networkParallelismLimited", initialize = 10)
    }

    val decodeParallelismLimited: SettingsStateFlow<Int> by lazy {
        intSettingsStateFlow(context, key = "decodeParallelismLimited", initialize = 4)
    }

    val composePage: SettingsStateFlow<Boolean> by lazy {
        booleanSettingsStateFlow(context, "composePage", true)
    }

    val currentPageIndex: SettingsStateFlow<Int> by lazy {
        intSettingsStateFlow(context, "currentPageIndex", 0)
    }

    val pagerGuideShowed: SettingsStateFlow<Boolean> by lazy {
        booleanSettingsStateFlow(context, "pagerGuideShowed", false)
    }

    companion object
}