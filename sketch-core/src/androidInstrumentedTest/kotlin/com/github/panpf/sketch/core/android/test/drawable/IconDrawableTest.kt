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

package com.github.panpf.sketch.core.android.test.drawable

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
import android.graphics.drawable.ColorDrawable
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.github.panpf.sketch.drawable.IconDrawable
import com.github.panpf.sketch.drawable.asEquitable
import com.github.panpf.sketch.test.utils.SizeColorDrawable
import com.github.panpf.sketch.test.utils.SizeDrawable
import com.github.panpf.sketch.test.utils.TestColor
import com.github.panpf.sketch.test.utils.TestNewMutateDrawable
import com.github.panpf.sketch.test.utils.asEquitable
import com.github.panpf.sketch.test.utils.getTestContext
import com.github.panpf.sketch.test.utils.intrinsicSize
import com.github.panpf.sketch.util.Size
import com.github.panpf.sketch.util.asOrThrow
import com.github.panpf.sketch.util.getDrawableCompat
import com.github.panpf.sketch.util.toSketchRect
import com.github.panpf.tools4a.dimen.ktx.dp2px
import org.junit.runner.RunWith
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.min
import kotlin.math.roundToInt
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertNotSame
import kotlin.test.assertNull
import kotlin.test.assertSame
import kotlin.test.assertTrue

@RunWith(AndroidJUnit4::class)
class IconDrawableTest {

    @Test
    fun testConstructor() {
        IconDrawable(
            icon = SizeDrawable(ColorDrawable(Color.GREEN).asEquitable(), Size(100, 100)),
        ).apply {
            assertEquals(
                expected = SizeDrawable(ColorDrawable(Color.GREEN).asEquitable(), Size(100, 100)),
                actual = icon
            )
            assertNull(background)
            assertNull(iconSize)
            assertNull(iconTint)
        }

        IconDrawable(
            icon = SizeDrawable(ColorDrawable(Color.GREEN).asEquitable(), Size(100, 100)),
            background = ColorDrawable(Color.BLUE),
            iconSize = Size(69, 44),
            iconTint = Color.RED,
        ).apply {
            assertEquals(
                expected = SizeDrawable(ColorDrawable(Color.GREEN).asEquitable(), Size(100, 100)),
                actual = icon
            )
            assertEquals(Color.BLUE, background!!.asOrThrow<ColorDrawable>().color)
            assertEquals(Size(69, 44), iconSize)
            assertEquals(Color.RED, iconTint)
        }

        assertFailsWith(IllegalArgumentException::class) {
            IconDrawable(icon = ColorDrawable(Color.GREEN))
        }
        assertFailsWith(IllegalArgumentException::class) {
            IconDrawable(icon = ColorDrawable(Color.GREEN), iconSize = Size(-1, -1))
        }
        IconDrawable(icon = ColorDrawable(Color.GREEN), iconSize = Size(100, 100))

        assertFailsWith(IllegalArgumentException::class) {
            IconDrawable(icon = SizeDrawable(ColorDrawable(Color.GREEN), Size(-1, -1)))
        }
        assertFailsWith(IllegalArgumentException::class) {
            IconDrawable(
                icon = SizeDrawable(ColorDrawable(Color.GREEN), Size(-1, -1)),
                iconSize = Size(-1, -1)
            )
        }
        IconDrawable(
            icon = SizeDrawable(ColorDrawable(Color.GREEN), Size(-1, -1)),
            iconSize = Size(100, 100)
        )
        IconDrawable(
            icon = SizeDrawable(ColorDrawable(Color.GREEN), Size(100, 100)),
        )
    }

