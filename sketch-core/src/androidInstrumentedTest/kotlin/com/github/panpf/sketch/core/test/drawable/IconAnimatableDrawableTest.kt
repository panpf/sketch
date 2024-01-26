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
package com.github.panpf.sketch.core.test.drawable

import android.R
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.Bitmap.Config.ARGB_8888
import android.graphics.BlendMode.CLEAR
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.PixelFormat
import android.graphics.PorterDuff.Mode.DST
import android.graphics.PorterDuff.Mode.DST_IN
import android.graphics.PorterDuffColorFilter
import android.graphics.Rect
import android.graphics.drawable.Animatable
import android.graphics.drawable.ColorDrawable
import android.os.Build
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.github.panpf.sketch.test.utils.getTestContext
import com.github.panpf.sketch.drawable.IconAnimatableDrawable
import com.github.panpf.sketch.drawable.internal.calculateFitBounds
import com.github.panpf.sketch.test.utils.R.drawable
import com.github.panpf.sketch.util.Size
import com.github.panpf.sketch.util.asOrThrow
import com.github.panpf.sketch.util.getDrawableCompat
import com.github.panpf.tools4a.dimen.ktx.dp2px
import com.github.panpf.tools4j.test.ktx.assertThrow
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.math.roundToInt

@RunWith(AndroidJUnit4::class)
class IconAnimatableDrawableTest {

    @Test
    fun testConstructor() {
        val context = getTestContext()

        IconAnimatableDrawable(
            icon = context.getDrawableCompat(drawable.ic_animated)
        ).apply {
            Assert.assertTrue(icon is Animatable)
            Assert.assertNull(iconSize)
            Assert.assertNull(background)
        }

        IconAnimatableDrawable(
            icon = context.getDrawableCompat(drawable.ic_animated),
            background = ColorDrawable(Color.GREEN),
            iconSize = Size(69, 44),
        ).apply {
            Assert.assertTrue(icon is Animatable)
            Assert.assertEquals(Size(69, 44), iconSize)
            Assert.assertEquals(Color.GREEN, background!!.asOrThrow<ColorDrawable>().color)
        }

        assertThrow(IllegalArgumentException::class) {
            IconAnimatableDrawable(icon = context.getDrawableCompat(R.drawable.ic_delete))
        }
    }

//    @Test
//    fun testMutate() {
//        val context = getTestContext()
//
//        AnimatableIconDrawable(
//            icon = context.getDrawableCompat(com.github.panpf.sketch.test.utils.R.drawable.ic_animated),
//            background = ColorDrawable(Color.GREEN)
//        ).apply {
//            mutate()
//            alpha = 146
//
//            context.getDrawableCompat(com.github.panpf.sketch.test.utils.R.drawable.ic_animated).also {
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//                    Assert.assertEquals(255, it.alpha)
//                }
//            }
//        }
//
//        AnimatableIconDrawable(
//            icon = TestNewMutateDrawable(context.getDrawableCompat(com.github.panpf.sketch.test.utils.R.drawable.ic_animated)),
//            background = ColorDrawable(Color.GREEN)
//        ).apply {
//            mutate()
//            alpha = 146
//
//            context.getDrawableCompat(com.github.panpf.sketch.test.utils.R.drawable.ic_animated).also {
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//                    Assert.assertEquals(255, it.alpha)
//                }
//            }
//        }
//
//        AnimatableIconDrawable(
//            icon = context.getDrawableCompat(com.github.panpf.sketch.test.utils.R.drawable.ic_animated),
//            background = TestNewMutateDrawable(ColorDrawable(Color.GREEN))
//        ).apply {
//            mutate()
//            alpha = 146
//
//            context.getDrawableCompat(com.github.panpf.sketch.test.utils.R.drawable.ic_animated).also {
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//                    Assert.assertEquals(255, it.alpha)
//                }
//            }
//        }
//    }

    @Test
    fun testTint() {
        val context = getTestContext()

        IconAnimatableDrawable(
            context.getDrawableCompat(drawable.ic_animated)
        ).apply {
            setTint(Color.RED)
            setTintList(ColorStateList.valueOf(Color.GREEN))
            setTintMode(DST)
            if (Build.VERSION.SDK_INT >= 29) {
                setTintBlendMode(CLEAR)
            }
        }

        IconAnimatableDrawable(
            icon = context.getDrawableCompat(drawable.ic_animated),
            background = context.getDrawableCompat(R.drawable.ic_input_add)
        ).apply {
            setTint(Color.RED)
            setTintList(ColorStateList.valueOf(Color.GREEN))
            setTintMode(DST)
            if (Build.VERSION.SDK_INT >= 29) {
                setTintBlendMode(CLEAR)
            }
        }
    }

