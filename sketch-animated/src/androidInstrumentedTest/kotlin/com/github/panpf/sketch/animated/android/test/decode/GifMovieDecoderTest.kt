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

import android.os.Build
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.github.panpf.sketch.ComponentRegistry
import com.github.panpf.sketch.animated.android.test.internal.TranslucentAnimatedTransformation
import com.github.panpf.sketch.decode.GifMovieDecoder
import com.github.panpf.sketch.decode.ImageInfo
import com.github.panpf.sketch.decode.supportMovieGif
import com.github.panpf.sketch.drawable.AnimatableDrawable
import com.github.panpf.sketch.drawable.MovieDrawable
import com.github.panpf.sketch.fetch.FetchResult
import com.github.panpf.sketch.images.ResourceImages
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.animatedTransformation
import com.github.panpf.sketch.request.onAnimationEnd
import com.github.panpf.sketch.request.onAnimationStart
import com.github.panpf.sketch.request.repeatCount
import com.github.panpf.sketch.source.AssetDataSource
import com.github.panpf.sketch.source.DataFrom.LOCAL
import com.github.panpf.sketch.test.singleton.sketch
import com.github.panpf.sketch.test.utils.getDrawableOrThrow
import com.github.panpf.sketch.test.utils.toRequestContext
import com.github.panpf.sketch.util.Size
import kotlinx.coroutines.test.runTest
import org.junit.runner.RunWith
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNotSame
import kotlin.test.assertNull

@RunWith(AndroidJUnit4::class)
class GifMovieDecoderTest {

    @Test
    fun testSupportMovieGif() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) return

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

            supportMovieGif()
            build().apply {
                assertEquals(
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
                assertEquals(
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
    fun testFactory() = runTest {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) return@runTest

        val context = InstrumentationRegistry.getInstrumentation().context
        val sketch = context.sketch
        val factory = GifMovieDecoder.Factory()

        assertEquals("GifMovieDecoder", factory.toString())

        // normal
        ImageRequest(context, ResourceImages.animGif.uri).let {
            val fetchResult =
                FetchResult(
                    AssetDataSource(context, ResourceImages.animGif.resourceName),
                    "image/gif"
                )
            assertNotNull(factory.create(it.toRequestContext(sketch), fetchResult))
        }.apply {
            assertNotNull(this)
        }

        // no mimeType
        ImageRequest(context, ResourceImages.animGif.uri).let {
            val fetchResult =
                FetchResult(AssetDataSource(context, ResourceImages.animGif.resourceName), null)
            factory.create(it.toRequestContext(sketch), fetchResult)
        }.apply {
            assertNotNull(this)
        }

        // Disguised mimeType
        ImageRequest(context, ResourceImages.animGif.uri).let {
            val fetchResult = FetchResult(
                AssetDataSource(context, ResourceImages.animGif.resourceName),
                "image/jpeg",
            )
            factory.create(it.toRequestContext(sketch), fetchResult)
        }.apply {
            assertNotNull(this)
        }

        // disallowAnimatedImage true
        ImageRequest(context, ResourceImages.animGif.uri) {
            disallowAnimatedImage()
        }.let {
            val fetchResult =
                FetchResult(AssetDataSource(context, ResourceImages.animGif.resourceName), null)
            factory.create(it.toRequestContext(sketch), fetchResult)
        }.apply {
            assertNull(this)
        }

        // data error
        ImageRequest(context, ResourceImages.png.uri).let {
            val fetchResult =
                FetchResult(AssetDataSource(context, ResourceImages.png.resourceName), null)
            factory.create(it.toRequestContext(sketch), fetchResult)
        }.apply {
            assertNull(this)
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
            assertNull(this)
        }
    }

    @Test
    fun testFactoryEqualsAndHashCode() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) return

        val element1 = GifMovieDecoder.Factory()
        val element11 = GifMovieDecoder.Factory()
        val element2 = GifMovieDecoder.Factory()

        assertNotSame(element1, element11)
        assertNotSame(element1, element2)
        assertNotSame(element2, element11)

        assertEquals(element1, element1)
        assertEquals(element1, element11)
        assertEquals(element1, element2)
        assertEquals(element2, element11)
        assertNotEquals(element1, null as Any?)
        assertNotEquals(element1, Any())

        assertEquals(element1.hashCode(), element1.hashCode())
        assertEquals(element1.hashCode(), element11.hashCode())
        assertEquals(element1.hashCode(), element2.hashCode())
        assertEquals(element2.hashCode(), element11.hashCode())
    }

    // TODO test: decodeImageInfo

    @Test
    fun testDecodeDrawable() = runTest {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) return@runTest

        val context = InstrumentationRegistry.getInstrumentation().context
        val sketch = context.sketch
        val factory = GifMovieDecoder.Factory()

        ImageRequest(context, ResourceImages.animGif.uri) {
            onAnimationStart { }
        }.apply {
            val fetchResult = sketch.components.newFetcherOrThrow(
                this.toRequestContext(sketch, Size.Empty)
            ).fetch().getOrThrow()
            factory.create(this@apply.toRequestContext(sketch), fetchResult)!!
                .decode().apply {
                    assertEquals(ImageInfo(480, 480, "image/gif"), this.imageInfo)
                    assertEquals(480, image.getDrawableOrThrow().intrinsicWidth)
                    assertEquals(480, image.getDrawableOrThrow().intrinsicHeight)
                    assertEquals(LOCAL, this.dataFrom)
                    assertNull(this.transformeds)
                    val movieDrawable =
                        (image.getDrawableOrThrow() as AnimatableDrawable).drawable as MovieDrawable
                    assertEquals(-1, movieDrawable.getRepeatCount())
                    assertNull(movieDrawable.getAnimatedTransformation())
                }
        }

        ImageRequest(context, ResourceImages.animGif.uri) {
            repeatCount(3)
            animatedTransformation(TranslucentAnimatedTransformation)
            onAnimationEnd { }
            resize(300, 300)
        }.apply {
            val fetchResult1 = sketch.components.newFetcherOrThrow(
                this.toRequestContext(sketch, Size.Empty)
            ).fetch().getOrThrow()
            factory.create(this@apply.toRequestContext(sketch), fetchResult1)!!
                .decode()
                .apply {
                    assertEquals(ImageInfo(480, 480, "image/gif"), this.imageInfo)
                    assertEquals(480, image.getDrawableOrThrow().intrinsicWidth)
                    assertEquals(480, image.getDrawableOrThrow().intrinsicHeight)
                    assertEquals(LOCAL, this.dataFrom)
                    assertNull(this.transformeds)
                    val movieDrawable =
                        (image.getDrawableOrThrow() as AnimatableDrawable).drawable as MovieDrawable
                    assertEquals(3, movieDrawable.getRepeatCount())
                    assertNotNull(movieDrawable.getAnimatedTransformation())
                }
        }
    }
}