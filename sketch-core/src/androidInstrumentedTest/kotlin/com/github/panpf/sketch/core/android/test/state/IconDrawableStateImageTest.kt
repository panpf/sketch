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

package com.github.panpf.sketch.core.android.test.state

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.github.panpf.sketch.DrawableImage
import com.github.panpf.sketch.drawable.ColorEquitableDrawable
import com.github.panpf.sketch.drawable.IconDrawable
import com.github.panpf.sketch.drawable.asEquitable
import com.github.panpf.sketch.drawable.getEquitableDrawable
import com.github.panpf.sketch.images.ResourceImages
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.state.IconDrawableStateImage
import com.github.panpf.sketch.test.singleton.getTestContextAndSketch
import com.github.panpf.sketch.util.IntColor
import com.github.panpf.sketch.util.Size
import com.github.panpf.sketch.util.SketchSize
import com.github.panpf.sketch.util.asOrNull
import com.github.panpf.sketch.util.asOrThrow
import org.junit.runner.RunWith
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNotSame
import kotlin.test.assertNull
import kotlin.test.assertTrue

@RunWith(AndroidJUnit4::class)
class IconDrawableStateImageTest {

    @Test
    fun createIconDrawableStateImage() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val drawableIcon =
            androidx.core.R.drawable.ic_call_decline.let { context.getEquitableDrawable(it) }
        val resIcon = androidx.core.R.drawable.ic_call_answer
        val drawableBackground =
            androidx.core.R.drawable.notification_bg.let { context.getEquitableDrawable(it) }
        val resBackground = androidx.core.R.drawable.notification_template_icon_bg
        val intColorBackground = IntColor(Color.BLUE)
        val iconSize = Size(100, 100)
        val intIconTint = IntColor(Color.GREEN)
        val resIconTint = android.R.color.black

        // drawable icon
        IconDrawableStateImage(
            icon = drawableIcon,
            background = drawableBackground,
            iconSize = iconSize,
            iconTint = resIconTint
        )
        IconDrawableStateImage(
            icon = drawableIcon,
            background = resBackground,
            iconSize = iconSize,
            iconTint = resIconTint
        )
        IconDrawableStateImage(
            icon = drawableIcon,
            background = intColorBackground,
            iconSize = iconSize,
            iconTint = resIconTint
        )
        IconDrawableStateImage(
            icon = drawableIcon,
            background = drawableBackground,
            iconSize = iconSize,
            iconTint = intIconTint
        )
        IconDrawableStateImage(
            icon = drawableIcon,
            background = resBackground,
            iconSize = iconSize,
            iconTint = intIconTint
        )
        IconDrawableStateImage(
            icon = drawableIcon,
            background = intColorBackground,
            iconSize = iconSize,
            iconTint = intIconTint
        )

        IconDrawableStateImage(
            icon = drawableIcon,
            background = drawableBackground,
            iconSize = iconSize,
        )
        IconDrawableStateImage(
            icon = drawableIcon,
            background = resBackground,
            iconSize = iconSize,
        )
        IconDrawableStateImage(
            icon = drawableIcon,
            background = intColorBackground,
            iconSize = iconSize,
        )

        IconDrawableStateImage(
            icon = drawableIcon,
            background = drawableBackground,
            iconTint = resIconTint
        )
        IconDrawableStateImage(
            icon = drawableIcon,
            background = resBackground,
            iconTint = resIconTint
        )
        IconDrawableStateImage(
            icon = drawableIcon,
            background = intColorBackground,
            iconTint = resIconTint
        )

        IconDrawableStateImage(
            icon = drawableIcon,
            background = drawableBackground,
            iconTint = intIconTint
        )
        IconDrawableStateImage(
            icon = drawableIcon,
            background = resBackground,
            iconTint = intIconTint
        )
        IconDrawableStateImage(
            icon = drawableIcon,
            background = intColorBackground,
            iconTint = intIconTint
        )

        IconDrawableStateImage(
            icon = drawableIcon,
            iconSize = iconSize,
            iconTint = resIconTint
        )
        IconDrawableStateImage(
            icon = drawableIcon,
            iconSize = iconSize,
            iconTint = intIconTint
        )

        IconDrawableStateImage(
            icon = drawableIcon,
            background = drawableBackground,
        )
        IconDrawableStateImage(
            icon = drawableIcon,
            background = resBackground,
        )
        IconDrawableStateImage(
            icon = drawableIcon,
            background = intColorBackground,
        )

        IconDrawableStateImage(
            icon = drawableIcon,
            iconSize = iconSize,
        )

        IconDrawableStateImage(
            icon = drawableIcon,
            iconTint = resIconTint
        )
        IconDrawableStateImage(
            icon = drawableIcon,
            iconTint = intIconTint
        )

        IconDrawableStateImage(
            icon = drawableIcon,
        )

