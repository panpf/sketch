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

import com.github.panpf.sketch.SkiaAnimatedImage
import com.github.panpf.sketch.decode.DecodeResult
import com.github.panpf.sketch.decode.Decoder
import com.github.panpf.sketch.decode.ImageInfo
import com.github.panpf.sketch.request.RequestContext
import com.github.panpf.sketch.request.animationEndCallback
import com.github.panpf.sketch.request.animationStartCallback
import com.github.panpf.sketch.request.cacheDecodeTimeoutFrame
import com.github.panpf.sketch.request.repeatCount
import com.github.panpf.sketch.source.DataSource
import okio.buffer
import org.jetbrains.skia.Codec
import org.jetbrains.skia.Data

open class SkiaAnimatedDecoder(
    private val requestContext: RequestContext,
    private val dataSource: DataSource,
) : Decoder {

    override suspend fun decode(): Result<DecodeResult> = runCatching {
        val bytes = dataSource.openSource().buffer().readByteArray()
        val data = Data.makeFromBytes(bytes)
        val codec = Codec.makeFromData(data)
        val mimeType = "image/${codec.encodedImageFormat.name.lowercase()}"
        val imageInfo = ImageInfo(
            width = codec.width,
            height = codec.height,
            mimeType = mimeType,
        )
        // TODO not support resize
        val request = requestContext.request
        val repeatCount = request.repeatCount
        val cacheDecodeTimeoutFrame = request.cacheDecodeTimeoutFrame == true
        val resize = requestContext.computeResize(imageInfo.size)
        DecodeResult(
            image = SkiaAnimatedImage(
                codec = codec,
                repeatCount = repeatCount,
                cacheDecodeTimeoutFrame = cacheDecodeTimeoutFrame,
                animationStartCallback = request.animationStartCallback,
                animationEndCallback = request.animationEndCallback
            ),
            imageInfo = imageInfo,
            dataFrom = dataSource.dataFrom,
            resize = resize,
            transformeds = null,
            extras = null,
        )
    }
}