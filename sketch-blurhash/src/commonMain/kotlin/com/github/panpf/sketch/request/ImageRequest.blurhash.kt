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

package com.github.panpf.sketch.request

import com.github.panpf.sketch.cache.CachePolicy
import com.github.panpf.sketch.fetch.newBlurHashUri
import com.github.panpf.sketch.state.BlurHashStateImage
import com.github.panpf.sketch.util.Size

/**
 * Set BlurHash placeholder image when loading
 *
 * @param blurHash 'LEHLh[WB2yk8pyoJadR*.7kCMdnj' or 'blurhash://LEHV6nWB2yk8pyo0adR*.7kCMdnj?width=200&height=100'.
 * When using the uri format, please use the [newBlurHashUri] function to build it, which will automatically encode characters that are not supported by url.
 * @see com.github.panpf.sketch.blurhash.common.test.request.ImageRequestBlurHashTest.testBlurHashPlaceholder
 */
fun ImageRequest.Builder.blurHashPlaceholder(
    blurHash: String,
    size: Size? = null,
    maxSide: Int? = null,
    cachePolicy: CachePolicy? = null,
): ImageRequest.Builder =
    placeholder(stateImage = BlurHashStateImage(blurHash, size, maxSide, cachePolicy))

/**
 * Set BlurHash placeholder image when uri is invalid
 *
 * @param blurHash 'LEHLh[WB2yk8pyoJadR*.7kCMdnj' or 'blurhash://LEHV6nWB2yk8pyo0adR*.7kCMdnj?width=200&height=100'.
 * When using the uri format, please use the [newBlurHashUri] function to build it, which will automatically encode characters that are not supported by url.
 * @see com.github.panpf.sketch.blurhash.common.test.request.ImageRequestBlurHashTest.testBlurHashFallback
 */
fun ImageRequest.Builder.blurHashFallback(
    blurHash: String,
    size: Size? = null,
    maxSide: Int? = null,
    cachePolicy: CachePolicy? = null,
): ImageRequest.Builder =
    fallback(stateImage = BlurHashStateImage(blurHash, size, maxSide, cachePolicy))

/**
 * Set BlurHash placeholder image when loading fails.
 *
 * @param blurHash 'LEHLh[WB2yk8pyoJadR*.7kCMdnj' or 'blurhash://LEHV6nWB2yk8pyo0adR*.7kCMdnj?width=200&height=100'.
 * When using the uri format, please use the [newBlurHashUri] function to build it, which will automatically encode characters that are not supported by url.
 * @see com.github.panpf.sketch.blurhash.common.test.request.ImageRequestBlurHashTest.testBlurHashError
 */
fun ImageRequest.Builder.blurHashError(
    blurHash: String,
    size: Size? = null,
    maxSide: Int? = null,
    cachePolicy: CachePolicy? = null,
): ImageRequest.Builder =
    error(stateImage = BlurHashStateImage(blurHash, size, maxSide, cachePolicy))