        // res icon
        IconDrawableStateImage(
            icon = resIcon,
            background = drawableBackground,
            iconSize = iconSize,
            iconTint = resIconTint
        )
        IconDrawableStateImage(
            icon = resIcon,
            background = resBackground,
            iconSize = iconSize,
            iconTint = resIconTint
        )
        IconDrawableStateImage(
            icon = resIcon,
            background = intColorBackground,
            iconSize = iconSize,
            iconTint = resIconTint
        )
        IconDrawableStateImage(
            icon = resIcon,
            background = drawableBackground,
            iconSize = iconSize,
            iconTint = intIconTint
        )
        IconDrawableStateImage(
            icon = resIcon,
            background = resBackground,
            iconSize = iconSize,
            iconTint = intIconTint
        )
        IconDrawableStateImage(
            icon = resIcon,
            background = intColorBackground,
            iconSize = iconSize,
            iconTint = intIconTint
        )

        IconDrawableStateImage(
            icon = resIcon,
            background = drawableBackground,
            iconSize = iconSize,
        )
        IconDrawableStateImage(
            icon = resIcon,
            background = resBackground,
            iconSize = iconSize,
        )
        IconDrawableStateImage(
            icon = resIcon,
            background = intColorBackground,
            iconSize = iconSize,
        )

        IconDrawableStateImage(
            icon = resIcon,
            background = drawableBackground,
            iconTint = resIconTint
        )
        IconDrawableStateImage(
            icon = resIcon,
            background = resBackground,
            iconTint = resIconTint
        )
        IconDrawableStateImage(
            icon = resIcon,
            background = intColorBackground,
            iconTint = resIconTint
        )

        IconDrawableStateImage(
            icon = resIcon,
            background = drawableBackground,
            iconTint = intIconTint
        )
        IconDrawableStateImage(
            icon = resIcon,
            background = resBackground,
            iconTint = intIconTint
        )
        IconDrawableStateImage(
            icon = resIcon,
            background = intColorBackground,
            iconTint = intIconTint
        )

        IconDrawableStateImage(
            icon = resIcon,
            iconSize = iconSize,
            iconTint = resIconTint
        )
        IconDrawableStateImage(
            icon = resIcon,
            iconSize = iconSize,
            iconTint = intIconTint
        )

        IconDrawableStateImage(
            icon = resIcon,
            background = drawableBackground,
        )
        IconDrawableStateImage(
            icon = resIcon,
            background = resBackground,
        )
        IconDrawableStateImage(
            icon = resIcon,
            background = intColorBackground,
        )

        IconDrawableStateImage(
            icon = resIcon,
            iconSize = iconSize,
        )

        IconDrawableStateImage(
            icon = resIcon,
            iconTint = resIconTint
        )
        IconDrawableStateImage(
            icon = resIcon,
            iconTint = intIconTint
        )

