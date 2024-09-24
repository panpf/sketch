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

@file:Suppress("RedundantConstructorKeyword")

package com.github.panpf.sketch.request

import com.github.panpf.sketch.Image
import com.github.panpf.sketch.decode.ImageInfo
import com.github.panpf.sketch.resize.Resize
import com.github.panpf.sketch.source.DataFrom

/**
 * Result of [ImageRequest]
 *
 * @see com.github.panpf.sketch.core.common.test.request.ImageResultTest
 */
interface ImageResult {

    /**
     * Final request
     */
    val request: ImageRequest

    /**
     * Image data, may be null
     */
    val image: Image?

    /**
     * Image loading success result
     */
    data class Success constructor(
        override val request: ImageRequest,
        override val image: Image,

        /**
         * Cache key, it is used for memory cache reading and writing.
         */
        val cacheKey: String,

        /**
         * Image width, height, type and other information
         */
        val imageInfo: ImageInfo,

        /**
         * Where image comes from
         */
        val dataFrom: DataFrom,

        /**
         * Use this Resize to resize the image when decoding
         */
        val resize: Resize,

        /**
         * Store the transformation history of the Bitmap
         */
        val transformeds: List<String>?,

        /**
         * Store some additional information for consumer use
         */
        val extras: Map<String, String>?,
    ) : ImageResult

    /**
     * Image loading error result
     */
    data class Error constructor(
        override val request: ImageRequest,
        override val image: Image?,

        /**
         * Exception information
         */
        val throwable: Throwable,
    ) : ImageResult
}