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

import com.github.panpf.sketch.decode.internal.HelperDecoder
import com.github.panpf.sketch.decode.internal.PhotosAssetDecodeHelper
import com.github.panpf.sketch.fetch.FetchResult
import com.github.panpf.sketch.request.RequestContext
import com.github.panpf.sketch.source.PhotosAssetDataSource
import kotlinx.cinterop.ExperimentalForeignApi

/**
 * Decode the photos asset in the iOS system album.
 *
 * @see com.github.panpf.sketch.core.ios.test.decode.PhotosAssetDecoderTest
 */
class PhotosAssetDecoder(
    requestContext: RequestContext,
    dataSource: PhotosAssetDataSource,
    mimeType: String,
) : HelperDecoder(
    requestContext = requestContext,
    dataSource = dataSource,
    decodeHelperFactory = {
        PhotosAssetDecodeHelper(
            dataSource = dataSource,
            mimeType = mimeType
        )
    },
) {

    class Factory : Decoder.Factory {

        override val key: String = "PhotosAssetDecoder"

        override fun create(
            requestContext: RequestContext,
            fetchResult: FetchResult
        ): PhotosAssetDecoder? {
            val dataSource = fetchResult.dataSource
            if (dataSource !is PhotosAssetDataSource) return null
            val mimeType = fetchResult.mimeType ?: return null
            return PhotosAssetDecoder(
                requestContext = requestContext,
                dataSource = dataSource,
                mimeType = mimeType,
            )
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            return other != null && this::class == other::class
        }

        override fun hashCode(): Int = this::class.hashCode()

        override fun toString(): String = "PhotosAssetDecoder"
    }
}
