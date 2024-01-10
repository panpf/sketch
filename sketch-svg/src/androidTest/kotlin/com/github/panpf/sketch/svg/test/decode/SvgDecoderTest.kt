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

import android.graphics.Bitmap
import android.graphics.Bitmap.Config.RGB_565
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.ComponentRegistry
import com.github.panpf.sketch.datasource.AssetDataSource
import com.github.panpf.sketch.datasource.DataFrom.LOCAL
import com.github.panpf.sketch.decode.SvgDecoder
import com.github.panpf.sketch.decode.internal.createScaledTransformed
import com.github.panpf.sketch.decode.supportSvg
import com.github.panpf.sketch.fetch.FetchResult
import com.github.panpf.sketch.getBitmapOrThrow
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.resize.Precision.LESS_PIXELS
import com.github.panpf.sketch.resources.AssetImages
import com.github.panpf.sketch.test.singleton.getTestContextAndSketch
import com.github.panpf.sketch.test.utils.toRequestContext
import com.github.panpf.tools4j.test.ktx.assertThrow
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SvgDecoderTest {

    @Test
    fun testSupportSvg() {
        ComponentRegistry.Builder().apply {
            build().apply {
                Assert.assertEquals(
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
                Assert.assertEquals(
                    "ComponentRegistry(" +
                            "fetcherFactoryList=[]," +
                            "decoderFactoryList=[SvgDecoder]," +
                            "requestInterceptorList=[]," +
                            "decodeInterceptorList=[]" +
                            ")",
                    toString()
                )
            }

            supportSvg()
            build().apply {
                Assert.assertEquals(
                    "ComponentRegistry(" +
                            "fetcherFactoryList=[]," +
                            "decoderFactoryList=[SvgDecoder,SvgDecoder]," +
                            "requestInterceptorList=[]," +
                            "decodeInterceptorList=[]" +
                            ")",
                    toString()
                )
            }
        }
    }

    @Test
    fun testFactory() {
        val (context, sketch) = getTestContextAndSketch()

        Assert.assertEquals(
            "SvgDecoder(useViewBoundsAsIntrinsicSize=false)",
            SvgDecoder.Factory(false).toString()
        )
        Assert.assertEquals(
            "SvgDecoder(useViewBoundsAsIntrinsicSize=true)",
            SvgDecoder.Factory(true).toString()
        )

        // normal
        val factory = SvgDecoder.Factory(false)
        ImageRequest(context, AssetImages.svg.uri).let {
            val fetchResult =
                FetchResult(AssetDataSource(sketch, it, AssetImages.svg.fileName), null)
            factory.create(sketch, it.toRequestContext(sketch), fetchResult)
        }.apply {
            Assert.assertNotNull(this)
        }

        // data error
        ImageRequest(context, AssetImages.png.uri).let {
            val fetchResult =
                FetchResult(AssetDataSource(sketch, it, AssetImages.png.fileName), null)
            factory.create(sketch, it.toRequestContext(sketch), fetchResult)
        }.apply {
            Assert.assertNull(this)
        }

        // mimeType error
        ImageRequest(context, AssetImages.svg.uri).let {
            val fetchResult =
                FetchResult(AssetDataSource(sketch, it, AssetImages.svg.fileName), "image/svg")
            factory.create(sketch, it.toRequestContext(sketch), fetchResult)
        }.apply {
            Assert.assertNotNull(this)
        }
    }

    @Test
    fun testFactoryEqualsAndHashCode() {
        val element1 = SvgDecoder.Factory()
        val element11 = SvgDecoder.Factory()
        val element2 = SvgDecoder.Factory(false)

        Assert.assertNotSame(element1, element11)
        Assert.assertNotSame(element1, element2)
        Assert.assertNotSame(element2, element11)

        Assert.assertEquals(element1, element1)
        Assert.assertEquals(element1, element11)
        Assert.assertNotEquals(element1, element2)
        Assert.assertNotEquals(element2, element11)
        Assert.assertNotEquals(element1, null)
        Assert.assertNotEquals(element1, Any())

        Assert.assertEquals(element1.hashCode(), element1.hashCode())
        Assert.assertEquals(element1.hashCode(), element11.hashCode())
        Assert.assertNotEquals(element1.hashCode(), element2.hashCode())
        Assert.assertNotEquals(element2.hashCode(), element11.hashCode())
    }

    @Test
    fun testDecode() {
        val (context, sketch) = getTestContextAndSketch()

        val factory = SvgDecoder.Factory()

        ImageRequest(context, AssetImages.svg.uri).run {
            val fetcher = sketch.components.newFetcherOrThrow(this)
            val fetchResult = runBlocking { fetcher.fetch() }.getOrThrow()
            runBlocking {
                factory.create(sketch, this@run.toRequestContext(sketch), fetchResult)!!.decode()
            }.getOrThrow()
        }.apply {
            Assert.assertEquals(
                "Bitmap(842x595,ARGB_8888)",
                image.getBitmapOrThrow().toShortInfoString()
            )
            Assert.assertEquals(
                "ImageInfo(842x595,'image/svg+xml',UNDEFINED)",
                imageInfo.toShortString()
            )
            Assert.assertEquals(LOCAL, dataFrom)
            Assert.assertNull(transformedList)
        }

        ImageRequest(context, AssetImages.svg.uri) {
            bitmapConfig(RGB_565)
        }.run {
            val fetcher = sketch.components.newFetcherOrThrow(this)
            val fetchResult = runBlocking { fetcher.fetch() }.getOrThrow()
            runBlocking {
                factory.create(sketch, this@run.toRequestContext(sketch), fetchResult)!!.decode()
            }.getOrThrow()
        }.apply {
            Assert.assertEquals(
                "Bitmap(842x595,RGB_565)",
                image.getBitmapOrThrow().toShortInfoString()
            )
            Assert.assertEquals(
                "ImageInfo(842x595,'image/svg+xml',UNDEFINED)",
                imageInfo.toShortString()
            )
            Assert.assertEquals(LOCAL, dataFrom)
            Assert.assertNull(transformedList)
        }

        ImageRequest(context, AssetImages.svg.uri) {
            resize(600, 600, LESS_PIXELS)
        }.run {
            val fetcher = sketch.components.newFetcherOrThrow(this)
            val fetchResult = runBlocking { fetcher.fetch() }.getOrThrow()
            runBlocking {
                factory.create(sketch, this@run.toRequestContext(sketch), fetchResult)!!.decode()
            }.getOrThrow()
        }.apply {
            Assert.assertEquals(
                "Bitmap(600x424,ARGB_8888)",
                image.getBitmapOrThrow().toShortInfoString()
            )
            Assert.assertEquals(listOf(createScaledTransformed(0.71f)), transformedList)
            Assert.assertEquals(
                "ImageInfo(842x595,'image/svg+xml',UNDEFINED)",
                imageInfo.toShortString()
            )
            Assert.assertEquals(LOCAL, dataFrom)
        }

        ImageRequest(context, AssetImages.svg.uri) {
            resize(1500, 1800, LESS_PIXELS)
        }.run {
            val fetcher = sketch.components.newFetcherOrThrow(this)
            val fetchResult = runBlocking { fetcher.fetch() }.getOrThrow()
            runBlocking {
                factory.create(sketch, this@run.toRequestContext(sketch), fetchResult)!!.decode()
            }.getOrThrow()
        }.apply {
            Assert.assertEquals(
                "Bitmap(1500x1061,ARGB_8888)",
                image.getBitmapOrThrow().toShortInfoString()
            )
            Assert.assertEquals(listOf(createScaledTransformed(1.78f)), transformedList)
            Assert.assertEquals(
                "ImageInfo(842x595,'image/svg+xml',UNDEFINED)",
                imageInfo.toShortString()
            )
            Assert.assertEquals(LOCAL, dataFrom)
        }

        ImageRequest(context, AssetImages.png.uri).run {
            val fetcher = sketch.components.newFetcherOrThrow(this)
            val fetchResult = runBlocking { fetcher.fetch() }.getOrThrow()
            assertThrow(NullPointerException::class) {
                runBlocking {
                    factory.create(sketch, this@run.toRequestContext(sketch), fetchResult)!!
                        .decode()
                }.getOrThrow()
            }
        }
    }

    private fun Bitmap.toShortInfoString(): String = "Bitmap(${width}x${height},$config)"
}