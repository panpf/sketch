package me.panpf.sketch.sample

import android.app.Application
import android.content.Context
import android.os.Handler
import android.os.Looper
import me.panpf.sketch.Configuration
import me.panpf.sketch.Initializer
import me.panpf.sketch.SLog
import me.panpf.sketch.sample.util.VideoThumbnailUriModel
import me.panpf.sketch.util.SketchUtils

class MySketchInitializer : Initializer {

    private var context: Context? = null
    private var configuration: Configuration? = null

    override fun onInitialize(context: Context, configuration: Configuration) {
        this.context = context
        this.configuration = configuration
        configuration.uriModelManager.add(VideoThumbnailUriModel())

        if (SketchUtils.isMainThread()) {
            AppEvents.appConfigChangedEvent.listenForever {
                it?.let { it1 -> onConfigChange(it1) }
            }
        } else {
            Handler(Looper.getMainLooper()).post {
                AppEvents.appConfigChangedEvent.listenForever {
                    it?.let { it1 -> onConfigChange(it1) }
                }
            }
        }

        initConfig()
    }

    private fun initConfig() {
        onConfigChange(AppConfig.Key.OUT_LOG_2_SDCARD)
        onConfigChange(AppConfig.Key.LOG_LEVEL)

        onConfigChange(AppConfig.Key.MOBILE_NETWORK_PAUSE_DOWNLOAD)
        onConfigChange(AppConfig.Key.GLOBAL_LOW_QUALITY_IMAGE)
        onConfigChange(AppConfig.Key.GLOBAL_IN_PREFER_QUALITY_OVER_SPEED)
        onConfigChange(AppConfig.Key.GLOBAL_DISABLE_CACHE_IN_DISK)
        onConfigChange(AppConfig.Key.GLOBAL_DISABLE_BITMAP_POOL)
        onConfigChange(AppConfig.Key.GLOBAL_DISABLE_CACHE_IN_MEMORY)

        configuration!!.callback = MySketchCallback(context!!.applicationContext as Application)
    }

    private fun onConfigChange(key: AppConfig.Key) {
        when (key) {
            AppConfig.Key.OUT_LOG_2_SDCARD -> {
                val proxy = if (AppConfig.getBoolean(
                        context!!,
                        AppConfig.Key.OUT_LOG_2_SDCARD
                    )
                ) MySketchLogProxy(context!!) else null
                SLog.setProxy(proxy)
            }
            AppConfig.Key.LOG_LEVEL -> {
                var levelValue: String? =
                    AppConfig.getString(context!!, AppConfig.Key.LOG_LEVEL)
                if (levelValue == null) {
                    levelValue = if (BuildConfig.DEBUG) "DEBUG" else "INFO"
                }
                when (levelValue) {
                    "VERBOSE" -> SLog.setLevel(SLog.VERBOSE)
                    "DEBUG" -> SLog.setLevel(SLog.DEBUG)
                    "INFO" -> SLog.setLevel(SLog.INFO)
                    "ERROR" -> SLog.setLevel(SLog.ERROR)
                    "WARNING" -> SLog.setLevel(SLog.WARNING)
                    "NONE" -> SLog.setLevel(SLog.NONE)
                }
            }
            AppConfig.Key.MOBILE_NETWORK_PAUSE_DOWNLOAD -> configuration!!.isMobileDataPauseDownloadEnabled =
                AppConfig.getBoolean(context!!, AppConfig.Key.MOBILE_NETWORK_PAUSE_DOWNLOAD)
            AppConfig.Key.GLOBAL_LOW_QUALITY_IMAGE -> configuration!!.isLowQualityImageEnabled =
                AppConfig.getBoolean(context!!, AppConfig.Key.GLOBAL_LOW_QUALITY_IMAGE)
            AppConfig.Key.GLOBAL_IN_PREFER_QUALITY_OVER_SPEED -> configuration!!.isInPreferQualityOverSpeedEnabled =
                AppConfig.getBoolean(
                    context!!,
                    AppConfig.Key.GLOBAL_IN_PREFER_QUALITY_OVER_SPEED
                )
            AppConfig.Key.GLOBAL_DISABLE_CACHE_IN_DISK -> configuration!!.diskCache.isDisabled =
                AppConfig.getBoolean(context!!, AppConfig.Key.GLOBAL_DISABLE_CACHE_IN_DISK)
            AppConfig.Key.GLOBAL_DISABLE_BITMAP_POOL -> configuration!!.bitmapPool.isDisabled =
                AppConfig.getBoolean(context!!, AppConfig.Key.GLOBAL_DISABLE_BITMAP_POOL)
            AppConfig.Key.GLOBAL_DISABLE_CACHE_IN_MEMORY -> configuration!!.memoryCache.isDisabled =
                AppConfig.getBoolean(context!!, AppConfig.Key.GLOBAL_DISABLE_CACHE_IN_MEMORY)
        }
    }
}
