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
import kotlin.test.assertSame

@RunWith(AndroidJUnit4::class)
class RealDrawableTest {

    @Test
    fun testKey() {
        val drawable = ColorDrawable(Color.RED)
        assertEquals(
            expected = "RealDrawable($drawable)",
            actual = RealDrawable(drawable).key
        )
    }

    @Test
    fun testGetDrawable() {
        val context = getTestContext()
        val redColorDrawable = ColorDrawable(Color.RED)
        RealDrawable(redColorDrawable).apply {
            assertSame(redColorDrawable, drawable)
            assertSame(redColorDrawable, getDrawable(context))
        }
    }

    @Test
    fun testEqualsAndHashCode() {
        val redDrawable = ColorDrawable(Color.RED)
        val element1 = RealDrawable(redDrawable)
        val element11 = RealDrawable(redDrawable)
        val element2 = RealDrawable(ColorDrawable(Color.GREEN))

        assertEquals(element1, element11)
        assertNotEquals(element1, element2)
        assertNotEquals(element1, null as Any?)
        assertNotEquals(element1, Any())

        assertEquals(element1.hashCode(), element11.hashCode())
        assertNotEquals(element1.hashCode(), element2.hashCode())
    }

    @Test
    fun testToString() {
        val redColorDrawable = ColorDrawable(Color.RED)
        assertEquals(
            expected = "RealDrawable($redColorDrawable)",
            actual = RealDrawable(redColorDrawable).toString()
        )
    }
}