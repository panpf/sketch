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
import androidx.core.view.ViewCompat
import com.github.panpf.sketch.cache.CachePolicy
import com.github.panpf.sketch.displayResult
import com.github.panpf.sketch.request.DisplayResult
import com.github.panpf.sketch.sketch
import com.github.panpf.sketch.stateimage.internal.SketchStateDrawable
import com.github.panpf.sketch.util.SketchUtils
import com.github.panpf.sketch.util.findLeafChildDrawable
import com.github.panpf.sketch.util.findLeafSketchDrawable
import com.github.panpf.zoomimage.internal.AbsStateZoomImageView
import com.github.panpf.zoomimage.sketch.SketchImageSource
import com.github.panpf.zoomimage.sketch.SketchTileBitmapCache
import com.github.panpf.zoomimage.sketch.SketchTileBitmapPool
import com.github.panpf.zoomimage.subsampling.ImageSource

/**
 * An ImageView that integrates the Sketch image loading framework that zoom and subsampling huge images
 *
 * Example usages:
 *
 * ```kotlin
 * val sketchZoomImageView = SketchZoomImageView(context)
 * sketchZoomImageView.displayImage("http://sample.com/sample.jpg") {
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
        _subsamplingEngine?.tileBitmapPoolState?.value =
            SketchTileBitmapPool(context.sketch, "SketchZoomImageView")
        _subsamplingEngine?.tileBitmapCacheState?.value =
            SketchTileBitmapCache(context.sketch, "SketchZoomImageView")
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
            val result = displayResult
            if (result == null) {
                logger.d { "SketchZoomImageView. Can't use Subsampling, result is null" }
                return@post
            }
            if (result !is DisplayResult.Success) {
                logger.d { "SketchZoomImageView. Can't use Subsampling, result is not Success" }
                return@post
            }
            _subsamplingEngine?.disabledTileBitmapCacheState?.value =
                isDisableMemoryCache(result.drawable)
            _subsamplingEngine?.disabledTileBitmapReuseState?.value =
                isDisallowReuseBitmap(result.drawable)
            _subsamplingEngine?.ignoreExifOrientationState?.value =
                isIgnoreExifOrientation(result.drawable)
            _subsamplingEngine?.setImageSource(newImageSource(result.drawable))
        }
    }

    private fun isDisableMemoryCache(drawable: Drawable?): Boolean {
        val sketchDrawable = drawable?.findLeafSketchDrawable()
        val requestKey = sketchDrawable?.requestKey
        val displayResult = SketchUtils.getResult(this)
        return displayResult != null
                && displayResult is DisplayResult.Success
                && displayResult.requestKey == requestKey
                && displayResult.request.memoryCachePolicy != CachePolicy.ENABLED
    }

    private fun isDisallowReuseBitmap(drawable: Drawable?): Boolean {
        val sketchDrawable = drawable?.findLeafSketchDrawable()
        val requestKey = sketchDrawable?.requestKey
        val displayResult = SketchUtils.getResult(this)
        return displayResult != null
                && displayResult is DisplayResult.Success
                && displayResult.requestKey == requestKey
                && displayResult.request.disallowReuseBitmap
    }

    private fun isIgnoreExifOrientation(drawable: Drawable?): Boolean {
        val sketchDrawable = drawable?.findLeafSketchDrawable()
        val requestKey = sketchDrawable?.requestKey
        val displayResult = SketchUtils.getResult(this)
        return displayResult != null
                && displayResult is DisplayResult.Success
                && displayResult.requestKey == requestKey
                && displayResult.request.ignoreExifOrientation
    }

    private fun newImageSource(drawable: Drawable?): ImageSource? {
        drawable ?: return null
        if (drawable.findLeafChildDrawable() is SketchStateDrawable) {
            logger.d { "SketchZoomImageView. Can't use Subsampling, drawable is SketchStateDrawable" }
            return null
        }
        val sketchDrawable = drawable.findLeafSketchDrawable()
        if (sketchDrawable == null) {
            logger.d { "SketchZoomImageView. Can't use Subsampling, drawable is not SketchDrawable" }
            return null
        }
        if (sketchDrawable is Animatable) {
            logger.d { "SketchZoomImageView. Can't use Subsampling, drawable is Animatable" }
            return null
        }
        return SketchImageSource(
            context = context,
            sketch = context.sketch,
            imageUri = sketchDrawable.imageUri,
        )
    }
}