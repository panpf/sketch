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
package com.github.panpf.sketch.decode.internal

import android.graphics.ImageDecoder
import android.graphics.drawable.AnimatedImageDrawable
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.annotation.WorkerThread
import androidx.exifinterface.media.ExifInterface
import com.github.panpf.sketch.datasource.AssetDataSource
import com.github.panpf.sketch.datasource.BasedFileDataSource
import com.github.panpf.sketch.datasource.ByteArrayDataSource
import com.github.panpf.sketch.datasource.ContentDataSource
import com.github.panpf.sketch.datasource.DataSource
import com.github.panpf.sketch.datasource.ResourceDataSource
import com.github.panpf.sketch.decode.DrawableDecodeResult
import com.github.panpf.sketch.decode.DrawableDecoder
import com.github.panpf.sketch.decode.ImageInfo
import com.github.panpf.sketch.drawable.SketchAnimatableDrawable
import com.github.panpf.sketch.drawable.SketchBitmapDrawable
import com.github.panpf.sketch.drawable.internal.ScaledAnimatedImageDrawable
import com.github.panpf.sketch.request.ANIMATION_REPEAT_INFINITE
import com.github.panpf.sketch.request.animatable2CompatCallbackOf
import com.github.panpf.sketch.request.animatedTransformation
import com.github.panpf.sketch.request.animationEndCallback
import com.github.panpf.sketch.request.animationStartCallback
import com.github.panpf.sketch.request.internal.RequestContext
import com.github.panpf.sketch.request.repeatCount
import com.github.panpf.sketch.transform.asPostProcessor
import com.github.panpf.sketch.util.Size
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.nio.ByteBuffer

/**
 * Only the following attributes are supported:
 *
 * resize.size
 *
 * resize.precision: It is always LESS_PIXELS
 *
 * colorSpace
 *
 * repeatCount
 *
 * animatedTransformation
 *
 * onAnimationStart
 *
 * onAnimationEnd
 */
@RequiresApi(Build.VERSION_CODES.P)
abstract class BaseAnimatedImageDrawableDecoder(
    private val requestContext: RequestContext,
    private val dataSource: DataSource,
) : DrawableDecoder {

    @WorkerThread
    override suspend fun decode(): Result<DrawableDecodeResult> = kotlin.runCatching {
        val request = requestContext.request
        val source = when (dataSource) {
            is AssetDataSource -> {
                ImageDecoder.createSource(request.context.assets, dataSource.assetFileName)
            }

            is ResourceDataSource -> {
                ImageDecoder.createSource(dataSource.resources, dataSource.resId)
            }

            is ContentDataSource -> {
                ImageDecoder.createSource(request.context.contentResolver, dataSource.contentUri)
            }

            is ByteArrayDataSource -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    ImageDecoder.createSource(dataSource.data)
                } else {
                    ImageDecoder.createSource(ByteBuffer.wrap(dataSource.data))
                }
            }

            is BasedFileDataSource -> {
                ImageDecoder.createSource(dataSource.getFile())
            }

            else -> {
                throw Exception("Unsupported DataSource: ${dataSource.javaClass}")
            }
        }

        var imageInfo: ImageInfo? = null
        var inSampleSize = 1
        var imageDecoder: ImageDecoder? = null
        val drawable = try {
            ImageDecoder.decodeDrawable(source) { decoder, info, _ ->
                imageDecoder = decoder
                imageInfo = ImageInfo(
                    info.size.width,
                    info.size.height,
                    info.mimeType,
                    ExifInterface.ORIENTATION_UNDEFINED
                )
                val resizeSize = requestContext.resizeSize
                inSampleSize = calculateSampleSize(
                    imageSize = Size(info.size.width, info.size.height),
                    targetSize = Size(resizeSize.width, resizeSize.height)
                )
                decoder.setTargetSampleSize(inSampleSize)

                request.colorSpace?.let {
                    decoder.setTargetColorSpace(it)
                }

                // Set the animated transformation to be applied on each frame.
                decoder.postProcessor = request.animatedTransformation?.asPostProcessor()
            }
        } finally {
            imageDecoder?.close()
        }

        val transformedList: List<String>?
        val finalDrawable = when (drawable) {
            is AnimatedImageDrawable -> {
                transformedList =
                    if (inSampleSize != 1) listOf(createInSampledTransformed(inSampleSize)) else null
                drawable.repeatCount = request.repeatCount
                    ?.takeIf { it != ANIMATION_REPEAT_INFINITE }
                    ?: AnimatedImageDrawable.REPEAT_INFINITE
                SketchAnimatableDrawable(
                    // AnimatedImageDrawable cannot be scaled using bounds, which will be exposed in the ResizeDrawable
                    // Use ScaledAnimatedImageDrawable package solution to this it
                    animatableDrawable = ScaledAnimatedImageDrawable(drawable),
                    imageUri = request.uriString,
                    requestKey = requestContext.key,
                    requestCacheKey = requestContext.cacheKey,
                    imageInfo = imageInfo!!,
                    dataFrom = dataSource.dataFrom,
                    transformedList = transformedList,
                    extras = null,
                ).apply {
                    val onStart = request.animationStartCallback
                    val onEnd = request.animationEndCallback
                    if (onStart != null || onEnd != null) {
                        withContext(Dispatchers.Main) {
                            registerAnimationCallback(animatable2CompatCallbackOf(onStart, onEnd))
                        }
                    }
                }
            }

            is BitmapDrawable -> {
                // Some images are encoded with animated, but only one frame is static, which will be decoded into BitmapDrawable
                transformedList =
                    if (inSampleSize != 1) listOf(createInSampledTransformed(inSampleSize)) else null
                SketchBitmapDrawable(
                    resources = requestContext.request.context.resources,
                    bitmap = drawable.bitmap,
                    imageUri = requestContext.request.uriString,
                    requestKey = requestContext.key,
                    requestCacheKey = requestContext.cacheKey,
                    imageInfo = imageInfo!!,
                    transformedList = transformedList,
                    extras = null,
                    dataFrom = dataSource.dataFrom
                )
            }

            else -> {
                transformedList = null
                drawable
            }
        }
        DrawableDecodeResult(
            drawable = finalDrawable,
            imageInfo = imageInfo!!,
            dataFrom = dataSource.dataFrom,
            transformedList = transformedList,
            extras = null,
        )
    }
}