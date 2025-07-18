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

package com.github.panpf.sketch.decode.internal

import com.github.panpf.sketch.Image
import com.github.panpf.sketch.annotation.WorkerThread
import com.github.panpf.sketch.decode.DecodeResult
import com.github.panpf.sketch.decode.Decoder
import com.github.panpf.sketch.decode.ImageInfo
import com.github.panpf.sketch.decode.ImageInvalidException
import com.github.panpf.sketch.request.RequestContext
import com.github.panpf.sketch.resize.Resize
import com.github.panpf.sketch.resize.isSmallerSizeMode
import com.github.panpf.sketch.size
import com.github.panpf.sketch.source.DataSource
import com.github.panpf.sketch.util.requiredWorkThread
import com.github.panpf.sketch.util.size
import kotlinx.atomicfu.locks.SynchronizedObject
import kotlinx.atomicfu.locks.synchronized
import okio.use

/**
 * Using DecodeHelper to decode image
 *
 * @see com.github.panpf.sketch.core.common.test.decode.internal.HelperDecoderTest
 * @see com.github.panpf.sketch.core.android.test.decode.internal.BitmapFactoryDecoderTest
 * @see com.github.panpf.sketch.video.test.decode.VideoFrameDecoderTest
 * @see com.github.panpf.sketch.video.ffmpeg.test.decode.FFmpegVideoFrameDecoderTest
 * @see com.github.panpf.sketch.core.nonandroid.test.decode.SkiaDecoderTest
 */
open class HelperDecoder(
    requestContext: RequestContext,
    dataSource: DataSource,
    decodeHelperFactory: () -> DecodeHelper,
) : AbstractHelperDecoder(requestContext, dataSource, decodeHelperFactory) {

    private var _imageInfo: ImageInfo? = null
    private val imageInfoLock = SynchronizedObject()

    override val imageInfo: ImageInfo
        get() {
            synchronized(imageInfoLock) {
                val imageInfo = _imageInfo
                if (imageInfo != null) return imageInfo
                val decodeHelper = decodeHelperFactory()
                return decodeHelper.use { it.imageInfo }.apply {
                    _imageInfo = this
                }
            }
        }

    override fun decodeFull(decodeHelper: DecodeHelper, resize: Resize): Pair<Image, List<String>?> {
        val smallerSizeMode = resize.precision.isSmallerSizeMode()
        val sampleSize = calculateSampleSize(
            imageSize = imageInfo.size,
            targetSize = resize.size,
            smallerSizeMode = smallerSizeMode,
            mimeType = imageInfo.mimeType
        )
        val image = decodeHelper.decode(sampleSize)
        val transformeds = if (sampleSize > 1) {
            mutableListOf(createInSampledTransformed(sampleSize)).toList()
        } else {
            null
        }
        return Pair(image, transformeds)
    }

    override fun decodeRegion(
        decodeHelper: DecodeHelper,
        resize: Resize
    ): Pair<Image, List<String>?> {
        val smallerSizeMode = resize.precision.isSmallerSizeMode()
        val resizeMapping = resize.calculateMapping(imageSize = imageInfo.size)
        val regionSize = resizeMapping.srcRect.size
        val targetSize = resizeMapping.dstRect.size
        val sampleSize = calculateSampleSizeForRegion(
            regionSize = regionSize,
            targetSize = targetSize,
            smallerSizeMode = smallerSizeMode,
            mimeType = imageInfo.mimeType,
            imageSize = imageInfo.size
        )
        val transformeds = mutableListOf<String>()
        val image = decodeHelper.decodeRegion(resizeMapping.srcRect, sampleSize)
        if (sampleSize > 1) {
            transformeds.add(createInSampledTransformed(sampleSize))
        }
        transformeds.add(createSubsamplingTransformed(resizeMapping.srcRect))
        return Pair(image, transformeds.toList())
    }
}