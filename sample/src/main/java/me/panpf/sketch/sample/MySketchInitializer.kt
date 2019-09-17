package me.panpf.sketch.sample

import android.app.Application
import android.content.Context
import me.panpf.sketch.Configuration
import me.panpf.sketch.Initializer
import me.panpf.sketch.SLog
import me.panpf.sketch.sample.event.AppConfigChangedEvent
import me.panpf.sketch.sample.util.XpkIconUriModel
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

class MySketchInitializer : Initializer {

    private var context: Context? = null
    private var configuration: Configuration? = null

    override fun onInitialize(context: Context, configuration: Configuration) {
        this.context = context
        this.configuration = configuration

        initConfig()

        EventBus.getDefault().register(this)
    }

    private fun initConfig() {
        onEvent(AppConfigChangedEvent(AppConfig.Key.OUT_LOG_2_SDCARD))
        onEvent(AppConfigChangedEvent(AppConfig.Key.LOG_LEVEL))

        onEvent(AppConfigChangedEvent(AppConfig.Key.MOBILE_NETWORK_PAUSE_DOWNLOAD))
        onEvent(AppConfigChangedEvent(AppConfig.Key.GLOBAL_LOW_QUALITY_IMAGE))
        onEvent(AppConfigChangedEvent(AppConfig.Key.GLOBAL_IN_PREFER_QUALITY_OVER_SPEED))
        onEvent(AppConfigChangedEvent(AppConfig.Key.GLOBAL_DISABLE_CACHE_IN_DISK))
        onEvent(AppConfigChangedEvent(AppConfig.Key.GLOBAL_DISABLE_BITMAP_POOL))
        onEvent(AppConfigChangedEvent(AppConfig.Key.GLOBAL_DISABLE_CACHE_IN_MEMORY))

        configuration!!.callback = MySketchCallback(context!!.applicationContext as Application)

        configuration!!.uriModelManager.add(XpkIconUriModel())
    }

    @Subscribe
    fun onEvent(event: AppConfigChangedEvent) {
        when {
            AppConfig.Key.OUT_LOG_2_SDCARD == event.key -> {
                val proxy = if (AppConfig.getBoolean(context!!, AppConfig.Key.OUT_LOG_2_SDCARD)) MySketchLogProxy(context!!) else null
                SLog.setProxy(proxy)
            }
            AppConfig.Key.LOG_LEVEL == event.key -> {
                var levelValue: String? = AppConfig.getString(context!!, AppConfig.Key.LOG_LEVEL)
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
            AppConfig.Key.MOBILE_NETWORK_PAUSE_DOWNLOAD == event.key -> configuration!!.isMobileDataPauseDownloadEnabled = AppConfig.getBoolean(context!!, AppConfig.Key.MOBILE_NETWORK_PAUSE_DOWNLOAD)
            AppConfig.Key.GLOBAL_LOW_QUALITY_IMAGE == event.key -> configuration!!.isLowQualityImageEnabled = AppConfig.getBoolean(context!!, AppConfig.Key.GLOBAL_LOW_QUALITY_IMAGE)
            AppConfig.Key.GLOBAL_IN_PREFER_QUALITY_OVER_SPEED == event.key -> configuration!!.isInPreferQualityOverSpeedEnabled = AppConfig.getBoolean(context!!, AppConfig.Key.GLOBAL_IN_PREFER_QUALITY_OVER_SPEED)
            AppConfig.Key.GLOBAL_DISABLE_CACHE_IN_DISK == event.key -> configuration!!.diskCache.isDisabled = AppConfig.getBoolean(context!!, AppConfig.Key.GLOBAL_DISABLE_CACHE_IN_DISK)
            AppConfig.Key.GLOBAL_DISABLE_BITMAP_POOL == event.key -> configuration!!.bitmapPool.isDisabled = AppConfig.getBoolean(context!!, AppConfig.Key.GLOBAL_DISABLE_BITMAP_POOL)
            AppConfig.Key.GLOBAL_DISABLE_CACHE_IN_MEMORY == event.key -> configuration!!.memoryCache.isDisabled = AppConfig.getBoolean(context!!, AppConfig.Key.GLOBAL_DISABLE_CACHE_IN_MEMORY)
        }
    }
}
