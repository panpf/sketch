package com.github.panpf.sketch.sample.util

import com.github.panpf.sketch.cache.CachePolicy.DISABLED
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
            if (appSettings.disabledBitmapMemoryCache.value == true) {
                bitmapMemoryCachePolicy(DISABLED)
            }
            if (appSettings.disabledNetworkContentDiskCache.value == true) {
                networkContentDiskCachePolicy(DISABLED)
            }
            if (appSettings.disabledBitmapResultDiskCache.value == true) {
                bitmapResultDiskCachePolicy(DISABLED)
            }
            if (appSettings.disabledBitmapPool.value == true) {
                disabledBitmapPool(true)
            }
            if (appSettings.disabledCorrectImageOrientation.value == true) {
                disabledCorrectExifOrientation(true)
            }
            val target = chain.request.target
            if (target is ViewTarget<*>) {
                val view = target.view
                if (view is MyListImageView) {
                    if (appSettings.disabledAnimatableDrawableInList.value == true) {
                        disabledAnimationDrawable(true)
                    }
                    if (appSettings.pauseLoadWhenScrollInList.value == true) {
                        pauseLoadWhenScrolling(true)
                    }
                    if (appSettings.saveCellularTrafficInList.value == true) {
                        saveCellularTraffic(true)
                    }
                }
            }
        }
        return chain.proceed(newRequest)
    }

    override fun toString(): String = "SettingsDisplayRequestInterceptor"
}