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

package com.github.panpf.sketch.core.common.test.resize

import com.github.panpf.sketch.resize.FixedSizeResolver
import com.github.panpf.sketch.resize.SizeResolver
import com.github.panpf.sketch.util.Size
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class SizeResolverTest {

    @Test
    fun testCreateFunction() = runTest {
        SizeResolver(Size(100, 200)).apply {
            assertTrue(this is FixedSizeResolver)
            assertEquals(Size(100, 200), this.size())
        }

        SizeResolver(101, 201).apply {
            assertTrue(this is FixedSizeResolver)
            assertEquals(Size(101, 201), this.size())
        }
    }

    @Test
    fun testFixedSizeResolverSize() = runTest {
        FixedSizeResolver(Size(100, 200)).size().apply {
            assertEquals(Size(100, 200), this)
        }

        FixedSizeResolver(200, 100).size().apply {
            assertEquals(Size(200, 100), this)
        }
    }

    @Test
    fun testFixedSizeResolverEqualsAndHashCode() {
        val element1 = FixedSizeResolver(Size(100, 200))
        val element11 = FixedSizeResolver(Size(100, 200))
        val element2 = FixedSizeResolver(Size(200, 100))

        assertEquals(element1, element11)
        assertNotEquals(element1, element2)
        assertNotEquals(element1, null as Any?)
        assertNotEquals(element1, Any())

        assertEquals(element1.hashCode(), element11.hashCode())
        assertNotEquals(element1.hashCode(), element2.hashCode())
    }

    @Test
    fun testFixedSizeResolverToString() {
        FixedSizeResolver(Size(100, 200)).apply {
            assertEquals("FixedSizeResolver(100x200)", toString())
        }
        FixedSizeResolver(Size(200, 100)).apply {
            assertEquals("FixedSizeResolver(200x100)", toString())
        }
    }
}