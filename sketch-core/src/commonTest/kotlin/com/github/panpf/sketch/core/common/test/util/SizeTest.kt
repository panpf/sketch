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

package com.github.panpf.sketch.core.common.test.util

import com.github.panpf.sketch.util.Size
import com.github.panpf.sketch.util.isNotEmpty
import com.github.panpf.sketch.util.isSameAspectRatio
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertNotSame
import kotlin.test.assertTrue

class SizeTest {

    @Test
    fun testWidthHeight() {
        Size(13, 56).apply {
            assertEquals(13, width)
            assertEquals(56, height)
        }

        Size(684, 4234).apply {
            assertEquals(684, width)
            assertEquals(4234, height)
        }
    }

    @Test
    fun testEmptyNotEmpty() {
        Size(0, 0).apply {
            assertTrue(isEmpty)
            assertFalse(isNotEmpty)
        }
        Size(0, 45).apply {
            assertTrue(isEmpty)
            assertFalse(isNotEmpty)
        }
        Size(45, 0).apply {
            assertTrue(isEmpty)
            assertFalse(isNotEmpty)
        }
        Size(684, 4234).apply {
            assertFalse(isEmpty)
            assertTrue(isNotEmpty)
        }
    }

    @Test
    fun testComponent() {
        val (width, height) = Size(13, 56)
        assertEquals(13, width)
        assertEquals(56, height)

        val (width1, height1) = Size(684, 4234)
        assertEquals(684, width1)
        assertEquals(4234, height1)
    }

    @Test
    fun testIsSameAspectRatio() {
        assertTrue(Size(400, 200).isSameAspectRatio(Size(400, 200)))
        assertTrue(Size(400, 200).isSameAspectRatio(Size(200, 100)))
        assertFalse(Size(400, 200).isSameAspectRatio(Size(200, 99)))
        assertTrue(Size(400, 200).isSameAspectRatio(Size(200, 99), delta = 0.1f))
        assertFalse(Size(400, 200).isSameAspectRatio(Size(200, 92), delta = 0.1f))
    }

    @Test
    fun testToString() {
        Size(13, 56).apply {
            assertEquals("13x56", toString())
        }
        Size(684, 4234).apply {
            assertEquals("684x4234", toString())
        }
    }

    @Test
    fun testEquals() {
        val size1 = Size(13, 56)
        val size11 = Size(13, 56)
        val size2 = Size(684, 4234)
        val size21 = Size(684, 4234)

        assertNotSame(size1, size11)
        assertNotSame(size2, size21)

        assertEquals(size1, size11)
        assertEquals(size2, size21)

        assertNotEquals(size1, size2)
    }

    @Test
    fun testHashCode() {
        val size1 = Size(13, 56)
        val size11 = Size(13, 56)
        val size2 = Size(684, 4234)
        val size21 = Size(684, 4234)

        assertEquals(size1.hashCode(), size11.hashCode())
        assertEquals(size2.hashCode(), size21.hashCode())

        assertNotEquals(size1.hashCode(), size2.hashCode())
    }

    @Test
    fun testParseSize() {
        Size.parseSize("13x56").apply {
            assertEquals("13x56", toString())
        }
        Size.parseSize("684x4234").apply {
            assertEquals("684x4234", toString())
        }
    }

    @Test
    fun testTimes() {
        // TODO test
    }

    @Test
    fun testDiv() {
        // TODO test
    }

    @Test
    fun testRotate() {
        // TODO test
    }

    @Test
    fun testCoerceAtLeast() {
        // TODO test
    }

    @Test
    fun testCoerceAtMost() {
        // TODO test
    }
}