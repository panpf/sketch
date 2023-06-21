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
import android.os.Build
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.github.panpf.sketch.ComponentRegistry
import com.github.panpf.sketch.datasource.AssetDataSource
import com.github.panpf.sketch.datasource.DataFrom.LOCAL
import com.github.panpf.sketch.decode.SvgBitmapDecoder
import com.github.panpf.sketch.decode.internal.createInSampledTransformed
import com.github.panpf.sketch.decode.supportSvg
import com.github.panpf.sketch.fetch.FetchResult
import com.github.panpf.sketch.fetch.newAssetUri
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.LoadRequest
import com.github.panpf.sketch.request.internal.RequestContext
import com.github.panpf.sketch.resize.Precision.LESS_PIXELS
import com.github.panpf.sketch.sketch
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
        LoadRequest(context, newAssetUri("sample.svg")).let {
            val fetchResult = FetchResult(AssetDataSource(sketch, it, "sample.svg"), null)
            factory.create(sketch, it.toRequestContext(), fetchResult)
        }.apply {
            Assert.assertNotNull(this)
        }

        // data error
        LoadRequest(context, newAssetUri("sample.png")).let {
            val fetchResult = FetchResult(AssetDataSource(sketch, it, "sample.png"), null)
            factory.create(sketch, it.toRequestContext(), fetchResult)
        }.apply {
            Assert.assertNull(this)
        }

        // mimeType error
        LoadRequest(context, newAssetUri("sample.svg")).let {
            val fetchResult = FetchResult(AssetDataSource(sketch, it, "sample.svg"), "image/svg")
            factory.create(sketch, it.toRequestContext(), fetchResult)
        }.apply {
            Assert.assertNotNull(this)
        }
    }

    @Test
    fun testFactoryEqualsAndHashCode() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) return

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

        LoadRequest(context, newAssetUri("sample.svg")).run {
            val fetcher = sketch.components.newFetcherOrThrow(this)
            val fetchResult = runBlocking { fetcher.fetch() }.getOrThrow()
            runBlocking {
                factory.create(sketch, this@run.toRequestContext(), fetchResult)!!.decode()
            }.getOrThrow()
        }.apply {
            Assert.assertEquals("Bitmap(841x595,ARGB_8888)", bitmap.toShortInfoString())
            Assert.assertEquals(
                "ImageInfo(841x595,'image/svg+xml',UNDEFINED)",
                imageInfo.toShortString()
            )
            Assert.assertEquals(LOCAL, dataFrom)
            Assert.assertNull(transformedList)
        }

        LoadRequest(context, newAssetUri("sample.svg")) {
            bitmapConfig(RGB_565)
        }.run {
            val fetcher = sketch.components.newFetcherOrThrow(this)
            val fetchResult = runBlocking { fetcher.fetch() }.getOrThrow()
            runBlocking {
                factory.create(sketch, this@run.toRequestContext(), fetchResult)!!.decode()
            }.getOrThrow()
        }.apply {
            Assert.assertEquals("Bitmap(841x595,RGB_565)", bitmap.toShortInfoString())
            Assert.assertEquals(
                "ImageInfo(841x595,'image/svg+xml',UNDEFINED)",
                imageInfo.toShortString()
            )
            Assert.assertEquals(LOCAL, dataFrom)
            Assert.assertNull(transformedList)
        }

        LoadRequest(context, newAssetUri("sample.svg")) {
            resize(600, 600, LESS_PIXELS)
        }.run {
            val fetcher = sketch.components.newFetcherOrThrow(this)
            val fetchResult = runBlocking { fetcher.fetch() }.getOrThrow()
            runBlocking {
                factory.create(sketch, this@run.toRequestContext(), fetchResult)!!.decode()
            }.getOrThrow()
        }.apply {
            Assert.assertEquals("Bitmap(600x424,ARGB_8888)", bitmap.toShortInfoString())
            Assert.assertEquals(listOf(createInSampledTransformed(2)), transformedList)
            Assert.assertEquals(
                "ImageInfo(841x595,'image/svg+xml',UNDEFINED)",
                imageInfo.toShortString()
            )
            Assert.assertEquals(LOCAL, dataFrom)
        }

        LoadRequest(context, newAssetUri("sample.svg")) {
            resize(1500, 1800, LESS_PIXELS)
        }.run {
            val fetcher = sketch.components.newFetcherOrThrow(this)
            val fetchResult = runBlocking { fetcher.fetch() }.getOrThrow()
            runBlocking {
                factory.create(sketch, this@run.toRequestContext(), fetchResult)!!.decode()
            }.getOrThrow()
        }.apply {
            Assert.assertEquals("Bitmap(1500x1061,ARGB_8888)", bitmap.toShortInfoString())
            Assert.assertEquals(null, transformedList)
            Assert.assertEquals(
                "ImageInfo(841x595,'image/svg+xml',UNDEFINED)",
                imageInfo.toShortString()
            )
            Assert.assertEquals(LOCAL, dataFrom)
        }

        LoadRequest(context, newAssetUri("sample.png")).run {
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