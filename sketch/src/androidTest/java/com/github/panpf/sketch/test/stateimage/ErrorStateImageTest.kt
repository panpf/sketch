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
package com.github.panpf.sketch.test.stateimage

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.github.panpf.sketch.request.DisplayRequest
import com.github.panpf.sketch.request.UriInvalidException
import com.github.panpf.sketch.sketch
import com.github.panpf.sketch.stateimage.ColorStateImage
import com.github.panpf.sketch.stateimage.DrawableStateImage
import com.github.panpf.sketch.stateimage.ErrorStateImage
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ErrorStateImageTest {

    @Test
    fun testGetDrawable() {
        val context = InstrumentationRegistry.getInstrumentation().context
        val sketch = context.sketch
        val request = DisplayRequest(context, "")
        val colorDrawable = ColorDrawable(Color.BLUE)
        val colorDrawable2 = ColorDrawable(Color.RED)

        ErrorStateImage(DrawableStateImage(colorDrawable)).apply {
            Assert.assertFalse(matcherList.isEmpty())
            Assert.assertEquals(colorDrawable, getDrawable(sketch, request, null))
            Assert.assertEquals(
                colorDrawable,
                getDrawable(sketch, request, UriInvalidException(""))
            )
        }

        ErrorStateImage(DrawableStateImage(colorDrawable)) {
            uriEmptyError(colorDrawable2)
        }.apply {
            Assert.assertFalse(matcherList.isEmpty())
            Assert.assertEquals(colorDrawable, getDrawable(sketch, request, null))
            Assert.assertEquals(
                colorDrawable2,
                getDrawable(sketch, request, UriInvalidException(""))
            )
        }

        ErrorStateImage {
        }.apply {
            Assert.assertTrue(matcherList.isEmpty())
            Assert.assertNull(getDrawable(sketch, request, null))
            Assert.assertNull(
                getDrawable(sketch, request, UriInvalidException(""))
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
                "ErrorStateImage([DefaultMatcher(ColorStateImage(IntColor(${Color.RED})))])",
                toString()
            )
        }

        ErrorStateImage(ColorStateImage(Color.GREEN)) {
            uriEmptyError(ColorStateImage(Color.YELLOW))
        }.apply {
            Assert.assertEquals(
                "ErrorStateImage([UriEmptyMatcher(ColorStateImage(IntColor(${Color.YELLOW}))), DefaultMatcher(ColorStateImage(IntColor(${Color.GREEN})))])",
                toString()
            )
        }
    }
}