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

package com.github.panpf.sketch.video.test.decode

import android.graphics.Bitmap
import android.graphics.Bitmap.Config.RGB_565
import android.media.MediaMetadataRetriever
import android.os.Build
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.github.panpf.sketch.ComponentRegistry
import com.github.panpf.sketch.cache.CachePolicy.DISABLED
import com.github.panpf.sketch.decode.VideoFrameDecoder
import com.github.panpf.sketch.decode.internal.createInSampledTransformed
import com.github.panpf.sketch.decode.internal.createResizeTransformed
import com.github.panpf.sketch.decode.supportVideoFrame
import com.github.panpf.sketch.fetch.copy
import com.github.panpf.sketch.getBitmapOrThrow
import com.github.panpf.sketch.images.ResourceImages
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.bitmapConfig
import com.github.panpf.sketch.request.videoFrameMillis
import com.github.panpf.sketch.request.videoFrameOption
import com.github.panpf.sketch.request.videoFramePercent
import com.github.panpf.sketch.resize.Precision.LESS_PIXELS
import com.github.panpf.sketch.resize.Resize
import com.github.panpf.sketch.source.DataFrom.LOCAL
import com.github.panpf.sketch.test.singleton.sketch
import com.github.panpf.sketch.test.utils.corners
import com.github.panpf.sketch.test.utils.toRequestContext
import com.github.panpf.sketch.util.Size
import com.github.panpf.tools4j.test.ktx.assertThrow
import kotlinx.coroutines.runBlocking
import org.junit.runner.RunWith
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNotSame
import kotlin.test.assertNull

@RunWith(AndroidJUnit4::class)
class VideoFrameDecoderTest {

