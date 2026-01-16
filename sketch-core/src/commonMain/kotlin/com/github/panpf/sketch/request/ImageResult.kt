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
 * @see com.github.panpf.sketch.core.common.test.request.ImageResultSuccessTest
 * @see com.github.panpf.sketch.core.common.test.request.ImageResultErrorTest
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

    override fun equals(other: Any?): Boolean

    override fun hashCode(): Int

    override fun toString(): String

    /**
     * Image loading success result
     *
     * @see com.github.panpf.sketch.core.common.test.request.ImageResultSuccessTest
     */
    data class Success constructor(
        override val request: ImageRequest,
        override val image: Image,

        /**
         * Cache key, it is used for memory cache or result cache reading and writing.
         *
         * If you use [ImageRequest.memoryCacheKey], [ImageRequest.memoryCacheKeyMapper], [ImageRequest.resultCacheKey], [ImageRequest.resultCacheKeyMapper], then this [cacheKey] is inaccurate. Please use [memoryCacheKey], [resultCacheKey] instead.
         */
        @Deprecated("Use memoryCacheKey or resultCacheKey instead")
        val cacheKey: String,

        /**
         * Cache key, it is used for memory cache or result cache reading and writing.
         */
        val memoryCacheKey: String,

        /**
         * Cache key, it is used for memory cache or result cache reading and writing.
         */
        val resultCacheKey: String,

        /**
         * Cache key, it is used for memory cache or result cache reading and writing.
         */
        val downloadCacheKey: String,

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
     *
     * @see com.github.panpf.sketch.core.common.test.request.ImageResultErrorTest
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

internal fun buildSuccessResult(
    requestContext: RequestContext,
    request: ImageRequest,
    imageData: ImageData,
    image: Image = imageData.image,
): ImageResult.Success = ImageResult.Success(
    request = request,
    image = image,
    cacheKey = requestContext.cacheKey,
    memoryCacheKey = requestContext.memoryCacheKey,
    resultCacheKey = requestContext.resultCacheKey,
    downloadCacheKey = requestContext.downloadCacheKey,
    imageInfo = imageData.imageInfo,
    dataFrom = imageData.dataFrom,
    resize = imageData.resize,
    transformeds = imageData.transformeds,
    extras = imageData.extras,
)