    @Test
    fun testColorFilter() {
        val context = getTestContext()

        IconAnimatableDrawable(
            icon = context.getDrawableCompat(drawable.ic_animated)
        ).apply {
            if (Build.VERSION.SDK_INT >= 21) {
                Assert.assertNull(colorFilter)
            }
            mutate()

            colorFilter = PorterDuffColorFilter(Color.BLUE, DST)
            if (Build.VERSION.SDK_INT >= 21) {
                Assert.assertTrue(colorFilter is PorterDuffColorFilter)
            }

            colorFilter = null
            if (Build.VERSION.SDK_INT >= 21) {
                Assert.assertNull(colorFilter)
            }

            @Suppress("DEPRECATION")
            setColorFilter(Color.RED, DST_IN)
            if (Build.VERSION.SDK_INT >= 21) {
                Assert.assertTrue(colorFilter is PorterDuffColorFilter)
            }
        }

        IconAnimatableDrawable(
            icon = context.getDrawableCompat(drawable.ic_animated),
            background = context.getDrawableCompat(R.drawable.ic_input_add)
        ).apply {
            if (Build.VERSION.SDK_INT >= 21) {
                Assert.assertNull(colorFilter)
            }
            mutate()

            colorFilter = PorterDuffColorFilter(Color.BLUE, DST)
            if (Build.VERSION.SDK_INT >= 21) {
                Assert.assertTrue(colorFilter is PorterDuffColorFilter)
            }

            colorFilter = null
            if (Build.VERSION.SDK_INT >= 21) {
                Assert.assertNull(colorFilter)
            }

            @Suppress("DEPRECATION")
            setColorFilter(Color.RED, DST_IN)
            if (Build.VERSION.SDK_INT >= 21) {
                Assert.assertTrue(colorFilter is PorterDuffColorFilter)
            }
        }
    }

    @Test
    fun testChange() {
        val context = getTestContext()

        IconAnimatableDrawable(
            icon = context.getDrawableCompat(drawable.ic_animated),
            background = ColorDrawable(Color.RED)
        ).apply {
            level = 4
            state = intArrayOf(android.R.attr.state_enabled)
        }

        IconAnimatableDrawable(
            icon = context.getDrawableCompat(drawable.ic_animated),
        ).apply {
            level = 4
            state = intArrayOf(android.R.attr.state_enabled)
        }
    }

    @Test
    fun testOpacity() {
        val context = getTestContext()

        IconAnimatableDrawable(
            icon = context.getDrawableCompat(drawable.ic_animated)
        ).apply {
            @Suppress("DEPRECATION")
            Assert.assertEquals(PixelFormat.TRANSLUCENT, opacity)
        }
    }

    @Test
    fun testDraw() {
        val context = getTestContext()

        IconAnimatableDrawable(
            icon = context.getDrawableCompat(drawable.ic_animated)
        ).apply {
            val canvas = Canvas(Bitmap.createBitmap(100, 100, ARGB_8888))
            draw(canvas)
        }

        IconAnimatableDrawable(
            icon = context.getDrawableCompat(drawable.ic_animated),
            background = ColorDrawable(Color.RED)
        ).apply {
            val canvas = Canvas(Bitmap.createBitmap(100, 100, ARGB_8888))
            draw(canvas)
        }
    }

