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

package com.github.panpf.sketch.core.android.test.request

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.ColorSpace.Named.ACES
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.decode.BitmapColorSpace
import com.github.panpf.sketch.decode.FixedColorType
import com.github.panpf.sketch.drawable.ColorEquitableDrawable
import com.github.panpf.sketch.images.ResourceImages
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.colorSpace
import com.github.panpf.sketch.request.colorType
import com.github.panpf.sketch.request.error
import com.github.panpf.sketch.request.fallback
import com.github.panpf.sketch.request.placeholder
import com.github.panpf.sketch.request.preferQualityOverSpeed
import com.github.panpf.sketch.request.sizeWithDisplay
import com.github.panpf.sketch.resize.FixedSizeResolver
import com.github.panpf.sketch.state.ColorDrawableStateImage
import com.github.panpf.sketch.state.DrawableStateImage
import com.github.panpf.sketch.state.IntColorDrawableStateImage
import com.github.panpf.sketch.test.utils.TestColor
import com.github.panpf.sketch.test.utils.getTestContext
import com.github.panpf.sketch.util.IntColor
import com.github.panpf.sketch.util.ResColor
import com.github.panpf.sketch.util.screenSize
import org.junit.runner.RunWith
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull

@RunWith(AndroidJUnit4::class)
class ImageRequestAndroidTest {

    @Test
    fun testSizeWithDisplay() {
        val context = getTestContext()
        ImageRequest(context, "http://test.com/test.jpeg").apply {
            assertEquals(
                expected = FixedSizeResolver(context.screenSize()),
                actual = sizeResolver
            )
        }

        ImageRequest(context, "http://test.com/test.jpeg") {
            sizeWithDisplay(context)
        }.apply {
            assertEquals(
                expected = FixedSizeResolver(context.screenSize()),
                actual = sizeResolver
            )
        }
    }

    @Test
    fun testPlaceholder() {
        val context = getTestContext()
        val imageUri = ResourceImages.jpeg.uri
        ImageRequest(context, imageUri) {
            build().apply {
                assertNull(placeholder)
            }

            placeholder(IntColorDrawableStateImage(Color.BLUE))
            build().apply {
                assertEquals(IntColorDrawableStateImage(Color.BLUE), placeholder)
            }

            placeholder(ColorEquitableDrawable(Color.GREEN))
            build().apply {
                assertEquals(true, placeholder is DrawableStateImage)
            }

            placeholder(android.R.drawable.ic_lock_lock)
            build().apply {
                assertEquals(
                    DrawableStateImage(android.R.drawable.ic_lock_lock),
                    placeholder
                )
            }

            placeholder(IntColor(TestColor.RED))
            build().apply {
                assertEquals(
                    ColorDrawableStateImage(IntColor(TestColor.RED)),
                    placeholder
                )
            }

            placeholder(ResColor(android.R.drawable.ic_lock_lock))
            build().apply {
                assertEquals(
                    ColorDrawableStateImage(ResColor(android.R.drawable.ic_lock_lock)),
                    placeholder
                )
            }

            placeholder(null)
            build().apply {
                assertNull(placeholder)
            }
        }
    }

    @Test
    fun testFallback() {
        val context = getTestContext()
        val imageUri = ResourceImages.jpeg.uri
        ImageRequest(context, imageUri) {
            build().apply {
                assertNull(fallback)
            }

            fallback(IntColorDrawableStateImage(Color.BLUE))
            build().apply {
                assertEquals(IntColorDrawableStateImage(Color.BLUE), fallback)
            }

            fallback(ColorEquitableDrawable(Color.GREEN))
            build().apply {
                assertEquals(true, fallback is DrawableStateImage)
            }

            fallback(android.R.drawable.ic_lock_lock)
            build().apply {
                assertEquals(
                    DrawableStateImage(android.R.drawable.ic_lock_lock),
                    fallback
                )
            }

            fallback(IntColor(TestColor.RED))
            build().apply {
                assertEquals(
                    ColorDrawableStateImage(IntColor(TestColor.RED)),
                    fallback
                )
            }

            fallback(ResColor(android.R.drawable.ic_lock_lock))
            build().apply {
                assertEquals(
                    ColorDrawableStateImage(ResColor(android.R.drawable.ic_lock_lock)),
                    fallback
                )
            }

            fallback(null)
            build().apply {
                assertNull(fallback)
            }
        }
    }

    @Test
    fun testError() {
        val context = getTestContext()
        val imageUri = ResourceImages.jpeg.uri
        ImageRequest(context, imageUri) {
            build().apply {
                assertNull(error)
            }

            error(ColorEquitableDrawable(Color.GREEN))
            build().apply {
                assertEquals(
                    DrawableStateImage(ColorEquitableDrawable(Color.GREEN)),
                    error
                )
            }

            error(android.R.drawable.ic_lock_lock)
            build().apply {
                assertEquals(
                    DrawableStateImage(android.R.drawable.ic_lock_lock),
                    error
                )
            }

            error(android.R.drawable.ic_lock_lock)
            build().apply {
                assertEquals(
                    DrawableStateImage(android.R.drawable.ic_lock_lock),
                    error
                )
            }

            error(IntColor(TestColor.RED))
            build().apply {
                assertEquals(
                    ColorDrawableStateImage(IntColor(TestColor.RED)),
                    error
                )
            }

            error(ResColor(android.R.drawable.ic_lock_lock))
            build().apply {
                assertEquals(
                    ColorDrawableStateImage(ResColor(android.R.drawable.ic_lock_lock)),
                    error
                )
            }

            error(null)
            build().apply {
                assertNull(error)
            }
        }
    }

    @Test
    fun testColorType() {
        val context1 = getTestContext()
        val uri = ResourceImages.jpeg.uri
        ImageRequest(context1, uri) {
            colorType(Bitmap.Config.ARGB_8888)
        }.apply {
            assertEquals(FixedColorType(Bitmap.Config.ARGB_8888.name), colorType)
        }
    }

    @Test
    fun testColorSpace() {
        if (VERSION.SDK_INT < VERSION_CODES.O) return

        val context1 = getTestContext()
        val uri = ResourceImages.jpeg.uri
        ImageRequest(context1, uri) {
            colorSpace(ACES)
        }.apply {
            assertEquals(BitmapColorSpace("ACES"), colorSpace)
        }
    }

    @Test
    @Suppress("DEPRECATION")
    fun testPreferQualityOverSpeed() {
        val context1 = getTestContext()
        val uri = ResourceImages.jpeg.uri
        ImageRequest.Builder(context1, uri).apply {
            build().apply {
                assertFalse(preferQualityOverSpeed)
            }

            preferQualityOverSpeed()
            build().apply {
                assertEquals(true, preferQualityOverSpeed)
            }

            preferQualityOverSpeed(false)
            build().apply {
                assertEquals(false, preferQualityOverSpeed)
            }

            preferQualityOverSpeed(null)
            build().apply {
                assertFalse(preferQualityOverSpeed)
            }
        }
    }
}