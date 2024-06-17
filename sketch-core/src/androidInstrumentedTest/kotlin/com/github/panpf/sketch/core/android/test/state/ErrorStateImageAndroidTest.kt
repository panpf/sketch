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
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.AndroidDrawableImage
import com.github.panpf.sketch.drawable.ColorDrawableEqualizer
import com.github.panpf.sketch.images.MyImages
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.UriInvalidException
import com.github.panpf.sketch.state.DrawableStateImage
import com.github.panpf.sketch.state.ErrorStateImage
import com.github.panpf.sketch.state.IntColorDrawableStateImage
import com.github.panpf.sketch.state.addState
import com.github.panpf.sketch.state.asStateImage
import com.github.panpf.sketch.test.singleton.getTestContextAndSketch
import com.github.panpf.sketch.test.utils.UriInvalidCondition
import com.github.panpf.sketch.test.utils.getTestContext
import com.github.panpf.sketch.util.IntColor
import com.github.panpf.sketch.util.asOrThrow
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ErrorStateImageAndroidTest {

    @Test
    fun testGetDrawable() {
        val (context, sketch) = getTestContextAndSketch()
        val request = ImageRequest(context, "")
        val colorDrawable = ColorDrawableEqualizer(Color.BLUE)
        val colorDrawable2 = ColorDrawableEqualizer(Color.RED)

        ErrorStateImage(colorDrawable.asStateImage()).apply {
            Assert.assertFalse(stateList.isEmpty())
            Assert.assertEquals(
                colorDrawable.wrapped,
                getImage(sketch, request, null)?.asOrThrow<AndroidDrawableImage>()?.drawable
            )
            Assert.assertEquals(
                colorDrawable.wrapped,
                getImage(sketch, request, UriInvalidException(""))
                    ?.asOrThrow<AndroidDrawableImage>()?.drawable
            )
        }

        ErrorStateImage(DrawableStateImage(colorDrawable)) {
            addState(UriInvalidCondition, colorDrawable2.asStateImage())
        }.apply {
            Assert.assertFalse(stateList.isEmpty())
            Assert.assertEquals(
                colorDrawable.wrapped,
                getImage(sketch, request, null)?.asOrThrow<AndroidDrawableImage>()?.drawable
            )
            Assert.assertEquals(
                colorDrawable2.wrapped,
                getImage(sketch, request, UriInvalidException(""))
                    ?.asOrThrow<AndroidDrawableImage>()?.drawable
            )
        }

        ErrorStateImage {
        }.apply {
            Assert.assertTrue(stateList.isEmpty())
            Assert.assertNull(getImage(sketch, request, null))
            Assert.assertNull(
                getImage(sketch, request, UriInvalidException(""))
            )
        }
    }

    @Test
    fun testEqualsAndHashCode() {
        val element1 = ErrorStateImage(IntColorDrawableStateImage(Color.RED))
        val element11 = ErrorStateImage(IntColorDrawableStateImage(Color.RED))
        val element2 = ErrorStateImage(IntColorDrawableStateImage(Color.GREEN))
        val element3 = ErrorStateImage(IntColorDrawableStateImage(Color.BLUE))

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
        ErrorStateImage(IntColorDrawableStateImage(Color.RED)).apply {
            Assert.assertEquals(
                "ErrorStateImage([DefaultCondition:ColorDrawableStateImage(IntColor(${Color.RED}))])",
                toString()
            )
        }

        ErrorStateImage(IntColorDrawableStateImage(Color.GREEN)) {
            addState(UriInvalidCondition, IntColor(Color.YELLOW))
        }.apply {
            Assert.assertEquals(
                "ErrorStateImage([UriInvalidCondition:ColorDrawableStateImage(IntColor(${Color.YELLOW})), DefaultCondition:ColorDrawableStateImage(IntColor(${Color.GREEN}))])",
                toString()
            )
        }
    }

    @Test
    fun testDefaultCondition() {
        val context = getTestContext()
        val request = ImageRequest(context, MyImages.jpeg.uri)

        ErrorStateImage.DefaultCondition.apply {
            Assert.assertTrue(accept(request, null))
            Assert.assertTrue(accept(request, null))
            Assert.assertEquals("DefaultCondition", toString())
        }
    }
}