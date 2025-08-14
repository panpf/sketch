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
import com.github.panpf.sketch.asImage
import com.github.panpf.sketch.cache.ImageCacheValue
import com.github.panpf.sketch.fetch.isBlurHashUri
import com.github.panpf.sketch.fetch.newBlurHashUri
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.util.BlurHashUtil
import com.github.panpf.sketch.util.Size
import com.github.panpf.sketch.util.blurHashMemoryCacheKey
import com.github.panpf.sketch.util.decodeBlurHashToBitmap
import com.github.panpf.sketch.util.limitSide
import com.github.panpf.sketch.util.resolveBlurHashBitmapSize
import com.github.panpf.sketch.util.toUri

/**
 * StateImage that creates a Bitmap directly from blurHash and caches it in memory
 *
 * @param blurHash 'LEHLh[WB2yk8pyoJadR*.7kCMdnj' or 'blurhash://LEHV6nWB2yk8pyo0adR*.7kCMdnj?width=200&height=100'.
 * When using the uri format, please use the [newBlurHashUri] function to build it, which will automatically encode characters that are not supported by url.
 * @see com.github.panpf.sketch.blurhash.common.test.state.BlurHashStateImageTest
 */
data class BlurHashStateImage constructor(
    val blurHash: String,
    val size: Size? = null,
    val maxSide: Int? = null,
) : StateImage {

    override val key: String = "BlurHashStateImage('${blurHash}',${size},${maxSide})"

    override fun getImage(sketch: Sketch, request: ImageRequest, throwable: Throwable?): Image? {
        val (realBlurHash, bitmapSize) = if (isBlurHashUri(blurHash)) {
            val blurHashUri = blurHash.toUri()
            val bitmapSize = resolveBlurHashBitmapSize(blurHashUri, size)
            val realBlurHash = blurHashUri.authority.orEmpty()
            realBlurHash to bitmapSize.limitSide(maxSide)
        } else {
            val bitmapSize = resolveBlurHashBitmapSize(blurHashUri = null, size = size)
            blurHash to bitmapSize.limitSide(maxSide)
        }
        require(BlurHashUtil.isValid(realBlurHash)) {
            "Invalid blurHash: $blurHash"
        }

        val cacheKey = blurHashMemoryCacheKey(realBlurHash, bitmapSize)
        val memoryCache = sketch.memoryCache
        val cachedValue = memoryCache[cacheKey]
        if (cachedValue != null) {
            return cachedValue.image
        }

        // If you go to IO thread decoding, you must complete the decoding before the user sees it (actually this is not guaranteed), otherwise the user will see an empty picture
        // So we cannot go to IO thread decoding here. To improve decoding performance, we can only reduce the size of the image.
        val bitmap = runCatching {
            decodeBlurHashToBitmap(
                blurHash = realBlurHash,
                width = bitmapSize.width,
                height = bitmapSize.height
            )
        }.onFailure {
            sketch.logger.w {
                "BlurHashStateImage decode blurHash failed, blurHash=$realBlurHash, size=$bitmapSize, error=${it.message}"
            }
        }.getOrNull() ?: return null

        val bitmapImage = bitmap.asImage()
        memoryCache.put(cacheKey, ImageCacheValue(bitmapImage))

        return bitmapImage
    }

    override fun toString(): String =
        "BlurHashStateImage(blurHash='${blurHash}', size=$size, maxSide=$maxSide)"
}