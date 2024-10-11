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

import com.github.panpf.sketch.resize.DefaultLongImageDecider
import com.github.panpf.sketch.resize.LongImageDecider
import com.github.panpf.sketch.test.utils.isLongImage
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class LongImageDeciderTest {

    @Test
    fun testCreateFunction() {
        assertTrue(LongImageDecider() is DefaultLongImageDecider)
    }

    @Test
    fun testConstructor() {
        DefaultLongImageDecider().apply {
            assertEquals(2.5f, sameDirectionMultiple, 0.0f)
            assertEquals(5.0f, notSameDirectionMultiple, 0.0f)
        }

        DefaultLongImageDecider(
            sameDirectionMultiple = 3.2f,
            notSameDirectionMultiple = 6.1f
        ).apply {
            assertEquals(3.2f, sameDirectionMultiple, 0.0f)
            assertEquals(6.1f, notSameDirectionMultiple, 0.0f)
        }
    }

    @Test
    fun testKey() {
        DefaultLongImageDecider().apply {
            assertEquals("Default(2.5,5.0)", key)
        }
        DefaultLongImageDecider(4f, 10f).apply {
            assertEquals("Default(4.0,10.0)", key)
        }
    }

    @Test
    fun testIsLongImage() {
        val longImageDecider = DefaultLongImageDecider()

        /* Either one is a square */
        assertTrue(longImageDecider.isLongImage(150, 58, 50, 50))
        assertTrue(longImageDecider.isLongImage(150, 59, 50, 50))
        assertTrue(longImageDecider.isLongImage(150, 60, 50, 50))
        assertFalse(longImageDecider.isLongImage(150, 61, 50, 50))
        assertFalse(longImageDecider.isLongImage(150, 62, 50, 50))

        assertTrue(longImageDecider.isLongImage(50, 50, 150, 58))
        assertTrue(longImageDecider.isLongImage(50, 50, 150, 59))
        assertTrue(longImageDecider.isLongImage(50, 50, 150, 60))
        assertFalse(longImageDecider.isLongImage(50, 50, 150, 61))
        assertFalse(longImageDecider.isLongImage(50, 50, 150, 62))

        /* They go in the same direction */
        assertTrue(longImageDecider.isLongImage(200, 48, 80, 50))
        assertTrue(longImageDecider.isLongImage(200, 49, 80, 50))
        assertTrue(longImageDecider.isLongImage(200, 50, 80, 50))
        assertFalse(longImageDecider.isLongImage(200, 51, 80, 50))
        assertFalse(longImageDecider.isLongImage(200, 52, 80, 50))

        assertTrue(longImageDecider.isLongImage(49, 200, 50, 80))
        assertTrue(longImageDecider.isLongImage(50, 200, 50, 80))
        assertTrue(longImageDecider.isLongImage(51, 200, 50, 80))
        assertFalse(longImageDecider.isLongImage(52, 200, 50, 80))
        assertFalse(longImageDecider.isLongImage(53, 200, 50, 80))

        /* They don't go in the same direction */
        assertTrue(longImageDecider.isLongImage(200, 61, 50, 80))
        assertTrue(longImageDecider.isLongImage(200, 62, 50, 80))
        assertTrue(longImageDecider.isLongImage(200, 63, 50, 80))
        assertFalse(longImageDecider.isLongImage(200, 65, 50, 80))
        assertFalse(longImageDecider.isLongImage(200, 66, 50, 80))

        assertTrue(longImageDecider.isLongImage(63, 200, 80, 50))
        assertTrue(longImageDecider.isLongImage(64, 200, 80, 50))
        assertTrue(longImageDecider.isLongImage(65, 200, 80, 50))
        assertFalse(longImageDecider.isLongImage(66, 200, 80, 50))
        assertFalse(longImageDecider.isLongImage(67, 200, 80, 50))
    }

    @Test
    fun testEqualsAndHashCode() {
        val element1 = DefaultLongImageDecider()
        val element11 = DefaultLongImageDecider()
        val element2 = DefaultLongImageDecider(sameDirectionMultiple = 3f)
        val element3 = DefaultLongImageDecider(notSameDirectionMultiple = 6f)

        assertEquals(element1, element11)
        assertNotEquals(element1, element2)
        assertNotEquals(element1, element3)
        assertNotEquals(element2, element3)
        assertNotEquals(element1, null as Any?)
        assertNotEquals(element1, Any())

        assertEquals(element1.hashCode(), element11.hashCode())
        assertNotEquals(element1.hashCode(), element2.hashCode())
        assertNotEquals(element1.hashCode(), element3.hashCode())
        assertNotEquals(element2.hashCode(), element3.hashCode())
    }

    @Test
    fun testToString() {
        DefaultLongImageDecider().apply {
            assertEquals(
                "DefaultLongImageDecider(sameDirectionMultiple=2.5, notSameDirectionMultiple=5.0)",
                toString()
            )
        }
        DefaultLongImageDecider(4f, 10f).apply {
            assertEquals(
                "DefaultLongImageDecider(sameDirectionMultiple=4.0, notSameDirectionMultiple=10.0)",
                toString()
            )
        }
    }
}