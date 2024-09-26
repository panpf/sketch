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

import com.github.panpf.sketch.util.toLogString
import org.jetbrains.skia.Codec

/**
 * Skia animated [Codec] [Image]
 *
 * @see com.github.panpf.sketch.core.nonandroid.test.SkiaAnimatedImageTest
 */
data class SkiaAnimatedImage constructor(
    val codec: Codec,
    val imageInfo: SkiaImageInfo = codec.imageInfo,
    val repeatCount: Int? = null,
    val cacheDecodeTimeoutFrame: Boolean = false,
    val animationStartCallback: (() -> Unit)? = null,
    val animationEndCallback: (() -> Unit)? = null,
) : Image {

    override val width: Int = codec.width

    override val height: Int = codec.height

    override val byteCount: Long = imageInfo.bytesPerPixel.toLong() * width * height

    override val shareable: Boolean = false

    override fun checkValid(): Boolean = true

    override fun toString(): String =
        "SkiaAnimatedImage(image=${codec.toLogString()}, shareable=$shareable)"
}