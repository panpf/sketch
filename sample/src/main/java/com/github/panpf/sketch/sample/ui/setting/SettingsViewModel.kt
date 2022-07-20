package com.github.panpf.sketch.sample.ui.setting

import android.app.Application
import android.graphics.ColorSpace
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.github.panpf.sketch.resize.Precision
import com.github.panpf.sketch.resize.Scale
import com.github.panpf.sketch.sample.model.InfoMenu
import com.github.panpf.sketch.sample.model.ListSeparator
import com.github.panpf.sketch.sample.model.MultiSelectMenu
import com.github.panpf.sketch.sample.model.SwitchMenuFlow
import com.github.panpf.sketch.sample.prefsService
import com.github.panpf.sketch.sample.ui.base.LifecycleAndroidViewModel
import com.github.panpf.sketch.sketch
import com.github.panpf.sketch.util.Logger.Level
import com.github.panpf.tools4j.io.ktx.formatFileSize
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.launch

class SettingsViewModel(application1: Application) : LifecycleAndroidViewModel(application1) {

    val menuListData = MutableLiveData<List<Any>>()
    private val prefsService = application1.prefsService

    init {
        val states = listOfNotNull(
            prefsService.showMimeTypeLogoInLIst.sharedFlow,
            prefsService.showProgressIndicatorInList.sharedFlow,
            prefsService.saveCellularTrafficInList.sharedFlow,
            prefsService.pauseLoadWhenScrollInList.sharedFlow,
            prefsService.resizePrecision.sharedFlow,
            prefsService.resizeScale.sharedFlow,
            prefsService.longImageResizeScale.sharedFlow,
            prefsService.otherImageResizeScale.sharedFlow,
            prefsService.inPreferQualityOverSpeed.sharedFlow,
            prefsService.bitmapQuality.sharedFlow,
            if (VERSION.SDK_INT >= VERSION_CODES.O) prefsService.colorSpace.sharedFlow else null,
            prefsService.ignoreExifOrientation.sharedFlow,
            prefsService.disabledBitmapMemoryCache.sharedFlow,
            prefsService.disabledBitmapResultCache.sharedFlow,
            prefsService.disabledDownloadCache.sharedFlow,
            prefsService.disallowReuseBitmap.sharedFlow,
            prefsService.showDataFromLogo.sharedFlow,
            prefsService.showTileBoundsInHugeImagePage.sharedFlow,
            prefsService.logLevel.sharedFlow,
        )
        viewModelScope.launch {
            merge(*states.toTypedArray()).collect {
                updateList()
            }
        }

        updateList()
    }

    private fun updateList() {
        menuListData.postValue(buildList {
            add(ListSeparator("List"))
            addAll(makeListMenuList())
            add(ListSeparator("Decode"))
            addAll(makeDecodeMenuList())
            add(ListSeparator("Cache"))
            addAll(makeCacheMenuList())
            add(ListSeparator("Other"))
            addAll(makeOtherMenuList())
        })
    }

    private fun makeListMenuList(): List<Any> = buildList {
        add(
            SwitchMenuFlow(
                title = "MimeType Logo",
                data = prefsService.showMimeTypeLogoInLIst,
                desc = "Displays the image type in the lower right corner of the ImageView"
            )
        )
        add(
            SwitchMenuFlow(
                title = "Progress Indicator",
                data = prefsService.showProgressIndicatorInList,
                desc = "A black translucent mask is displayed on the ImageView surface to indicate progress"
            )
        )
        add(
            SwitchMenuFlow(
                title = "Show Data From Logo",
                data = prefsService.showDataFromLogo,
                desc = "A different color triangle is displayed in the lower right corner of the ImageView according to DataFrom"
            )
        )
        add(
            SwitchMenuFlow(
                title = "Save Cellular Traffic",
                data = prefsService.saveCellularTrafficInList,
                desc = "Mobile cell traffic does not download pictures"
            )
        )
        add(
            SwitchMenuFlow(
                title = "Pause Load When Scrolling",
                data = prefsService.pauseLoadWhenScrollInList,
                desc = "No image is loaded during list scrolling to improve the smoothness"
            )
        )
    }

