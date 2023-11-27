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
package com.github.panpf.sketch.sample.widget

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import androidx.lifecycle.lifecycleScope
import com.github.panpf.sketch.sample.prefsService
import com.github.panpf.sketch.sample.util.lifecycleOwner
import com.github.panpf.sketch.viewability.removeDataFromLogo
import com.github.panpf.sketch.viewability.removeMimeTypeLogo
import com.github.panpf.sketch.viewability.removeProgressIndicator
import com.github.panpf.sketch.viewability.showDataFromLogo
import com.github.panpf.sketch.viewability.showMaskProgressIndicator
import com.github.panpf.sketch.viewability.showMimeTypeLogoWithDrawable
import com.github.panpf.tools4a.dimen.ktx.dp2px
import kotlinx.coroutines.launch

class MyListImageView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyle: Int = 0
) : MyImageView(context, attrs, defStyle) {

    private val mimeTypeLogoMap by lazy {
        val newLogoDrawable: (String) -> Drawable = {
            TextDrawable.builder()
                .beginConfig()
                .width(((it.length + 1) * 6).dp2px)
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
        )
    }

    override fun onAttachedToWindow() {
        /* Must be in onAttachedToWindow */
        /* Must be before super.onAttachedToWindow() */
        lifecycleOwner.lifecycleScope.launch {
            prefsService.showProgressIndicatorInList.stateFlow.collect {
                setShowProgressIndicator(it)
            }
        }
        lifecycleOwner.lifecycleScope.launch {
            prefsService.showMimeTypeLogoInLIst.stateFlow.collect {
                setShowMimeTypeLogo(it)
            }
        }
        lifecycleOwner.lifecycleScope.launch {
            prefsService.showDataFromLogo.stateFlow.collect {
                setShowDataFromLogo(it)
            }
        }
        super.onAttachedToWindow()
    }

    private fun setShowProgressIndicator(show: Boolean) {
        if (show) {
            showMaskProgressIndicator()
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