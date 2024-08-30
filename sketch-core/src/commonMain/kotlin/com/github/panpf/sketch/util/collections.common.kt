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

/**
 * Create a [MutableMap] that orders its entries by most recently used to least recently used.
 *
 * https://youtrack.jetbrains.com/issue/KT-52183
 *
 * @see com.github.panpf.sketch.core.jvmcommon.test.util.CollectionsJvmTest.testLruMutableMap
 * @see com.github.panpf.sketch.core.nonjvmcommon.test.util.CollectionsNonJvmTest.testLruMutableMap
 */
internal expect fun <K : Any, V : Any> LruMutableMap(
    initialCapacity: Int = 0,
    loadFactor: Float = 0.75F,
): MutableMap<K, V>

/**
 * Traverse the collection by index
 *
 * @see com.github.panpf.sketch.core.common.test.util.CollectionsTest.testForEachIndices
 */
internal inline fun <T> List<T>.forEachIndices(action: (T) -> Unit) {
    for (i in indices) {
        action(get(i))
    }
}

/**
 * Traverse the collection by index
 *
 * @see com.github.panpf.sketch.core.common.test.util.CollectionsTest.testForEachIndexedIndices
 */
internal inline fun <T> List<T>.forEachIndexedIndices(action: (index: Int, T) -> Unit) {
    for (i in indices) {
        action(i, get(i))
    }
}