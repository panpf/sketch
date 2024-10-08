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

package com.github.panpf.sketch.internal

import com.github.panpf.sketch.resize.Scale
import com.github.panpf.sketch.resize.ScaleDecider
import com.github.panpf.sketch.util.Size

/**
 * Just to show that it's [ScaleDecider] from AsyncImage
 */
class AsyncImageScaleDecider(val wrapped: ScaleDecider) : ScaleDecider {

    override val key: String
        get() = wrapped.key

    override fun get(imageSize: Size, targetSize: Size): Scale {
        return wrapped.get(imageSize, targetSize)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as AsyncImageScaleDecider
        if (wrapped != other.wrapped) return false
        return true
    }

    override fun hashCode(): Int {
        return wrapped.hashCode()
    }

    override fun toString(): String {
        return "AsyncImageScaleDecider($wrapped)"
    }
}