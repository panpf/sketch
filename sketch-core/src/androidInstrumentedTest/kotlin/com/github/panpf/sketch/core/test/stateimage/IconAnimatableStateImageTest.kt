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
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.github.panpf.sketch.state.IconAnimatableStateImage
import com.github.panpf.sketch.state.IntColor
import com.github.panpf.sketch.util.Size
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class IconAnimatableStateImageTest {

    @Test
    fun createFunctionTest() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val iconSize = Size(100, 100)
        val intIconTine = IntColor(Color.GREEN)
        val resIconTine = android.R.color.black

        // icon drawable, background drawable
        val drawableIcon = context.getDrawable(androidx.core.R.drawable.ic_call_decline)!!
        val drawableBackground = context.getDrawable(androidx.core.R.drawable.notification_bg)
        IconAnimatableStateImage(
            icon = drawableIcon,
            background = drawableBackground,
            iconSize = iconSize,
            iconTint = resIconTine
        )
        IconAnimatableStateImage(
            icon = drawableIcon,
            background = drawableBackground,
            iconSize = iconSize,
            iconTint = intIconTine
        )

        IconAnimatableStateImage(
            icon = drawableIcon,
            background = drawableBackground,
            iconSize = iconSize,
        )
        IconAnimatableStateImage(
            icon = drawableIcon,
            background = drawableBackground,
            iconTint = resIconTine
        )
        IconAnimatableStateImage(
            icon = drawableIcon,
            background = drawableBackground,
            iconTint = intIconTine
        )
        IconAnimatableStateImage(
            icon = drawableIcon,
            iconSize = iconSize,
            iconTint = resIconTine
        )
        IconAnimatableStateImage(
            icon = drawableIcon,
            iconSize = iconSize,
            iconTint = intIconTine
        )

        IconAnimatableStateImage(
            icon = drawableIcon,
            background = drawableBackground,
        )
        IconAnimatableStateImage(
            icon = drawableIcon,
            iconSize = iconSize,
        )
        IconAnimatableStateImage(
            icon = drawableIcon,
            iconTint = resIconTine
        )
        IconAnimatableStateImage(
            icon = drawableIcon,
            iconTint = intIconTine
        )

        IconAnimatableStateImage(
            icon = drawableIcon,
        )

        // icon res, background res
        val resIcon = androidx.core.R.drawable.ic_call_answer
        val resBackground = androidx.core.R.drawable.notification_template_icon_bg
        IconAnimatableStateImage(
            icon = resIcon,
            background = resBackground,
            iconSize = iconSize,
            iconTint = resIconTine
        )
        IconAnimatableStateImage(
            icon = resIcon,
            background = resBackground,
            iconSize = iconSize,
            iconTint = intIconTine
        )

        IconAnimatableStateImage(
            icon = resIcon,
            background = resBackground,
            iconSize = iconSize,
        )
        IconAnimatableStateImage(
            icon = resIcon,
            background = resBackground,
            iconTint = resIconTine
        )
        IconAnimatableStateImage(
            icon = resIcon,
            background = resBackground,
            iconTint = intIconTine
        )
        IconAnimatableStateImage(
            icon = resIcon,
            iconSize = iconSize,
            iconTint = resIconTine
        )
        IconAnimatableStateImage(
            icon = resIcon,
            iconSize = iconSize,
            iconTint = intIconTine
        )

        IconAnimatableStateImage(
            icon = resIcon,
            background = resBackground,
        )
        IconAnimatableStateImage(
            icon = resIcon,
            iconSize = iconSize,
        )
        IconAnimatableStateImage(
            icon = resIcon,
            iconTint = resIconTine
        )
        IconAnimatableStateImage(
            icon = resIcon,
            iconTint = intIconTine
        )

        IconAnimatableStateImage(
            icon = resIcon,
        )

        // icon drawable, background int color
        val intColorBackground = IntColor(Color.BLUE)
        IconAnimatableStateImage(
            icon = resIcon,
            background = intColorBackground,
            iconSize = iconSize,
            iconTint = resIconTine
        )
        IconAnimatableStateImage(
            icon = resIcon,
            background = intColorBackground,
            iconSize = iconSize,
            iconTint = intIconTine
        )

        IconAnimatableStateImage(
            icon = resIcon,
            background = intColorBackground,
            iconSize = iconSize,
        )
        IconAnimatableStateImage(
            icon = resIcon,
            background = intColorBackground,
            iconTint = resIconTine
        )
        IconAnimatableStateImage(
            icon = resIcon,
            background = intColorBackground,
            iconTint = intIconTine
        )

        IconAnimatableStateImage(
            icon = resIcon,
            background = intColorBackground,
        )
    }

    @Test
    fun testGetDrawable() {
        val (context, sketch) = getTestContextAndSketch()
        val request = ImageRequest(context, AssetImages.jpeg.uri)
        val iconDrawable =
            context.getDrawableCompat(com.github.panpf.sketch.test.utils.R.drawable.ic_animated)
        val greenBgDrawable = ColorDrawable(Color.GREEN)

        IconAnimatableStateImage(iconDrawable) {
            background(greenBgDrawable)
        }.apply {
            getImage(sketch, request, null)
                ?.asOrThrow<DrawableImage>()?.drawable
                .asOrNull<IconAnimatableDrawable>()!!.apply {
                    Assert.assertEquals(iconDrawable, icon)
                    Assert.assertEquals(greenBgDrawable, background)
                    Assert.assertNull(iconSize)
                }
        }

        IconAnimatableStateImage(iconDrawable) {
            iconSize(40)
            resBackground(android.R.drawable.bottom_bar)
        }.apply {
            getImage(sketch, request, null)
                ?.asOrThrow<DrawableImage>()?.drawable
                .asOrNull<IconAnimatableDrawable>()!!.apply {
                    Assert.assertEquals(iconDrawable, icon)
                    Assert.assertTrue(background is BitmapDrawable)
                    Assert.assertEquals(Size(40, 40), iconSize)
                }
        }

        IconAnimatableStateImage(iconDrawable) {
            colorBackground(Color.BLUE)
        }.apply {
            getImage(sketch, request, null)
                ?.asOrThrow<DrawableImage>()?.drawable
                .asOrNull<IconAnimatableDrawable>()!!.apply {
                    Assert.assertEquals(iconDrawable, icon)
                    Assert.assertEquals(Color.BLUE, (background as ColorDrawable).color)
                    Assert.assertNull(iconSize)
                }
        }

        IconAnimatableStateImage(iconDrawable).apply {
            getImage(sketch, request, null)
                ?.asOrThrow<DrawableImage>()?.drawable
                .asOrNull<IconAnimatableDrawable>()!!.apply {
                    Assert.assertEquals(iconDrawable, icon)
                    Assert.assertNull(background)
                    Assert.assertNull(iconSize)
                }
        }


        IconAnimatableStateImage(android.R.drawable.ic_delete) {
            background(greenBgDrawable)
        }.apply {
            Assert.assertNull(getImage(sketch, request, null))
        }
    }

    @Test
    fun testEqualsAndHashCode() {
        val element1 = IconAnimatableStateImage(android.R.drawable.ic_delete) {
            resBackground(android.R.drawable.bottom_bar)
        }
        val element11 = IconAnimatableStateImage(android.R.drawable.ic_delete) {
            resBackground(android.R.drawable.bottom_bar)
        }
        val element2 = IconAnimatableStateImage(android.R.drawable.ic_delete) {
            resBackground(android.R.drawable.btn_default)
        }
        val element3 = IconAnimatableStateImage(android.R.drawable.btn_star) {
            resBackground(android.R.drawable.bottom_bar)
        }
        val element4 = IconAnimatableStateImage(android.R.drawable.btn_star)

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
        IconAnimatableStateImage(android.R.drawable.ic_delete).apply {
            Assert.assertEquals(
                "IconAnimatableStateImage(icon=ResDrawable(${android.R.drawable.ic_delete}), background=null, iconSize=null)",
                toString()
            )
        }
        IconAnimatableStateImage(android.R.drawable.ic_delete) {
            resBackground(android.R.drawable.bottom_bar)
        }.apply {
            Assert.assertEquals(
                "IconAnimatableStateImage(icon=ResDrawable(${android.R.drawable.ic_delete}), background=ResDrawable(${android.R.drawable.bottom_bar}), iconSize=null)",
                toString()
            )
        }
        IconAnimatableStateImage(android.R.drawable.ic_delete) {
            iconSize(50)
        }.apply {
            Assert.assertEquals(
                "IconAnimatableStateImage(icon=ResDrawable(${android.R.drawable.ic_delete}), background=null, iconSize=50x50)",
                toString()
            )
        }
        IconAnimatableStateImage(android.R.drawable.ic_delete) {
            iconSize(50, 30)
        }.apply {
            Assert.assertEquals(
                "IconAnimatableStateImage(icon=ResDrawable(${android.R.drawable.ic_delete}), background=null, iconSize=50x30)",
                toString()
            )
        }
        IconAnimatableStateImage(android.R.drawable.ic_delete) {
            iconSize(Size(44, 67))
            resBackground(android.R.drawable.btn_default)
        }.apply {
            Assert.assertEquals(
                "IconAnimatableStateImage(icon=ResDrawable(${android.R.drawable.ic_delete}), background=ResDrawable(${android.R.drawable.btn_default}), iconSize=44x67)",
                toString()
            )
        }
    }
}