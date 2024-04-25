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
package com.github.panpf.sketch.core.android.test.state

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.test.utils.getTestContext
import com.github.panpf.sketch.util.RealDrawable
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RealDrawableTest {

    @Test
    fun testGetDrawable() {
        val context = getTestContext()

        val redColorDrawable = ColorDrawable(Color.RED)
        RealDrawable(redColorDrawable).apply {
            Assert.assertSame(redColorDrawable, drawable)
            Assert.assertSame(redColorDrawable, getDrawable(context))
        }

        val greenColorDrawable = ColorDrawable(Color.GREEN)
        RealDrawable(greenColorDrawable).apply {
            Assert.assertSame(greenColorDrawable, drawable)
            Assert.assertSame(greenColorDrawable, getDrawable(context))
        }
    }

    @Test
    fun testToString() {
        val redColorDrawable = ColorDrawable(Color.RED)
        RealDrawable(redColorDrawable).apply {
            Assert.assertEquals("RealDrawable($redColorDrawable)", toString())
        }

        val greenColorDrawable = ColorDrawable(Color.GREEN)
        RealDrawable(greenColorDrawable).apply {
            Assert.assertEquals("RealDrawable($greenColorDrawable)", toString())
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
}