/*
 * Copyright (C) 2023 panpf <panpfpanpf@outlook.com>
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

import com.github.panpf.sketch.cache.MemoryCache.Value
import com.github.panpf.sketch.resize.internal.ResizeMapping
import com.github.panpf.sketch.util.asOrThrow
import com.github.panpf.sketch.util.mapping
import com.github.panpf.sketch.util.readPixels
import com.github.panpf.sketch.util.scaled
import com.github.panpf.sketch.util.toLogString

fun JvmBitmap.asSketchImage(): JvmBitmapImage = JvmBitmapImage(this)

data class JvmBitmapImage(
    val bitmap: JvmBitmap,
    override val shareable: Boolean = true
) : Image {

    override val width: Int = bitmap.width

    override val height: Int = bitmap.height

    override val byteCount: Long = width * height * (bitmap.colorModel.pixelSize / 8L)

    override val allocationByteCount: Long = byteCount

    override fun cacheValue(extras: Map<String, Any?>?): Value? = null

    override fun checkValid(): Boolean = true

    override fun transformer(): ImageTransformer = JvmBitmapTransformer()

    override fun getPixels(): IntArray = bitmap.readPixels()

    override fun toString(): String =
        "JvmBitmapImage(bitmap=${bitmap.toLogString()}, shareable=$shareable)"
}

class JvmBitmapTransformer : ImageTransformer {

    override fun scale(image: Image, scaleFactor: Float): Image {
        val inputBitmap = image.asOrThrow<JvmBitmapImage>().bitmap
        val outBitmap = inputBitmap.scaled(scaleFactor)
        return outBitmap.asSketchImage()
    }

    override fun mapping(image: Image, mapping: ResizeMapping): Image {
        val inputBitmap = image.asOrThrow<JvmBitmapImage>().bitmap
        val outBitmap = inputBitmap.mapping(mapping)
        return outBitmap.asSketchImage()
    }
}