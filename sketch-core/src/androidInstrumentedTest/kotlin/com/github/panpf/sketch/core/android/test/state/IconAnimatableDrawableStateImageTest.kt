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

import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.github.panpf.sketch.AndroidDrawableImage
import com.github.panpf.sketch.drawable.ColorDrawableEqualizer
import com.github.panpf.sketch.drawable.IconAnimatableDrawable
import com.github.panpf.sketch.drawable.asEquality
import com.github.panpf.sketch.drawable.getEqualityDrawableCompat
import com.github.panpf.sketch.images.ResourceImages
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.state.IconAnimatableDrawableStateImage
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
class IconAnimatableDrawableStateImageTest {

    @Test
    fun createIconAnimatableDrawableStateImage() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val drawableIcon = androidx.core.R.drawable.ic_call_decline.let {
            context.getDrawable(it)!!.asEquality(it)
        }
        val resIcon = androidx.core.R.drawable.ic_call_answer
        val drawableBackground = androidx.core.R.drawable.notification_bg.let {
            context.getDrawable(it)!!.asEquality(it)
        }
        val resBackground = androidx.core.R.drawable.notification_template_icon_bg
        val intColorBackground = IntColor(Color.BLUE)
        val iconSize = Size(100, 100)
        val intIconTint = IntColor(Color.GREEN)
        val resIconTint = android.R.color.black

        // drawable icon
        IconAnimatableDrawableStateImage(
            icon = drawableIcon,
            background = drawableBackground,
            iconSize = iconSize,
            iconTint = resIconTint
        )
        IconAnimatableDrawableStateImage(
            icon = drawableIcon,
            background = resBackground,
            iconSize = iconSize,
            iconTint = resIconTint
        )
        IconAnimatableDrawableStateImage(
            icon = drawableIcon,
            background = intColorBackground,
            iconSize = iconSize,
            iconTint = resIconTint
        )
        IconAnimatableDrawableStateImage(
            icon = drawableIcon,
            background = drawableBackground,
            iconSize = iconSize,
            iconTint = intIconTint
        )
        IconAnimatableDrawableStateImage(
            icon = drawableIcon,
            background = resBackground,
            iconSize = iconSize,
            iconTint = intIconTint
        )
        IconAnimatableDrawableStateImage(
            icon = drawableIcon,
            background = intColorBackground,
            iconSize = iconSize,
            iconTint = intIconTint
        )

        IconAnimatableDrawableStateImage(
            icon = drawableIcon,
            background = drawableBackground,
            iconSize = iconSize,
        )
        IconAnimatableDrawableStateImage(
            icon = drawableIcon,
            background = resBackground,
            iconSize = iconSize,
        )
        IconAnimatableDrawableStateImage(
            icon = drawableIcon,
            background = intColorBackground,
            iconSize = iconSize,
        )

        IconAnimatableDrawableStateImage(
            icon = drawableIcon,
            background = drawableBackground,
            iconTint = resIconTint
        )
        IconAnimatableDrawableStateImage(
            icon = drawableIcon,
            background = resBackground,
            iconTint = resIconTint
        )
        IconAnimatableDrawableStateImage(
            icon = drawableIcon,
            background = intColorBackground,
            iconTint = resIconTint
        )

        IconAnimatableDrawableStateImage(
            icon = drawableIcon,
            background = drawableBackground,
            iconTint = intIconTint
        )
        IconAnimatableDrawableStateImage(
            icon = drawableIcon,
            background = resBackground,
            iconTint = intIconTint
        )
        IconAnimatableDrawableStateImage(
            icon = drawableIcon,
            background = intColorBackground,
            iconTint = intIconTint
        )

        IconAnimatableDrawableStateImage(
            icon = drawableIcon,
            iconSize = iconSize,
            iconTint = resIconTint
        )
        IconAnimatableDrawableStateImage(
            icon = drawableIcon,
            iconSize = iconSize,
            iconTint = intIconTint
        )

        IconAnimatableDrawableStateImage(
            icon = drawableIcon,
            background = drawableBackground,
        )
        IconAnimatableDrawableStateImage(
            icon = drawableIcon,
            background = resBackground,
        )
        IconAnimatableDrawableStateImage(
            icon = drawableIcon,
            background = intColorBackground,
        )

        IconAnimatableDrawableStateImage(
            icon = drawableIcon,
            iconSize = iconSize,
        )

