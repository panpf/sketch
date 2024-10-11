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
import com.github.panpf.sketch.resize.FixedScaleDecider
import com.github.panpf.sketch.resize.LongImageScaleDecider
import com.github.panpf.sketch.resize.Scale.CENTER_CROP
import com.github.panpf.sketch.resize.Scale.END_CROP
import com.github.panpf.sketch.resize.Scale.FILL
import com.github.panpf.sketch.resize.Scale.START_CROP
import com.github.panpf.sketch.resize.ScaleDecider
import com.github.panpf.sketch.test.utils.get
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class ScaleDeciderTest {

    @Test
    fun testCreateFunction() {
        assertTrue(ScaleDecider(CENTER_CROP) is FixedScaleDecider)
    }

    @Test
    fun testFixedScaleDeciderCreateFunction() {
        assertEquals(FixedScaleDecider(START_CROP), FixedScaleDecider(START_CROP))
        assertEquals(FixedScaleDecider(END_CROP), FixedScaleDecider(END_CROP))
        assertEquals(FixedScaleDecider(CENTER_CROP), FixedScaleDecider(CENTER_CROP))
        assertEquals(FixedScaleDecider(FILL), FixedScaleDecider(FILL))
    }

    @Test
    fun testFixedScaleDeciderGet() {
        FixedScaleDecider(START_CROP).apply {
            assertEquals(START_CROP, get(100, 48, 50, 50))
        }
        FixedScaleDecider(END_CROP).apply {
            assertEquals(END_CROP, get(100, 48, 50, 50))
        }
    }

    @Test
    fun testFixedScaleDeciderKey() {
        FixedScaleDecider(START_CROP).apply {
            assertEquals("Fixed(START_CROP)", key)
        }
        FixedScaleDecider(END_CROP).apply {
            assertEquals("Fixed(END_CROP)", key)
        }
    }

    @Test
    fun testFixedScaleDeciderToString() {
        FixedScaleDecider(START_CROP).apply {
            assertEquals("FixedScaleDecider(START_CROP)", toString())
        }
        FixedScaleDecider(END_CROP).apply {
            assertEquals("FixedScaleDecider(END_CROP)", toString())
        }
    }

    @Test
    fun testFixedScaleDeciderEqualsAndHashCode() {
        val element1 = FixedScaleDecider(START_CROP)
        val element11 = FixedScaleDecider(START_CROP)
        val element2 = FixedScaleDecider(END_CROP)
        assertEquals(element1, element11)
        assertNotEquals(element1, element2)
        assertNotEquals(element1, null as Any?)
        assertNotEquals(element1, Any())
        assertEquals(element1.hashCode(), element11.hashCode())
        assertNotEquals(element1.hashCode(), element2.hashCode())
    }

    @Test
    fun testLongImageScaleDeciderCreateFunction() {
        assertEquals(
            LongImageScaleDecider(START_CROP, CENTER_CROP),
            LongImageScaleDecider()
        )
        assertEquals(
            LongImageScaleDecider(START_CROP, CENTER_CROP),
            LongImageScaleDecider(START_CROP, CENTER_CROP)
        )
        assertEquals(
            LongImageScaleDecider(END_CROP, START_CROP),
            LongImageScaleDecider(END_CROP, START_CROP)
        )
    }

    @Test
    fun testLongImageScaleDeciderGet() {
        LongImageScaleDecider(START_CROP, CENTER_CROP).apply {
            assertEquals(CENTER_CROP, get(100, 50, 50, 50))
            assertEquals(START_CROP, get(100, 40, 50, 50))
        }
        LongImageScaleDecider(END_CROP, CENTER_CROP).apply {
            assertEquals(CENTER_CROP, get(100, 50, 50, 50))
            assertEquals(END_CROP, get(100, 40, 50, 50))
        }
    }

    @Test
    fun testLongImageScaleDeciderKey() {
        LongImageScaleDecider(START_CROP, CENTER_CROP).apply {
            assertEquals(
                "LongImage(START_CROP,CENTER_CROP,Default(2.5,5.0))",
                key
            )
        }
        LongImageScaleDecider(END_CROP, CENTER_CROP).apply {
            assertEquals(
                "LongImage(END_CROP,CENTER_CROP,Default(2.5,5.0))",
                key
            )
        }
    }

    @Test
    fun testLongImageScaleDeciderToString() {
        LongImageScaleDecider(START_CROP, CENTER_CROP).apply {
            assertEquals(
                "LongImageScaleDecider(longImage=START_CROP, otherImage=CENTER_CROP, longImageDecider=DefaultLongImageDecider(sameDirectionMultiple=2.5, notSameDirectionMultiple=5.0))",
                toString()
            )
        }
        LongImageScaleDecider(END_CROP, CENTER_CROP).apply {
            assertEquals(
                "LongImageScaleDecider(longImage=END_CROP, otherImage=CENTER_CROP, longImageDecider=DefaultLongImageDecider(sameDirectionMultiple=2.5, notSameDirectionMultiple=5.0))",
                toString()
            )
        }
    }

    @Test
    fun testLongImageScaleDeciderEquals() {
        val element1 = LongImageScaleDecider(START_CROP, CENTER_CROP)
        val element11 = LongImageScaleDecider(START_CROP, CENTER_CROP)
        val element2 = LongImageScaleDecider(END_CROP, CENTER_CROP)
        val element3 = LongImageScaleDecider(START_CROP, END_CROP)
        val element4 = LongImageScaleDecider(
            START_CROP,
            CENTER_CROP,
            longImageDecider = DefaultLongImageDecider(3f, 6f)
        )

        assertEquals(element1, element11)
        assertNotEquals(element1, element2)
        assertNotEquals(element1, element3)
        assertNotEquals(element1, element4)
        assertNotEquals(element2, element3)
        assertNotEquals(element2, element4)
        assertNotEquals(element3, element4)
        assertNotEquals(element1, null as Any?)
        assertNotEquals(element1, Any())

        assertEquals(element1.hashCode(), element11.hashCode())
        assertNotEquals(element1.hashCode(), element2.hashCode())
        assertNotEquals(element1.hashCode(), element3.hashCode())
        assertNotEquals(element1.hashCode(), element4.hashCode())
        assertNotEquals(element2.hashCode(), element3.hashCode())
        assertNotEquals(element2.hashCode(), element4.hashCode())
        assertNotEquals(element3.hashCode(), element4.hashCode())
    }

    @Test
    fun testLongImageScaleDeciderHashCode() {
        val element1 = LongImageScaleDecider(START_CROP, CENTER_CROP)
        val element11 = LongImageScaleDecider(START_CROP, CENTER_CROP)
        val element2 = LongImageScaleDecider(END_CROP, CENTER_CROP)
        assertEquals(element1.hashCode(), element11.hashCode())
        assertNotEquals(element1.hashCode(), element2.hashCode())
    }
}