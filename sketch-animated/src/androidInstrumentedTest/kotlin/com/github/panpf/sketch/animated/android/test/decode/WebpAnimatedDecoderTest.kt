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
import com.github.panpf.sketch.ComponentRegistry
import com.github.panpf.sketch.decode.ImageInfo
import com.github.panpf.sketch.decode.WebpAnimatedDecoder
import com.github.panpf.sketch.decode.internal.createInSampledTransformed
import com.github.panpf.sketch.decode.supportAnimatedWebp
import com.github.panpf.sketch.drawable.AnimatableDrawable
import com.github.panpf.sketch.images.ResourceImages
import com.github.panpf.sketch.images.toDataSource
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.colorSpace
import com.github.panpf.sketch.request.onAnimationEnd
import com.github.panpf.sketch.request.onAnimationStart
import com.github.panpf.sketch.request.repeatCount
import com.github.panpf.sketch.size
import com.github.panpf.sketch.source.DataFrom.LOCAL
import com.github.panpf.sketch.test.singleton.getTestContextAndSketch
import com.github.panpf.sketch.test.utils.asOrThrow
import com.github.panpf.sketch.test.utils.createDecoderOrDefault
import com.github.panpf.sketch.test.utils.createDecoderOrNull
import com.github.panpf.sketch.test.utils.decode
import com.github.panpf.sketch.test.utils.getDrawableOrThrow
import com.github.panpf.sketch.test.utils.toRequestContext
import com.github.panpf.sketch.util.Size
import kotlinx.coroutines.test.runTest
import org.junit.runner.RunWith
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

@RunWith(AndroidJUnit4::class)
class WebpAnimatedDecoderTest {

