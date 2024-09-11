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
import android.graphics.Bitmap
import android.graphics.Rect
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.VectorDrawable
import android.os.Build
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
import com.github.panpf.sketch.test.utils.getTestContext
import com.github.panpf.sketch.test.utils.shortInfoColorSpaceName
import com.github.panpf.sketch.util.getDrawableCompat
import com.github.panpf.sketch.util.getXmlDrawableCompat
import com.github.panpf.sketch.util.toNewBitmap
import com.github.panpf.sketch.util.toShortInfoString
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
    fun testToNewBitmap() {
        val context = getTestContext()

        val drawable = BitmapDrawable(
            context.resources,
            Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)
        )

        assertEquals(Rect(0, 0, 0, 0), drawable.bounds)
        drawable.toNewBitmap().apply {
            assertEquals(Bitmap.Config.ARGB_8888, config)
            assertEquals(
                "AndroidBitmap(100x100,ARGB_8888${shortInfoColorSpaceName("SRGB")})",
                toShortInfoString()
            )
        }
        assertEquals(Rect(0, 0, 0, 0), drawable.bounds)

        drawable.setBounds(100, 100, 200, 200)
        assertEquals(Rect(100, 100, 200, 200), drawable.bounds)
        drawable.toNewBitmap(Bitmap.Config.RGB_565).apply {
            assertEquals(Bitmap.Config.RGB_565, config)
            assertEquals(
                "AndroidBitmap(100x100,RGB_565${shortInfoColorSpaceName("SRGB")})",
                toShortInfoString()
            )
        }
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