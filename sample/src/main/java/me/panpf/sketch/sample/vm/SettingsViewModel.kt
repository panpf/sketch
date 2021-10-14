package me.panpf.sketch.sample.vm

import android.app.Application
import android.content.Context
import android.text.format.Formatter
import androidx.lifecycle.MutableLiveData
import com.github.panpf.liveevent.LiveEvent
import me.panpf.sketch.SLog
import me.panpf.sketch.Sketch
import me.panpf.sketch.sample.AppConfig
import me.panpf.sketch.sample.AppEvents
import me.panpf.sketch.sample.base.LifecycleAndroidViewModel
import me.panpf.sketch.sample.bean.CheckMenu
import me.panpf.sketch.sample.bean.InfoMenu
import me.panpf.sketch.sample.bean.ListSeparator

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

        return ArrayList<Any>().apply {
            add(ListSeparator("Cache"))
            add(CacheInfoMenu(appContext, "Memory", "Memory Cache (Click Clean)"))
            add(CacheInfoMenu(appContext, "BitmapPool", "Bitmap Pool (Click Clean)"))
            add(CacheInfoMenu(appContext, "Disk", "Disk Cache (Click Clean)"))
            add(
                CheckMenu(
                    appContext,
                    "Disable Memory Cache",
                    AppConfig.Key.GLOBAL_DISABLE_CACHE_IN_MEMORY
                )
            )
            add(
                CheckMenu(
                    appContext,
                    "Disable Bitmap Pool",
                    AppConfig.Key.GLOBAL_DISABLE_BITMAP_POOL
                )
            )
            add(
                CheckMenu(
                    appContext,
                    "Disable Disk Cache",
                    AppConfig.Key.GLOBAL_DISABLE_CACHE_IN_DISK
                )
            )

            add(ListSeparator("Zoom"))
            add(
                CheckMenu(
                    appContext,
                    "Enabled Read Mode In Detail Page",
                    AppConfig.Key.READ_MODE
                )
            )
            add(
                CheckMenu(
                    appContext,
                    "Enabled Location Animation In Detail Page",
                    AppConfig.Key.LOCATION_ANIMATE
                )
            )
            add(
                CheckMenu(
                    appContext,
                    "Pause Block Display When Page Not Visible In Detail Page",
                    AppConfig.Key.PAUSE_BLOCK_DISPLAY_WHEN_PAGE_NOT_VISIBLE
                )
            )
            add(
                CheckMenu(
                    appContext,
                    "Fixed Three Level Zoom Mode",
                    AppConfig.Key.FIXED_THREE_LEVEL_ZOOM_MODE
                )
            )

            add(ListSeparator("GIF"))
            add(
                CheckMenu(
                    appContext,
                    "Auto Play GIF In List",
                    AppConfig.Key.PLAY_GIF_ON_LIST
                )
            )
            add(
                CheckMenu(
                    appContext,
                    "Click Play GIF In List",
                    AppConfig.Key.CLICK_PLAY_GIF
                )
            )
            add(
                CheckMenu(
                    appContext,
                    "Show GIF Flag In List",
                    AppConfig.Key.SHOW_GIF_FLAG
                )
            )

            add(ListSeparator("Decode"))
            add(
                CheckMenu(
                    appContext,
                    "In Prefer Quality Over Speed",
                    AppConfig.Key.GLOBAL_IN_PREFER_QUALITY_OVER_SPEED
                )
            )
            add(
                CheckMenu(
                    appContext,
                    "Low Quality Bitmap",
                    AppConfig.Key.GLOBAL_LOW_QUALITY_IMAGE
                )
            )
            add(
                CheckMenu(
                    appContext,
                    "Enabled Thumbnail Mode In List",
                    AppConfig.Key.THUMBNAIL_MODE
                )
            )
            add(
                CheckMenu(
                    appContext,
                    "Cache Processed Image In Disk",
                    AppConfig.Key.CACHE_PROCESSED_IMAGE
                )
            )
            add(
                CheckMenu(
                    appContext,
                    "Disabled Correct Image Orientation",
                    AppConfig.Key.DISABLE_CORRECT_IMAGE_ORIENTATION
                )
            )

            add(ListSeparator("Other"))
            add(
                CheckMenu(
                    appContext,
                    "Show Round Rect In Photo List",
                    AppConfig.Key.SHOW_ROUND_RECT_IN_PHOTO_LIST
                )
            )
            add(
                CheckMenu(
                    appContext,
                    "Show Unsplash Raw Image In Detail Page",
                    AppConfig.Key.SHOW_UNSPLASH_RAW_IMAGE
                )
            )
            add(
                CheckMenu(
                    appContext,
                    "Show Mapping Thumbnail In Detail Page",
                    AppConfig.Key.SHOW_TOOLS_IN_IMAGE_DETAIL
                )
            )
            add(
                CheckMenu(
                    appContext,
                    "Show Press Status In List",
                    AppConfig.Key.CLICK_SHOW_PRESSED_STATUS
                )
            )
            add(
                CheckMenu(
                    appContext,
                    "Show Image From Corner Mark",
                    AppConfig.Key.SHOW_IMAGE_FROM_FLAG
                )
            )
            add(
                CheckMenu(
                    appContext,
                    "Show Download Progress In List",
                    AppConfig.Key.SHOW_IMAGE_DOWNLOAD_PROGRESS
                )
            )
            add(
                CheckMenu(
                    appContext,
                    "Click Show Image On Pause Download In List",
                    AppConfig.Key.CLICK_RETRY_ON_PAUSE_DOWNLOAD
                )
            )
            add(
                CheckMenu(
                    appContext,
                    "Click Retry On Error In List",
                    AppConfig.Key.CLICK_RETRY_ON_FAILED
                )
            )
            add(
                CheckMenu(
                    appContext,
                    "Scrolling Pause Load Image In List",
                    AppConfig.Key.SCROLLING_PAUSE_LOAD
                )
            )
            add(
                CheckMenu(
                    appContext,
                    "Mobile Data Pause Download Image",
                    AppConfig.Key.MOBILE_NETWORK_PAUSE_DOWNLOAD
                )
            )
            add(
                CheckMenu(
                    appContext,
                    "Long Clock Show Image Info",
                    AppConfig.Key.LONG_CLICK_SHOW_IMAGE_INFO
                )
            )

            add(ListSeparator("Log"))
            add(LogLevelMenu(appContext, showLogLevelDialogEvent))
            add(
                CheckMenu(
                    appContext,
                    "Sync Output Log To Disk (cache/sketch_log)",
                    AppConfig.Key.OUT_LOG_2_SDCARD
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
                    AppEvents.cacheCleanEvent.postValue(1)
                }
                "Disk" -> {
                    Sketch.with(context).configuration.diskCache.clear()
                    AppEvents.cacheCleanEvent.postValue(1)
                }
                "BitmapPool" -> {
                    Sketch.with(context).configuration.bitmapPool.clear()
                    AppEvents.cacheCleanEvent.postValue(1)
                }
            }
        }
    }
}