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
@file:Suppress("DEPRECATION")

package com.github.panpf.sketch.decode

import android.graphics.Movie
import androidx.annotation.WorkerThread
import com.github.panpf.sketch.ComponentRegistry
import com.github.panpf.sketch.asImage
import com.github.panpf.sketch.decode.internal.ImageFormat
import com.github.panpf.sketch.decode.internal.checkImageInfo
import com.github.panpf.sketch.decode.internal.checkImageSize
import com.github.panpf.sketch.decode.internal.isGif
import com.github.panpf.sketch.drawable.AnimatableDrawable
import com.github.panpf.sketch.drawable.MovieDrawable
import com.github.panpf.sketch.fetch.FetchResult
import com.github.panpf.sketch.request.ANIMATION_REPEAT_INFINITE
import com.github.panpf.sketch.request.ImageData
import com.github.panpf.sketch.request.RequestContext
import com.github.panpf.sketch.request.animatedTransformation
import com.github.panpf.sketch.request.animationEndCallback
import com.github.panpf.sketch.request.animationStartCallback
import com.github.panpf.sketch.request.disallowAnimatedImage
import com.github.panpf.sketch.request.repeatCount
import com.github.panpf.sketch.source.DataSource
import com.github.panpf.sketch.util.Size
import com.github.panpf.sketch.util.animatable2CompatCallbackOf
import com.github.panpf.sketch.util.safeToSoftware
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.internal.SynchronizedObject
import kotlinx.coroutines.launch
import okio.buffer

/**
 * Adds gif support by Movie
 *
 * @see com.github.panpf.sketch.animated.gif.android.test.decode.MovieGifDecoderTest.testSupportMovieGif
 */
fun ComponentRegistry.Builder.supportMovieGif(): ComponentRegistry.Builder = apply {
    add(MovieGifDecoder.Factory())
}

/**
 * A [Decoder] that uses [Movie] to decode GIFs.
 *
 * The following decoding related properties are supported:
 *
 * * colorType
 * * disallowAnimatedImage
 * * repeatCount
 * * animatedTransformation
 * * onAnimationStart
 * * onAnimationEnd
 *
 * The following decoding related properties are not supported:
 *
 * * sizeResolver
 * * sizeMultiplier
 * * precisionDecider
 * * scaleDecider
 * * colorSpace
 *
 * @see com.github.panpf.sketch.animated.gif.android.test.decode.MovieGifDecoderTest
 */
class MovieGifDecoder(
    private val requestContext: RequestContext,
    private val dataSource: DataSource,
) : Decoder {

    private var _imageInfo: ImageInfo? = null
    private val imageInfoLock = SynchronizedObject()

    override val imageInfo: ImageInfo
        get() {
            synchronized(imageInfoLock) {
                val imageInfo = _imageInfo
                if (imageInfo != null) return imageInfo
                val movie: Movie? = dataSource.openSource()
                    .buffer().inputStream().use { Movie.decodeStream(it) }
                val width = movie?.width() ?: 0
                val height = movie?.height() ?: 0
                return ImageInfo(
                    size = Size(width = width, height = height),
                    mimeType = ImageFormat.GIF.mimeType,
                ).apply {
                    checkImageInfo(this)
                    _imageInfo = this
                }
            }
        }

    @WorkerThread
    override fun decode(): ImageData {
        val request = requestContext.request
        val movie: Movie? = dataSource.openSource()
            .buffer().inputStream().use { Movie.decodeStream(it) }
        check(movie != null) { "Failed to decode GIF." }

        val imageSize = Size(width = movie.width(), height = movie.height())
        checkImageSize(imageSize)
        val imageInfo = ImageInfo(
            size = imageSize,
            mimeType = ImageFormat.GIF.mimeType
        )

        val decodeConfig =
            DecodeConfig(request, ImageFormat.GIF.mimeType, isOpaque = movie.isOpaque)
        val config = decodeConfig.colorType.safeToSoftware()
        val movieDrawable = MovieDrawable(movie, config).apply {
            setRepeatCount(request.repeatCount ?: ANIMATION_REPEAT_INFINITE)

            // Set the animated transformation to be applied on each frame.
            setAnimatedTransformation(request.animatedTransformation)
        }

        val animatableDrawable = AnimatableDrawable(movieDrawable).apply {
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

        val resize = requestContext.computeResize(imageInfo.size)
        return ImageData(
            image = animatableDrawable.asImage(),
            imageInfo = imageInfo,
            dataFrom = dataSource.dataFrom,
            resize = resize,
            transformeds = null,
            extras = null,
        )
    }

    class Factory : Decoder.Factory {

        override val key: String = "MovieGifDecoder"

        override fun create(
            requestContext: RequestContext,
            fetchResult: FetchResult
        ): Decoder? {
            if (requestContext.request.disallowAnimatedImage == true) return null
            if (!isApplicable(fetchResult)) return null
            return MovieGifDecoder(requestContext, fetchResult.dataSource)
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

        override fun toString(): String = "MovieGifDecoder"
    }
}
