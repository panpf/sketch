/*
 * Copyright (C) 2024 panpf <panpfpanpf@outlook.com>
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

import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.resize.Precision
import com.github.panpf.sketch.resize.Scale
import com.github.panpf.sketch.sample.AppSettings
import com.github.panpf.sketch.sample.model.ListSeparator
import com.github.panpf.sketch.sample.model.MultiSelectMenu
import com.github.panpf.sketch.sample.model.SwitchMenuFlow
import com.github.panpf.sketch.sample.ui.setting.Page.LIST
import com.github.panpf.sketch.sample.ui.setting.Page.ZOOM
import com.github.panpf.sketch.sample.util.ignoreFirst
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

class AppSettingsViewModel(
    val sketch: Sketch,
    val appSettings: AppSettings,
    private val page: Page
) : ViewModel() {

    private val _menuListData = MutableStateFlow<List<Any>>(emptyList())
    val menuListData: StateFlow<List<Any>> = _menuListData

    init {
        val states = listOfNotNull(
            appSettings.showMimeTypeLogoInList.ignoreFirst(),
            appSettings.showProgressIndicatorInList.ignoreFirst(),
            appSettings.saveCellularTrafficInList.ignoreFirst(),
            appSettings.pauseLoadWhenScrollInList.ignoreFirst(),
            appSettings.repeatCount.ignoreFirst(),
            appSettings.precisionName.ignoreFirst(),
            appSettings.scaleName.ignoreFirst(),
            appSettings.longImageScale.ignoreFirst(),
            appSettings.otherImageScale.ignoreFirst(),
            appSettings.preferQualityOverSpeed.ignoreFirst(),
            appSettings.colorTypeName.ignoreFirst(),
            if (VERSION.SDK_INT >= VERSION_CODES.O) appSettings.colorSpaceName.ignoreFirst() else null,
            appSettings.memoryCacheName.ignoreFirst(),
            appSettings.resultCacheName.ignoreFirst(),
            appSettings.downloadCacheName.ignoreFirst(),
            appSettings.showDataFromLogoInList.ignoreFirst(),
            appSettings.showTileBounds.ignoreFirst(),
            appSettings.logLevel.ignoreFirst(),
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

            add(ListSeparator("Animated"))
            addAll(makeAnimatedMenuList())

            add(ListSeparator("Cache"))
            addAll(makeCacheMenuList())

            add(ListSeparator("Other"))
            addAll(makeOtherMenuList())
        }
    }

    private fun makeListMenuList(): List<Any> = buildList {
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
                getValue = { appSettings.listContentScaleName.value },
                onSelect = { _, value ->
                    appSettings.listContentScaleName.value = value
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
                getValue = { appSettings.listAlignmentName.value },
                onSelect = { _, value ->
                    appSettings.listAlignmentName.value = value
                }
            )
        )
        add(
            SwitchMenuFlow(
                title = "Resize On Draw",
                data = appSettings.resizeOnDrawEnabled,
                desc = null
            )
        )
        add(
            SwitchMenuFlow(
                title = "MimeType Logo",
                data = appSettings.showMimeTypeLogoInList,
                desc = "Displays the image type in the lower right corner of the ImageView"
            )
        )
        add(
            SwitchMenuFlow(
                title = "Data From Logo",
                data = appSettings.showDataFromLogoInList,
                desc = "A different color triangle is displayed in the lower right corner of the ImageView according to DataFrom"
            )
        )
        add(
            SwitchMenuFlow(
                title = "Progress Indicator",
                data = appSettings.showProgressIndicatorInList,
                desc = "A black translucent mask is displayed on the ImageView surface to indicate progress"
            )
        )
        add(
            SwitchMenuFlow(
                title = "Save Cellular Traffic",
                data = appSettings.saveCellularTrafficInList,
                desc = "Mobile cell traffic does not download pictures"
            )
        )
        add(
            SwitchMenuFlow(
                title = "Pause Load When Scrolling",
                data = appSettings.pauseLoadWhenScrollInList,
                desc = "No image is loaded during list scrolling to improve the smoothness"
            )
        )
        add(
            MultiSelectMenu(
                title = "Resize Precision",
                desc = null,
                values = Precision.values().map { it.name }.plus(listOf("LongImageMode")),
                getValue = { appSettings.precisionName.value },
                onSelect = { _, value -> appSettings.precisionName.value = value }
            )
        )
        add(
            MultiSelectMenu(
                title = "Resize Scale",
                desc = null,
                values = Scale.values().map { it.name }.plus(listOf("LongImageMode")),
                getValue = { appSettings.scaleName.value },
                onSelect = { _, value -> appSettings.scaleName.value = value }
            )
        )
        if (appSettings.scaleName.value == "LongImageMode") {
            add(
                MultiSelectMenu(
                    title = "Long Image Resize Scale",
                    desc = "Only Resize Scale is LongImageMode",
                    values = Scale.values().map { it.name },
                    getValue = { appSettings.longImageScale.value.name },
                    onSelect = { _, value ->
                        appSettings.longImageScale.value = Scale.valueOf(value)
                    }
                )
            )
            add(
                MultiSelectMenu(
                    title = "Other Image Resize Scale",
                    desc = "Only Resize Scale is LongImageMode",
                    values = Scale.values().map { it.name },
                    getValue = { appSettings.otherImageScale.value.name },
                    onSelect = { _, value ->
                        appSettings.otherImageScale.value = Scale.valueOf(value)
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
                getValue = { appSettings.contentScaleName.value },
                onSelect = { _, value ->
                    appSettings.contentScaleName.value = value
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
                getValue = { appSettings.alignmentName.value },
                onSelect = { _, value ->
                    appSettings.alignmentName.value = value
                }
            )
        )
        add(
            SwitchMenuFlow(
                title = "Scroll Bar",
                desc = null,
                data = appSettings.scrollBarEnabled,
            )
        )
        add(
            SwitchMenuFlow(
                title = "Read Mode",
                data = appSettings.readModeEnabled,
                desc = "Long images are displayed in full screen by default"
            )
        )
        add(
            SwitchMenuFlow(
                title = "Show Tile Bounds",
                desc = "Overlay the state and area of the tile on the View",
                data = appSettings.showTileBounds,
            )
        )
    }

    private fun makeDecodeMenuList(): List<Any> = buildList {
        add(
            MultiSelectMenu(
                title = "Bitmap Color Type",
                desc = null,
                values = listOf("Default", "LowQuality", "HighQuality").plus(platformColorTypes()),
                getValue = { appSettings.colorTypeName.value },
                onSelect = { _, value -> appSettings.colorTypeName.value = value }
            )
        )
        if (VERSION.SDK_INT >= VERSION_CODES.O) {
            // Cannot use Named.entries, crashes on versions lower than O
            val items = listOf("Default").plus(platformColorSpaces())
            add(
                MultiSelectMenu(
                    title = "Bitmap Color Space",
                    desc = null,
                    values = items,
                    getValue = { appSettings.colorSpaceName.value },
                    onSelect = { _, value -> appSettings.colorSpaceName.value = value }
                )
            )
        }
        if (VERSION.SDK_INT <= VERSION_CODES.M) {
            add(
                SwitchMenuFlow(
                    title = "preferQualityOverSpeed",
                    desc = null,
                    data = appSettings.preferQualityOverSpeed
                )
            )
        }
    }

    private fun makeAnimatedMenuList(): List<Any> = buildList {
        add(
            MultiSelectMenu(
                title = "Repeat Count",
                desc = null,
                values = listOf("-1", "0", "1", "2", "4"),
                getValue = { appSettings.repeatCount.value.toString() },
                onSelect = { _, value -> appSettings.repeatCount.value = value.toInt() }
            )
        )
    }

    private fun makeCacheMenuList(): List<Any> = buildList {
        add(
            SwitchMenuFlow(
                title = "Memory Cache",
                desc = "%s/%s（Long Press Clean）".format(
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
                data = appSettings.memoryCacheName,
                onLongClick = {
                    sketch.memoryCache.clear()
                    updateList()
                }
            )
        )

        add(
            SwitchMenuFlow(
                title = "Result Cache",
                desc = "%s/%s（Long Press Clean）".format(
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
                data = appSettings.resultCacheName,
                onLongClick = {
                    sketch.resultCache.clear()
                    updateList()
                }
            )
        )

        add(
            SwitchMenuFlow(
                title = "Download Cache",
                desc = "%s/%s（Long Press Clean）".format(
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
                data = appSettings.downloadCacheName,
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
                desc = if (sketch.logger.level <= Logger.Level.Debug) "DEBUG and below will reduce UI fluency" else "",
                values = Logger.Level.values().map { it.name },
                getValue = { sketch.logger.level.name },
                onSelect = { _, value ->
                    appSettings.logLevel.value = Logger.Level.valueOf(value)
                }
            )
        )
        add(
            MultiSelectMenu(
                title = "Network Parallelism Limited",
                desc = "No limit when less than or equal to 0",
                values = listOf(-1, 1, 2, 4, 10, 20).map { it.toString() },
                getValue = { appSettings.networkParallelismLimited.value.toString() },
                onSelect = { _, value ->
                    appSettings.networkParallelismLimited.value = value.toInt()
                    Toastx.showLong(sketch.context, "Restart the app to take effect")
                }
            )
        )
        add(
            MultiSelectMenu(
                title = "Decode Parallelism Limited",
                desc = "No limit when less than or equal to 0",
                values = listOf(-1, 1, 2, 4, 10, 20).map { it.toString() },
                getValue = { appSettings.decodeParallelismLimited.value.toString() },
                onSelect = { _, value ->
                    appSettings.decodeParallelismLimited.value = value.toInt()
                    Toastx.showLong(sketch.context, "Restart the app to take effect")
                }
            )
        )
        add(
            MultiSelectMenu(
                title = "Http Client",
                desc = null,
                values = listOf("Ktor", "OkHttp", "HttpURLConnection"),
                getValue = { appSettings.httpClient.value },
                onSelect = { _, value ->
                    appSettings.httpClient.value = value
                    Toastx.showLong(sketch.context, "Restart the app to take effect")
                }
            )
        )
        add(
            MultiSelectMenu(
                title = "Video Frame Decoder",
                desc = null,
                values = listOf("FFmpeg", "AndroidBuiltIn"),
                getValue = { appSettings.videoFrameDecoder.value },
                onSelect = { _, value ->
                    appSettings.videoFrameDecoder.value = value
                    Toastx.showLong(sketch.context, "Restart the app to take effect")
                }
            )
        )
        add(
            MultiSelectMenu(
                title = "Gif Decoder",
                desc = null,
                values = listOf("KoralGif", "Movie", "Movie+ImageDecoder"),
                getValue = { appSettings.gifDecoder.value },
                onSelect = { _, value ->
                    appSettings.gifDecoder.value = value
                    Toastx.showLong(sketch.context, "Restart the app to take effect")
                }
            )
        )
    }
}