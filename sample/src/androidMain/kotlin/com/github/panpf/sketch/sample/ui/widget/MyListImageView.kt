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
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.util.AttributeSet
import com.github.panpf.sketch.ability.removeDataFromLogo
import com.github.panpf.sketch.ability.removeMimeTypeLogo
import com.github.panpf.sketch.ability.removeProgressIndicator
import com.github.panpf.sketch.ability.setClickIgnoreSaveCellularTrafficEnabled
import com.github.panpf.sketch.ability.showDataFromLogo
import com.github.panpf.sketch.ability.showMimeTypeLogoWithDrawable
import com.github.panpf.sketch.ability.showProgressIndicator
import com.github.panpf.sketch.loadImage
import com.github.panpf.sketch.request.ImageOptions
import com.github.panpf.sketch.request.bitmapConfig
import com.github.panpf.sketch.request.colorSpace
import com.github.panpf.sketch.request.pauseLoadWhenScrolling
import com.github.panpf.sketch.request.preferQualityOverSpeed
import com.github.panpf.sketch.request.saveCellularTraffic
import com.github.panpf.sketch.request.updateImageOptions
import com.github.panpf.sketch.sample.R
import com.github.panpf.sketch.sample.appSettings
import com.github.panpf.sketch.sample.buildScale
import com.github.panpf.sketch.sample.ui.util.createThemeSectorProgressDrawable
import com.github.panpf.sketch.sample.ui.util.lifecycleOwner
import com.github.panpf.sketch.sample.util.collectWithLifecycle
import com.github.panpf.sketch.sample.util.ignoreFirst
import com.github.panpf.sketch.state.IconAnimatableDrawableStateImage
import com.github.panpf.sketch.state.IconDrawableStateImage
import com.github.panpf.sketch.state.saveCellularTrafficError
import com.github.panpf.sketch.util.SketchUtils
import com.github.panpf.tools4a.dimen.ktx.dp2px
import kotlinx.coroutines.flow.StateFlow