    @Test
    fun testIntrinsicSize() {
        IconDrawable(
            icon = SizeColorDrawable(Color.RED, Size(101, 202)).asEquitable(),
            background = null
        ).apply {
            assertEquals(expected = Size(-1, -1), actual = intrinsicSize)
        }

        IconDrawable(
            icon = SizeColorDrawable(Color.RED, Size(101, 202)).asEquitable(),
            background = SizeColorDrawable(Color.GRAY, Size(1000, 500)).asEquitable()
        ).apply {
            assertEquals(expected = Size(-1, -1), actual = intrinsicSize)
        }
    }

    @Test
    fun testTint() {
        val context = getTestContext()

        IconDrawable(
            context.getDrawableCompat(android.R.drawable.ic_delete)
        ).apply {
            setTint(Color.RED)
            setTintList(ColorStateList.valueOf(Color.GREEN))
            setTintMode(DST)
            if (VERSION.SDK_INT >= 29) {
                setTintBlendMode(CLEAR)
            }
        }

        IconDrawable(
            icon = context.getDrawableCompat(android.R.drawable.ic_delete),
            background = context.getDrawableCompat(android.R.drawable.ic_input_add)
        ).apply {
            setTint(Color.RED)
            setTintList(ColorStateList.valueOf(Color.GREEN))
            setTintMode(DST)
            if (VERSION.SDK_INT >= 29) {
                setTintBlendMode(CLEAR)
            }
        }
    }

    @Test
    fun testColorFilter() {
        val context = getTestContext()

        IconDrawable(
            icon = context.getDrawableCompat(android.R.drawable.ic_delete)
        ).apply {
            if (VERSION.SDK_INT >= 21) {
                assertNull(colorFilter)
            }
            mutate()

            colorFilter = PorterDuffColorFilter(Color.BLUE, DST)
            if (VERSION.SDK_INT >= 21) {
                assertTrue(colorFilter is PorterDuffColorFilter)
            }

            colorFilter = null
            if (VERSION.SDK_INT >= 21) {
                assertNull(colorFilter)
            }

            @Suppress("DEPRECATION")
            setColorFilter(Color.RED, DST_IN)
            if (VERSION.SDK_INT >= 21) {
                assertTrue(colorFilter is PorterDuffColorFilter)
            }
        }

        IconDrawable(
            icon = context.getDrawableCompat(android.R.drawable.ic_delete),
            background = context.getDrawableCompat(android.R.drawable.ic_input_add)
        ).apply {
            if (VERSION.SDK_INT >= 21) {
                assertNull(colorFilter)
            }
            mutate()

            colorFilter = PorterDuffColorFilter(Color.BLUE, DST)
            if (VERSION.SDK_INT >= 21) {
                assertTrue(colorFilter is PorterDuffColorFilter)
            }

            colorFilter = null
            if (VERSION.SDK_INT >= 21) {
                assertNull(colorFilter)
            }

            @Suppress("DEPRECATION")
            setColorFilter(Color.RED, DST_IN)
            if (VERSION.SDK_INT >= 21) {
                assertTrue(colorFilter is PorterDuffColorFilter)
            }
        }
    }

    @Test
    fun testChange() {
        val context = getTestContext()

        IconDrawable(
            icon = context.getDrawableCompat(android.R.drawable.ic_delete),
            background = ColorDrawable(Color.RED)
        ).apply {
            level = 4
            state = intArrayOf(android.R.attr.state_enabled)
        }

        IconDrawable(
            icon = context.getDrawableCompat(android.R.drawable.ic_delete),
        ).apply {
            level = 4
            state = intArrayOf(android.R.attr.state_enabled)
        }
    }

    @Test
    fun testOpacity() {
        val context = getTestContext()

        IconDrawable(
            icon = context.getDrawableCompat(android.R.drawable.ic_delete)
        ).apply {
            @Suppress("DEPRECATION")
            assertEquals(PixelFormat.TRANSLUCENT, opacity)
        }
    }

