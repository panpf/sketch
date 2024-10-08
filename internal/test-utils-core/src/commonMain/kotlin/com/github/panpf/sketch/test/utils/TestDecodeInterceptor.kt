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

package com.github.panpf.sketch.test.utils

import com.github.panpf.sketch.decode.DecodeInterceptor
import com.github.panpf.sketch.decode.DecodeResult

class TestDecodeInterceptor(override val sortWeight: Int = 0) : DecodeInterceptor {

    override val key: String = "TestDecodeInterceptor"

    override suspend fun intercept(chain: DecodeInterceptor.Chain): Result<DecodeResult> {
        val decodeResult = chain.proceed().let {
            it.getOrNull() ?: return it
        }
        val newDecodeResult = decodeResult.newResult {
            addTransformed("TestDecodeInterceptor")
        }
        return Result.success(newDecodeResult)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as TestDecodeInterceptor
        if (sortWeight != other.sortWeight) return false
        return true
    }

    override fun hashCode(): Int {
        return sortWeight.hashCode()
    }

    override fun toString(): String {
        return "TestDecodeInterceptor(sortWeight=$sortWeight)"
    }
}