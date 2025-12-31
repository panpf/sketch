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

package com.github.panpf.sketch

import com.github.panpf.sketch.util.Rect
import com.github.panpf.sketch.util.toLogString
import org.jetbrains.skia.Codec
import org.jetbrains.skia.ColorInfo
import org.jetbrains.skia.ImageInfo

/**
 * Skia Animated [Image]
 *
 * @see com.github.panpf.sketch.animated.core.nonandroid.test.SkiaAnimatedImageTest
 */
data class SkiaAnimatedImage constructor(
    val codec: Codec,
    val colorInfo: ColorInfo = codec.colorInfo,
    override val repeatCount: Int = codec.repetitionCount,
    override val cacheDecodeTimeoutFrame: Boolean = false,
) : AnimatedImage {

    private val imageInfo = ImageInfo(colorInfo, codec.width, codec.height)

    override val frameCount: Int = codec.frameCount
    override val frameDurations: Array<Int> by lazy {
        codec.framesInfo
            .map { it.duration.takeIf { it > 0 } ?: 100 }
            .toTypedArray()
    }

    override var animatedTransformation: ((Any, Rect) -> Unit)? = null
    override var animationStartCallback: (() -> Unit)? = null
    override var animationEndCallback: (() -> Unit)? = null

    override fun createFrameBitmap(width: Int, height: Int): Bitmap =
        createBitmap(ImageInfo(colorInfo, width, height))

    override fun readFrame(bitmap: Bitmap, frameIndex: Int) {
        codec.readPixels(bitmap, frameIndex)
    }

    override val width: Int = codec.width

    override val height: Int = codec.height

    override val byteCount: Long = imageInfo.bytesPerPixel.toLong() * width * height

    override val shareable: Boolean = false

    override fun checkValid(): Boolean = true

    override fun toString(): String = "SkiaAnimatedImage(" +
            "image=${codec.toLogString()}, " +
            "colorInfo=${colorInfo.toLogString()}, " +
            "repeatCount=$repeatCount, " +
            "cacheDecodeTimeoutFrame=$cacheDecodeTimeoutFrame" +
            ")"
}