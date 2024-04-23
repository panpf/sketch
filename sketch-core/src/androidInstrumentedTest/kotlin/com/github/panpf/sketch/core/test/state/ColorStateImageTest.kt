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
package com.github.panpf.sketch.core.test.state

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.DrawableImage
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.images.MyImages
import com.github.panpf.sketch.state.ColorStateImage
import com.github.panpf.sketch.util.IntColor
import com.github.panpf.sketch.test.singleton.getTestContextAndSketch
import com.github.panpf.sketch.util.asOrNull
import com.github.panpf.sketch.util.asOrThrow
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ColorStateImageTest {

    @Test
    fun testGetDrawable() {
        val (context, sketch) = getTestContextAndSketch()
        val request = ImageRequest(context, MyImages.jpeg.uri)

        ColorStateImage(Color.BLUE).apply {
            Assert.assertEquals(
                Color.BLUE,
                getImage(sketch, request, null)
                    .asOrThrow<DrawableImage>().drawable
                    .asOrNull<ColorDrawable>()!!.color
            )
        }

        ColorStateImage(IntColor(Color.RED)).apply {
            Assert.assertEquals(
                Color.RED,
                getImage(sketch, request, null)
                    .asOrThrow<DrawableImage>().drawable
                    .asOrNull<ColorDrawable>()!!.color
            )
        }

        ColorStateImage(IntColor(Color.GREEN)).apply {
            Assert.assertEquals(
                Color.GREEN,
                getImage(sketch, request, null)
                    .asOrThrow<DrawableImage>().drawable
                    .asOrNull<ColorDrawable>()!!.color
            )
        }
    }

    @Test
    fun testEqualsAndHashCode() {
        val element1 = ColorStateImage(Color.RED)
        val element11 = ColorStateImage(Color.RED)
        val element2 = ColorStateImage(Color.GREEN)
        val element3 = ColorStateImage(Color.BLUE)

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
        ColorStateImage(Color.RED).apply {
            Assert.assertEquals("ColorStateImage(IntColor(${Color.RED}))", toString())
        }
        ColorStateImage(Color.GREEN).apply {
            Assert.assertEquals("ColorStateImage(IntColor(${Color.GREEN}))", toString())
        }
    }
}