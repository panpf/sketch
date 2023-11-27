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
package com.github.panpf.sketch.core.test.resize

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.resize.FixedSizeResolver
import com.github.panpf.sketch.util.Size
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FixedSizeResolverTest {

    @Test
    fun test() {
        runBlocking {
            FixedSizeResolver(Size(100, 200)).size()
        }.apply {
            Assert.assertEquals(Size(100, 200), this)
        }

        runBlocking {
            FixedSizeResolver(200, 100).size()
        }.apply {
            Assert.assertEquals(Size(200, 100), this)
        }
    }

    @Test
    fun testEqualsAndHashCode() {
        val element1 = FixedSizeResolver(Size(100, 200))
        val element11 = FixedSizeResolver(Size(100, 200))
        val element2 = FixedSizeResolver(Size(200, 100))

        Assert.assertNotSame(element1, element11)
        Assert.assertNotSame(element1, element2)
        Assert.assertNotSame(element2, element11)

        Assert.assertEquals(element1, element1)
        Assert.assertEquals(element1, element11)
        Assert.assertNotEquals(element1, element2)
        Assert.assertNotEquals(element2, element11)
        Assert.assertNotEquals(element1, null)
        Assert.assertNotEquals(element1, Any())

        Assert.assertEquals(element1.hashCode(), element1.hashCode())
        Assert.assertEquals(element1.hashCode(), element11.hashCode())
        Assert.assertNotEquals(element1.hashCode(), element2.hashCode())
        Assert.assertNotEquals(element2.hashCode(), element11.hashCode())
    }

    @Test
    fun testToString() {
        FixedSizeResolver(Size(100, 200)).apply {
            Assert.assertEquals("FixedSizeResolver(100x200)", toString())
        }
        FixedSizeResolver(Size(200, 100)).apply {
            Assert.assertEquals("FixedSizeResolver(200x100)", toString())
        }
    }
}