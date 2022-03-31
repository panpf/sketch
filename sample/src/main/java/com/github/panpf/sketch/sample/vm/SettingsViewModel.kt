package com.github.panpf.sketch.sample.vm

import android.app.Application
import android.os.Build
import androidx.lifecycle.MutableLiveData
import com.github.panpf.sketch.resize.Scale
import com.github.panpf.sketch.sample.appSettingsService
import com.github.panpf.sketch.sample.base.LifecycleAndroidViewModel
import com.github.panpf.sketch.sample.bean.InfoMenu
import com.github.panpf.sketch.sample.bean.ListSeparator
import com.github.panpf.sketch.sample.bean.MultiSelectMenu
import com.github.panpf.sketch.sample.bean.SwitchMenu
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
                title = "MimeType logo",
                data = appSettingsService.showMimeTypeLogoInLIst,
                desc = "Displays the image type in the lower right corner of the ImageView"
            )
        )
        add(
            SwitchMenu(
                title = "Progress indicator",
                data = appSettingsService.showProgressIndicatorInList,
                desc = "A black translucent mask is displayed on the ImageView surface to indicate progress"
            )
        )
        add(
            SwitchMenu(
                title = "Save cellular traffic",
                data = appSettingsService.saveCellularTrafficInList,
                desc = "Mobile cell traffic does not download pictures"
            )
        )
        add(
            SwitchMenu(
                title = "Pause load when scroll",
                data = appSettingsService.pauseLoadWhenScrollInList,
                desc = "No image is loaded during list scrolling to improve the smoothness of list sliding"
            )
        )
        add(
            MultiSelectMenu(
                title = "Resize precision",
                desc = null,
                values = listOf(
                    "LESS_PIXELS",
                    "KEEP_ASPECT_RATIO",
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
                        1 -> "KEEP_ASPECT_RATIO"
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
                "Resize scale",
                null,
                Scale.values().map { it.name },
                value = {
                    appSettingsService.resizeScale.value
                },
                onSelect = { which ->
                    appSettingsService.resizeScale.value = when (which) {
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
                    title = "in prefer quality over speed",
                    desc = null,
                    data = appSettingsService.inPreferQualityOverSpeed
                )
            )
        }
        add(
            MultiSelectMenu(
                "Bitmap quality",
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
                title = "Exif orientation",
                desc = null,
                data = appSettingsService.ignoreExifOrientation,
                reverse = true
            )
        )
    }

    private fun makeCacheMenuList(): List<Any> = buildList {
        add(
            SwitchMenu(
                title = "Bitmap memory cache",
                desc = null,
                data = appSettingsService.disabledBitmapMemoryCache,
                reverse = true
            )
        )

        add(
            SwitchMenu(
                title = "Bitmap pool",
                desc = null,
                data = appSettingsService.disabledBitmapPool,
                reverse = true
            )
        )

        add(
            SwitchMenu(
                title = "Bitmap result disk cache",
                desc = null,
                data = appSettingsService.disabledBitmapResultDiskCache,
                reverse = true
            )
        )

        add(
            SwitchMenu(
                title = "Network content disk cache",
                desc = null,
                data = appSettingsService.disabledNetworkContentDiskCache,
                reverse = true
            )
        )

        val sketch = application1.sketch
        add(InfoMenu("Memory cache statistics", "Click clear", getInfo = {
            val usedSizeFormat = sketch.memoryCache.size.formatFileSize(0, false, true)
            val maxSizeFormat = sketch.memoryCache.maxSize.formatFileSize(0, false, true)
            "$usedSizeFormat/$maxSizeFormat"
        }, onClick = {
            sketch.memoryCache.clear()
        }))

        add(InfoMenu("Bitmap pool statistics", "Click clear", getInfo = {
            val usedSizeFormat = sketch.bitmapPool.size.formatFileSize(0, false, true)
            val maxSizeFormat = sketch.bitmapPool.maxSize.formatFileSize(0, false, true)
            "$usedSizeFormat/$maxSizeFormat"
        }, onClick = {
            sketch.bitmapPool.clear()
        }))

        add(InfoMenu("Disk cache statistics", "Click clear", getInfo = {
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
                title = "Show data from logo",
                data = appSettingsService.showDataFromLogo,
                desc = "A different color triangle is displayed in the lower right corner of the ImageView according to DataFrom"
            )
        )
        add(
            SwitchMenu(
                title = "Show tile bounds in 'Huge Image' page",
                data = appSettingsService.showTileBoundsInHugeImagePage,
                desc = null
            )
        )
        add(
            MultiSelectMenu(
                "Logger level",
                null,
                listOf("VERBOSE", "DEBUG", "INFO", "WARNING", "ERROR", "NONE"),
                value = {
                    application1.sketch.logger.level.toString()
                },
                onSelect = { which ->
                    when (which) {
                        0 -> application1.sketch.logger.level = Logger.Level.VERBOSE
                        1 -> application1.sketch.logger.level = Logger.Level.DEBUG
                        2 -> application1.sketch.logger.level = Logger.Level.INFO
                        3 -> application1.sketch.logger.level = Logger.Level.WARNING
                        4 -> application1.sketch.logger.level = Logger.Level.ERROR
                        5 -> application1.sketch.logger.level = Logger.Level.NONE
                        else -> throw IllegalArgumentException("$which")
                    }
                })
        )
    }
}