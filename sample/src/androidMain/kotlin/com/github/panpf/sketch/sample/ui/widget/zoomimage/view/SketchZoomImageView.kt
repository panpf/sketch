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
import android.graphics.drawable.Animatable
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.cache.CachePolicy
import com.github.panpf.sketch.imageResult
import com.github.panpf.sketch.request.ImageResult
import com.github.panpf.sketch.util.SketchUtils
import com.github.panpf.sketch.util.findLeafChildDrawable
import com.github.panpf.zoomimage.internal.AbsStateZoomImageView
import com.github.panpf.zoomimage.sketch.SketchImageSource
import com.github.panpf.zoomimage.sketch.SketchTileBitmapCache

/**
 * An ImageView that integrates the Sketch image loading framework that zoom and subsampling huge images
 *
 * Example usages:
 *
 * ```kotlin
 * val sketchZoomImageView = SketchZoomImageView(context)
 * sketchZoomImageView.loadImage("http://sample.com/huge_world.jpeg") {
 *     placeholder(R.drawable.placeholder)
 *     crossfade()
 * }
 * ```
 */
open class SketchZoomImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : AbsStateZoomImageView(context, attrs, defStyle) {

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (drawable != null) {
            resetImageSource()
        }
    }

    override fun onDrawableChanged(oldDrawable: Drawable?, newDrawable: Drawable?) {
        super.onDrawableChanged(oldDrawable, newDrawable)
        if (isAttachedToWindow) {
            resetImageSource()
        }
    }

    private fun resetImageSource() {
        post {
            if (!isAttachedToWindow) {
                return@post
            }
            val result: ImageResult? = imageResult
            if (result == null) {
                logger.d { "SketchZoomImageView. Can't use Subsampling, result is null" }
                return@post
            }
            if (result !is ImageResult.Success) {
                logger.d { "SketchZoomImageView. Can't use Subsampling, result is not Success" }
                return@post
            }
            val sketch = SketchUtils.getSketch(this)
            if (sketch == null) {
                logger.d { "SketchZoomImageView. Can't use Subsampling, sketch is null" }
                return@post
            }

            _subsamplingEngine?.apply {
                if (tileBitmapCacheState.value == null) {
                    tileBitmapCacheState.value = SketchTileBitmapCache(sketch)
                }
                disabledTileBitmapCacheState.value =
                    result.request.memoryCachePolicy != CachePolicy.ENABLED
                setImageSource(newImageSource(sketch, result))
            }
        }
    }

    private fun newImageSource(sketch: Sketch, result: ImageResult): SketchImageSource.Factory? {
        val drawable = drawable
        if (drawable == null) {
            logger.d { "SketchZoomImageView. Can't use Subsampling, drawable is null" }
            return null
        }
        val leafDrawable = drawable.findLeafChildDrawable() ?: drawable
        if (leafDrawable is Animatable) {
            logger.d { "SketchZoomImageView. Can't use Subsampling, drawable is Animatable" }
            return null
        }
        return SketchImageSource.Factory(context, sketch, result.request.uri)
    }

    override fun getLogTag(): String? = "SketchZoomImageView"
}