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

package com.github.panpf.sketch.core.android.test.util

import android.content.res.Resources
import android.graphics.ColorSpace
import android.graphics.Rect
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.VectorDrawable
import android.os.Build
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
import com.github.panpf.sketch.ColorType
import com.github.panpf.sketch.colorType
import com.github.panpf.sketch.size
import com.github.panpf.sketch.test.utils.TestColor
import com.github.panpf.sketch.test.utils.colorSpaceNameCompat
import com.github.panpf.sketch.test.utils.corners
import com.github.panpf.sketch.test.utils.getTestContext
import com.github.panpf.sketch.util.Size
import com.github.panpf.sketch.util.getDrawableCompat
import com.github.panpf.sketch.util.getXmlDrawableCompat
import com.github.panpf.sketch.util.toBitmap
import com.github.panpf.tools4a.dimen.ktx.dp2px
import org.junit.runner.RunWith
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@RunWith(AndroidJUnit4::class)
class DrawablesTest {

    @Test
    fun testGetDrawableCompat() {
        val context = getTestContext()

        assertNotNull(context.getDrawableCompat(android.R.drawable.ic_delete))
        assertFailsWith(Resources.NotFoundException::class) {
            context.getDrawableCompat(1101)
        }

        assertNotNull(
            context.resources.getDrawableCompat(android.R.drawable.ic_delete, null)
        )
        assertFailsWith(Resources.NotFoundException::class) {
            context.resources.getDrawableCompat(1101, null)
        }
    }

    @Test
    fun testGetXmlDrawableCompat() {
        val context = getTestContext()

        if (Build.VERSION.SDK_INT >= 21) {
            context.getXmlDrawableCompat(
                context.resources,
                com.github.panpf.sketch.test.utils.core.R.drawable.ic_cloudy
            ).apply {
                if (Build.VERSION.SDK_INT >= 24) {
                    assertTrue(this is VectorDrawable)
                } else {
                    assertTrue(this is VectorDrawableCompat)
                }
            }
        } else {
            assertFailsWith(Resources.NotFoundException::class) {
                context.getXmlDrawableCompat(
                    context.resources,
                    com.github.panpf.sketch.test.utils.core.R.drawable.ic_cloudy
                )
            }
        }

        context.getXmlDrawableCompat(
            context.resources,
            com.github.panpf.sketch.test.utils.core.R.drawable.test_error
        ).apply {
            assertTrue(this is GradientDrawable)
        }

        if (Build.VERSION.SDK_INT >= 24) {
            context.getXmlDrawableCompat(
                context.resources,
                com.github.panpf.sketch.test.utils.core.R.drawable.ic_launcher
            ).apply {
                assertTrue(this is BitmapDrawable)
            }
        } else {
            assertFailsWith(Resources.NotFoundException::class) {
                context.getXmlDrawableCompat(
                    context.resources,
                    com.github.panpf.sketch.test.utils.core.R.drawable.ic_launcher
                )
            }
        }
    }

    @Test
    fun testToBitmap() {
        val context = getTestContext()

        val imageResId = com.github.panpf.sketch.test.utils.core.R.drawable.test
        val imageSize = Size(60.dp2px, 30.dp2px)
        val drawable = context.getDrawableCompat(imageResId)

        // default
        drawable.toBitmap().apply {
            val bitmap = this
            assertEquals(expected = imageSize, actual = bitmap.size)
            assertEquals(expected = ColorType.ARGB_8888, actual = bitmap.colorType)
            assertEquals(expected = colorSpaceNameCompat(), actual = bitmap.colorSpaceNameCompat)
            assertEquals(expected = listOf(0, 0, 0, 0), actual = bitmap.corners())
        }

        // colorType
        drawable.toBitmap(colorType = ColorType.RGB_565).apply {
            val bitmap = this
            assertEquals(expected = imageSize, actual = bitmap.size)
            assertEquals(expected = ColorType.RGB_565, actual = bitmap.colorType)
            assertEquals(expected = colorSpaceNameCompat(), actual = bitmap.colorSpaceNameCompat)
            assertEquals(
                expected = listOf(
                    TestColor.BLACK,
                    TestColor.BLACK,
                    TestColor.BLACK,
                    TestColor.BLACK
                ),
                actual = bitmap.corners()
            )
        }

        // colorSpace
        if (VERSION.SDK_INT >= VERSION_CODES.O) {
            drawable.toBitmap(colorSpace = ColorSpace.get(ColorSpace.Named.DISPLAY_P3)).apply {
                val bitmap = this
                assertEquals(expected = imageSize, actual = bitmap.size)
                assertEquals(expected = ColorType.ARGB_8888, actual = bitmap.colorType)
                assertEquals(
                    expected = colorSpaceNameCompat("DISPLAY_P3"),
                    actual = bitmap.colorSpaceNameCompat
                )
                assertEquals(expected = listOf(0, 0, 0, 0), actual = bitmap.corners())
            }
        }

        // restore bounds
        assertEquals(Rect(0, 0, 0, 0), drawable.bounds)
        drawable.toBitmap()
        assertEquals(Rect(0, 0, 0, 0), drawable.bounds)

        drawable.setBounds(100, 100, 200, 200)
        assertEquals(Rect(100, 100, 200, 200), drawable.bounds)
        drawable.toBitmap()
        assertEquals(Rect(100, 100, 200, 200), drawable.bounds)
    }

    @Test
    fun testWidthWithBitmapFirst() {
        // TODO test
    }

    @Test
    fun testHeightWithBitmapFirst() {
        // TODO test
    }

    @Test
    fun testToLogString() {
        // TODO test
    }

    @Test
    fun testToSizeString() {
        // TODO test
    }

    @Test
    fun testCalculateFitBounds() {
        // TODO test
    }
}