    @Test
    fun testDraw() {
        val context = getTestContext()

        IconDrawable(
            icon = context.getDrawableCompat(android.R.drawable.ic_delete)
        ).apply {
            val canvas = Canvas(Bitmap.createBitmap(100, 100, ARGB_8888))
            draw(canvas)
        }

        IconDrawable(
            icon = context.getDrawableCompat(android.R.drawable.ic_delete),
            background = ColorDrawable(Color.RED)
        ).apply {
            val canvas = Canvas(Bitmap.createBitmap(100, 100, ARGB_8888))
            draw(canvas)
        }

        // TODO test: Screenshot test or draw to Bitmap, then compare Bitmap
    }

    @Test
    fun testBounds() {
        val context = InstrumentationRegistry.getInstrumentation().context

        val icon =
            context.getDrawableCompat(com.github.panpf.sketch.test.R.drawable.ic_circle)
        val iconIntrinsicSize = Size(icon.intrinsicWidth, icon.intrinsicHeight)
        assertEquals(
            Size(50.dp2px, 50.dp2px),
            iconIntrinsicSize
        )
        assertEquals(Rect(0, 0, 0, 0), icon.bounds)

        val bgDrawable = ColorDrawable(Color.RED)
        assertEquals(Rect(0, 0, 0, 0), bgDrawable.bounds)

        val iconDrawable = IconDrawable(icon = icon, background = bgDrawable)
        assertNull(iconDrawable.iconSize)
        assertEquals(Rect(0, 0, 0, 0), iconDrawable.bounds)

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

            val iconBounds = calculateInsideBounds(iconIntrinsicSize, bounds.toSketchRect())
            assertNotEquals(Rect(0, 0, 0, 0), icon.bounds, "bounds=$bounds")
            assertEquals(iconBounds, icon.bounds.toSketchRect(), "bounds=$bounds")
            assertEquals(bounds, bgDrawable.bounds, "bounds=$bounds")

            iconBoundsList.add(Rect(icon.bounds))
        }

