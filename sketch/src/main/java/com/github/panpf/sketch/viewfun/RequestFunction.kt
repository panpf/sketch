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

import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import com.github.panpf.sketch.SketchView
import com.github.panpf.sketch.drawable.SketchDrawable
import com.github.panpf.sketch.drawable.SketchGifDrawable
import com.github.panpf.sketch.drawable.SketchLoadingDrawable
import com.github.panpf.sketch.drawable.SketchRefDrawable
import com.github.panpf.sketch.request.CancelCause
import com.github.panpf.sketch.request.DisplayCache
import com.github.panpf.sketch.request.DisplayOptions
import com.github.panpf.sketch.util.SketchUtils

/**
 * 请求基本功能，更新图片显示引用计数和在onDetachedFromWindow的时候取消请求并清空图片
 */
class RequestFunction(private val sketchView: SketchView) : ViewFunction() {

    val displayOptions = DisplayOptions()
    var displayCache: DisplayCache? = null
    private var isOldDrawableFromSketch = false
    private var isNewDrawableFromSketch = false

    override fun onDetachedFromWindow(): Boolean {
        // 主动取消请求
        val potentialRequest = SketchUtils.findDisplayRequest(sketchView)
        if (potentialRequest != null && !potentialRequest.isFinished) {
            potentialRequest.cancel(CancelCause.ON_DETACHED_FROM_WINDOW)
        }
        // todo 尝试不再主动清空图片

        // 如果当前图片是来自Sketch，那么就有可能在这里被主动回收，因此要主动设置ImageView的drawable为null
        val oldDrawable = sketchView.getDrawable()
        return notifyDrawable("onDetachedFromWindow", oldDrawable, false)
    }

    override fun onDrawableChanged(
        callPosition: String,
        oldDrawable: Drawable?,
        newDrawable: Drawable?
    ): Boolean {
        // 当Drawable改变的时候新Drawable的显示引用计数加1，旧Drawable的显示引用计数减1，一定要先处理newDrawable
        isNewDrawableFromSketch = notifyDrawable("$callPosition:newDrawable", newDrawable, true)
        isOldDrawableFromSketch = notifyDrawable("$callPosition:oldDrawable", oldDrawable, false)

        // 如果新Drawable不是来自Sketch，那么就要清空显示参数，防止被RecyclerCompatFunction在onAttachedToWindow的时候错误的恢复成上一张图片
        if (!isNewDrawableFromSketch) {
            displayCache = null
        }
        return false
    }

    fun clean() {
        if (displayCache != null) {
            displayCache!!.uri = null
            displayCache!!.options.reset()
        }
    }

    companion object {
        /**
         * 修改Drawable显示状态
         *
         * @param callingStation 调用位置
         * @param drawable       Drawable
         * @param isDisplayed    是否已显示
         * @return true：drawable或其子Drawable是SketchDrawable
         */
        private fun notifyDrawable(
            callingStation: String,
            drawable: Drawable?,
            isDisplayed: Boolean
        ): Boolean {
            if (drawable == null) {
                return false
            }
            var isSketchDrawable = false
            if (drawable is LayerDrawable) {
                var i = 0
                val z = drawable.numberOfLayers
                while (i < z) {
                    isSketchDrawable = isSketchDrawable or notifyDrawable(
                        callingStation,
                        drawable.getDrawable(i),
                        isDisplayed
                    )
                    i++
                }
            } else {
                if (!isDisplayed && drawable is SketchLoadingDrawable) {
                    val displayRequest = drawable.request
                    if (displayRequest != null && !displayRequest.isFinished) {
                        displayRequest.cancel(CancelCause.BE_REPLACED_ON_SET_DRAWABLE)
                    }
                }
                if (drawable is SketchRefDrawable) {
                    (drawable as SketchRefDrawable).setIsDisplayed(callingStation, isDisplayed)
                } else if (drawable is SketchGifDrawable) {
                    if (!isDisplayed) {
                        (drawable as SketchGifDrawable).recycle()
                    }
                }
                isSketchDrawable = drawable is SketchDrawable
            }
            return isSketchDrawable
        }
    }
}