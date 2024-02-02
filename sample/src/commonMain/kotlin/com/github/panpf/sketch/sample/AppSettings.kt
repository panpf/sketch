package com.github.panpf.sketch.sample

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.github.panpf.sketch.cache.CachePolicy
import com.github.panpf.sketch.cache.CachePolicy.DISABLED
import com.github.panpf.sketch.cache.CachePolicy.ENABLED
import com.github.panpf.sketch.resize.LongImageClipPrecisionDecider
import com.github.panpf.sketch.resize.LongImageStartCropScaleDecider
import com.github.panpf.sketch.resize.Precision
import com.github.panpf.sketch.resize.Precision.SAME_ASPECT_RATIO
import com.github.panpf.sketch.resize.PrecisionDecider
import com.github.panpf.sketch.resize.Scale
import com.github.panpf.sketch.resize.Scale.CENTER_CROP
import com.github.panpf.sketch.resize.Scale.START_CROP
import com.github.panpf.sketch.resize.ScaleDecider
import com.github.panpf.sketch.sample.util.internal.SettingsStateFlow
import com.github.panpf.sketch.util.Logger
import com.github.panpf.sketch.PlatformContext
import com.github.panpf.sketch.sample.ui.model.LayoutMode
import com.github.panpf.sketch.sample.util.ParamLazy
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

    /*
     * list config
     */
    val showMimeTypeLogoInLIst by lazy {
        SettingsStateFlow("showMimeTypeLogoInLIst", true, dataStore)
    }
    val showProgressIndicatorInList by lazy {
        SettingsStateFlow("showProgressIndicatorInList", true, dataStore)
    }
    val showDataFromLogo by lazy {
        SettingsStateFlow("showDataFrom", true, dataStore)
    }
    val pauseLoadWhenScrollInList by lazy {
        SettingsStateFlow("pauseLoadWhenScrollInList", false, dataStore)
    }

    /*
     * image load
     */
    val bitmapQuality by lazy {
        SettingsStateFlow("bitmapQuality", "Default", dataStore)
    }
    val colorSpace by lazy {
        SettingsStateFlow("colorSpace", "Default", dataStore)
    }
    val inPreferQualityOverSpeed by lazy {
        SettingsStateFlow("inPreferQualityOverSpeed", false, dataStore)
    }

    val disabledMemoryCache by lazy {
        SettingsStateFlow("disabledBitmapMemoryCache", false, dataStore)
    }
    val disabledResultCache by lazy {
        SettingsStateFlow("disabledBitmapResultCache", false, dataStore)
    }
    val disabledDownloadCache by lazy {
        SettingsStateFlow("disabledDownloadCache", false, dataStore)
    }

    val precision by lazy {
        SettingsStateFlow("precision", "LongImageClipMode", dataStore)
    }
    val scale by lazy {
        SettingsStateFlow("scale", "LongImageMode", dataStore)
    }
    val longImageScale by lazy {
        SettingsStateFlow("longImageScale", START_CROP.name, dataStore)
    }
    val otherImageScale by lazy {
        SettingsStateFlow("otherImageScale", CENTER_CROP.name, dataStore)
    }

    val ignoreExifOrientation by lazy {
        SettingsStateFlow("ignoreExifOrientation", false, dataStore)
    }
    val saveCellularTrafficInList by lazy {
        SettingsStateFlow("saveCellularTrafficInList", false, dataStore)
    }
    val disallowAnimatedImageInList by lazy {
        SettingsStateFlow("disallowAnimatedImageInList", false, dataStore)
    }

    /*
     * view config
     */
    val contentScale by lazy {
        SettingsStateFlow("contentScale", ContentScaleCompat.Fit.name, dataStore)
    }
    val alignment by lazy {
        SettingsStateFlow("alignment", AlignmentCompat.Center.name, dataStore)
    }
    val scrollBarEnabled by lazy {
        SettingsStateFlow("scrollBarEnabled", true, dataStore)
    }
    val readModeEnabled by lazy {
        SettingsStateFlow("readModeEnabled", true, dataStore)
    }
    val showTileBounds by lazy {
        SettingsStateFlow("showTileBounds", false, dataStore)
    }

    /*
     * other
     */
    val photoListLayoutMode by lazy {
        SettingsStateFlow("photoListLayoutMode", LayoutMode.GRID.name, dataStore)
    }
    val showOriginImage by lazy {
        SettingsStateFlow("showOriginImage", false, dataStore)
    }
    val logLevel by lazy {
        val defaultState = if (isDebugMode()) Logger.Level.DEBUG.name else Logger.Level.INFO.name
        SettingsStateFlow("logLevel", defaultState, dataStore)
    }

//    private val bitmapQualityValue: BitmapConfig?
//        get() = when (bitmapQuality.value) {
//            "LOW" -> BitmapConfig.LowQuality
//            "HIGH" -> BitmapConfig.HighQuality
//            else -> null
//        }
//
//    @get:RequiresApi(VERSION_CODES.O)
//    private val colorSpaceValue: ColorSpace.Named?
//        get() = if (VERSION.SDK_INT >= VERSION_CODES.O) {
//            when (val value = colorSpace.value) {
//                "Default" -> null
//                else -> ColorSpace.Named.valueOf(value)
//            }
//        } else {
//            null
//        }
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

//    fun buildListImageOptions(): ImageOptions = ImageOptions {
//        pauseLoadWhenScrolling(pauseLoadWhenScrollInList.value)
//
//        bitmapConfig(bitmapQualityValue)
//        if (VERSION.SDK_INT >= VERSION_CODES.O) {
//            colorSpace(colorSpaceValue)
//        }
//        preferQualityOverSpeed(VERSION.SDK_INT <= VERSION_CODES.M && inPreferQualityOverSpeed.value)
//
//        memoryCachePolicy(disabledMemoryCacheValue)
//        resultCachePolicy(disabledResultCacheValue)
//        downloadCachePolicy(disabledDownloadCacheValue)
//
//        precision(precisionValue)
//        scale(scaleValue)
//
//        ignoreExifOrientation(ignoreExifOrientation.value)
//        saveCellularTraffic(saveCellularTrafficInList.value)
//        disallowAnimatedImage(disallowAnimatedImageInList.value)
//    }
//
//    fun buildViewerImageOptions(): ImageOptions = ImageOptions {
//        bitmapConfig(bitmapQualityValue)
//        if (VERSION.SDK_INT >= VERSION_CODES.O) {
//            colorSpace(colorSpaceValue)
//        }
//        preferQualityOverSpeed(VERSION.SDK_INT <= VERSION_CODES.M && inPreferQualityOverSpeed.value)
//
//        memoryCachePolicy(disabledMemoryCacheValue)
//        resultCachePolicy(disabledResultCacheValue)
//        downloadCachePolicy(disabledDownloadCacheValue)
//
//        ignoreExifOrientation(ignoreExifOrientation.value)
//    }
}