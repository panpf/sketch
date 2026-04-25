/*
 * Copyright (C) 2024 panpf <panpfpanpf@outlook.com>
 * Copyright (C) 2026 Kuki93
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

@file:OptIn(ExperimentalForeignApi::class)

package com.github.panpf.sketch.decode

import com.github.panpf.sketch.ComponentRegistry
import com.github.panpf.sketch.decode.internal.FileVideoFrameDecodeHelper
import com.github.panpf.sketch.decode.internal.HelperDecoder
import com.github.panpf.sketch.fetch.FetchResult
import com.github.panpf.sketch.request.RequestContext
import com.github.panpf.sketch.source.FileDataSource
import kotlinx.cinterop.ExperimentalForeignApi

/**
 * Add support for video frames from File
 *
 * @see com.github.panpf.sketch.video.ios.test.decode.FileVideoFrameDecoderTest.testSupportFileVideoFrame
 */
fun ComponentRegistry.Builder.supportFileVideoFrame(): ComponentRegistry.Builder = apply {
    add(FileVideoFrameDecoder.Factory())
}

/**
 * Decode a frame of a video file from a File and convert it to a bitmap
 *
 * The following decoding related properties are supported:
 *
 * * sizeResolver: Only sampleSize
 * * sizeMultiplier
 * * precisionDecider: Only LESS_PIXELS and SMALLER_SIZE is supported
 * * videoFrameMicros
 * * videoFramePercent
 *
 * The following decoding related properties are not supported:
 *
 * * scaleDecider
 * * colorType
 * * colorSpace
 *
 * @see com.github.panpf.sketch.video.ios.test.decode.FileVideoFrameDecoderTest
 */
class FileVideoFrameDecoder(
    requestContext: RequestContext,
    dataSource: FileDataSource,
    mimeType: String,
) : HelperDecoder(
    requestContext = requestContext,
    dataSource = dataSource,
    decodeHelperFactory = {
        FileVideoFrameDecodeHelper(
            request = requestContext.request,
            dataSource = dataSource,
            mimeType = mimeType,
        )
    },
) {

    companion object {
        const val SORT_WEIGHT = 30
    }

    class Factory : Decoder.Factory {

        override val key: String = "FileVideoFrameDecoder"
        override val sortWeight: Int = SORT_WEIGHT

        override fun create(
            requestContext: RequestContext,
            fetchResult: FetchResult,
        ): FileVideoFrameDecoder? {
            val dataSource = fetchResult.dataSource
            if (dataSource !is FileDataSource) return null
            val mimeType = fetchResult.mimeType ?: return null
            if (!isApplicable(mimeType)) return null
            return FileVideoFrameDecoder(
                requestContext = requestContext,
                dataSource = dataSource,
                mimeType = mimeType,
            )
        }

        private fun isApplicable(mimeType: String): Boolean {
            return mimeType.startsWith("video/")
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            return other != null && this::class == other::class
        }

        override fun hashCode(): Int = this::class.hashCode()

        override fun toString(): String = "FileVideoFrameDecoder"
    }
}
