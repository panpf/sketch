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

package com.github.panpf.sketch.svg.common.test.decode

import com.github.panpf.sketch.ComponentRegistry
import com.github.panpf.sketch.decode.SvgDecoder
import com.github.panpf.sketch.decode.internal.createResizeTransformed
import com.github.panpf.sketch.decode.internal.createScaledTransformed
import com.github.panpf.sketch.decode.supportSvg
import com.github.panpf.sketch.images.ResourceImages
import com.github.panpf.sketch.images.toDataSource
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.resize.Precision
import com.github.panpf.sketch.resize.Resize
import com.github.panpf.sketch.resize.Scale.CENTER_CROP
import com.github.panpf.sketch.size
import com.github.panpf.sketch.source.DataFrom.LOCAL
import com.github.panpf.sketch.test.singleton.getTestContextAndSketch
import com.github.panpf.sketch.test.utils.createDecoderOrDefault
import com.github.panpf.sketch.test.utils.createDecoderOrNull
import com.github.panpf.sketch.test.utils.decode
import com.github.panpf.sketch.test.utils.fetch
import com.github.panpf.sketch.test.utils.toRequestContext
import com.github.panpf.sketch.util.Size
import com.github.panpf.sketch.util.SketchSize
import com.github.panpf.sketch.util.calculateScaleMultiplierWithOneSide
import com.github.panpf.sketch.util.screenSize
import com.github.panpf.sketch.util.times
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class SvgDecoderTest {

    @Test
    fun testSupportSvg() {
        ComponentRegistry().apply {
            assertEquals(
                expected = "ComponentRegistry(" +
                        "fetchers=[]," +
                        "decoders=[]," +
                        "interceptors=[]" +
                        ")",
                actual = toString()
            )
        }

        ComponentRegistry {
            supportSvg()
        }.apply {
            assertEquals(
                expected = "ComponentRegistry(" +
                        "fetchers=[]," +
                        "decoders=[SvgDecoder(useViewBoundsAsIntrinsicSize=true)]," +
                        "interceptors=[]" +
                        ")",
                actual = toString()
            )
        }

        ComponentRegistry {
            supportSvg()
            supportSvg()
        }.apply {
            assertEquals(
                expected = "ComponentRegistry(" +
                        "fetchers=[]," +
                        "decoders=[SvgDecoder(useViewBoundsAsIntrinsicSize=true),SvgDecoder(useViewBoundsAsIntrinsicSize=true)]," +
                        "interceptors=[]" +
                        ")",
                actual = toString()
            )
        }
    }

    @Test
    fun testConstructor() = runTest {
        val (context, sketch) = getTestContextAndSketch()

        val request = ImageRequest(context, ResourceImages.svg.uri)
        val requestContext = request.toRequestContext(sketch)
        val dataSource = ResourceImages.svg.toDataSource(context)

        SvgDecoder(requestContext, dataSource)
        SvgDecoder(requestContext = requestContext, dataSource = dataSource)
    }

    @Test
    fun testImageInfo() = runTest {
        val (context, sketch) = getTestContextAndSketch()

        val factory = SvgDecoder.Factory()
        ImageRequest(context, ResourceImages.svg.uri)
            .createDecoderOrDefault(sketch, factory)
            .apply {
                assertEquals(
                    expected = "ImageInfo(257x226,'image/svg+xml')",
                    actual = imageInfo.toShortString()
                )
            }

        val factory1 = SvgDecoder.Factory(useViewBoundsAsIntrinsicSize = false)
        ImageRequest(context, ResourceImages.svg.uri)
            .createDecoderOrDefault(sketch, factory1)
            .apply {
                assertEquals(
                    expected = "ImageInfo(256x225,'image/svg+xml')",
                    actual = imageInfo.toShortString()
                )
            }
    }

    @Test
    fun testDecode() = runTest {
        val (context, sketch) = getTestContextAndSketch()

        val factory = SvgDecoder.Factory()

        // normal
        ImageRequest(context, ResourceImages.svg.uri)
            .decode(sketch, factory).apply {
                assertEquals(
                    expected = "ImageInfo(257x226,'image/svg+xml')",
                    actual = imageInfo.toShortString()
                )
                val size = context.screenSize()
                val sizeMultiplier = calculateScaleMultiplierWithOneSide(imageInfo.size, size)
                val bitmapSize = imageInfo.size.times(sizeMultiplier)
                assertEquals(expected = bitmapSize, actual = image.size)
                assertEquals(expected = LOCAL, actual = dataFrom)
                if (sizeMultiplier != 1f) {
                    assertEquals(
                        expected = listOf(createScaledTransformed(sizeMultiplier)),
                        actual = transformeds
                    )
                } else {
                    assertNull(actual = transformeds)
                }
            }

        // error: png
        ImageRequest(context, ResourceImages.png.uri).let {
            val fetchResult = it.fetch(sketch)
            factory.create(it.toRequestContext(sketch), fetchResult)
        }.apply {
            assertNull(this)
        }

        /*
         * resize size
         */
        listOf(
            SketchSize(600, 600) to (2.33f to Size(600, 528)),
            SketchSize(1500, 1500) to (5.84f to Size(1500, 1319)),
            SketchSize(600, 300) to (1.33f to Size(341, 300)),
            SketchSize(300, 600) to (1.17f to Size(300, 264)),
            SketchSize(400, 0) to (1.56f to Size(400, 352)),
            SketchSize(0, 400) to (1.77f to Size(455, 400)),
            SketchSize(0, 0) to (1f to Size(257, 226)),
        ).forEach { (targetSize, expected) ->
            val (expectedScaleFactor, expectedSize) = expected
            ImageRequest(context, ResourceImages.svg.uri) {
                size(targetSize)
            }.decode(sketch, factory).apply {
                assertEquals(
                    expected = "ImageInfo(257x226,'image/svg+xml')",
                    actual = imageInfo.toShortString(),
                    message = "targetSize=$targetSize"
                )
                assertEquals(
                    expected = expectedSize,
                    actual = image.size,
                    message = "targetSize=$targetSize"
                )
                val scaledTransformed = if (expectedScaleFactor != 1f) {
                    createScaledTransformed(expectedScaleFactor)
                } else {
                    null
                }
                assertEquals(
                    expected = listOfNotNull(scaledTransformed).takeIf { it.isNotEmpty() },
                    actual = transformeds,
                    message = "targetSize=$targetSize"
                )
            }
        }

        /*
         * resize precision
         */
        listOf(
            Precision.EXACTLY to (2.33f to Size(600, 600)),
            Precision.LESS_PIXELS to (2.33f to Size(600, 528)),
            Precision.SAME_ASPECT_RATIO to (2.33f to Size(528, 528)),
            Precision.SMALLER_SIZE to (2.33f to Size(600, 528)),
        ).forEach { (precision, expected) ->
            val (expectedScaleFactor, expectedSize) = expected
            ImageRequest(context, ResourceImages.svg.uri) {
                size(SketchSize(600, 600))
                precision(precision)
            }.decode(sketch, factory).apply {
                assertEquals(
                    expected = "ImageInfo(257x226,'image/svg+xml')",
                    actual = imageInfo.toShortString(),
                    message = "precision=$precision"
                )
                assertEquals(
                    expected = expectedSize,
                    actual = image.size,
                    message = "precision=$precision"
                )
                val resizeTransformed =
                    if (precision == Precision.EXACTLY || precision == Precision.SAME_ASPECT_RATIO) {
                        createResizeTransformed(
                            Resize(SketchSize(600, 600), precision, CENTER_CROP)
                        )
                    } else {
                        null
                    }
                val scaledTransformed = if (expectedScaleFactor != 1f) {
                    createScaledTransformed(expectedScaleFactor)
                } else {
                    null
                }
                val expectedTransformeds =
                    listOfNotNull(scaledTransformed, resizeTransformed).takeIf { it.isNotEmpty() }
                assertEquals(
                    expected = expectedTransformeds,
                    actual = transformeds,
                    message = "precision=$precision"
                )
            }
        }
    }

    @Test
    fun testEqualsAndHashCode() = runTest {
        val (context, sketch) = getTestContextAndSketch()
        val request = ImageRequest(context, ResourceImages.svg.uri)
        val requestContext = request.toRequestContext(sketch)
        val dataSource = ResourceImages.svg.toDataSource(context)
        val element1 = SvgDecoder(requestContext, dataSource)
        val element11 = SvgDecoder(requestContext, dataSource)

        assertNotEquals(illegal = element1, actual = element11)
        assertNotEquals(illegal = element1, actual = null as Any?)
        assertNotEquals(illegal = element1, actual = Any())
        assertNotEquals(illegal = element1.hashCode(), actual = element11.hashCode())
    }

    @Test
    fun testToString() = runTest {
        val (context, sketch) = getTestContextAndSketch()
        val request = ImageRequest(context, ResourceImages.svg.uri)
        val requestContext = request.toRequestContext(sketch)
        val dataSource = ResourceImages.svg.toDataSource(context)
        val decoder = SvgDecoder(requestContext, dataSource)
        assertTrue(actual = decoder.toString().contains("SvgDecoder"), message = decoder.toString())
        assertTrue(actual = decoder.toString().contains("@"), message = decoder.toString())
    }

    @Test
    fun testFactoryConstructor() {
        SvgDecoder.Factory()
    }

    @Test
    fun testFactoryKey() {
        assertEquals(
            expected = "SvgDecoder(useViewBoundsAsIntrinsicSize=false)",
            actual = SvgDecoder.Factory(false).key
        )
    }

    @Test
    fun testFactoryCreate() = runTest {
        val (context, sketch) = getTestContextAndSketch()

        // normal
        val factory = SvgDecoder.Factory(false)
        ImageRequest(context, ResourceImages.svg.uri)
            .createDecoderOrNull(sketch, factory).apply {
                assertTrue(this is SvgDecoder)
            }

        // data error
        ImageRequest(context, ResourceImages.png.uri)
            .createDecoderOrNull(sketch, factory).apply {
                assertNull(this)
            }

        // mimeType error
        ImageRequest(context, ResourceImages.svg.uri)
            .createDecoderOrNull(sketch, factory) {
                it.copy(mimeType = "image/svg")
            }.apply {
                assertTrue(this is SvgDecoder)
            }

        // Correct mimeType; data error
        ImageRequest(context, ResourceImages.png.uri)
            .createDecoderOrNull(sketch, factory) {
                it.copy(mimeType = "image/svg+xml")
            }.apply {
                assertTrue(this is SvgDecoder)
            }
    }

    @Test
    fun testFactoryEqualsAndHashCode() {
        val element1 = SvgDecoder.Factory()
        val element11 = SvgDecoder.Factory()
        val element2 = SvgDecoder.Factory(false)

        assertEquals(element1, element11)
        assertNotEquals(element1, element2)
        assertNotEquals(element1, null as SvgDecoder.Factory?)
        assertNotEquals(element1, Any())

        assertEquals(element1.hashCode(), element11.hashCode())
        assertNotEquals(element1.hashCode(), element2.hashCode())
    }

    @Test
    fun testFactoryToString() {
        assertEquals(
            expected = "SvgDecoder(useViewBoundsAsIntrinsicSize=false)",
            actual = SvgDecoder.Factory(false).toString()
        )
    }
}