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

package com.github.panpf.sketch.state

import com.github.panpf.sketch.Image
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.request.ImageRequest

/**
 * Use current [Image] as the state [Image]
 */
class CurrentStateImage(
    val defaultImage: StateImage? = null
) : StateImage {

    override val key: String = "CurrentStateImage(${defaultImage?.key})"

    override fun getImage(
        sketch: Sketch,
        request: ImageRequest,
        throwable: Throwable?
    ): Image? {
        val currentImage = request.target?.currentImage
        if (currentImage != null) {
            return currentImage
        }
        val image = defaultImage?.getImage(sketch, request, throwable)
        return image
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as CurrentStateImage
        if (defaultImage != other.defaultImage) return false
        return true
    }

    override fun hashCode(): Int {
        return defaultImage?.hashCode() ?: 0
    }

    override fun toString(): String {
        return "CurrentStateImage($defaultImage)"
    }
}