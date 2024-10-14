package com.github.panpf.sketch.sample

import androidx.compose.ui.Alignment
import androidx.compose.ui.layout.ContentScale
import com.github.panpf.sketch.PlatformContext
import com.github.panpf.sketch.cache.CachePolicy
import com.github.panpf.sketch.cache.CachePolicy.DISABLED
import com.github.panpf.sketch.cache.CachePolicy.ENABLED
import com.github.panpf.sketch.decode.BitmapColorType
import com.github.panpf.sketch.decode.HighQualityColorType
import com.github.panpf.sketch.decode.LowQualityColorType
import com.github.panpf.sketch.resize.PrecisionDecider
import com.github.panpf.sketch.resize.Scale
import com.github.panpf.sketch.sample.ui.util.valueOf
import com.github.panpf.sketch.sample.util.ParamLazy
import com.github.panpf.sketch.sample.util.SettingsStateFlow
import com.github.panpf.sketch.sample.util.booleanSettingsStateFlow
import com.github.panpf.sketch.sample.util.enumSettingsStateFlow
import com.github.panpf.sketch.sample.util.intSettingsStateFlow
import com.github.panpf.sketch.sample.util.isDebugMode
import com.github.panpf.sketch.sample.util.stateMap
import com.github.panpf.sketch.sample.util.stringSettingsStateFlow
import com.github.panpf.sketch.util.Logger
import kotlinx.coroutines.flow.StateFlow


private val appSettingsLazy = ParamLazy<PlatformContext, AppSettings> { AppSettings(it) }

