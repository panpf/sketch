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

import android.annotation.SuppressLint
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.view.View
import androidx.annotation.ColorInt
import com.github.panpf.sketch.SLog
import com.github.panpf.sketch.decode.ImageAttrs
import com.github.panpf.sketch.request.CancelCause
import com.github.panpf.sketch.request.DisplayCache
import com.github.panpf.sketch.request.ErrorCause
import com.github.panpf.sketch.request.ImageFrom
import com.github.panpf.sketch.shaper.ImageShaper
import com.github.panpf.sketch.uri.UriModel

/**
 * 显示下载进度功能，会在 [android.widget.ImageView] 上面显示一个黑色半透明蒙层显示下载进度，蒙层会随着进度渐渐变小
 */
class ShowDownloadProgressFunction(private val view: FunctionPropertyView) : ViewFunction() {

    private var maskColor = DEFAULT_MASK_COLOR
    private var maskShaper: ImageShaper? = null
    private var maskPaint: Paint? = null
    private var progress = NONE.toFloat()
    private var bounds: Rect? = null

    override fun onReadyDisplay(uri: String): Boolean {
        val uriModel = UriModel.match(view.context, uri)
        val newProgress = if (uriModel != null && uriModel.isFromNet) 0 else NONE.toLong()
        val needRefresh = progress != newProgress.toFloat()
        progress = newProgress.toFloat()
        return needRefresh
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        if (progress == NONE.toFloat()) {
            return
        }
        val shaper = getMaskShaper()
        if (shaper != null) {
            canvas.save()
            try {
                val bounds = bounds ?: Rect().apply {
                    this@ShowDownloadProgressFunction.bounds = this
                }
                bounds.set(
                    view.paddingLeft,
                    view.paddingTop,
                    view.width - view.paddingRight,
                    view.height - view.paddingBottom
                )
                val maskPath = shaper.getPath(bounds)
                canvas.clipPath(maskPath)
            } catch (e: UnsupportedOperationException) {
                SLog.em(
                    NAME,
                    "The current environment doesn't support clipPath has shut down automatically hardware acceleration"
                )
                view.setLayerType(View.LAYER_TYPE_SOFTWARE, null)
                e.printStackTrace()
            }
        }
        val maskPaint = maskPaint ?: Paint().apply {
            color = maskColor
            isAntiAlias = true
            this@ShowDownloadProgressFunction.maskPaint = this
        }
        canvas.drawRect(
            view.paddingLeft.toFloat(),
            view.paddingTop + progress * view.height,
            (view.width - view.paddingLeft - view.paddingRight).toFloat(),
            (view.height - view.paddingTop - view.paddingBottom).toFloat(),
            maskPaint
        )
        if (shaper != null) {
            canvas.restore()
        }
    }

    override fun onUpdateDownloadProgress(totalLength: Int, completedLength: Int): Boolean {
        progress = completedLength.toFloat() / totalLength
        return true
    }

    override fun onDisplayCompleted(
        drawable: Drawable,
        imageFrom: ImageFrom,
        imageAttrs: ImageAttrs
    ): Boolean {
        progress = NONE.toFloat()
        return true
    }

    override fun onDisplayError(errorCause: ErrorCause): Boolean {
        progress = NONE.toFloat()
        return true
    }

    override fun onDisplayCanceled(cancelCause: CancelCause): Boolean {
        progress = NONE.toFloat()
        return false
    }

    fun setMaskColor(@ColorInt maskColor: Int): Boolean {
        if (this.maskColor == maskColor) {
            return false
        }
        this.maskColor = maskColor
        if (maskPaint != null) {
            maskPaint!!.color = maskColor
        }
        return true
    }

    private fun getMaskShaper(): ImageShaper? {
        val maskShaper = maskShaper
        if (maskShaper != null) {
            return maskShaper
        }
        val displayCache: DisplayCache? = view.displayCache
        val shaperFromCacheOptions = displayCache?.options?.shaper
        return shaperFromCacheOptions ?: view.options.shaper
    }

    fun setMaskShaper(maskShaper: ImageShaper?): Boolean {
        if (this.maskShaper === maskShaper) {
            return false
        }
        this.maskShaper = maskShaper
        return true
    }

    companion object {
        const val DEFAULT_MASK_COLOR = 0x22000000
        private const val NAME = "ShowProgressFunction"
        private const val NONE = -1
    }
}