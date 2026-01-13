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

@file:OptIn(InternalCoroutinesApi::class)

package com.github.panpf.sketch.decode

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import androidx.annotation.WorkerThread
import com.github.panpf.sketch.ComponentRegistry
import com.github.panpf.sketch.asImage
import com.github.panpf.sketch.decode.internal.ImageFormat
import com.github.panpf.sketch.decode.internal.calculateSampleSize
import com.github.panpf.sketch.decode.internal.checkImageInfo
import com.github.panpf.sketch.decode.internal.createInSampledTransformed
import com.github.panpf.sketch.decode.internal.isGif
import com.github.panpf.sketch.drawable.AnimatableDrawable
import com.github.panpf.sketch.drawable.GifDrawableWrapperDrawable
import com.github.panpf.sketch.fetch.FetchResult
import com.github.panpf.sketch.request.ImageData
import com.github.panpf.sketch.request.RequestContext
import com.github.panpf.sketch.request.animatedTransformation
import com.github.panpf.sketch.request.animationEndCallback
import com.github.panpf.sketch.request.animationStartCallback
import com.github.panpf.sketch.request.disallowAnimatedImage
import com.github.panpf.sketch.request.repeatCount
import com.github.panpf.sketch.resize.isSmallerSizeMode
import com.github.panpf.sketch.source.DataSource
import com.github.panpf.sketch.util.Size
import com.github.panpf.sketch.util.animatable2CompatCallbackOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.internal.SynchronizedObject
import kotlinx.coroutines.launch
import pl.droidsonroids.gif.GifInfoHandleHelper
import pl.droidsonroids.gif.GifOptions
import pl.droidsonroids.gif.transforms.Transform

/**
 * Adds gif support by koral GifDrawable
 *
 * @see com.github.panpf.sketch.animated.gif.koral.test.decode.KoralGifDecoderTest.testSupportKoralGif
 */
fun ComponentRegistry.Builder.supportKoralGif(): ComponentRegistry.Builder = apply {
    add(KoralGifDecoder.Factory())
}

/**
 * Decoding GIF using koral GifDrawable
 *
 * The following decoding related properties are supported:
 *
 * * sizeResolver: Only sampleSize
 * * sizeMultiplier
 * * precisionDecider: Only LESS_PIXELS and SMALLER_SIZE is supported
 * * disallowAnimatedImage
 * * repeatCount
 * * animatedTransformation
 * * onAnimationStart
 * * onAnimationEnd
 *
 * The following decoding related properties are not supported:
 *
 * * scaleDecider
 * * colorType
 * * colorSpace
 *
 * @see com.github.panpf.sketch.animated.gif.koral.test.decode.KoralGifDecoderTest
 */
class KoralGifDecoder(
    private val requestContext: RequestContext,
    private val dataSource: DataSource,
) : Decoder {

    private val gifInfoHandleHelper by lazy {
        GifInfoHandleHelper(requestContext.sketch, dataSource)
    }

    private var _imageInfo: ImageInfo? = null
    private val imageInfoLock = SynchronizedObject()

    override val imageInfo: ImageInfo
        get() {
            synchronized(imageInfoLock) {
                val imageInfo = _imageInfo
                if (imageInfo != null) return imageInfo
                return ImageInfo(
                    size = Size(
                        width = gifInfoHandleHelper.width,
                        height = gifInfoHandleHelper.height
                    ),
                    mimeType = ImageFormat.GIF.mimeType,
                ).apply {
                    checkImageInfo(this)
                    _imageInfo = this
                }
            }
        }

    @WorkerThread
    override fun decode(): ImageData {
        val imageInfo = imageInfo
        val resize = requestContext.computeResize(imageInfo.size)
        val inSampleSize = calculateSampleSize(
            imageSize = imageInfo.size,
            targetSize = resize.size,
            smallerSizeMode = resize.precision.isSmallerSizeMode(),
        )
        gifInfoHandleHelper.setOptions(GifOptions().apply {
            setInSampleSize(inSampleSize)
        })
        val request = requestContext.request
        val gifDrawable = gifInfoHandleHelper.createGifDrawable().apply {
            loopCount = request.repeatCount?.let { it + 1 }?.coerceAtLeast(0) ?: 0

            // Set the animated transformation to be applied on each frame.
            val transformation = request.animatedTransformation
            if (transformation != null) {
                transform = object : Transform {
                    private val srcRect = Rect()
                    private val dstRect = Rect()
                    private val bounds1 = com.github.panpf.sketch.util.Rect()
                    override fun onBoundsChange(bounds: Rect?) {
                        if (bounds != null) {
                            dstRect.set(bounds)
                            bounds1.set(bounds.left, bounds.top, bounds.right, bounds.bottom)
                        }
                    }

                    override fun onDraw(canvas: Canvas, paint: Paint?, buffer: Bitmap?) {
                        if (buffer != null) {
                            srcRect.set(0, 0, buffer.width, buffer.height)
                            canvas.drawBitmap(buffer, srcRect, dstRect, paint)
                            transformation.transform(canvas, bounds1)
                        }
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
                    // Will be executed before DecoderInterceptor.intercept() return
                    @Suppress("OPT_IN_USAGE")
                    GlobalScope.launch(Dispatchers.Main) {
                        registerAnimationCallback(animatable2CompatCallbackOf(onStart, onEnd))
                    }
                }
            }

        return ImageData(
            image = animatableDrawable.asImage(),
            imageInfo = imageInfo,
            dataFrom = dataSource.dataFrom,
            resize = resize,
            transformeds = transformeds,
            extras = null,
        )
    }

    class Factory : Decoder.Factory {

        override val key: String = "KoralGifDecoder"

        override fun create(
            requestContext: RequestContext,
            fetchResult: FetchResult
        ): Decoder? {
            if (requestContext.request.disallowAnimatedImage == true) return null
            if (!isApplicable(fetchResult)) return null
            return KoralGifDecoder(requestContext, fetchResult.dataSource)
        }

        private fun isApplicable(fetchResult: FetchResult): Boolean {
            return fetchResult.headerBytes.isGif()
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            return other != null && this::class == other::class
        }

        override fun hashCode(): Int {
            return this::class.hashCode()
        }

        override fun toString(): String = "KoralGifDecoder"
    }
}