actual val PlatformContext.appSettings: AppSettings
    get() = appSettingsLazy.get(this)

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual class AppSettings actual constructor(val context: PlatformContext) {

    // -------------------------------------- image --------------------------------------

    actual val memoryCacheName: SettingsStateFlow<Boolean> by lazy {
        booleanSettingsStateFlow(context, "memoryCache", true)
    }
    actual val memoryCache: StateFlow<CachePolicy> =
        memoryCacheName.stateMap { if (it) ENABLED else DISABLED }

    actual val resultCacheName: SettingsStateFlow<Boolean> by lazy {
        booleanSettingsStateFlow(context, "resultCache", true)
    }
    actual val resultCache: StateFlow<CachePolicy> =
        resultCacheName.stateMap { if (it) ENABLED else DISABLED }

    actual val downloadCacheName: SettingsStateFlow<Boolean> by lazy {
        booleanSettingsStateFlow(context, "downloadCache", true)
    }
    actual val downloadCache: StateFlow<CachePolicy> =
        downloadCacheName.stateMap { if (it) ENABLED else DISABLED }

    actual val colorTypeName: SettingsStateFlow<String> by lazy {
        stringSettingsStateFlow(context, "colorType", "Default")
    }
    actual val colorType: StateFlow<BitmapColorType?> =
        colorTypeName.stateMap {
            when (it) {
                "Default" -> null
                "LowQuality" -> LowQualityColorType
                "HighQuality" -> HighQualityColorType
                else -> BitmapColorType(it)
            }
        }

    actual val colorSpaceName: SettingsStateFlow<String> by lazy {
        stringSettingsStateFlow(context, "colorSpace", "Default")
    }
    actual val colorSpace: StateFlow<String?> =
        colorSpaceName.stateMap {
            it.takeIf { it != "Default" }
        }

    val cacheDecodeTimeoutFrame: SettingsStateFlow<Boolean> by lazy {
        booleanSettingsStateFlow(context, "cacheDecodeTimeoutFrame", true)
    }


    // -------------------------------------- list image --------------------------------------

    actual val precisionName: SettingsStateFlow<String> by lazy {
        stringSettingsStateFlow(context, "precision1", "LongImageMode")
    }
    actual val precision: StateFlow<PrecisionDecider> = precisionName.stateMap {
        buildPrecision(it)
    }

    actual val scaleName: SettingsStateFlow<String> by lazy {
        stringSettingsStateFlow(context, "scale", "LongImageMode")
    }
    actual val longImageScale: SettingsStateFlow<Scale> by lazy {
        enumSettingsStateFlow(
            context = context,
            key = "longImageScale",
            initialize = Scale.START_CROP,
            convert = { Scale.valueOf(it) },
        )
    }
    actual val otherImageScale: SettingsStateFlow<Scale> by lazy {
        enumSettingsStateFlow(
            context = context,
            key = "otherImageScale",
            initialize = Scale.CENTER_CROP,
            convert = { Scale.valueOf(it) },
        )
    }
    // stateCombine will cause UI lag
//    actual val scale: StateFlow<ScaleDecider> =
//        stateCombine(listOf(scaleName, longImageScale, otherImageScale)) {
//            val scaleName: String = it[0] as String
//            val longImageScale: Scale = it[1] as Scale
//            val otherImageScale: Scale = it[2] as Scale
//            buildScale(scaleName, longImageScale, otherImageScale)
//        }

    actual val saveCellularTrafficInList: SettingsStateFlow<Boolean> by lazy {
        booleanSettingsStateFlow(context, "saveCellularTrafficInList", false)
    }

    actual val disallowAnimatedImageInList: SettingsStateFlow<Boolean> by lazy {
        booleanSettingsStateFlow(context, "disallowAnimatedImageInList", false)
    }

    actual val showMimeTypeLogoInList: SettingsStateFlow<Boolean> by lazy {
        booleanSettingsStateFlow(context, "showMimeTypeLogoInList", true)
    }

    actual val showProgressIndicatorInList: SettingsStateFlow<Boolean> by lazy {
        booleanSettingsStateFlow(context, "showProgressIndicatorInList", true)
    }

    actual val showDataFromLogoInList: SettingsStateFlow<Boolean> by lazy {
        booleanSettingsStateFlow(context, "showDataFromLogoInList", true)
    }

    actual val pauseLoadWhenScrollInList: SettingsStateFlow<Boolean> by lazy {
        booleanSettingsStateFlow(context, "pauseLoadWhenScrollInList", false)
    }


    // -------------------------------------- animated image --------------------------------------

    actual val repeatCount: SettingsStateFlow<Int> by lazy {
        intSettingsStateFlow(context, "repeatCount", -1)
    }


    // -------------------------------------- viewer image --------------------------------------

    actual val contentScaleName: SettingsStateFlow<String> by lazy {
        stringSettingsStateFlow(context, "contentScale", "Fit")
    }
    actual val contentScale: StateFlow<ContentScale> =
        contentScaleName.stateMap { ContentScale.valueOf(it) }

    actual val alignmentName: SettingsStateFlow<String> by lazy {
        stringSettingsStateFlow(context, "alignment", "Center")
    }
    actual val alignment: StateFlow<Alignment> = alignmentName.stateMap { Alignment.valueOf(it) }

    actual val scrollBarEnabled: SettingsStateFlow<Boolean> by lazy {
        booleanSettingsStateFlow(context, "scrollBarEnabled", true)
    }

    actual val readModeEnabled: SettingsStateFlow<Boolean> by lazy {
        booleanSettingsStateFlow(context, "readModeEnabled", true)
    }

    actual val showTileBounds: SettingsStateFlow<Boolean> by lazy {
        booleanSettingsStateFlow(context, "showTileBounds", false)
    }

    actual val showOriginImage: SettingsStateFlow<Boolean> by lazy {
        booleanSettingsStateFlow(context, "showOriginImage", true)
    }


    // -------------------------------------- other --------------------------------------

    actual val staggeredGridMode: SettingsStateFlow<Boolean> by lazy {
        booleanSettingsStateFlow(
            context = context,
            key = "staggeredGridMode",
            initialize = false,
        )
    }

    actual val logLevel: SettingsStateFlow<Logger.Level> by lazy {
        val defaultState = if (isDebugMode()) Logger.Level.Debug else Logger.Level.Info
        enumSettingsStateFlow(context, "newNewLogLevel2", defaultState) { Logger.Level.valueOf(it) }
    }

    actual val httpClient: SettingsStateFlow<String> by lazy {
        stringSettingsStateFlow(context, key = "httpClient", initialize = "Ktor")
    }

    actual val networkParallelismLimited: SettingsStateFlow<Int> by lazy {
        intSettingsStateFlow(context, key = "networkParallelismLimited", initialize = 10)
    }

    actual val decodeParallelismLimited: SettingsStateFlow<Int> by lazy {
        intSettingsStateFlow(context, key = "decodeParallelismLimited", initialize = 4)
    }

    actual val currentPageIndex: SettingsStateFlow<Int> by lazy {
        intSettingsStateFlow(context, "currentPageIndex", 0)
    }

    actual val pagerGuideShowed: SettingsStateFlow<Boolean> by lazy {
        booleanSettingsStateFlow(context, "pagerGuideShowed", false)
    }

    actual val darkMode: SettingsStateFlow<DarkMode> by lazy {
        enumSettingsStateFlow(
            context = context,
            key = "darkMode",
            initialize = platformSupportedDarkModes().first(),
            convert = DarkMode::valueOf
        )
    }
}