        assertEquals(4, iconBoundsList.size)
        assertEquals(4, iconBoundsList.distinct().size)
    }

    @Test
    fun testIconSize() {
        val context = InstrumentationRegistry.getInstrumentation().context

        val icon =
            context.getDrawableCompat(com.github.panpf.sketch.test.R.drawable.ic_circle)
        val iconIntrinsicSize = Size(icon.intrinsicWidth, icon.intrinsicHeight)
        assertEquals(
            Size(50.dp2px, 50.dp2px),
            iconIntrinsicSize
        )
        assertEquals(Rect(0, 0, 0, 0), icon.bounds)

        val bgDrawable = ColorDrawable(Color.RED)
        assertEquals(Rect(0, 0, 0, 0), bgDrawable.bounds)

        val iconSize = Size(
            (icon.intrinsicWidth * 1.3f).roundToInt(),
            (icon.intrinsicHeight * 1.3f).roundToInt()
        )
        assertNotEquals(iconSize, iconIntrinsicSize)

        val iconDrawable = IconDrawable(icon = icon, background = bgDrawable, iconSize = iconSize)
        assertEquals(iconSize, iconDrawable.iconSize)
        assertEquals(Rect(0, 0, 0, 0), iconDrawable.bounds)

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

            val iconBounds = calculateInsideBounds(iconSize, bounds.toSketchRect())
            assertNotEquals(Rect(0, 0, 0, 0), icon.bounds, "bounds=$bounds")
            assertEquals(iconBounds, icon.bounds.toSketchRect(), "bounds=$bounds")
            assertEquals(bounds, bgDrawable.bounds, "bounds=$bounds")

            iconBoundsList.add(Rect(icon.bounds))
        }

        assertEquals(4, iconBoundsList.size)
        assertEquals(4, iconBoundsList.distinct().size)
    }

    @Test
    fun testAlpha() {
        val context = InstrumentationRegistry.getInstrumentation().context

        IconDrawable(icon = context.getDrawableCompat(android.R.drawable.ic_delete)).apply {
            if (VERSION.SDK_INT >= VERSION_CODES.KITKAT) {
                assertEquals(255, alpha)
            }

            mutate()
            alpha = 144
            if (VERSION.SDK_INT >= VERSION_CODES.KITKAT) {
                assertEquals(144, alpha)
            }
        }
    }

    @Test
    fun testHotspot() {
        if (VERSION.SDK_INT < 21) return
        val context = InstrumentationRegistry.getInstrumentation().context

        val iconDrawable = context.getDrawableCompat(android.R.drawable.ic_delete)
        val bgDrawable = ColorDrawable(Color.RED)
        IconDrawable(icon = iconDrawable, background = bgDrawable).apply {
            assertEquals(Rect(0, 0, 0, 0), Rect().apply { getHotspotBounds(this) })
            assertEquals(
                Rect(0, 0, 0, 0),
                Rect().apply { iconDrawable.getHotspotBounds(this) })
            assertEquals(
                Rect(0, 0, 0, 0),
                Rect().apply { bgDrawable.getHotspotBounds(this) })

            setHotspot(10f, 15f)
            assertEquals(
                Rect(0, 0, 0, 0),
                Rect().apply { getHotspotBounds(this) }
            )
            assertEquals(
                Rect(0, 0, 0, 0),
                Rect().apply { iconDrawable.getHotspotBounds(this) }
            )
            assertEquals(
                Rect(0, 0, 0, 0),
                Rect().apply { bgDrawable.getHotspotBounds(this) }
            )

            setHotspotBounds(0, 0, 10, 15)
            assertEquals(
                Rect(0, 0, 0, 0),
                Rect().apply { getHotspotBounds(this) }
            )
            assertEquals(
                Rect(0, 0, 0, 0),
                Rect().apply { iconDrawable.getHotspotBounds(this) }
            )
            assertEquals(
                Rect(0, 0, 0, 0),
                Rect().apply { bgDrawable.getHotspotBounds(this) }
            )
        }

        IconDrawable(icon = iconDrawable).apply {
            assertEquals(Rect(0, 0, 0, 0), Rect().apply { getHotspotBounds(this) })
            assertEquals(
                Rect(0, 0, 0, 0),
                Rect().apply { iconDrawable.getHotspotBounds(this) })

            setHotspot(10f, 15f)
            assertEquals(
                Rect(0, 0, 0, 0),
                Rect().apply { getHotspotBounds(this) }
            )
            assertEquals(
                Rect(0, 0, 0, 0),
                Rect().apply { iconDrawable.getHotspotBounds(this) }
            )

            setHotspotBounds(0, 0, 10, 15)
            assertEquals(
                Rect(0, 0, 0, 0),
                Rect().apply { getHotspotBounds(this) }
            )
            assertEquals(
                Rect(0, 0, 0, 0),
                Rect().apply { iconDrawable.getHotspotBounds(this) }
            )
        }
    }

    @Test
    fun testAutoMirrored() {
        if (VERSION.SDK_INT < 19) return
        val context = InstrumentationRegistry.getInstrumentation().context

        val iconDrawable = context.getDrawableCompat(android.R.drawable.ic_delete)
        val bgDrawable = context.getDrawableCompat(android.R.drawable.editbox_background)
        IconDrawable(icon = iconDrawable, background = bgDrawable).apply {
            isAutoMirrored = false
            assertFalse(isAutoMirrored)
            assertFalse(iconDrawable.isAutoMirrored)
            assertFalse(bgDrawable.isAutoMirrored)

            isAutoMirrored = true
            assertTrue(isAutoMirrored)
            assertTrue(iconDrawable.isAutoMirrored)
            assertTrue(bgDrawable.isAutoMirrored)

            iconDrawable.isAutoMirrored = false
            bgDrawable.isAutoMirrored = true
            assertTrue(isAutoMirrored)
            assertFalse(iconDrawable.isAutoMirrored)
            assertTrue(bgDrawable.isAutoMirrored)

            iconDrawable.isAutoMirrored = true
            bgDrawable.isAutoMirrored = false
            assertTrue(isAutoMirrored)
            assertTrue(iconDrawable.isAutoMirrored)
            assertFalse(bgDrawable.isAutoMirrored)
        }

        iconDrawable.isAutoMirrored = false
        IconDrawable(icon = iconDrawable).apply {
            assertFalse(isAutoMirrored)
            assertFalse(iconDrawable.isAutoMirrored)

            isAutoMirrored = true
            assertTrue(isAutoMirrored)
            assertTrue(iconDrawable.isAutoMirrored)

            iconDrawable.isAutoMirrored = false
            assertFalse(isAutoMirrored)
            assertFalse(iconDrawable.isAutoMirrored)
        }
    }

    @Test
    fun testPadding() {
        val context = InstrumentationRegistry.getInstrumentation().context

        val iconDrawable = context.getDrawableCompat(android.R.drawable.spinner_background).apply {
            assertFalse(Rect().apply { getPadding(this) }
                .run { left == 0 && top == 0 && right == 0 && bottom == 0 })
        }
        val bgDrawable =
            context.getDrawableCompat(android.R.drawable.editbox_background_normal).apply {
                assertFalse(Rect().apply { getPadding(this) }
                    .run { left == 0 && top == 0 && right == 0 && bottom == 0 })
            }

        IconDrawable(icon = iconDrawable, background = bgDrawable).apply {
            assertEquals(
                Rect().apply { bgDrawable.getPadding(this) },
                Rect().apply { getPadding(this) }
            )
        }

        IconDrawable(icon = iconDrawable).apply {
            assertTrue(Rect().apply { getPadding(this) }
                .run { left == 0 && top == 0 && right == 0 && bottom == 0 })
        }
    }

    @Test
    fun testTransparentRegion() {
        val context = InstrumentationRegistry.getInstrumentation().context

        val iconDrawable = context.getDrawableCompat(android.R.drawable.spinner_background).apply {
            assertNull(transparentRegion)
        }
        val bgDrawable =
            context.getDrawableCompat(android.R.drawable.editbox_background_normal).apply {
                assertNull(transparentRegion)
            }

        IconDrawable(icon = iconDrawable, background = bgDrawable).apply {
            assertNull(transparentRegion)
        }

        IconDrawable(icon = iconDrawable).apply {
            assertNull(transparentRegion)
        }
    }

    @Test
    fun testFilterBitmap() {
        if (VERSION.SDK_INT < VERSION_CODES.M) return

        val context = InstrumentationRegistry.getInstrumentation().context

        val iconDrawable = context.getDrawableCompat(android.R.drawable.spinner_background).apply {
            assertFalse(isFilterBitmap)
            isFilterBitmap = true
            assertFalse(isFilterBitmap)
            isFilterBitmap = false
        }
        val bgDrawable =
            context.getDrawableCompat(android.R.drawable.editbox_background_normal).apply {
                assertFalse(isFilterBitmap)
                isFilterBitmap = true
                assertTrue(isFilterBitmap)
                isFilterBitmap = false
            }

        IconDrawable(icon = iconDrawable, background = bgDrawable).apply {
            assertFalse(isFilterBitmap)

            isFilterBitmap = true
            assertTrue(isFilterBitmap)
            assertFalse(iconDrawable.isFilterBitmap)
            assertTrue(bgDrawable.isFilterBitmap)
        }

        iconDrawable.isFilterBitmap = false
        bgDrawable.isFilterBitmap = false
        IconDrawable(icon = bgDrawable).apply {
            assertFalse(isFilterBitmap)

            isFilterBitmap = true
            assertTrue(isFilterBitmap)
            assertFalse(iconDrawable.isFilterBitmap)
            assertTrue(bgDrawable.isFilterBitmap)
        }
    }

    @Test
    fun testChangingConfigurations() {
        val context = InstrumentationRegistry.getInstrumentation().context

        val iconDrawable = context.getDrawableCompat(android.R.drawable.spinner_background)
        val bgDrawable = context.getDrawableCompat(android.R.drawable.editbox_background_normal)
        val iconChangingConfigurations = iconDrawable.changingConfigurations
        val bgChangingConfigurations = bgDrawable.changingConfigurations

        iconDrawable.apply {
            changingConfigurations = 1
            assertEquals(iconChangingConfigurations + 1, changingConfigurations)
        }
        bgDrawable.apply {
            changingConfigurations = 2
            assertEquals(bgChangingConfigurations + 2, changingConfigurations)
        }

        IconDrawable(icon = iconDrawable, background = bgDrawable).apply {
            changingConfigurations = 0
            assertEquals(iconChangingConfigurations, changingConfigurations)
            assertEquals(iconChangingConfigurations, iconDrawable.changingConfigurations)
            assertEquals(bgChangingConfigurations, bgDrawable.changingConfigurations)
        }

        iconDrawable.changingConfigurations = 1
        IconDrawable(icon = iconDrawable).apply {
            assertEquals(iconChangingConfigurations + 1, changingConfigurations)

            changingConfigurations = 0
            assertEquals(iconChangingConfigurations, changingConfigurations)
            assertEquals(iconChangingConfigurations, iconDrawable.changingConfigurations)
            assertEquals(bgChangingConfigurations, bgDrawable.changingConfigurations)
        }
    }

    @Test
    fun testState() {
        val context = InstrumentationRegistry.getInstrumentation().context

        val iconDrawable = context.getDrawableCompat(android.R.drawable.spinner_background)
        val bgDrawable = context.getDrawableCompat(android.R.drawable.editbox_background_normal)

        IconDrawable(icon = iconDrawable, background = bgDrawable).apply {
            assertEquals(intArrayOf().toList(), state.toList())

            state = intArrayOf(1)
            assertEquals(intArrayOf(1).toList(), state.toList())
            assertEquals(intArrayOf(1).toList(), iconDrawable.state.toList())
            assertEquals(intArrayOf(1).toList(), bgDrawable.state.toList())

            bgDrawable.state = intArrayOf(2)
            assertEquals(intArrayOf(2).toList(), bgDrawable.state.toList())
            state = intArrayOf(1)
        }

        iconDrawable.state = intArrayOf()
        IconDrawable(icon = iconDrawable).apply {
            assertEquals(intArrayOf().toList(), state.toList())

            state = intArrayOf(1)
            assertEquals(intArrayOf(1).toList(), state.toList())
            assertEquals(intArrayOf(1).toList(), iconDrawable.state.toList())
        }

        IconDrawable(icon = iconDrawable, background = bgDrawable).apply {
            jumpToCurrentState()
        }
        IconDrawable(icon = iconDrawable).apply {
            jumpToCurrentState()
        }
    }

    @Test
    fun testVisible() {
        val context = InstrumentationRegistry.getInstrumentation().context

        val iconDrawable = context.getDrawableCompat(android.R.drawable.spinner_background)
        val bgDrawable = context.getDrawableCompat(android.R.drawable.editbox_background_normal)

        assertTrue(iconDrawable.isVisible)
        assertTrue(bgDrawable.isVisible)

        IconDrawable(icon = iconDrawable, background = bgDrawable).apply {
            assertTrue(iconDrawable.isVisible)
            assertTrue(bgDrawable.isVisible)
            assertTrue(isVisible)

            iconDrawable.setVisible(false, false)
            assertFalse(iconDrawable.isVisible)
            assertTrue(bgDrawable.isVisible)
            assertTrue(isVisible)

            bgDrawable.setVisible(false, false)
            assertFalse(iconDrawable.isVisible)
            assertFalse(bgDrawable.isVisible)
            assertTrue(isVisible)

            iconDrawable.setVisible(true, false)
            assertTrue(iconDrawable.isVisible)
            assertFalse(bgDrawable.isVisible)
            assertTrue(isVisible)

            iconDrawable.setVisible(false, false)
            assertFalse(iconDrawable.isVisible)
            assertFalse(bgDrawable.isVisible)
            assertTrue(isVisible)

            setVisible(visible = true, restart = true)
            assertTrue(iconDrawable.isVisible)
            assertTrue(bgDrawable.isVisible)
            assertTrue(isVisible)

            setVisible(visible = false, restart = false)
            assertFalse(iconDrawable.isVisible)
            assertFalse(bgDrawable.isVisible)
            assertFalse(isVisible)
        }
    }

    @Test
    fun testStateful() {
        val context = InstrumentationRegistry.getInstrumentation().context

        val iconDrawable = context.getDrawableCompat(android.R.drawable.spinner_background)
        val bgDrawable = context.getDrawableCompat(android.R.drawable.editbox_background_normal)
        IconDrawable(icon = iconDrawable, background = bgDrawable).apply {
            assertTrue(isStateful)
            assertTrue(iconDrawable.isStateful)
            assertFalse(bgDrawable.isStateful)
        }

        IconDrawable(icon = iconDrawable).apply {
            assertTrue(isStateful)
            assertTrue(iconDrawable.isStateful)
        }
    }

    @Test
    fun testMutate() {
        val context = getTestContext()

        IconDrawable(
            icon = context.getDrawableCompat(android.R.drawable.bottom_bar),
        ).apply {
            val mutateDrawable = mutate()
            assertSame(this, mutateDrawable)
            mutateDrawable.alpha = 146

            context.getDrawableCompat(android.R.drawable.bottom_bar).also {
                if (VERSION.SDK_INT >= VERSION_CODES.KITKAT) {
                    assertEquals(255, it.alpha)
                }
            }
        }

        IconDrawable(
            icon = TestNewMutateDrawable(context.getDrawableCompat(android.R.drawable.bottom_bar)),
        ).apply {
            val mutateDrawable = mutate()
            assertNotSame(this, mutateDrawable)
            mutateDrawable.alpha = 146

            context.getDrawableCompat(android.R.drawable.bottom_bar).also {
                if (VERSION.SDK_INT >= VERSION_CODES.KITKAT) {
                    assertEquals(255, it.alpha)
                }
            }
        }

        IconDrawable(
            icon = context.getDrawableCompat(android.R.drawable.bottom_bar),
            background = TestNewMutateDrawable(ColorDrawable(Color.RED)),
        ).apply {
            val mutateDrawable = mutate()
            assertNotSame(this, mutateDrawable)
            mutateDrawable.alpha = 146

            context.getDrawableCompat(android.R.drawable.bottom_bar).also {
                if (VERSION.SDK_INT >= VERSION_CODES.KITKAT) {
                    assertEquals(255, it.alpha)
                }
            }
        }
    }

    @Test
    fun testEqualsAndHashCode() {
        val element1 = IconDrawable(
            icon = SizeDrawable(ColorDrawable(TestColor.RED).asEquitable(), Size(100, 100)),
        )
        val element11 = IconDrawable(
            icon = SizeDrawable(ColorDrawable(TestColor.RED).asEquitable(), Size(100, 100)),
        )
        val element2 = IconDrawable(
            icon = SizeDrawable(ColorDrawable(TestColor.GREEN).asEquitable(), Size(100, 100)),
        )
        val element3 = IconDrawable(
            icon = SizeDrawable(ColorDrawable(TestColor.RED).asEquitable(), Size(100, 100)),
            background = ColorDrawable(TestColor.GRAY).asEquitable(),
        )
        val element4 = IconDrawable(
            icon = SizeDrawable(ColorDrawable(TestColor.RED).asEquitable(), Size(100, 100)),
            iconSize = Size(69, 44),
        )
        val element5 = IconDrawable(
            icon = SizeDrawable(ColorDrawable(TestColor.RED).asEquitable(), Size(100, 100)),
            iconTint = TestColor.BLUE,
        )

        assertEquals(element1, element11)
        assertNotEquals(element1, element2)
        assertNotEquals(element1, element3)
        assertNotEquals(element1, element4)
        assertNotEquals(element1, element5)
        assertNotEquals(element2, element3)
        assertNotEquals(element2, element4)
        assertNotEquals(element2, element5)
        assertNotEquals(element3, element4)
        assertNotEquals(element3, element5)
        assertNotEquals(element4, element5)
        assertNotEquals(element1, null as Any?)
        assertNotEquals(element1, Any())

        assertEquals(element1.hashCode(), element11.hashCode())
        assertNotEquals(element1.hashCode(), element2.hashCode())
        assertNotEquals(element1.hashCode(), element3.hashCode())
        assertNotEquals(element1.hashCode(), element4.hashCode())
        assertNotEquals(element1.hashCode(), element5.hashCode())
        assertNotEquals(element2.hashCode(), element3.hashCode())
        assertNotEquals(element2.hashCode(), element4.hashCode())
        assertNotEquals(element2.hashCode(), element5.hashCode())
        assertNotEquals(element3.hashCode(), element4.hashCode())
        assertNotEquals(element3.hashCode(), element5.hashCode())
        assertNotEquals(element4.hashCode(), element5.hashCode())
    }

    @Test
    fun testToString() {
        assertEquals(
            expected = "IconDrawable(icon=SizeDrawable(drawable=ColorDrawable(color=-65536), size=100x100), background=ColorDrawable(color=-7829368), iconSize=69x44, iconTint=-16776961)",
            actual = IconDrawable(
                icon = SizeDrawable(ColorDrawable(TestColor.RED), Size(100, 100)),
                background = ColorDrawable(TestColor.GRAY),
                iconSize = Size(69, 44),
                iconTint = TestColor.BLUE
            ).toString()
        )
    }

    /**
     * Calculate the bounds of the Drawable to inside the container.
     *
     * @see com.github.panpf.sketch.core.common.test.util.CoreUtilsTest.testCalculateInsideBounds
     */
    private fun calculateInsideBounds(
        contentSize: Size,
        containerBounds: com.github.panpf.sketch.util.Rect
    ): com.github.panpf.sketch.util.Rect {
        val containerWidth = containerBounds.width()
        val containerHeight = containerBounds.height()
        if (contentSize.width <= containerWidth && contentSize.height <= containerHeight) {
            // center
            val left = containerBounds.left + (containerWidth - contentSize.width) / 2f
            val top = containerBounds.top + (containerHeight - contentSize.height) / 2f
            val right = left + contentSize.width
            val bottom = top + contentSize.height
            return com.github.panpf.sketch.util.Rect(
                /* left = */ floor(left).toInt(),
                /* top = */ floor(top).toInt(),
                /* right = */ ceil(right).toInt(),
                /* bottom = */ ceil(bottom).toInt()
            )
        } else {
            // fit
            val widthScale = containerWidth.toFloat() / contentSize.width
            val heightScale = containerHeight.toFloat() / contentSize.height
            val scale = min(widthScale, heightScale)
            val scaledWidth = contentSize.width * scale
            val scaledHeight = contentSize.height * scale
            val left = containerBounds.left + (containerWidth - scaledWidth) / 2f
            val top = containerBounds.top + (containerHeight - scaledHeight) / 2f
            val right = left + scaledWidth
            val bottom = top + scaledHeight
            return com.github.panpf.sketch.util.Rect(
                /* left = */ floor(left).toInt(),
                /* top = */ floor(top).toInt(),
                /* right = */ ceil(right).toInt(),
                /* bottom = */ ceil(bottom).toInt()
            )
        }
    }
}