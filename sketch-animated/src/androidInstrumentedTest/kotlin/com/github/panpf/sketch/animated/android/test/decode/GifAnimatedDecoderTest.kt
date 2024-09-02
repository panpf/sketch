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

package com.github.panpf.sketch.animated.android.test.decode

import android.graphics.ColorSpace
import android.os.Build
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.github.panpf.sketch.ComponentRegistry
import com.github.panpf.sketch.decode.GifAnimatedDecoder
import com.github.panpf.sketch.decode.ImageInfo
import com.github.panpf.sketch.decode.internal.createInSampledTransformed
import com.github.panpf.sketch.decode.supportAnimatedGif
import com.github.panpf.sketch.drawable.AnimatableDrawable
import com.github.panpf.sketch.fetch.FetchResult
import com.github.panpf.sketch.getDrawableOrThrow
import com.github.panpf.sketch.images.ResourceImages
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.colorSpace
import com.github.panpf.sketch.request.onAnimationEnd
import com.github.panpf.sketch.request.onAnimationStart
import com.github.panpf.sketch.request.repeatCount
import com.github.panpf.sketch.source.AssetDataSource
import com.github.panpf.sketch.source.DataFrom.LOCAL
import com.github.panpf.sketch.test.singleton.sketch
import com.github.panpf.sketch.test.utils.intrinsicSize
import com.github.panpf.sketch.test.utils.toRequestContext
import com.github.panpf.sketch.util.Size
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class GifAnimatedDecoderTest {

    @Test
    fun testSupportAnimatedGif() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) return

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

            supportAnimatedGif()
            build().apply {
                Assert.assertEquals(
                    "ComponentRegistry(" +
                            "fetcherFactoryList=[]," +
                            "decoderFactoryList=[GifAnimatedDecoder]," +
                            "requestInterceptorList=[]," +
                            "decodeInterceptorList=[]" +
                            ")",
                    toString()
                )
            }

            supportAnimatedGif()
            build().apply {
                Assert.assertEquals(
                    "ComponentRegistry(" +
                            "fetcherFactoryList=[]," +
                            "decoderFactoryList=[GifAnimatedDecoder,GifAnimatedDecoder]," +
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
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) return@runTest

        val context = InstrumentationRegistry.getInstrumentation().context
        val sketch = context.sketch
        val factory = GifAnimatedDecoder.Factory()

        Assert.assertEquals("GifAnimatedDecoder", factory.toString())

        // normal
        ImageRequest(context, ResourceImages.animGif.uri).let {
            val fetchResult =
                FetchResult(
                    AssetDataSource(context, ResourceImages.animGif.resourceName),
                    "image/gif"
                )
            factory.create(it.toRequestContext(sketch), fetchResult)
        }.apply {
            Assert.assertNotNull(this)
        }

        // no mimeType
        ImageRequest(context, ResourceImages.animGif.uri).let {
            val fetchResult =
                FetchResult(AssetDataSource(context, ResourceImages.animGif.resourceName), null)
            factory.create(it.toRequestContext(sketch), fetchResult)
        }.apply {
            Assert.assertNotNull(this)
        }

        // Disguised mimeType
        ImageRequest(context, ResourceImages.animGif.uri).let {
            val fetchResult = FetchResult(
                AssetDataSource(context, ResourceImages.animGif.resourceName),
                "image/jpeg",
            )
            factory.create(it.toRequestContext(sketch), fetchResult)
        }.apply {
            Assert.assertNotNull(this)
        }

        // disallowAnimatedImage true
        ImageRequest(context, ResourceImages.animGif.uri) {
            disallowAnimatedImage()
        }.let {
            val fetchResult =
                FetchResult(AssetDataSource(context, ResourceImages.animGif.resourceName), null)
            factory.create(it.toRequestContext(sketch), fetchResult)
        }.apply {
            Assert.assertNull(this)
        }

        // data error
        ImageRequest(context, ResourceImages.png.uri).let {
            val fetchResult =
                FetchResult(AssetDataSource(context, ResourceImages.png.resourceName), null)
            factory.create(it.toRequestContext(sketch), fetchResult)
        }.apply {
            Assert.assertNull(this)
        }

        // Disguised, mimeType; data error
        ImageRequest(context, ResourceImages.png.uri).let {
            val fetchResult =
                FetchResult(
                    AssetDataSource(context, ResourceImages.png.resourceName),
                    "image/gif"
                )
            factory.create(it.toRequestContext(sketch), fetchResult)
        }.apply {
            Assert.assertNull(this)
        }
    }

    @Test
    fun testDecodeDrawable() = runTest {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) return@runTest

        val context = InstrumentationRegistry.getInstrumentation().context
        val sketch = context.sketch
        val factory = GifAnimatedDecoder.Factory()

        val request = ImageRequest(context, ResourceImages.animGif.uri) {
            colorSpace(ColorSpace.Named.SRGB)
            onAnimationEnd { }
            onAnimationStart { }
        }
        val fetchResult = sketch.components.newFetcherOrThrow(request)
            .let { runBlocking { it.fetch() }.getOrThrow() }
        factory.create(request.toRequestContext(sketch), fetchResult)!!
            .let { runBlocking { it.decode() }.getOrThrow() }.apply {
                Assert.assertEquals(ImageInfo(480, 480, "image/gif"), this.imageInfo)
                Assert.assertEquals(Size(480, 480), image.getDrawableOrThrow().intrinsicSize)
                Assert.assertEquals(LOCAL, this.dataFrom)
                Assert.assertNull(this.transformeds)
                val animatedImageDrawable =
                    ((image.getDrawableOrThrow() as AnimatableDrawable).drawable as com.github.panpf.sketch.drawable.ScaledAnimatedImageDrawable).drawable
                Assert.assertEquals(-1, animatedImageDrawable.repeatCount)
            }

        val request1 = ImageRequest(context, ResourceImages.animGif.uri) {
            repeatCount(3)
            size(300, 300)
        }
        val fetchResult1 = sketch.components.newFetcherOrThrow(request1)
            .let { runBlocking { it.fetch() }.getOrThrow() }
        factory.create(request1.toRequestContext(sketch), fetchResult1)!!
            .let { runBlocking { it.decode() }.getOrThrow() }.apply {
                Assert.assertEquals(ImageInfo(480, 480, "image/gif"), this.imageInfo)
                Assert.assertEquals(Size(240, 240), image.getDrawableOrThrow().intrinsicSize)
                Assert.assertEquals(LOCAL, this.dataFrom)
                Assert.assertEquals(listOf(createInSampledTransformed(2)), this.transformeds)
                val animatedImageDrawable =
                    ((image.getDrawableOrThrow() as AnimatableDrawable).drawable as com.github.panpf.sketch.drawable.ScaledAnimatedImageDrawable).drawable
                Assert.assertEquals(3, animatedImageDrawable.repeatCount)
            }
    }

    @Test
    fun testFactoryEqualsAndHashCode() {
        val element1 = GifAnimatedDecoder.Factory()
        val element11 = GifAnimatedDecoder.Factory()
        val element2 = GifAnimatedDecoder.Factory()

        Assert.assertNotSame(element1, element11)
        Assert.assertNotSame(element1, element2)
        Assert.assertNotSame(element2, element11)

        Assert.assertEquals(element1, element1)
        Assert.assertEquals(element1, element11)
        Assert.assertEquals(element1, element2)
        Assert.assertEquals(element2, element11)
        Assert.assertNotEquals(element1, null)
        Assert.assertNotEquals(element1, Any())

        Assert.assertEquals(element1.hashCode(), element1.hashCode())
        Assert.assertEquals(element1.hashCode(), element11.hashCode())
        Assert.assertEquals(element1.hashCode(), element2.hashCode())
        Assert.assertEquals(element2.hashCode(), element11.hashCode())
    }
}