    @Test
    fun testSupportAnimatedWebp() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) return

        ComponentRegistry().apply {
            assertEquals(
                expected = "ComponentRegistry(" +
                        "fetcherFactoryList=[]," +
                        "decoderFactoryList=[]," +
                        "requestInterceptorList=[]," +
                        "decodeInterceptorList=[]" +
                        ")",
                actual = toString()
            )
        }

        ComponentRegistry {
            supportAnimatedWebp()
        }.apply {
            assertEquals(
                expected = "ComponentRegistry(" +
                        "fetcherFactoryList=[]," +
                        "decoderFactoryList=[WebpAnimatedDecoder]," +
                        "requestInterceptorList=[]," +
                        "decodeInterceptorList=[]" +
                        ")",
                actual = toString()
            )
        }

        ComponentRegistry {
            supportAnimatedWebp()
            supportAnimatedWebp()
        }.apply {
            assertEquals(
                expected = "ComponentRegistry(" +
                        "fetcherFactoryList=[]," +
                        "decoderFactoryList=[WebpAnimatedDecoder,WebpAnimatedDecoder]," +
                        "requestInterceptorList=[]," +
                        "decodeInterceptorList=[]" +
                        ")",
                actual = toString()
            )
        }
    }

    @Test
    fun testImageInfo() = runTest {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) return@runTest

        val (context, sketch) = getTestContextAndSketch()
        val factory = WebpAnimatedDecoder.Factory()

        ImageRequest(context, ResourceImages.animWebp.uri)
            .createDecoderOrDefault(sketch, factory)
            .apply {
                assertEquals(
                    expected = ImageInfo(480, 270, "image/webp"),
                    actual = imageInfo
                )
            }
    }

    @Test
    fun testDecode() = runTest {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) return@runTest

        val (context, sketch) = getTestContextAndSketch()
        val factory = WebpAnimatedDecoder.Factory()

        ImageRequest(context, ResourceImages.animWebp.uri) {
            colorSpace(ColorSpace.Named.SRGB)
            onAnimationEnd { }
            onAnimationStart { }
        }.decode(sketch, factory).apply {
            assertEquals(expected = ImageInfo(480, 270, "image/webp"), actual = this.imageInfo)
            assertEquals(expected = Size(480, 270), actual = image.size)
            assertEquals(expected = LOCAL, actual = this.dataFrom)
            assertEquals(expected = null, actual = this.transformeds)
            val animatedImageDrawable = image.getDrawableOrThrow()
                .asOrThrow<AnimatableDrawable>().drawable!!
                .asOrThrow<com.github.panpf.sketch.drawable.ScaledAnimatedImageDrawable>()
                .drawable
            assertEquals(expected = -1, actual = animatedImageDrawable.repeatCount)
        }

        ImageRequest(context, ResourceImages.animWebp.uri) {
            repeatCount(3)
            size(300, 300)
        }.decode(sketch, factory).apply {
            assertEquals(expected = ImageInfo(480, 270, "image/webp"), actual = this.imageInfo)
            assertEquals(expected = Size(240, 135), actual = image.size)
            assertEquals(expected = LOCAL, actual = this.dataFrom)
            assertEquals(
                expected = listOf(createInSampledTransformed(2)),
                actual = this.transformeds
            )
            val animatedImageDrawable = image.getDrawableOrThrow()
                .asOrThrow<AnimatableDrawable>().drawable!!
                .asOrThrow<com.github.panpf.sketch.drawable.ScaledAnimatedImageDrawable>()
                .drawable
            assertEquals(expected = 3, actual = animatedImageDrawable.repeatCount)
        }
    }

    @Test
    fun testEqualsAndHashCode() = runTest {
        val (context, sketch) = getTestContextAndSketch()
        val request = ImageRequest(context, ResourceImages.animWebp.uri)
        val requestContext = request.toRequestContext(sketch)
        val dataSource = ResourceImages.animWebp.toDataSource(context)
        val element1 = WebpAnimatedDecoder(requestContext, dataSource)
        val element11 = WebpAnimatedDecoder(requestContext, dataSource)

        assertNotEquals(illegal = element1, actual = element11)
        assertNotEquals(illegal = element1, actual = null as Any?)
        assertNotEquals(illegal = element1, actual = Any())
        assertNotEquals(illegal = element1.hashCode(), actual = element11.hashCode())
    }

    @Test
    fun testToString() = runTest {
        val (context, sketch) = getTestContextAndSketch()
        val request = ImageRequest(context, ResourceImages.animWebp.uri)
        val requestContext = request.toRequestContext(sketch)
        val dataSource = ResourceImages.animWebp.toDataSource(context)
        val decoder = WebpAnimatedDecoder(requestContext, dataSource)
        assertTrue(actual = decoder.toString().contains("WebpAnimatedDecoder"))
        assertTrue(actual = decoder.toString().contains("@"))
    }

    @Test
    fun testFactoryKey() = runTest {
        assertEquals(
            expected = "WebpAnimatedDecoder",
            actual = WebpAnimatedDecoder.Factory().key
        )
    }

    @Test
    fun testFactoryCreate() = runTest {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) return@runTest

        val (context, sketch) = getTestContextAndSketch()
        val factory = WebpAnimatedDecoder.Factory()

        // normal
        ImageRequest(context, ResourceImages.animWebp.uri)
            .createDecoderOrNull(sketch, factory) {
                it.copy(mimeType = "image/webp")
            }.apply {
                assertTrue(this is WebpAnimatedDecoder)
            }

        ImageRequest(context, ResourceImages.animWebp.uri)
            .createDecoderOrNull(sketch, factory) {
                it.copy(mimeType = null)
            }.apply {
                assertTrue(this is WebpAnimatedDecoder)
            }

        // disallowAnimatedImage true
        ImageRequest(context, ResourceImages.animWebp.uri) {
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

        ImageRequest(context, ResourceImages.animGif.uri)
            .createDecoderOrNull(sketch, factory) {
                it.copy(mimeType = "image/webp")
            }.apply {
                assertNull(this)
            }

        // mimeType error
        ImageRequest(context, ResourceImages.animWebp.uri)
            .createDecoderOrNull(sketch, factory) {
                it.copy(mimeType = "image/jpeg")
            }.apply {
                assertTrue(this is WebpAnimatedDecoder)
            }

        // Disguised, mimeType; data error
        ImageRequest(context, ResourceImages.png.uri)
            .createDecoderOrNull(sketch, factory) {
                it.copy(mimeType = "image/webp")
            }.apply {
                assertNull(this)
            }
    }

    @Test
    fun testFactoryEqualsAndHashCode() {
        val element1 = WebpAnimatedDecoder.Factory()
        val element11 = WebpAnimatedDecoder.Factory()

        assertEquals(expected = element1, actual = element11)
        assertNotEquals(illegal = element1, actual = null as Any?)
        assertNotEquals(illegal = element1, actual = Any())

        assertEquals(expected = element1.hashCode(), actual = element11.hashCode())
    }

    @Test
    fun testFactoryToString() = runTest {
        assertEquals(
            expected = "WebpAnimatedDecoder",
            actual = WebpAnimatedDecoder.Factory().toString()
        )
    }
}