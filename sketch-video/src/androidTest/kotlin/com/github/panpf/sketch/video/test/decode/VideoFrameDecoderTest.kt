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
import com.github.panpf.sketch.images.MyImages
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
import com.github.panpf.tools4j.test.ktx.assertThrow
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class VideoFrameDecoderTest {

    @Test
    fun testSupportApkIcon() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O_MR1) return

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

            supportVideoFrame()
            build().apply {
                Assert.assertEquals(
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
                Assert.assertEquals(
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

        Assert.assertEquals("VideoFrameDecoder", factory.toString())

        val mp4Request = ImageRequest(context, MyImages.mp4.uri)
        val mp4RequestContext = runBlocking {
            mp4Request.toRequestContext(sketch)
        }
        val mp4FetchResult = runBlocking {
            sketch.components.newFetcherOrThrow(mp4Request).fetch().getOrThrow()
        }.apply {
            Assert.assertEquals(
                /* expected = */ "FetchResult(source=AssetDataSource('sample.mp4'),mimeType='video/mp4')",
                /* actual = */ this@apply.toString()
            )
        }

        val pngRequest = ImageRequest(context, MyImages.png.uri)
        val pngRequestContext = runBlocking {
            pngRequest.toRequestContext(sketch)
        }
        val pngFetchResult = runBlocking {
            sketch.components.newFetcherOrThrow(pngRequest).fetch().getOrThrow()
        }.apply {
            Assert.assertEquals(
                /* expected = */ "FetchResult(source=AssetDataSource('sample.png'),mimeType='image/png')",
                /* actual = */ this@apply.toString()
            )
        }

        // normal
        Assert.assertNotNull(factory.create(mp4RequestContext, mp4FetchResult))

        // mimeType error
        Assert.assertNull(
            factory.create(mp4RequestContext, mp4FetchResult.copy(mimeType = null))
        )
        Assert.assertNull(
            factory.create(pngRequestContext, pngFetchResult)
        )

        // data error
        Assert.assertNotNull(
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

        Assert.assertNotSame(element1, element11)

        Assert.assertEquals(element1, element1)
        Assert.assertEquals(element1, element11)

        Assert.assertNotEquals(element1, Any())
        Assert.assertNotEquals(element1, null)

        Assert.assertEquals(element1.hashCode(), element1.hashCode())
        Assert.assertEquals(element1.hashCode(), element11.hashCode())
    }

    @Test
    fun testDecode() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O_MR1) return

        val context = InstrumentationRegistry.getInstrumentation().context
        val sketch = context.sketch
        val factory = VideoFrameDecoder.Factory()

        ImageRequest(context, MyImages.mp4.uri).run {
            val fetcher = sketch.components.newFetcherOrThrow(this)
            val fetchResult = runBlocking { fetcher.fetch() }.getOrThrow()
            runBlocking {
                factory.create(this@run.toRequestContext(sketch), fetchResult)!!.decode()
            }.getOrThrow()
        }.apply {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                Assert.assertEquals(
                    "Bitmap(500x250,ARGB_8888)",
                    image.getBitmapOrThrow().toShortInfoString()
                )
            } else {
                Assert.assertEquals(
                    "Bitmap(500x250,RGB_565)",
                    image.getBitmapOrThrow().toShortInfoString()
                )
            }
            Assert.assertEquals(
                "ImageInfo(500x250,'video/mp4')",
                imageInfo.toShortString()
            )
            Assert.assertEquals(LOCAL, dataFrom)
            Assert.assertNull(transformeds)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            ImageRequest(context, MyImages.mp4.uri) {
                bitmapConfig(RGB_565)
            }.run {
                val fetcher = sketch.components.newFetcherOrThrow(this)
                val fetchResult = runBlocking { fetcher.fetch() }.getOrThrow()
                runBlocking {
                    factory.create(this@run.toRequestContext(sketch), fetchResult)!!.decode()
                }.getOrThrow()
            }.apply {
                Assert.assertEquals(
                    "Bitmap(500x250,RGB_565)",
                    image.getBitmapOrThrow().toShortInfoString()
                )
                Assert.assertEquals(
                    "ImageInfo(500x250,'video/mp4')",
                    imageInfo.toShortString()
                )
                Assert.assertEquals(LOCAL, dataFrom)
                Assert.assertNull(transformeds)
            }
        }

        ImageRequest(context, MyImages.mp4.uri) {
            resize(300, 300, LESS_PIXELS)
        }.run {
            val fetcher = sketch.components.newFetcherOrThrow(this)
            val fetchResult = runBlocking { fetcher.fetch() }.getOrThrow()
            runBlocking {
                factory.create(this@run.toRequestContext(sketch), fetchResult)!!.decode()
            }.getOrThrow()
        }.apply {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                Assert.assertEquals(
                    "Bitmap(250x125,ARGB_8888)",
                    image.getBitmapOrThrow().toShortInfoString()
                )
                Assert.assertEquals(listOf(createInSampledTransformed(2)), transformeds)
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
                Assert.assertEquals(
                    "Bitmap(250x125,RGB_565)",
                    image.getBitmapOrThrow().toShortInfoString()
                )
                Assert.assertEquals(listOf(createInSampledTransformed(2)), transformeds)
            } else {
                Assert.assertEquals(
                    "Bitmap(250x125,RGB_565)",
                    image.getBitmapOrThrow().toShortInfoString()
                )
                Assert.assertEquals(
                    listOf(createResizeTransformed(Resize(300, 300, LESS_PIXELS))),
                    transformeds
                )
            }
            Assert.assertEquals(
                "ImageInfo(500x250,'video/mp4')",
                imageInfo.toShortString()
            )
            Assert.assertEquals(LOCAL, dataFrom)
        }

        ImageRequest(context, MyImages.png.uri).run {
            val fetcher = sketch.components.newFetcherOrThrow(this)
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
        val bitmap1 = ImageRequest(context, MyImages.mp4.uri) {
            memoryCachePolicy(DISABLED)
            resultCachePolicy(DISABLED)
            videoFrameOption(MediaMetadataRetriever.OPTION_CLOSEST)
        }.run {
            val fetcher = sketch.components.newFetcherOrThrow(this)
            val fetchResult = runBlocking { fetcher.fetch() }.getOrThrow()
            runBlocking {
                factory.create(this@run.toRequestContext(sketch), fetchResult)!!.decode()
            }.getOrThrow()
        }.image.getBitmapOrThrow()
        val bitmap11 = ImageRequest(context, MyImages.mp4.uri) {
            memoryCachePolicy(DISABLED)
            resultCachePolicy(DISABLED)
            videoFrameOption(MediaMetadataRetriever.OPTION_CLOSEST)
        }.run {
            val fetcher = sketch.components.newFetcherOrThrow(this)
            val fetchResult = runBlocking { fetcher.fetch() }.getOrThrow()
            runBlocking {
                factory.create(this@run.toRequestContext(sketch), fetchResult)!!.decode()
            }.getOrThrow()
        }.image.getBitmapOrThrow()
        val bitmap2 = ImageRequest(context, MyImages.mp4.uri) {
            memoryCachePolicy(DISABLED)
            resultCachePolicy(DISABLED)
            videoFrameOption(MediaMetadataRetriever.OPTION_CLOSEST)
            videoFrameMillis(500)
        }.run {
            val fetcher = sketch.components.newFetcherOrThrow(this)
            val fetchResult = runBlocking { fetcher.fetch() }.getOrThrow()
            runBlocking {
                factory.create(this@run.toRequestContext(sketch), fetchResult)!!.decode()
            }.getOrThrow()
        }.image.getBitmapOrThrow()
        Assert.assertEquals(bitmap1.corners(), bitmap11.corners())
        Assert.assertNotEquals(bitmap1.corners(), bitmap2.corners())
    }

    @Test
    fun testDecodeVideoFramePercent() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O_MR1) return

        val context = InstrumentationRegistry.getInstrumentation().context

        val sketch = context.sketch
        val factory = VideoFrameDecoder.Factory()
        val bitmap1 = ImageRequest(context, MyImages.mp4.uri) {
            memoryCachePolicy(DISABLED)
            resultCachePolicy(DISABLED)
            videoFrameOption(MediaMetadataRetriever.OPTION_CLOSEST)
        }.run {
            val fetcher = sketch.components.newFetcherOrThrow(this)
            val fetchResult = runBlocking { fetcher.fetch() }.getOrThrow()
            runBlocking {
                factory.create(this@run.toRequestContext(sketch), fetchResult)!!.decode()
            }.getOrThrow()
        }.image.getBitmapOrThrow()
        val bitmap11 = ImageRequest(context, MyImages.mp4.uri) {
            memoryCachePolicy(DISABLED)
            resultCachePolicy(DISABLED)
            videoFrameOption(MediaMetadataRetriever.OPTION_CLOSEST)
        }.run {
            val fetcher = sketch.components.newFetcherOrThrow(this)
            val fetchResult = runBlocking { fetcher.fetch() }.getOrThrow()
            runBlocking {
                factory.create(this@run.toRequestContext(sketch), fetchResult)!!.decode()
            }.getOrThrow()
        }.image.getBitmapOrThrow()
        val bitmap2 = ImageRequest(context, MyImages.mp4.uri) {
            memoryCachePolicy(DISABLED)
            resultCachePolicy(DISABLED)
            videoFrameOption(MediaMetadataRetriever.OPTION_CLOSEST)
            videoFramePercent(0.45f)
        }.run {
            val fetcher = sketch.components.newFetcherOrThrow(this)
            val fetchResult = runBlocking { fetcher.fetch() }.getOrThrow()
            runBlocking {
                factory.create(this@run.toRequestContext(sketch), fetchResult)!!.decode()
            }.getOrThrow()
        }.image.getBitmapOrThrow()
        Assert.assertEquals(bitmap1.corners(), bitmap11.corners())
        Assert.assertNotEquals(bitmap1.corners(), bitmap2.corners())
    }

    @Test
    fun testDecodeVideoOption() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O_MR1) return

        val context = InstrumentationRegistry.getInstrumentation().context

        val sketch = context.sketch
        val factory = VideoFrameDecoder.Factory()
        val bitmap1 = ImageRequest(context, MyImages.mp4.uri) {
            memoryCachePolicy(DISABLED)
            resultCachePolicy(DISABLED)
            videoFramePercent(0.5f)
        }.run {
            val fetcher = sketch.components.newFetcherOrThrow(this)
            val fetchResult = runBlocking { fetcher.fetch() }.getOrThrow()
            runBlocking {
                factory.create(this@run.toRequestContext(sketch), fetchResult)!!.decode()
            }.getOrThrow()
        }.image.getBitmapOrThrow()
        val bitmap2 = ImageRequest(context, MyImages.mp4.uri) {
            memoryCachePolicy(DISABLED)
            resultCachePolicy(DISABLED)
            videoFramePercent(0.5f)
            videoFrameOption(MediaMetadataRetriever.OPTION_CLOSEST)
        }.run {
            val fetcher = sketch.components.newFetcherOrThrow(this)
            val fetchResult = runBlocking { fetcher.fetch() }.getOrThrow()
            runBlocking {
                factory.create(this@run.toRequestContext(sketch), fetchResult)!!.decode()
            }.getOrThrow()
        }.image.getBitmapOrThrow()
        Assert.assertNotEquals(bitmap1.corners(), bitmap2.corners())
    }

    private fun Bitmap.toShortInfoString(): String = "Bitmap(${width}x${height},$config)"
}