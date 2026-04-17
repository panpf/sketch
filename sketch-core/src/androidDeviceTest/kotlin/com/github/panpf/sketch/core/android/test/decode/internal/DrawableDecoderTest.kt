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

package com.github.panpf.sketch.core.android.test.decode.internal

import android.content.res.Resources
import android.graphics.ColorSpace
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.ColorType
import com.github.panpf.sketch.colorType
import com.github.panpf.sketch.decode.internal.DrawableDecoder
import com.github.panpf.sketch.decode.internal.createResizeTransformed
import com.github.panpf.sketch.decode.internal.createScaledTransformed
import com.github.panpf.sketch.drawable.ResDrawableFetcher
import com.github.panpf.sketch.fetch.FetchResult
import com.github.panpf.sketch.fetch.newResourceUri
import com.github.panpf.sketch.images.ComposeResImageFiles
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.colorSpace
import com.github.panpf.sketch.request.colorType
import com.github.panpf.sketch.resize.Precision
import com.github.panpf.sketch.resize.Resize
import com.github.panpf.sketch.resize.Scale
import com.github.panpf.sketch.size
import com.github.panpf.sketch.source.DataFrom.LOCAL
import com.github.panpf.sketch.source.DrawableDataSource
import com.github.panpf.sketch.test.singleton.getTestContextAndSketch
import com.github.panpf.sketch.test.utils.TestColor
import com.github.panpf.sketch.test.utils.colorSpaceNameCompat
import com.github.panpf.sketch.test.utils.corners
import com.github.panpf.sketch.test.utils.createDecoderOrDefault
import com.github.panpf.sketch.test.utils.createDecoderOrNull
import com.github.panpf.sketch.test.utils.decode
import com.github.panpf.sketch.test.utils.getBitmapOrThrow
import com.github.panpf.sketch.test.utils.toRequestContext
import com.github.panpf.sketch.util.Size
import com.github.panpf.sketch.util.calculateScaleMultiplierWithOneSide
import com.github.panpf.tools4a.dimen.ktx.dp2px
import kotlinx.coroutines.test.runTest
import org.junit.runner.RunWith
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

@RunWith(AndroidJUnit4::class)
class DrawableDecoderTest {

    @Test
    fun testConstructor() = runTest {
        val (context, sketch) = getTestContextAndSketch()

        val request = ImageRequest(
            context,
            newResourceUri(com.github.panpf.sketch.test.R.drawable.test)
        )
        val requestContext = request.toRequestContext(sketch)
        val dataSource = DrawableDataSource(
            context = context,
            drawableFetcher = ResDrawableFetcher(com.github.panpf.sketch.test.R.drawable.ic_cloudy),
            dataFrom = LOCAL
        )

        DrawableDecoder(requestContext, dataSource, "image/png")
        DrawableDecoder(
            requestContext = requestContext,
            dataSource = dataSource,
            mimeType = "image/png"
        )
    }

    @Test
    fun testImageInfo() = runTest {
        val (context, sketch) = getTestContextAndSketch()
        val factory = DrawableDecoder.Factory()

        val imageUri = newResourceUri(com.github.panpf.sketch.test.R.drawable.test)
        val imageSize = Size(60.dp2px, 30.dp2px)
        val request = ImageRequest(context, imageUri)

        request.createDecoderOrDefault(sketch, factory)
            .apply {
                assertEquals(
                    expected = "ImageInfo($imageSize,'text/xml')",
                    actual = imageInfo.toShortString()
                )
            }

        request.createDecoderOrDefault(sketch, factory) { fetchResult ->
            fetchResult.copy(mimeType = null)
        }.apply {
            assertEquals(
                expected = "ImageInfo($imageSize,'image/png')",
                actual = imageInfo.toShortString()
            )
        }

        ImageRequest(context, newResourceUri(8801)).run {
            assertFailsWith(Resources.NotFoundException::class) {
                factory.create(
                    requestContext = this@run.toRequestContext(sketch),
                    fetchResult = FetchResult(
                        dataSource = DrawableDataSource(
                            context = context,
                            dataFrom = LOCAL,
                            drawableFetcher = ResDrawableFetcher(8801)
                        ),
                        mimeType = "image/png"
                    )
                )!!.imageInfo
            }
        }
    }

