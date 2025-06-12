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

package com.github.panpf.sketch.view.core.test.drawable

import android.graphics.Bitmap
import android.graphics.Bitmap.Config.RGB_565
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.widget.ImageView.ScaleType
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.drawable.ResizeAnimatableDrawable
import com.github.panpf.sketch.drawable.ResizeDrawable
import com.github.panpf.sketch.drawable.asEquitable
import com.github.panpf.sketch.drawable.resize
import com.github.panpf.sketch.resize.Scale
import com.github.panpf.sketch.test.utils.TestAnimatableDrawable
import com.github.panpf.sketch.test.utils.TestColor
import com.github.panpf.sketch.test.utils.TestNewMutateDrawable
import com.github.panpf.sketch.test.utils.getDrawableCompat
import com.github.panpf.sketch.test.utils.getTestContext
import com.github.panpf.sketch.test.utils.intrinsicSize
import com.github.panpf.sketch.util.Size
import com.github.panpf.sketch.util.toLogString
import org.junit.runner.RunWith
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNotSame
import kotlin.test.assertSame
import kotlin.test.assertTrue

@RunWith(AndroidJUnit4::class)
class ResizeDrawableTest {

    @Test
    fun testResize() {
        ColorDrawable(Color.GREEN)
            .resize(Size(100, 100))
            .apply {
                assertTrue(this !is ResizeAnimatableDrawable)
                assertEquals(ScaleType.CENTER_CROP, scaleType)
            }

        TestAnimatableDrawable(ColorDrawable(Color.YELLOW))
            .resize(Size(100, 100), scaleType = ScaleType.FIT_XY)
            .apply {
                assertTrue(this is ResizeAnimatableDrawable)
                assertEquals(ScaleType.FIT_XY, scaleType)
            }

        ColorDrawable(Color.GREEN)
            .resize(Size(100, 100), scale = Scale.START_CROP)
            .apply {
                assertTrue(this !is ResizeAnimatableDrawable)
                assertEquals(ScaleType.FIT_START, scaleType)
            }
        ColorDrawable(Color.GREEN)
            .resize(Size(100, 100), scale = Scale.CENTER_CROP)
            .apply {
                assertTrue(this !is ResizeAnimatableDrawable)
                assertEquals(ScaleType.CENTER_CROP, scaleType)
            }
        ColorDrawable(Color.GREEN)
            .resize(Size(100, 100), scale = Scale.END_CROP)
            .apply {
                assertTrue(this !is ResizeAnimatableDrawable)
                assertEquals(ScaleType.FIT_END, scaleType)
            }
        ColorDrawable(Color.GREEN)
            .resize(Size(100, 100), scale = Scale.FILL)
            .apply {
                assertTrue(this !is ResizeAnimatableDrawable)
                assertEquals(ScaleType.FIT_XY, scaleType)
            }
    }

    @Test
    fun testConstructor() {
        ResizeDrawable(
            drawable = ColorDrawable(Color.GREEN),
            size = Size(100, 100)
        ).apply {
            assertEquals(ScaleType.CENTER_CROP, scaleType)
        }

        ResizeDrawable(
            drawable = ColorDrawable(Color.GREEN),
            size = Size(100, 100),
            scaleType = ScaleType.FIT_XY
        ).apply {
            assertEquals(ScaleType.FIT_XY, scaleType)
        }

        ResizeDrawable(
            drawable = ColorDrawable(Color.GREEN),
            size = Size(100, 100),
            scale = Scale.START_CROP
        ).apply {
            assertEquals(ScaleType.FIT_START, scaleType)
        }
        ResizeDrawable(
            drawable = ColorDrawable(Color.GREEN),
            size = Size(100, 100),
            scale = Scale.CENTER_CROP
        ).apply {
            assertEquals(ScaleType.CENTER_CROP, scaleType)
        }
        ResizeDrawable(
            drawable = ColorDrawable(Color.GREEN),
            size = Size(100, 100),
            scale = Scale.END_CROP
        ).apply {
            assertEquals(ScaleType.FIT_END, scaleType)
        }
        ResizeDrawable(
            drawable = ColorDrawable(Color.GREEN),
            size = Size(100, 100),
            scale = Scale.FILL
        ).apply {
            assertEquals(ScaleType.FIT_XY, scaleType)
        }
    }

    @Test
    fun testIntrinsicSize() {
        val context = getTestContext()
        val resources = context.resources

        val bitmapDrawable = BitmapDrawable(resources, Bitmap.createBitmap(100, 200, RGB_565))
            .apply {
                assertEquals(Size(100, 200), intrinsicSize)
            }

        ResizeDrawable(bitmapDrawable, Size(500, 300)).apply {
            assertEquals(Size(500, 300), intrinsicSize)
            assertEquals(Size(500, 300), size)
            assertSame(bitmapDrawable, drawable)
        }
    }

    @Test
    fun testDraw() {
        // TODO test: Screenshot test or draw to Bitmap, then compare Bitmap
    }

