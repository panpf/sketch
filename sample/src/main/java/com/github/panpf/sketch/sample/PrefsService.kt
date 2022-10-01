/*
 * Copyright (C) 2022 panpf <panpfpanpf@outlook.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.panpf.sketch.sample

import android.content.Context
import android.widget.ImageView.ScaleType
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
        StringMmkvData(mmkv, "resizePrecision", "LongImageClipMode")
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

    val colorSpace by lazy {
        StringMmkvData(mmkv, "colorSpace", "Default")
    }
    val inPreferQualityOverSpeed by lazy {
        BooleanMmkvData(mmkv, "inPreferQualityOverSpeed", false)
    }
    val ignoreExifOrientation by lazy {
        BooleanMmkvData(mmkv, "ignoreExifOrientation", false)
    }

    val disabledMemoryCache by lazy {
        BooleanMmkvData(mmkv, "disabledBitmapMemoryCache", false)
    }
    val disabledResultCache by lazy {
        BooleanMmkvData(mmkv, "disabledBitmapResultCache", false)
    }
    val disabledDownloadCache by lazy {
        BooleanMmkvData(mmkv, "disabledDownloadCache", false)
    }
    val disallowReuseBitmap by lazy {
        BooleanMmkvData(mmkv, "disallowReuseBitmap", false)
    }

    val scaleType by lazy {
        StringMmkvData(mmkv, "scaleType", ScaleType.FIT_CENTER.name)
    }
    val scrollBarEnabled by lazy {
        BooleanMmkvData(mmkv, "scrollBarEnabled", true)
    }
    val readModeEnabled by lazy {
        BooleanMmkvData(mmkv, "readModeEnabled", true)
    }
    val showTileBoundsInHugeImagePage by lazy {
        BooleanMmkvData(mmkv, "showTileBoundsInHugeImagePage", false)
    }

    val logLevel by lazy {
        StringMmkvData(mmkv, "logLevel", Logger.Level.INFO.name)
    }

    val showOriginImage by lazy {
        BooleanMmkvData(mmkv, "showOriginImage", false)
    }
}