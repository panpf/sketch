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

import com.github.panpf.sketch.annotation.WorkerThread
import com.github.panpf.sketch.decode.DecodeResult
import com.github.panpf.sketch.decode.Decoder
import com.github.panpf.sketch.request.RequestContext
import com.github.panpf.sketch.source.DataSource
import okio.use

/**
 * Using DecodeHelper to decode image
 *
 * @see com.github.panpf.sketch.core.android.test.decode.internal.BitmapFactoryDecoderTest
 * @see com.github.panpf.sketch.video.test.decode.VideoFrameDecoderTest
 * @see com.github.panpf.sketch.video.ffmpeg.test.decode.FFmpegVideoFrameDecoderTest
 * @see com.github.panpf.sketch.core.nonandroid.test.decode.SkiaDecoderTest
 */
open class HelperDecoder(
    private val requestContext: RequestContext,
    private val dataSource: DataSource,
    private val decodeHelperFactory: () -> DecodeHelper,
) : Decoder {

    @WorkerThread
    override suspend fun decode(): Result<DecodeResult> = kotlin.runCatching {
        val decodeResult = decodeHelperFactory().use { decodeHelper ->
            val imageInfo = decodeHelper.imageInfo
            val supportRegion = decodeHelper.supportRegion
            realDecode(
                requestContext = requestContext,
                dataFrom = dataSource.dataFrom,
                imageInfo = imageInfo,
                decodeFull = { sampleSize ->
                    decodeHelper.decode(sampleSize)
                },
                decodeRegion = if (supportRegion) { srcRect, sampleSize ->
                    decodeHelper.decodeRegion(srcRect, sampleSize)
                } else null
            )
        }
        val resizedResult = decodeResult.appliedResize(requestContext)
        resizedResult
    }
}