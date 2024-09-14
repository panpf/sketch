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

package com.github.panpf.sketch.animated.koralgif.test.decode

import android.graphics.Canvas
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.github.panpf.sketch.ComponentRegistry
import com.github.panpf.sketch.decode.GifDrawableDecoder
import com.github.panpf.sketch.decode.ImageInfo
import com.github.panpf.sketch.decode.internal.createInSampledTransformed
import com.github.panpf.sketch.decode.supportKoralGif
import com.github.panpf.sketch.drawable.AnimatableDrawable
import com.github.panpf.sketch.drawable.GifDrawableWrapperDrawable
import com.github.panpf.sketch.fetch.FetchResult
import com.github.panpf.sketch.fetch.copy
import com.github.panpf.sketch.images.ResourceImages
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.animatedTransformation
import com.github.panpf.sketch.request.onAnimationEnd
import com.github.panpf.sketch.request.onAnimationStart
import com.github.panpf.sketch.request.repeatCount
import com.github.panpf.sketch.source.AssetDataSource
import com.github.panpf.sketch.source.DataFrom.LOCAL
import com.github.panpf.sketch.test.singleton.sketch
import com.github.panpf.sketch.test.utils.decode
import com.github.panpf.sketch.test.utils.fetch
import com.github.panpf.sketch.test.utils.getDrawableOrThrow
import com.github.panpf.sketch.test.utils.toRequestContext
import com.github.panpf.sketch.transform.AnimatedTransformation
import com.github.panpf.sketch.transform.PixelOpacity
import kotlinx.coroutines.test.runTest
import org.junit.runner.RunWith
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNotSame
import kotlin.test.assertNull

@RunWith(AndroidJUnit4::class)
class GifDrawableDecoderTest {

    @Test
    fun testSupportKoralGif() {
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

            supportKoralGif()
            build().apply {
                assertEquals(
                    "ComponentRegistry(" +
                            "fetcherFactoryList=[]," +
                            "decoderFactoryList=[GifDrawableDecoder]," +
                            "requestInterceptorList=[]," +
                            "decodeInterceptorList=[]" +
                            ")",
                    toString()
                )
            }

            supportKoralGif()
            build().apply {
                assertEquals(
                    "ComponentRegistry(" +
                            "fetcherFactoryList=[]," +
                            "decoderFactoryList=[GifDrawableDecoder,GifDrawableDecoder]," +
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
        val context = InstrumentationRegistry.getInstrumentation().context
        val sketch = context.sketch
        val factory = GifDrawableDecoder.Factory()

        assertEquals("GifDrawableDecoder", factory.toString())

        // normal
        ImageRequest(context, ResourceImages.animGif.uri).let {
            val fetchResult = it.fetch(sketch)
            factory.create(it.toRequestContext(sketch), fetchResult)
        }.apply {
            assertNotNull(this)
        }

        // no mimeType
        ImageRequest(context, ResourceImages.animGif.uri).let {
            val fetchResult = it.fetch(sketch).copy(mimeType = null)
            factory.create(it.toRequestContext(sketch), fetchResult)
        }.apply {
            assertNotNull(this)
        }

        // Disguised mimeType
        ImageRequest(context, ResourceImages.animGif.uri).let {
            val fetchResult = it.fetch(sketch).copy(mimeType = "image/jpeg")
            factory.create(it.toRequestContext(sketch), fetchResult)
        }.apply {
            assertNotNull(this)
        }

        // disallowAnimatedImage true
        ImageRequest(context, ResourceImages.animGif.uri) {
            disallowAnimatedImage()
        }.let {
            val fetchResult = it.fetch(sketch)
            factory.create(it.toRequestContext(sketch), fetchResult)
        }.apply {
            assertNull(this)
        }

        // data error
        ImageRequest(context, ResourceImages.png.uri).let {
            val fetchResult = it.fetch(sketch)
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
        val element1 = GifDrawableDecoder.Factory()
        val element11 = GifDrawableDecoder.Factory()
        val element2 = GifDrawableDecoder.Factory()

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
    fun testDecode() = runTest {
        val context = InstrumentationRegistry.getInstrumentation().context
        val sketch = context.sketch
        val factory = GifDrawableDecoder.Factory()

        ImageRequest(context, ResourceImages.animGif.uri) {
            onAnimationStart { }
        }.decode(sketch, factory).apply {
            assertEquals(ImageInfo(480, 480, "image/gif"), this.imageInfo)
            assertEquals(480, image.getDrawableOrThrow().intrinsicWidth)
            assertEquals(480, image.getDrawableOrThrow().intrinsicHeight)
            assertEquals(LOCAL, this.dataFrom)
            assertNull(this.transformeds)
            val gifDrawable =
                ((image.getDrawableOrThrow() as AnimatableDrawable).drawable as GifDrawableWrapperDrawable).gifDrawable
            assertEquals(0, gifDrawable.loopCount)
            assertNull(gifDrawable.transform)
        }

        ImageRequest(context, ResourceImages.animGif.uri) {
            repeatCount(3)
            animatedTransformation(TranslucentAnimatedTransformation)
            onAnimationEnd {}
            resize(300, 300)
        }.decode(sketch, factory).apply {
            assertEquals(ImageInfo(480, 480, "image/gif"), this.imageInfo)
            assertEquals(240, image.getDrawableOrThrow().intrinsicWidth)
            assertEquals(240, image.getDrawableOrThrow().intrinsicHeight)
            assertEquals(LOCAL, this.dataFrom)
            assertEquals(listOf(createInSampledTransformed(2)), this.transformeds)
            val gifDrawable =
                ((image.getDrawableOrThrow() as AnimatableDrawable).drawable as GifDrawableWrapperDrawable).gifDrawable
            assertEquals(3, gifDrawable.loopCount)
            assertNotNull(gifDrawable.transform)
            gifDrawable.transform!!.onDraw(Canvas(), null, null)
        }
    }

    object TranslucentAnimatedTransformation : AnimatedTransformation {
        override val key: String = "TranslucentAnimatedTransformation"

        override fun transform(canvas: Canvas): PixelOpacity {
            return PixelOpacity.TRANSLUCENT
        }
    }
}