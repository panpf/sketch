package com.github.panpf.sketch.sample

import com.github.panpf.sketch.PlatformContext
import com.github.panpf.sketch.cache.CachePolicy
import com.github.panpf.sketch.cache.CachePolicy.DISABLED
import com.github.panpf.sketch.cache.CachePolicy.ENABLED
import com.github.panpf.sketch.request.ImageOptions
import com.github.panpf.sketch.request.pauseLoadWhenScrolling
import com.github.panpf.sketch.request.saveCellularTraffic
import com.github.panpf.sketch.resize.LongImageClipPrecisionDecider
import com.github.panpf.sketch.resize.LongImageStartCropScaleDecider
import com.github.panpf.sketch.resize.Precision
import com.github.panpf.sketch.resize.Precision.SAME_ASPECT_RATIO
import com.github.panpf.sketch.resize.PrecisionDecider
import com.github.panpf.sketch.resize.Scale
import com.github.panpf.sketch.resize.ScaleDecider
import com.github.panpf.sketch.sample.ui.model.PhotoGridMode
import com.github.panpf.sketch.sample.util.ParamLazy
import com.github.panpf.sketch.sample.util.SettingsStateFlow
import com.github.panpf.sketch.sample.util.booleanSettingsStateFlow
import com.github.panpf.sketch.sample.util.enumSettingsStateFlow
import com.github.panpf.sketch.sample.util.intSettingsStateFlow
import com.github.panpf.sketch.sample.util.stringSettingsStateFlow
import com.github.panpf.sketch.util.Logger
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

private val appSettingsServiceLazy = ParamLazy<PlatformContext, AppSettings> { AppSettings(it) }

val PlatformContext.appSettings: AppSettings
    get() = appSettingsServiceLazy.get(this)

expect fun isDebugMode(): Boolean

class AppSettings(val context: PlatformContext) {

    val photoGridMode: SettingsStateFlow<PhotoGridMode> by lazy {
        enumSettingsStateFlow(
            context = context,
            key = "photoGridMode",
            initialize = PhotoGridMode.SQUARE,
            convert = { PhotoGridMode.valueOf(it) },
        )
    }

    /*
     * list config
     */
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

    /*
     * image load
     */
    val bitmapQuality by lazy {
        stringSettingsStateFlow(context, "bitmapQuality", "Default")
    }
    val colorSpace by lazy {
        stringSettingsStateFlow(context, "colorSpace", "Default")
    }
    val inPreferQualityOverSpeed: SettingsStateFlow<Boolean> by lazy {
        booleanSettingsStateFlow(context, "inPreferQualityOverSpeed", false)
    }

    val memoryCache: SettingsStateFlow<Boolean> by lazy {
        booleanSettingsStateFlow(context, "memoryCache", true)
    }
    val resultCache: SettingsStateFlow<Boolean> by lazy {
        booleanSettingsStateFlow(context, "resultCache", true)
    }
    val downloadCache: SettingsStateFlow<Boolean> by lazy {
        booleanSettingsStateFlow(context, "downloadCache", true)
    }

    val precision by lazy {
        stringSettingsStateFlow(context, "precision", "LongImageClipMode")
    }
    val scale by lazy {
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

    val saveCellularTrafficInList: SettingsStateFlow<Boolean> by lazy {
        booleanSettingsStateFlow(context, "saveCellularTrafficInList", false)
    }
    val disallowAnimatedImageInList: SettingsStateFlow<Boolean> by lazy {
        booleanSettingsStateFlow(context, "disallowAnimatedImageInList", false)
    }

    /*
     * detail
     */
    val contentScale: SettingsStateFlow<String> by lazy {
        stringSettingsStateFlow(context, "contentScale", "Fit")
    }
    val alignment: SettingsStateFlow<String> by lazy {
        stringSettingsStateFlow(context, "alignment", "Center")
    }
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

    /*
     * other
     */
    val logLevel by lazy {
        val defaultState = if (isDebugMode()) Logger.Level.Debug else Logger.Level.Info
        enumSettingsStateFlow(context, "newNewLogLevel2", defaultState) { Logger.Level.valueOf(it) }
    }
    val httpClient by lazy {
        stringSettingsStateFlow(context, key = "httpClient", initialize = "Ktor")
    }
    val videoFrameDecoder by lazy {
        stringSettingsStateFlow(context, key = "videoFrameDecoder", initialize = "FFmpeg")
    }
    val gifDecoder by lazy {
        stringSettingsStateFlow(context, key = "gifDecoder", initialize = "KoralGif")
    }

    // Only for Android
    val composePage: SettingsStateFlow<Boolean> by lazy {
        booleanSettingsStateFlow(context, "composePage", false)
    }
    val currentPageIndex: SettingsStateFlow<Int> by lazy {
        intSettingsStateFlow(context, "currentPageIndex", 0)
    }

    private val memoryCacheValue: CachePolicy
        get() = if (memoryCache.value) ENABLED else DISABLED
    private val downloadCacheValue: CachePolicy
        get() = if (downloadCache.value) ENABLED else DISABLED
    private val resultCacheValue: CachePolicy
        get() = if (resultCache.value) ENABLED else DISABLED
    private val precisionValue: PrecisionDecider
        get() = when (precision.value) {
            "LongImageClipMode" -> LongImageClipPrecisionDecider(longImage = SAME_ASPECT_RATIO)
            else -> PrecisionDecider(Precision.valueOf(precision.value))
        }
    private val scaleValue: ScaleDecider
        get() = when (scale.value) {
            "LongImageMode" -> LongImageStartCropScaleDecider(
                longImage = longImageScale.value,
                otherImage = otherImageScale.value
            )

            else -> ScaleDecider(Scale.valueOf(value = scale.value))
        }

    private val listSettingFlows = listOf(
        bitmapQuality,  // Only for Android
        colorSpace,  // Only for Android
        inPreferQualityOverSpeed,  // Only for Android

        memoryCache,
        resultCache,
        downloadCache,

        precision,
        scale,
        longImageScale,
        otherImageScale,

        saveCellularTrafficInList,
        disallowAnimatedImageInList,
    )

    val listsCombinedFlow: Flow<Any> = combine(listSettingFlows) { it.joinToString() }

    private val viewerSettingFlows = listOf(
        bitmapQuality,  // Only for Android
        colorSpace,  // Only for Android
        inPreferQualityOverSpeed,  // Only for Android

        memoryCache,
        resultCache,
        downloadCache,
    )
    val viewersCombinedFlow: Flow<Any> =
        combine(viewerSettingFlows) { it.joinToString() }

    fun buildListImageOptions(): ImageOptions = ImageOptions {
        pauseLoadWhenScrolling(pauseLoadWhenScrollInList.value)

        platformBuildImageOptions(this@AppSettings)

        memoryCachePolicy(memoryCacheValue)
        resultCachePolicy(resultCacheValue)
        downloadCachePolicy(downloadCacheValue)

        precision(precisionValue)
        scale(scaleValue)

        saveCellularTraffic(saveCellularTrafficInList.value)
        disallowAnimatedImage(disallowAnimatedImageInList.value)
    }

    fun buildViewerImageOptions(): ImageOptions = ImageOptions {
        platformBuildImageOptions(this@AppSettings)

        memoryCachePolicy(memoryCacheValue)
        resultCachePolicy(resultCacheValue)
        downloadCachePolicy(downloadCacheValue)
    }
}

expect fun ImageOptions.Builder.platformBuildImageOptions(appSettings: AppSettings)