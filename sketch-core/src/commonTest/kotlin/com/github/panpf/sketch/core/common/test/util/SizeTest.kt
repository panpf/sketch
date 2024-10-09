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
import com.github.panpf.sketch.util.coerceAtLeast
import com.github.panpf.sketch.util.coerceAtMost
import com.github.panpf.sketch.util.div
import com.github.panpf.sketch.util.isNotEmpty
import com.github.panpf.sketch.util.isSameAspectRatio
import com.github.panpf.sketch.util.rotate
import com.github.panpf.sketch.util.times
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
    fun testEquals() {  // TODO testEqualsAndHashCode
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
        assertEquals(
            "43x23",
            (Size(13, 7) * 3.3f).toString()
        )
        assertEquals(
            "69x37",
            (Size(13, 7) * 5.3f).toString()
        )
    }

    @Test
    fun testDiv() {
        assertEquals(
            "13x11",
            (Size(43, 37) / 3.3f).toString()
        )
        assertEquals(
            "8x7",
            (Size(43, 37) / 5.3f).toString()
        )
    }

    @Test
    fun testRotate() {
        listOf(0, 0 - 360, 0 + 360, 0 - 360 - 360).forEach { rotation ->
            assertEquals(
                expected = Size(width = 600, height = 200),
                actual = Size(600, 200).rotate(rotation),
                message = "rotation: $rotation",
            )
        }

        listOf(90, 90 - 360, 90 + 360, 90 - 360 - 360).forEach { rotation ->
            assertEquals(
                expected = Size(width = 200, height = 600),
                actual = Size(600, 200).rotate(rotation),
                message = "rotation: $rotation",
            )
        }

        listOf(180, 180 - 360, 180 + 360, 180 - 360 - 360).forEach { rotation ->
            assertEquals(
                expected = Size(width = 600, height = 200),
                actual = Size(600, 200).rotate(rotation),
                message = "rotation: $rotation",
            )
        }

        listOf(270, 270 - 360, 270 + 360, 270 - 360 - 360).forEach { rotation ->
            assertEquals(
                expected = Size(200, 600),
                actual = Size(600, 200).rotate(rotation),
                message = "rotation: $rotation",
            )
        }

        listOf(360, 360 - 360, 360 + 360, 360 - 360 - 360).forEach { rotation ->
            assertEquals(
                expected = Size(width = 600, height = 200),
                actual = Size(600, 200).rotate(rotation),
                message = "rotation: $rotation",
            )
        }
    }

    @Test
    fun testCoerceAtLeast() {
        assertEquals(
            expected = Size(10, 20),
            actual = Size(5, 20).coerceAtLeast(Size(10, 10))
        )
        assertEquals(
            expected = Size(15, 25),
            actual = Size(15, 25).coerceAtLeast(Size(10, 10))
        )
        assertEquals(
            expected = Size(10, 30),
            actual = Size(10, 30).coerceAtLeast(Size(10, 10))
        )
        assertEquals(
            expected = Size(20, 20),
            actual = Size(20, 5).coerceAtLeast(Size(10, 20))
        )
    }

    @Test
    fun testCoerceAtMost() {
        assertEquals(
            expected = Size(5, 10),
            actual = Size(5, 20).coerceAtMost(Size(10, 10))
        )
        assertEquals(
            expected = Size(10, 10),
            actual = Size(15, 25).coerceAtMost(Size(10, 10))
        )
        assertEquals(
            expected = Size(10, 10),
            actual = Size(10, 30).coerceAtMost(Size(10, 10))
        )
        assertEquals(
            expected = Size(10, 5),
            actual = Size(20, 5).coerceAtMost(Size(10, 20))
        )
    }
}