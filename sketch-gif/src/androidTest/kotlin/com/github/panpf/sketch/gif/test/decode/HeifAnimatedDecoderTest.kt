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
package com.github.panpf.sketch.gif.test.decode

import android.graphics.ColorSpace
import android.os.Build
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.github.panpf.sketch.ComponentRegistry
import com.github.panpf.sketch.datasource.AssetDataSource
import com.github.panpf.sketch.datasource.DataFrom.LOCAL
import com.github.panpf.sketch.decode.HeifAnimatedDecoder
import com.github.panpf.sketch.decode.ImageInfo
import com.github.panpf.sketch.decode.internal.createInSampledTransformed
import com.github.panpf.sketch.decode.supportAnimatedHeif
import com.github.panpf.sketch.drawable.SketchAnimatableDrawable
import com.github.panpf.sketch.fetch.FetchResult
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.getDrawableOrThrow
import com.github.panpf.sketch.request.onAnimationEnd
import com.github.panpf.sketch.request.onAnimationStart
import com.github.panpf.sketch.request.repeatCount
import com.github.panpf.sketch.resources.AssetImages
import com.github.panpf.sketch.test.singleton.sketch
import com.github.panpf.sketch.test.utils.intrinsicSize
import com.github.panpf.sketch.test.utils.toRequestContext
import com.github.panpf.sketch.util.Size
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class HeifAnimatedDecoderTest {

    @Test
    fun testSupportApkIcon() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) return

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

            supportAnimatedHeif()
            build().apply {
                Assert.assertEquals(
                    "ComponentRegistry(" +
                            "fetcherFactoryList=[]," +
                            "decoderFactoryList=[HeifAnimatedDecoder]," +
                            "requestInterceptorList=[]," +
                            "decodeInterceptorList=[]" +
                            ")",
                    toString()
                )
            }

            supportAnimatedHeif()
            build().apply {
                Assert.assertEquals(
                    "ComponentRegistry(" +
                            "fetcherFactoryList=[]," +
                            "decoderFactoryList=[HeifAnimatedDecoder,HeifAnimatedDecoder]," +
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
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) return

        val context = InstrumentationRegistry.getInstrumentation().context
        val sketch = context.sketch
        val factory = HeifAnimatedDecoder.Factory()

        Assert.assertEquals("HeifAnimatedDecoder", factory.toString())

        // normal
        ImageRequest(context, AssetImages.animHeif.uri).let {
            val fetchResult =
                FetchResult(
                    AssetDataSource(sketch, it, AssetImages.animHeif.fileName),
                    "image/heif"
                )
            factory.create(sketch, it.toRequestContext(sketch), fetchResult)
        }.apply {
            Assert.assertNotNull(this)
        }

        ImageRequest(context, AssetImages.animHeif.uri).let {
            val fetchResult =
                FetchResult(AssetDataSource(sketch, it, AssetImages.animHeif.fileName), null)
            factory.create(sketch, it.toRequestContext(sketch), fetchResult)
        }.apply {
            Assert.assertNotNull(this)
        }

        // disallowAnimatedImage true
        ImageRequest(context, AssetImages.animHeif.uri) {
            disallowAnimatedImage()
        }.let {
            val fetchResult =
                FetchResult(AssetDataSource(sketch, it, AssetImages.animHeif.fileName), null)
            factory.create(sketch, it.toRequestContext(sketch), fetchResult)
        }.apply {
            Assert.assertNull(this)
        }

        // data error
        ImageRequest(context, AssetImages.png.uri).let {
            val fetchResult =
                FetchResult(AssetDataSource(sketch, it, AssetImages.png.fileName), null)
            factory.create(sketch, it.toRequestContext(sketch), fetchResult)
        }.apply {
            Assert.assertNull(this)
        }

        ImageRequest(context, AssetImages.animGif.uri).let {
            val fetchResult =
                FetchResult(AssetDataSource(sketch, it, AssetImages.animGif.fileName), "image/heif")
            factory.create(sketch, it.toRequestContext(sketch), fetchResult)
        }.apply {
            Assert.assertNull(this)
        }

        // mimeType error
        ImageRequest(context, AssetImages.animHeif.uri).let {
            val fetchResult = FetchResult(
                AssetDataSource(sketch, it, AssetImages.animHeif.fileName),
                "image/jpeg",
            )
            factory.create(sketch, it.toRequestContext(sketch), fetchResult)
        }.apply {
            Assert.assertNull(this)
        }
    }

    @Test
    fun testDecodeDrawable() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) return

        val context = InstrumentationRegistry.getInstrumentation().context
        val sketch = context.sketch
        val factory = HeifAnimatedDecoder.Factory()

        val request = ImageRequest(context, AssetImages.animHeif.uri) {
            colorSpace(ColorSpace.get(ColorSpace.Named.SRGB))
            onAnimationEnd { }
            onAnimationStart { }
        }
        val fetchResult = sketch.components.newFetcherOrThrow(request)
            .let { runBlocking { it.fetch() }.getOrThrow() }
        factory.create(sketch, request.toRequestContext(sketch), fetchResult)!!
            .let { runBlocking { it.decode() }.getOrThrow() }.apply {
                Assert.assertEquals(ImageInfo(256, 144, "image/heif", 0), this.imageInfo)
                Assert.assertEquals(Size(256, 144), image.getDrawableOrThrow().intrinsicSize)
                Assert.assertEquals(LOCAL, this.dataFrom)
                Assert.assertNull(this.transformedList)
                val animatedImageDrawable =
                    ((image.getDrawableOrThrow() as SketchAnimatableDrawable).drawable as com.github.panpf.sketch.drawable.ScaledAnimatedImageDrawable).drawable
                Assert.assertEquals(-1, animatedImageDrawable.repeatCount)
            }

        val request1 = ImageRequest(context, AssetImages.animHeif.uri) {
            repeatCount(3)
            size(100, 100)
        }
        val fetchResult1 = sketch.components.newFetcherOrThrow(request1)
            .let { runBlocking { it.fetch() }.getOrThrow() }
        factory.create(sketch, request1.toRequestContext(sketch), fetchResult1)!!
            .let { runBlocking { it.decode().getOrThrow() } }.apply {
                Assert.assertEquals(ImageInfo(256, 144, "image/heif", 0), this.imageInfo)
                Assert.assertEquals(Size(128, 72), image.getDrawableOrThrow().intrinsicSize)
                Assert.assertEquals(LOCAL, this.dataFrom)
                Assert.assertEquals(listOf(createInSampledTransformed(2)), this.transformedList)
                val animatedImageDrawable =
                    ((image.getDrawableOrThrow() as SketchAnimatableDrawable).drawable as com.github.panpf.sketch.drawable.ScaledAnimatedImageDrawable).drawable
                Assert.assertEquals(3, animatedImageDrawable.repeatCount)
            }
    }

    @Test
    fun testFactoryEqualsAndHashCode() {
        val element1 = HeifAnimatedDecoder.Factory()
        val element11 = HeifAnimatedDecoder.Factory()
        val element2 = HeifAnimatedDecoder.Factory()

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