class MyListImageView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyle: Int = 0
) : MyImageView(context, attrs, defStyle) {

    private val mimeTypeLogoMap by lazy {
        val newLogoDrawable: (String) -> Drawable = {
            TextDrawable.builder()
                .beginConfig()
                .width(((it.length + 1) * 6.5f).dp2px)
                .height(16.dp2px)
                .fontSize(9.dp2px)
                .bold()
                .textColor(Color.WHITE)
                .endConfig()
                .buildRoundRect(it, Color.parseColor("#88000000"), 10.dp2px)
        }
        mapOf(
            "image/gif" to newLogoDrawable("GIF"),
            "image/png" to newLogoDrawable("PNG"),
            "image/jpeg" to newLogoDrawable("JPEG"),
            "image/webp" to newLogoDrawable("WEBP"),
            "image/bmp" to newLogoDrawable("BMP"),
            "image/svg+xml" to newLogoDrawable("SVG"),
            "image/heic" to newLogoDrawable("HEIC"),
            "image/heif" to newLogoDrawable("HEIF"),
            "video/mp4" to newLogoDrawable("MP4"),
        )
    }

    init {
        // When the first request is executed, it has not yet reached onAttachedToWindow,
        // so it must be initialized here in advance to ensure that the first request can also display progress.
        setShowProgressIndicator(show = appSettings.showProgressIndicatorInList.value)

        updateImageOptions {
            placeholder(
                IconDrawableStateImage(
                    icon = R.drawable.ic_image_outline,
                    background = R.color.placeholder_bg,
                )
            )
            error(
                defaultImage = IconDrawableStateImage(
                    icon = R.drawable.ic_image_broken_outline,
                    background = R.color.placeholder_bg
                )
            ) {
                saveCellularTrafficError(
                    IconDrawableStateImage(
                        icon = R.drawable.ic_signal_cellular,
                        background = R.color.placeholder_bg
                    )
                )
            }
            crossfade()
            resizeOnDraw()
            sizeMultiplier(2f)  // To get a clearer thumbnail

            memoryCachePolicy(appSettings.memoryCache.value)
            resultCachePolicy(appSettings.resultCache.value)
            downloadCachePolicy(appSettings.downloadCache.value)

            precision(appSettings.precision.value)
            scale(appSettings.scale.value)

            disallowAnimatedImage(appSettings.disallowAnimatedImageInList.value)

            pauseLoadWhenScrolling(appSettings.pauseLoadWhenScrollInList.value)
            saveCellularTraffic(appSettings.saveCellularTrafficInList.value)

            bitmapConfig(appSettings.bitmapQuality.value)
            if (VERSION.SDK_INT >= VERSION_CODES.O) {
                colorSpace(appSettings.colorSpace.value)
            }
            @Suppress("DEPRECATION")
            preferQualityOverSpeed(VERSION.SDK_INT <= VERSION_CODES.M && appSettings.inPreferQualityOverSpeed.value)
        }

        setClickIgnoreSaveCellularTrafficEnabled(true)
    }

    fun setAnimatedPlaceholder(animatedPlaceholder: Boolean) {
        updateImageOptions {
            if (animatedPlaceholder) {
                placeholder(
                    IconAnimatableDrawableStateImage(
                        icon = R.drawable.ic_placeholder_eclipse_animated,
                        background = R.color.placeholder_bg
                    )
                )
            } else {
                placeholder(
                    IconDrawableStateImage(
                        icon = R.drawable.ic_image_outline,
                        background = R.color.placeholder_bg,
                    )
                )
            }
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        val appSettings = appSettings

        appSettings.showProgressIndicatorInList.collectWithLifecycle(lifecycleOwner) { show ->
            setShowProgressIndicator(show = show)
        }
        appSettings.showMimeTypeLogoInList.collectWithLifecycle(lifecycleOwner) { show ->
            setShowMimeTypeLogo(show = show)
        }
        appSettings.showDataFromLogoInList.collectWithLifecycle(lifecycleOwner) { show ->
            setShowDataFromLogo(show = show)
        }

        listenSettings(appSettings.memoryCache) { cachePolicy ->
            memoryCachePolicy(cachePolicy)
        }
        listenSettings(appSettings.resultCache) { cachePolicy ->
            resultCachePolicy(cachePolicy)
        }
        listenSettings(appSettings.downloadCache) { cachePolicy ->
            downloadCachePolicy(cachePolicy)
        }

        listenSettings(appSettings.precision) { precision ->
            precision(precision)
        }

        listenSettings(appSettings.scaleName) {
            scale(
                buildScale(
                    appSettings.scaleName.value,
                    appSettings.longImageScale.value,
                    appSettings.otherImageScale.value
                )
            )
        }
        listenSettings(appSettings.longImageScale) {
            scale(
                buildScale(
                    appSettings.scaleName.value,
                    appSettings.longImageScale.value,
                    appSettings.otherImageScale.value
                )
            )
        }
        listenSettings(appSettings.otherImageScale) {
            scale(
                buildScale(
                    appSettings.scaleName.value,
                    appSettings.longImageScale.value,
                    appSettings.otherImageScale.value
                )
            )
        }
//        listenSettings(appSettings.scale) { scale ->
//            scale(scale)
//        }

        listenSettings(appSettings.disallowAnimatedImageInList) { disallowAnimatedImage ->
            disallowAnimatedImage(disallowAnimatedImage)
        }

        listenSettings(appSettings.pauseLoadWhenScrollInList) { pauseLoadWhenScrolling ->
            pauseLoadWhenScrolling(pauseLoadWhenScrolling)
        }
        listenSettings(appSettings.saveCellularTrafficInList) { saveCellularTraffic ->
            saveCellularTraffic(saveCellularTraffic)
        }

        listenSettings(appSettings.bitmapQuality) { bitmapQuality ->
            bitmapConfig(bitmapQuality)
        }
        if (VERSION.SDK_INT >= VERSION_CODES.O) {
            listenSettings(appSettings.colorSpace) { colorSpace ->
                colorSpace(colorSpace)
            }
        }
        listenSettings(appSettings.inPreferQualityOverSpeed) { inPreferQualityOverSpeed ->
            @Suppress("DEPRECATION")
            preferQualityOverSpeed(VERSION.SDK_INT <= VERSION_CODES.M && inPreferQualityOverSpeed)
        }
    }

    private fun setShowProgressIndicator(show: Boolean) {
        if (show) {
            showProgressIndicator(
                createThemeSectorProgressDrawable(
                    context = context,
                    hiddenWhenIndeterminate = true
                )
            )
        } else {
            removeProgressIndicator()
        }
    }

    private fun setShowMimeTypeLogo(show: Boolean) {
        if (show) {
            showMimeTypeLogoWithDrawable(mimeTypeLogoMap, 4.dp2px)
        } else {
            removeMimeTypeLogo()
        }
    }

    private fun setShowDataFromLogo(show: Boolean) {
        if (show) {
            showDataFromLogo()
        } else {
            removeDataFromLogo()
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