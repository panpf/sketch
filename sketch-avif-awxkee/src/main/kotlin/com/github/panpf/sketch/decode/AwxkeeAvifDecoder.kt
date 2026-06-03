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

@file:OptIn(InternalCoroutinesApi::class)

package com.github.panpf.sketch.decode

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import com.github.panpf.sketch.ComponentRegistry
import com.github.panpf.sketch.decode.internal.AwxkeeAvifDecodeHelper
import com.github.panpf.sketch.decode.internal.HelperDecoder
import com.github.panpf.sketch.fetch.FetchResult
import com.github.panpf.sketch.request.RequestContext
import com.github.panpf.sketch.source.DataSource
import com.github.panpf.sketch.util.isAvifFile
import com.github.panpf.sketch.util.isHeifFile
import kotlinx.coroutines.InternalCoroutinesApi

/**
 * Adds avif and heif support by awxkee's avifcoder library.
 *
 * @see com.github.panpf.sketch.avif.awxkee.test.decode.AwxkeeAvifDecoderTest.testSupportAwxkeeAvif
 */
@RequiresApi(Build.VERSION_CODES.N)
fun ComponentRegistry.Builder.supportAwxkeeAvif(): ComponentRegistry.Builder = apply {
    add(AwxkeeAvifDecoder.Factory())
}

/**
 * Decoding implementation of avif and heif by awxkee's avifcoder library.
 *
 * The following decoding related properties are supported:
 *
 * * sizeResolver
 * * sizeMultiplier
 * * precisionDecider
 * * scaleDecider
 * * colorType
 *
 * The following decoding related properties are not supported:
 *
 * * colorSpace
 *
 * @see com.github.panpf.sketch.avif.awxkee.test.decode.AwxkeeAvifDecoderTest
 */
@RequiresApi(Build.VERSION_CODES.N)
class AwxkeeAvifDecoder(
    requestContext: RequestContext,
    dataSource: DataSource,
    mimeType: String,
) : HelperDecoder(
    requestContext = requestContext,
    dataSource = dataSource,
    decodeHelperFactory = { AwxkeeAvifDecodeHelper(requestContext.request, dataSource, mimeType) }
) {

    companion object {
        const val SORT_WEIGHT = 25
    }

    class Factory : Decoder.Factory {

        override val key: String = "AwxkeeAvifDecoder"
        override val sortWeight: Int = SORT_WEIGHT

        @SuppressLint("ObsoleteSdkInt")
        override fun create(
            requestContext: RequestContext,
            fetchResult: FetchResult
        ): AwxkeeAvifDecoder? {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) return null
            val mimeType = isApplicable(fetchResult) ?: return null
            return AwxkeeAvifDecoder(requestContext, fetchResult.dataSource, mimeType)
        }

        private fun isApplicable(fetchResult: FetchResult): String? {
            if (isHeifFile(fetchResult.headerBytes)) {
                return "image/heif"
            }
            if (isAvifFile(fetchResult.headerBytes)) {
                return "image/avif"
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

        override fun toString(): String = "AwxkeeAvifDecoder"
    }
}