package com.github.panpf.sketch.sample.ui.setting

import android.app.Application
import android.graphics.ColorSpace
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import androidx.lifecycle.MutableLiveData
import com.github.panpf.liveevent.Listener
import com.github.panpf.liveevent.MediatorLiveEvent
import com.github.panpf.sketch.resize.Precision
import com.github.panpf.sketch.resize.Scale
import com.github.panpf.sketch.sample.appSettingsService
import com.github.panpf.sketch.sample.model.InfoMenu
import com.github.panpf.sketch.sample.model.ListSeparator
import com.github.panpf.sketch.sample.model.MultiSelectMenu
import com.github.panpf.sketch.sample.model.SwitchMenu
import com.github.panpf.sketch.sample.ui.base.LifecycleAndroidViewModel
import com.github.panpf.sketch.sketch
import com.github.panpf.sketch.util.Logger.Level
import com.github.panpf.tools4j.io.ktx.formatFileSize

class SettingsViewModel(application1: Application) : LifecycleAndroidViewModel(application1) {

    val menuListData = MutableLiveData<List<Any>>()
    private val appSettingsService = application1.appSettingsService
    private val mediatorLiveData = MediatorLiveEvent<Any>()

    init {
        updateList()

        mediatorLiveData.apply {
            val observer = Listener<Any> {
                postValue(1)
            }
            addSource(appSettingsService.showMimeTypeLogoInLIst.liveEvent, observer)
            addSource(appSettingsService.showProgressIndicatorInList.liveEvent, observer)
            addSource(appSettingsService.saveCellularTrafficInList.liveEvent, observer)
            addSource(appSettingsService.pauseLoadWhenScrollInList.liveEvent, observer)
            addSource(appSettingsService.resizePrecision.liveEvent, observer)
            addSource(appSettingsService.resizeScale.liveEvent, observer)
            addSource(appSettingsService.longImageResizeScale.liveEvent, observer)
            addSource(appSettingsService.otherImageResizeScale.liveEvent, observer)
            addSource(appSettingsService.inPreferQualityOverSpeed.liveEvent, observer)
            addSource(appSettingsService.bitmapQuality.liveEvent, observer)
            if (VERSION.SDK_INT >= VERSION_CODES.O) {
                addSource(appSettingsService.colorSpace.liveEvent, observer)
            }
            addSource(appSettingsService.ignoreExifOrientation.liveEvent, observer)
            addSource(appSettingsService.disabledBitmapMemoryCache.liveEvent, observer)
            addSource(appSettingsService.disabledBitmapResultDiskCache.liveEvent, observer)
            addSource(appSettingsService.disabledDownloadDiskCache.liveEvent, observer)
            addSource(appSettingsService.disabledReuseBitmap.liveEvent, observer)
            addSource(appSettingsService.showDataFromLogo.liveEvent, observer)
            addSource(appSettingsService.showTileBoundsInHugeImagePage.liveEvent, observer)
            addSource(appSettingsService.logLevel.liveEvent, observer)
        }

        mediatorLiveData.listen(this) {
            updateList()
        }
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
            SwitchMenu(
                title = "MimeType Logo",
                data = appSettingsService.showMimeTypeLogoInLIst,
                desc = "Displays the image type in the lower right corner of the ImageView"
            )
        )
        add(
            SwitchMenu(
                title = "Progress Indicator",
                data = appSettingsService.showProgressIndicatorInList,
                desc = "A black translucent mask is displayed on the ImageView surface to indicate progress"
            )
        )
        add(
            SwitchMenu(
                title = "Show Data From Logo",
                data = appSettingsService.showDataFromLogo,
                desc = "A different color triangle is displayed in the lower right corner of the ImageView according to DataFrom"
            )
        )
        add(
            SwitchMenu(
                title = "Save Cellular Traffic",
                data = appSettingsService.saveCellularTrafficInList,
                desc = "Mobile cell traffic does not download pictures"
            )
        )
        add(
            SwitchMenu(
                title = "Pause Load When Scrolling",
                data = appSettingsService.pauseLoadWhenScrollInList,
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
                getValue = { appSettingsService.resizePrecision.value },
                onSelect = { _, value -> appSettingsService.resizePrecision.value = value }
            )
        )
        add(
            MultiSelectMenu(
                title = "Resize Scale",
                desc = null,
                values = listOf("LongImageMode").plus(Scale.values().map { it.name }),
                getValue = { appSettingsService.resizeScale.value },
                onSelect = { _, value -> appSettingsService.resizeScale.value = value }
            )
        )
        if (appSettingsService.resizeScale.value == "LongImageMode") {
            add(
                MultiSelectMenu(
                    title = "Long Image Resize Scale",
                    desc = "Only Resize Scale is LongImageMode",
                    values = Scale.values().map { it.name },
                    getValue = { appSettingsService.longImageResizeScale.value },
                    onSelect = { _, value -> appSettingsService.longImageResizeScale.value = value }
                )
            )
            add(
                MultiSelectMenu(
                    title = "Other Image Resize Scale",
                    desc = "Only Resize Scale is LongImageMode",
                    values = Scale.values().map { it.name },
                    getValue = { appSettingsService.otherImageResizeScale.value },
                    onSelect = { _, value ->
                        appSettingsService.otherImageResizeScale.value = value
                    }
                )
            )
        }
        add(
            MultiSelectMenu(
                title = "Bitmap Quality",
                desc = null,
                values = listOf("Default", "LOW", "MIDDEN", "HIGH"),
                getValue = { appSettingsService.bitmapQuality.value },
                onSelect = { _, value -> appSettingsService.bitmapQuality.value = value }
            )
        )
        if (VERSION.SDK_INT >= VERSION_CODES.O) {
            val items = listOf("Default").plus(ColorSpace.Named.values().map { it.name })
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
        if (VERSION.SDK_INT < VERSION_CODES.N) {
            add(
                SwitchMenu(
                    title = "inPreferQualityOverSpeed",
                    desc = null,
                    data = appSettingsService.inPreferQualityOverSpeed
                )
            )
        }
        add(
            SwitchMenu(
                title = "Exif Orientation",
                desc = null,
                data = appSettingsService.ignoreExifOrientation,
                reverse = true
            )
        )
    }

