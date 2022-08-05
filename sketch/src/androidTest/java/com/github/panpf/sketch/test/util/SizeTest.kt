/*
 * Copyright (C) 2022 panpf <panpfpanpf@outlook.com>
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
package com.github.panpf.sketch.test.util

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.util.Size
import com.github.panpf.sketch.util.isNotEmpty
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SizeTest {

    @Test
    fun testWidthHeight() {
        Size(13, 56).apply {
            Assert.assertEquals(13, width)
            Assert.assertEquals(56, height)
        }

        Size(684, 4234).apply {
            Assert.assertEquals(684, width)
            Assert.assertEquals(4234, height)
        }
    }

    @Test
    fun testEmptyNotEmpty() {
        Size(0, 0).apply {
            Assert.assertTrue(isEmpty)
            Assert.assertFalse(isNotEmpty)
        }
        Size(0, 45).apply {
            Assert.assertTrue(isEmpty)
            Assert.assertFalse(isNotEmpty)
        }
        Size(45, 0).apply {
            Assert.assertTrue(isEmpty)
            Assert.assertFalse(isNotEmpty)
        }
        Size(684, 4234).apply {
            Assert.assertFalse(isEmpty)
            Assert.assertTrue(isNotEmpty)
        }
    }

    @Test
    fun testComponent() {
        val (width, height) = Size(13, 56)
        Assert.assertEquals(13, width)
        Assert.assertEquals(56, height)

        val (width1, height1) = Size(684, 4234)
        Assert.assertEquals(684, width1)
        Assert.assertEquals(4234, height1)
    }

    @Test
    fun testToString() {
        Size(13, 56).apply {
            Assert.assertEquals("13x56", toString())
        }
        Size(684, 4234).apply {
            Assert.assertEquals("684x4234", toString())
        }
    }

    @Test
    fun testEquals() {
        val size1 = Size(13, 56)
        val size11 = Size(13, 56)
        val size2 = Size(684, 4234)
        val size21 = Size(684, 4234)

        Assert.assertNotSame(size1, size11)
        Assert.assertNotSame(size2, size21)

        Assert.assertEquals(size1, size11)
        Assert.assertEquals(size2, size21)

        Assert.assertNotEquals(size1, size2)
    }

    @Test
    fun testHashCode() {
        val size1 = Size(13, 56)
        val size11 = Size(13, 56)
        val size2 = Size(684, 4234)
        val size21 = Size(684, 4234)

        Assert.assertEquals(size1.hashCode(), size11.hashCode())
        Assert.assertEquals(size2.hashCode(), size21.hashCode())

        Assert.assertNotEquals(size1.hashCode(), size2.hashCode())
    }

    @Test
    fun testParseSize() {
        Size.parseSize("13x56").apply {
            Assert.assertEquals("13x56", toString())
        }
        Size.parseSize("684x4234").apply {
            Assert.assertEquals("684x4234", toString())
        }
    }
}