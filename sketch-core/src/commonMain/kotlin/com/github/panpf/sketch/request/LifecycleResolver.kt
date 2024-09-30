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

@file:Suppress("RedundantConstructorKeyword")

package com.github.panpf.sketch.request

import androidx.lifecycle.Lifecycle
import kotlin.js.JsName

/**
 * Lifecycle resolver
 *
 * @see com.github.panpf.sketch.core.common.test.request.LifecycleResolverTest.testLifecycleResolver
 */
fun LifecycleResolver(lifecycle: Lifecycle): LifecycleResolver =
    FixedLifecycleResolver(lifecycle)

/**
 * Lifecycle resolver, used to get the life cycle of the request.
 * The request starts when the life cycle starts and ends when the life cycle ends.
 *
 * IMPORTANT: It is necessary to ensure compliance with the consistency principle,
 * that is, the equals() and hashCode() methods of instances created with the same
 * construction parameters return consistent results. This is important in Compose
 */
interface LifecycleResolver {

    @JsName("getLifecycle")
    suspend fun lifecycle(): Lifecycle

    override fun equals(other: Any?): Boolean

    override fun hashCode(): Int

    override fun toString(): String
}

/**
 * Fixed lifecycle
 *
 * @see com.github.panpf.sketch.core.common.test.request.LifecycleResolverTest.testFixedLifecycleResolver
 */
data class FixedLifecycleResolver constructor(
    private val lifecycle: Lifecycle
) : LifecycleResolver {

    override suspend fun lifecycle(): Lifecycle = lifecycle

    override fun toString(): String = "FixedLifecycleResolver($lifecycle)"
}