    private fun makeCacheMenuList(): List<Any> = buildList {
        add(
            SwitchMenu(
                title = "Bitmap Memory Cache",
                desc = null,
                data = appSettingsService.disabledBitmapMemoryCache,
                reverse = true
            )
        )

        add(
            SwitchMenu(
                title = "Bitmap Pool",
                desc = null,
                data = appSettingsService.disabledReuseBitmap,
                reverse = true
            )
        )

        add(
            SwitchMenu(
                title = "Bitmap Result Disk Cache",
                desc = null,
                data = appSettingsService.disabledBitmapResultDiskCache,
                reverse = true
            )
        )

        add(
            SwitchMenu(
                title = "Download Disk Cache",
                desc = null,
                data = appSettingsService.disabledDownloadDiskCache,
                reverse = true
            )
        )

        val sketch = application1.sketch
        add(InfoMenu(
            title = "Memory Cache Statistics",
            desc = "Click clear",
            info = "%s/%s".format(
                sketch.memoryCache.size.formatFileSize(0, false, true),
                sketch.memoryCache.maxSize.formatFileSize(0, false, true)
            ),
            onClick = {
                sketch.memoryCache.clear()
                updateList()
            }
        ))

        add(InfoMenu(
            title = "Bitmap Pool Statistics",
            desc = "Click clear",
            info = "%s/%s".format(
                sketch.bitmapPool.size.formatFileSize(0, false, true),
                sketch.bitmapPool.maxSize.formatFileSize(0, false, true)
            ),
            onClick = {
                sketch.bitmapPool.clear()
                updateList()
            }
        ))

        add(InfoMenu(
            "Disk Cache Statistics",
            "Click clear",
            info = "%s/%s".format(
                sketch.diskCache.size.formatFileSize(0, false, true),
                sketch.diskCache.maxSize.formatFileSize(0, false, true)
            ),
            onClick = {
                sketch.diskCache.clear()
                updateList()
            }
        ))
    }

    private fun makeOtherMenuList(): List<Any> = buildList {
        add(
            SwitchMenu(
                title = "Show Tile Bounds",
                data = appSettingsService.showTileBoundsInHugeImagePage,
                desc = "Only huge image page"
            )
        )
        add(
            SwitchMenu(
                title = "Read Mode",
                data = appSettingsService.readModeEnabled,
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
                    appSettingsService.logLevel.value = value
                }
            )
        )
    }
}