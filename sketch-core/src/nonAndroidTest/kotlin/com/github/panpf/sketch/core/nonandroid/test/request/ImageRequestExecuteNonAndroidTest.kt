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

import com.github.panpf.sketch.SkiaBitmapImage
import com.github.panpf.sketch.cache.CachePolicy.DISABLED
import com.github.panpf.sketch.decode.BitmapConfig
import com.github.panpf.sketch.images.ResourceImages
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.ImageResult
import com.github.panpf.sketch.request.bitmapConfig
import com.github.panpf.sketch.test.singleton.getTestContextAndSketch
import com.github.panpf.sketch.util.asOrNull
import com.github.panpf.sketch.util.asOrThrow
import kotlinx.coroutines.test.runTest
import org.jetbrains.skia.ColorType
import kotlin.test.Test
import kotlin.test.assertEquals

class ImageRequestExecuteNonAndroidTest {

    @Test
    fun testBitmapConfig() = runTest {
        val (context, sketch) = getTestContextAndSketch()

        ImageRequest(context, ResourceImages.jpeg.uri) {
            resultCachePolicy(DISABLED)
            memoryCachePolicy(DISABLED)
        }.let { sketch.execute(it) }.asOrNull<ImageResult.Success>()!!.apply {
            assertEquals(
                expected = ColorType.RGBA_8888,
                actual = image.asOrThrow<SkiaBitmapImage>().bitmap.colorType
            )
        }

        ImageRequest(context, ResourceImages.jpeg.uri) {
            resultCachePolicy(DISABLED)
            memoryCachePolicy(DISABLED)
            bitmapConfig(ColorType.RGBA_8888)
        }.let { sketch.execute(it) }.asOrNull<ImageResult.Success>()!!.apply {
            assertEquals(
                expected = ColorType.RGBA_8888,
                actual = image.asOrThrow<SkiaBitmapImage>().bitmap.colorType
            )
        }

        ImageRequest(context, ResourceImages.jpeg.uri) {
            resultCachePolicy(DISABLED)
            memoryCachePolicy(DISABLED)
            bitmapConfig(ColorType.ARGB_4444)
        }.let { sketch.execute(it) }.asOrNull<ImageResult.Success>()!!.apply {
            assertEquals(
                expected = ColorType.ARGB_4444,
                actual = image.asOrThrow<SkiaBitmapImage>().bitmap.colorType
            )
        }

        ImageRequest(context, ResourceImages.jpeg.uri) {
            resultCachePolicy(DISABLED)
            memoryCachePolicy(DISABLED)
            bitmapConfig(ColorType.ALPHA_8)
        }.let { sketch.execute(it) }.asOrNull<ImageResult.Success>()!!.apply {
            assertEquals(
                expected = ColorType.ALPHA_8,
                actual = image.asOrThrow<SkiaBitmapImage>().bitmap.colorType
            )
        }

        ImageRequest(context, ResourceImages.jpeg.uri) {
            resultCachePolicy(DISABLED)
            memoryCachePolicy(DISABLED)
            bitmapConfig(ColorType.RGB_565)
        }.let { sketch.execute(it) }.asOrNull<ImageResult.Success>()!!.apply {
            assertEquals(
                expected = ColorType.RGB_565,
                actual = image.asOrThrow<SkiaBitmapImage>().bitmap.colorType
            )
        }

        ImageRequest(context, ResourceImages.jpeg.uri) {
            resultCachePolicy(DISABLED)
            memoryCachePolicy(DISABLED)
            bitmapConfig(ColorType.RGBA_F16)
        }.let { sketch.execute(it) }.asOrNull<ImageResult.Success>()!!.apply {
            assertEquals(
                expected = ColorType.RGBA_F16,
                actual = image.asOrThrow<SkiaBitmapImage>().bitmap.colorType
            )
        }

        ImageRequest(context, ResourceImages.jpeg.uri) {
            resultCachePolicy(DISABLED)
            memoryCachePolicy(DISABLED)
            bitmapConfig(BitmapConfig.LowQuality)
        }.let { sketch.execute(it) }.asOrNull<ImageResult.Success>()!!.apply {
            assertEquals(
                expected = ColorType.RGB_565,
                actual = image.asOrThrow<SkiaBitmapImage>().bitmap.colorType
            )
        }
        ImageRequest(context, ResourceImages.png.uri) {
            resultCachePolicy(DISABLED)
            memoryCachePolicy(DISABLED)
            bitmapConfig(BitmapConfig.LowQuality)
        }.let { sketch.execute(it) }.asOrNull<ImageResult.Success>()!!.apply {
            assertEquals(
                expected = ColorType.ARGB_4444,
                actual = image.asOrThrow<SkiaBitmapImage>().bitmap.colorType
            )
        }

        ImageRequest(context, ResourceImages.jpeg.uri) {
            resultCachePolicy(DISABLED)
            memoryCachePolicy(DISABLED)
            bitmapConfig(BitmapConfig.HighQuality)
        }.let { sketch.execute(it) }.asOrNull<ImageResult.Success>()!!.apply {
            assertEquals(
                expected = ColorType.RGBA_F16,
                actual = image.asOrThrow<SkiaBitmapImage>().bitmap.colorType
            )
        }
        ImageRequest(context, ResourceImages.png.uri) {
            resultCachePolicy(DISABLED)
            memoryCachePolicy(DISABLED)
            bitmapConfig(BitmapConfig.HighQuality)
        }.let { sketch.execute(it) }.asOrNull<ImageResult.Success>()!!.apply {
            assertEquals(
                expected = ColorType.RGBA_F16,
                actual = image.asOrThrow<SkiaBitmapImage>().bitmap.colorType
            )
        }
    }
}