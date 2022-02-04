package com.github.panpf.sketch.sample.util

import android.os.Build
import com.github.panpf.sketch.cache.CachePolicy.DISABLED
import com.github.panpf.sketch.decode.BitmapConfig
import com.github.panpf.sketch.request.DisplayData
import com.github.panpf.sketch.request.DisplayRequest
import com.github.panpf.sketch.request.RequestInterceptor
import com.github.panpf.sketch.request.RequestInterceptor.Chain
import com.github.panpf.sketch.request.pauseLoadWhenScrolling
import com.github.panpf.sketch.request.saveCellularTraffic
import com.github.panpf.sketch.sample.appSettingsService
import com.github.panpf.sketch.sample.widget.MyListImageView
import com.github.panpf.sketch.target.ViewTarget

class SettingsDisplayRequestInterceptor : RequestInterceptor<DisplayRequest, DisplayData> {
    override suspend fun intercept(chain: Chain<DisplayRequest, DisplayData>): DisplayData {
        val newRequest = chain.request.newDisplayRequest {
            val appSettings = chain.sketch.appContext.appSettingsService
            if (appSettings.disabledBitmapMemoryCache.value) {
                bitmapMemoryCachePolicy(DISABLED)
            }
            if (appSettings.disabledNetworkContentDiskCache.value) {
                networkContentDiskCachePolicy(DISABLED)
            }
            if (appSettings.disabledBitmapResultDiskCache.value) {
                bitmapResultDiskCachePolicy(DISABLED)
            }
            if (appSettings.disabledBitmapPool.value) {
                disabledBitmapPool(true)
            }
            if (appSettings.ignoreExifOrientation.value) {
                ignoreExifOrientation(true)
            }
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N && appSettings.inPreferQualityOverSpeed.value) {
                @Suppress("DEPRECATION")
                preferQualityOverSpeed(true)
            }
            when (appSettings.bitmapQuality.value) {
                "LOW" -> bitmapConfig(BitmapConfig.LOW_QUALITY)
                "MIDDEN" -> bitmapConfig(BitmapConfig.MIDDEN_QUALITY)
                "HIGH" -> bitmapConfig(BitmapConfig.HIGH_QUALITY)
            }
            val target = chain.request.target
            if (target is ViewTarget<*>) {
                val view = target.view
                if (view is MyListImageView) {
                    if (appSettings.disabledAnimatableDrawableInList.value) {
                        disabledAnimationDrawable(true)
                    }
                    if (appSettings.pauseLoadWhenScrollInList.value) {
                        pauseLoadWhenScrolling(true)
                    }
                    if (appSettings.saveCellularTrafficInList.value) {
                        saveCellularTraffic(true)
                    }
                }
            }
        }
        return chain.proceed(newRequest)
    }

    override fun toString(): String = "SettingsDisplayRequestInterceptor"
}