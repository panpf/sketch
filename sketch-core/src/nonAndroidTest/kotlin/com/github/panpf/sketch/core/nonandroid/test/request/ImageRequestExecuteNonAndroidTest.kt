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

package com.github.panpf.sketch.core.nonandroid.test.request

import com.github.panpf.sketch.BitmapImage
import com.github.panpf.sketch.cache.CachePolicy.DISABLED
import com.github.panpf.sketch.decode.HighQualityColorType
import com.github.panpf.sketch.decode.LowQualityColorType
import com.github.panpf.sketch.images.ComposeResImageFiles
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.ImageResult
import com.github.panpf.sketch.request.colorSpace
import com.github.panpf.sketch.request.colorType
import com.github.panpf.sketch.test.singleton.getTestContextAndSketch
import com.github.panpf.sketch.test.utils.defaultColorType
import com.github.panpf.sketch.util.asOrNull
import com.github.panpf.sketch.util.asOrThrow
import kotlinx.coroutines.test.runTest
import org.jetbrains.skia.ColorSpace
import org.jetbrains.skia.ColorType
import kotlin.test.Test
import kotlin.test.assertEquals

class ImageRequestExecuteNonAndroidTest {

    @Test
    fun testColorType() = runTest {
        val (context, sketch) = getTestContextAndSketch()

        ImageRequest(context, ComposeResImageFiles.jpeg.uri) {
            resultCachePolicy(DISABLED)
            memoryCachePolicy(DISABLED)
        }.let { sketch.execute(it) }.asOrNull<ImageResult.Success>()!!.apply {
            assertEquals(
                expected = defaultColorType,
                actual = image.asOrThrow<BitmapImage>().bitmap.colorType
            )
        }

        ImageRequest(context, ComposeResImageFiles.jpeg.uri) {
            resultCachePolicy(DISABLED)
            memoryCachePolicy(DISABLED)
            colorType(ColorType.RGBA_8888)
        }.let { sketch.execute(it) }.asOrNull<ImageResult.Success>()!!.apply {
            assertEquals(
                expected = ColorType.RGBA_8888,
                actual = image.asOrThrow<BitmapImage>().bitmap.colorType
            )
        }

        ImageRequest(context, ComposeResImageFiles.jpeg.uri) {
            resultCachePolicy(DISABLED)
            memoryCachePolicy(DISABLED)
            colorType(ColorType.ARGB_4444)
        }.let { sketch.execute(it) }.asOrNull<ImageResult.Success>()!!.apply {
            assertEquals(
                expected = ColorType.ARGB_4444,
                actual = image.asOrThrow<BitmapImage>().bitmap.colorType
            )
        }

        ImageRequest(context, ComposeResImageFiles.jpeg.uri) {
            resultCachePolicy(DISABLED)
            memoryCachePolicy(DISABLED)
            colorType(ColorType.ALPHA_8)
        }.let { sketch.execute(it) }.asOrNull<ImageResult.Success>()!!.apply {
            assertEquals(
                expected = ColorType.ALPHA_8,
                actual = image.asOrThrow<BitmapImage>().bitmap.colorType
            )
        }

        ImageRequest(context, ComposeResImageFiles.jpeg.uri) {
            resultCachePolicy(DISABLED)
            memoryCachePolicy(DISABLED)
            colorType(ColorType.RGB_565)
        }.let { sketch.execute(it) }.asOrNull<ImageResult.Success>()!!.apply {
            assertEquals(
                expected = ColorType.RGB_565,
                actual = image.asOrThrow<BitmapImage>().bitmap.colorType
            )
        }

        ImageRequest(context, ComposeResImageFiles.jpeg.uri) {
            resultCachePolicy(DISABLED)
            memoryCachePolicy(DISABLED)
            colorType(ColorType.RGBA_F16)
        }.let { sketch.execute(it) }.asOrNull<ImageResult.Success>()!!.apply {
            assertEquals(
                expected = ColorType.RGBA_F16,
                actual = image.asOrThrow<BitmapImage>().bitmap.colorType
            )
        }

        ImageRequest(context, ComposeResImageFiles.jpeg.uri) {
            resultCachePolicy(DISABLED)
            memoryCachePolicy(DISABLED)
            this.colorType(LowQualityColorType)
        }.let { sketch.execute(it) }.asOrNull<ImageResult.Success>()!!.apply {
            assertEquals(
                expected = ColorType.RGB_565,
                actual = image.asOrThrow<BitmapImage>().bitmap.colorType
            )
        }
        ImageRequest(context, ComposeResImageFiles.png.uri) {
            resultCachePolicy(DISABLED)
            memoryCachePolicy(DISABLED)
            this.colorType(LowQualityColorType)
        }.let { sketch.execute(it) }.asOrNull<ImageResult.Success>()!!.apply {
            assertEquals(
                expected = ColorType.ARGB_4444,
                actual = image.asOrThrow<BitmapImage>().bitmap.colorType
            )
        }

        ImageRequest(context, ComposeResImageFiles.jpeg.uri) {
            resultCachePolicy(DISABLED)
            memoryCachePolicy(DISABLED)
            this.colorType(HighQualityColorType)
        }.let { sketch.execute(it) }.asOrNull<ImageResult.Success>()!!.apply {
            assertEquals(
                expected = ColorType.RGBA_F16,
                actual = image.asOrThrow<BitmapImage>().bitmap.colorType
            )
        }
        ImageRequest(context, ComposeResImageFiles.png.uri) {
            resultCachePolicy(DISABLED)
            memoryCachePolicy(DISABLED)
            this.colorType(HighQualityColorType)
        }.let { sketch.execute(it) }.asOrNull<ImageResult.Success>()!!.apply {
            assertEquals(
                expected = ColorType.RGBA_F16,
                actual = image.asOrThrow<BitmapImage>().bitmap.colorType
            )
        }
    }

    @Test
    fun testColorSpace() = runTest {
        val (context, sketch) = getTestContextAndSketch()

        ImageRequest(context, ComposeResImageFiles.jpeg.uri) {
            resultCachePolicy(DISABLED)
            memoryCachePolicy(DISABLED)
        }.let { sketch.execute(it) }.asOrNull<ImageResult.Success>()!!.apply {
            assertEquals(
                expected = ColorSpace.sRGB,
                actual = image.asOrThrow<BitmapImage>().bitmap.colorSpace
            )
        }

        ImageRequest(context, ComposeResImageFiles.jpeg.uri) {
            resultCachePolicy(DISABLED)
            memoryCachePolicy(DISABLED)
            colorSpace(ColorSpace.sRGBLinear)
        }.let { sketch.execute(it) }.asOrNull<ImageResult.Success>()!!.apply {
            assertEquals(
                expected = ColorSpace.sRGBLinear,
                actual = image.asOrThrow<BitmapImage>().bitmap.colorSpace
            )
        }

        ImageRequest(context, ComposeResImageFiles.jpeg.uri) {
            resultCachePolicy(DISABLED)
            memoryCachePolicy(DISABLED)
            colorSpace(ColorSpace.displayP3)
        }.let { sketch.execute(it) }.asOrNull<ImageResult.Success>()!!.apply {
            assertEquals(
                expected = ColorSpace.displayP3,
                actual = image.asOrThrow<BitmapImage>().bitmap.colorSpace
            )
        }
    }
}