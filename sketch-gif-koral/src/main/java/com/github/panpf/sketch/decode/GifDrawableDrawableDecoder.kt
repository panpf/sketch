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
package com.github.panpf.sketch.decode

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import androidx.annotation.WorkerThread
import androidx.exifinterface.media.ExifInterface
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.datasource.DataSource
import com.github.panpf.sketch.decode.internal.ImageFormat
import com.github.panpf.sketch.decode.internal.calculateSampleSize
import com.github.panpf.sketch.decode.internal.createInSampledTransformed
import com.github.panpf.sketch.decode.internal.isGif
import com.github.panpf.sketch.drawable.GifDrawableWrapperDrawable
import com.github.panpf.sketch.drawable.SketchAnimatableDrawable
import com.github.panpf.sketch.fetch.FetchResult
import com.github.panpf.sketch.request.ANIMATION_REPEAT_INFINITE
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.animatable2CompatCallbackOf
import com.github.panpf.sketch.request.animatedTransformation
import com.github.panpf.sketch.request.animationEndCallback
import com.github.panpf.sketch.request.animationStartCallback
import com.github.panpf.sketch.request.internal.RequestContext
import com.github.panpf.sketch.request.repeatCount
import com.github.panpf.sketch.util.Size
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import pl.droidsonroids.gif.GifInfoHandleHelper
import pl.droidsonroids.gif.GifOptions
import pl.droidsonroids.gif.transforms.Transform

/**
 * Only the following attributes are supported:
 *
 * resize.size
 *
 * resize.precision: It is always LESS_PIXELS
 *
 * repeatCount
 *
 * animatedTransformation
 *
 * onAnimationStart
 *
 * onAnimationEnd
 */
class GifDrawableDrawableDecoder(
    private val request: ImageRequest,
    private val dataSource: DataSource,
) : DrawableDecoder {

    @WorkerThread
    override suspend fun decode(): DrawableDecodeResult {
        val gifInfoHandleHelper = GifInfoHandleHelper(dataSource)
        val imageWidth = gifInfoHandleHelper.width
        val imageHeight = gifInfoHandleHelper.height
        val resize = request.resize
        var inSampleSize = 1
        if (resize != null) {
            inSampleSize = calculateSampleSize(
                Size(imageWidth, imageHeight),
                Size(resize.width, resize.height)
            )
            gifInfoHandleHelper.setOptions(GifOptions().apply {
                setInSampleSize(inSampleSize)
            })
        }
        val gifDrawable = gifInfoHandleHelper.createGifDrawable().apply {
            loopCount =
                (request.repeatCount ?: ANIMATION_REPEAT_INFINITE).takeIf { it != -1 } ?: 0

            // Set the animated transformation to be applied on each frame.
            val transformation = request.animatedTransformation
            if (transformation != null) {
                transform = object : Transform {
                    override fun onBoundsChange(bounds: Rect?) {
                    }

                    override fun onDraw(canvas: Canvas, paint: Paint?, buffer: Bitmap?) {
                        transformation.transform(canvas)
                    }
                }
            }
        }

        val transformedList =
            if (inSampleSize != 1) listOf(createInSampledTransformed(inSampleSize)) else null
        val imageInfo = ImageInfo(
            imageWidth,
            imageHeight,
            ImageFormat.GIF.mimeType,
            ExifInterface.ORIENTATION_UNDEFINED
        )
        val animatableDrawable = SketchAnimatableDrawable(
            animatableDrawable = GifDrawableWrapperDrawable(gifDrawable),
            imageUri = this.request.uriString,
            requestKey = this.request.key,
            requestCacheKey = this.request.cacheKey,
            imageInfo = imageInfo,
            dataFrom = dataSource.dataFrom,
            transformedList = transformedList,
            extras = null,
        ).apply {
            // Set the start and end animation callbacks if any one is supplied through the request.
            val onStart = request.animationStartCallback
            val onEnd = request.animationEndCallback
            if (onStart != null || onEnd != null) {
                withContext(Dispatchers.Main) {
                    registerAnimationCallback(animatable2CompatCallbackOf(onStart, onEnd))
                }
            }
        }

        return DrawableDecodeResult(
            drawable = animatableDrawable,
            imageInfo = animatableDrawable.imageInfo,
            dataFrom = animatableDrawable.dataFrom,
            transformedList = animatableDrawable.transformedList,
            extras = animatableDrawable.extras,
        )
    }

    class Factory : DrawableDecoder.Factory {

        override fun create(
            sketch: Sketch,
            request: ImageRequest,
            requestContext: RequestContext,
            fetchResult: FetchResult
        ): GifDrawableDrawableDecoder? {
            if (!request.disallowAnimatedImage) {
                val imageFormat = ImageFormat.parseMimeType(fetchResult.mimeType)
                // Some sites disguise the suffix of a GIF file as a JPEG, which must be identified by the file header
                val isGif =
                    if (imageFormat == null) fetchResult.headerBytes.isGif() else imageFormat == ImageFormat.GIF
                if (isGif) {
                    return GifDrawableDrawableDecoder(request, fetchResult.dataSource)
                }
            }
            return null
        }

        override fun toString(): String = "GifDrawableDrawableDecoder"

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false
            return true
        }

        override fun hashCode(): Int {
            return javaClass.hashCode()
        }
    }
}