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
import com.github.panpf.sketch.fetch.FetchResult
import com.github.panpf.sketch.request.RequestContext

/**
 * Adds webp animated image support
 *
 * @see com.github.panpf.sketch.animated.webp.common.test.decode.AnimatedWebpDecoderTest.testSupportAnimatedWebp
 */
fun ComponentRegistry.Builder.supportAnimatedWebp(): ComponentRegistry.Builder = apply {
    addDecoder(AnimatedWebpDecoder.Factory())
}

/**
 * Decode webp animated image files
 *
 * @see com.github.panpf.sketch.animated.webp.common.test.decode.AnimatedWebpDecoderTest
 */
class AnimatedWebpDecoder(val decoder: Decoder) : Decoder by decoder {

    class Factory : Decoder.Factory {

        private val decoderFactory = defaultAnimatedWebpDecoderFactory()

        override val key: String = "AnimatedWebpDecoder(${decoderFactory?.key})"

        override fun create(
            requestContext: RequestContext,
            fetchResult: FetchResult
        ): Decoder? {
            val decoder = decoderFactory?.create(requestContext, fetchResult) ?: return null
            return AnimatedWebpDecoder(decoder)
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            return other != null && this::class == other::class
        }

        override fun hashCode(): Int {
            return this::class.hashCode()
        }

        override fun toString(): String = "AnimatedWebpDecoder(decoderFactory=$decoderFactory)"
    }
}

/**
 * Get the default GIF decoder factory
 *
 * @see com.github.panpf.sketch.animated.webp.android.test.decode.AnimatedWebpDecoderAndroidTest.testDefaultAnimatedWebpDecoderFactory
 * @see com.github.panpf.sketch.animated.webp.nonandroid.test.decode.AnimatedWebpDecoderNonAndroidTest.testDefaultAnimatedWebpDecoderFactory
 */
expect fun defaultAnimatedWebpDecoderFactory(): Decoder.Factory?