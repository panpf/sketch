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
import androidx.core.view.ViewCompat
import com.github.panpf.sketch.cache.CachePolicy
import com.github.panpf.sketch.imageResult
import com.github.panpf.sketch.request.ImageResult
import com.github.panpf.sketch.sketch
import com.github.panpf.zoomimage.internal.AbsStateZoomImageView
import com.github.panpf.sketch.sample.ui.components.zoomimage.core.SketchImageSource
import com.github.panpf.zoomimage.subsampling.ImageSource

/**
 * An ImageView that integrates the Sketch image loading framework that zoom and subsampling huge images
 *
 * Example usages:
 *
 * ```kotlin
 * val sketchZoomImageView = SketchZoomImageView(context)
 * sketchZoomImageView.loadImage("http://sample.com/sample.jpg") {
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

    init {
        _subsamplingEngine?.tileBitmapCacheState?.value =
            createTileBitmapCache(context.sketch, "SketchZoomImageView")
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (drawable != null) {
            resetImageSource()
        }
    }

    override fun onDrawableChanged(oldDrawable: Drawable?, newDrawable: Drawable?) {
        super.onDrawableChanged(oldDrawable, newDrawable)
        if (ViewCompat.isAttachedToWindow(this)) {
            resetImageSource()
        }
    }

    private fun resetImageSource() {
        post {
            if (!ViewCompat.isAttachedToWindow(this)) {
                return@post
            }
            val result = imageResult
            if (result == null) {
                logger.d { "SketchZoomImageView. Can't use Subsampling, result is null" }
                return@post
            }
            if (result !is ImageResult.Success) {
                logger.d { "SketchZoomImageView. Can't use Subsampling, result is not Success" }
                return@post
            }
            _subsamplingEngine?.disabledTileBitmapCacheState?.value =
                result.request.memoryCachePolicy != CachePolicy.ENABLED
            _subsamplingEngine?.setImageSource(newImageSource(result))
        }
    }

    private fun newImageSource(result: ImageResult): ImageSource? {
        drawable ?: return null
        return SketchImageSource(
            context = context,
            sketch = context.sketch,
            imageUri = result.request.uri,
        )
    }
}