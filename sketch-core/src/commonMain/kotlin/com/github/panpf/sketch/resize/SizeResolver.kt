/*
 * Copyright (C) 2022 panpf <panpfpanpf@outlook.com>
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
package com.github.panpf.sketch.resize

import androidx.annotation.MainThread
import com.github.panpf.sketch.Key
import com.github.panpf.sketch.util.Size

fun SizeResolver(size: Size): SizeResolver = FixedSizeResolver(size)

/**
 * An interface for measuring the target size for an image request.
 *
 * @see com.github.panpf.sketch.request.ImageRequest.Builder.resizeSize
 */
interface SizeResolver : Key {

    /** Return the [Size] that the image should be loaded at. */
    @MainThread
    suspend fun size(): Size
}

/**
 * Returns the fixed size
 */
data class FixedSizeResolver constructor(private val size: Size) : SizeResolver {

    constructor(width: Int, height: Int) : this(Size(width, height))

    override val key: String by lazy { "Fixed($size)" }

    override suspend fun size(): Size = size

    override fun toString(): String = "FixedSizeResolver($size)"
}