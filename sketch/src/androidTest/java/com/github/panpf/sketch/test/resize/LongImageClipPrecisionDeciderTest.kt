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
package com.github.panpf.sketch.test.resize

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.resize.DefaultLongImageDecider
import com.github.panpf.sketch.resize.LongImageClipPrecisionDecider
import com.github.panpf.sketch.resize.Precision.EXACTLY
import com.github.panpf.sketch.resize.Precision.LESS_PIXELS
import com.github.panpf.sketch.resize.Precision.SAME_ASPECT_RATIO
import com.github.panpf.sketch.resize.longImageClipPrecision
import com.github.panpf.tools4j.test.ktx.assertThrow
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LongImageClipPrecisionDeciderTest {

    @Test
    fun testCreateFunction() {
        Assert.assertEquals(LongImageClipPrecisionDecider(EXACTLY), longImageClipPrecision(EXACTLY))
        Assert.assertEquals(
            LongImageClipPrecisionDecider(SAME_ASPECT_RATIO),
            longImageClipPrecision(SAME_ASPECT_RATIO)
        )
    }

    @Test
    fun testConstructor() {
        LongImageClipPrecisionDecider().apply {
            Assert.assertEquals(SAME_ASPECT_RATIO, get(150, 48, 50, 50))
        }
        LongImageClipPrecisionDecider(SAME_ASPECT_RATIO).apply {
            Assert.assertEquals(SAME_ASPECT_RATIO, get(150, 48, 50, 50))
        }
        LongImageClipPrecisionDecider(EXACTLY).apply {
            Assert.assertEquals(EXACTLY, get(150, 48, 50, 50))
        }
        assertThrow(IllegalArgumentException::class) {
            LongImageClipPrecisionDecider(LESS_PIXELS)
        }
    }

    @Test
    fun testGet() {
        LongImageClipPrecisionDecider(SAME_ASPECT_RATIO).apply {
            Assert.assertEquals(SAME_ASPECT_RATIO, get(150, 48, 50, 50))
            Assert.assertEquals(SAME_ASPECT_RATIO, get(150, 49, 50, 50))
            Assert.assertEquals(SAME_ASPECT_RATIO, get(150, 50, 50, 50))
            Assert.assertEquals(LESS_PIXELS, get(150, 76, 50, 50))
            Assert.assertEquals(LESS_PIXELS, get(150, 77, 50, 50))
        }

        LongImageClipPrecisionDecider(EXACTLY).apply {
            Assert.assertEquals(EXACTLY, get(150, 48, 50, 50))
            Assert.assertEquals(EXACTLY, get(150, 49, 50, 50))
            Assert.assertEquals(EXACTLY, get(150, 50, 50, 50))
            Assert.assertEquals(LESS_PIXELS, get(150, 76, 50, 50))
            Assert.assertEquals(LESS_PIXELS, get(150, 77, 50, 50))
        }
    }

    @Test
    fun testKey() {
        Assert.assertEquals(
            "LongImageClip(EXACTLY,Default(2.5,5.0))",
            LongImageClipPrecisionDecider(EXACTLY).key
        )
        Assert.assertEquals(
            "LongImageClip(SAME_ASPECT_RATIO,Default(2.5,5.0))",
            LongImageClipPrecisionDecider(SAME_ASPECT_RATIO).key
        )
    }

    @Test
    fun testEqualsAndHashCode() {
        val element1 = LongImageClipPrecisionDecider()
        val element11 = LongImageClipPrecisionDecider()
        val element2 = LongImageClipPrecisionDecider(EXACTLY)
        val element3 =
            LongImageClipPrecisionDecider(longImageDecider = DefaultLongImageDecider(3f, 6f))

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
        Assert.assertEquals(
            "LongImageClipPrecisionDecider(precision=EXACTLY, longImageDecider=DefaultLongImageDecider(smallRatioMultiple=2.5, bigRatioMultiple=5.0))",
            LongImageClipPrecisionDecider(EXACTLY).toString()
        )
        Assert.assertEquals(
            "LongImageClipPrecisionDecider(precision=SAME_ASPECT_RATIO, longImageDecider=DefaultLongImageDecider(smallRatioMultiple=2.5, bigRatioMultiple=5.0))",
            LongImageClipPrecisionDecider(SAME_ASPECT_RATIO).toString()
        )
    }
}