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

import android.graphics.Bitmap
import android.graphics.Bitmap.Config.RGB_565
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.github.panpf.sketch.drawable.internal.IconDrawable
import com.github.panpf.sketch.fetch.newAssetUri
import com.github.panpf.sketch.request.DisplayRequest
import com.github.panpf.sketch.sketch
import com.github.panpf.sketch.stateimage.IconStateImage
import com.github.panpf.sketch.stateimage.IntColor
import com.github.panpf.sketch.util.asOrNull
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class IconStateImageTest {

    @Test
    fun testGetDrawable() {
        val context = InstrumentationRegistry.getInstrumentation().context
        val sketch = context.sketch
        val request = DisplayRequest(context, newAssetUri("sample.jpeg"))
        val iconDrawable = BitmapDrawable(context.resources, Bitmap.createBitmap(100, 100, RGB_565))
        val greenBgDrawable = ColorDrawable(Color.GREEN)

        IconStateImage(iconDrawable, greenBgDrawable).apply {
            getDrawable(sketch, request, null).asOrNull<IconDrawable>()!!.apply {
                Assert.assertEquals(iconDrawable, icon)
                Assert.assertEquals(greenBgDrawable, bg)
            }
        }

        IconStateImage(iconDrawable, android.R.drawable.bottom_bar).apply {
            getDrawable(sketch, request, null).asOrNull<IconDrawable>()!!.apply {
                Assert.assertEquals(iconDrawable, icon)
                Assert.assertTrue(bg is BitmapDrawable)
            }
        }

        IconStateImage(iconDrawable, IntColor(Color.BLUE)).apply {
            getDrawable(sketch, request, null).asOrNull<IconDrawable>()!!.apply {
                Assert.assertEquals(iconDrawable, icon)
                Assert.assertEquals(Color.BLUE, (bg as ColorDrawable).color)
            }
        }

        IconStateImage(iconDrawable).apply {
            getDrawable(sketch, request, null).asOrNull<IconDrawable>()!!.apply {
                Assert.assertEquals(iconDrawable, icon)
                Assert.assertNull(bg)
            }
        }


        IconStateImage(android.R.drawable.ic_delete, greenBgDrawable).apply {
            getDrawable(sketch, request, null).asOrNull<IconDrawable>()!!.apply {
                Assert.assertTrue(icon is BitmapDrawable)
                Assert.assertEquals(greenBgDrawable, bg)
            }
        }

        IconStateImage(android.R.drawable.ic_delete, android.R.drawable.bottom_bar).apply {
            getDrawable(sketch, request, null).asOrNull<IconDrawable>()!!.apply {
                Assert.assertTrue(icon is BitmapDrawable)
                Assert.assertTrue(bg is BitmapDrawable)
            }
        }

        IconStateImage(android.R.drawable.ic_delete, IntColor(Color.BLUE)).apply {
            getDrawable(sketch, request, null).asOrNull<IconDrawable>()!!.apply {
                Assert.assertTrue(icon is BitmapDrawable)
                Assert.assertEquals(Color.BLUE, (bg as ColorDrawable).color)
            }
        }

        IconStateImage(android.R.drawable.ic_delete).apply {
            getDrawable(sketch, request, null).asOrNull<IconDrawable>()!!.apply {
                Assert.assertTrue(icon is BitmapDrawable)
                Assert.assertNull(bg)
            }
        }
    }

    @Test
    fun testEqualsAndHashCode() {
        val element1 = IconStateImage(android.R.drawable.ic_delete, android.R.drawable.bottom_bar)
        val element11 = IconStateImage(android.R.drawable.ic_delete, android.R.drawable.bottom_bar)
        val element2 = IconStateImage(android.R.drawable.ic_delete, android.R.drawable.btn_default)
        val element3 = IconStateImage(android.R.drawable.btn_star, android.R.drawable.bottom_bar)
        val element4 = IconStateImage(android.R.drawable.btn_star)

        Assert.assertNotSame(element1, element11)
        Assert.assertNotSame(element1, element2)
        Assert.assertNotSame(element1, element3)
        Assert.assertNotSame(element1, element4)
        Assert.assertNotSame(element2, element11)
        Assert.assertNotSame(element2, element3)
        Assert.assertNotSame(element2, element4)
        Assert.assertNotSame(element3, element4)

        Assert.assertEquals(element1, element1)
        Assert.assertEquals(element1, element11)
        Assert.assertNotEquals(element1, element2)
        Assert.assertNotEquals(element1, element3)
        Assert.assertNotEquals(element1, element4)
        Assert.assertNotEquals(element2, element11)
        Assert.assertNotEquals(element2, element3)
        Assert.assertNotEquals(element2, element4)
        Assert.assertNotEquals(element3, element4)
        Assert.assertNotEquals(element1, null)
        Assert.assertNotEquals(element1, Any())

        Assert.assertEquals(element1.hashCode(), element1.hashCode())
        Assert.assertEquals(element1.hashCode(), element11.hashCode())
        Assert.assertNotEquals(element1.hashCode(), element2.hashCode())
        Assert.assertNotEquals(element1.hashCode(), element3.hashCode())
        Assert.assertNotEquals(element1.hashCode(), element4.hashCode())
        Assert.assertNotEquals(element2.hashCode(), element11.hashCode())
        Assert.assertNotEquals(element2.hashCode(), element3.hashCode())
        Assert.assertNotEquals(element2.hashCode(), element4.hashCode())
        Assert.assertNotEquals(element3.hashCode(), element4.hashCode())
    }

    @Test
    fun testToString() {
        IconStateImage(android.R.drawable.ic_delete, android.R.drawable.bottom_bar).apply {
            Assert.assertEquals(
                "IconStateImage(icon=ResDrawable(${android.R.drawable.ic_delete}), bg=ResDrawable(${android.R.drawable.bottom_bar}))",
                toString()
            )
        }
        IconStateImage(android.R.drawable.ic_delete, android.R.drawable.btn_default).apply {
            Assert.assertEquals(
                "IconStateImage(icon=ResDrawable(${android.R.drawable.ic_delete}), bg=ResDrawable(${android.R.drawable.btn_default}))",
                toString()
            )
        }
        IconStateImage(android.R.drawable.ic_delete).apply {
            Assert.assertEquals(
                "IconStateImage(icon=ResDrawable(${android.R.drawable.ic_delete}), bg=null)",
                toString()
            )
        }
    }
}