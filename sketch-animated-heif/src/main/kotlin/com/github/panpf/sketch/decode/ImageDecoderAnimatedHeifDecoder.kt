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

import android.os.Build
import androidx.annotation.RequiresApi
import com.github.panpf.sketch.ComponentRegistry
import com.github.panpf.sketch.decode.internal.ImageDecoderAnimatedDecoder
import com.github.panpf.sketch.decode.internal.isAnimatedHeif
import com.github.panpf.sketch.fetch.FetchResult
import com.github.panpf.sketch.request.RequestContext
import com.github.panpf.sketch.source.DataSource

/**
 * Adds animation heif support by AnimatedImageDrawable
 *
 * @see com.github.panpf.sketch.animated.heif.test.decode.ImageDecoderAnimatedHeifDecoderTest.testSupportAnimatedHeif
 */
@RequiresApi(Build.VERSION_CODES.R)
fun ComponentRegistry.Builder.supportAnimatedHeif(): ComponentRegistry.Builder = apply {
    addDecoder(ImageDecoderAnimatedHeifDecoder.Factory())
}

/**
 * Decode heif animated image files using ImageDecoder
 *
 * The following decoding related properties are supported:
 *
 * * sizeResolver: Only sampleSize
 * * sizeMultiplier
 * * precisionDecider: Only LESS_PIXELS and SMALLER_SIZE is supported
 * * colorSpace
 * * disallowAnimatedImage
 * * repeatCount
 * * animatedTransformation
 * * onAnimationStart
 * * onAnimationEnd
 *
 * The following decoding related properties are not supported:
 *
 * * scaleDecider
 * * colorType
 *
 * @see com.github.panpf.sketch.animated.heif.test.decode.ImageDecoderAnimatedHeifDecoderTest
 */
@RequiresApi(Build.VERSION_CODES.R)
class ImageDecoderAnimatedHeifDecoder(
    requestContext: RequestContext,
    dataSource: DataSource,
) : ImageDecoderAnimatedDecoder(requestContext, dataSource) {

    class Factory : Decoder.Factory {

        override val key: String = "ImageDecoderAnimatedHeifDecoder"

        override fun create(
            requestContext: RequestContext,
            fetchResult: FetchResult
        ): Decoder? {
            val dataSource = fetchResult.dataSource
            if (
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.R
                && !requestContext.request.disallowAnimatedImage
                && fetchResult.headerBytes.isAnimatedHeif()
            ) {
                return ImageDecoderAnimatedHeifDecoder(requestContext, dataSource)
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

        override fun toString(): String = "ImageDecoderAnimatedHeifDecoder"
    }
}