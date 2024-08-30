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
import com.github.panpf.sketch.resize.FixedPrecisionDecider
import com.github.panpf.sketch.resize.LongImagePrecisionDecider
import com.github.panpf.sketch.resize.Precision.EXACTLY
import com.github.panpf.sketch.resize.Precision.LESS_PIXELS
import com.github.panpf.sketch.resize.Precision.SAME_ASPECT_RATIO
import com.github.panpf.sketch.resize.Precision.SMALLER_SIZE
import com.github.panpf.sketch.resize.PrecisionDecider
import com.github.panpf.sketch.test.utils.get
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNotSame
import kotlin.test.assertTrue

class PrecisionDeciderTest {

    @Test
    fun testCreateFunction() {
        assertTrue(PrecisionDecider(EXACTLY) is FixedPrecisionDecider)
    }

    @Test
    fun testFixedPrecisionDeciderCreateFunction() {
        assertEquals(FixedPrecisionDecider(EXACTLY), FixedPrecisionDecider(EXACTLY))
        assertEquals(FixedPrecisionDecider(LESS_PIXELS), FixedPrecisionDecider(LESS_PIXELS))
        assertEquals(
            FixedPrecisionDecider(SAME_ASPECT_RATIO),
            FixedPrecisionDecider(SAME_ASPECT_RATIO)
        )
    }

    @Test
    fun testFixedPrecisionDeciderGet() {
        FixedPrecisionDecider(SAME_ASPECT_RATIO).apply {
            assertEquals(SAME_ASPECT_RATIO, get(100, 48, 50, 50))
            assertEquals(SAME_ASPECT_RATIO, get(100, 49, 50, 50))
            assertEquals(SAME_ASPECT_RATIO, get(100, 50, 50, 50))
            assertEquals(SAME_ASPECT_RATIO, get(100, 51, 50, 50))
            assertEquals(SAME_ASPECT_RATIO, get(100, 52, 50, 50))
        }

        FixedPrecisionDecider(EXACTLY).apply {
            assertEquals(EXACTLY, get(100, 48, 50, 50))
            assertEquals(EXACTLY, get(100, 49, 50, 50))
            assertEquals(EXACTLY, get(100, 50, 50, 50))
            assertEquals(EXACTLY, get(100, 51, 50, 50))
            assertEquals(EXACTLY, get(100, 52, 50, 50))
        }

        FixedPrecisionDecider(LESS_PIXELS).apply {
            assertEquals(LESS_PIXELS, get(100, 32, 50, 50))
            assertEquals(LESS_PIXELS, get(100, 33, 50, 50))
            assertEquals(LESS_PIXELS, get(100, 34, 50, 50))
        }
    }

    @Test
    fun testFixedPrecisionDeciderKey() {
        assertEquals(
            "Fixed(SAME_ASPECT_RATIO)",
            FixedPrecisionDecider(SAME_ASPECT_RATIO).key
        )
        assertEquals(
            "Fixed(EXACTLY)",
            FixedPrecisionDecider(EXACTLY).key
        )
        assertEquals(
            "Fixed(LESS_PIXELS)",
            FixedPrecisionDecider(LESS_PIXELS).key
        )
    }

    @Test
    fun testFixedPrecisionDeciderToString() {
        assertEquals(
            "FixedPrecisionDecider(SAME_ASPECT_RATIO)",
            FixedPrecisionDecider(SAME_ASPECT_RATIO).toString()
        )
        assertEquals(
            "FixedPrecisionDecider(EXACTLY)",
            FixedPrecisionDecider(EXACTLY).toString()
        )
        assertEquals(
            "FixedPrecisionDecider(LESS_PIXELS)",
            FixedPrecisionDecider(LESS_PIXELS).toString()
        )
    }

    @Test
    fun testLongImagePrecisionDeciderCreateFunction() {
        assertEquals(
            LongImagePrecisionDecider(EXACTLY),
            LongImagePrecisionDecider(EXACTLY)
        )
        assertEquals(
            LongImagePrecisionDecider(SAME_ASPECT_RATIO),
            LongImagePrecisionDecider(SAME_ASPECT_RATIO)
        )
    }

