/*
 * Copyright (C) 2019 panpf <panpfpanpf@outlook.com>
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
package com.github.panpf.sketch.viewfun

import android.graphics.Canvas
import android.graphics.drawable.Drawable
import com.github.panpf.sketch.util.SketchUtils.Companion.isGifImage

/**
 * 显示 gif 标识功能，使用者指定一个小图标，如果当前显示的图片是 gif 图就会在 [android.widget.ImageView] 的右下角显示这个小图标
 */
class ShowGifFlagFunction(private val view: FunctionCallbackView) : ViewFunction() {

    private var gifFlagDrawable: Drawable? = null
    private var gifImage = false
    private var iconDrawLeft = 0f
    private var iconDrawTop = 0f
    private var lastDrawable: Drawable? = null
    private var cacheViewWidth = 0
    private var cacheViewHeight = 0

    override fun onDraw(canvas: Canvas) {
        val gifFlagDrawable = gifFlagDrawable ?: return
        val drawable = view.drawable
        if (drawable !== lastDrawable) {
            gifImage = isGifImage(drawable)
            lastDrawable = drawable
        }
        if (!gifImage) {
            return
        }
        if (cacheViewWidth != view.width || cacheViewHeight != view.height) {
            cacheViewWidth = view.width
            cacheViewHeight = view.height
            iconDrawLeft =
                (view.width - view.paddingRight - gifFlagDrawable.intrinsicWidth).toFloat()
            iconDrawTop =
                (view.height - view.paddingBottom - gifFlagDrawable.intrinsicHeight).toFloat()
        }
        canvas.save()
        canvas.translate(iconDrawLeft, iconDrawTop)
        gifFlagDrawable.draw(canvas)
        canvas.restore()
    }

    fun setGifFlagDrawable(gifFlagDrawable: Drawable): Boolean {
        if (this.gifFlagDrawable === gifFlagDrawable) {
            return false
        }
        gifFlagDrawable.setBounds(
            0,
            0,
            gifFlagDrawable.intrinsicWidth,
            gifFlagDrawable.intrinsicHeight
        )
        this.gifFlagDrawable = gifFlagDrawable
        return true
    }
}