    @Test
    fun testSetBounds() {
        val context = getTestContext()
        val resources = context.resources
        val size = Size(1000, 1000)

        val horSmallDrawable = BitmapDrawable(resources, Bitmap.createBitmap(303, 707, RGB_565))
//        ScaleType.values().forEach { scaleType ->
//            val resizeDrawable = ResizeDrawable(horSmallDrawable, size, scaleType)
//            resizeDrawable.setBounds(0, 0, size.width, size.height)
//            println("scaleType=$scaleType, bounds=${resizeDrawable.bounds.toBuildString()}, bitmapDrawable.bounds=${horSmallDrawable.bounds.toBuildString()}")
//        }
        listOf(
            ScaleType.MATRIX to Rect(0, 0, 303, 707),
            ScaleType.FIT_XY to Rect(0, 0, 1000, 1000),
            ScaleType.FIT_START to Rect(0, 0, 429, 1000),
            ScaleType.FIT_CENTER to Rect(285, 0, 715, 1000),
            ScaleType.FIT_END to Rect(571, 0, 1000, 1000),
            ScaleType.CENTER to Rect(348, 146, 652, 854),
            ScaleType.CENTER_CROP to Rect(0, -667, 1000, 1667),
            ScaleType.CENTER_INSIDE to Rect(348, 146, 652, 854),
        ).forEach { (scaleType, expected) ->
            val resizeDrawable = ResizeDrawable(horSmallDrawable, size, scaleType)
            resizeDrawable.setBounds(0, 0, size.width, size.height)
            assertEquals(Rect(0, 0, size.width, size.height), resizeDrawable.bounds)
            assertEquals(expected, horSmallDrawable.bounds)
        }

        val verSmallDrawable = BitmapDrawable(resources, Bitmap.createBitmap(707, 303, RGB_565))
//        ScaleType.values().forEach { scaleType ->
//            val resizeDrawable = ResizeDrawable(verSmallDrawable, size, scaleType)
//            resizeDrawable.setBounds(0, 0, size.width, size.height)
//            println("scaleType=$scaleType, bounds=${resizeDrawable.bounds.toBuildString()}, bitmapDrawable.bounds=${verSmallDrawable.bounds.toBuildString()}")
//        }
        listOf(
            ScaleType.MATRIX to Rect(0, 0, 707, 303),
            ScaleType.FIT_XY to Rect(0, 0, 1000, 1000),
            ScaleType.FIT_START to Rect(0, 0, 1000, 429),
            ScaleType.FIT_CENTER to Rect(0, 285, 1000, 715),
            ScaleType.FIT_END to Rect(0, 571, 1000, 1000),
            ScaleType.CENTER to Rect(146, 348, 854, 652),
            ScaleType.CENTER_CROP to Rect(-667, 0, 1667, 1000),
            ScaleType.CENTER_INSIDE to Rect(146, 348, 854, 652),
        ).forEach { (scaleType, expected) ->
            val resizeDrawable = ResizeDrawable(verSmallDrawable, size, scaleType)
            resizeDrawable.setBounds(0, 0, size.width, size.height)
            assertEquals(Rect(0, 0, size.width, size.height), resizeDrawable.bounds)
            assertEquals(expected, verSmallDrawable.bounds)
        }

        val horBigDrawable = BitmapDrawable(resources, Bitmap.createBitmap(3033, 7077, RGB_565))
//        ScaleType.values().forEach { scaleType ->
//            val resizeDrawable = ResizeDrawable(horBigDrawable, size, scaleType)
//            resizeDrawable.setBounds(0, 0, size.width, size.height)
//            println("scaleType=$scaleType, bounds=${resizeDrawable.bounds.toBuildString()}, bitmapDrawable.bounds=${horBigDrawable.bounds.toBuildString()}")
//        }
        listOf(
            ScaleType.MATRIX to Rect(0, 0, 3033, 7077),
            ScaleType.FIT_XY to Rect(0, 0, 1000, 1000),
            ScaleType.FIT_START to Rect(0, 0, 429, 1000),
            ScaleType.FIT_CENTER to Rect(285, 0, 715, 1000),
            ScaleType.FIT_END to Rect(571, 0, 1000, 1000),
            ScaleType.CENTER to Rect(-1017, -3039, 2017, 4039),
            ScaleType.CENTER_CROP to Rect(0, -667, 1000, 1667),
            ScaleType.CENTER_INSIDE to Rect(285, 0, 715, 1000),
        ).forEach { (scaleType, expected) ->
            val resizeDrawable = ResizeDrawable(horBigDrawable, size, scaleType)
            resizeDrawable.setBounds(0, 0, size.width, size.height)
            assertEquals(Rect(0, 0, size.width, size.height), resizeDrawable.bounds)
            assertEquals(expected, horBigDrawable.bounds)
        }

        val bitmapDrawable = BitmapDrawable(
            /* res = */ resources,
            /* bitmap = */ Bitmap.createBitmap(100, 200, RGB_565),
        )
        ResizeDrawable(
            drawable = ResizeDrawable(
                drawable = bitmapDrawable,
                size = Size(0, 300),
                scaleType = ScaleType.CENTER_CROP
            ),
            size = Size(500, 300),
            scaleType = ScaleType.CENTER_CROP
        ).apply {
            setBounds(0, 0, 500, 300)
            assertEquals(Rect(0, 0, 500, 300), bounds)
            assertEquals(Rect(0, -350, 500, 650), bitmapDrawable.bounds)
        }
        ResizeDrawable(
            drawable = ResizeDrawable(
                drawable = bitmapDrawable,
                size = Size(300, 0),
                scaleType = ScaleType.CENTER_CROP
            ),
            size = Size(width = 500, height = 300),
            scaleType = ScaleType.CENTER_CROP
        ).apply {
            setBounds(0, 0, 500, 300)
            assertEquals(Rect(0, 0, 500, 300), bounds)
            assertEquals(Rect(0, -350, 500, 650), bitmapDrawable.bounds)
        }
        ResizeDrawable(
            drawable = ResizeDrawable(
                drawable = bitmapDrawable,
                size = Size.Empty,
                scaleType = ScaleType.CENTER_CROP
            ),
            size = Size(500, 300),
            scaleType = ScaleType.CENTER_CROP
        ).apply {
            setBounds(0, 0, 500, 300)
            assertEquals(Rect(0, 0, 500, 300), bounds)
            assertEquals(Rect(0, -350, 500, 650), bitmapDrawable.bounds)
        }

        ResizeDrawable(
            drawable = bitmapDrawable,
            size = Size(500, 300),
            scaleType = ScaleType.CENTER_CROP
        ).apply {
            setBounds(0, 0, 500, 300)
            assertEquals(Rect(0, 0, 500, 300), bounds)
            assertEquals(Rect(0, -350, 500, 650), bitmapDrawable.bounds)
        }
    }

