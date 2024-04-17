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

import android.annotation.TargetApi
import android.os.Build
import com.github.panpf.sketch.ComponentRegistry
import com.github.panpf.sketch.decode.internal.HelperDecoder
import com.github.panpf.sketch.decode.internal.VideoFrameDecodeHelper
import com.github.panpf.sketch.fetch.FetchResult
import com.github.panpf.sketch.request.internal.RequestContext
import com.github.panpf.sketch.source.DataSource

/**
 * Adds video frame support
 */
@TargetApi(Build.VERSION_CODES.O_MR1)
fun ComponentRegistry.Builder.supportVideoFrame(): ComponentRegistry.Builder = apply {
    addDecoder(VideoFrameDecoder.Factory())
}

/**
 * Decode a frame of a video file and convert it to Bitmap
 *
 * Notes: Android O(26/8.0) and before versions do not support scale to read frames,
 * resulting in slow decoding speed and large memory consumption in the case of large videos and causes memory jitter
 *
 * Notes：ImageRequest's preferQualityOverSpeed, colorSpace attributes will not take effect;
 * The bitmapConfig attribute takes effect only on Android 30 or later
 */
@TargetApi(Build.VERSION_CODES.O_MR1)
class VideoFrameDecoder(
    private val requestContext: RequestContext,
    private val dataSource: DataSource,
    private val mimeType: String,
) : HelperDecoder(
    requestContext = requestContext,
    dataSource = dataSource,
    decodeHelperFactory = { VideoFrameDecodeHelper(requestContext.request, dataSource, mimeType) }
) {

    @TargetApi(Build.VERSION_CODES.O_MR1)
    class Factory : Decoder.Factory {

        override val key: String = "VideoFrameDecoder"

        override fun create(
            requestContext: RequestContext,
            fetchResult: FetchResult
        ): VideoFrameDecoder? {
            val dataSource = fetchResult.dataSource
            val mimeType = fetchResult.mimeType
            if (mimeType?.startsWith("video/") == true) {
                return VideoFrameDecoder(
                    requestContext = requestContext,
                    dataSource = dataSource,
                    mimeType = mimeType
                )
            }
            return null
        }

        override fun toString(): String = "VideoFrameDecoder"

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