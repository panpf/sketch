package com.github.panpf.sketch.sample

import android.content.Context
import com.github.panpf.sketch.decode.resize.Scale
import com.github.panpf.sketch.sample.bean.LayoutMode
import com.github.panpf.sketch.sample.bean.LayoutMode.GRID
import com.github.panpf.sketch.sample.util.prefsdata.BooleanPrefsData
import com.github.panpf.sketch.sample.util.prefsdata.EnumPrefsData
import com.github.panpf.sketch.sample.util.prefsdata.StringPrefsData

class AppSettingsService(val context: Context) {

    val photoListLayoutMode by lazy {
        EnumPrefsData(
            context,
            "photoListLayoutMode",
            GRID,
            { LayoutMode.valueOf(it) },
            { it.name }
        )
    }
    val disabledAnimatableDrawableInList by lazy {
        BooleanPrefsData(context, "disabledAnimatableDrawableInList", false)
    }

    val showMimeTypeLogoInLIst by lazy {
        BooleanPrefsData(context, "showMimeTypeLogoInLIst", true)
    }
    val showProgressIndicatorInList by lazy {
        BooleanPrefsData(context, "showProgressIndicatorInList", true)
    }
    val saveCellularTrafficInList by lazy {
        BooleanPrefsData(context, "saveCellularTrafficInList", false)
    }
    val pauseLoadWhenScrollInList by lazy {
        BooleanPrefsData(context, "pauseLoadWhenScrollInList", false)
    }
    val resizePrecision by lazy {
        StringPrefsData(context, "resizePrecision", "LESS_PIXELS")
    }
    val resizeScale by lazy {
        StringPrefsData(context, "resizeScale", Scale.START_CROP.name)
    }

    val inPreferQualityOverSpeed by lazy {
        BooleanPrefsData(context, "inPreferQualityOverSpeed", false)
    }
    val bitmapQuality by lazy {
        StringPrefsData(context, "bitmapQuality", "MIDDEN")
    }
    val ignoreExifOrientation by lazy {
        BooleanPrefsData(context, "ignoreExifOrientation", false)
    }

    val disabledBitmapMemoryCache by lazy {
        BooleanPrefsData(context, "disabledBitmapMemoryCache", false)
    }
    val disabledBitmapResultDiskCache by lazy {
        BooleanPrefsData(context, "disabledBitmapResultDiskCache", false)
    }
    val disabledNetworkContentDiskCache by lazy {
        BooleanPrefsData(context, "disabledNetworkContentDiskCache", false)
    }
    val disabledBitmapPool by lazy {
        BooleanPrefsData(context, "disabledBitmapPool", false)
    }

    val showDataFrom by lazy {
        BooleanPrefsData(context, "showDataFrom", true)
    }

//    val readModeEnabled by lazy {
//        BooleanPrefsData(context, "readModeEnabled", true)
//    }
//    val pauseBlockDisplayWhenPageNoVisibleEnabled by lazy {
//        BooleanPrefsData(context, "pauseBlockDisplayWhenPageNoVisibleEnabled", true)
//    }
//    val threeLevelZoomModeEnabled by lazy {
//        BooleanPrefsData(context, "threeLevelZoomModeEnabled", false)
//    }
//    val smallMapLocationAnimateEnabled by lazy {
//        BooleanPrefsData(context, "smallMapLocationAnimateEnabled", true)
//    }
//
//    val clickPlayGifEnabled by lazy {
//        BooleanPrefsData(context, "clickPlayGifEnabled", false)
//    }
//
//    val showRoundedInPhotoListEnabled by lazy {
//        BooleanPrefsData(context, "showRoundedInPhotoListEnabled", false)
//    }
//    val showRawImageInDetailEnabled by lazy {
//        BooleanPrefsData(context, "showRawImageInDetailEnabled", false)
//    }
}