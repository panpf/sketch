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

package com.github.panpf.sketch.decode.internal

import android.graphics.ImageDecoder
import android.graphics.drawable.AnimatedImageDrawable
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.annotation.WorkerThread
import com.github.panpf.sketch.asImage
import com.github.panpf.sketch.decode.DecodeConfig
import com.github.panpf.sketch.decode.DecodeException
import com.github.panpf.sketch.decode.DecodeResult
import com.github.panpf.sketch.decode.Decoder
import com.github.panpf.sketch.decode.ImageInfo
import com.github.panpf.sketch.drawable.AnimatableDrawable
import com.github.panpf.sketch.drawable.ScaledAnimatedImageDrawable
import com.github.panpf.sketch.request.ANIMATION_REPEAT_INFINITE
import com.github.panpf.sketch.request.RequestContext
import com.github.panpf.sketch.request.animatable2CompatCallbackOf
import com.github.panpf.sketch.request.animatedTransformation
import com.github.panpf.sketch.request.animationEndCallback
import com.github.panpf.sketch.request.animationStartCallback
import com.github.panpf.sketch.request.repeatCount
import com.github.panpf.sketch.resize.isSmallerSizeMode
import com.github.panpf.sketch.source.AssetDataSource
import com.github.panpf.sketch.source.ByteArrayDataSource
import com.github.panpf.sketch.source.ContentDataSource
import com.github.panpf.sketch.source.DataSource
import com.github.panpf.sketch.source.ResourceDataSource
import com.github.panpf.sketch.source.getFileOrNull
import com.github.panpf.sketch.transform.asPostProcessor
import com.github.panpf.sketch.util.Size
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.internal.SynchronizedObject
import kotlinx.coroutines.launch
import java.nio.ByteBuffer

/**
 * Only the following attributes are supported:
 *
 * * resize.size
 * * resize.precision: It is always LESS_PIXELS or SMALLER_SIZE
 * * colorSpace
 * * repeatCount
 * * animatedTransformation
 * * onAnimationStart
 * * onAnimationEnd
 *
 * @see com.github.panpf.sketch.animated.android.test.decode.GifAnimatedDecoderTest
 * @see com.github.panpf.sketch.animated.android.test.decode.WebpAnimatedDecoderTest
 * @see com.github.panpf.sketch.animated.android.test.decode.HeifAnimatedDecoderTest
 */
@RequiresApi(Build.VERSION_CODES.P)
open class ImageDecoderAnimatedDecoder(
    private val requestContext: RequestContext,
    private val dataSource: DataSource,
) : Decoder {

    private var _imageInfo: ImageInfo? = null
    private val imageInfoLock = SynchronizedObject()

    override val imageInfo: ImageInfo
        get() {
            synchronized(imageInfoLock) {
                return _imageInfo ?: dataSource.readImageInfo()
                    .apply { _imageInfo = this }
            }
        }

    @WorkerThread
    override fun decode(): DecodeResult {
        val context = requestContext.request.context
        val source = when (dataSource) {
            is AssetDataSource -> {
                ImageDecoder.createSource(context.assets, dataSource.fileName)
            }

            is ResourceDataSource -> {
                ImageDecoder.createSource(dataSource.resources, dataSource.resId)
            }

            is ContentDataSource -> {
                ImageDecoder.createSource(context.contentResolver, dataSource.contentUri)
            }

            is ByteArrayDataSource -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    ImageDecoder.createSource(dataSource.data)
                } else {
                    ImageDecoder.createSource(ByteBuffer.wrap(dataSource.data))
                }
            }

            else -> {
                dataSource.getFileOrNull(requestContext.sketch)
                    ?.let { ImageDecoder.createSource(it.toFile()) }
                    ?: throw Exception("Unsupported DataSource: ${dataSource::class}")
            }
        }

        var imageInfo: ImageInfo? = null
        var inSampleSize = 1
        var imageDecoder: ImageDecoder? = null
        val request = requestContext.request
        val drawable = try {
            ImageDecoder.decodeDrawable(source) { decoder, info, _ ->
                imageDecoder = decoder
                imageInfo = ImageInfo(
                    width = info.size.width,
                    height = info.size.height,
                    mimeType = info.mimeType,
                )
                val size = requestContext.size
                val precision = request.precisionDecider.get(
                    imageSize = Size(info.size.width, info.size.height),
                    targetSize = size,
                )
                inSampleSize = calculateSampleSize(
                    imageSize = Size(info.size.width, info.size.height),
                    targetSize = size,
                    smallerSizeMode = precision.isSmallerSizeMode()
                )
                decoder.setTargetSampleSize(inSampleSize)

                val decodeConfig = DecodeConfig(request, info.mimeType, isOpaque = true)
                decodeConfig.inPreferredColorSpace?.let {
                    decoder.setTargetColorSpace(it)
                }

                // TODO inPreferredConfig is not supported

                // Set the animated transformation to be applied on each frame.
                decoder.postProcessor = request.animatedTransformation?.asPostProcessor()
            }
        } finally {
            imageDecoder?.close()
        }

        if (drawable !is AnimatedImageDrawable) {
            throw DecodeException("This image is not a animated image, please modify your DrawableDecoder.Factory.create() method to match the image accurately")
        }

        val transformeds: List<String>? =
            if (inSampleSize != 1) listOf(createInSampledTransformed(inSampleSize)) else null
        drawable.repeatCount = request.repeatCount
            ?.takeIf { it != ANIMATION_REPEAT_INFINITE }
            ?: AnimatedImageDrawable.REPEAT_INFINITE
        // AnimatedImageDrawable cannot be scaled using bounds, which will be exposed in the ResizeDrawable
        // Use ScaledAnimatedImageDrawable package solution to this it
        val animatableDrawable =
            AnimatableDrawable(ScaledAnimatedImageDrawable(drawable)).apply {
                val onStart = request.animationStartCallback
                val onEnd = request.animationEndCallback
                if (onStart != null || onEnd != null) {
                    // Will be executed before EngineRequestInterceptor.intercept() return
                    @Suppress("OPT_IN_USAGE")
                    GlobalScope.launch(Dispatchers.Main) {
                        registerAnimationCallback(animatable2CompatCallbackOf(onStart, onEnd))
                    }
                }
            }
        val resize = requestContext.computeResize(imageInfo!!.size)
        return DecodeResult(
            image = animatableDrawable.asImage(),
            imageInfo = imageInfo!!,
            dataFrom = dataSource.dataFrom,
            resize = resize,
            transformeds = transformeds,
            extras = null,
        )
    }
}