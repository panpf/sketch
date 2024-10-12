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
import android.graphics.Color
import android.graphics.ColorSpace
import android.graphics.ImageDecoder
import android.graphics.Rect
import android.graphics.drawable.AnimatedImageDrawable
import android.graphics.drawable.AnimatedVectorDrawable
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.ColorStateListDrawable
import android.graphics.drawable.DrawableWrapper
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.graphics.drawable.TransitionDrawable
import android.graphics.drawable.VectorDrawable
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import androidx.appcompat.graphics.drawable.DrawableWrapperCompat
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
import com.github.panpf.sketch.AndroidBitmap
import com.github.panpf.sketch.ColorType
import com.github.panpf.sketch.colorType
import com.github.panpf.sketch.drawable.ResizeDrawable
import com.github.panpf.sketch.images.ResourceImages
import com.github.panpf.sketch.images.toDataSource
import com.github.panpf.sketch.resize.Scale
import com.github.panpf.sketch.size
import com.github.panpf.sketch.test.utils.TestColor
import com.github.panpf.sketch.test.utils.TestKeyDrawable
import com.github.panpf.sketch.test.utils.TestNullableKeyDrawable
import com.github.panpf.sketch.test.utils.colorSpaceNameCompat
import com.github.panpf.sketch.test.utils.corners
import com.github.panpf.sketch.test.utils.getTestContext
import com.github.panpf.sketch.util.Size
import com.github.panpf.sketch.util.asOrThrow
import com.github.panpf.sketch.util.getDrawableCompat
import com.github.panpf.sketch.util.getXmlDrawableCompat
import com.github.panpf.sketch.util.key
import com.github.panpf.sketch.util.toBitmap
import com.github.panpf.sketch.util.toLogString
import com.github.panpf.sketch.util.toSizeString
import com.github.panpf.tools4a.dimen.ktx.dp2px
import okio.buffer
import org.junit.runner.RunWith
import java.nio.ByteBuffer
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

        if (VERSION.SDK_INT >= 21) {
            context.getXmlDrawableCompat(
                context.resources,
                com.github.panpf.sketch.test.utils.core.R.drawable.ic_cloudy
            ).apply {
                if (VERSION.SDK_INT >= 24) {
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

        if (VERSION.SDK_INT >= 24) {
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
    fun testKey() {
        val context = getTestContext()

        TestKeyDrawable(ColorDrawable(Color.GRAY), key = "testKey1").apply {
            assertEquals(
                expected = "testKey1",
                actual = key(equalityKey = null)
            )
            assertEquals(
                expected = "testKey1",
                actual = key(equalityKey = "equalityKey1")
            )
        }

        TestNullableKeyDrawable(ColorDrawable(Color.GRAY), key = null).apply {
            assertEquals(
                expected = "TestNullableKeyDrawable(drawable=ColorDrawable(color=-7829368))",
                actual = key(equalityKey = null)
            )
            assertEquals(
                expected = "TestNullableKeyDrawable(drawable=ColorDrawable(color=-7829368)):equalityKey1",
                actual = key(equalityKey = "equalityKey1")
            )
        }
        TestNullableKeyDrawable(ColorDrawable(Color.GRAY), "testKey1").apply {
            assertEquals(
                expected = "testKey1",
                actual = key(equalityKey = null)
            )
            assertEquals(
                expected = "testKey1",
                actual = key(equalityKey = "equalityKey1")
            )
        }

        context.getDrawableCompat(android.R.drawable.ic_delete).asOrThrow<BitmapDrawable>().apply {
            assertEquals(
                expected = "BitmapDrawable(${bitmap.toLogString()})",
                actual = key(equalityKey = null)
            )
            assertEquals(
                expected = "BitmapDrawable:equalityKey1",
                actual = key(equalityKey = "equalityKey1")
            )
        }

        RoundedBitmapDrawableFactory.create(context.resources, AndroidBitmap(100, 100)).apply {
            assertEquals(
                expected = "RoundedBitmapDrawable(${bitmap?.toLogString()},0.0)",
                actual = key(equalityKey = null)
            )
            assertEquals(
                expected = "RoundedBitmapDrawable:equalityKey1",
                actual = key(equalityKey = "equalityKey1")
            )
        }

        if (VERSION.SDK_INT >= VERSION_CODES.P) {
            val bytes = ResourceImages.animGif.toDataSource(context).openSource()
                .buffer()
                .use { it.readByteArray() }
            ImageDecoder.decodeDrawable(ImageDecoder.createSource(ByteBuffer.wrap(bytes)))
                .asOrThrow<AnimatedImageDrawable>().apply {
                    assertEquals(
                        expected = "AnimatedImageDrawable(${toSizeString()})",
                        actual = key(equalityKey = null)
                    )
                    assertEquals(
                        expected = "AnimatedImageDrawable:equalityKey1",
                        actual = key(equalityKey = "equalityKey1")
                    )
                }
        }

        if (VERSION.SDK_INT <= VERSION_CODES.M) {
            context.getDrawableCompat(com.github.panpf.sketch.test.utils.core.R.drawable.ic_animated)
                .asOrThrow<AnimatedVectorDrawableCompat>().apply {
                    assertEquals(
                        expected = "AnimatedVectorDrawableCompat(${toSizeString()})",
                        actual = key(equalityKey = null)
                    )
                    assertEquals(
                        expected = "AnimatedVectorDrawableCompat:equalityKey1",
                        actual = key(equalityKey = "equalityKey1")
                    )
                }
        }

        context.getDrawable(com.github.panpf.sketch.test.utils.core.R.drawable.ic_animated)!!
            .asOrThrow<AnimatedVectorDrawable>().apply {
                assertEquals(
                    expected = "AnimatedVectorDrawable(${toSizeString()})",
                    actual = key(equalityKey = null)
                )
                assertEquals(
                    expected = "AnimatedVectorDrawable:equalityKey1",
                    actual = key(equalityKey = "equalityKey1")
                )
            }

        TransitionDrawable(
            /* layers = */ arrayOf(
                context.getDrawableCompat(com.github.panpf.sketch.test.utils.core.R.drawable.test),
                context.getDrawableCompat(android.R.drawable.ic_delete)
            )
        ).apply {
            assertEquals(
                expected = "TransitionDrawable(${toSizeString()})",
                actual = key(equalityKey = null)
            )
            assertEquals(
                expected = "TransitionDrawable:equalityKey1",
                actual = key(equalityKey = "equalityKey1")
            )
        }

        ColorDrawable(TestColor.RED).apply {
            assertEquals(
                expected = "ColorDrawable(${color})",
                actual = key(equalityKey = null)
            )
            assertEquals(
                expected = "ColorDrawable(${color})",
                actual = key(equalityKey = "equalityKey1")
            )
        }

        if (VERSION.SDK_INT >= VERSION_CODES.Q) {
            ColorStateListDrawable().apply {
                assertEquals(
                    expected = "ColorStateListDrawable(${toSizeString()})",
                    actual = key(equalityKey = null)
                )
                assertEquals(
                    expected = "ColorStateListDrawable:equalityKey1",
                    actual = key(equalityKey = "equalityKey1")
                )
            }
        }

        if (VERSION.SDK_INT <= VERSION_CODES.M) {
            context.getDrawableCompat(com.github.panpf.sketch.test.utils.core.R.drawable.ic_cloudy)
                .asOrThrow<VectorDrawableCompat>().apply {
                    assertEquals(
                        expected = "VectorDrawableCompat(${toSizeString()})",
                        actual = key(equalityKey = null)
                    )
                    assertEquals(
                        expected = "VectorDrawableCompat:equalityKey1",
                        actual = key(equalityKey = "equalityKey1")
                    )
                }
        }

        context.getDrawable(com.github.panpf.sketch.test.utils.core.R.drawable.ic_cloudy)!!
            .asOrThrow<VectorDrawable>().apply {
                assertEquals(
                    expected = "VectorDrawable(${toSizeString()})",
                    actual = key(equalityKey = null)
                )
                assertEquals(
                    expected = "VectorDrawable:equalityKey1",
                    actual = key(equalityKey = "equalityKey1")
                )
            }

        GradientDrawable().apply {
            assertEquals(
                expected = "GradientDrawable(${toSizeString()})",
                actual = key(equalityKey = null)
            )
            assertEquals(
                expected = "GradientDrawable:equalityKey1",
                actual = key(equalityKey = "equalityKey1")
            )
        }

        if (VERSION.SDK_INT >= VERSION_CODES.M) {
            object : DrawableWrapper(
                context.getDrawableCompat(android.R.drawable.ic_delete)
            ) {}.apply {
                assertEquals(
                    expected = this.toString(),
                    actual = key(equalityKey = null)
                )
                assertEquals(
                    expected = "${this}:equalityKey1",
                    actual = key(equalityKey = "equalityKey1")
                )
            }
        }

        object : DrawableWrapperCompat(
            context.getDrawableCompat(android.R.drawable.ic_delete)
        ) {}.apply {
            assertEquals(
                expected = this.toString(),
                actual = key(equalityKey = null)
            )
            assertEquals(
                expected = "${this}:equalityKey1",
                actual = key(equalityKey = "equalityKey1")
            )
        }

        ResizeDrawable(
            drawable = context.getDrawableCompat(com.github.panpf.sketch.test.utils.core.R.drawable.test),
            size = Size(100, 100),
            scale = Scale.CENTER_CROP
        ).apply {
            assertEquals(
                expected = "ResizeDrawable(drawable=${drawable!!.toLogString()}, size=100x100, scale=CENTER_CROP)",
                actual = key()
            )
        }

        LayerDrawable(
            /* layers = */ arrayOf(
                context.getDrawableCompat(com.github.panpf.sketch.test.utils.core.R.drawable.test),
                context.getDrawableCompat(android.R.drawable.ic_delete)
            )
        ).apply {
            assertEquals(
                expected = toString(),
                actual = key()
            )
        }
    }

    @Test
    fun testToLogString() {
        val context = getTestContext()
        ResizeDrawable(
            drawable = context.getDrawableCompat(com.github.panpf.sketch.test.utils.core.R.drawable.test),
            size = Size(100, 100),
            scale = Scale.CENTER_CROP
        ).apply {
            assertEquals(
                expected = "ResizeDrawable(drawable=${drawable!!.toLogString()}, size=100x100, scale=CENTER_CROP)",
                actual = toLogString()
            )
        }

        context.getDrawableCompat(android.R.drawable.ic_delete).asOrThrow<BitmapDrawable>().apply {
            assertEquals(
                expected = "BitmapDrawable(bitmap=${bitmap.toLogString()})",
                actual = toLogString()
            )
        }

        RoundedBitmapDrawableFactory.create(context.resources, AndroidBitmap(100, 100)).apply {
            assertEquals(
                expected = "RoundedBitmapDrawable(drawable=${bitmap?.toLogString()}, cornerRadius=0.0)",
                actual = toLogString()
            )
        }

        if (VERSION.SDK_INT >= VERSION_CODES.P) {
            val bytes = ResourceImages.animGif.toDataSource(context).openSource()
                .buffer()
                .use { it.readByteArray() }
            ImageDecoder.decodeDrawable(ImageDecoder.createSource(ByteBuffer.wrap(bytes)))
                .asOrThrow<AnimatedImageDrawable>().apply {
                    assertEquals(
                        expected = "AnimatedImageDrawable(size=${toSizeString()})",
                        actual = toLogString()
                    )
                }
        }

        if (VERSION.SDK_INT <= VERSION_CODES.M) {
            context.getDrawableCompat(com.github.panpf.sketch.test.utils.core.R.drawable.ic_animated)
                .asOrThrow<AnimatedVectorDrawableCompat>().apply {
                    assertEquals(
                        expected = "AnimatedVectorDrawableCompat(size=${toSizeString()})",
                        actual = toLogString()
                    )
                }
        }

        context.getDrawable(com.github.panpf.sketch.test.utils.core.R.drawable.ic_animated)!!
            .asOrThrow<AnimatedVectorDrawable>().apply {
                assertEquals(
                    expected = "AnimatedVectorDrawable(size=${toSizeString()})",
                    actual = toLogString()
                )
            }

        TransitionDrawable(
            /* layers = */ arrayOf(
                context.getDrawableCompat(com.github.panpf.sketch.test.utils.core.R.drawable.test),
                context.getDrawableCompat(android.R.drawable.ic_delete)
            )
        ).apply {
            assertEquals(
                expected = "TransitionDrawable(size=${toSizeString()})",
                actual = toLogString()
            )
        }

        ColorDrawable(TestColor.RED).apply {
            assertEquals(
                expected = "ColorDrawable(color=${color})",
                actual = toLogString()
            )
        }

        if (VERSION.SDK_INT >= VERSION_CODES.Q) {
            ColorStateListDrawable().apply {
                assertEquals(
                    expected = "ColorStateListDrawable(size=${toSizeString()})",
                    actual = toLogString()
                )
            }
        }

        if (VERSION.SDK_INT <= VERSION_CODES.M) {
            context.getDrawableCompat(com.github.panpf.sketch.test.utils.core.R.drawable.ic_cloudy)
                .asOrThrow<VectorDrawableCompat>().apply {
                    assertEquals(
                        expected = "VectorDrawableCompat(size=${toSizeString()})",
                        actual = toLogString()
                    )
                }
        }

        context.getDrawable(com.github.panpf.sketch.test.utils.core.R.drawable.ic_cloudy)!!
            .asOrThrow<VectorDrawable>().apply {
                assertEquals(
                    expected = "VectorDrawable(size=${toSizeString()})",
                    actual = toLogString()
                )
            }

        GradientDrawable().apply {
            assertEquals(
                expected = "GradientDrawable(size=${toSizeString()})",
                actual = toLogString()
            )
        }

        if (VERSION.SDK_INT >= VERSION_CODES.M) {
            object : DrawableWrapper(
                context.getDrawableCompat(android.R.drawable.ic_delete)
            ) {}.apply {
                assertEquals(
                    expected = this.toString(),
                    actual = toLogString()
                )
            }
        }

        object : DrawableWrapperCompat(
            context.getDrawableCompat(android.R.drawable.ic_delete)
        ) {}.apply {
            assertEquals(
                expected = this.toString(),
                actual = toLogString()
            )
        }

        LayerDrawable(
            /* layers = */ arrayOf(
                context.getDrawableCompat(com.github.panpf.sketch.test.utils.core.R.drawable.test),
                context.getDrawableCompat(android.R.drawable.ic_delete)
            )
        ).apply {
            assertEquals(
                expected = toString(),
                actual = toLogString()
            )
        }
    }

    @Test
    fun testToSizeString() {
        val context = getTestContext()
        context.getDrawableCompat(android.R.drawable.ic_delete).apply {
            assertEquals(
                expected = "${intrinsicWidth}x${intrinsicHeight}",
                actual = this@apply.toSizeString()
            )
        }
    }
}