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

@file:Suppress("DEPRECATION")

package com.github.panpf.sketch.core.android.test.request

import android.graphics.Bitmap.Config.ALPHA_8
import android.graphics.Bitmap.Config.ARGB_4444
import android.graphics.Bitmap.Config.ARGB_8888
import android.graphics.Bitmap.Config.HARDWARE
import android.graphics.Bitmap.Config.RGBA_F16
import android.graphics.Bitmap.Config.RGB_565
import android.graphics.ColorSpace
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.cache.CachePolicy.DISABLED
import com.github.panpf.sketch.decode.HighQualityColorType
import com.github.panpf.sketch.decode.LowQualityColorType
import com.github.panpf.sketch.getBitmapOrThrow
import com.github.panpf.sketch.images.ResourceImages
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.ImageResult
import com.github.panpf.sketch.request.colorSpace
import com.github.panpf.sketch.request.colorType
import com.github.panpf.sketch.request.preferQualityOverSpeed
import com.github.panpf.sketch.test.singleton.getTestContextAndSketch
import com.github.panpf.sketch.util.asOrNull
import kotlinx.coroutines.test.runTest
import org.junit.runner.RunWith
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@RunWith(AndroidJUnit4::class)
class ImageRequestExecuteAndroidTest {

    @Test
    fun testColorType() = runTest {
        val (context, sketch) = getTestContextAndSketch()

        ImageRequest(context, ResourceImages.jpeg.uri) {
            resultCachePolicy(DISABLED)
            memoryCachePolicy(DISABLED)
        }.let { sketch.execute(it) }.asOrNull<ImageResult.Success>()!!.apply {
            assertEquals(ARGB_8888, image.getBitmapOrThrow().config)
        }

        ImageRequest(context, ResourceImages.jpeg.uri) {
            resultCachePolicy(DISABLED)
            memoryCachePolicy(DISABLED)
            colorType(ARGB_8888)
        }.let { sketch.execute(it) }.asOrNull<ImageResult.Success>()!!.apply {
            assertEquals(ARGB_8888, image.getBitmapOrThrow().config)
        }

        ImageRequest(context, ResourceImages.jpeg.uri) {
            resultCachePolicy(DISABLED)
            memoryCachePolicy(DISABLED)
            @Suppress("DEPRECATION")
            colorType(ARGB_4444)
        }.let { sketch.execute(it) }.asOrNull<ImageResult.Success>()!!.apply {
            if (VERSION.SDK_INT > VERSION_CODES.M) {
                assertEquals(ARGB_8888, image.getBitmapOrThrow().config)
            } else {
                @Suppress("DEPRECATION")
                (assertEquals(ARGB_4444, image.getBitmapOrThrow().config))
            }
        }

        ImageRequest(context, ResourceImages.jpeg.uri) {
            resultCachePolicy(DISABLED)
            memoryCachePolicy(DISABLED)
            colorType(ALPHA_8)
        }.let { sketch.execute(it) }.asOrNull<ImageResult.Success>()!!.apply {
            assertEquals(ARGB_8888, image.getBitmapOrThrow().config)
        }

        ImageRequest(context, ResourceImages.jpeg.uri) {
            resultCachePolicy(DISABLED)
            memoryCachePolicy(DISABLED)
            colorType(RGB_565)
        }.let { sketch.execute(it) }.asOrNull<ImageResult.Success>()!!.apply {
            assertEquals(RGB_565, image.getBitmapOrThrow().config)
        }

        if (VERSION.SDK_INT >= VERSION_CODES.O) {
            ImageRequest(context, ResourceImages.jpeg.uri) {
                resultCachePolicy(DISABLED)
                memoryCachePolicy(DISABLED)
                colorType(RGBA_F16)
            }.let { sketch.execute(it) }
                .asOrNull<ImageResult.Success>()!!.apply {
                    assertEquals(RGBA_F16, image.getBitmapOrThrow().config)
                }
        }

        if (VERSION.SDK_INT >= VERSION_CODES.O) {
            ImageRequest(context, ResourceImages.jpeg.uri) {
                resultCachePolicy(DISABLED)
                memoryCachePolicy(DISABLED)
                colorType(HARDWARE)
            }.let { sketch.execute(it) }.asOrNull<ImageResult.Success>()!!.apply {
                assertEquals(HARDWARE, image.getBitmapOrThrow().config)
            }
        }

        ImageRequest(context, ResourceImages.jpeg.uri) {
            resultCachePolicy(DISABLED)
            memoryCachePolicy(DISABLED)
            this.colorType(LowQualityColorType)
        }.let { sketch.execute(it) }.asOrNull<ImageResult.Success>()!!.apply {
            assertEquals(RGB_565, image.getBitmapOrThrow().config)
        }
        ImageRequest(context, ResourceImages.png.uri) {
            resultCachePolicy(DISABLED)
            memoryCachePolicy(DISABLED)
            this.colorType(LowQualityColorType)
        }.let { sketch.execute(it) }.asOrNull<ImageResult.Success>()!!.apply {
            if (VERSION.SDK_INT >= VERSION_CODES.KITKAT) {
                assertEquals(ARGB_8888, image.getBitmapOrThrow().config)
            } else {
                @Suppress("DEPRECATION")
                (assertEquals(ARGB_4444, image.getBitmapOrThrow().config))
            }
        }

        ImageRequest(context, ResourceImages.jpeg.uri) {
            resultCachePolicy(DISABLED)
            memoryCachePolicy(DISABLED)
            this.colorType(HighQualityColorType)
        }.let { sketch.execute(it) }.asOrNull<ImageResult.Success>()!!.apply {
            if (VERSION.SDK_INT >= VERSION_CODES.O) {
                assertEquals(RGBA_F16, image.getBitmapOrThrow().config)
            } else {
                assertEquals(ARGB_8888, image.getBitmapOrThrow().config)
            }
        }
        ImageRequest(context, ResourceImages.png.uri) {
            resultCachePolicy(DISABLED)
            memoryCachePolicy(DISABLED)
            this.colorType(HighQualityColorType)
        }.let { sketch.execute(it) }.asOrNull<ImageResult.Success>()!!.apply {
            if (VERSION.SDK_INT >= VERSION_CODES.O) {
                assertEquals(RGBA_F16, image.getBitmapOrThrow().config)
            } else {
                assertEquals(ARGB_8888, image.getBitmapOrThrow().config)
            }
        }
    }

