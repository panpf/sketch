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

package com.github.panpf.sketch.request.internal

import androidx.compose.ui.graphics.asComposeImageBitmap
import com.github.panpf.sketch.ComposeBitmap
import com.github.panpf.sketch.SkiaBitmapImage
import com.github.panpf.sketch.asSketchImage
import com.github.panpf.sketch.request.ImageData
import com.github.panpf.sketch.request.RequestInterceptor
import com.github.panpf.sketch.request.RequestInterceptor.Chain

object SkiaBitmapToComposeBitmapRequestInterceptor : RequestInterceptor {

    override val key: String? = null

    override val sortWeight: Int = 95

    override suspend fun intercept(chain: Chain): Result<ImageData> {
        val request = chain.request
        val result = chain.proceed(request)
        val imageData = result.getOrNull()
        if (imageData != null) {
            val image = imageData.image
            if (image is SkiaBitmapImage) {
                val composeBitmap: ComposeBitmap = image.bitmap.asComposeImageBitmap()
                val newImage = composeBitmap.asSketchImage()
                return Result.success(imageData.copy(image = newImage))
            }
        }
        return result
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        return other is SkiaBitmapToComposeBitmapRequestInterceptor
    }

    override fun hashCode(): Int {
        return this@SkiaBitmapToComposeBitmapRequestInterceptor::class.hashCode()
    }

    override fun toString(): String =
        "SkiaBitmapToComposeBitmapRequestInterceptor(sortWeight=$sortWeight)"
}