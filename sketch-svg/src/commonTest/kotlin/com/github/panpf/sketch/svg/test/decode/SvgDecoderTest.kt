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
package com.github.panpf.sketch.svg.test.decode

import com.github.panpf.sketch.ComponentRegistry
import com.github.panpf.sketch.decode.SvgDecoder
import com.github.panpf.sketch.decode.internal.createScaledTransformed
import com.github.panpf.sketch.decode.supportSvg
import com.github.panpf.sketch.fetch.copy
import com.github.panpf.sketch.images.MyImages
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.resize.Precision.LESS_PIXELS
import com.github.panpf.sketch.size
import com.github.panpf.sketch.source.DataFrom.LOCAL
import com.github.panpf.sketch.test.singleton.getTestContextAndSketch
import com.github.panpf.sketch.test.utils.decode
import com.github.panpf.sketch.test.utils.fetch
import com.github.panpf.sketch.test.utils.toRequestContext
import com.github.panpf.sketch.util.Size
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNotSame
import kotlin.test.assertNull

class SvgDecoderTest {

    @Test
    fun testSupportSvg() {
        ComponentRegistry.Builder().apply {
            build().apply {
                assertEquals(
                    "ComponentRegistry(" +
                            "fetcherFactoryList=[]," +
                            "decoderFactoryList=[]," +
                            "requestInterceptorList=[]," +
                            "decodeInterceptorList=[]" +
                            ")",
                    toString()
                )
            }

            supportSvg()
            build().apply {
                assertEquals(
                    "ComponentRegistry(" +
                            "fetcherFactoryList=[]," +
                            "decoderFactoryList=[SvgDecoder(useViewBoundsAsIntrinsicSize=true)]," +
                            "requestInterceptorList=[]," +
                            "decodeInterceptorList=[]" +
                            ")",
                    toString()
                )
            }

            supportSvg()
            build().apply {
                assertEquals(
                    "ComponentRegistry(" +
                            "fetcherFactoryList=[]," +
                            "decoderFactoryList=[SvgDecoder(useViewBoundsAsIntrinsicSize=true),SvgDecoder(useViewBoundsAsIntrinsicSize=true)]," +
                            "requestInterceptorList=[]," +
                            "decodeInterceptorList=[]" +
                            ")",
                    toString()
                )
            }
        }
    }

    @Test
    fun testFactory() = runTest {
        val (context, sketch) = getTestContextAndSketch()

        assertEquals(
            "SvgDecoder(useViewBoundsAsIntrinsicSize=false)",
            SvgDecoder.Factory(false).toString()
        )
        assertEquals(
            "SvgDecoder(useViewBoundsAsIntrinsicSize=true)",
            SvgDecoder.Factory(true).toString()
        )

        // normal
        val factory = SvgDecoder.Factory(false)
        ImageRequest(context, MyImages.svg.uri)
            .let {
                val fetchResult = sketch.components.newFetcherOrThrow(it).fetch().getOrThrow()
                factory.create(it.toRequestContext(sketch), fetchResult)
            }.apply {
                assertNotNull(this)
            }

        // data error
        ImageRequest(context, MyImages.png.uri)
            .let {
                val fetchResult = sketch.components.newFetcherOrThrow(it).fetch().getOrThrow()
                factory.create(it.toRequestContext(sketch), fetchResult)
            }.apply {
                assertNull(this)
            }

        // mimeType error
        ImageRequest(context, MyImages.svg.uri).let {
            val fetchResult = sketch.components.newFetcherOrThrow(it).fetch().getOrThrow()
                .copy(mimeType = "image/svg")
            factory.create(it.toRequestContext(sketch), fetchResult)
        }.apply {
            assertNotNull(this)
        }
    }

    @Test
    fun testFactoryEqualsAndHashCode() {
        val element1 = SvgDecoder.Factory()
        val element11 = SvgDecoder.Factory()
        val element2 = SvgDecoder.Factory(false)

        assertNotSame(element1, element11)
        assertNotSame(element1, element2)
        assertNotSame(element2, element11)

        assertEquals(element1, element1)
        assertEquals(element1, element11)
        assertNotEquals(element1, element2)
        assertNotEquals(element2, element11)
        assertNotEquals(element1, null as SvgDecoder.Factory?)
        assertNotEquals(element1, Any())

        assertEquals(element1.hashCode(), element1.hashCode())
        assertEquals(element1.hashCode(), element11.hashCode())
        assertNotEquals(element1.hashCode(), element2.hashCode())
        assertNotEquals(element2.hashCode(), element11.hashCode())
    }

    @Test
    fun testDecode() = runTest {
        val (context, sketch) = getTestContextAndSketch()

        val factory = SvgDecoder.Factory()

        ImageRequest(context, MyImages.svg.uri)
            .decode(sketch, factory).apply {
                assertEquals(Size(256, 225), image.size)
                assertEquals(
                    "ImageInfo(256x225,'image/svg+xml')",
                    imageInfo.toShortString()
                )
                assertEquals(LOCAL, dataFrom)
                assertNull(transformedList)
            }

        ImageRequest(context, MyImages.svg.uri) {
            resize(600, 600, LESS_PIXELS)
        }.decode(sketch, factory).apply {
            assertEquals(Size(600, 527), image.size)
            assertEquals(
                "ImageInfo(256x225,'image/svg+xml')",
                imageInfo.toShortString()
            )
            assertEquals(LOCAL, dataFrom)
            assertEquals(listOf(createScaledTransformed(2.34f)), transformedList)
        }

        ImageRequest(context, MyImages.svg.uri) {
            resize(1500, 1800, LESS_PIXELS)
        }.decode(sketch, factory).apply {
            assertEquals(Size(1500, 1318), image.size)
            assertEquals(
                "ImageInfo(256x225,'image/svg+xml')",
                imageInfo.toShortString()
            )
            assertEquals(LOCAL, dataFrom)
            assertEquals(listOf(createScaledTransformed(5.86f)), transformedList)
        }

        ImageRequest(context, MyImages.png.uri).let {
            val fetchResult = it.fetch(sketch)
            factory.create(it.toRequestContext(sketch), fetchResult)
        }.apply {
            assertNull(this)
        }
    }
}