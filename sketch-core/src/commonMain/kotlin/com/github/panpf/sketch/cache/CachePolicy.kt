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

/**
 * Represents the read/write policy for a cache source.
 *
 * @see com.github.panpf.sketch.core.common.test.cache.CachePolicyTest
 */
enum class CachePolicy(
    val readEnabled: Boolean,
    val writeEnabled: Boolean
) {
    /**
     * readable, writable
     */
    ENABLED(true, true),

    /**
     * readable, not writable
     */
    READ_ONLY(true, false),

    /**
     * not readable, writable
     */
    WRITE_ONLY(false, true),

    /**
     * not readable, not writable
     */
    DISABLED(false, false)
}


/**
 * Return true if readable or writable
 */
val CachePolicy.isReadOrWrite: Boolean
    get() = readEnabled || writeEnabled

/**
 * Return true if readable and writable
 */
val CachePolicy.isReadAndWrite: Boolean
    get() = readEnabled && writeEnabled