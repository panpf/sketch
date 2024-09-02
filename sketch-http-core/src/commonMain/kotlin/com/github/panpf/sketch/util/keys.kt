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

interface Key {
    val key: String
}

interface NullableKey {
    val key: String?
}

/**
 * If the incoming object is a [Key], the [Key.key] value is returned, otherwise the [Any.toString] value is returned
 *
 * @see com.github.panpf.sketch.http.core.common.test.util.KeysTest.testKey
 */
fun key(it: Any): String {
    return when (it) {
        is Key -> it.key
        is NullableKey -> it.key ?: it.toString()
        else -> it.toString()
    }
}

/**
 * If the incoming object is a [Key] or [NullableKey], the [Key.key] or [NullableKey.key] value is returned, otherwise the [Any.toString] value is returned
 *
 * @see com.github.panpf.sketch.http.core.common.test.util.KeysTest.testKeyOrNull
 */
fun keyOrNull(it: Any?): String? {
    return when (it) {
        is Key -> it.key
        is NullableKey -> it.key
        else -> it?.toString()
    }
}