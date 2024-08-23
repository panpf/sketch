/*
 * Copyright (C) 2023 panpf <panpfpanpf@outlook.com>
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

package com.github.panpf.zoomimage

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.request.ImageResult
import com.github.panpf.sketch.util.SketchUtils
import com.github.panpf.zoomimage.sketch.SketchImageSource
import com.github.panpf.zoomimage.sketch.SketchTileBitmapCache
import com.github.panpf.zoomimage.util.Logger
import com.github.panpf.zoomimage.view.sketch.internal.AbsStateZoomImageView

/**
 * An ImageView that integrates the Sketch image loading framework that zoom and subsampling huge images
 *
 * Example usages:
 *
 * ```kotlin
 * val sketchZoomImageView = SketchZoomImageView(context)
 * sketchZoomImageView.loadImage("https://sample.com/sample.jpeg") {
 *     placeholder(R.drawable.placeholder)
 *     crossfade()
 * }
 * ```
 *
 * @see com.github.panpf.zoomimage.view.sketch.core.test.SketchZoomImageViewTest
 */
open class SketchZoomImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : AbsStateZoomImageView(context, attrs, defStyle) {

    private var resetImageSourceOnAttachedToWindow: Boolean = false

    override fun newLogger(): Logger = Logger(tag = "SketchZoomImageView")

    override fun onDrawableChanged(oldDrawable: Drawable?, newDrawable: Drawable?) {
        super.onDrawableChanged(oldDrawable, newDrawable)
        if (isAttachedToWindow) {
            resetImageSource()
        } else {
            resetImageSourceOnAttachedToWindow = true
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (resetImageSourceOnAttachedToWindow) {
            resetImageSourceOnAttachedToWindow = false
            resetImageSource()
        }
    }

    private fun resetImageSource() {
        // You must use post to delay execution because 'SketchUtils.getResult' may not be ready when onDrawableChanged is executed.
        post {
            if (!isAttachedToWindow) {
                resetImageSourceOnAttachedToWindow = true
                return@post
            }
            val sketch = SketchUtils.getSketch(this)
            val result = SketchUtils.getResult(this)
            _subsamplingEngine?.apply {
                if (tileBitmapCacheState.value == null && sketch != null) {
                    tileBitmapCacheState.value = SketchTileBitmapCache(sketch)
                }
                setImageSource(newImageSource(sketch, result))
            }
        }
    }

    private fun newImageSource(
        sketch: Sketch?,
        result: ImageResult?
    ): SketchImageSource.Factory? {
        val drawable = drawable
        if (drawable == null) {
            logger.d { "SketchZoomImageView. Can't use Subsampling, drawable is null" }
            return null
        }
        if (sketch == null) {
            logger.d { "SketchZoomImageView. Can't use Subsampling, sketch is null" }
            return null
        }
        if (result !is ImageResult.Success) {
            logger.d { "SketchZoomImageView. Can't use Subsampling, result is not Success" }
            return null
        }
        return SketchImageSource.Factory(sketch, result.request.uri.toString())
    }
}