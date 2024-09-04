/*
 * Copyright (C) 2024 panpf <panpfpanpf@outlook.com>
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
import com.github.panpf.sketch.ComponentRegistry
import com.github.panpf.sketch.asSketchImage
import com.github.panpf.sketch.decode.internal.ImageFormat
import com.github.panpf.sketch.decode.internal.calculateSampleSize
import com.github.panpf.sketch.decode.internal.createInSampledTransformed
import com.github.panpf.sketch.decode.internal.isGif
import com.github.panpf.sketch.drawable.AnimatableDrawable
import com.github.panpf.sketch.drawable.GifDrawableWrapperDrawable
import com.github.panpf.sketch.fetch.FetchResult
import com.github.panpf.sketch.request.ANIMATION_REPEAT_INFINITE
import com.github.panpf.sketch.request.RequestContext
import com.github.panpf.sketch.request.animatable2CompatCallbackOf
import com.github.panpf.sketch.request.animatedTransformation
import com.github.panpf.sketch.request.animationEndCallback
import com.github.panpf.sketch.request.animationStartCallback
import com.github.panpf.sketch.request.repeatCount
import com.github.panpf.sketch.resize.isSmallerSizeMode
import com.github.panpf.sketch.source.DataSource
import com.github.panpf.sketch.util.Size
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import pl.droidsonroids.gif.GifInfoHandleHelper
import pl.droidsonroids.gif.GifOptions
import pl.droidsonroids.gif.transforms.Transform

/**
 * Adds gif support by koral GifDrawable
 *
 * @see com.github.panpf.sketch.animated.koralgif.test.decode.GifDrawableDecoderTest.testSupportKoralGif
 */
fun ComponentRegistry.Builder.supportKoralGif(): ComponentRegistry.Builder = apply {
    addDecoder(GifDrawableDecoder.Factory())
}

/**
 * Only the following attributes are supported:
 *
 * * resize.size
 * * resize.precision: It is always LESS_PIXELS
 * * repeatCount
 * * animatedTransformation
 * * onAnimationStart
 * * onAnimationEnd
 *
 * @see com.github.panpf.sketch.animated.koralgif.test.decode.GifDrawableDecoderTest
 */
class GifDrawableDecoder(
    private val requestContext: RequestContext,
    private val dataSource: DataSource,
) : Decoder {

    @WorkerThread
    override suspend fun decode(): Result<DecodeResult> = kotlin.runCatching {
        val request = requestContext.request
        val gifInfoHandleHelper = GifInfoHandleHelper(requestContext.sketch, dataSource)
        val imageWidth = gifInfoHandleHelper.width
        val imageHeight = gifInfoHandleHelper.height
        val size = requestContext.size
        val imageSize = Size(imageWidth, imageHeight)
        val imageInfo = ImageInfo(
            width = imageWidth,
            height = imageHeight,
            mimeType = ImageFormat.GIF.mimeType,
        )
        val resize = requestContext.computeResize(imageInfo.size)
        val inSampleSize = calculateSampleSize(
            imageSize = imageSize,
            targetSize = size,
            smallerSizeMode = resize.precision.isSmallerSizeMode(),
        )
        gifInfoHandleHelper.setOptions(GifOptions().apply {
            setInSampleSize(inSampleSize)
        })
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

        val transformeds =
            if (inSampleSize != 1) listOf(createInSampledTransformed(inSampleSize)) else null
        val animatableDrawable =
            AnimatableDrawable(GifDrawableWrapperDrawable(gifDrawable)).apply {
                // Set the start and end animation callbacks if any one is supplied through the request.
                val onStart = request.animationStartCallback
                val onEnd = request.animationEndCallback
                if (onStart != null || onEnd != null) {
                    withContext(Dispatchers.Main) {
                        registerAnimationCallback(animatable2CompatCallbackOf(onStart, onEnd))
                    }
                }
            }

        DecodeResult(
            image = animatableDrawable.asSketchImage(),
            imageInfo = imageInfo,
            dataFrom = dataSource.dataFrom,
            resize = resize,
            transformeds = transformeds,
            extras = null,
        )
    }

    class Factory : Decoder.Factory {

        override val key: String = "GifDrawableDecoder"

        override fun create(
            requestContext: RequestContext,
            fetchResult: FetchResult
        ): Decoder? {
            if (
                !requestContext.request.disallowAnimatedImage
                && fetchResult.headerBytes.isGif()
            ) {
                return GifDrawableDecoder(requestContext, fetchResult.dataSource)
            }
            return null
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            return other != null && this::class == other::class
        }

        override fun hashCode(): Int {
            return this::class.hashCode()
        }

        override fun toString(): String = "GifDrawableDecoder"
    }
}