package com.github.panpf.sketch.sample

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
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
import com.github.panpf.zoomimage.zoom.AlignmentCompat
import com.github.panpf.zoomimage.zoom.ContentScaleCompat
import com.github.panpf.zoomimage.zoom.name
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

private val appSettingsServiceLazy = ParamLazy<PlatformContext, AppSettings> { AppSettings(it) }

val PlatformContext.appSettings: AppSettings
    get() = appSettingsServiceLazy.get(this)

expect fun createDataStore(context: PlatformContext): DataStore<Preferences>

expect fun isDebugMode(): Boolean

class AppSettings(val context: PlatformContext) {

    private val dataStore = createDataStore(context)

    val photoGridMode: SettingsStateFlow<PhotoGridMode> by lazy {
        enumSettingsStateFlow(
            key = "photoGridMode",
            initialize = PhotoGridMode.SQUARE,
            convert = { PhotoGridMode.valueOf(it) },
            dataStore = dataStore
        )
    }

    /*
     * list config
     */
    val showMimeTypeLogoInList: SettingsStateFlow<Boolean> by lazy {
        booleanSettingsStateFlow("showMimeTypeLogoInList", true, dataStore)
    }
    val showProgressIndicatorInList: SettingsStateFlow<Boolean> by lazy {
        booleanSettingsStateFlow("showProgressIndicatorInList", true, dataStore)
    }
    val showDataFromLogoInList: SettingsStateFlow<Boolean> by lazy {
        booleanSettingsStateFlow("showDataFromLogoInList", true, dataStore)
    }
    val pauseLoadWhenScrollInList: SettingsStateFlow<Boolean> by lazy {
        booleanSettingsStateFlow("pauseLoadWhenScrollInList", false, dataStore)
    }

    /*
     * image load
     */
    val bitmapQuality by lazy {
        stringSettingsStateFlow("bitmapQuality", "Default", dataStore)
    }
    val colorSpace by lazy {
        stringSettingsStateFlow("colorSpace", "Default", dataStore)
    }
    val inPreferQualityOverSpeed: SettingsStateFlow<Boolean> by lazy {
        booleanSettingsStateFlow("inPreferQualityOverSpeed", false, dataStore)
    }

    val disabledMemoryCache: SettingsStateFlow<Boolean> by lazy {
        booleanSettingsStateFlow("disabledBitmapMemoryCache", false, dataStore)
    }
    val disabledResultCache: SettingsStateFlow<Boolean> by lazy {
        booleanSettingsStateFlow("disabledBitmapResultCache", false, dataStore)
    }
    val disabledDownloadCache: SettingsStateFlow<Boolean> by lazy {
        booleanSettingsStateFlow("disabledDownloadCache", false, dataStore)
    }

    val precision by lazy {
        stringSettingsStateFlow("precision", "LongImageClipMode", dataStore)
    }
    val scale by lazy {
        stringSettingsStateFlow("scale", "LongImageMode", dataStore)
    }
    val longImageScale: SettingsStateFlow<Scale> by lazy {
        enumSettingsStateFlow(
            key = "longImageScale",
            initialize = Scale.START_CROP,
            convert = { Scale.valueOf(it) },
            dataStore = dataStore
        )
    }
    val otherImageScale: SettingsStateFlow<Scale> by lazy {
        enumSettingsStateFlow(
            key = "otherImageScale",
            initialize = Scale.CENTER_CROP,
            convert = { Scale.valueOf(it) },
            dataStore = dataStore
        )
    }

    val ignoreExifOrientation: SettingsStateFlow<Boolean> by lazy {
        booleanSettingsStateFlow("ignoreExifOrientation", false, dataStore)
    }
    val saveCellularTrafficInList: SettingsStateFlow<Boolean> by lazy {
        booleanSettingsStateFlow("saveCellularTrafficInList", false, dataStore)
    }
    val disallowAnimatedImageInList: SettingsStateFlow<Boolean> by lazy {
        booleanSettingsStateFlow("disallowAnimatedImageInList", false, dataStore)
    }

    /*
     * detail
     */
    val contentScale: SettingsStateFlow<String> by lazy {
        stringSettingsStateFlow("contentScale", ContentScaleCompat.Fit.name, dataStore)
    }
    val alignment: SettingsStateFlow<String> by lazy {
        stringSettingsStateFlow("alignment", AlignmentCompat.Center.name, dataStore)
    }
    val scrollBarEnabled: SettingsStateFlow<Boolean> by lazy {
        booleanSettingsStateFlow("scrollBarEnabled", true, dataStore)
    }
    val readModeEnabled: SettingsStateFlow<Boolean> by lazy {
        booleanSettingsStateFlow("readModeEnabled", true, dataStore)
    }
    val showTileBounds: SettingsStateFlow<Boolean> by lazy {
        booleanSettingsStateFlow("showTileBounds", false, dataStore)
    }
    val showOriginImage: SettingsStateFlow<Boolean> by lazy {
        booleanSettingsStateFlow("showOriginImage", false, dataStore)
    }

    /*
     * other
     */
    val logLevel by lazy {
        val defaultState = if (isDebugMode()) Logger.Level.DEBUG else Logger.Level.INFO
        enumSettingsStateFlow(
            key = "logLevel",
            initialize = defaultState,
            convert = { Logger.Level.valueOf(it) },
            dataStore = dataStore
        )
    }

    // Only for Android
    val composePage: SettingsStateFlow<Boolean> by lazy {
        booleanSettingsStateFlow("composePage", false, dataStore)
    }
    val currentPageIndex: SettingsStateFlow<Int> by lazy {
        intSettingsStateFlow("currentPageIndex", 0, dataStore)
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
                longImage = longImageScale.value,
                otherImage = otherImageScale.value
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

        platformBuildImageOptions(this@AppSettings)

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
        platformBuildImageOptions(this@AppSettings)

        memoryCachePolicy(disabledMemoryCacheValue)
        resultCachePolicy(disabledResultCacheValue)
        downloadCachePolicy(disabledDownloadCacheValue)

        ignoreExifOrientation(ignoreExifOrientation.value)
    }
}

expect fun ImageOptions.Builder.platformBuildImageOptions(appSettings: AppSettings)