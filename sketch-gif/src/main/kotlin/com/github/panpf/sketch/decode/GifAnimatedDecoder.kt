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

import android.os.Build
import androidx.annotation.RequiresApi
import com.github.panpf.sketch.ComponentRegistry
import com.github.panpf.sketch.datasource.DataSource
import com.github.panpf.sketch.decode.internal.AnimatedImageDecoderDecoder
import com.github.panpf.sketch.decode.internal.ImageFormat
import com.github.panpf.sketch.decode.internal.isGif
import com.github.panpf.sketch.fetch.FetchResult
import com.github.panpf.sketch.request.internal.RequestContext

/**
 * Adds gif support by AnimatedImageDrawable
 */
@RequiresApi(Build.VERSION_CODES.P)
fun ComponentRegistry.Builder.supportAnimatedGif(): ComponentRegistry.Builder = apply {
    addDecoder(GifAnimatedDecoder.Factory())
}

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
 */
@RequiresApi(Build.VERSION_CODES.P)
class GifAnimatedDecoder(
    requestContext: RequestContext,
    dataSource: DataSource,
) : AnimatedImageDecoderDecoder(requestContext, dataSource) {

    class Factory : Decoder.Factory {

        override val key: String = "GifAnimatedDecoder"

        override fun create(
            requestContext: RequestContext,
            fetchResult: FetchResult
        ): Decoder? {
            val dataSource = fetchResult.dataSource
            if (
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.P
                && !requestContext.request.disallowAnimatedImage
            ) {
                val imageFormat = ImageFormat.parseMimeType(fetchResult.mimeType)
                val isGif =
                    if (imageFormat == null) fetchResult.headerBytes.isGif() else imageFormat == ImageFormat.GIF
                if (isGif) {
                    return GifAnimatedDecoder(requestContext, dataSource)
                }
            }
            return null
        }

        override fun toString(): String = "GifAnimatedDecoder"

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false
            return true
        }

        override fun hashCode(): Int {
            return javaClass.hashCode()
        }
    }
}