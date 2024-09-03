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

package com.github.panpf.sketch.core.android.test.drawable

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.drawable.RealDrawable
import com.github.panpf.sketch.test.utils.getTestContext
import org.junit.runner.RunWith
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNotSame
import kotlin.test.assertSame

@RunWith(AndroidJUnit4::class)
class RealDrawableTest {

    @Test
    fun testGetDrawable() {
        val context = getTestContext()

        val redColorDrawable = ColorDrawable(Color.RED)
        RealDrawable(redColorDrawable).apply {
            assertSame(redColorDrawable, drawable)
            assertSame(redColorDrawable, getDrawable(context))
        }

        val greenColorDrawable = ColorDrawable(Color.GREEN)
        RealDrawable(greenColorDrawable).apply {
            assertSame(greenColorDrawable, drawable)
            assertSame(greenColorDrawable, getDrawable(context))
        }
    }

    @Test
    fun testToString() {
        val redColorDrawable = ColorDrawable(Color.RED)
        RealDrawable(redColorDrawable).apply {
            assertEquals("RealDrawable($redColorDrawable)", toString())
        }

        val greenColorDrawable = ColorDrawable(Color.GREEN)
        RealDrawable(greenColorDrawable).apply {
            assertEquals("RealDrawable($greenColorDrawable)", toString())
        }
    }

    @Test
    fun testEqualsAndHashCode() {
        val redColorDrawable = ColorDrawable(Color.RED)
        val greenColorDrawable = ColorDrawable(Color.GREEN)
        val blueColorDrawable = ColorDrawable(Color.BLUE)
        val element1 = RealDrawable(redColorDrawable)
        val element11 = RealDrawable(redColorDrawable)
        val element2 = RealDrawable(greenColorDrawable)
        val element3 = RealDrawable(blueColorDrawable)

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
}