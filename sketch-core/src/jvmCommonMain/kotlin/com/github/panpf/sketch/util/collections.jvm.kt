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

package com.github.panpf.sketch.util

import java.util.Collections

/**
 * Create a [MutableMap] that orders its entries by most recently used to least recently used.
 *
 * @see com.github.panpf.sketch.core.jvmcommon.test.util.CollectionsJvmTest.testLruMutableMap
 */
internal actual fun <K : Any, V : Any> LruMutableMap(
    initialCapacity: Int,
    loadFactor: Float,
): MutableMap<K, V> = LinkedHashMap(initialCapacity, loadFactor, true)

/**
 * Convert this [Map] to an immutable [Map].
 *
 * @see com.github.panpf.sketch.core.jvmcommon.test.util.CollectionsJvmTest.testToImmutableMap
 */
internal actual fun <K, V> Map<K, V>.toImmutableMap(): Map<K, V> = when (size) {
    0 -> emptyMap()
    1 -> entries.first().let { (key, value) -> Collections.singletonMap(key, value) }
    else -> Collections.unmodifiableMap(LinkedHashMap(this))
}

/**
 * Convert this [List] to an immutable [List].
 *
 * @see com.github.panpf.sketch.core.jvmcommon.test.util.CollectionsJvmTest.testToImmutableList
 */
internal actual fun <T> List<T>.toImmutableList(): List<T> = when (size) {
    0 -> emptyList()
    1 -> Collections.singletonList(first())
    else -> Collections.unmodifiableList(ArrayList(this))
}
