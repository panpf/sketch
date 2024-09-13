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

@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package com.github.panpf.sketch

import com.github.panpf.sketch.cache.MemoryCache.Value
import com.github.panpf.sketch.util.toLogString

actual data class BitmapImage(
    actual val bitmap: Bitmap,
    actual override val shareable: Boolean = bitmap.isImmutable
) : Image {

    actual override val width: Int = bitmap.width

    actual override val height: Int = bitmap.height

    actual override val byteCount: Long = (bitmap.rowBytes * bitmap.height).toLong()

    actual override val allocationByteCount: Long = byteCount

    actual override fun cacheValue(extras: Map<String, Any?>?): Value? = null

    actual override fun checkValid(): Boolean = true

    override fun transformer(): ImageTransformer = SkiaBitmapImageTransformer

    override fun toString(): String =
        "SkiaBitmapImage(bitmap=${bitmap.toLogString()}, shareable=$shareable)"
}

actual typealias Bitmap = org.jetbrains.skia.Bitmap

actual fun Bitmap.asImage(): BitmapImage {
    return BitmapImage(this)
}