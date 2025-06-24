/*
 * Copyright (C) 2024 panpf <panpfpanpf@outlook.com>
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.panpf.sketch.sample.ui.widget

import android.content.Context
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.util.AttributeSet
import com.github.panpf.sketch.loadImage
import com.github.panpf.sketch.request.ImageOptions
import com.github.panpf.sketch.request.preferQualityOverSpeed
import com.github.panpf.sketch.request.repeatCount
import com.github.panpf.sketch.request.updateImageOptions
import com.github.panpf.sketch.sample.AppSettings
import com.github.panpf.sketch.sample.ui.util.lifecycleOwner
import com.github.panpf.sketch.sample.util.collectWithLifecycle
import com.github.panpf.sketch.sample.util.ignoreFirst
import com.github.panpf.sketch.util.SketchUtils
import com.github.panpf.zoomimage.SketchZoomImageView
import kotlinx.coroutines.flow.StateFlow
import org.koin.mp.KoinPlatform

class MyZoomImageView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyle: Int = 0
) : SketchZoomImageView(context, attrs, defStyle) {

    private val appSettings: AppSettings = KoinPlatform.getKoin().get()

    init {
        updateImageOptions {
            memoryCachePolicy(appSettings.memoryCache.value)
            resultCachePolicy(appSettings.resultCache.value)
            downloadCachePolicy(appSettings.downloadCache.value)
            colorType(appSettings.colorType.value)
            if (VERSION.SDK_INT >= VERSION_CODES.O) {
                colorSpace(appSettings.colorSpace.value)
            }
            @Suppress("DEPRECATION")
            preferQualityOverSpeed(VERSION.SDK_INT <= VERSION_CODES.M && appSettings.preferQualityOverSpeed.value)
            repeatCount(appSettings.repeatCount.value)
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        listenSettings(appSettings.memoryCache) { cachePolicy ->
            memoryCachePolicy(cachePolicy)
        }
        listenSettings(appSettings.resultCache) { cachePolicy ->
            resultCachePolicy(cachePolicy)
        }
        listenSettings(appSettings.downloadCache) { cachePolicy ->
            downloadCachePolicy(cachePolicy)
        }

        listenSettings(appSettings.colorType) { colorType ->
            colorType(colorType)
        }
        if (VERSION.SDK_INT >= VERSION_CODES.O) {
            listenSettings(appSettings.colorSpace) { colorSpace ->
                colorSpace(colorSpace)
            }
        }
        listenSettings(appSettings.preferQualityOverSpeed) { preferQualityOverSpeed ->
            @Suppress("DEPRECATION")
            preferQualityOverSpeed(VERSION.SDK_INT <= VERSION_CODES.M && preferQualityOverSpeed)
        }
        listenSettings(appSettings.repeatCount) { repeatCount ->
            repeatCount(repeatCount)
        }
    }

    private fun reloadImage() {
        val request = SketchUtils.getRequest(this)
        if (request != null) {
            loadImage(request.uri.toString())
        }
    }

    private fun <T> listenSettings(
        state: StateFlow<T>,
        configBlock: (ImageOptions.Builder.(T) -> Unit)
    ) {
        state.ignoreFirst()
            .collectWithLifecycle(lifecycleOwner) { newValue ->
                updateImageOptions {
                    configBlock(newValue)
                }
                reloadImage()
            }
    }
}