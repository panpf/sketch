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
import com.github.panpf.sketch.resize.LongImagePrecisionDecider
import com.github.panpf.sketch.resize.LongImageScaleDecider
import com.github.panpf.sketch.resize.Precision
import com.github.panpf.sketch.resize.Precision.SAME_ASPECT_RATIO
import com.github.panpf.sketch.resize.PrecisionDecider
import com.github.panpf.sketch.resize.Scale
import com.github.panpf.sketch.resize.ScaleDecider
import com.github.panpf.sketch.sample.ui.util.valueOf
import com.github.panpf.sketch.sample.util.SettingsStateFlow
import com.github.panpf.sketch.sample.util.booleanSettingsStateFlow
import com.github.panpf.sketch.sample.util.enumSettingsStateFlow
import com.github.panpf.sketch.sample.util.intSettingsStateFlow
import com.github.panpf.sketch.sample.util.isDebugMode
import com.github.panpf.sketch.sample.util.stateMap
import com.github.panpf.sketch.sample.util.stringSettingsStateFlow
import com.github.panpf.sketch.util.Logger
import kotlinx.coroutines.flow.StateFlow

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
expect class AppSettings(context: PlatformContext) {

    // -------------------------------------- image --------------------------------------
    val memoryCacheName: SettingsStateFlow<Boolean>
    val memoryCache: StateFlow<CachePolicy>

    val resultCacheName: SettingsStateFlow<Boolean>
    val resultCache: StateFlow<CachePolicy>

    val downloadCacheName: SettingsStateFlow<Boolean>
    val downloadCache: StateFlow<CachePolicy>

    val colorTypeName: SettingsStateFlow<String>
    val colorType: StateFlow<BitmapColorType?>

    val colorSpaceName: SettingsStateFlow<String>
    val colorSpace: StateFlow<String?>


    // -------------------------------------- list image --------------------------------------

    val listContentScaleName: SettingsStateFlow<String>
    val listContentScale: StateFlow<ContentScale>

    val listAlignmentName: SettingsStateFlow<String>
    val listAlignment: StateFlow<Alignment>

    val resizeOnDrawEnabled: SettingsStateFlow<Boolean>

    val precisionName: SettingsStateFlow<String>
    val precision: StateFlow<PrecisionDecider>

    val scaleName: SettingsStateFlow<String>
    val longImageScale: SettingsStateFlow<Scale>
    val otherImageScale: SettingsStateFlow<Scale>
    // stateCombine will cause UI lag
//    val scale: StateFlow<ScaleDecider>

    val saveCellularTrafficInList: SettingsStateFlow<Boolean>

    val disallowAnimatedImageInList: SettingsStateFlow<Boolean>

    val showMimeTypeLogoInList: SettingsStateFlow<Boolean>

    val showProgressIndicatorInList: SettingsStateFlow<Boolean>

    val showDataFromLogoInList: SettingsStateFlow<Boolean>

    val pauseLoadWhenScrollInList: SettingsStateFlow<Boolean>


    // -------------------------------------- animated image --------------------------------------

    val repeatCount: SettingsStateFlow<Int>

    // -------------------------------------- viewer image --------------------------------------

    val contentScaleName: SettingsStateFlow<String>
    val contentScale: StateFlow<ContentScale>

    val alignmentName: SettingsStateFlow<String>
    val alignment: StateFlow<Alignment>

    val scrollBarEnabled: SettingsStateFlow<Boolean>

    val readModeEnabled: SettingsStateFlow<Boolean>

    val showTileBounds: SettingsStateFlow<Boolean>

    val showOriginImage: SettingsStateFlow<Boolean>

    val thumbnailMode: SettingsStateFlow<Boolean>


    // -------------------------------------- other --------------------------------------

    val staggeredGridMode: SettingsStateFlow<Boolean>

    val logLevel: SettingsStateFlow<Logger.Level>

    val zoomImageLogLevel: SettingsStateFlow<com.github.panpf.zoomimage.util.Logger.Level>

    val httpClient: SettingsStateFlow<String>

    val networkParallelismLimited: SettingsStateFlow<Int>

    val decodeParallelismLimited: SettingsStateFlow<Int>

    val currentPageIndex: SettingsStateFlow<Int>

    val pagerGuideShowed: SettingsStateFlow<Boolean>

    val darkMode: SettingsStateFlow<DarkMode>
}

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
abstract class BaseAppSettings constructor(val context: PlatformContext) {

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

    val colorTypeName: SettingsStateFlow<String> by lazy {
        stringSettingsStateFlow(context, "colorType", "Default")
    }
    val colorType: StateFlow<BitmapColorType?> =
        colorTypeName.stateMap {
            when (it) {
                "Default" -> null
                "LowQuality" -> LowQualityColorType
                "HighQuality" -> HighQualityColorType
                else -> BitmapColorType(it)
            }
        }