    @Test
    fun testDecode() = runTest {
        val (context, sketch) = getTestContextAndSketch()

        val factory = DrawableDecoder.Factory()
        val imageUri = newResourceUri(com.github.panpf.sketch.test.R.drawable.test)
        val imageSize = Size(60.dp2px, 30.dp2px)
        val request = ImageRequest(context, imageUri)

        // default
        request.newRequest {
            size(Size.Origin)
        }.decode(sketch, factory).apply {
            val bitmap = image.getBitmapOrThrow()
            assertEquals(expected = imageSize, actual = bitmap.size)
            assertEquals(expected = ColorType.ARGB_8888, actual = bitmap.colorType)
            assertEquals(expected = colorSpaceNameCompat(), actual = bitmap.colorSpaceNameCompat)
            assertEquals(expected = listOf(0, 0, 0, 0), actual = bitmap.corners())
            assertEquals(expected = null, actual = transformeds)
            assertEquals(
                expected = "ImageInfo($imageSize,'text/xml')",
                actual = imageInfo.toShortString()
            )
            assertEquals(expected = LOCAL, actual = dataFrom)
        }.image.getBitmapOrThrow()

        // resize: scale, EXACTLY, START_CROP
        val targetSize = Size(100, 100)
        val targetScale = calculateScaleMultiplierWithOneSide(
            sourceSize = imageSize,
            targetSize = targetSize
        )
        var targetResize =
            Resize(size = targetSize, precision = Precision.EXACTLY, scale = Scale.START_CROP)
        request.newRequest {
            resize(targetResize.size, targetResize.precision, targetResize.scale)
        }.decode(sketch, factory).apply {
            val bitmap = image.getBitmapOrThrow()
            assertEquals(expected = targetSize, actual = bitmap.size)
            assertEquals(expected = ColorType.ARGB_8888, actual = bitmap.colorType)
            assertEquals(expected = colorSpaceNameCompat(), actual = bitmap.colorSpaceNameCompat)
            assertEquals(
                expected = listOf(0, 1342177280, 1342177280, 0),
                actual = bitmap.corners()
            )
            assertEquals(
                expected = listOf(
                    createScaledTransformed(targetScale),
                    createResizeTransformed(targetResize)
                ),
                actual = transformeds
            )
            assertEquals(
                expected = "ImageInfo($imageSize,'text/xml')",
                actual = imageInfo.toShortString()
            )
            assertEquals(expected = LOCAL, actual = dataFrom)
        }.image.getBitmapOrThrow()

        // resize: scale, EXACTLY, CENTER_CROP
        targetResize =
            Resize(size = targetSize, precision = Precision.EXACTLY, scale = Scale.CENTER_CROP)
        request.newRequest {
            resize(targetResize.size, targetResize.precision, targetResize.scale)
        }.decode(sketch, factory).apply {
            val bitmap = image.getBitmapOrThrow()
            assertEquals(expected = targetSize, actual = bitmap.size)
            assertEquals(expected = ColorType.ARGB_8888, actual = bitmap.colorType)
            assertEquals(expected = colorSpaceNameCompat(), actual = bitmap.colorSpaceNameCompat)
            assertEquals(
                expected = listOf(1342177280, 1342177280, 1342177280, 1342177280),
                actual = bitmap.corners()
            )
            assertEquals(
                expected = listOf(
                    createScaledTransformed(targetScale),
                    createResizeTransformed(targetResize)
                ),
                actual = transformeds
            )
            assertEquals(
                expected = "ImageInfo($imageSize,'text/xml')",
                actual = imageInfo.toShortString()
            )
            assertEquals(expected = LOCAL, actual = dataFrom)
        }.image.getBitmapOrThrow()

        // size: scale, EXACTLY, END_CROP
        targetResize =
            Resize(size = targetSize, precision = Precision.EXACTLY, scale = Scale.END_CROP)
        request.newRequest {
            resize(targetResize.size, targetResize.precision, targetResize.scale)
        }.decode(sketch, factory).apply {
            val bitmap = image.getBitmapOrThrow()
            assertEquals(expected = targetSize, actual = bitmap.size)
            assertEquals(expected = ColorType.ARGB_8888, actual = bitmap.colorType)
            assertEquals(expected = colorSpaceNameCompat(), actual = bitmap.colorSpaceNameCompat)
            assertEquals(
                expected = listOf(1342177280, 0, 0, 1342177280),
                actual = bitmap.corners()
            )
            assertEquals(
                expected = listOf(
                    createScaledTransformed(targetScale),
                    createResizeTransformed(targetResize)
                ),
                actual = transformeds
            )
            assertEquals(
                expected = "ImageInfo($imageSize,'text/xml')",
                actual = imageInfo.toShortString()
            )
            assertEquals(expected = LOCAL, actual = dataFrom)
        }.image.getBitmapOrThrow()

        // colorType
        request.newRequest {
            size(Size.Origin)
            colorType(ColorType.RGB_565)
        }.decode(sketch, factory).apply {
            val bitmap = image.getBitmapOrThrow()
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
            assertEquals(expected = null, actual = transformeds)
            assertEquals(
                expected = "ImageInfo($imageSize,'text/xml')",
                actual = imageInfo.toShortString()
            )
            assertEquals(expected = LOCAL, actual = dataFrom)
        }.image.getBitmapOrThrow()

        // colorSpace
        if (VERSION.SDK_INT >= VERSION_CODES.O) {
            request.newRequest {
                size(Size.Origin)
                colorSpace(ColorSpace.Named.DISPLAY_P3)
            }.decode(sketch, factory).apply {
                val bitmap = image.getBitmapOrThrow()
                assertEquals(expected = imageSize, actual = bitmap.size)
                assertEquals(expected = ColorType.ARGB_8888, actual = bitmap.colorType)
                assertEquals(
                    expected = colorSpaceNameCompat("DISPLAY_P3"),
                    actual = bitmap.colorSpaceNameCompat
                )
                assertEquals(expected = listOf(0, 0, 0, 0), actual = bitmap.corners())
                assertEquals(expected = null, actual = transformeds)
                assertEquals(
                    expected = "ImageInfo($imageSize,'text/xml')",
                    actual = imageInfo.toShortString()
                )
                assertEquals(expected = LOCAL, actual = dataFrom)
            }.image.getBitmapOrThrow()
        }

        assertFailsWith(Resources.NotFoundException::class) {
            val request1 = ImageRequest(context, newResourceUri(8801))
            val requestContext = request1.toRequestContext(sketch)
            val dataSource = DrawableDataSource(context, ResDrawableFetcher(8801), LOCAL)
            val fetchResult = FetchResult(dataSource = dataSource, mimeType = "image/png")
            factory.create(requestContext, fetchResult)!!.decode()
        }
    }

