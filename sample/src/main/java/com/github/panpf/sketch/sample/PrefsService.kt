package com.github.panpf.sketch.sample

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import com.github.panpf.sketch.resize.Scale
import com.github.panpf.sketch.sample.model.LayoutMode.GRID
import com.github.panpf.sketch.sample.util.BooleanMmkvData
import com.github.panpf.sketch.sample.util.StringMmkvData
import com.github.panpf.sketch.util.Logger
import com.tencent.mmkv.MMKV

class PrefsService(val context: Context) {

    private val mmkv = MMKV.defaultMMKV()

    val photoListLayoutMode by lazy {
        StringMmkvData(mmkv, "photoListLayoutMode", GRID.name)
    }
    val disallowAnimatedImageInList by lazy {
        BooleanMmkvData(mmkv, "disallowAnimatedImageInList", false)
    }

    val showMimeTypeLogoInLIst by lazy {
        BooleanMmkvData(mmkv, "showMimeTypeLogoInLIst", true)
    }
    val showProgressIndicatorInList by lazy {
        BooleanMmkvData(mmkv, "showProgressIndicatorInList", true)
    }
    val showDataFromLogo by lazy {
        BooleanMmkvData(mmkv, "showDataFrom", true)
    }
    val saveCellularTrafficInList by lazy {
        BooleanMmkvData(mmkv, "saveCellularTrafficInList", false)
    }
    val pauseLoadWhenScrollInList by lazy {
        BooleanMmkvData(mmkv, "pauseLoadWhenScrollInList", false)
    }

    val resizePrecision by lazy {
        StringMmkvData(mmkv, "resizePrecision", "LongImageMode")
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
    val bitmapQuality by lazy {
        StringMmkvData(mmkv, "bitmapQuality", "Default")
    }

    @get:RequiresApi(Build.VERSION_CODES.O)
    val colorSpace by lazy {
        StringMmkvData(mmkv, "colorSpace", "Default")
    }
    val inPreferQualityOverSpeed by lazy {
        BooleanMmkvData(mmkv, "inPreferQualityOverSpeed", false)
    }
    val ignoreExifOrientation by lazy {
        BooleanMmkvData(mmkv, "ignoreExifOrientation", false)
    }

    val disabledBitmapMemoryCache by lazy {
        BooleanMmkvData(mmkv, "disabledBitmapMemoryCache", false)
    }
    val disabledBitmapResultDiskCache by lazy {
        BooleanMmkvData(mmkv, "disabledBitmapResultDiskCache", false)
    }
    val disabledDownloadDiskCache by lazy {
        BooleanMmkvData(mmkv, "disabledDownloadDiskCache", false)
    }
    val disallowReuseBitmap by lazy {
        BooleanMmkvData(mmkv, "disallowReuseBitmap", false)
    }

    val showTileBoundsInHugeImagePage by lazy {
        BooleanMmkvData(mmkv, "showTileBoundsInHugeImagePage", true)
    }
    val readModeEnabled by lazy {
        BooleanMmkvData(mmkv, "readModeEnabled", true)
    }
    val logLevel by lazy {
        StringMmkvData(mmkv, "logLevel", Logger.Level.DEBUG.name)
    }
}