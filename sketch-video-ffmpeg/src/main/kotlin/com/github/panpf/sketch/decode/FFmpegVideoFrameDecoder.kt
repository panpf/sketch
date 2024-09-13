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

import com.github.panpf.sketch.ComponentRegistry
import com.github.panpf.sketch.decode.internal.FFmpegVideoFrameDecodeHelper
import com.github.panpf.sketch.decode.internal.HelperDecoder
import com.github.panpf.sketch.fetch.FetchResult
import com.github.panpf.sketch.request.RequestContext
import com.github.panpf.sketch.source.DataSource

/**
 * Adds video frame support by ffmpeg
 *
 * @see com.github.panpf.sketch.video.ffmpeg.test.decode.FFmpegVideoFrameDecoderTest.testSupportApkIcon
 */
fun ComponentRegistry.Builder.supportFFmpegVideoFrame(): ComponentRegistry.Builder = apply {
    addDecoder(FFmpegVideoFrameDecoder.Factory())
}

/**
 * Decode a frame of a video file and convert it to Bitmap
 *
 * Notes: It is not support MediaMetadataRetriever.BitmapParams
 *
 * Notes：ImageRequest's preferQualityOverSpeed, colorType, colorSpace attributes will not take effect
 *
 * @see com.github.panpf.sketch.video.ffmpeg.test.decode.FFmpegVideoFrameDecoderTest
 */
class FFmpegVideoFrameDecoder(
    requestContext: RequestContext,
    dataSource: DataSource,
    mimeType: String,
) : HelperDecoder(
    requestContext = requestContext,
    dataSource = dataSource,
    decodeHelperFactory = {
        FFmpegVideoFrameDecodeHelper(
            sketch = requestContext.sketch,
            request = requestContext.request,
            dataSource = dataSource,
            mimeType = mimeType
        )
    }
) {

    class Factory : Decoder.Factory {

        override val key: String = "FFmpegVideoFrameDecoder"

        override fun create(
            requestContext: RequestContext,
            fetchResult: FetchResult
        ): FFmpegVideoFrameDecoder? {
            val dataSource = fetchResult.dataSource
            val mimeType = fetchResult.mimeType
            if (mimeType?.startsWith("video/") == true) {
                return FFmpegVideoFrameDecoder(
                    requestContext = requestContext,
                    dataSource = dataSource,
                    mimeType = mimeType
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

        override fun toString(): String = "FFmpegVideoFrameDecoder"
    }
}