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
import com.github.panpf.sketch.request.internal.RequestContext
import com.github.panpf.sketch.util.Size

actual interface Image {

    /** The width of the image in pixels. */
    actual val width: Int

    /** The height of the image in pixels. */
    actual val height: Int

    /** Returns the minimum number of bytes that can be used to store this bitmap's pixels. */
    actual val byteCount: Int

    /** Returns the size of the allocated memory used to store this bitmap's pixels.. */
    actual val allocationByteCount: Int

    /**
     * True if the image can be shared between multiple [Target]s at the same time.
     *
     * For example, a bitmap can be shared between multiple targets if it's immutable.
     * Conversely, an animated image cannot be shared as its internal state is being mutated while
     * its animation is running.
     */
    actual val shareable: Boolean

    actual fun cacheValue(
        requestContext: RequestContext,
        extras: Map<String, Any?>
    ): Value?

    actual fun checkValid(): Boolean
}