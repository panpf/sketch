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
import android.os.Build
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.ColorType
import com.github.panpf.sketch.colorType
import com.github.panpf.sketch.decode.internal.DrawableDecoder
import com.github.panpf.sketch.decode.internal.createResizeTransformed
import com.github.panpf.sketch.decode.internal.createScaledTransformed
import com.github.panpf.sketch.drawable.ResDrawable
import com.github.panpf.sketch.fetch.FetchResult
import com.github.panpf.sketch.fetch.newResourceUri
import com.github.panpf.sketch.images.ResourceImages
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
import com.github.panpf.sketch.test.utils.decode
import com.github.panpf.sketch.test.utils.getBitmapOrThrow
import com.github.panpf.sketch.test.utils.toDecoder
import com.github.panpf.sketch.test.utils.toRequestContext
import com.github.panpf.sketch.util.Size
import com.github.panpf.sketch.util.computeScaleMultiplierWithOneSide
import com.github.panpf.tools4a.dimen.ktx.dp2px
import kotlinx.coroutines.test.runTest
import org.junit.runner.RunWith
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNotSame
import kotlin.test.assertNull

@RunWith(AndroidJUnit4::class)
class DrawableDecoderTest {

    @Test
    fun testFactory() = runTest {
        val (context, sketch) = getTestContextAndSketch()
        val factory = DrawableDecoder.Factory()

        assertEquals("DrawableDecoder", factory.toString())
        assertEquals("DrawableDecoder", factory.key)

        // normal
        ImageRequest(
            context,
            newResourceUri(com.github.panpf.sketch.test.utils.core.R.drawable.test)
        ).let {
            val fetcher = sketch.components.newFetcherOrThrow(
                it.toRequestContext(sketch, Size.Empty)
            )
            val fetchResult = fetcher.fetch().getOrThrow()
            factory.create(it.toRequestContext(sketch), fetchResult)
        }.apply {
            assertNotNull(this)
        }

        // data error
        ImageRequest(context, ResourceImages.png.uri).let {
            val fetcher = sketch.components.newFetcherOrThrow(
                it.toRequestContext(sketch, Size.Empty)
            )
            val fetchResult = fetcher.fetch().getOrThrow()
            factory.create(it.toRequestContext(sketch), fetchResult)
        }.apply {
            assertNull(this)
        }
    }

    @Test
    fun testFactoryEqualsAndHashCode() {
        if (VERSION.SDK_INT < VERSION_CODES.KITKAT) return

        val element1 = DrawableDecoder.Factory()
        val element11 = DrawableDecoder.Factory()

        assertNotSame(element1, element11)

        assertEquals(element1, element1)
        assertEquals(element1, element11)
        assertNotEquals(element1, null as Any?)
        assertNotEquals(element1, Any())

        assertEquals(element1.hashCode(), element1.hashCode())
        assertEquals(element1.hashCode(), element11.hashCode())
    }

    @Test
    fun testImageInfo() = runTest {
        val (context, sketch) = getTestContextAndSketch()
        val factory = DrawableDecoder.Factory()

        val imageUri = newResourceUri(com.github.panpf.sketch.test.utils.core.R.drawable.test)
        val imageSize = Size(60.dp2px, 30.dp2px)
        val request = ImageRequest(context, imageUri)

        request.toDecoder(sketch, factory)
            .imageInfo.apply {
                assertEquals(
                    expected = "ImageInfo($imageSize,'text/xml')",
                    actual = toShortString()
                )
            }

        request.toDecoder(sketch, factory) { fetchResult ->
            fetchResult.copy(mimeType = null)
        }.imageInfo.apply {
            assertEquals(
                expected = "ImageInfo($imageSize,'image/png')",
                actual = toShortString()
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
                            drawableFetcher = ResDrawable(8801)
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
        val imageUri = newResourceUri(com.github.panpf.sketch.test.utils.core.R.drawable.test)
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
            assertEquals(LOCAL, dataFrom)
        }.image.getBitmapOrThrow()

        // resize: scale, EXACTLY, START_CROP
        val targetSize = Size(100, 100)
        val targetScale = computeScaleMultiplierWithOneSide(
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
            assertEquals(LOCAL, dataFrom)
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
            assertEquals(LOCAL, dataFrom)
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
            assertEquals(LOCAL, dataFrom)
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
            assertEquals(LOCAL, dataFrom)
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
                assertEquals(LOCAL, dataFrom)
            }.image.getBitmapOrThrow()
        }

        ImageRequest(context, newResourceUri(8801)).run {
            assertFailsWith(Resources.NotFoundException::class) {
                factory.create(
                    requestContext = this@run.toRequestContext(sketch),
                    fetchResult = FetchResult(
                        dataSource = DrawableDataSource(
                            context = context,
                            dataFrom = LOCAL,
                            drawableFetcher = ResDrawable(8801)
                        ),
                        mimeType = "image/png"
                    )
                )!!.decode()
            }
        }
    }
}