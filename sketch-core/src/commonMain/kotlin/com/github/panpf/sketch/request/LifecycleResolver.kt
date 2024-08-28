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

package com.github.panpf.sketch.request

import androidx.lifecycle.Lifecycle
import kotlin.js.JsName

fun LifecycleResolver(lifecycle: Lifecycle): LifecycleResolver =
    FixedLifecycleResolver(lifecycle)

/**
 * IMPORTANT: It is necessary to ensure compliance with the consistency principle,
 * that is, the equals() and hashCode() methods of instances created with the same
 * construction parameters return consistent results. This is important in Compose
 */
fun interface LifecycleResolver {

    @JsName("getLifecycle")
    suspend fun lifecycle(): Lifecycle
}

class FixedLifecycleResolver constructor(
    val lifecycle: Lifecycle
) : LifecycleResolver {

    override suspend fun lifecycle(): Lifecycle = lifecycle

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is FixedLifecycleResolver) return false
        return lifecycle == other.lifecycle
    }

    override fun hashCode(): Int {
        return lifecycle.hashCode()
    }

    override fun toString(): String = "FixedLifecycleResolver($lifecycle)"
}