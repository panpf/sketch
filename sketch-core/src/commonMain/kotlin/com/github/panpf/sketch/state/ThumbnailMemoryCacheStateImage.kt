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

package com.github.panpf.sketch.state

import com.github.panpf.sketch.Image
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.cache.MemoryCache
import com.github.panpf.sketch.cache.getImageInfo
import com.github.panpf.sketch.cache.getTransformeds
import com.github.panpf.sketch.decode.internal.isInSampledTransformed
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.size
import com.github.panpf.sketch.util.isThumbnailWithSize

/**
 * Find a Bitmap with the same aspect ratio and not modified by Transformation as a status image from memory
 * @param uri The uri of the image, if null use ImageRequest.uri
 *
 * @see com.github.panpf.sketch.core.common.test.state.ThumbnailMemoryCacheStateImageTest
 */
data class ThumbnailMemoryCacheStateImage(
    val uri: String? = null,
    val defaultImage: StateImage? = null,
    val maxMismatchCount: Int = -1
) : StateImage {

    // For binary compatibility
    constructor(
        uri: String? = null,
        defaultImage: StateImage? = null
    ) : this(uri, defaultImage, -1)

    override val key: String =
        "ThumbnailMemoryCache(${uri?.let { "'${it}'" }},${defaultImage?.key},$maxMismatchCount)"

    override fun getImage(
        sketch: Sketch,
        request: ImageRequest,
        throwable: Throwable?
    ): Image? {
        var mismatchCount = 0
        var targetCachedValue: MemoryCache.Value? = null
        val uri: String = uri ?: request.uri.toString()
        val entries: Set<Map.Entry<String, MemoryCache.Value>> = sketch.memoryCache.entries()
        for (entry: Map.Entry<String, MemoryCache.Value> in entries) {
            val (memoryCacheKey: String, value: MemoryCache.Value) = entry

            if (!checkMemoryCacheKey(memoryCacheKey, uri)) {
                continue
            }

            if (checkThumbnailBySize(value) && checkTransformeds(value)) {
                targetCachedValue = value
                break
            } else if (maxMismatchCount >= 0 && ++mismatchCount > maxMismatchCount) {
                break
            }
        }
        return targetCachedValue?.image ?: defaultImage?.getImage(sketch, request, throwable)
    }

    private fun checkMemoryCacheKey(memoryCacheKey: String, uri: String): Boolean {
        // memoryCacheKey == "${uri}[?|&]_${options}". See RequestKeys.newCacheKey() for details.
        if (!memoryCacheKey.startsWith(uri)) {
            return false
        }
        if (memoryCacheKey.length < uri.length + 2) {
            return false
        }
        val char1 = memoryCacheKey[uri.length]
        val char2 = memoryCacheKey[uri.length + 1]
        return (char1 == '?' || char1 == '&') && char2 == '_'
    }

    private fun checkThumbnailBySize(value: MemoryCache.Value): Boolean {
        val imageInfo = value.getImageInfo() ?: return false
        val image = value.image
        if (image.width >= imageInfo.size.width || image.height >= imageInfo.size.height) {
            return false
        }
        return isThumbnailWithSize(
            size = imageInfo.size,
            otherSize = image.size,
            epsilonPixels = 2f
        )
    }

    private fun checkTransformeds(value: MemoryCache.Value): Boolean {
        val transformeds = value.getTransformeds()
        return transformeds == null || transformeds.all { isInSampledTransformed(it) }
    }

    override fun toString(): String =
        "ThumbnailMemoryCacheStateImage(uri=${uri?.let { "'${it}'" }}, defaultImage=$defaultImage, maxMismatchCount=$maxMismatchCount)"
}