    @Test
    fun testLongImagePrecisionDeciderConstructor() {
        LongImagePrecisionDecider().apply {
            assertEquals(SAME_ASPECT_RATIO, get(150, 48, 50, 50))
        }
        LongImagePrecisionDecider(SAME_ASPECT_RATIO).apply {
            assertEquals(SAME_ASPECT_RATIO, get(150, 48, 50, 50))
        }
        LongImagePrecisionDecider(EXACTLY).apply {
            assertEquals(EXACTLY, get(150, 48, 50, 50))
        }
        LongImagePrecisionDecider(SMALLER_SIZE).apply {
            assertEquals(SMALLER_SIZE, get(150, 48, 50, 50))
        }
    }

    @Test
    fun testLongImagePrecisionDeciderGet() {
        LongImagePrecisionDecider(SAME_ASPECT_RATIO).apply {
            assertEquals(SAME_ASPECT_RATIO, get(150, 48, 50, 50))
            assertEquals(SAME_ASPECT_RATIO, get(150, 49, 50, 50))
            assertEquals(SAME_ASPECT_RATIO, get(150, 50, 50, 50))
            assertEquals(LESS_PIXELS, get(150, 76, 50, 50))
            assertEquals(LESS_PIXELS, get(150, 77, 50, 50))
        }

        LongImagePrecisionDecider(SAME_ASPECT_RATIO, SMALLER_SIZE).apply {
            assertEquals(SAME_ASPECT_RATIO, get(150, 48, 50, 50))
            assertEquals(SAME_ASPECT_RATIO, get(150, 49, 50, 50))
            assertEquals(SAME_ASPECT_RATIO, get(150, 50, 50, 50))
            assertEquals(SMALLER_SIZE, get(150, 76, 50, 50))
            assertEquals(SMALLER_SIZE, get(150, 77, 50, 50))
        }

        LongImagePrecisionDecider(EXACTLY).apply {
            assertEquals(EXACTLY, get(150, 48, 50, 50))
            assertEquals(EXACTLY, get(150, 49, 50, 50))
            assertEquals(EXACTLY, get(150, 50, 50, 50))
            assertEquals(LESS_PIXELS, get(150, 76, 50, 50))
            assertEquals(LESS_PIXELS, get(150, 77, 50, 50))
        }
    }

    @Test
    fun testLongImagePrecisionDeciderKey() {
        assertEquals(
            "LongImage(EXACTLY,LESS_PIXELS,Default(2.5,5.0))",
            LongImagePrecisionDecider(EXACTLY).key
        )
        assertEquals(
            "LongImage(SAME_ASPECT_RATIO,LESS_PIXELS,Default(2.5,5.0))",
            LongImagePrecisionDecider(SAME_ASPECT_RATIO).key
        )
    }

    @Test
    fun testLongImagePrecisionDeciderEqualsAndHashCode() {
        val element1 = LongImagePrecisionDecider()
        val element11 = LongImagePrecisionDecider()
        val element2 = LongImagePrecisionDecider(EXACTLY)
        val element3 =
            LongImagePrecisionDecider(longImageDecider = DefaultLongImageDecider(3f, 6f))

        assertNotSame(element1, element11)
        assertNotSame(element1, element2)
        assertNotSame(element1, element3)
        assertNotSame(element2, element11)
        assertNotSame(element2, element3)

        assertEquals(element1, element1)
        assertEquals(element1, element11)
        assertNotEquals(element1, element2)
        assertNotEquals(element1, element3)
        assertNotEquals(element2, element11)
        assertNotEquals(element2, element3)
        assertNotEquals(element1, null as Any?)
        assertNotEquals(element1, Any())

        assertEquals(element1.hashCode(), element1.hashCode())
        assertEquals(element1.hashCode(), element11.hashCode())
        assertNotEquals(element1.hashCode(), element2.hashCode())
        assertNotEquals(element1.hashCode(), element3.hashCode())
        assertNotEquals(element2.hashCode(), element11.hashCode())
        assertNotEquals(element2.hashCode(), element3.hashCode())
    }

    @Test
    fun testLongImagePrecisionDeciderToString() {
        assertEquals(
            "LongImagePrecisionDecider(longImage=EXACTLY, otherImage=LESS_PIXELS, longImageDecider=DefaultLongImageDecider(sameDirectionMultiple=2.5, notSameDirectionMultiple=5.0))",
            LongImagePrecisionDecider(EXACTLY).toString()
        )
        assertEquals(
            "LongImagePrecisionDecider(longImage=SAME_ASPECT_RATIO, otherImage=LESS_PIXELS, longImageDecider=DefaultLongImageDecider(sameDirectionMultiple=2.5, notSameDirectionMultiple=5.0))",
            LongImagePrecisionDecider(SAME_ASPECT_RATIO).toString()
        )
    }
}