    @Test
    fun testMutate() {
        val context = getTestContext()

        ResizeDrawable(
            drawable = context.getDrawableCompat(android.R.drawable.ic_lock_lock),
            size = Size(500, 300),
        ).apply {
            val mutateDrawable = mutate()
            assertSame(this, mutateDrawable)
            mutateDrawable.alpha = 146

            context.getDrawableCompat(android.R.drawable.ic_lock_lock).also {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    assertEquals(255, it.alpha)
                }
            }
        }

        ResizeDrawable(
            drawable = TestNewMutateDrawable(context.getDrawableCompat(android.R.drawable.ic_lock_lock)),
            size = Size(500, 300),
        ).apply {
            val mutateDrawable = mutate()
            assertNotSame(this, mutateDrawable)
            mutateDrawable.alpha = 146

            context.getDrawableCompat(android.R.drawable.ic_lock_lock).also {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    assertEquals(255, it.alpha)
                }
            }
        }
    }

    @Test
    fun testEqualsAndHashCode() {
        val element1 = ResizeDrawable(
            drawable = ColorDrawable(TestColor.RED).asEquitable(),
            size = Size(100, 500),
        )
        val element11 = ResizeDrawable(
            drawable = ColorDrawable(TestColor.RED).asEquitable(),
            size = Size(100, 500),
        )
        val element2 = ResizeDrawable(
            drawable = ColorDrawable(TestColor.GREEN).asEquitable(),
            size = Size(100, 500),
        )
        val element3 = ResizeDrawable(
            drawable = ColorDrawable(TestColor.RED).asEquitable(),
            size = Size(500, 100),
        )
        val element4 = ResizeDrawable(
            drawable = ColorDrawable(TestColor.RED).asEquitable(),
            size = Size(100, 500),
            scaleType = ScaleType.FIT_XY,
        )

        assertEquals(element1, element11)
        assertNotEquals(element1, element2)
        assertNotEquals(element1, element3)
        assertNotEquals(element1, element4)
        assertNotEquals(element2, element3)
        assertNotEquals(element2, element4)
        assertNotEquals(element3, element4)
        assertNotEquals(element1, null as Any?)
        assertNotEquals(element1, Any())

        assertEquals(element1.hashCode(), element11.hashCode())
        assertNotEquals(element1.hashCode(), element2.hashCode())
        assertNotEquals(element1.hashCode(), element3.hashCode())
        assertNotEquals(element1.hashCode(), element4.hashCode())
        assertNotEquals(element2.hashCode(), element3.hashCode())
        assertNotEquals(element2.hashCode(), element4.hashCode())
        assertNotEquals(element3.hashCode(), element4.hashCode())
    }

    @Test
    fun testToString() {
        val context = getTestContext()
        val resources = context.resources

        val bitmapDrawable = BitmapDrawable(resources, Bitmap.createBitmap(100, 200, RGB_565))
            .apply {
                assertEquals(Size(100, 200), intrinsicSize)
            }

        ResizeDrawable(bitmapDrawable, Size(500, 300)).apply {
            assertEquals(
                "ResizeDrawable(drawable=${bitmapDrawable.toLogString()}, size=500x300, scaleType=CENTER_CROP)",
                toString()
            )
        }
    }
}
