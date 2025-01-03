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

package com.github.panpf.sketch

import com.github.panpf.sketch.util.Rect
import com.github.panpf.sketch.util.toLogString
import org.jetbrains.skia.Codec
import org.jetbrains.skia.ImageInfo

/**
 * Animated [Codec] [Image]
 *
 * @see com.github.panpf.sketch.core.nonandroid.test.AnimatedImageTest
 */
data class AnimatedImage constructor(
    val codec: Codec,
    val imageInfo: ImageInfo = codec.imageInfo,
    val repeatCount: Int? = null,
    val cacheDecodeTimeoutFrame: Boolean = false,
) : Image {

    var animatedTransformation: ((Any, Rect) -> Unit)? = null
    var animationStartCallback: (() -> Unit)? = null
    var animationEndCallback: (() -> Unit)? = null

    override val width: Int = codec.width

    override val height: Int = codec.height

    override val byteCount: Long = imageInfo.bytesPerPixel.toLong() * width * height

    override val shareable: Boolean = false

    override fun checkValid(): Boolean = true

    override fun toString(): String = "AnimatedImage(" +
            "image=${codec.toLogString()}, " +
            "imageInfo=$imageInfo, " +
            "repeatCount=$repeatCount, " +
            "cacheDecodeTimeoutFrame=$cacheDecodeTimeoutFrame" +
            ")"
}