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
@file:Suppress("EnumValuesSoftDeprecate")

package com.github.panpf.sketch.sample.ui.setting

import android.app.Application
import android.graphics.ColorSpace.Named
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.github.panpf.sketch.resize.Precision
import com.github.panpf.sketch.resize.Scale
import com.github.panpf.sketch.sample.appSettingsService
import com.github.panpf.sketch.sample.model.ListSeparator
import com.github.panpf.sketch.sample.model.MultiSelectMenu
import com.github.panpf.sketch.sample.model.SwitchMenuFlow
import com.github.panpf.sketch.sample.ui.base.LifecycleAndroidViewModel
import com.github.panpf.sketch.sample.ui.setting.Page.LIST
import com.github.panpf.sketch.sample.ui.setting.Page.ZOOM
import com.github.panpf.sketch.sample.util.ignoreFirst
import com.github.panpf.sketch.sketch
import com.github.panpf.sketch.util.Logger
import com.github.panpf.tools4a.toast.Toastx
import com.github.panpf.tools4j.io.ktx.formatFileSize
import com.github.panpf.zoomimage.zoom.AlignmentCompat
import com.github.panpf.zoomimage.zoom.ContentScaleCompat
import com.github.panpf.zoomimage.zoom.name
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.launch

class AppSettingsViewModel(application1: Application, val page: Page) :
    LifecycleAndroidViewModel(application1) {

    class Factory(val application: Application, val page: Page) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
            @Suppress("UNCHECKED_CAST")
            return AppSettingsViewModel(application, page) as T
        }
    }

    private val appSettingsService = application1.appSettingsService

    private val _menuListData = MutableStateFlow<List<Any>>(emptyList())
    val menuListData: StateFlow<List<Any>> = _menuListData

    init {
        val states = listOfNotNull(
            appSettingsService.showMimeTypeLogoInList.ignoreFirst(),
            appSettingsService.showProgressIndicatorInList.ignoreFirst(),
            appSettingsService.saveCellularTrafficInList.ignoreFirst(),
            appSettingsService.pauseLoadWhenScrollInList.ignoreFirst(),
            appSettingsService.precision.ignoreFirst(),
            appSettingsService.scale.ignoreFirst(),
            appSettingsService.longImageScale.ignoreFirst(),
            appSettingsService.otherImageScale.ignoreFirst(),
            appSettingsService.inPreferQualityOverSpeed.ignoreFirst(),
            appSettingsService.bitmapQuality.ignoreFirst(),
            if (VERSION.SDK_INT >= VERSION_CODES.O) appSettingsService.colorSpace.ignoreFirst() else null,
            appSettingsService.memoryCache.ignoreFirst(),
            appSettingsService.resultCache.ignoreFirst(),
            appSettingsService.downloadCache.ignoreFirst(),
            appSettingsService.showDataFromLogoInList.ignoreFirst(),
            appSettingsService.showTileBounds.ignoreFirst(),
            appSettingsService.logLevel.ignoreFirst(),
        )
        viewModelScope.launch {
            merge(*states.toTypedArray()).collect {
                updateList()
            }
        }

        updateList()
    }

    private fun updateList() {
        _menuListData.value = buildList {
            if (page == LIST) {
                add(ListSeparator("List"))
                addAll(makeListMenuList())
            } else if (page == ZOOM) {
                add(ListSeparator("Zoom"))
                addAll(makeZoomMenuList())
            }
            add(ListSeparator("Decode"))
            addAll(makeDecodeMenuList())
            add(ListSeparator("Cache"))
            addAll(makeCacheMenuList())
            add(ListSeparator("Other"))
            addAll(makeOtherMenuList())
        }
    }

    private fun makeListMenuList(): List<Any> = buildList {
        add(
            SwitchMenuFlow(
                title = "MimeType Logo",
                data = appSettingsService.showMimeTypeLogoInList,
                desc = "Displays the image type in the lower right corner of the ImageView"
            )
        )
        add(
            SwitchMenuFlow(
                title = "Data From Logo",
                data = appSettingsService.showDataFromLogoInList,
                desc = "A different color triangle is displayed in the lower right corner of the ImageView according to DataFrom"
            )
        )
        add(
            SwitchMenuFlow(
                title = "Progress Indicator",
                data = appSettingsService.showProgressIndicatorInList,
                desc = "A black translucent mask is displayed on the ImageView surface to indicate progress"
            )
        )
        add(
            SwitchMenuFlow(
                title = "Save Cellular Traffic",
                data = appSettingsService.saveCellularTrafficInList,
                desc = "Mobile cell traffic does not download pictures"
            )
        )
        add(
            SwitchMenuFlow(
                title = "Pause Load When Scrolling",
                data = appSettingsService.pauseLoadWhenScrollInList,
                desc = "No image is loaded during list scrolling to improve the smoothness"
            )
        )
        add(
            MultiSelectMenu(
                title = "Resize Precision",
                desc = null,
                values = Precision.values().map { it.name }.plus(listOf("LongImageMode")),
                getValue = { appSettingsService.precision.value },
                onSelect = { _, value -> appSettingsService.precision.value = value }
            )
        )
        add(
            MultiSelectMenu(
                title = "Resize Scale",
                desc = null,
                values = Scale.values().map { it.name }.plus(listOf("LongImageMode")),
                getValue = { appSettingsService.scale.value },
                onSelect = { _, value -> appSettingsService.scale.value = value }
            )
        )
        if (appSettingsService.scale.value == "LongImageMode") {
            add(
                MultiSelectMenu(
                    title = "Long Image Resize Scale",
                    desc = "Only Resize Scale is LongImageMode",
                    values = Scale.values().map { it.name },
                    getValue = { appSettingsService.longImageScale.value.name },
                    onSelect = { _, value ->
                        appSettingsService.longImageScale.value = Scale.valueOf(value)
                    }
                )
            )
            add(
                MultiSelectMenu(
                    title = "Other Image Resize Scale",
                    desc = "Only Resize Scale is LongImageMode",
                    values = Scale.values().map { it.name },
                    getValue = { appSettingsService.otherImageScale.value.name },
                    onSelect = { _, value ->
                        appSettingsService.otherImageScale.value = Scale.valueOf(value)
                    }
                )
            )
        }
    }

    private fun makeZoomMenuList(): List<Any> = buildList {
        val contentScales = listOf(
            ContentScaleCompat.Fit,
            ContentScaleCompat.Crop,
            ContentScaleCompat.Inside,
            ContentScaleCompat.FillWidth,
            ContentScaleCompat.FillHeight,
            ContentScaleCompat.FillBounds,
            ContentScaleCompat.None,
        )
        add(
            MultiSelectMenu(
                title = "Content Scale",
                desc = null,
                values = contentScales.map { it.name },
                getValue = { appSettingsService.contentScale.value },
                onSelect = { _, value ->
                    appSettingsService.contentScale.value = value
                }
            )
        )

        val alignments = listOf(
            AlignmentCompat.TopStart,
            AlignmentCompat.TopCenter,
            AlignmentCompat.TopEnd,
            AlignmentCompat.CenterStart,
            AlignmentCompat.Center,
            AlignmentCompat.CenterEnd,
            AlignmentCompat.BottomStart,
            AlignmentCompat.BottomCenter,
            AlignmentCompat.BottomEnd,
        )
        add(
            MultiSelectMenu(
                title = "Alignment",
                desc = null,
                values = alignments.map { it.name },
                getValue = { appSettingsService.alignment.value },
                onSelect = { _, value ->
                    appSettingsService.alignment.value = value
                }
            )
        )
        add(
            SwitchMenuFlow(
                title = "Scroll Bar",
                desc = null,
                data = appSettingsService.scrollBarEnabled,
            )
        )
        add(
            SwitchMenuFlow(
                title = "Read Mode",
                data = appSettingsService.readModeEnabled,
                desc = "Long images are displayed in full screen by default"
            )
        )
        add(
            SwitchMenuFlow(
                title = "Show Tile Bounds",
                desc = "Overlay the state and area of the tile on the View",
                data = appSettingsService.showTileBounds,
            )
        )
    }

    private fun makeDecodeMenuList(): List<Any> = buildList {
        add(
            MultiSelectMenu(
                title = "Bitmap Quality",
                desc = null,
                values = listOf("Default", "LOW", "HIGH"),
                getValue = { appSettingsService.bitmapQuality.value },
                onSelect = { _, value -> appSettingsService.bitmapQuality.value = value }
            )
        )
        if (VERSION.SDK_INT >= VERSION_CODES.O) {
            // Cannot use Named.entries, crashes on versions lower than O
            val items = listOf("Default").plus(Named.values().map { it.name })
            add(
                MultiSelectMenu(
                    title = "Color Space",
                    desc = null,
                    values = items,
                    getValue = { appSettingsService.colorSpace.value },
                    onSelect = { _, value -> appSettingsService.colorSpace.value = value }
                )
            )
        }
        if (VERSION.SDK_INT <= VERSION_CODES.M) {
            add(
                SwitchMenuFlow(
                    title = "inPreferQualityOverSpeed",
                    desc = null,
                    data = appSettingsService.inPreferQualityOverSpeed
                )
            )
        }
    }

    private fun makeCacheMenuList(): List<Any> = buildList {
        val sketch = application1.sketch

        add(
            SwitchMenuFlow(
                title = "Memory Cache",
                desc = "%s/%s（Long Click Clean）".format(
                    sketch.memoryCache.size.formatFileSize(
                        0,
                        decimalPlacesFillZero = false,
                        compact = true
                    ),
                    sketch.memoryCache.maxSize.formatFileSize(
                        0,
                        decimalPlacesFillZero = false,
                        compact = true
                    )
                ),
                data = appSettingsService.memoryCache,
                onLongClick = {
                    sketch.memoryCache.clear()
                    updateList()
                }
            )
        )

        add(
            SwitchMenuFlow(
                title = "Result Cache",
                desc = "%s/%s（Long Click Clean）".format(
                    sketch.resultCache.size.formatFileSize(
                        0,
                        decimalPlacesFillZero = false,
                        compact = true
                    ),
                    sketch.resultCache.maxSize.formatFileSize(
                        0,
                        decimalPlacesFillZero = false,
                        compact = true
                    )
                ),
                data = appSettingsService.resultCache,
                onLongClick = {
                    sketch.resultCache.clear()
                    updateList()
                }
            )
        )

        add(
            SwitchMenuFlow(
                title = "Download Cache",
                desc = "%s/%s（Long Click Clean）".format(
                    sketch.downloadCache.size.formatFileSize(
                        0,
                        decimalPlacesFillZero = false,
                        compact = true
                    ),
                    sketch.downloadCache.maxSize.formatFileSize(
                        0,
                        decimalPlacesFillZero = false,
                        compact = true
                    )
                ),
                data = appSettingsService.downloadCache,
                onLongClick = {
                    sketch.downloadCache.clear()
                    updateList()
                }
            )
        )
    }

    private fun makeOtherMenuList(): List<Any> = buildList {
        @Suppress("EnumValuesSoftDeprecate")
        add(
            MultiSelectMenu(
                title = "Logger Level",
                desc = if (application1.sketch.logger.level <= Logger.Level.Debug) "DEBUG and below will reduce UI fluency" else "",
                values = Logger.Level.values().map { it.name },
                getValue = { application1.sketch.logger.level.name },
                onSelect = { _, value ->
                    appSettingsService.logLevel.value = Logger.Level.valueOf(value)
                }
            )
        )
        add(
            MultiSelectMenu(
                title = "Http Client",
                desc = null,
                values = listOf("Ktor", "OkHttp", "HttpURLConnection"),
                getValue = { appSettingsService.httpClient.value },
                onSelect = { _, value ->
                    appSettingsService.httpClient.value = value
                    Toastx.showLong(application1, "Restart the app to take effect")
                }
            )
        )
        add(
            MultiSelectMenu(
                title = "Video Frame Decoder",
                desc = null,
                values = listOf("FFmpeg", "AndroidBuiltIn"),
                getValue = { appSettingsService.videoFrameDecoder.value },
                onSelect = { _, value ->
                    appSettingsService.videoFrameDecoder.value = value
                    Toastx.showLong(application1, "Restart the app to take effect")
                }
            )
        )
        add(
            MultiSelectMenu(
                title = "Gif Decoder",
                desc = null,
                values = listOf("KoralGif", "ImageDecoder+Movie"),
                getValue = { appSettingsService.gifDecoder.value },
                onSelect = { _, value ->
                    appSettingsService.gifDecoder.value = value
                    Toastx.showLong(application1, "Restart the app to take effect")
                }
            )
        )
    }
}