    val colorSpaceName: SettingsStateFlow<String> by lazy {
        stringSettingsStateFlow(context, "colorSpace", "Default")
    }
    val colorSpace: StateFlow<String?> =
        colorSpaceName.stateMap {
            it.takeIf { it != "Default" }
        }


    // -------------------------------------- list image --------------------------------------

    val listContentScaleName: SettingsStateFlow<String> by lazy {
        stringSettingsStateFlow(context, "listContentScaleName", "Crop")
    }
    val listContentScale: StateFlow<ContentScale> =
        listContentScaleName.stateMap { ContentScale.valueOf(it) }

    val listAlignmentName: SettingsStateFlow<String> by lazy {
        stringSettingsStateFlow(context, "listAlignmentName", "Center")
    }
    val listAlignment: StateFlow<Alignment> =
        listAlignmentName.stateMap { Alignment.valueOf(it) }

    val resizeOnDrawEnabled: SettingsStateFlow<Boolean> by lazy {
        booleanSettingsStateFlow(context, "resizeOnDrawEnabled", true)
    }

    val precisionName: SettingsStateFlow<String> by lazy {
        stringSettingsStateFlow(context, "precision1", "LongImageMode")
    }
    val precision: StateFlow<PrecisionDecider> = precisionName.stateMap {
        buildPrecision(it)
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
    // stateCombine will cause UI lag
//    val scale: StateFlow<ScaleDecider> =
//        stateCombine(listOf(scaleName, longImageScale, otherImageScale)) {
//            val scaleName: String = it[0] as String
//            val longImageScale: Scale = it[1] as Scale
//            val otherImageScale: Scale = it[2] as Scale
//            buildScale(scaleName, longImageScale, otherImageScale)
//        }

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


    // -------------------------------------- animated image --------------------------------------

    val repeatCount: SettingsStateFlow<Int> by lazy {
        intSettingsStateFlow(context, "repeatCount", -1)
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
        booleanSettingsStateFlow(context, "showOriginImage", true)
    }

    val thumbnailMode: SettingsStateFlow<Boolean> by lazy {
        booleanSettingsStateFlow(context, "thumbnailMode", false)
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

    val zoomImageLogLevel: SettingsStateFlow<com.github.panpf.zoomimage.util.Logger.Level> by lazy {
        val defaultState =
            if (isDebugMode()) com.github.panpf.zoomimage.util.Logger.Level.Debug else com.github.panpf.zoomimage.util.Logger.Level.Info
        enumSettingsStateFlow(
            context,
            "zoomImageLogLevel",
            defaultState
        ) { com.github.panpf.zoomimage.util.Logger.Level.valueOf(it) }
    }

    val httpClient: SettingsStateFlow<String> by lazy {
        stringSettingsStateFlow(context, key = "httpClient", initialize = "Ktor")
    }

    val networkParallelismLimited: SettingsStateFlow<Int> by lazy {
        intSettingsStateFlow(context, key = "networkParallelismLimited", initialize = 10)
    }

    val decodeParallelismLimited: SettingsStateFlow<Int> by lazy {
        intSettingsStateFlow(context, key = "decodeParallelismLimited", initialize = 4)
    }

    val currentPageIndex: SettingsStateFlow<Int> by lazy {
        intSettingsStateFlow(context, "currentPageIndex", 0)
    }

    val pagerGuideShowed: SettingsStateFlow<Boolean> by lazy {
        booleanSettingsStateFlow(context, "pagerGuideShowed", false)
    }

    val darkMode: SettingsStateFlow<DarkMode> by lazy {
        enumSettingsStateFlow(
            context = context,
            key = "darkMode",
            initialize = platformSupportedDarkModes().first(),
            convert = DarkMode::valueOf
        )
    }
}

enum class DarkMode {
    SYSTEM, LIGHT, DARK
}

expect fun platformSupportedDarkModes(): List<DarkMode>

fun buildPrecision(precisionName: String): PrecisionDecider {
    return if (precisionName == "LongImageMode") {
        LongImagePrecisionDecider(longImage = SAME_ASPECT_RATIO)
    } else {
        PrecisionDecider(Precision.valueOf(precisionName))
    }
}

fun buildScale(scaleName: String, longImageScale: Scale, otherImageScale: Scale): ScaleDecider {
    return if (scaleName == "LongImageMode") {
        LongImageScaleDecider(
            longImage = longImageScale,
            otherImage = otherImageScale
        )
    } else {
        ScaleDecider(Scale.valueOf(value = scaleName))
    }
}