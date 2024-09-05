package com.github.panpf.sketch.sample

import androidx.compose.ui.Alignment
import androidx.compose.ui.layout.ContentScale
import com.github.panpf.sketch.PlatformContext
import com.github.panpf.sketch.cache.CachePolicy
import com.github.panpf.sketch.resize.LongImagePrecisionDecider
import com.github.panpf.sketch.resize.LongImageScaleDecider
import com.github.panpf.sketch.resize.Precision
import com.github.panpf.sketch.resize.Precision.SAME_ASPECT_RATIO
import com.github.panpf.sketch.resize.PrecisionDecider
import com.github.panpf.sketch.resize.Scale
import com.github.panpf.sketch.resize.ScaleDecider
import com.github.panpf.sketch.sample.util.SettingsStateFlow
import com.github.panpf.sketch.util.Logger
import kotlinx.coroutines.flow.StateFlow

expect val PlatformContext.appSettings: AppSettings

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
expect class AppSettings constructor(context: PlatformContext) {

    // -------------------------------------- image --------------------------------------
    val memoryCacheName: SettingsStateFlow<Boolean>
    val memoryCache: StateFlow<CachePolicy>

    val resultCacheName: SettingsStateFlow<Boolean>
    val resultCache: StateFlow<CachePolicy>

    val downloadCacheName: SettingsStateFlow<Boolean>
    val downloadCache: StateFlow<CachePolicy>


    // -------------------------------------- list image --------------------------------------

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


    // -------------------------------------- viewer image --------------------------------------

    val contentScaleName: SettingsStateFlow<String>
    val contentScale: StateFlow<ContentScale>

    val alignmentName: SettingsStateFlow<String>
    val alignment: StateFlow<Alignment>

    val scrollBarEnabled: SettingsStateFlow<Boolean>

    val readModeEnabled: SettingsStateFlow<Boolean>

    val showTileBounds: SettingsStateFlow<Boolean>

    val showOriginImage: SettingsStateFlow<Boolean>


    // -------------------------------------- other --------------------------------------

    val staggeredGridMode: SettingsStateFlow<Boolean>

    val logLevel: SettingsStateFlow<Logger.Level>

    val httpClient: SettingsStateFlow<String>

    val networkParallelismLimited: SettingsStateFlow<Int>

    val decodeParallelismLimited: SettingsStateFlow<Int>

    val currentPageIndex: SettingsStateFlow<Int>

    val pagerGuideShowed: SettingsStateFlow<Boolean>
}

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