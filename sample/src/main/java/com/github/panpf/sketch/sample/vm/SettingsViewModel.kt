package com.github.panpf.sketch.sample.vm

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.github.panpf.liveevent.LiveEvent
import com.github.panpf.sketch.sample.appSettingsService
import com.github.panpf.sketch.sample.base.LifecycleAndroidViewModel
import com.github.panpf.sketch.sample.bean.ListSeparator
import com.github.panpf.sketch.sample.bean.SwitchMenu

class SettingsViewModel(application1: Application) : LifecycleAndroidViewModel(application1) {

    val menuListData = MutableLiveData<List<Any>>()
    val showLogLevelDialogEvent = LiveEvent<Int>()
    private val appSettingsService = application1.appSettingsService

    init {
        update()
    }

    fun update() {
        val assembleMenuList = buildList {
            add(ListSeparator("List"))
            addAll(makeListMenuList())
            add(ListSeparator("Global"))
            addAll(makeGlobalMenuList())
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
    }

    private fun makeGlobalMenuList(): List<Any> = buildList {
        add(
            SwitchMenu(
                title = "Show data from",
                data = appSettingsService.showDataFrom,
                desc = "A different color triangle is displayed in the lower right corner of the ImageView according to DataFrom"
            )
        )
        add(
            SwitchMenu(
                title = "Bitmap memory cache",
                data = appSettingsService.disabledBitmapMemoryCache,
                reverse = true
            )
        )
        add(
            SwitchMenu(
                title = "Bitmap result disk cache",
                data = appSettingsService.disabledBitmapResultDiskCache,
                reverse = true
            )
        )
        add(
            SwitchMenu(
                title = "Network content disk cache",
                data = appSettingsService.disabledNetworkContentDiskCache,
                reverse = true
            )
        )
        add(
            SwitchMenu(
                title = "Bitmap reuse pool",
                data = appSettingsService.disabledBitmapPool,
                reverse = true
            )
        )
        add(SwitchMenu("Ignore exif orientation", appSettingsService.ignoreExifOrientation))
    }

//    private fun makeMenuList(): List<Any> {
//        val appContext = application1
//        val appSettingsService = application1.appSettingsService
//
//        return ArrayList<Any>().apply {
//            add(ListSeparator("Cache"))
//            add(CacheInfoMenu(appContext, "Memory", "Memory Cache (Click Clean)"))
//            add(CacheInfoMenu(appContext, "BitmapPool", "Bitmap Pool (Click Clean)"))
//            add(CacheInfoMenu(appContext, "Disk", "Disk Cache (Click Clean)"))
//            add(SwitchMenu("Disable Memory Cache", appSettingsService.memoryCacheDisabled))
//            add(SwitchMenu("Disable Bitmap Pool", appSettingsService.bitmapPoolDisabled))
//            add(SwitchMenu("Disable Disk Cache", appSettingsService.diskCacheDisabled))
//
//            add(ListSeparator("Decode"))
//            add(
//                SwitchMenu(
//                    "In Prefer Quality Over Speed",
//                    appSettingsService.inPreferQualityOverSpeedEnabled
//                )
//            )
//            add(
//                SwitchMenu(
//                    "Low Quality Bitmap",
//                    appSettingsService.lowQualityImageEnabled
//                )
//            )
//            add(
//                SwitchMenu(
//                    "Cache Processed Image In Disk",
//                    appSettingsService.cacheProcessedImageEnabled
//                )
//            )
//            add(
//                SwitchMenu(
//                    "Correct Image Orientation",
//                    appSettingsService.correctImageOrientationEnabled
//                )
//            )
//
//            add(ListSeparator("Other"))
//            add(
//                SwitchMenu(
//                    "Show Image From Corner Mark",
//                    appSettingsService.showImageFromFlagEnabled
//                )
//            )
//            add(
//                SwitchMenu(
//                    "Scrolling Pause Load Image In List",
//                    appSettingsService.scrollingPauseLoadEnabled
//                )
//            )
//
//            add(ListSeparator("Log"))
//            add(LogLevelMenu(appContext, showLogLevelDialogEvent))
//            add(
//                SwitchMenu(
//                    "Sync Output Log To Disk (cache/sketch_log)",
//                    appSettingsService.outLog2SdcardLevel
//                )
//            )
//        }
//    }

//    class LogLevelMenu(val context: Context, private val showLogLevelDialogEvent: LiveEvent<Int>) :
//        InfoMenu("Log Level") {
//        override fun getInfo(): String {
//            return when (SLog.getLevel()) {
//                SLog.VERBOSE -> "VERBOSE"
//                SLog.DEBUG -> "DEBUG"
//                SLog.INFO -> "INFO"
//                SLog.WARNING -> "WARNING"
//                SLog.ERROR -> "ERROR"
//                SLog.NONE -> "NONE"
//                else -> "Unknown"
//            }
//        }
//
//        override fun onClick() {
//            showLogLevelDialogEvent.postValue(1)
//        }
//    }
//
//    class CacheInfoMenu(val context: Context, val type: String, title: String) : InfoMenu(title) {
//        override fun getInfo(): String {
//            when (type) {
//                "Memory" -> {
//                    val memoryCache = context.sketch.memoryCache
//                    val usedSizeFormat = Formatter.formatFileSize(context, memoryCache.size)
//                    val maxSizeFormat = Formatter.formatFileSize(context, memoryCache.maxSize)
//                    return "$usedSizeFormat/$maxSizeFormat"
//                }
//                "Disk" -> {
//                    val diskCache = context.sketch.diskCache
//                    val usedSizeFormat = Formatter.formatFileSize(context, diskCache.size)
//                    val maxSizeFormat = Formatter.formatFileSize(context, diskCache.maxSize)
//                    return "$usedSizeFormat/$maxSizeFormat"
//                }
//                "BitmapPool" -> {
//                    val bitmapPool = context.sketch.bitmapPool
//                    val usedSizeFormat = Formatter.formatFileSize(context, bitmapPool.size.toLong())
//                    val maxSizeFormat =
//                        Formatter.formatFileSize(context, bitmapPool.maxSize.toLong())
//                    return "$usedSizeFormat/$maxSizeFormat"
//                }
//                else -> return "Unknown Type"
//            }
//        }
//
//        override fun onClick() {
//            when (type) {
//                "Memory" -> {
//                    context.sketch.memoryCache.clear()
//                }
//                "Disk" -> {
//                    context.sketch.diskCache.clear()
//                }
//                "BitmapPool" -> {
//                    context.sketch.bitmapPool.clear()
//                }
//            }
//        }
//    }
}