        IconDrawableStateImage(
            icon = resIcon,
        )
    }

    @Test
    fun testGetImage() {
        val (context, sketch) = getTestContextAndSketch()
        val request = ImageRequest(context, ResourceImages.jpeg.uri)
        val iconDrawable = BitmapDrawable(
            context.resources,
            Bitmap.createBitmap(100, 100, Bitmap.Config.RGB_565)
        ).let { it.asEquitable(it) }
        val greenBgDrawable = ColorEquitableDrawable(Color.GREEN)

        IconDrawableStateImage(icon = iconDrawable, background = greenBgDrawable)
            .getImage(sketch, request, null)
            ?.asOrThrow<DrawableImage>()
            ?.drawable.asOrNull<IconDrawable>()!!.apply {
                assertEquals(iconDrawable, icon)
                assertEquals(greenBgDrawable, background)
                assertNull(iconSize)
            }

        IconDrawableStateImage(
            icon = iconDrawable,
            iconSize = SketchSize(40, 40),
            background = android.R.drawable.bottom_bar,
        ).getImage(sketch, request, null)
            ?.asOrThrow<DrawableImage>()
            ?.drawable.asOrNull<IconDrawable>()!!.apply {
                assertEquals(iconDrawable, icon)
                assertTrue(background is BitmapDrawable)
                assertEquals(Size(40, 40), iconSize)
            }

        IconDrawableStateImage(
            icon = iconDrawable,
            background = IntColor(Color.BLUE),
        ).getImage(sketch, request, null)
            ?.asOrThrow<DrawableImage>()
            ?.drawable.asOrNull<IconDrawable>()!!.apply {
                assertEquals(iconDrawable, icon)
                assertEquals(Color.BLUE, (background as ColorDrawable).color)
                assertNull(iconSize)
            }

        IconDrawableStateImage(icon = iconDrawable)
            .getImage(sketch, request, null)
            ?.asOrThrow<DrawableImage>()
            ?.drawable.asOrNull<IconDrawable>()!!.apply {
                assertEquals(iconDrawable, icon)
                assertNull(background)
                assertNull(iconSize)
            }


        IconDrawableStateImage(
            icon = android.R.drawable.ic_delete,
            background = greenBgDrawable
        ).getImage(sketch, request, null)
            ?.asOrThrow<DrawableImage>()
            ?.drawable.asOrNull<IconDrawable>()!!.apply {
                assertTrue(icon is BitmapDrawable)
                assertEquals(greenBgDrawable, background)
                assertNull(iconSize)
            }

        IconDrawableStateImage(
            icon = android.R.drawable.ic_delete,
            iconSize = SketchSize(30, 30),
            background = android.R.drawable.bottom_bar,
        ).getImage(sketch, request, null)
            ?.asOrThrow<DrawableImage>()
            ?.drawable.asOrNull<IconDrawable>()!!.apply {
                assertTrue(icon is BitmapDrawable)
                assertTrue(background is BitmapDrawable)
                assertEquals(Size(30, 30), iconSize)
            }

        IconDrawableStateImage(
            icon = android.R.drawable.ic_delete,
            background = IntColor(Color.BLUE)
        ).getImage(
            sketch,
            request,
            null
        )?.asOrThrow<DrawableImage>()?.drawable.asOrNull<IconDrawable>()!!.apply {
            assertTrue(icon is BitmapDrawable)
            assertEquals(Color.BLUE, (background as ColorDrawable).color)
            assertNull(iconSize)
        }

        IconDrawableStateImage(icon = android.R.drawable.ic_delete)
            .getImage(
                sketch,
                request,
                null
            )?.asOrThrow<DrawableImage>()?.drawable.asOrNull<IconDrawable>()!!.apply {
                assertTrue(icon is BitmapDrawable)
                assertNull(background)
                assertNull(iconSize)
            }
    }

    @Test
    fun testEqualsAndHashCode() {
        val element1 = IconDrawableStateImage(
            icon = android.R.drawable.ic_delete,
            background = android.R.drawable.bottom_bar
        )
        val element11 = IconDrawableStateImage(
            icon = android.R.drawable.ic_delete,
            background = android.R.drawable.bottom_bar
        )
        val element2 = IconDrawableStateImage(
            icon = android.R.drawable.ic_delete,
            background = android.R.drawable.btn_default
        )
        val element3 = IconDrawableStateImage(
            icon = android.R.drawable.btn_star,
            background = android.R.drawable.bottom_bar
        )
        val element4 = IconDrawableStateImage(icon = android.R.drawable.btn_star)

        assertNotSame(element1, element11)
        assertNotSame(element1, element2)
        assertNotSame(element1, element3)
        assertNotSame(element1, element4)
        assertNotSame(element2, element11)
        assertNotSame(element2, element3)
        assertNotSame(element2, element4)
        assertNotSame(element3, element4)

        assertEquals(element1, element1)
        assertEquals(element1, element11)
        assertNotEquals(element1, element2)
        assertNotEquals(element1, element3)
        assertNotEquals(element1, element4)
        assertNotEquals(element2, element11)
        assertNotEquals(element2, element3)
        assertNotEquals(element2, element4)
        assertNotEquals(element3, element4)
        assertNotEquals(element1, null as Any?)
        assertNotEquals(element1, Any())

        assertEquals(element1.hashCode(), element1.hashCode())
        assertEquals(element1.hashCode(), element11.hashCode())
        assertNotEquals(element1.hashCode(), element2.hashCode())
        assertNotEquals(element1.hashCode(), element3.hashCode())
        assertNotEquals(element1.hashCode(), element4.hashCode())
        assertNotEquals(element2.hashCode(), element11.hashCode())
        assertNotEquals(element2.hashCode(), element3.hashCode())
        assertNotEquals(element2.hashCode(), element4.hashCode())
        assertNotEquals(element3.hashCode(), element4.hashCode())
    }

    @Test
    fun testToString() {
        IconDrawableStateImage(icon = android.R.drawable.ic_delete).apply {
            assertEquals(
                "IconDrawableStateImage(icon=ResDrawable(${android.R.drawable.ic_delete}), background=null, iconSize=null)",
                toString()
            )
        }
        IconDrawableStateImage(
            icon = android.R.drawable.ic_delete,
            background = android.R.drawable.bottom_bar
        ).apply {
            assertEquals(
                "IconDrawableStateImage(icon=ResDrawable(${android.R.drawable.ic_delete}), background=ResDrawable(${android.R.drawable.bottom_bar}), iconSize=null)",
                toString()
            )
        }
        IconDrawableStateImage(
            icon = android.R.drawable.ic_delete,
            iconSize = SketchSize(50, 50)
        ).apply {
            assertEquals(
                "IconDrawableStateImage(icon=ResDrawable(${android.R.drawable.ic_delete}), background=null, iconSize=50x50)",
                toString()
            )
        }
        IconDrawableStateImage(
            icon = android.R.drawable.ic_delete,
            iconSize = SketchSize(50, 30)
        ).apply {
            assertEquals(
                "IconDrawableStateImage(icon=ResDrawable(${android.R.drawable.ic_delete}), background=null, iconSize=50x30)",
                toString()
            )
        }
        IconDrawableStateImage(
            icon = android.R.drawable.ic_delete,
            iconSize = Size(44, 67),
            background = android.R.drawable.btn_default,
        ).apply {
            assertEquals(
                "IconDrawableStateImage(icon=ResDrawable(${android.R.drawable.ic_delete}), background=ResDrawable(${android.R.drawable.btn_default}), iconSize=44x67)",
                toString()
            )
        }
    }
}