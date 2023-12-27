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
import androidx.test.platform.app.InstrumentationRegistry
import com.github.panpf.sketch.ComponentRegistry
import com.github.panpf.sketch.datasource.AssetDataSource
import com.github.panpf.sketch.datasource.DataFrom.LOCAL
import com.github.panpf.sketch.decode.SvgBitmapDecoder
import com.github.panpf.sketch.decode.internal.createScaledTransformed
import com.github.panpf.sketch.decode.supportSvg
import com.github.panpf.sketch.fetch.FetchResult
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.LoadRequest
import com.github.panpf.sketch.request.internal.RequestContext
import com.github.panpf.sketch.resize.Precision.LESS_PIXELS
import com.github.panpf.sketch.resources.AssetImages
import com.github.panpf.sketch.test.singleton.sketch
import com.github.panpf.sketch.util.Size
import com.github.panpf.tools4j.test.ktx.assertThrow
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SvgBitmapDecoderTest {

    @Test
    fun testSupportSvg() {
        ComponentRegistry.Builder().apply {
            build().apply {
                Assert.assertEquals(
                    "ComponentRegistry(" +
                            "fetcherFactoryList=[]," +
                            "bitmapDecoderFactoryList=[]," +
                            "drawableDecoderFactoryList=[]," +
                            "requestInterceptorList=[]," +
                            "bitmapDecodeInterceptorList=[]," +
                            "drawableDecodeInterceptorList=[]" +
                            ")",
                    toString()
                )
            }

            supportSvg()
            build().apply {
                Assert.assertEquals(
                    "ComponentRegistry(" +
                            "fetcherFactoryList=[]," +
                            "bitmapDecoderFactoryList=[SvgBitmapDecoder]," +
                            "drawableDecoderFactoryList=[]," +
                            "requestInterceptorList=[]," +
                            "bitmapDecodeInterceptorList=[]," +
                            "drawableDecodeInterceptorList=[]" +
                            ")",
                    toString()
                )
            }

            supportSvg()
            build().apply {
                Assert.assertEquals(
                    "ComponentRegistry(" +
                            "fetcherFactoryList=[]," +
                            "bitmapDecoderFactoryList=[SvgBitmapDecoder,SvgBitmapDecoder]," +
                            "drawableDecoderFactoryList=[]," +
                            "requestInterceptorList=[]," +
                            "bitmapDecodeInterceptorList=[]," +
                            "drawableDecodeInterceptorList=[]" +
                            ")",
                    toString()
                )
            }
        }
    }

    @Test
    fun testFactory() {
        val context = InstrumentationRegistry.getInstrumentation().context
        val sketch = context.sketch
        val factory = SvgBitmapDecoder.Factory(false)

        Assert.assertEquals("SvgBitmapDecoder", factory.toString())

        // normal
        LoadRequest(context, AssetImages.svg.uri).let {
            val fetchResult =
                FetchResult(AssetDataSource(sketch, it, AssetImages.svg.fileName), null)
            factory.create(sketch, it.toRequestContext(), fetchResult)
        }.apply {
            Assert.assertNotNull(this)
        }

        // data error
        LoadRequest(context, AssetImages.png.uri).let {
            val fetchResult =
                FetchResult(AssetDataSource(sketch, it, AssetImages.png.fileName), null)
            factory.create(sketch, it.toRequestContext(), fetchResult)
        }.apply {
            Assert.assertNull(this)
        }

        // mimeType error
        LoadRequest(context, AssetImages.svg.uri).let {
            val fetchResult =
                FetchResult(AssetDataSource(sketch, it, AssetImages.svg.fileName), "image/svg")
            factory.create(sketch, it.toRequestContext(), fetchResult)
        }.apply {
            Assert.assertNotNull(this)
        }
    }

    @Test
    fun testFactoryEqualsAndHashCode() {
        val element1 = SvgBitmapDecoder.Factory()
        val element11 = SvgBitmapDecoder.Factory()
        val element2 = SvgBitmapDecoder.Factory(false)

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
        val context = InstrumentationRegistry.getInstrumentation().context
        val sketch = context.sketch

        val factory = SvgBitmapDecoder.Factory()

        LoadRequest(context, AssetImages.svg.uri).run {
            val fetcher = sketch.components.newFetcherOrThrow(this)
            val fetchResult = runBlocking { fetcher.fetch() }.getOrThrow()
            runBlocking {
                factory.create(sketch, this@run.toRequestContext(), fetchResult)!!.decode()
            }.getOrThrow()
        }.apply {
            Assert.assertEquals("Bitmap(842x595,ARGB_8888)", bitmap.toShortInfoString())
            Assert.assertEquals(
                "ImageInfo(842x595,'image/svg+xml',UNDEFINED)",
                imageInfo.toShortString()
            )
            Assert.assertEquals(LOCAL, dataFrom)
            Assert.assertNull(transformedList)
        }

        LoadRequest(context, AssetImages.svg.uri) {
            bitmapConfig(RGB_565)
        }.run {
            val fetcher = sketch.components.newFetcherOrThrow(this)
            val fetchResult = runBlocking { fetcher.fetch() }.getOrThrow()
            runBlocking {
                factory.create(sketch, this@run.toRequestContext(), fetchResult)!!.decode()
            }.getOrThrow()
        }.apply {
            Assert.assertEquals("Bitmap(842x595,RGB_565)", bitmap.toShortInfoString())
            Assert.assertEquals(
                "ImageInfo(842x595,'image/svg+xml',UNDEFINED)",
                imageInfo.toShortString()
            )
            Assert.assertEquals(LOCAL, dataFrom)
            Assert.assertNull(transformedList)
        }

        LoadRequest(context, AssetImages.svg.uri) {
            resize(600, 600, LESS_PIXELS)
        }.run {
            val fetcher = sketch.components.newFetcherOrThrow(this)
            val fetchResult = runBlocking { fetcher.fetch() }.getOrThrow()
            runBlocking {
                factory.create(sketch, this@run.toRequestContext(), fetchResult)!!.decode()
            }.getOrThrow()
        }.apply {
            Assert.assertEquals("Bitmap(600x424,ARGB_8888)", bitmap.toShortInfoString())
            Assert.assertEquals(listOf(createScaledTransformed(0.71f)), transformedList)
            Assert.assertEquals(
                "ImageInfo(842x595,'image/svg+xml',UNDEFINED)",
                imageInfo.toShortString()
            )
            Assert.assertEquals(LOCAL, dataFrom)
        }

        LoadRequest(context, AssetImages.svg.uri) {
            resize(1500, 1800, LESS_PIXELS)
        }.run {
            val fetcher = sketch.components.newFetcherOrThrow(this)
            val fetchResult = runBlocking { fetcher.fetch() }.getOrThrow()
            runBlocking {
                factory.create(sketch, this@run.toRequestContext(), fetchResult)!!.decode()
            }.getOrThrow()
        }.apply {
            Assert.assertEquals("Bitmap(1500x1061,ARGB_8888)", bitmap.toShortInfoString())
            Assert.assertEquals(listOf(createScaledTransformed(1.78f)), transformedList)
            Assert.assertEquals(
                "ImageInfo(842x595,'image/svg+xml',UNDEFINED)",
                imageInfo.toShortString()
            )
            Assert.assertEquals(LOCAL, dataFrom)
        }

        LoadRequest(context, AssetImages.png.uri).run {
            val fetcher = sketch.components.newFetcherOrThrow(this)
            val fetchResult = runBlocking { fetcher.fetch() }.getOrThrow()
            assertThrow(NullPointerException::class) {
                runBlocking {
                    factory.create(sketch, this@run.toRequestContext(), fetchResult)!!.decode()
                }.getOrThrow()
            }
        }
    }

    private fun Bitmap.toShortInfoString(): String = "Bitmap(${width}x${height},$config)"
}

fun ImageRequest.toRequestContext(resizeSize: Size? = null): RequestContext {
    return RequestContext(this, resizeSize ?: runBlocking { resizeSizeResolver.size() })
}