        IconAnimatableDrawableStateImage(
            icon = drawableIcon,
            iconTint = resIconTint
        )
        IconAnimatableDrawableStateImage(
            icon = drawableIcon,
            iconTint = intIconTint
        )

        IconAnimatableDrawableStateImage(
            icon = drawableIcon,
        )

        // res icon
        IconAnimatableDrawableStateImage(
            icon = resIcon,
            background = drawableBackground,
            iconSize = iconSize,
            iconTint = resIconTint
        )
        IconAnimatableDrawableStateImage(
            icon = resIcon,
            background = resBackground,
            iconSize = iconSize,
            iconTint = resIconTint
        )
        IconAnimatableDrawableStateImage(
            icon = resIcon,
            background = intColorBackground,
            iconSize = iconSize,
            iconTint = resIconTint
        )
        IconAnimatableDrawableStateImage(
            icon = resIcon,
            background = drawableBackground,
            iconSize = iconSize,
            iconTint = intIconTint
        )
        IconAnimatableDrawableStateImage(
            icon = resIcon,
            background = resBackground,
            iconSize = iconSize,
            iconTint = intIconTint
        )
        IconAnimatableDrawableStateImage(
            icon = resIcon,
            background = intColorBackground,
            iconSize = iconSize,
            iconTint = intIconTint
        )

        IconAnimatableDrawableStateImage(
            icon = resIcon,
            background = drawableBackground,
            iconSize = iconSize,
        )
        IconAnimatableDrawableStateImage(
            icon = resIcon,
            background = resBackground,
            iconSize = iconSize,
        )
        IconAnimatableDrawableStateImage(
            icon = resIcon,
            background = intColorBackground,
            iconSize = iconSize,
        )

        IconAnimatableDrawableStateImage(
            icon = resIcon,
            background = drawableBackground,
            iconTint = resIconTint
        )
        IconAnimatableDrawableStateImage(
            icon = resIcon,
            background = resBackground,
            iconTint = resIconTint
        )
        IconAnimatableDrawableStateImage(
            icon = resIcon,
            background = intColorBackground,
            iconTint = resIconTint
        )

        IconAnimatableDrawableStateImage(
            icon = resIcon,
            background = drawableBackground,
            iconTint = intIconTint
        )
        IconAnimatableDrawableStateImage(
            icon = resIcon,
            background = resBackground,
            iconTint = intIconTint
        )
        IconAnimatableDrawableStateImage(
            icon = resIcon,
            background = intColorBackground,
            iconTint = intIconTint
        )

        IconAnimatableDrawableStateImage(
            icon = resIcon,
            iconSize = iconSize,
            iconTint = resIconTint
        )
        IconAnimatableDrawableStateImage(
            icon = resIcon,
            iconSize = iconSize,
            iconTint = intIconTint
        )

        IconAnimatableDrawableStateImage(
            icon = resIcon,
            background = drawableBackground,
        )
        IconAnimatableDrawableStateImage(
            icon = resIcon,
            background = resBackground,
        )
        IconAnimatableDrawableStateImage(
            icon = resIcon,
            background = intColorBackground,
        )

        IconAnimatableDrawableStateImage(
            icon = resIcon,
            iconSize = iconSize,
        )

        IconAnimatableDrawableStateImage(
            icon = resIcon,
            iconTint = resIconTint
        )
        IconAnimatableDrawableStateImage(
            icon = resIcon,
            iconTint = intIconTint
        )

