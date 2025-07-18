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
import com.github.panpf.sketch.util.format
import kotlin.math.abs

/**
 * Find a Bitmap with the same aspect ratio and not modified by Transformation as a status image from memory
 * @param uri The uri of the image, if null use ImageRequest.uri
 *
 * @see com.github.panpf.sketch.core.common.test.state.ThumbnailMemoryCacheStateImageTest
 */
data class ThumbnailMemoryCacheStateImage(
    val uri: String? = null,
    val defaultImage: StateImage? = null
) : StateImage {

    override val key: String =
        "ThumbnailMemoryCache(${uri?.let { "'${it}'" }},${defaultImage?.key})"

    override fun getImage(
        sketch: Sketch,
        request: ImageRequest,
        throwable: Throwable?
    ): Image? {
        val uri: String = uri ?: request.uri.toString()
        val keys = sketch.memoryCache.keys()
        var targetCachedValue: MemoryCache.Value? = null
        var count = 0
        for (key in keys) {
            // The key is spliced by uri and options. The options start with '_'. See RequestUtils.newKey() for details.
            var paramsStartFlagIndex = key.indexOf("?_")
            if (paramsStartFlagIndex == -1) {
                paramsStartFlagIndex = key.indexOf("&_")
            }
            val uriFromKey = if (paramsStartFlagIndex != -1) {
                key.substring(startIndex = 0, endIndex = paramsStartFlagIndex)
            } else {
                key
            }
            if (uri == uriFromKey) {
                val cachedValue = sketch.memoryCache[key]?.takeIf {
                    val imageInfo = it.getImageInfo() ?: return@takeIf false
                    val image = it.image
                    val bitmapAspectRatio = (image.width.toFloat() / image.height).format(1)
                    val imageAspectRatio =
                        (imageInfo.width.toFloat() / imageInfo.height).format(1)
                    val sizeSame = abs(bitmapAspectRatio - imageAspectRatio) <= 0.1f

                    val transformeds = it.getTransformeds()
                    val noOtherTransformed =
                        transformeds == null || transformeds.all { transformed ->
                            isInSampledTransformed(transformed)
                        }

                    sizeSame && noOtherTransformed
                }
                if (cachedValue != null) {
                    targetCachedValue = cachedValue
                    break
                } else if (++count >= 3) {
                    break
                }
            }
        }
        return targetCachedValue?.image ?: defaultImage?.getImage(sketch, request, throwable)
    }

    override fun toString(): String =
        "ThumbnailMemoryCacheStateImage(uri=${uri?.let { "'${it}'" }}, defaultImage=$defaultImage)"
}