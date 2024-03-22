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

import android.os.Build
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.github.panpf.sketch.ComponentRegistry
import com.github.panpf.sketch.datasource.AssetDataSource
import com.github.panpf.sketch.datasource.DataFrom.LOCAL
import com.github.panpf.sketch.decode.GifMovieDecoder
import com.github.panpf.sketch.decode.ImageInfo
import com.github.panpf.sketch.decode.supportMovieGif
import com.github.panpf.sketch.drawable.MovieDrawable
import com.github.panpf.sketch.drawable.AnimatableDrawable
import com.github.panpf.sketch.fetch.FetchResult
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.animatedTransformation
import com.github.panpf.sketch.getDrawableOrThrow
import com.github.panpf.sketch.request.onAnimationEnd
import com.github.panpf.sketch.request.onAnimationStart
import com.github.panpf.sketch.request.repeatCount
import com.github.panpf.sketch.resources.AssetImages
import com.github.panpf.sketch.test.singleton.sketch
import com.github.panpf.sketch.test.utils.toRequestContext
import com.github.panpf.sketch.transform.PixelOpacity
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class GifMovieDecoderTest {

    @Test
    fun testSupportApkIcon() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) return

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

            supportMovieGif()
            build().apply {
                Assert.assertEquals(
                    "ComponentRegistry(" +
                            "fetcherFactoryList=[]," +
                            "decoderFactoryList=[GifMovieDecoder]," +
                            "requestInterceptorList=[]," +
                            "decodeInterceptorList=[]" +
                            ")",
                    toString()
                )
            }

            supportMovieGif()
            build().apply {
                Assert.assertEquals(
                    "ComponentRegistry(" +
                            "fetcherFactoryList=[]," +
                            "decoderFactoryList=[GifMovieDecoder,GifMovieDecoder]," +
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
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) return

        val context = InstrumentationRegistry.getInstrumentation().context
        val sketch = context.sketch
        val factory = GifMovieDecoder.Factory()

        Assert.assertEquals("GifMovieDecoder", factory.toString())

        // normal
        ImageRequest(context, AssetImages.animGif.uri).let {
            val fetchResult =
                FetchResult(AssetDataSource(sketch, it, AssetImages.animGif.fileName), "image/gif")
            Assert.assertNotNull(
                factory.create(
                    sketch,
                    it.toRequestContext(sketch),
                    fetchResult
                )
            )
        }.apply {
            Assert.assertNotNull(this)
        }

        ImageRequest(context, AssetImages.animGif.uri).let {
            val fetchResult =
                FetchResult(AssetDataSource(sketch, it, AssetImages.animGif.fileName), null)
            factory.create(
                sketch,
                it.toRequestContext(sketch),
                fetchResult
            )
        }.apply {
            Assert.assertNotNull(this)
        }

        // disallowAnimatedImage true
        ImageRequest(context, AssetImages.animGif.uri) {
            disallowAnimatedImage()
        }.let {
            val fetchResult =
                FetchResult(AssetDataSource(sketch, it, AssetImages.animGif.fileName), null)
            factory.create(
                sketch,
                it.toRequestContext(sketch),
                fetchResult
            )
        }.apply {
            Assert.assertNull(this)
        }

        // data error
        ImageRequest(context, AssetImages.png.uri).let {
            val fetchResult =
                FetchResult(AssetDataSource(sketch, it, AssetImages.png.fileName), null)
            factory.create(
                sketch,
                it.toRequestContext(sketch),
                fetchResult
            )
        }.apply {
            Assert.assertNull(this)
        }

        // mimeType error
        ImageRequest(context, AssetImages.animGif.uri).let {
            val fetchResult = FetchResult(
                AssetDataSource(sketch, it, AssetImages.animGif.fileName),
                "image/jpeg",
            )
            factory.create(
                sketch,
                it.toRequestContext(sketch),
                fetchResult
            )
        }.apply {
            Assert.assertNull(this)
        }
    }

    @Test
    fun testFactoryEqualsAndHashCode() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) return

        val element1 = GifMovieDecoder.Factory()
        val element11 = GifMovieDecoder.Factory()
        val element2 = GifMovieDecoder.Factory()

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

    @Test
    fun testDecodeDrawable() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) return

        val context = InstrumentationRegistry.getInstrumentation().context
        val sketch = context.sketch
        val factory = GifMovieDecoder.Factory()

        ImageRequest(context, AssetImages.animGif.uri) {
            onAnimationStart { }
            disallowReuseBitmap()
        }.apply {
            val fetchResult = sketch.components.newFetcherOrThrow(this)
                .let { runBlocking { it.fetch() }.getOrThrow() }
            factory.create(
                sketch,
                this@apply.toRequestContext(sketch),
                fetchResult
            )!!.let { runBlocking { it.decode() }.getOrThrow() }.apply {
                Assert.assertEquals(ImageInfo(480, 480, "image/gif", 0), this.imageInfo)
                Assert.assertEquals(480, image.getDrawableOrThrow().intrinsicWidth)
                Assert.assertEquals(480, image.getDrawableOrThrow().intrinsicHeight)
                Assert.assertEquals(LOCAL, this.dataFrom)
                Assert.assertNull(this.transformedList)
                val movieDrawable =
                    (image.getDrawableOrThrow() as AnimatableDrawable).drawable as MovieDrawable
                Assert.assertEquals(-1, movieDrawable.getRepeatCount())
                Assert.assertNull(movieDrawable.getAnimatedTransformation())
            }
        }

        ImageRequest(context, AssetImages.animGif.uri) {
            repeatCount(3)
            animatedTransformation { PixelOpacity.TRANSLUCENT }
            onAnimationEnd { }
            resize(300, 300)
        }.apply {
            val fetchResult1 = sketch.components.newFetcherOrThrow(this)
                .let { runBlocking { it.fetch() }.getOrThrow() }
            factory.create(
                sketch,
                this@apply.toRequestContext(sketch),
                fetchResult1
            )!!.let { runBlocking { it.decode() }.getOrThrow() }.apply {
                Assert.assertEquals(ImageInfo(480, 480, "image/gif", 0), this.imageInfo)
                Assert.assertEquals(480, image.getDrawableOrThrow().intrinsicWidth)
                Assert.assertEquals(480, image.getDrawableOrThrow().intrinsicHeight)
                Assert.assertEquals(LOCAL, this.dataFrom)
                Assert.assertNull(this.transformedList)
                val movieDrawable =
                    (image.getDrawableOrThrow() as AnimatableDrawable).drawable as MovieDrawable
                Assert.assertEquals(3, movieDrawable.getRepeatCount())
                Assert.assertNotNull(movieDrawable.getAnimatedTransformation())
            }
        }
    }
}