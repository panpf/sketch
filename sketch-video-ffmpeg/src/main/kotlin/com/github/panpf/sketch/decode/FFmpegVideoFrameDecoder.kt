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
package com.github.panpf.sketch.decode

import com.github.panpf.sketch.ComponentRegistry
import com.github.panpf.sketch.decode.internal.FFmpegVideoFrameDecodeHelper
import com.github.panpf.sketch.decode.internal.HelperDecoder
import com.github.panpf.sketch.fetch.FetchResult
import com.github.panpf.sketch.request.internal.RequestContext
import com.github.panpf.sketch.source.DataSource

/**
 * Adds video frame support by ffmpeg
 */
fun ComponentRegistry.Builder.supportFFmpegVideoFrame(): ComponentRegistry.Builder = apply {
    addDecoder(FFmpegVideoFrameDecoder.Factory())
}

/**
 * Decode a frame of a video file and convert it to Bitmap
 *
 * Notes: It is not support MediaMetadataRetriever.BitmapParams
 *
 * Notes：ImageRequest's preferQualityOverSpeed, bitmapConfig, colorSpace attributes will not take effect
 */
class FFmpegVideoFrameDecoder(
    requestContext: RequestContext,
    dataSource: DataSource,
    mimeType: String,
) : HelperDecoder(
    requestContext = requestContext,
    dataSource = dataSource,
    decodeHelperFactory = {
        FFmpegVideoFrameDecodeHelper(requestContext.request, dataSource, mimeType)
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

        override fun toString(): String = "FFmpegVideoFrameDecoder"

        @Suppress("RedundantOverride")
        override fun equals(other: Any?): Boolean {
            // If you add construction parameters to this class, you need to change it here
            return super.equals(other)
        }

        @Suppress("RedundantOverride")
        override fun hashCode(): Int {
            // If you add construction parameters to this class, you need to change it here
            return super.hashCode()
        }
    }
}