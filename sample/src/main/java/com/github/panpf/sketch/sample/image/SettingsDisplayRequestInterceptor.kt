/*
 * Copyright (C) 2022 panpf <panpfpanpf@outlook.com>
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
package com.github.panpf.sketch.sample.image

import android.graphics.ColorSpace
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import androidx.annotation.MainThread
import com.github.panpf.sketch.cache.CachePolicy.DISABLED
import com.github.panpf.sketch.cache.CachePolicy.ENABLED
import com.github.panpf.sketch.compose.internal.AsyncImageScaleDecider
import com.github.panpf.sketch.decode.BitmapConfig
import com.github.panpf.sketch.request.DisplayRequest
import com.github.panpf.sketch.request.ImageData
import com.github.panpf.sketch.request.RequestInterceptor
import com.github.panpf.sketch.request.RequestInterceptor.Chain
import com.github.panpf.sketch.request.get
import com.github.panpf.sketch.request.pauseLoadWhenScrolling
import com.github.panpf.sketch.request.saveCellularTraffic
import com.github.panpf.sketch.resize.FixedPrecisionDecider
import com.github.panpf.sketch.resize.FixedScaleDecider
import com.github.panpf.sketch.resize.LongImageClipPrecisionDecider
import com.github.panpf.sketch.resize.LongImageScaleDecider
import com.github.panpf.sketch.resize.Precision
import com.github.panpf.sketch.resize.Precision.SAME_ASPECT_RATIO
import com.github.panpf.sketch.resize.Scale
import com.github.panpf.sketch.sample.prefsService
import com.github.panpf.sketch.sample.image.ImageType.LIST
import com.github.panpf.sketch.sample.widget.MyListImageView
import com.github.panpf.sketch.target.ViewDisplayTarget

private const val APPLY_SETTINGS_KEY = "app#imageType"

enum class ImageType {
    LIST, DETAIL
}

fun DisplayRequest.Builder.setApplySettings(imageType: ImageType): DisplayRequest.Builder = apply {
    setParameter(APPLY_SETTINGS_KEY, imageType.name)
}

class SettingsDisplayRequestInterceptor : RequestInterceptor {

    override val key: String? = null

    @MainThread
    override suspend fun intercept(chain: Chain): ImageData {
        val request = chain.request
        if (request !is DisplayRequest) {
            return chain.proceed(request)
        }
        val imageType =
            request.parameters?.get(APPLY_SETTINGS_KEY)?.let { ImageType.valueOf(it.toString()) }
                ?: return chain.proceed(request)

        val newRequest = request.newDisplayRequest {
            val prefsService = request.context.prefsService

            if (imageType == LIST) {
                if (request.definedOptions.resizePrecisionDecider == null) {
                    resizePrecision(
                        when (val value = prefsService.resizePrecision.value) {
                            "LongImageClipMode" -> LongImageClipPrecisionDecider(precision = SAME_ASPECT_RATIO)
                            else -> FixedPrecisionDecider(Precision.valueOf(value))
                        }
                    )
                }
                if (request.definedOptions.resizeScaleDecider == null || request.definedOptions.resizeScaleDecider is AsyncImageScaleDecider) {
                    resizeScale(
                        when (val value = prefsService.resizeScale.value) {
                            "LongImageMode" -> LongImageScaleDecider(
                                longImage = Scale.valueOf(prefsService.longImageResizeScale.value),
                                otherImage = Scale.valueOf(prefsService.otherImageResizeScale.value)
                            )
                            else -> FixedScaleDecider(Scale.valueOf(value))
                        }
                    )
                }
            }

            if (request.definedOptions.memoryCachePolicy == null) {
                if (prefsService.disabledMemoryCache.value) {
                    memoryCachePolicy(DISABLED)
                } else {
                    memoryCachePolicy(ENABLED)
                }
            }
            if (request.definedOptions.downloadCachePolicy == null) {
                if (prefsService.disabledDownloadCache.value) {
                    downloadCachePolicy(DISABLED)
                } else {
                    downloadCachePolicy(ENABLED)
                }
            }
            if (request.definedOptions.resultCachePolicy == null) {
                if (prefsService.disabledResultCache.value) {
                    resultCachePolicy(DISABLED)
                } else {
                    resultCachePolicy(ENABLED)
                }
            }
            if (request.definedOptions.disallowReuseBitmap == null) {
                disallowReuseBitmap(prefsService.disallowReuseBitmap.value)
            }
            if (request.definedOptions.ignoreExifOrientation == null) {
                ignoreExifOrientation(prefsService.ignoreExifOrientation.value)
            }
            @Suppress("DEPRECATION")
            if (request.definedOptions.preferQualityOverSpeed == null) {
                preferQualityOverSpeed(VERSION.SDK_INT < VERSION_CODES.N && prefsService.inPreferQualityOverSpeed.value)
            }
            if (request.definedOptions.bitmapConfig == null) {
                when (prefsService.bitmapQuality.value) {
                    "LOW" -> bitmapConfig(BitmapConfig.LowQuality)
                    "HIGH" -> bitmapConfig(BitmapConfig.HighQuality)
                    else -> bitmapConfig(null)
                }
            }
            if (VERSION.SDK_INT >= VERSION_CODES.O && request.definedOptions.colorSpace == null) {
                when (val value = prefsService.colorSpace.value) {
                    "Default" -> {
                        colorSpace(null)
                    }
                    else -> {
                        colorSpace(ColorSpace.get(ColorSpace.Named.valueOf(value)))
                    }
                }
            }
            val target = chain.request.target
            if (target is ViewDisplayTarget<*>) {
                val view = target.view
                if (view is MyListImageView) {
                    if (request.definedOptions.disallowAnimatedImage == null) {
                        disallowAnimatedImage(prefsService.disallowAnimatedImageInList.value)
                    }
                    pauseLoadWhenScrolling(prefsService.pauseLoadWhenScrollInList.value)
                    saveCellularTraffic(prefsService.saveCellularTrafficInList.value)
                }
            }
        }
        return chain.proceed(newRequest)
    }

    override fun toString(): String = "SettingsDisplayRequestInterceptor"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        return true
    }

    override fun hashCode(): Int {
        return javaClass.hashCode()
    }
}