    @Test
    fun testBounds() {
        val context = InstrumentationRegistry.getInstrumentation().context

        val icon =
            context.getDrawableCompat(com.github.panpf.sketch.test.utils.R.drawable.ic_animated)
        val iconIntrinsicSize = Size(icon.intrinsicWidth, icon.intrinsicHeight)
        Assert.assertEquals(/* expected = */ Size(48.dp2px, 48.dp2px),/* actual = */
            iconIntrinsicSize
        )
        Assert.assertEquals(Rect(0, 0, 0, 0), icon.bounds)

        val bgDrawable = ColorDrawable(Color.RED)
        Assert.assertEquals(Rect(0, 0, 0, 0), bgDrawable.bounds)

        val iconDrawable = IconAnimatableDrawable(icon = icon, background = bgDrawable)
        Assert.assertNull(iconDrawable.iconSize)
        Assert.assertEquals(Rect(0, 0, 0, 0), iconDrawable.bounds)

        val iconBoundsList = mutableListOf<Rect>()
        val boundsList = listOf(
            Rect(
                0,
                0,
                (icon.intrinsicWidth * 2f).toInt(),
                (icon.intrinsicHeight * 2f).toInt()
            ),
            Rect(
                0,
                0,
                (icon.intrinsicWidth * 0.5f).toInt(),
                (icon.intrinsicHeight * 0.5f).toInt()
            ),
            Rect(
                0,
                0,
                (icon.intrinsicWidth * 0.5f).toInt(),
                (icon.intrinsicHeight * 2f).toInt()
            ),
            Rect(
                0,
                0,
                (icon.intrinsicWidth * 2f).toInt(),
                (icon.intrinsicHeight * 0.5f).toInt()
            ),
        )
        boundsList.forEach { bounds ->
            iconDrawable.bounds = bounds

            val iconBounds = calculateFitBounds(iconIntrinsicSize, bounds)
            Assert.assertNotEquals("bounds=$bounds", Rect(0, 0, 0, 0), icon.bounds)
            Assert.assertEquals("bounds=$bounds", iconBounds, icon.bounds)
            Assert.assertEquals("bounds=$bounds", bounds, bgDrawable.bounds)

            iconBoundsList.add(Rect(icon.bounds))
        }

        Assert.assertEquals(4, iconBoundsList.size)
        Assert.assertEquals(4, iconBoundsList.distinct().size)
    }

    @Test
    fun testIconSize() {
        val context = InstrumentationRegistry.getInstrumentation().context

        val icon =
            context.getDrawableCompat(com.github.panpf.sketch.test.utils.R.drawable.ic_animated)
        val iconIntrinsicSize = Size(icon.intrinsicWidth, icon.intrinsicHeight)
        Assert.assertEquals(/* expected = */ Size(48.dp2px, 48.dp2px),/* actual = */
            iconIntrinsicSize
        )
        Assert.assertEquals(Rect(0, 0, 0, 0), icon.bounds)

        val bgDrawable = ColorDrawable(Color.RED)
        Assert.assertEquals(Rect(0, 0, 0, 0), bgDrawable.bounds)

        val iconSize = Size(
            (icon.intrinsicWidth * 1.3f).roundToInt(),
            (icon.intrinsicHeight * 1.3f).roundToInt()
        )
        Assert.assertNotEquals(iconSize, iconIntrinsicSize)

        val iconDrawable =
            IconAnimatableDrawable(icon = icon, background = bgDrawable, iconSize = iconSize)
        Assert.assertEquals(iconSize, iconDrawable.iconSize)
        Assert.assertEquals(Rect(0, 0, 0, 0), iconDrawable.bounds)

        val iconBoundsList = mutableListOf<Rect>()
        val boundsList = listOf(
            Rect(
                0,
                0,
                (icon.intrinsicWidth * 2f).toInt(),
                (icon.intrinsicHeight * 2f).toInt()
            ),
            Rect(
                0,
                0,
                (icon.intrinsicWidth * 0.5f).toInt(),
                (icon.intrinsicHeight * 0.5f).toInt()
            ),
            Rect(
                0,
                0,
                (icon.intrinsicWidth * 0.5f).toInt(),
                (icon.intrinsicHeight * 2f).toInt()
            ),
            Rect(
                0,
                0,
                (icon.intrinsicWidth * 2f).toInt(),
                (icon.intrinsicHeight * 0.5f).toInt()
            ),
        )
        boundsList.forEach { bounds ->
            iconDrawable.bounds = bounds

            val iconBounds = calculateFitBounds(iconSize, bounds)
            Assert.assertNotEquals("bounds=$bounds", Rect(0, 0, 0, 0), icon.bounds)
            Assert.assertEquals("bounds=$bounds", iconBounds, icon.bounds)
            Assert.assertEquals("bounds=$bounds", bounds, bgDrawable.bounds)

            iconBoundsList.add(Rect(icon.bounds))
        }

        Assert.assertEquals(4, iconBoundsList.size)
        Assert.assertEquals(4, iconBoundsList.distinct().size)
    }

