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
package com.github.panpf.sketch.core.android.test.resize

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.resize.DefaultLongImageDecider
import com.github.panpf.sketch.resize.LongImageDecider
import com.github.panpf.sketch.test.utils.isLongImage
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LongImageDeciderTest {

    @Test
    fun testCreateFunction() {
        Assert.assertTrue(LongImageDecider() is DefaultLongImageDecider)
    }

    @Test
    fun testConstructor() {
        DefaultLongImageDecider().apply {
            Assert.assertEquals(2.5f, sameDirectionMultiple, 0.0f)
            Assert.assertEquals(5.0f, notSameDirectionMultiple, 0.0f)
        }

        DefaultLongImageDecider(
            sameDirectionMultiple = 3.2f,
            notSameDirectionMultiple = 6.1f
        ).apply {
            Assert.assertEquals(3.2f, sameDirectionMultiple, 0.0f)
            Assert.assertEquals(6.1f, notSameDirectionMultiple, 0.0f)
        }
    }

    @Test
    fun test() {
        // TODO Because the format function has changed, the test cases here need to change as well
        val longImageDecider = DefaultLongImageDecider()

        /* Either one is a square */
        Assert.assertTrue(longImageDecider.isLongImage(150, 58, 50, 50))
        Assert.assertTrue(longImageDecider.isLongImage(150, 59, 50, 50))
        Assert.assertTrue(longImageDecider.isLongImage(150, 60, 50, 50))
        Assert.assertFalse(longImageDecider.isLongImage(150, 61, 50, 50))
        Assert.assertFalse(longImageDecider.isLongImage(150, 62, 50, 50))

        Assert.assertTrue(longImageDecider.isLongImage(50, 50, 150, 58))
        Assert.assertTrue(longImageDecider.isLongImage(50, 50, 150, 59))
        Assert.assertTrue(longImageDecider.isLongImage(50, 50, 150, 60))
        Assert.assertFalse(longImageDecider.isLongImage(50, 50, 150, 61))
        Assert.assertFalse(longImageDecider.isLongImage(50, 50, 150, 62))

        /* They go in the same direction */
        Assert.assertTrue(longImageDecider.isLongImage(200, 48, 80, 50))
        Assert.assertTrue(longImageDecider.isLongImage(200, 49, 80, 50))
        Assert.assertTrue(longImageDecider.isLongImage(200, 50, 80, 50))
        Assert.assertFalse(longImageDecider.isLongImage(200, 51, 80, 50))
        Assert.assertFalse(longImageDecider.isLongImage(200, 52, 80, 50))

        Assert.assertTrue(longImageDecider.isLongImage(49, 200, 50, 80))
        Assert.assertTrue(longImageDecider.isLongImage(50, 200, 50, 80))
        Assert.assertTrue(longImageDecider.isLongImage(51, 200, 50, 80))
        Assert.assertFalse(longImageDecider.isLongImage(52, 200, 50, 80))
        Assert.assertFalse(longImageDecider.isLongImage(53, 200, 50, 80))

        /* They don't go in the same direction */
        Assert.assertTrue(longImageDecider.isLongImage(200, 61, 50, 80))
        Assert.assertTrue(longImageDecider.isLongImage(200, 62, 50, 80))
        Assert.assertTrue(longImageDecider.isLongImage(200, 63, 50, 80))
        Assert.assertFalse(longImageDecider.isLongImage(200, 64, 50, 80))
        Assert.assertFalse(longImageDecider.isLongImage(200, 65, 50, 80))

        Assert.assertTrue(longImageDecider.isLongImage(63, 200, 80, 50))
        Assert.assertTrue(longImageDecider.isLongImage(64, 200, 80, 50))
        Assert.assertTrue(longImageDecider.isLongImage(65, 200, 80, 50))
        Assert.assertFalse(longImageDecider.isLongImage(66, 200, 80, 50))
        Assert.assertFalse(longImageDecider.isLongImage(67, 200, 80, 50))
    }

    @Test
    fun testKey() {
        DefaultLongImageDecider().apply {
            Assert.assertEquals("Default(2.5,5.0)", key)
        }
        DefaultLongImageDecider(4f, 10f).apply {
            Assert.assertEquals("Default(4.0,10.0)", key)
        }
    }

    @Test
    fun testEqualsAndHashCode() {
        val element1 = DefaultLongImageDecider()
        val element11 = DefaultLongImageDecider()
        val element2 = DefaultLongImageDecider(sameDirectionMultiple = 3f)
        val element3 = DefaultLongImageDecider(notSameDirectionMultiple = 6f)

        Assert.assertNotSame(element1, element11)
        Assert.assertNotSame(element1, element2)
        Assert.assertNotSame(element1, element3)
        Assert.assertNotSame(element2, element11)
        Assert.assertNotSame(element2, element3)

        Assert.assertEquals(element1, element1)
        Assert.assertEquals(element1, element11)
        Assert.assertNotEquals(element1, element2)
        Assert.assertNotEquals(element1, element3)
        Assert.assertNotEquals(element2, element11)
        Assert.assertNotEquals(element2, element3)
        Assert.assertNotEquals(element1, null)
        Assert.assertNotEquals(element1, Any())

        Assert.assertEquals(element1.hashCode(), element1.hashCode())
        Assert.assertEquals(element1.hashCode(), element11.hashCode())
        Assert.assertNotEquals(element1.hashCode(), element2.hashCode())
        Assert.assertNotEquals(element1.hashCode(), element3.hashCode())
        Assert.assertNotEquals(element2.hashCode(), element11.hashCode())
        Assert.assertNotEquals(element2.hashCode(), element3.hashCode())
    }

    @Test
    fun testToString() {
        DefaultLongImageDecider().apply {
            Assert.assertEquals(
                "DefaultLongImageDecider(sameDirectionMultiple=2.5, notSameDirectionMultiple=5.0)",
                toString()
            )
        }
        DefaultLongImageDecider(4f, 10f).apply {
            Assert.assertEquals(
                "DefaultLongImageDecider(sameDirectionMultiple=4.0, notSameDirectionMultiple=10.0)",
                toString()
            )
        }
    }
}