    @Test
    fun testEqualsAndHashCode() = runTest {
        val (context, sketch) = getTestContextAndSketch()
        val request = ImageRequest(
            context,
            newResourceUri(com.github.panpf.sketch.test.R.drawable.test)
        )
        val requestContext = request.toRequestContext(sketch)
        val dataSource = DrawableDataSource(
            context = context,
            drawableFetcher = ResDrawableFetcher(com.github.panpf.sketch.test.R.drawable.ic_cloudy),
            dataFrom = LOCAL
        )
        val element1 = DrawableDecoder(requestContext, dataSource, mimeType = null)
        val element11 = DrawableDecoder(requestContext, dataSource, mimeType = null)

        assertNotEquals(illegal = element1, actual = element11)
        assertNotEquals(illegal = element1, actual = null as Any?)
        assertNotEquals(illegal = element1, actual = Any())
        assertNotEquals(illegal = element1.hashCode(), actual = element11.hashCode())
    }

    @Test
    fun testToString() = runTest {
        val (context, sketch) = getTestContextAndSketch()
        val request = ImageRequest(
            context,
            newResourceUri(com.github.panpf.sketch.test.R.drawable.test)
        )
        val requestContext = request.toRequestContext(sketch)
        val dataSource = DrawableDataSource(
            context = context,
            drawableFetcher = ResDrawableFetcher(com.github.panpf.sketch.test.R.drawable.ic_cloudy),
            dataFrom = LOCAL
        )
        val decoder = DrawableDecoder(requestContext, dataSource, mimeType = null)
        assertTrue(
            actual = decoder.toString().contains("DrawableDecoder"),
            message = decoder.toString()
        )
        assertTrue(actual = decoder.toString().contains("@"), message = decoder.toString())
    }

    @Test
    fun testFactoryConstructor() {
        DrawableDecoder.Factory()
    }

    @Test
    fun testFactoryKey() {
        assertEquals(
            expected = "DrawableDecoder",
            actual = DrawableDecoder.Factory().key
        )
    }

    @Test
    fun testFactoryCreate() = runTest {
        val (context, sketch) = getTestContextAndSketch()
        val factory = DrawableDecoder.Factory()

        // normal
        ImageRequest(
            context = context,
            uri = newResourceUri(com.github.panpf.sketch.test.R.drawable.test)
        ).createDecoderOrNull(sketch, factory).apply {
            assertTrue(this is DrawableDecoder)
        }

        // data error
        ImageRequest(context, ComposeResImageFiles.png.uri)
            .createDecoderOrNull(sketch, factory).apply {
                assertNull(this)
            }
    }

    @Test
    fun testFactoryEqualsAndHashCode() {
        val element1 = DrawableDecoder.Factory()
        val element11 = DrawableDecoder.Factory()

        assertEquals(expected = element1, actual = element11)
        assertNotEquals(illegal = element1, actual = null as Any?)
        assertNotEquals(illegal = element1, actual = Any())

        assertEquals(expected = element1.hashCode(), actual = element11.hashCode())
    }

    @Test
    fun testFactoryToString() = runTest {
        assertEquals(
            expected = "DrawableDecoder",
            actual = DrawableDecoder.Factory().toString()
        )
    }
}