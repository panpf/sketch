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
package com.github.panpf.sketch.sample.ui.components.zoomimage.core

import com.github.panpf.sketch.Image
import com.github.panpf.sketch.ImageTransformer
import com.github.panpf.sketch.cache.MemoryCache.Value
import com.github.panpf.sketch.resize.internal.ResizeMapping

fun BufferedBitmap.asSketchImage(): BufferedBitmapImage = BufferedBitmapImage(this)

data class BufferedBitmapImage(
    val bitmap: BufferedBitmap,
    override val shareable: Boolean = true
) : Image {

    override val width: Int = bitmap.width

    override val height: Int = bitmap.height

    override val byteCount: Long = width * height * (bitmap.colorModel.pixelSize / 8L)

    override val allocationByteCount: Long = byteCount

    override fun cacheValue(extras: Map<String, Any?>?): Value? = null

    override fun checkValid(): Boolean = true

    override fun transformer(): ImageTransformer = BufferedBitmapTransformer()

    override fun getPixels(): IntArray = bitmap.readPixels()

    override fun toString(): String =
        "BufferedBitmapImage(bitmap=${bitmap.toLogString()}, shareable=$shareable)"
}

class BufferedBitmapTransformer : ImageTransformer {

    override fun scale(image: Image, scaleFactor: Float): Image {
        val inputBitmap = (image as BufferedBitmapImage).bitmap
        val outBitmap = inputBitmap.scaled(scaleFactor)
        return outBitmap.asSketchImage()
    }

    override fun mapping(image: Image, mapping: ResizeMapping): Image {
        val inputBitmap = (image as BufferedBitmapImage).bitmap
        val outBitmap = inputBitmap.mapping(mapping)
        return outBitmap.asSketchImage()
    }
}