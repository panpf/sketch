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

package com.github.panpf.sketch.core.test.cache

import com.github.panpf.sketch.cache.CachePolicy
import com.github.panpf.sketch.cache.isReadAndWrite
import com.github.panpf.sketch.cache.isReadOrWrite
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class CachePolicyTest {

    @Test
    fun testReadEnabled() {
        assertTrue(CachePolicy.ENABLED.readEnabled)
        assertTrue(CachePolicy.READ_ONLY.readEnabled)
        assertFalse(CachePolicy.WRITE_ONLY.readEnabled)
        assertFalse(CachePolicy.DISABLED.readEnabled)
    }

    @Test
    fun testWriteEnabled() {
        assertTrue(CachePolicy.ENABLED.writeEnabled)
        assertFalse(CachePolicy.READ_ONLY.writeEnabled)
        assertTrue(CachePolicy.WRITE_ONLY.writeEnabled)
        assertFalse(CachePolicy.DISABLED.writeEnabled)
    }

    @Test
    fun testIsReadOrWrite() {
        assertTrue(CachePolicy.ENABLED.isReadOrWrite)
        assertTrue(CachePolicy.READ_ONLY.isReadOrWrite)
        assertTrue(CachePolicy.WRITE_ONLY.isReadOrWrite)
        assertFalse(CachePolicy.DISABLED.isReadOrWrite)
    }

    @Test
    fun testIsReadAndWrite() {
        assertTrue(CachePolicy.ENABLED.isReadAndWrite)
        assertFalse(CachePolicy.READ_ONLY.isReadAndWrite)
        assertFalse(CachePolicy.WRITE_ONLY.isReadAndWrite)
        assertFalse(CachePolicy.DISABLED.isReadAndWrite)
    }
}