    @Test
    fun testAlpha() {
        val context = InstrumentationRegistry.getInstrumentation().context

        IconAnimatableDrawable(icon = context.getDrawableCompat(drawable.ic_animated)).apply {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                Assert.assertEquals(255, alpha)
            }

            mutate()
            alpha = 144
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                Assert.assertEquals(144, alpha)
            }
        }
    }

    @Test
    fun testHotspot() {
        if (Build.VERSION.SDK_INT < 21) return
        val context = InstrumentationRegistry.getInstrumentation().context

        val iconDrawable =
            context.getDrawableCompat(com.github.panpf.sketch.test.utils.R.drawable.ic_animated)
        val bgDrawable = ColorDrawable(Color.RED)
        IconAnimatableDrawable(icon = iconDrawable, background = bgDrawable).apply {
            Assert.assertEquals(Rect(0, 0, 0, 0), Rect().apply { getHotspotBounds(this) })
            Assert.assertEquals(
                Rect(0, 0, 0, 0),
                Rect().apply { iconDrawable.getHotspotBounds(this) })
            Assert.assertEquals(
                Rect(0, 0, 0, 0),
                Rect().apply { bgDrawable.getHotspotBounds(this) })

            setHotspot(10f, 15f)
            Assert.assertEquals(
                Rect(0, 0, 0, 0),
                Rect().apply { getHotspotBounds(this) }
            )
            Assert.assertEquals(
                Rect(0, 0, 0, 0),
                Rect().apply { iconDrawable.getHotspotBounds(this) }
            )
            Assert.assertEquals(
                Rect(0, 0, 0, 0),
                Rect().apply { bgDrawable.getHotspotBounds(this) }
            )

            setHotspotBounds(0, 0, 10, 15)
            Assert.assertEquals(
                Rect(0, 0, 0, 0),
                Rect().apply { getHotspotBounds(this) }
            )
            Assert.assertEquals(
                Rect(0, 0, 0, 0),
                Rect().apply { iconDrawable.getHotspotBounds(this) }
            )
            Assert.assertEquals(
                Rect(0, 0, 0, 0),
                Rect().apply { bgDrawable.getHotspotBounds(this) }
            )
        }

        IconAnimatableDrawable(icon = iconDrawable).apply {
            Assert.assertEquals(Rect(0, 0, 0, 0), Rect().apply { getHotspotBounds(this) })
            Assert.assertEquals(
                Rect(0, 0, 0, 0),
                Rect().apply { iconDrawable.getHotspotBounds(this) })

            setHotspot(10f, 15f)
            Assert.assertEquals(
                Rect(0, 0, 0, 0),
                Rect().apply { getHotspotBounds(this) }
            )
            Assert.assertEquals(
                Rect(0, 0, 0, 0),
                Rect().apply { iconDrawable.getHotspotBounds(this) }
            )

            setHotspotBounds(0, 0, 10, 15)
            Assert.assertEquals(
                Rect(0, 0, 0, 0),
                Rect().apply { getHotspotBounds(this) }
            )
            Assert.assertEquals(
                Rect(0, 0, 0, 0),
                Rect().apply { iconDrawable.getHotspotBounds(this) }
            )
        }
    }

    @Test
    fun testAutoMirrored() {
        if (Build.VERSION.SDK_INT < 19) return
        val context = InstrumentationRegistry.getInstrumentation().context

        val iconDrawable =
            context.getDrawableCompat(com.github.panpf.sketch.test.utils.R.drawable.ic_animated)
        val bgDrawable = context.getDrawableCompat(android.R.drawable.editbox_background)
        IconAnimatableDrawable(icon = iconDrawable, background = bgDrawable).apply {
            Assert.assertFalse(isAutoMirrored)
            Assert.assertFalse(iconDrawable.isAutoMirrored)
            Assert.assertFalse(bgDrawable.isAutoMirrored)

            isAutoMirrored = true
            Assert.assertTrue(isAutoMirrored)
            Assert.assertEquals(Build.VERSION.SDK_INT <= 23, iconDrawable.isAutoMirrored)
            Assert.assertTrue(bgDrawable.isAutoMirrored)
        }

        iconDrawable.isAutoMirrored = false
        IconAnimatableDrawable(icon = iconDrawable).apply {
            Assert.assertFalse(isAutoMirrored)
            Assert.assertFalse(iconDrawable.isAutoMirrored)

            isAutoMirrored = true
            Assert.assertEquals(Build.VERSION.SDK_INT <= 23, isAutoMirrored)
            Assert.assertEquals(Build.VERSION.SDK_INT <= 23, iconDrawable.isAutoMirrored)
        }
    }