    @Test
    fun testColorSpace() = runTest {
        if (VERSION.SDK_INT < VERSION_CODES.O) return@runTest

        val (context, sketch) = getTestContextAndSketch()

        ImageRequest(context, ResourceImages.jpeg.uri) {
            resultCachePolicy(DISABLED)
            memoryCachePolicy(DISABLED)
        }.let { sketch.execute(it) }.asOrNull<ImageResult.Success>()!!.apply {
            assertEquals(
                ColorSpace.get(ColorSpace.Named.SRGB).name,
                image.getBitmapOrThrow().colorSpace!!.name
            )
        }

        ImageRequest(context, ResourceImages.jpeg.uri) {
            resultCachePolicy(DISABLED)
            memoryCachePolicy(DISABLED)
            colorSpace(ColorSpace.Named.ADOBE_RGB)
        }.let { sketch.execute(it) }.asOrNull<ImageResult.Success>()!!.apply {
            assertEquals(
                ColorSpace.get(ColorSpace.Named.ADOBE_RGB).name,
                image.getBitmapOrThrow().colorSpace!!.name
            )
        }

        ImageRequest(context, ResourceImages.jpeg.uri) {
            resultCachePolicy(DISABLED)
            memoryCachePolicy(DISABLED)
            colorSpace(ColorSpace.Named.DISPLAY_P3)
        }.let { sketch.execute(it) }.asOrNull<ImageResult.Success>()!!.apply {
            assertEquals(
                ColorSpace.get(ColorSpace.Named.DISPLAY_P3).name,
                image.getBitmapOrThrow().colorSpace!!.name
            )
        }
    }

    @Test
    fun testPreferQualityOverSpeed() = runTest {
        val (context, sketch) = getTestContextAndSketch()

        ImageRequest(context, ResourceImages.jpeg.uri) {
            resultCachePolicy(DISABLED)
            memoryCachePolicy(DISABLED)
        }.let { sketch.execute(it) }.apply {
            assertTrue(this is ImageResult.Success)
        }

        ImageRequest(context, ResourceImages.jpeg.uri) {
            resultCachePolicy(DISABLED)
            memoryCachePolicy(DISABLED)
            preferQualityOverSpeed(true)
        }.let { sketch.execute(it) }.apply {
            assertTrue(this is ImageResult.Success)
        }

        ImageRequest(context, ResourceImages.jpeg.uri) {
            resultCachePolicy(DISABLED)
            memoryCachePolicy(DISABLED)
            preferQualityOverSpeed(false)
        }.let { sketch.execute(it) }.apply {
            assertTrue(this is ImageResult.Success)
        }
    }
}