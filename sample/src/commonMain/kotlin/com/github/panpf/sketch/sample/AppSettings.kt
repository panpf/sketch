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

    val memoryCache: SettingsStateFlow<Boolean> by lazy {
        booleanSettingsStateFlow("memoryCache", true, dataStore)
    }
    val resultCache: SettingsStateFlow<Boolean> by lazy {
        booleanSettingsStateFlow("resultCache", true, dataStore)
    }
    val downloadCache: SettingsStateFlow<Boolean> by lazy {
        booleanSettingsStateFlow("downloadCache", true, dataStore)
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

    val exifOrientation: SettingsStateFlow<Boolean> by lazy {
        booleanSettingsStateFlow("exifOrientation", true, dataStore)
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
        val defaultState = Logger.levelName(if (isDebugMode()) Logger.DEBUG else Logger.INFO)
        stringSettingsStateFlow(
            key = "newNewLogLevel",
            initialize = defaultState,
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

        exifOrientation,
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

        exifOrientation,
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

        ignoreExifOrientation(!exifOrientation.value)
        saveCellularTraffic(saveCellularTrafficInList.value)
        disallowAnimatedImage(disallowAnimatedImageInList.value)
    }

    fun buildViewerImageOptions(): ImageOptions = ImageOptions {
        platformBuildImageOptions(this@AppSettings)

        memoryCachePolicy(memoryCacheValue)
        resultCachePolicy(resultCacheValue)
        downloadCachePolicy(downloadCacheValue)

        ignoreExifOrientation(!exifOrientation.value)
    }
}

expect fun ImageOptions.Builder.platformBuildImageOptions(appSettings: AppSettings)