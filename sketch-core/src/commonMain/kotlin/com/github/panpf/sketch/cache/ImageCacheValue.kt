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

package com.github.panpf.sketch.cache

import com.github.panpf.sketch.Image

/**
 * Image Cache Value
 *
 * @see com.github.panpf.sketch.core.common.test.cache.ImageCacheValueTest
 */
class ImageCacheValue(
    override val image: Image,
    override val extras: Map<String, Any?>? = null,
) : MemoryCache.Value {

    override val size: Long = image.byteCount

    override fun checkValid(): Boolean {
        return image.checkValid()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as ImageCacheValue
        if (image != other.image) return false
        return extras == other.extras
    }

    override fun hashCode(): Int {
        var result = image.hashCode()
        result = 31 * result + extras.hashCode()
        return result
    }

    override fun toString(): String {
        return "ImageCacheValue(image=${image}, extras=$extras)"
    }
}