        IconAnimatableDrawableStateImage(
            icon = resIcon,
        )
    }

    @Test
    fun testGetImage() {
        val (context, sketch) = getTestContextAndSketch()
        val request = ImageRequest(context, ResourceImages.jpeg.uri)
        val iconDrawable =
            context.getEqualityDrawableCompat(com.github.panpf.sketch.test.utils.core.R.drawable.ic_animated)
        val greenBgDrawable = ColorDrawableEqualizer(Color.GREEN)

        IconAnimatableDrawableStateImage(
            icon = iconDrawable,
            background = greenBgDrawable
        ).apply {
            getImage(sketch, request, null)
                ?.asOrThrow<AndroidDrawableImage>()?.drawable
                .asOrNull<IconAnimatableDrawable>()!!.apply {
                    assertEquals(iconDrawable.wrapped, icon)
                    assertEquals(greenBgDrawable.wrapped, background)
                    assertNull(iconSize)
                }
        }

        IconAnimatableDrawableStateImage(
            icon = iconDrawable,
            background = android.R.drawable.bottom_bar,
            iconSize = SketchSize(40, 40),
        ).apply {
            getImage(sketch, request, null)
                ?.asOrThrow<AndroidDrawableImage>()?.drawable
                .asOrNull<IconAnimatableDrawable>()!!.apply {
                    assertEquals(iconDrawable.wrapped, icon)
                    assertTrue(background is BitmapDrawable)
                    assertEquals(Size(40, 40), iconSize)
                }
        }

        IconAnimatableDrawableStateImage(
            icon = iconDrawable,
            background = IntColor(Color.BLUE)
        ).apply {
            getImage(sketch, request, null)
                ?.asOrThrow<AndroidDrawableImage>()?.drawable
                .asOrNull<IconAnimatableDrawable>()!!.apply {
                    assertEquals(iconDrawable.wrapped, icon)
                    assertEquals(Color.BLUE, (background as ColorDrawable).color)
                    assertNull(iconSize)
                }
        }

        IconAnimatableDrawableStateImage(iconDrawable).apply {
            getImage(sketch, request, null)
                ?.asOrThrow<AndroidDrawableImage>()?.drawable
                .asOrNull<IconAnimatableDrawable>()!!.apply {
                    assertEquals(iconDrawable.wrapped, icon)
                    assertNull(background)
                    assertNull(iconSize)
                }
        }


        IconAnimatableDrawableStateImage(
            icon = android.R.drawable.ic_delete,
            background = greenBgDrawable
        ).apply {
            assertNull(getImage(sketch, request, null))
        }
    }

    @Test
    fun testEqualsAndHashCode() {
        val element1 = IconAnimatableDrawableStateImage(
            icon = android.R.drawable.ic_delete,
            background = android.R.drawable.bottom_bar
        )
        val element11 = IconAnimatableDrawableStateImage(
            icon = android.R.drawable.ic_delete,
            background = android.R.drawable.bottom_bar
        )
        val element2 = IconAnimatableDrawableStateImage(
            icon = android.R.drawable.ic_delete,
            background = android.R.drawable.btn_default
        )
        val element3 = IconAnimatableDrawableStateImage(
            icon = android.R.drawable.btn_star,
            background = android.R.drawable.bottom_bar
        )
        val element4 = IconAnimatableDrawableStateImage(icon = android.R.drawable.btn_star)

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
        IconAnimatableDrawableStateImage(icon = android.R.drawable.ic_delete).apply {
            assertEquals(
                "IconAnimatableDrawableStateImage(icon=ResDrawable(${android.R.drawable.ic_delete}), background=null, iconSize=null)",
                toString()
            )
        }
        IconAnimatableDrawableStateImage(
            icon = android.R.drawable.ic_delete,
            background = android.R.drawable.bottom_bar
        ).apply {
            assertEquals(
                "IconAnimatableDrawableStateImage(icon=ResDrawable(${android.R.drawable.ic_delete}), background=ResDrawable(${android.R.drawable.bottom_bar}), iconSize=null)",
                toString()
            )
        }
        IconAnimatableDrawableStateImage(
            icon = android.R.drawable.ic_delete,
            iconSize = SketchSize(50, 50)
        ).apply {
            assertEquals(
                "IconAnimatableDrawableStateImage(icon=ResDrawable(${android.R.drawable.ic_delete}), background=null, iconSize=50x50)",
                toString()
            )
        }
        IconAnimatableDrawableStateImage(
            icon = android.R.drawable.ic_delete,
            iconSize = SketchSize(50, 30)
        ).apply {
            assertEquals(
                "IconAnimatableDrawableStateImage(icon=ResDrawable(${android.R.drawable.ic_delete}), background=null, iconSize=50x30)",
                toString()
            )
        }
        IconAnimatableDrawableStateImage(
            icon = android.R.drawable.ic_delete,
            iconSize = Size(44, 67),
            background = android.R.drawable.btn_default,
        ).apply {
            assertEquals(
                "IconAnimatableDrawableStateImage(icon=ResDrawable(${android.R.drawable.ic_delete}), background=ResDrawable(${android.R.drawable.btn_default}), iconSize=44x67)",
                toString()
            )
        }
    }
}