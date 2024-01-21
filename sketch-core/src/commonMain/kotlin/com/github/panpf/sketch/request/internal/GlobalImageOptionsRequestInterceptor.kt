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

import com.github.panpf.sketch.request.ImageData
import com.github.panpf.sketch.request.RequestInterceptor
import com.github.panpf.sketch.request.RequestInterceptor.Chain

class GlobalImageOptionsRequestInterceptor : RequestInterceptor {

    override val key: String? = null

    override val sortWeight: Int = 80

    override suspend fun intercept(chain: Chain): Result<ImageData> {
        val request = chain.request
        val defaultImageOptions = request.defaultOptions
        val globalImageOptions = chain.sketch.globalImageOptions
        return if (globalImageOptions != null) {
            val newDefaultOptions =
                if (defaultImageOptions != null && defaultImageOptions !== globalImageOptions) {
                    defaultImageOptions.merged(globalImageOptions)
                } else {
                    globalImageOptions
                }
            chain.proceed(request.newBuilder().default(newDefaultOptions).build())
        } else {
            chain.proceed(request)
        }
    }

    override fun toString(): String = "GlobalImageOptionsRequestInterceptor(sortWeight=$sortWeight)"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        return true
    }

    override fun hashCode(): Int {
        return javaClass.hashCode()
    }
}