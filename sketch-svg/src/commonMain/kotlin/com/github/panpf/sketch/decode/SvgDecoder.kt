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
import com.github.panpf.sketch.decode.SvgDecoder.Factory
import com.github.panpf.sketch.decode.internal.decodeSvg
import com.github.panpf.sketch.decode.internal.isSvg
import com.github.panpf.sketch.fetch.FetchResult
import com.github.panpf.sketch.request.RequestContext
import com.github.panpf.sketch.request.svgBackgroundColor
import com.github.panpf.sketch.request.svgCss
import com.github.panpf.sketch.source.DataSource

/**
 * Adds SVG support
 *
 * @see com.github.panpf.sketch.svg.common.test.decode.SvgDecoderTest.testSupportSvg
 */
fun ComponentRegistry.Builder.supportSvg(): ComponentRegistry.Builder = apply {
    addDecoder(Factory())
}

/**
 * Decode svg file and convert to Bitmap
 *
 * @see com.github.panpf.sketch.svg.common.test.decode.SvgDecoderTest
 */
class SvgDecoder(
    private val requestContext: RequestContext,
    private val dataSource: DataSource,
    private val useViewBoundsAsIntrinsicSize: Boolean = true,
    private val backgroundColor: Int?,
    private val css: String?,
) : Decoder {

    companion object {
        const val MIME_TYPE = "image/svg+xml"
    }

    override suspend fun decode(): Result<DecodeResult> = kotlin.runCatching {
        decodeSvg(
            requestContext = requestContext,
            dataSource = dataSource,
            useViewBoundsAsIntrinsicSize = useViewBoundsAsIntrinsicSize,
            backgroundColor = backgroundColor,
            css = css
        )
    }

    class Factory(val useViewBoundsAsIntrinsicSize: Boolean = true) : Decoder.Factory {

        override val key: String =
            "SvgDecoder(useViewBoundsAsIntrinsicSize=$useViewBoundsAsIntrinsicSize)"

        override fun create(
            requestContext: RequestContext,
            fetchResult: FetchResult
        ): SvgDecoder? {
            val dataSource = fetchResult.dataSource
            return if (fetchResult.headerBytes.isSvg()) {
                SvgDecoder(
                    requestContext = requestContext,
                    dataSource = dataSource,
                    useViewBoundsAsIntrinsicSize = useViewBoundsAsIntrinsicSize,
                    backgroundColor = requestContext.request.svgBackgroundColor,
                    css = requestContext.request.svgCss
                )
            } else {
                null
            }
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other == null || this::class != other::class) return false
            other as Factory
            if (useViewBoundsAsIntrinsicSize != other.useViewBoundsAsIntrinsicSize) return false
            return true
        }

        override fun hashCode(): Int {
            return useViewBoundsAsIntrinsicSize.hashCode()
        }

        override fun toString(): String =
            "SvgDecoder(useViewBoundsAsIntrinsicSize=$useViewBoundsAsIntrinsicSize)"
    }
}