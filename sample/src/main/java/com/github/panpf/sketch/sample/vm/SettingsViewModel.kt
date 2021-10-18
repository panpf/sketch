package com.github.panpf.sketch.sample.vm

import android.app.Application
import android.content.Context
import android.text.format.Formatter
import androidx.lifecycle.MutableLiveData
import com.github.panpf.liveevent.LiveEvent
import com.github.panpf.sketch.SLog
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.sample.appSettingsService
import com.github.panpf.sketch.sample.base.LifecycleAndroidViewModel
import com.github.panpf.sketch.sample.bean.CheckMenu
import com.github.panpf.sketch.sample.bean.InfoMenu
import com.github.panpf.sketch.sample.bean.ListSeparator

class SettingsViewModel(application1: Application) : LifecycleAndroidViewModel(application1) {

    val menuListData = MutableLiveData<List<Any>>()
    val showLogLevelDialogEvent = LiveEvent<Int>()

    init {
        update()
    }

    fun update() {
        menuListData.postValue(makeMenuList())
    }

    private fun makeMenuList(): List<Any> {
        val appContext = application1
        val appSettingsService = application1.appSettingsService

        return ArrayList<Any>().apply {
            add(ListSeparator("Cache"))
            add(CacheInfoMenu(appContext, "Memory", "Memory Cache (Click Clean)"))
            add(CacheInfoMenu(appContext, "BitmapPool", "Bitmap Pool (Click Clean)"))
            add(CacheInfoMenu(appContext, "Disk", "Disk Cache (Click Clean)"))
            add(CheckMenu("Disable Memory Cache", appSettingsService.memoryCacheDisabled))
            add(CheckMenu("Disable Bitmap Pool", appSettingsService.bitmapPoolDisabled))
            add(CheckMenu("Disable Disk Cache", appSettingsService.diskCacheDisabled))

            add(ListSeparator("Decode"))
            add(
                CheckMenu(
                    "In Prefer Quality Over Speed",
                    appSettingsService.inPreferQualityOverSpeedEnabled
                )
            )
            add(
                CheckMenu(
                    "Low Quality Bitmap",
                    appSettingsService.lowQualityImageEnabled
                )
            )
            add(
                CheckMenu(
                    "Cache Processed Image In Disk",
                    appSettingsService.cacheProcessedImageEnabled
                )
            )
            add(
                CheckMenu(
                    "Correct Image Orientation",
                    appSettingsService.correctImageOrientationEnabled
                )
            )

            add(ListSeparator("Other"))
            add(
                CheckMenu(
                    "Show Image From Corner Mark",
                    appSettingsService.showImageFromFlagEnabled
                )
            )
            add(
                CheckMenu(
                    "Scrolling Pause Load Image In List",
                    appSettingsService.scrollingPauseLoadEnabled
                )
            )

            add(ListSeparator("Log"))
            add(LogLevelMenu(appContext, showLogLevelDialogEvent))
            add(
                CheckMenu(
                    "Sync Output Log To Disk (cache/sketch_log)",
                    appSettingsService.outLog2SdcardLevel
                )
            )
        }
    }

    class LogLevelMenu(val context: Context, private val showLogLevelDialogEvent: LiveEvent<Int>) :
        InfoMenu("Log Level") {
        override fun getInfo(): String {
            return when (SLog.getLevel()) {
                SLog.VERBOSE -> "VERBOSE"
                SLog.DEBUG -> "DEBUG"
                SLog.INFO -> "INFO"
                SLog.WARNING -> "WARNING"
                SLog.ERROR -> "ERROR"
                SLog.NONE -> "NONE"
                else -> "Unknown"
            }
        }

        override fun onClick() {
            showLogLevelDialogEvent.postValue(1)
        }
    }

    class CacheInfoMenu(val context: Context, val type: String, title: String) : InfoMenu(title) {
        override fun getInfo(): String {
            when (type) {
                "Memory" -> {
                    val memoryCache = Sketch.with(context).configuration.memoryCache
                    val usedSizeFormat = Formatter.formatFileSize(context, memoryCache.size)
                    val maxSizeFormat = Formatter.formatFileSize(context, memoryCache.maxSize)
                    return "$usedSizeFormat/$maxSizeFormat"
                }
                "Disk" -> {
                    val diskCache = Sketch.with(context).configuration.diskCache
                    val usedSizeFormat = Formatter.formatFileSize(context, diskCache.size)
                    val maxSizeFormat = Formatter.formatFileSize(context, diskCache.maxSize)
                    return "$usedSizeFormat/$maxSizeFormat"
                }
                "BitmapPool" -> {
                    val bitmapPool = Sketch.with(context).configuration.bitmapPool
                    val usedSizeFormat = Formatter.formatFileSize(context, bitmapPool.size.toLong())
                    val maxSizeFormat =
                        Formatter.formatFileSize(context, bitmapPool.maxSize.toLong())
                    return "$usedSizeFormat/$maxSizeFormat"
                }
                else -> return "Unknown Type"
            }
        }

        override fun onClick() {
            when (type) {
                "Memory" -> {
                    Sketch.with(context).configuration.memoryCache.clear()
                }
                "Disk" -> {
                    Sketch.with(context).configuration.diskCache.clear()
                }
                "BitmapPool" -> {
                    Sketch.with(context).configuration.bitmapPool.clear()
                }
            }
        }
    }
}