    private fun makeDecodeMenuList(): List<Any> = buildList {
        add(
            MultiSelectMenu(
                title = "Resize Precision",
                desc = null,
                values = listOf("LongImageMode").plus(Precision.values().map { it.name }),
                getValue = { prefsService.resizePrecision.value },
                onSelect = { _, value -> prefsService.resizePrecision.value = value }
            )
        )
        add(
            MultiSelectMenu(
                title = "Resize Scale",
                desc = null,
                values = listOf("LongImageMode").plus(Scale.values().map { it.name }),
                getValue = { prefsService.resizeScale.value },
                onSelect = { _, value -> prefsService.resizeScale.value = value }
            )
        )
        if (prefsService.resizeScale.value == "LongImageMode") {
            add(
                MultiSelectMenu(
                    title = "Long Image Resize Scale",
                    desc = "Only Resize Scale is LongImageMode",
                    values = Scale.values().map { it.name },
                    getValue = { prefsService.longImageResizeScale.value },
                    onSelect = { _, value -> prefsService.longImageResizeScale.value = value }
                )
            )
            add(
                MultiSelectMenu(
                    title = "Other Image Resize Scale",
                    desc = "Only Resize Scale is LongImageMode",
                    values = Scale.values().map { it.name },
                    getValue = { prefsService.otherImageResizeScale.value },
                    onSelect = { _, value ->
                        prefsService.otherImageResizeScale.value = value
                    }
                )
            )
        }
        add(
            MultiSelectMenu(
                title = "Bitmap Quality",
                desc = null,
                values = listOf("Default", "LOW", "HIGH"),
                getValue = { prefsService.bitmapQuality.value },
                onSelect = { _, value -> prefsService.bitmapQuality.value = value }
            )
        )
        if (VERSION.SDK_INT >= VERSION_CODES.O) {
            val items = listOf("Default").plus(ColorSpace.Named.values().map { it.name })
            add(
                MultiSelectMenu(
                    title = "Color Space",
                    desc = null,
                    values = items,
                    getValue = { prefsService.colorSpace.value },
                    onSelect = { _, value -> prefsService.colorSpace.value = value }
                )
            )
        }
        if (VERSION.SDK_INT < VERSION_CODES.N) {
            add(
                SwitchMenuFlow(
                    title = "inPreferQualityOverSpeed",
                    desc = null,
                    data = prefsService.inPreferQualityOverSpeed
                )
            )
        }
        add(
            SwitchMenuFlow(
                title = "Exif Orientation",
                desc = null,
                data = prefsService.ignoreExifOrientation,
                reverse = true
            )
        )
    }

    private fun makeCacheMenuList(): List<Any> = buildList {
        val sketch = application1.sketch

        add(
            SwitchMenuFlow(
                title = "Memory Cache",
                desc = "%s/%s（Long Click Clean）".format(
                    sketch.memoryCache.size.formatFileSize(0, false, true),
                    sketch.memoryCache.maxSize.formatFileSize(0, false, true)
                ),
                data = prefsService.disabledBitmapMemoryCache,
                reverse = true,
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
                    sketch.resultCache.size.formatFileSize(0, false, true),
                    sketch.resultCache.maxSize.formatFileSize(0, false, true)
                ),
                data = prefsService.disabledBitmapResultCache,
                reverse = true,
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
                    sketch.downloadCache.size.formatFileSize(0, false, true),
                    sketch.downloadCache.maxSize.formatFileSize(0, false, true)
                ),
                data = prefsService.disabledDownloadCache,
                reverse = true,
                onLongClick = {
                    sketch.downloadCache.clear()
                    updateList()
                }
            )
        )

        add(
            SwitchMenuFlow(
                title = "Bitmap Pool",
                desc = "%s/%s（Long Click Clean）".format(
                    sketch.bitmapPool.size.formatFileSize(0, false, true),
                    sketch.bitmapPool.maxSize.formatFileSize(0, false, true)
                ),
                data = prefsService.disallowReuseBitmap,
                reverse = true,
                onLongClick = {
                    sketch.bitmapPool.clear()
                    updateList()
                }
            )
        )
    }

    private fun makeOtherMenuList(): List<Any> = buildList {
        add(
            SwitchMenuFlow(
                title = "Show Tile Bounds",
                data = prefsService.showTileBoundsInHugeImagePage,
                desc = "Only huge image page"
            )
        )
        add(
            SwitchMenuFlow(
                title = "Read Mode",
                data = prefsService.readModeEnabled,
                desc = null
            )
        )
        add(
            MultiSelectMenu(
                title = "Logger Level",
                desc = null,
                values = Level.values().map { it.name },
                getValue = { application1.sketch.logger.level.toString() },
                onSelect = { _, value ->
                    application1.sketch.logger.level = Level.valueOf(value)
                    prefsService.logLevel.value = value
                }
            )
        )
    }
}