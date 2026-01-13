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

package com.github.panpf.sketch.animated.gif.koral.test.decode

import android.graphics.Canvas
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.ComponentRegistry
import com.github.panpf.sketch.decode.ImageInfo
import com.github.panpf.sketch.decode.KoralGifDecoder
import com.github.panpf.sketch.decode.internal.createInSampledTransformed
import com.github.panpf.sketch.decode.supportKoralGif
import com.github.panpf.sketch.drawable.AnimatableDrawable
import com.github.panpf.sketch.drawable.GifDrawableWrapperDrawable
import com.github.panpf.sketch.images.ResourceImages
import com.github.panpf.sketch.images.toDataSource
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.animatedTransformation
import com.github.panpf.sketch.request.disallowAnimatedImage
import com.github.panpf.sketch.request.onAnimationEnd
import com.github.panpf.sketch.request.onAnimationStart
import com.github.panpf.sketch.request.repeatCount
import com.github.panpf.sketch.size
import com.github.panpf.sketch.source.DataFrom.LOCAL
import com.github.panpf.sketch.test.singleton.getTestContextAndSketch
import com.github.panpf.sketch.test.utils.createDecoderOrDefault
import com.github.panpf.sketch.test.utils.createDecoderOrNull
import com.github.panpf.sketch.test.utils.decode
import com.github.panpf.sketch.test.utils.getDrawableOrThrow
import com.github.panpf.sketch.test.utils.toRequestContext
import com.github.panpf.sketch.transform.AnimatedTransformation
import com.github.panpf.sketch.transform.PixelOpacity
import com.github.panpf.sketch.util.Rect
import com.github.panpf.sketch.util.Size
import kotlinx.coroutines.test.runTest
import org.junit.runner.RunWith
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

@RunWith(AndroidJUnit4::class)
class KoralGifDecoderTest {

    @Test
    fun testSupportKoralGif() {
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
            supportKoralGif()
        }.apply {
            assertEquals(
                expected = "ComponentRegistry(" +
                        "fetchers=[]," +
                        "decoders=[KoralGifDecoder]," +
                        "interceptors=[]" +
                        ")",
                actual = toString()
            )
        }

