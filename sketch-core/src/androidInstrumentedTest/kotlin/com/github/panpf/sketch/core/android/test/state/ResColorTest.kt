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

import android.R
import androidx.core.content.res.ResourcesCompat
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.test.utils.getTestContext
import com.github.panpf.sketch.drawable.ResColor
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ResColorTest {

    @Test
    fun testGetColor() {
        val context = getTestContext()

        ResColor(R.color.background_dark).apply {
            Assert.assertEquals(android.R.color.background_dark, resId)
            Assert.assertEquals(
                ResourcesCompat.getColor(context.resources, android.R.color.background_dark, null),
                getColor(context)
            )
        }

        ResColor(R.color.background_light).apply {
            Assert.assertEquals(android.R.color.background_light, resId)
            Assert.assertEquals(
                ResourcesCompat.getColor(context.resources, android.R.color.background_light, null),
                getColor(context)
            )
        }
    }

    @Test
    fun testToString() {
        ResColor(R.color.background_dark).apply {
            Assert.assertEquals("ResColor(${android.R.color.background_dark})", toString())
        }

        ResColor(R.color.background_light).apply {
            Assert.assertEquals("ResColor(${android.R.color.background_light})", toString())
        }
    }

    @Test
    fun testEqualsAndHashCode() {
        val element1 = ResColor(R.color.background_dark)
        val element11 = ResColor(R.color.background_dark)
        val element2 = ResColor(R.color.background_light)
        val element3 = ResColor(R.color.darker_gray)

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