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
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.drawable.ResizeAnimatableDrawable
import com.github.panpf.sketch.drawable.ResizeDrawable
import com.github.panpf.sketch.drawable.asEquitable
import com.github.panpf.sketch.drawable.resize
import com.github.panpf.sketch.resize.Scale.CENTER_CROP
import com.github.panpf.sketch.resize.Scale.END_CROP
import com.github.panpf.sketch.resize.Scale.FILL
import com.github.panpf.sketch.resize.Scale.START_CROP
import com.github.panpf.sketch.test.utils.TestAnimatableDrawable
import com.github.panpf.sketch.test.utils.TestColor
import com.github.panpf.sketch.test.utils.getTestContext
import com.github.panpf.sketch.test.utils.intrinsicSize
import com.github.panpf.sketch.util.Size
import com.github.panpf.sketch.util.toLogString
import org.junit.runner.RunWith
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertSame
import kotlin.test.assertTrue

@RunWith(AndroidJUnit4::class)
class ResizeDrawableTest {

    @Test
    fun testResize() {
        ColorDrawable(Color.GREEN).resize(Size(100, 100)).apply {
            assertTrue(this !is ResizeAnimatableDrawable)
        }

        TestAnimatableDrawable().resize(Size(100, 100)).apply {
            assertTrue(this is ResizeAnimatableDrawable)
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

        ResizeDrawable(bitmapDrawable, Size(500, 300), CENTER_CROP).apply {
            assertEquals(Size(500, 300), intrinsicSize)
            assertEquals(Size(500, 300), size)
            assertSame(bitmapDrawable, drawable)
        }
    }

    @Test
    fun testSetBounds() {
        val context = getTestContext()
        val resources = context.resources

        val bitmapDrawable = BitmapDrawable(resources, Bitmap.createBitmap(100, 200, RGB_565))
            .apply {
                assertEquals(Size(100, 200), intrinsicSize)
            }

        ResizeDrawable(bitmapDrawable, Size(500, 300), START_CROP).apply {
            setBounds(0, 0, 500, 300)
            assertEquals(Rect(0, 0, 500, 300), bounds)
            assertEquals(Rect(0, 0, 500, 1000), bitmapDrawable.bounds)
        }
        ResizeDrawable(bitmapDrawable, Size(500, 300), CENTER_CROP).apply {
            setBounds(0, 0, 500, 300)
            assertEquals(Rect(0, 0, 500, 300), bounds)
            assertEquals(Rect(0, -350, 500, 650), bitmapDrawable.bounds)
        }
        ResizeDrawable(bitmapDrawable, Size(500, 300), END_CROP).apply {
            setBounds(0, 0, 500, 300)
            assertEquals(Rect(0, 0, 500, 300), bounds)
            assertEquals(Rect(0, -700, 500, 300), bitmapDrawable.bounds)
        }
        ResizeDrawable(bitmapDrawable, Size(500, 300), FILL).apply {
            setBounds(0, 0, 500, 300)
            assertEquals(Rect(0, 0, 500, 300), bounds)
            assertEquals(Rect(0, 0, 500, 300), bitmapDrawable.bounds)
        }

        ResizeDrawable(
            ResizeDrawable(bitmapDrawable, Size(0, 300), CENTER_CROP),
            Size(500, 300),
            CENTER_CROP
        ).apply {
            setBounds(0, 0, 500, 300)
            assertEquals(Rect(0, 0, 500, 300), bounds)
            assertEquals(Rect(0, -350, 500, 650), bitmapDrawable.bounds)
        }
        ResizeDrawable(
            ResizeDrawable(bitmapDrawable, Size(300, 0), CENTER_CROP),
            Size(width = 500, height = 300),
            CENTER_CROP
        ).apply {
            setBounds(0, 0, 500, 300)
            assertEquals(Rect(0, 0, 500, 300), bounds)
            assertEquals(Rect(0, -150, 300, 450), bitmapDrawable.bounds)
        }
        ResizeDrawable(
            ResizeDrawable(bitmapDrawable, Size.Empty, CENTER_CROP),
            Size(500, 300),
            CENTER_CROP
        ).apply {
            setBounds(0, 0, 500, 300)
            assertEquals(Rect(0, 0, 500, 300), bounds)
            assertEquals(Rect(0, -350, 500, 650), bitmapDrawable.bounds)
        }

        val sketchDrawable = BitmapDrawable(
            resources,
            Bitmap.createBitmap(100, 200, RGB_565),
        )
        ResizeDrawable(sketchDrawable, Size(500, 300), CENTER_CROP).apply {
            setBounds(0, 0, 500, 300)
            assertEquals(Rect(0, 0, 500, 300), bounds)
            assertEquals(Rect(0, -350, 500, 650), bitmapDrawable.bounds)
        }
    }

    @Test
    fun testMutate() {
        // TODO testMutate
//        val context = getTestContext()
//
//        ResizeDrawable(
//            context.getDrawableCompat(drawable.bottom_bar),
//            Size(500, 300),
//            CENTER_CROP
//        ).apply {
//            mutate()
//            alpha = 146
//
//            context.getDrawableCompat(android.R.drawable.bottom_bar).also {
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//                    assertEquals(255, it.alpha)
//                }
//            }
//        }
//
//        ResizeDrawable(
//            TestNewMutateDrawable(context.getDrawableCompat(drawable.bottom_bar)),
//            Size(500, 300),
//            CENTER_CROP
//        ).apply {
//            mutate()
//            alpha = 146
//
//            context.getDrawableCompat(android.R.drawable.bottom_bar).also {
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//                    assertEquals(255, it.alpha)
//                }
//            }
//        }
    }

    @Test
    fun testEqualsAndHashCode() {
        val element1 = ResizeDrawable(
            drawable = ColorDrawable(TestColor.RED).asEquitable(),
            size = Size(100, 500),
            scale = CENTER_CROP,
        )
        val element11 = ResizeDrawable(
            drawable = ColorDrawable(TestColor.RED).asEquitable(),
            size = Size(100, 500),
            scale = CENTER_CROP,
        )
        val element2 = ResizeDrawable(
            drawable = ColorDrawable(TestColor.GREEN).asEquitable(),
            size = Size(100, 500),
            scale = CENTER_CROP,
        )
        val element3 = ResizeDrawable(
            drawable = ColorDrawable(TestColor.RED).asEquitable(),
            size = Size(500, 100),
            scale = CENTER_CROP,
        )
        val element4 = ResizeDrawable(
            drawable = ColorDrawable(TestColor.RED).asEquitable(),
            size = Size(100, 500),
            scale = START_CROP,
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

        ResizeDrawable(bitmapDrawable, Size(500, 300), CENTER_CROP).apply {
            assertEquals(
                "ResizeDrawable(drawable=${bitmapDrawable.toLogString()}, size=500x300, scale=CENTER_CROP)",
                toString()
            )
        }
    }
}
