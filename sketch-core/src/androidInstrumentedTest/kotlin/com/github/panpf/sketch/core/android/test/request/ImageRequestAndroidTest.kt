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
import android.graphics.ColorSpace
import android.graphics.ColorSpace.Named.ACES
import android.graphics.ColorSpace.Named.BT709
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.decode.BitmapConfig
import com.github.panpf.sketch.drawable.ColorDrawableEqualizer
import com.github.panpf.sketch.images.ResourceImages
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.bitmapConfig
import com.github.panpf.sketch.request.colorSpace
import com.github.panpf.sketch.request.error
import com.github.panpf.sketch.request.fallback
import com.github.panpf.sketch.request.placeholder
import com.github.panpf.sketch.request.preferQualityOverSpeed
import com.github.panpf.sketch.state.DrawableStateImage
import com.github.panpf.sketch.state.ErrorStateImage
import com.github.panpf.sketch.state.IntColorDrawableStateImage
import com.github.panpf.sketch.state.addState
import com.github.panpf.sketch.test.utils.UriInvalidCondition
import com.github.panpf.sketch.test.utils.getTestContext
import org.junit.runner.RunWith
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull

@RunWith(AndroidJUnit4::class)
class ImageRequestAndroidTest {

    @Test
    fun testSizeWithDisplay() {
        // TODO test
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

            placeholder(ColorDrawableEqualizer(Color.GREEN))
            build().apply {
                assertEquals(true, placeholder is DrawableStateImage)
            }

            placeholder(android.R.drawable.bottom_bar)
            build().apply {
                assertEquals(
                    DrawableStateImage(android.R.drawable.bottom_bar),
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

            fallback(ColorDrawableEqualizer(Color.GREEN))
            build().apply {
                assertEquals(true, fallback is DrawableStateImage)
            }

            fallback(android.R.drawable.bottom_bar)
            build().apply {
                assertEquals(
                    DrawableStateImage(android.R.drawable.bottom_bar),
                    fallback
                )
            }

            fallback(null)
            build().apply {
                assertNull(fallback)
            }

            // TODO test: IntColor
            // TODO test: ResColor
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

            error(IntColorDrawableStateImage(Color.BLUE))
            build().apply {
                assertEquals(
                    ErrorStateImage(IntColorDrawableStateImage(Color.BLUE)),
                    error
                )
            }

            error(ColorDrawableEqualizer(Color.GREEN))
            build().apply {
                assertEquals(true, error is ErrorStateImage)
            }

            error(android.R.drawable.bottom_bar)
            build().apply {
                assertEquals(
                    ErrorStateImage(DrawableStateImage(android.R.drawable.bottom_bar)),
                    error
                )
            }

            error(android.R.drawable.bottom_bar) {
                addState(UriInvalidCondition, android.R.drawable.alert_dark_frame)
            }
            build().apply {
                assertEquals(
                    ErrorStateImage(DrawableStateImage(android.R.drawable.bottom_bar)) {
                        addState(UriInvalidCondition, android.R.drawable.alert_dark_frame)
                    },
                    error
                )
            }

            error()
            build().apply {
                assertNull(error)
            }

            error {
                addState(UriInvalidCondition, android.R.drawable.btn_dialog)
            }
            build().apply {
                assertNotNull(error)
            }
        }
    }

    @Test
    fun testBitmapConfig() {
        val context1 = getTestContext()
        val uri = ResourceImages.jpeg.uri
        ImageRequest(context1, uri) {
            bitmapConfig(Bitmap.Config.ARGB_8888)
        }.apply {
            assertEquals(BitmapConfig.FixedQuality(Bitmap.Config.ARGB_8888.name), bitmapConfig)
        }
    }

    @Test
    fun testColorSpace() {
        if (VERSION.SDK_INT < VERSION_CODES.O) return

        val context1 = getTestContext()
        val uri = ResourceImages.jpeg.uri
        ImageRequest.Builder(context1, uri).apply {
            build().apply {
                assertNull(colorSpace)
            }

            colorSpace(ACES)
            build().apply {
                assertEquals(ColorSpace.get(ACES), colorSpace)
            }

            colorSpace(BT709)
            build().apply {
                assertEquals(ColorSpace.get(BT709), colorSpace)
            }

            colorSpace(null)
            build().apply {
                assertNull(colorSpace)
            }
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