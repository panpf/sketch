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
package com.github.panpf.sketch.core.test.stateimage

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.request.DrawableImage
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.UriInvalidException
import com.github.panpf.sketch.resources.AssetImages
import com.github.panpf.sketch.stateimage.ColorStateImage
import com.github.panpf.sketch.stateimage.DrawableStateImage
import com.github.panpf.sketch.stateimage.ErrorStateImage
import com.github.panpf.sketch.test.singleton.getTestContextAndSketch
import com.github.panpf.sketch.test.utils.getTestContext
import com.github.panpf.sketch.util.asOrThrow
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ErrorStateImageTest {

    @Test
    fun testGetDrawable() {
        val (context, sketch) = getTestContextAndSketch()
        val request = ImageRequest(context, "")
        val colorDrawable = ColorDrawable(Color.BLUE)
        val colorDrawable2 = ColorDrawable(Color.RED)

        ErrorStateImage(DrawableStateImage(colorDrawable)).apply {
            Assert.assertFalse(stateList.isEmpty())
            Assert.assertEquals(
                colorDrawable,
                getImage(sketch, request, null)?.asOrThrow<DrawableImage>()?.drawable
            )
            Assert.assertEquals(
                colorDrawable,
                getImage(sketch, request, UriInvalidException(""))
                    ?.asOrThrow<DrawableImage>()?.drawable
            )
        }

        ErrorStateImage(DrawableStateImage(colorDrawable)) {
            uriEmptyError(colorDrawable2)
        }.apply {
            Assert.assertFalse(stateList.isEmpty())
            Assert.assertEquals(
                colorDrawable,
                getImage(sketch, request, null)?.asOrThrow<DrawableImage>()?.drawable
            )
            Assert.assertEquals(
                colorDrawable2,
                getImage(sketch, request, UriInvalidException(""))
                    ?.asOrThrow<DrawableImage>()?.drawable
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
        val element1 = ErrorStateImage(ColorStateImage(Color.RED))
        val element11 = ErrorStateImage(ColorStateImage(Color.RED))
        val element2 = ErrorStateImage(ColorStateImage(Color.GREEN))
        val element3 = ErrorStateImage(ColorStateImage(Color.BLUE))

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
        ErrorStateImage(ColorStateImage(Color.RED)).apply {
            Assert.assertEquals(
                "ErrorStateImage([(DefaultCondition, ColorStateImage(IntColor(${Color.RED})))])",
                toString()
            )
        }

        ErrorStateImage(ColorStateImage(Color.GREEN)) {
            uriEmptyError(ColorStateImage(Color.YELLOW))
        }.apply {
            Assert.assertEquals(
                "ErrorStateImage([(UriEmptyCondition, ColorStateImage(IntColor(${Color.YELLOW}))), (DefaultCondition, ColorStateImage(IntColor(${Color.GREEN})))])",
                toString()
            )
        }
    }

    @Test
    fun testUriEmptyCondition() {
        val context = getTestContext()
        val request = ImageRequest(context, AssetImages.jpeg.uri)
        val request1 = ImageRequest(context, "")
        val request2 = ImageRequest(context, " ")

        ErrorStateImage.UriEmptyCondition.apply {
            Assert.assertTrue(accept(request1, UriInvalidException("")))
            Assert.assertTrue(accept(request2, UriInvalidException("")))
            Assert.assertFalse(accept(request, UriInvalidException("")))
            Assert.assertFalse(accept(request1, Exception("")))
            Assert.assertFalse(accept(request1, null))
            Assert.assertEquals("UriEmptyCondition", toString())
        }
    }

    @Test
    fun testDefaultCondition() {
        val context = getTestContext()
        val request = ImageRequest(context, AssetImages.jpeg.uri)

        ErrorStateImage.DefaultCondition.apply {
            Assert.assertTrue(accept(request, null))
            Assert.assertTrue(accept(request, null))
            Assert.assertEquals("DefaultCondition", toString())
        }
    }
}