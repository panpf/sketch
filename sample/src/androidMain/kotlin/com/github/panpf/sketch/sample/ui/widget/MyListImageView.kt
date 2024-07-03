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
package com.github.panpf.sketch.sample.ui.widget

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import com.github.panpf.sketch.ability.removeDataFromLogo
import com.github.panpf.sketch.ability.removeMimeTypeLogo
import com.github.panpf.sketch.ability.removeProgressIndicator
import com.github.panpf.sketch.ability.showDataFromLogo
import com.github.panpf.sketch.ability.showMimeTypeLogoWithDrawable
import com.github.panpf.sketch.ability.showProgressIndicator
import com.github.panpf.sketch.sample.appSettings
import com.github.panpf.sketch.sample.ui.util.createThemeSectorProgressDrawable
import com.github.panpf.sketch.sample.ui.util.lifecycleOwner
import com.github.panpf.sketch.sample.util.collectWithLifecycle
import com.github.panpf.tools4a.dimen.ktx.dp2px

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
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        appSettings.showProgressIndicatorInList.collectWithLifecycle(lifecycleOwner) { show ->
            setShowProgressIndicator(show = show)
        }
        appSettings.showMimeTypeLogoInList.collectWithLifecycle(lifecycleOwner) { show ->
            setShowMimeTypeLogo(show = show)
        }
        appSettings.showDataFromLogoInList.collectWithLifecycle(lifecycleOwner) { show ->
            setShowDataFromLogo(show = show)
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
}