        ComponentRegistry {
            supportKoralGif()
            supportKoralGif()
        }.apply {
            assertEquals(
                expected = "ComponentRegistry(" +
                        "fetchers=[]," +
                        "decoders=[KoralGifDecoder,KoralGifDecoder]," +
                        "interceptors=[]" +
                        ")",
                actual = toString()
            )
        }
    }

    @Test
    fun testConstructor() = runTest {
        val (context, sketch) = getTestContextAndSketch()

        val request = ImageRequest(context, ResourceImages.animGif.uri)
        val requestContext = request.toRequestContext(sketch)
        val dataSource = ResourceImages.animGif.toDataSource(context)

        KoralGifDecoder(requestContext, dataSource)
        KoralGifDecoder(requestContext = requestContext, dataSource = dataSource)
    }

    @Test
    fun testImageInfo() = runTest {
        val (context, sketch) = getTestContextAndSketch()
        val factory = KoralGifDecoder.Factory()

        ImageRequest(context, ResourceImages.animGif.uri)
            .createDecoderOrDefault(sketch, factory)
            .apply {
                assertEquals(
                    expected = ImageInfo(480, 480, "image/gif"),
                    actual = imageInfo
                )
            }
    }

    @Test
    fun testDecode() = runTest {
        val (context, sketch) = getTestContextAndSketch()
        val factory = KoralGifDecoder.Factory()

        ImageRequest(context, ResourceImages.animGif.uri) {
            onAnimationStart { }
        }.decode(sketch, factory).apply {
            assertEquals(expected = ImageInfo(480, 480, "image/gif"), actual = this.imageInfo)
            assertEquals(expected = Size(480, 480), actual = image.size)
            assertEquals(expected = LOCAL, actual = this.dataFrom)
            assertEquals(expected = null, actual = this.transformeds)
            val gifDrawable =
                ((image.getDrawableOrThrow() as AnimatableDrawable).drawable as GifDrawableWrapperDrawable).gifDrawable
            assertEquals(expected = 0, actual = gifDrawable.loopCount)
            assertNull(actual = gifDrawable.transform)
        }

        ImageRequest(context, ResourceImages.animGif.uri) {
            repeatCount(3)
            animatedTransformation(TranslucentAnimatedTransformation)
            onAnimationEnd {}
            resize(300, 300)
        }.decode(sketch, factory).apply {
            assertEquals(expected = ImageInfo(480, 480, "image/gif"), actual = this.imageInfo)
            assertEquals(expected = Size(240, 240), actual = image.size)
            assertEquals(expected = LOCAL, actual = this.dataFrom)
            assertEquals(
                expected = listOf(createInSampledTransformed(2)),
                actual = this.transformeds
            )
            val gifDrawable =
                ((image.getDrawableOrThrow() as AnimatableDrawable).drawable as GifDrawableWrapperDrawable).gifDrawable
            assertEquals(expected = 4, actual = gifDrawable.loopCount)
            assertNotNull(actual = gifDrawable.transform)
            gifDrawable.transform!!.onDraw(Canvas(), null, null)
        }
    }

    @Test
    fun testEqualsAndHashCode() = runTest {
        val (context, sketch) = getTestContextAndSketch()
        val request = ImageRequest(context, ResourceImages.animGif.uri)
        val requestContext = request.toRequestContext(sketch)
        val dataSource = ResourceImages.animGif.toDataSource(context)
        val element1 = KoralGifDecoder(requestContext, dataSource)
        val element11 = KoralGifDecoder(requestContext, dataSource)

        assertNotEquals(illegal = element1, actual = element11)
        assertNotEquals(illegal = element1, actual = null as Any?)
        assertNotEquals(illegal = element1, actual = Any())
        assertNotEquals(illegal = element1.hashCode(), actual = element11.hashCode())
    }

    @Test
    fun testToString() = runTest {
        val (context, sketch) = getTestContextAndSketch()
        val request = ImageRequest(context, ResourceImages.animGif.uri)
        val requestContext = request.toRequestContext(sketch)
        val dataSource = ResourceImages.animGif.toDataSource(context)
        val decoder = KoralGifDecoder(requestContext, dataSource)
        assertTrue(actual = decoder.toString().contains("KoralGifDecoder"))
        assertTrue(actual = decoder.toString().contains("@"))
    }

    @Test
    fun testFactoryConstructor() {
        KoralGifDecoder.Factory()
    }

    @Test
    fun testFactoryKey() {
        assertEquals(
            expected = "KoralGifDecoder",
            actual = KoralGifDecoder.Factory().key
        )
    }

    @Test
    fun testFactoryCreate() = runTest {
        val (context, sketch) = getTestContextAndSketch()
        val factory = KoralGifDecoder.Factory()

        // normal
        ImageRequest(context, ResourceImages.animGif.uri)
            .createDecoderOrNull(sketch, factory) {
                it.copy(mimeType = "image/gif")
            }.apply {
                assertTrue(this is KoralGifDecoder)
            }

        // no mimeType
        ImageRequest(context, ResourceImages.animGif.uri)
            .createDecoderOrNull(sketch, factory) {
                it.copy(mimeType = null)
            }.apply {
                assertTrue(this is KoralGifDecoder)
            }

        // Disguised mimeType
        ImageRequest(context, ResourceImages.animGif.uri)
            .createDecoderOrNull(sketch, factory) {
                it.copy(mimeType = "image/jpeg")
            }.apply {
                assertTrue(this is KoralGifDecoder)
            }

        // disallowAnimatedImage true
        ImageRequest(context, ResourceImages.animGif.uri) {
            disallowAnimatedImage()
        }.createDecoderOrNull(sketch, factory) {
            it.copy(mimeType = null)
        }.apply {
            assertNull(this)
        }

        // data error
        ImageRequest(context, ResourceImages.png.uri)
            .createDecoderOrNull(sketch, factory) {
                it.copy(mimeType = null)
            }.apply {
                assertNull(this)
            }

        // Disguised, mimeType; data error
        ImageRequest(context, ResourceImages.png.uri)
            .createDecoderOrNull(sketch, factory) {
                it.copy(mimeType = "image/gif")
            }.apply {
                assertNull(this)
            }
    }

    @Test
    fun testFactoryEqualsAndHashCode() {
        val element1 = KoralGifDecoder.Factory()
        val element11 = KoralGifDecoder.Factory()

        assertEquals(expected = element1, actual = element11)
        assertNotEquals(illegal = element1, actual = null as Any?)
        assertNotEquals(illegal = element1, actual = Any())

        assertEquals(expected = element1.hashCode(), actual = element11.hashCode())
    }

    @Test
    fun testFactoryToString() = runTest {
        assertEquals(
            expected = "KoralGifDecoder",
            actual = KoralGifDecoder.Factory().toString()
        )
    }

    private data object TranslucentAnimatedTransformation : AnimatedTransformation {
        override val key: String = "TranslucentAnimatedTransformation"

        override fun transform(canvas: Any, bounds: Rect): PixelOpacity {
            return PixelOpacity.TRANSLUCENT
        }
    }
}