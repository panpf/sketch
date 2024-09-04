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

import android.graphics.Bitmap.Config.ARGB_8888
import android.graphics.Bitmap.Config.RGB_565
import android.graphics.Movie
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.annotation.WorkerThread
import com.github.panpf.sketch.ComponentRegistry
import com.github.panpf.sketch.asSketchImage
import com.github.panpf.sketch.decode.internal.ImageFormat
import com.github.panpf.sketch.decode.internal.isGif
import com.github.panpf.sketch.drawable.AnimatableDrawable
import com.github.panpf.sketch.drawable.MovieDrawable
import com.github.panpf.sketch.fetch.FetchResult
import com.github.panpf.sketch.request.ANIMATION_REPEAT_INFINITE
import com.github.panpf.sketch.request.RequestContext
import com.github.panpf.sketch.request.animatable2CompatCallbackOf
import com.github.panpf.sketch.request.animatedTransformation
import com.github.panpf.sketch.request.animationEndCallback
import com.github.panpf.sketch.request.animationStartCallback
import com.github.panpf.sketch.request.bitmapConfig
import com.github.panpf.sketch.request.repeatCount
import com.github.panpf.sketch.source.DataSource
import com.github.panpf.sketch.util.Size
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.internal.SynchronizedObject
import kotlinx.coroutines.withContext
import okio.buffer

/**
 * Adds gif support by Movie
 *
 * @see com.github.panpf.sketch.animated.android.test.decode.GifMovieDecoderTest.testSupportMovieGif
 */
@RequiresApi(Build.VERSION_CODES.KITKAT)
fun ComponentRegistry.Builder.supportMovieGif(): ComponentRegistry.Builder = apply {
    addDecoder(GifMovieDecoder.Factory())
}

/**
 * A [Decoder] that uses [Movie] to decode GIFs.
 *
 * Only the following attributes are supported:
 *
 * * repeatCount
 * * animatedTransformation
 * * onAnimationStart
 * * onAnimationEnd
 *
 * @see com.github.panpf.sketch.animated.android.test.decode.GifMovieDecoderTest
 */
@RequiresApi(Build.VERSION_CODES.KITKAT)
class GifMovieDecoder(
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
                    _imageInfo = this
                }
            }
        }

    @WorkerThread
    override suspend fun decode(): Result<DecodeResult> = kotlin.runCatching {
        val request = requestContext.request
        val movie: Movie? = dataSource.openSource()
            .buffer().inputStream().use { Movie.decodeStream(it) }

        val width = movie?.width() ?: 0
        val height = movie?.height() ?: 0
        check(movie != null && width > 0 && height > 0) { "Failed to decode GIF." }

        val config = if (movie.isOpaque && request.bitmapConfig?.isLowQuality == true)
            RGB_565 else ARGB_8888
        val movieDrawable = MovieDrawable(movie, config).apply {
            setRepeatCount(request.repeatCount ?: ANIMATION_REPEAT_INFINITE)

            // Set the animated transformation to be applied on each frame.
            setAnimatedTransformation(request.animatedTransformation)

            // TODO Support resize
        }

        val imageInfo = ImageInfo(
            width = width,
            height = height,
            mimeType = ImageFormat.GIF.mimeType
        )

        val animatableDrawable = AnimatableDrawable(movieDrawable).apply {
            // Set the start and end animation callbacks if any one is supplied through the request.
            val onStart = request.animationStartCallback
            val onEnd = request.animationEndCallback
            if (onStart != null || onEnd != null) {
                withContext(Dispatchers.Main) {
                    registerAnimationCallback(animatable2CompatCallbackOf(onStart, onEnd))
                }
            }
        }

        val resize = requestContext.computeResize(imageInfo.size)
        DecodeResult(
            image = animatableDrawable.asSketchImage(),
            imageInfo = imageInfo,
            dataFrom = dataSource.dataFrom,
            resize = resize,
            transformeds = null,
            extras = null,
        )
    }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    class Factory : Decoder.Factory {

        override val key: String = "GifMovieDecoder"

        override fun create(
            requestContext: RequestContext,
            fetchResult: FetchResult
        ): Decoder? {
            val dataSource = fetchResult.dataSource
            if (
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT
                && !requestContext.request.disallowAnimatedImage
                && fetchResult.headerBytes.isGif()
            ) {
                return GifMovieDecoder(requestContext, dataSource)
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

        override fun toString(): String = "GifMovieDecoder"
    }
}
