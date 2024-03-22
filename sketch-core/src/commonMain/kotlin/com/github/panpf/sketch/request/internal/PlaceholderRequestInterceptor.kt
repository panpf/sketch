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
package com.github.panpf.sketch.request.internal

import com.github.panpf.sketch.annotation.MainThread
import com.github.panpf.sketch.request.ImageData
import com.github.panpf.sketch.request.RequestInterceptor
import com.github.panpf.sketch.resize.resizeOnDraw

/**
 * Initialize the placeholder image and fill it into the target
 *
 * [PlaceholderRequestInterceptor] must be executed after [MemoryCacheRequestInterceptor]
 * * First, when the memory cache is valid, one callback can be reduced
 * * Secondly, when RecyclerView executes notifyDataSetChanged(),
 * it can avoid the flickering phenomenon caused by the fast switching of the picture between placeholder and result
 */
class PlaceholderRequestInterceptor : RequestInterceptor {

    override val key: String? = null

    override val sortWeight: Int = 95

    @MainThread
    override suspend fun intercept(chain: RequestInterceptor.Chain): Result<ImageData> {
        val sketch = chain.sketch
        val request = chain.request
        val requestContext = chain.requestContext
        val target = request.target
        if (target != null) {
            val placeholderDrawable = request.placeholder
                ?.getImage(sketch, request, null)
                ?.resizeOnDraw(request, requestContext.size)
            target.onStart(requestContext, placeholderDrawable)
        }
        return chain.proceed(request)
    }

    @Suppress("RedundantOverride")
    override fun equals(other: Any?): Boolean {
        // If you add construction parameters to this class, you need to change it here
        return super.equals(other)
    }

    @Suppress("RedundantOverride")
    override fun hashCode(): Int {
        // If you add construction parameters to this class, you need to change it here
        return super.hashCode()
    }

    override fun toString(): String = "PlaceholderRequestInterceptor(sortWeight=$sortWeight)"
}