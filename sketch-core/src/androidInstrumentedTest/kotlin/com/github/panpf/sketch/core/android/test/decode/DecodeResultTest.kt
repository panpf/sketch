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
package com.github.panpf.sketch.core.android.test.decode

import android.graphics.Bitmap
import android.graphics.Bitmap.Config.RGB_565
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.source.DataFrom.LOCAL
import com.github.panpf.sketch.source.DataFrom.MEMORY
import com.github.panpf.sketch.decode.DecodeResult
import com.github.panpf.sketch.decode.ImageInfo
import com.github.panpf.sketch.decode.internal.createInSampledTransformed
import com.github.panpf.sketch.asSketchImage
import com.github.panpf.sketch.getBitmapOrThrow
import com.github.panpf.sketch.resize.Precision.EXACTLY
import com.github.panpf.sketch.resize.Precision.LESS_PIXELS
import com.github.panpf.sketch.resize.Resize
import com.github.panpf.sketch.resize.Scale.CENTER_CROP
import com.github.panpf.sketch.resize.Scale.FILL
import com.github.panpf.sketch.transform.createCircleCropTransformed
import com.github.panpf.sketch.transform.createRotateTransformed
import com.github.panpf.sketch.util.Size
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DecodeResultTest {

    @Test
    fun testConstructor() {
        val newBitmap = Bitmap.createBitmap(100, 100, RGB_565)
        val imageInfo = ImageInfo(3000, 500, "image/png")
        val transformeds = listOf(createInSampledTransformed(4), createRotateTransformed(45))
        DecodeResult(
            image = newBitmap.asSketchImage(),
            imageInfo = imageInfo,
            dataFrom = LOCAL,
            resize = Resize(100, 100, LESS_PIXELS, CENTER_CROP),
            transformeds = transformeds,
            extras = mapOf("age" to "16")
        ).apply {
            Assert.assertTrue(newBitmap === image.getBitmapOrThrow())
            Assert.assertEquals(
                "ImageInfo(size=3000x500, mimeType='image/png')",
                imageInfo.toString()
            )
            Assert.assertEquals(LOCAL, dataFrom)
            Assert.assertEquals(Resize(100, 100, LESS_PIXELS, CENTER_CROP), resize)
            Assert.assertEquals(
                "InSampledTransformed(4), RotateTransformed(45)",
                this.transformeds?.joinToString()
            )
            Assert.assertEquals(
                mapOf("age" to "16"),
                this.extras
            )
        }
    }

    @Test
    fun testToString() {
        val image = Bitmap.createBitmap(100, 100, RGB_565).asSketchImage()
        DecodeResult(
            image = image,
            imageInfo = ImageInfo(3000, 500, "image/png"),
            dataFrom = LOCAL,
            resize = Resize(100, 100, LESS_PIXELS, CENTER_CROP),
            transformeds = listOf(createInSampledTransformed(4), createRotateTransformed(45)),
            extras = mapOf("age" to "16"),
        ).apply {
            Assert.assertEquals(
                "DecodeResult(" +
                        "image=$image, " +
                        "imageInfo=$imageInfo, " +
                        "dataFrom=$dataFrom, " +
                        "resize=$resize, " +
                        "transformeds=$transformeds, " +
                        "extras={age=16})",
                toString()
            )
        }
    }

    @Test
    fun testNewResult() {
        val bitmap1 = Bitmap.createBitmap(100, 100, RGB_565)
        val bitmap2 = Bitmap.createBitmap(200, 200, RGB_565)

        val result = DecodeResult(
            image = bitmap1.asSketchImage(),
            imageInfo = ImageInfo(3000, 500, "image/png"),
            dataFrom = LOCAL,
            resize = Resize(100, 100, LESS_PIXELS, CENTER_CROP),
            transformeds = listOf(createInSampledTransformed(4), createRotateTransformed(45)),
            extras = mapOf("age" to "16"),
        ).apply {
            Assert.assertEquals(bitmap1, image.getBitmapOrThrow())
            Assert.assertEquals(ImageInfo(3000, 500, "image/png"), imageInfo)
            Assert.assertEquals(LOCAL, dataFrom)
            Assert.assertEquals(Resize(100, 100, LESS_PIXELS, CENTER_CROP), resize)
            Assert.assertEquals(
                listOf(createInSampledTransformed(4), createRotateTransformed(45)),
                transformeds
            )
            Assert.assertEquals(
                mapOf("age" to "16"),
                this.extras
            )
        }

        result.newResult().apply {
            Assert.assertNotSame(result, this)
            Assert.assertEquals(result, this)
            Assert.assertEquals(bitmap1, image.getBitmapOrThrow())
            Assert.assertEquals(ImageInfo(3000, 500, "image/png"), imageInfo)
            Assert.assertEquals(LOCAL, dataFrom)
            Assert.assertEquals(Resize(100, 100, LESS_PIXELS, CENTER_CROP), resize)
            Assert.assertEquals(
                listOf(createInSampledTransformed(4), createRotateTransformed(45)),
                transformeds
            )
            Assert.assertEquals(
                mapOf("age" to "16"),
                this.extras
            )
        }

        result.newResult(image = bitmap2.asSketchImage()).apply {
            Assert.assertNotSame(result, this)
            Assert.assertNotEquals(result, this)
            Assert.assertEquals(bitmap2, image.getBitmapOrThrow())
            Assert.assertEquals(ImageInfo(3000, 500, "image/png"), imageInfo)
            Assert.assertEquals(LOCAL, dataFrom)
            Assert.assertEquals(Resize(100, 100, LESS_PIXELS, CENTER_CROP), resize)
            Assert.assertEquals(
                listOf(createInSampledTransformed(4), createRotateTransformed(45)),
                transformeds
            )
            Assert.assertEquals(
                mapOf("age" to "16"),
                this.extras
            )
        }

        result.newResult(imageInfo = result.imageInfo.copy(Size(200, 200)))
            .apply {
                Assert.assertNotSame(result, this)
                Assert.assertNotEquals(result, this)
                Assert.assertEquals(bitmap1, image.getBitmapOrThrow())
                Assert.assertEquals(ImageInfo(200, 200, "image/png"), imageInfo)
                Assert.assertEquals(LOCAL, dataFrom)
                Assert.assertEquals(Resize(100, 100, LESS_PIXELS, CENTER_CROP), resize)
                Assert.assertEquals(
                    listOf(createInSampledTransformed(4), createRotateTransformed(45)),
                    transformeds
                )
                Assert.assertEquals(
                    mapOf("age" to "16"),
                    this.extras
                )
            }

        result.newResult(dataFrom = MEMORY).apply {
            Assert.assertNotSame(result, this)
            Assert.assertNotEquals(result, this)
            Assert.assertEquals(bitmap1, image.getBitmapOrThrow())
            Assert.assertEquals(ImageInfo(3000, 500, "image/png"), imageInfo)
            Assert.assertEquals(MEMORY, dataFrom)
            Assert.assertEquals(Resize(100, 100, LESS_PIXELS, CENTER_CROP), resize)
            Assert.assertEquals(
                listOf(createInSampledTransformed(4), createRotateTransformed(45)),
                transformeds
            )
            Assert.assertEquals(
                mapOf("age" to "16"),
                this.extras
            )
        }

        result.newResult {
            addTransformed(createCircleCropTransformed(FILL))
        }.apply {
            Assert.assertNotSame(result, this)
            Assert.assertNotEquals(result, this)
            Assert.assertEquals(bitmap1, image.getBitmapOrThrow())
            Assert.assertEquals(ImageInfo(3000, 500, "image/png"), imageInfo)
            Assert.assertEquals(LOCAL, dataFrom)
            Assert.assertEquals(Resize(100, 100, LESS_PIXELS, CENTER_CROP), resize)
            Assert.assertEquals(
                listOf(
                    createInSampledTransformed(4),
                    createRotateTransformed(45),
                    createCircleCropTransformed(FILL)
                ),
                transformeds
            )
            Assert.assertEquals(
                mapOf("age" to "16"),
                this.extras
            )
        }

        result.newResult {
            addExtras("sex", "male")
        }.apply {
            Assert.assertNotSame(result, this)
            Assert.assertNotEquals(result, this)
            Assert.assertEquals(bitmap1, image.getBitmapOrThrow())
            Assert.assertEquals(ImageInfo(3000, 500, "image/png"), imageInfo)
            Assert.assertEquals(LOCAL, dataFrom)
            Assert.assertEquals(Resize(100, 100, LESS_PIXELS, CENTER_CROP), resize)
            Assert.assertEquals(
                listOf(createInSampledTransformed(4), createRotateTransformed(45)),
                transformeds
            )
            Assert.assertEquals(
                mapOf("age" to "16", "sex" to "male"),
                this.extras
            )
        }

        result.newResult(resize = Resize(200, 300, EXACTLY, FILL)).apply {
            Assert.assertNotSame(result, this)
            Assert.assertNotEquals(result, this)
            Assert.assertEquals(bitmap1, image.getBitmapOrThrow())
            Assert.assertEquals(ImageInfo(3000, 500, "image/png"), imageInfo)
            Assert.assertEquals(LOCAL, dataFrom)
            Assert.assertEquals(Resize(200, 300, EXACTLY, FILL), resize)
            Assert.assertEquals(
                listOf(createInSampledTransformed(4), createRotateTransformed(45)),
                transformeds
            )
            Assert.assertEquals(
                mapOf("age" to "16"),
                this.extras
            )
        }
    }
}