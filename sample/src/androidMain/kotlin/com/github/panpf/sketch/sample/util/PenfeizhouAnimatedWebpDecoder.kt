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

package com.github.panpf.sketch.sample.util

import android.content.Context
import com.github.panpf.sketch.asImage
import com.github.panpf.sketch.decode.DecodeResult
import com.github.panpf.sketch.decode.Decoder
import com.github.panpf.sketch.decode.ImageInfo
import com.github.panpf.sketch.decode.internal.calculateSampleSize
import com.github.panpf.sketch.decode.internal.isAnimatedWebP
import com.github.panpf.sketch.decode.internal.resize
import com.github.panpf.sketch.drawable.ScaledAnimatableDrawable
import com.github.panpf.sketch.fetch.FetchResult
import com.github.panpf.sketch.request.RequestContext
import com.github.panpf.sketch.request.disallowAnimatedImage
import com.github.panpf.sketch.resize.isSmallerSizeMode
import com.github.panpf.sketch.source.AssetDataSource
import com.github.panpf.sketch.source.ByteArrayDataSource
import com.github.panpf.sketch.source.ContentDataSource
import com.github.panpf.sketch.source.DataSource
import com.github.panpf.sketch.source.FileDataSource
import com.github.panpf.sketch.source.ResourceDataSource
import com.github.panpf.sketch.util.Size
import com.github.penfeizhou.animation.loader.AssetStreamLoader
import com.github.penfeizhou.animation.loader.ByteBufferLoader
import com.github.penfeizhou.animation.loader.FileLoader
import com.github.penfeizhou.animation.loader.Loader
import com.github.penfeizhou.animation.loader.ResourceStreamLoader
import com.github.penfeizhou.animation.loader.StreamLoader
import com.github.penfeizhou.animation.webp.WebPDrawable
import com.github.penfeizhou.animation.webp.decode.WebPDecoder
import java.io.IOException
import java.io.InputStream
import java.nio.ByteBuffer
import kotlin.math.ceil

class PenfeizhouAnimatedWebpDecoder(
    val requestContext: RequestContext,
    val dataSource: DataSource,
    val disallowAnimatedImage: Boolean,
) : Decoder {

    override val imageInfo: ImageInfo by lazy {
        val streamLoader = dataSource.toStreamLoader(requestContext.request.context)
        val webpDecoder = WebPDecoder(streamLoader, null)
        val bounds = webpDecoder.bounds
        ImageInfo(
            width = bounds.width(),
            height = bounds.height(),
            mimeType = "image/webp"
        )
    }

    override fun decode(): DecodeResult {
        val loader = dataSource.toStreamLoader(requestContext.request.context)
        val webpDecoder = WebPDecoder(loader, null)

        val bounds = webpDecoder.bounds
        val imageInfo = ImageInfo(
            width = bounds.width(),
            height = bounds.height(),
            mimeType = "image/webp"
        )
        val resize = requestContext.computeResize(imageInfo.size)

        // TODO https://github.com/penfeizhou/APNG4Android/issues/228
        if (!disallowAnimatedImage) {
            val inSampleSize = calculateSampleSize(
                imageSize = imageInfo.size,
                targetSize = resize.size,
                smallerSizeMode = resize.precision.isSmallerSizeMode()
            )
            val dstSize = Size(
                width = ceil(imageInfo.width / inSampleSize.toFloat()).toInt(),
                height = ceil(imageInfo.height / inSampleSize.toFloat()).toInt()
            )
            webpDecoder.setDesiredSize(dstSize.width, dstSize.height)
        }

        val image = if (disallowAnimatedImage) {
            webpDecoder.getFrameBitmap(0).asImage()
        } else {
            // WebPDrawable cannot be scaled using bounds, which will be exposed in the ResizeDrawable
            // Use ScaledAnimatableDrawable package solution to this it
            ScaledAnimatableDrawable(WebPDrawable(webpDecoder)).asImage()
        }

        val decodeResult = DecodeResult(
            image = image,
            imageInfo = imageInfo,
            dataFrom = dataSource.dataFrom,
            resize = resize,
            transformeds = null,
            extras = null,
        )
        val resizeResult = decodeResult.resize(resize)
        return resizeResult
    }

    class Factory : Decoder.Factory {

        override val key: String = "PenfeizhouAnimatedWebpDecoder"

        override fun create(requestContext: RequestContext, fetchResult: FetchResult): Decoder? {
            val dataSource = fetchResult.dataSource
            if (fetchResult.headerBytes.isAnimatedWebP()) {
                return PenfeizhouAnimatedWebpDecoder(
                    requestContext = requestContext,
                    dataSource = dataSource,
                    disallowAnimatedImage = requestContext.request.disallowAnimatedImage ?: false
                )
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

        override fun toString(): String = "PenfeizhouAnimatedWebpDecoder"
    }

    private fun DataSource.toStreamLoader(context: Context): Loader {
        return when (this) {
            is FileDataSource -> {
                FileLoader(path.toString())
            }

            is ByteArrayDataSource -> {
                object : ByteBufferLoader() {
                    override fun getByteBuffer(): ByteBuffer {
                        return ByteBuffer.wrap(data)
                    }
                }
            }

            is ContentDataSource -> {
                object : StreamLoader() {
                    override fun getInputStream(): InputStream {
                        return context.contentResolver.openInputStream(contentUri)
                            ?: throw IOException("Invalid content uri: $contentUri")
                    }
                }
            }

            is AssetDataSource -> {
                AssetStreamLoader(context, fileName)
            }

            is ResourceDataSource -> {
                ResourceStreamLoader(context, resId)
            }

            else -> {
                throw IllegalArgumentException("Unsupported DataSource: ${this::class}")
            }
        }
    }
}