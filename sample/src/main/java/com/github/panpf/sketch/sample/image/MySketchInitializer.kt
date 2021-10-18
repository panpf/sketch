package com.github.panpf.sketch.sample.image

import android.app.Application
import android.content.Context
import com.github.panpf.sketch.Configuration
import com.github.panpf.sketch.Initializer
import com.github.panpf.sketch.SLog
import com.github.panpf.sketch.sample.appSettingsService
import com.github.panpf.sketch.sample.util.VideoThumbnailUriModel

class MySketchInitializer : Initializer {

    override fun onInitialize(context: Context, configuration: Configuration) {
        configuration.apply {
            uriModelManager.add(VideoThumbnailUriModel())
            callback = MySketchCallback(context.applicationContext as Application)

            context.appSettingsService.diskCacheDisabled.observeForever {
                diskCache.isDisabled = it == true
            }

            context.appSettingsService.bitmapPoolDisabled.observeForever {
                bitmapPool.isDisabled = it == true
            }

            context.appSettingsService.memoryCacheDisabled.observeForever {
                memoryCache.isDisabled = it == true
            }

            context.appSettingsService.mobileNetworkPauseDownloadEnabled.observeForever {
                isMobileDataPauseDownloadEnabled = it == true
            }

            context.appSettingsService.lowQualityImageEnabled.observeForever {
                isLowQualityImageEnabled = it == true
            }

            context.appSettingsService.inPreferQualityOverSpeedEnabled.observeForever {
                isInPreferQualityOverSpeedEnabled = it == true
            }
        }

        context.appSettingsService.apply {
            outLog2SdcardLevel.observeForever {
                if (it == true) {
                    SLog.setProxy(MySketchLogProxy(context))
                } else {
                    SLog.setProxy(null)
                }
            }

            logLevel.observeForever {
                if (it != null) {
                    SLog.setLevel(it)
                }
            }
        }
    }
}
