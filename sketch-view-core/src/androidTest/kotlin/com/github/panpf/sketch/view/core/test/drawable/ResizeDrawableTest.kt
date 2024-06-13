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
package com.github.panpf.sketch.view.core.test.drawable

import android.R.drawable
import android.graphics.Bitmap
import android.graphics.Bitmap.Config.RGB_565
import android.graphics.Rect
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.drawable.ResizeDrawable
import com.github.panpf.sketch.drawable.toLogString
import com.github.panpf.sketch.resize.Scale.CENTER_CROP
import com.github.panpf.sketch.resize.Scale.END_CROP
import com.github.panpf.sketch.resize.Scale.FILL
import com.github.panpf.sketch.resize.Scale.START_CROP
import com.github.panpf.sketch.test.utils.TestNewMutateDrawable
import com.github.panpf.sketch.test.utils.getDrawableCompat
import com.github.panpf.sketch.test.utils.getTestContext
import com.github.panpf.sketch.test.utils.intrinsicSize
import com.github.panpf.sketch.util.Size
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ResizeDrawableTest {

    // TODO test resize()

    @Test
    fun testIntrinsicSize() {
        val context = getTestContext()
        val resources = context.resources

        val bitmapDrawable = BitmapDrawable(resources, Bitmap.createBitmap(100, 200, RGB_565))
            .apply {
                Assert.assertEquals(Size(100, 200), intrinsicSize)
            }

        ResizeDrawable(bitmapDrawable, Size(500, 300), CENTER_CROP).apply {
            Assert.assertEquals(Size(500, 300), intrinsicSize)
            Assert.assertEquals(Size(500, 300), size)
            Assert.assertSame(bitmapDrawable, drawable)
        }
    }

    @Test
    fun testSetBounds() {
        val context = getTestContext()
        val resources = context.resources

        val bitmapDrawable = BitmapDrawable(resources, Bitmap.createBitmap(100, 200, RGB_565))
            .apply {
                Assert.assertEquals(Size(100, 200), intrinsicSize)
            }

        ResizeDrawable(bitmapDrawable, Size(500, 300), START_CROP).apply {
            setBounds(0, 0, 500, 300)
            Assert.assertEquals(Rect(0, 0, 500, 300), bounds)
            Assert.assertEquals(Rect(0, 0, 500, 1000), bitmapDrawable.bounds)
        }
        ResizeDrawable(bitmapDrawable, Size(500, 300), CENTER_CROP).apply {
            setBounds(0, 0, 500, 300)
            Assert.assertEquals(Rect(0, 0, 500, 300), bounds)
            Assert.assertEquals(Rect(0, -350, 500, 650), bitmapDrawable.bounds)
        }
        ResizeDrawable(bitmapDrawable, Size(500, 300), END_CROP).apply {
            setBounds(0, 0, 500, 300)
            Assert.assertEquals(Rect(0, 0, 500, 300), bounds)
            Assert.assertEquals(Rect(0, -700, 500, 300), bitmapDrawable.bounds)
        }
        ResizeDrawable(bitmapDrawable, Size(500, 300), FILL).apply {
            setBounds(0, 0, 500, 300)
            Assert.assertEquals(Rect(0, 0, 500, 300), bounds)
            Assert.assertEquals(Rect(0, 0, 500, 300), bitmapDrawable.bounds)
        }

        ResizeDrawable(
            ResizeDrawable(bitmapDrawable, Size(0, 300), CENTER_CROP),
            Size(500, 300),
            CENTER_CROP
        ).apply {
            setBounds(0, 0, 500, 300)
            Assert.assertEquals(Rect(0, 0, 500, 300), bounds)
            Assert.assertEquals(Rect(0, -350, 500, 650), bitmapDrawable.bounds)
        }
        ResizeDrawable(
            ResizeDrawable(bitmapDrawable, Size(300, 0), CENTER_CROP),
            Size(width = 500, height = 300),
            CENTER_CROP
        ).apply {
            setBounds(0, 0, 500, 300)
            Assert.assertEquals(Rect(0, 0, 500, 300), bounds)
            Assert.assertEquals(Rect(0, -150, 300, 450), bitmapDrawable.bounds)
        }
        ResizeDrawable(
            ResizeDrawable(bitmapDrawable, Size.Empty, CENTER_CROP),
            Size(500, 300),
            CENTER_CROP
        ).apply {
            setBounds(0, 0, 500, 300)
            Assert.assertEquals(Rect(0, 0, 500, 300), bounds)
            Assert.assertEquals(Rect(0, -350, 500, 650), bitmapDrawable.bounds)
        }

        val sketchDrawable = BitmapDrawable(
            resources,
            Bitmap.createBitmap(100, 200, RGB_565),
        )
        ResizeDrawable(sketchDrawable, Size(500, 300), CENTER_CROP).apply {
            setBounds(0, 0, 500, 300)
            Assert.assertEquals(Rect(0, 0, 500, 300), bounds)
            Assert.assertEquals(Rect(0, -350, 500, 650), bitmapDrawable.bounds)
        }
    }

    @Test
    fun testMutate() {
        val context = getTestContext()

        ResizeDrawable(
            context.getDrawableCompat(drawable.bottom_bar),
            Size(500, 300),
            CENTER_CROP
        ).apply {
            mutate()
            alpha = 146

            context.getDrawableCompat(android.R.drawable.bottom_bar).also {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    Assert.assertEquals(255, it.alpha)
                }
            }
        }

        ResizeDrawable(
            TestNewMutateDrawable(context.getDrawableCompat(drawable.bottom_bar)),
            Size(500, 300),
            CENTER_CROP
        ).apply {
            mutate()
            alpha = 146

            context.getDrawableCompat(android.R.drawable.bottom_bar).also {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    Assert.assertEquals(255, it.alpha)
                }
            }
        }
    }

    @Test
    fun testToString() {
        val context = getTestContext()
        val resources = context.resources

        val bitmapDrawable = BitmapDrawable(resources, Bitmap.createBitmap(100, 200, RGB_565))
            .apply {
                Assert.assertEquals(Size(100, 200), intrinsicSize)
            }

        ResizeDrawable(bitmapDrawable, Size(500, 300), CENTER_CROP).apply {
            Assert.assertEquals(
                "ResizeDrawable(drawable=${bitmapDrawable.toLogString()}, size=500x300, scale=CENTER_CROP)",
                toString()
            )
        }
    }
}