    @Test
    fun testSupportApkIcon() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O_MR1) return

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

            supportVideoFrame()
            build().apply {
                assertEquals(
                    "ComponentRegistry(" +
                            "fetcherFactoryList=[]," +
                            "decoderFactoryList=[VideoFrameDecoder]," +
                            "requestInterceptorList=[]," +
                            "decodeInterceptorList=[]" +
                            ")",
                    toString()
                )
            }

            supportVideoFrame()
            build().apply {
                assertEquals(
                    "ComponentRegistry(" +
                            "fetcherFactoryList=[]," +
                            "decoderFactoryList=[VideoFrameDecoder,VideoFrameDecoder]," +
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
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O_MR1) return

        val context = InstrumentationRegistry.getInstrumentation().context
        val sketch = context.sketch
        val factory = VideoFrameDecoder.Factory()

        assertEquals("VideoFrameDecoder", factory.toString())

        val mp4Request = ImageRequest(context, ResourceImages.mp4.uri)
        val mp4RequestContext = runBlocking {
            mp4Request.toRequestContext(sketch)
        }
        val mp4FetchResult = runBlocking {
            sketch.components.newFetcherOrThrow(mp4Request.toRequestContext(sketch, Size.Empty))
                .fetch().getOrThrow()
        }.apply {
            assertEquals(
                "FetchResult(source=AssetDataSource('sample.mp4'),mimeType='video/mp4')",
                this@apply.toString()
            )
        }

        val pngRequest = ImageRequest(context, ResourceImages.png.uri)
        val pngRequestContext = runBlocking {
            pngRequest.toRequestContext(sketch)
        }
        val pngFetchResult = runBlocking {
            sketch.components.newFetcherOrThrow(pngRequest.toRequestContext(sketch, Size.Empty))
                .fetch().getOrThrow()
        }.apply {
            assertEquals(
                "FetchResult(source=AssetDataSource('sample.png'),mimeType='image/png')",
                this@apply.toString()
            )
        }

        // normal
        assertNotNull(factory.create(mp4RequestContext, mp4FetchResult))

        // mimeType error
        assertNull(
            factory.create(mp4RequestContext, mp4FetchResult.copy(mimeType = null))
        )
        assertNull(
            factory.create(pngRequestContext, pngFetchResult)
        )

        // data error
        assertNotNull(
            factory.create(
                pngRequestContext,
                pngFetchResult.copy(mimeType = "video/mp4")
            )
        )
    }

    @Test
    fun testFactoryEqualsAndHashCode() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O_MR1) return

        val element1 = VideoFrameDecoder.Factory()
        val element11 = VideoFrameDecoder.Factory()

        assertNotSame(element1, element11)

        assertEquals(element1, element1)
        assertEquals(element1, element11)

        assertNotEquals(element1, Any())
        assertNotEquals(element1, null as Any?)

        assertEquals(element1.hashCode(), element1.hashCode())
        assertEquals(element1.hashCode(), element11.hashCode())
    }

    @Test
    fun testDecode() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O_MR1) return

        val context = InstrumentationRegistry.getInstrumentation().context
        val sketch = context.sketch
        val factory = VideoFrameDecoder.Factory()

        ImageRequest(context, ResourceImages.mp4.uri).run {
            val fetcher =
                sketch.components.newFetcherOrThrow(this.toRequestContext(sketch, Size.Empty))
            val fetchResult = runBlocking { fetcher.fetch() }.getOrThrow()
            runBlocking {
                factory.create(this@run.toRequestContext(sketch), fetchResult)!!.decode()
            }.getOrThrow()
        }.apply {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                assertEquals(
                    "Bitmap(500x250,ARGB_8888)",
                    image.getBitmapOrThrow().toShortInfoString()
                )
            } else {
                assertEquals(
                    "Bitmap(500x250,RGB_565)",
                    image.getBitmapOrThrow().toShortInfoString()
                )
            }
            assertEquals(
                "ImageInfo(500x250,'video/mp4')",
                imageInfo.toShortString()
            )
            assertEquals(LOCAL, dataFrom)
            assertNull(transformeds)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            ImageRequest(context, ResourceImages.mp4.uri) {
                bitmapConfig(RGB_565)
            }.run {
                val fetcher =
                    sketch.components.newFetcherOrThrow(this.toRequestContext(sketch, Size.Empty))
                val fetchResult = runBlocking { fetcher.fetch() }.getOrThrow()
                runBlocking {
                    factory.create(this@run.toRequestContext(sketch), fetchResult)!!.decode()
                }.getOrThrow()
            }.apply {
                assertEquals(
                    "Bitmap(500x250,RGB_565)",
                    image.getBitmapOrThrow().toShortInfoString()
                )
                assertEquals(
                    "ImageInfo(500x250,'video/mp4')",
                    imageInfo.toShortString()
                )
                assertEquals(LOCAL, dataFrom)
                assertNull(transformeds)
            }
        }

        ImageRequest(context, ResourceImages.mp4.uri) {
            resize(300, 300, LESS_PIXELS)
        }.run {
            val fetcher =
                sketch.components.newFetcherOrThrow(this.toRequestContext(sketch, Size.Empty))
            val fetchResult = runBlocking { fetcher.fetch() }.getOrThrow()
            runBlocking {
                factory.create(this@run.toRequestContext(sketch), fetchResult)!!.decode()
            }.getOrThrow()
        }.apply {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                assertEquals(
                    "Bitmap(250x125,ARGB_8888)",
                    image.getBitmapOrThrow().toShortInfoString()
                )
                assertEquals(listOf(createInSampledTransformed(2)), transformeds)
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
                assertEquals(
                    "Bitmap(250x125,RGB_565)",
                    image.getBitmapOrThrow().toShortInfoString()
                )
                assertEquals(listOf(createInSampledTransformed(2)), transformeds)
            } else {
                assertEquals(
                    "Bitmap(250x125,RGB_565)",
                    image.getBitmapOrThrow().toShortInfoString()
                )
                assertEquals(
                    listOf(createResizeTransformed(Resize(300, 300, LESS_PIXELS))),
                    transformeds
                )
            }
            assertEquals(
                "ImageInfo(500x250,'video/mp4')",
                imageInfo.toShortString()
            )
            assertEquals(LOCAL, dataFrom)
        }

        ImageRequest(context, ResourceImages.png.uri).run {
            val fetcher =
                sketch.components.newFetcherOrThrow(this.toRequestContext(sketch, Size.Empty))
            val fetchResult = runBlocking { fetcher.fetch() }.getOrThrow()
            assertThrow(NullPointerException::class) {
                runBlocking {
                    factory.create(this@run.toRequestContext(sketch), fetchResult)!!.decode()
                }.getOrThrow()
            }
        }
    }

    @Test
    fun testDecodeVideoFrameMicros() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O_MR1) return

        val context = InstrumentationRegistry.getInstrumentation().context

        val sketch = context.sketch
        val factory = VideoFrameDecoder.Factory()
        val bitmap1 = ImageRequest(context, ResourceImages.mp4.uri) {
            memoryCachePolicy(DISABLED)
            resultCachePolicy(DISABLED)
            videoFrameOption(MediaMetadataRetriever.OPTION_CLOSEST)
        }.run {
            val fetcher =
                sketch.components.newFetcherOrThrow(this.toRequestContext(sketch, Size.Empty))
            val fetchResult = runBlocking { fetcher.fetch() }.getOrThrow()
            runBlocking {
                factory.create(this@run.toRequestContext(sketch), fetchResult)!!.decode()
            }.getOrThrow()
        }.image.getBitmapOrThrow()
        val bitmap11 = ImageRequest(context, ResourceImages.mp4.uri) {
            memoryCachePolicy(DISABLED)
            resultCachePolicy(DISABLED)
            videoFrameOption(MediaMetadataRetriever.OPTION_CLOSEST)
        }.run {
            val fetcher =
                sketch.components.newFetcherOrThrow(this.toRequestContext(sketch, Size.Empty))
            val fetchResult = runBlocking { fetcher.fetch() }.getOrThrow()
            runBlocking {
                factory.create(this@run.toRequestContext(sketch), fetchResult)!!.decode()
            }.getOrThrow()
        }.image.getBitmapOrThrow()
        val bitmap2 = ImageRequest(context, ResourceImages.mp4.uri) {
            memoryCachePolicy(DISABLED)
            resultCachePolicy(DISABLED)
            videoFrameOption(MediaMetadataRetriever.OPTION_CLOSEST)
            videoFrameMillis(500)
        }.run {
            val fetcher =
                sketch.components.newFetcherOrThrow(this.toRequestContext(sketch, Size.Empty))
            val fetchResult = runBlocking { fetcher.fetch() }.getOrThrow()
            runBlocking {
                factory.create(this@run.toRequestContext(sketch), fetchResult)!!.decode()
            }.getOrThrow()
        }.image.getBitmapOrThrow()
        assertEquals(bitmap1.corners(), bitmap11.corners())
        assertNotEquals(bitmap1.corners(), bitmap2.corners())
    }

    @Test
    fun testDecodeVideoFramePercent() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O_MR1) return

        val context = InstrumentationRegistry.getInstrumentation().context

        val sketch = context.sketch
        val factory = VideoFrameDecoder.Factory()
        val bitmap1 = ImageRequest(context, ResourceImages.mp4.uri) {
            memoryCachePolicy(DISABLED)
            resultCachePolicy(DISABLED)
            videoFrameOption(MediaMetadataRetriever.OPTION_CLOSEST)
        }.run {
            val fetcher =
                sketch.components.newFetcherOrThrow(this.toRequestContext(sketch, Size.Empty))
            val fetchResult = runBlocking { fetcher.fetch() }.getOrThrow()
            runBlocking {
                factory.create(this@run.toRequestContext(sketch), fetchResult)!!.decode()
            }.getOrThrow()
        }.image.getBitmapOrThrow()
        val bitmap11 = ImageRequest(context, ResourceImages.mp4.uri) {
            memoryCachePolicy(DISABLED)
            resultCachePolicy(DISABLED)
            videoFrameOption(MediaMetadataRetriever.OPTION_CLOSEST)
        }.run {
            val fetcher =
                sketch.components.newFetcherOrThrow(this.toRequestContext(sketch, Size.Empty))
            val fetchResult = runBlocking { fetcher.fetch() }.getOrThrow()
            runBlocking {
                factory.create(this@run.toRequestContext(sketch), fetchResult)!!.decode()
            }.getOrThrow()
        }.image.getBitmapOrThrow()
        val bitmap2 = ImageRequest(context, ResourceImages.mp4.uri) {
            memoryCachePolicy(DISABLED)
            resultCachePolicy(DISABLED)
            videoFrameOption(MediaMetadataRetriever.OPTION_CLOSEST)
            videoFramePercent(0.45f)
        }.run {
            val fetcher =
                sketch.components.newFetcherOrThrow(this.toRequestContext(sketch, Size.Empty))
            val fetchResult = runBlocking { fetcher.fetch() }.getOrThrow()
            runBlocking {
                factory.create(this@run.toRequestContext(sketch), fetchResult)!!.decode()
            }.getOrThrow()
        }.image.getBitmapOrThrow()
        assertEquals(bitmap1.corners(), bitmap11.corners())
        assertNotEquals(bitmap1.corners(), bitmap2.corners())
    }

    @Test
    fun testDecodeVideoOption() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O_MR1) return

        val context = InstrumentationRegistry.getInstrumentation().context

        val sketch = context.sketch
        val factory = VideoFrameDecoder.Factory()
        val bitmap1 = ImageRequest(context, ResourceImages.mp4.uri) {
            memoryCachePolicy(DISABLED)
            resultCachePolicy(DISABLED)
            videoFramePercent(0.5f)
        }.run {
            val fetcher =
                sketch.components.newFetcherOrThrow(this.toRequestContext(sketch, Size.Empty))
            val fetchResult = runBlocking { fetcher.fetch() }.getOrThrow()
            runBlocking {
                factory.create(this@run.toRequestContext(sketch), fetchResult)!!.decode()
            }.getOrThrow()
        }.image.getBitmapOrThrow()
        val bitmap2 = ImageRequest(context, ResourceImages.mp4.uri) {
            memoryCachePolicy(DISABLED)
            resultCachePolicy(DISABLED)
            videoFramePercent(0.5f)
            videoFrameOption(MediaMetadataRetriever.OPTION_CLOSEST)
        }.run {
            val fetcher =
                sketch.components.newFetcherOrThrow(this.toRequestContext(sketch, Size.Empty))
            val fetchResult = runBlocking { fetcher.fetch() }.getOrThrow()
            runBlocking {
                factory.create(this@run.toRequestContext(sketch), fetchResult)!!.decode()
            }.getOrThrow()
        }.image.getBitmapOrThrow()
        assertNotEquals(bitmap1.corners(), bitmap2.corners())
    }

    private fun Bitmap.toShortInfoString(): String = "Bitmap(${width}x${height},$config)"
}