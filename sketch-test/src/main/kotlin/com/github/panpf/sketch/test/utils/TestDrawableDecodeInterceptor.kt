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
package com.github.panpf.sketch.test.utils

import com.github.panpf.sketch.decode.DrawableDecodeInterceptor
import com.github.panpf.sketch.decode.DrawableDecodeResult

class TestDrawableDecodeInterceptor(override val sortWeight: Int = 0) : DrawableDecodeInterceptor {

    override val key: String = "TestDrawableDecodeInterceptor"

    override suspend fun intercept(chain: DrawableDecodeInterceptor.Chain): Result<DrawableDecodeResult> {
        val decodeResult = chain.proceed().let {
            it.getOrNull() ?: return it
        }
        val newDecodeResult = decodeResult.let {
            it.copy(
                transformedList = (it.transformedList
                    ?: listOf()).plus("TestDrawableDecodeInterceptor")
            )
        }
        return Result.success(newDecodeResult)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as TestDrawableDecodeInterceptor
        if (sortWeight != other.sortWeight) return false
        return true
    }

    override fun hashCode(): Int {
        return sortWeight
    }

    override fun toString(): String {
        return "TestDrawableDecodeInterceptor(sortWeight=$sortWeight)"
    }
}