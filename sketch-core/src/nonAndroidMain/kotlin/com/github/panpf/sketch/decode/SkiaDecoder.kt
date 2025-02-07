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

import com.github.panpf.sketch.decode.internal.HelperDecoder
import com.github.panpf.sketch.decode.internal.SkiaDecodeHelper
import com.github.panpf.sketch.fetch.FetchResult
import com.github.panpf.sketch.request.RequestContext
import com.github.panpf.sketch.source.DataSource

/**
 * Decode image files using Skia Image
 *
 * The following decoding related properties are supported:
 *
 * * sizeResolver
 * * sizeMultiplier
 * * precisionDecider
 * * scaleDecider
 * * colorType
 * * colorSpace
 *
 * @see com.github.panpf.sketch.core.nonandroid.test.decode.SkiaDecoderTest
 */
class SkiaDecoder(
    requestContext: RequestContext,
    dataSource: DataSource,
) : HelperDecoder(
    requestContext = requestContext,
    dataSource = dataSource,
    decodeHelperFactory = { SkiaDecodeHelper(requestContext.request, dataSource) }
) {

    class Factory : Decoder.Factory {

        override val key: String get() = "SkiaDecoder"

        override fun create(
            requestContext: RequestContext,
            fetchResult: FetchResult,
        ): Decoder = SkiaDecoder(requestContext, fetchResult.dataSource)

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            return other != null && this::class == other::class
        }

        override fun hashCode(): Int {
            return this::class.hashCode()
        }

        override fun toString(): String = "SkiaDecoder"
    }
}