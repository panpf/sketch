package com.github.panpf.sketch.sample.ui.setting

import android.app.Application
import android.os.Build
import androidx.lifecycle.MutableLiveData
import com.github.panpf.sketch.resize.Scale
import com.github.panpf.sketch.sample.appSettingsService
import com.github.panpf.sketch.sample.model.InfoMenu
import com.github.panpf.sketch.sample.model.ListSeparator
import com.github.panpf.sketch.sample.model.MultiSelectMenu
import com.github.panpf.sketch.sample.model.SwitchMenu
import com.github.panpf.sketch.sample.ui.base.LifecycleAndroidViewModel
import com.github.panpf.sketch.sketch
import com.github.panpf.sketch.util.Logger
import com.github.panpf.tools4j.io.ktx.formatFileSize

class SettingsViewModel(application1: Application) : LifecycleAndroidViewModel(application1) {

    val menuListData = MutableLiveData<List<Any>>()
    private val appSettingsService = application1.appSettingsService

    init {
        val assembleMenuList = buildList {
            this.add(ListSeparator("List"))
            this.addAll(makeListMenuList())
            this.add(ListSeparator("Decode"))
            this.addAll(makeDecodeMenuList())
            this.add(ListSeparator("Cache"))
            this.addAll(makeCacheMenuList())
            this.add(ListSeparator("Other"))
            this.addAll(makeOtherMenuList())
        }
        menuListData.postValue(assembleMenuList)
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
                title = "Save Cellular Traffic",
                data = appSettingsService.saveCellularTrafficInList,
                desc = "Mobile cell traffic does not download pictures"
            )
        )
        add(
            SwitchMenu(
                title = "Pause Load When Scrolling",
                data = appSettingsService.pauseLoadWhenScrollInList,
                desc = "No image is loaded during list scrolling to improve the smoothness of list sliding"
            )
        )
        add(
            MultiSelectMenu(
                title = "Resize Precision",
                desc = null,
                values = listOf(
                    "LESS_PIXELS",
                    "SAME_ASPECT_RATIO",
                    "EXACTLY",
                    "LONG_IMAGE_CROP",
                    "ORIGINAL"
                ),
                value = {
                    appSettingsService.resizePrecision.value
                },
                onSelect = { which ->
                    appSettingsService.resizePrecision.value = when (which) {
                        0 -> "LESS_PIXELS"
                        1 -> "SAME_ASPECT_RATIO"
                        2 -> "EXACTLY"
                        3 -> "LONG_IMAGE_CROP"
                        4 -> "ORIGINAL"
                        else -> throw IllegalArgumentException("$which")
                    }
                }
            )
        )
        add(
            MultiSelectMenu(
                "Resize Scale",
                null,
                Scale.values().map { it.name },
                value = {
                    appSettingsService.otherResizeScale.value
                },
                onSelect = { which ->
                    appSettingsService.otherResizeScale.value = when (which) {
                        0 -> Scale.START_CROP.name
                        1 -> Scale.CENTER_CROP.name
                        2 -> Scale.END_CROP.name
                        3 -> Scale.FILL.name
                        else -> throw IllegalArgumentException("$which")
                    }
                })
        )
        add(
            MultiSelectMenu(
                "Long Image Resize Scale",
                null,
                Scale.values().map { it.name },
                value = {
                    appSettingsService.longImageResizeScale.value
                },
                onSelect = { which ->
                    appSettingsService.longImageResizeScale.value = when (which) {
                        0 -> Scale.START_CROP.name
                        1 -> Scale.CENTER_CROP.name
                        2 -> Scale.END_CROP.name
                        3 -> Scale.FILL.name
                        else -> throw IllegalArgumentException("$which")
                    }
                })
        )
    }

    private fun makeDecodeMenuList(): List<Any> = buildList {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            add(
                SwitchMenu(
                    title = "inPreferQualityOverSpeed",
                    desc = null,
                    data = appSettingsService.inPreferQualityOverSpeed
                )
            )
        }
        add(
            MultiSelectMenu(
                "Bitmap Quality",
                null,
                listOf("LOW", "MIDDEN", "HIGH"),
                value = {
                    appSettingsService.bitmapQuality.value
                },
                onSelect = { which ->
                    appSettingsService.bitmapQuality.value = when (which) {
                        0 -> "LOW"
                        1 -> "MIDDEN"
                        2 -> "HIGH"
                        else -> throw IllegalArgumentException("$which")
                    }
                })
        )
        // todo color space
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
        add(InfoMenu("Memory Cache Statistics", "Click clear", getInfo = {
            val usedSizeFormat = sketch.memoryCache.size.formatFileSize(0, false, true)
            val maxSizeFormat = sketch.memoryCache.maxSize.formatFileSize(0, false, true)
            "$usedSizeFormat/$maxSizeFormat"
        }, onClick = {
            sketch.memoryCache.clear()
        }))

        add(InfoMenu("Bitmap Pool Statistics", "Click clear", getInfo = {
            val usedSizeFormat = sketch.bitmapPool.size.formatFileSize(0, false, true)
            val maxSizeFormat = sketch.bitmapPool.maxSize.formatFileSize(0, false, true)
            "$usedSizeFormat/$maxSizeFormat"
        }, onClick = {
            sketch.bitmapPool.clear()
        }))

        add(InfoMenu("Disk Cache Statistics", "Click clear", getInfo = {
            val usedSizeFormat = sketch.diskCache.size.formatFileSize(0, false, true)
            val maxSizeFormat = sketch.diskCache.maxSize.formatFileSize(0, false, true)
            "$usedSizeFormat/$maxSizeFormat"
        }, onClick = {
            sketch.diskCache.clear()
        }))
    }

    private fun makeOtherMenuList(): List<Any> = buildList {
        add(
            SwitchMenu(
                title = "Show Data From Logo",
                data = appSettingsService.showDataFromLogo,
                desc = "A different color triangle is displayed in the lower right corner of the ImageView according to DataFrom"
            )
        )
        add(
            SwitchMenu(
                title = "Show Tile Bounds In 'Huge Image' Page",
                data = appSettingsService.showTileBoundsInHugeImagePage,
                desc = null
            )
        )
        add(
            MultiSelectMenu(
                "Logger Level",
                null,
                listOf("VERBOSE", "DEBUG", "INFO", "WARNING", "ERROR", "NONE"),
                value = {
                    application1.sketch.logger.level.toString()
                },
                onSelect = { which ->
                    val logger = application1.sketch.logger
                    when (which) {
                        0 -> logger.level = Logger.Level.VERBOSE
                        1 -> logger.level = Logger.Level.DEBUG
                        2 -> logger.level = Logger.Level.INFO
                        3 -> logger.level = Logger.Level.WARNING
                        4 -> logger.level = Logger.Level.ERROR
                        5 -> logger.level = Logger.Level.NONE
                        else -> throw IllegalArgumentException("$which")
                    }
                })
        )
    }
}