//    @Test
//    fun testPadding() {
//        val context = InstrumentationRegistry.getInstrumentation().context
//
//        val iconDrawable = context.getDrawableCompat(com.github.panpf.sketch.test.utils.R.drawable.ic_animated).apply {
//            Assert.assertFalse(Rect().apply { getPadding(this) }
//                .run { left == 0 && top == 0 && right == 0 && bottom == 0 })
//        }
//        val bgDrawable =
//            context.getDrawableCompat(android.R.drawable.editbox_background_normal).apply {
//                Assert.assertFalse(Rect().apply { getPadding(this) }
//                    .run { left == 0 && top == 0 && right == 0 && bottom == 0 })
//            }
//
//        AnimatableIconDrawable(icon = iconDrawable, background = bgDrawable).apply {
//            Assert.assertEquals(
//                Rect().apply { bgDrawable.getPadding(this) },
//                Rect().apply { getPadding(this) }
//            )
//        }
//
//        AnimatableIconDrawable(icon = iconDrawable).apply {
//            Assert.assertTrue(Rect().apply { getPadding(this) }
//                .run { left == 0 && top == 0 && right == 0 && bottom == 0 })
//        }
//    }

    @Test
    fun testTransparentRegion() {
        val context = InstrumentationRegistry.getInstrumentation().context

        val iconDrawable =
            context.getDrawableCompat(com.github.panpf.sketch.test.utils.R.drawable.ic_animated)
                .apply {
                    Assert.assertNull(transparentRegion)
                }
        val bgDrawable =
            context.getDrawableCompat(android.R.drawable.editbox_background_normal).apply {
                Assert.assertNull(transparentRegion)
            }

        IconAnimatableDrawable(icon = iconDrawable, background = bgDrawable).apply {
            Assert.assertNull(transparentRegion)
        }

        IconAnimatableDrawable(icon = iconDrawable).apply {
            Assert.assertNull(transparentRegion)
        }
    }

    @Test
    fun testFilterBitmap() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) return

        val context = InstrumentationRegistry.getInstrumentation().context

        val iconDrawable =
            context.getDrawableCompat(com.github.panpf.sketch.test.utils.R.drawable.ic_animated)
                .apply {
                    Assert.assertFalse(isFilterBitmap)
                    isFilterBitmap = true
                    Assert.assertFalse(isFilterBitmap)
                    isFilterBitmap = false
                }
        val bgDrawable =
            context.getDrawableCompat(android.R.drawable.editbox_background_normal).apply {
                Assert.assertFalse(isFilterBitmap)
                isFilterBitmap = true
                Assert.assertTrue(isFilterBitmap)
                isFilterBitmap = false
            }

        IconAnimatableDrawable(icon = iconDrawable, background = bgDrawable).apply {
            Assert.assertFalse(isFilterBitmap)

            isFilterBitmap = true
            Assert.assertTrue(isFilterBitmap)
            Assert.assertFalse(iconDrawable.isFilterBitmap)
            Assert.assertTrue(bgDrawable.isFilterBitmap)
        }
    }

    @Test
    fun testChangingConfigurations() {
        val context = InstrumentationRegistry.getInstrumentation().context

        val iconDrawable =
            context.getDrawableCompat(com.github.panpf.sketch.test.utils.R.drawable.ic_animated)
        val bgDrawable = context.getDrawableCompat(android.R.drawable.editbox_background_normal)
        val iconChangingConfigurations = iconDrawable.changingConfigurations
        val bgChangingConfigurations = bgDrawable.changingConfigurations

        iconDrawable.apply {
            changingConfigurations = 1
            Assert.assertEquals(iconChangingConfigurations + 1, changingConfigurations)
        }
        bgDrawable.apply {
            changingConfigurations = 2
            Assert.assertEquals(bgChangingConfigurations + 2, changingConfigurations)
        }

        IconAnimatableDrawable(icon = iconDrawable, background = bgDrawable).apply {
            changingConfigurations = 0
            Assert.assertEquals(iconChangingConfigurations, changingConfigurations)
            Assert.assertEquals(iconChangingConfigurations, iconDrawable.changingConfigurations)
            Assert.assertEquals(bgChangingConfigurations, bgDrawable.changingConfigurations)
        }

        iconDrawable.changingConfigurations = 1
        IconAnimatableDrawable(icon = iconDrawable).apply {
            Assert.assertEquals(iconChangingConfigurations + 1, changingConfigurations)

            changingConfigurations = 0
            Assert.assertEquals(iconChangingConfigurations, changingConfigurations)
            Assert.assertEquals(iconChangingConfigurations, iconDrawable.changingConfigurations)
            Assert.assertEquals(bgChangingConfigurations, bgDrawable.changingConfigurations)
        }
    }

    @Test
    fun testState() {
        val context = InstrumentationRegistry.getInstrumentation().context

        val iconDrawable =
            context.getDrawableCompat(com.github.panpf.sketch.test.utils.R.drawable.ic_animated)
        val bgDrawable = context.getDrawableCompat(android.R.drawable.editbox_background_normal)

        IconAnimatableDrawable(icon = iconDrawable, background = bgDrawable).apply {
            Assert.assertEquals(intArrayOf().toList(), state.toList())

            state = intArrayOf(1)
            Assert.assertEquals(intArrayOf(1).toList(), state.toList())
            Assert.assertEquals(intArrayOf(1).toList(), iconDrawable.state.toList())
            Assert.assertEquals(intArrayOf(1).toList(), bgDrawable.state.toList())

            bgDrawable.state = intArrayOf(2)
            Assert.assertEquals(intArrayOf(2).toList(), bgDrawable.state.toList())
            state = intArrayOf(1)
        }

        iconDrawable.state = intArrayOf()
        IconAnimatableDrawable(icon = iconDrawable).apply {
            Assert.assertEquals(intArrayOf().toList(), state.toList())

            state = intArrayOf(1)
            Assert.assertEquals(intArrayOf(1).toList(), state.toList())
            Assert.assertEquals(intArrayOf(1).toList(), iconDrawable.state.toList())
        }

        IconAnimatableDrawable(icon = iconDrawable, background = bgDrawable).apply {
            jumpToCurrentState()
        }
        IconAnimatableDrawable(icon = iconDrawable).apply {
            jumpToCurrentState()
        }
    }

    @Test
    fun testVisible() {
        val context = InstrumentationRegistry.getInstrumentation().context

        val iconDrawable =
            context.getDrawableCompat(com.github.panpf.sketch.test.utils.R.drawable.ic_animated)
        val bgDrawable = context.getDrawableCompat(android.R.drawable.editbox_background_normal)

        Assert.assertTrue(iconDrawable.isVisible)
        Assert.assertTrue(bgDrawable.isVisible)

        IconAnimatableDrawable(icon = iconDrawable, background = bgDrawable).apply {
            Assert.assertTrue(iconDrawable.isVisible)
            Assert.assertTrue(bgDrawable.isVisible)
            Assert.assertTrue(isVisible)

            iconDrawable.setVisible(false, false)
            Assert.assertFalse(iconDrawable.isVisible)
            Assert.assertTrue(bgDrawable.isVisible)
            Assert.assertTrue(isVisible)

            bgDrawable.setVisible(false, false)
            Assert.assertFalse(iconDrawable.isVisible)
            Assert.assertFalse(bgDrawable.isVisible)
            Assert.assertTrue(isVisible)

            iconDrawable.setVisible(true, false)
            Assert.assertTrue(iconDrawable.isVisible)
            Assert.assertFalse(bgDrawable.isVisible)
            Assert.assertTrue(isVisible)

            iconDrawable.setVisible(false, false)
            Assert.assertFalse(iconDrawable.isVisible)
            Assert.assertFalse(bgDrawable.isVisible)
            Assert.assertTrue(isVisible)

            setVisible(visible = true, restart = true)
            Assert.assertTrue(iconDrawable.isVisible)
            Assert.assertTrue(bgDrawable.isVisible)
            Assert.assertTrue(isVisible)

            setVisible(visible = false, restart = false)
            Assert.assertFalse(iconDrawable.isVisible)
            Assert.assertFalse(bgDrawable.isVisible)
            Assert.assertFalse(isVisible)
        }
    }

//    @Test
//    fun testStateful() {
//        val context = InstrumentationRegistry.getInstrumentation().context
//
//        val iconDrawable = context.getDrawableCompat(com.github.panpf.sketch.test.utils.R.drawable.ic_animated)
//        val bgDrawable = context.getDrawableCompat(android.R.drawable.editbox_background_normal)
//        AnimatableIconDrawable(icon = iconDrawable, background = bgDrawable).apply {
//            Assert.assertTrue(isStateful)
//            Assert.assertTrue(iconDrawable.isStateful)
//            Assert.assertFalse(bgDrawable.isStateful)
//        }
//
//        AnimatableIconDrawable(icon = iconDrawable).apply {
//            Assert.assertTrue(isStateful)
//            Assert.assertTrue(iconDrawable.isStateful)
//        }
//    }
}