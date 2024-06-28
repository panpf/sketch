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
package com.github.panpf.sketch.core.android.test.decode.internal

import android.content.res.Resources
import android.graphics.Bitmap
import android.os.Build
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.decode.internal.DrawableDecoder
import com.github.panpf.sketch.decode.internal.createScaledTransformed
import com.github.panpf.sketch.fetch.FetchResult
import com.github.panpf.sketch.fetch.newResourceUri
import com.github.panpf.sketch.getBitmapOrThrow
import com.github.panpf.sketch.images.ResourceImages
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.source.DataFrom.LOCAL
import com.github.panpf.sketch.source.DrawableDataSource
import com.github.panpf.sketch.test.singleton.getTestContextAndSketch
import com.github.panpf.sketch.test.utils.toRequestContext
import com.github.panpf.sketch.drawable.ResDrawable
import com.github.panpf.tools4a.dimen.ktx.dp2px
import com.github.panpf.tools4j.test.ktx.assertThrow
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.math.min
import kotlin.math.roundToInt

@RunWith(AndroidJUnit4::class)
class DrawableDecoderTest {

    @Test
    fun testFactory() = runTest {
        val (context, sketch) = getTestContextAndSketch()
        val factory = DrawableDecoder.Factory()

        Assert.assertEquals("DrawableDecoder", factory.toString())

        // normal
        ImageRequest(
            context,
            newResourceUri(com.github.panpf.sketch.test.utils.core.R.drawable.test)
        ).let {
            val fetcher = sketch.components.newFetcherOrThrow(it)
            val fetchResult = runBlocking { fetcher.fetch() }.getOrThrow()
            factory.create(it.toRequestContext(sketch), fetchResult)
        }.apply {
            Assert.assertNotNull(this)
        }

        // data error
        ImageRequest(context, ResourceImages.png.uri).let {
            val fetcher = sketch.components.newFetcherOrThrow(it)
            val fetchResult = runBlocking { fetcher.fetch() }.getOrThrow()
            factory.create(it.toRequestContext(sketch), fetchResult)
        }.apply {
            Assert.assertNull(this)
        }
    }

    @Test
    fun testFactoryEqualsAndHashCode() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) return

        val element1 = DrawableDecoder.Factory()
        val element11 = DrawableDecoder.Factory()

        Assert.assertNotSame(element1, element11)

        Assert.assertEquals(element1, element1)
        Assert.assertEquals(element1, element11)
        Assert.assertNotEquals(element1, null)
        Assert.assertNotEquals(element1, Any())

        Assert.assertEquals(element1.hashCode(), element1.hashCode())
        Assert.assertEquals(element1.hashCode(), element11.hashCode())
    }

    @Test
    fun testDecode() {
        val (context, sketch) = getTestContextAndSketch()

        val factory = DrawableDecoder.Factory()
        val imageWidth = 50.dp2px
        val imageHeight = 40.dp2px

//        ImageRequest(context, newResourceUri(R.drawable.test)).run {
//            val fetcher = sketch.components.newFetcherOrThrow(this)
//            val fetchResult = runBlocking { fetcher.fetch() }.getOrThrow()
//            runBlocking {
//                factory.create(sketch, this@run.toRequestContext(sketch), fetchResult)!!.decode()
//            }.getOrThrow()
//        }.apply {
//            Assert.assertEquals(
//                "Bitmap(${imageWidth}x${imageHeight},ARGB_8888)",
//                bitmap.toShortInfoString()
//            )
//            Assert.assertEquals(
//                "ImageInfo(${imageWidth}x${imageHeight},'text/xml')",
//                imageInfo.toShortString()
//            )
//            Assert.assertEquals(LOCAL, dataFrom)
//            Assert.assertNull(transformeds)
//        }
//
//        ImageRequest(context, newResourceUri(R.drawable.test)) {
//            bitmapConfig(Bitmap.Config.RGB_565)
//        }.run {
//            val fetcher = sketch.components.newFetcherOrThrow(this)
//            val fetchResult = runBlocking { fetcher.fetch() }.getOrThrow()
//            runBlocking {
//                factory.create(sketch, this@run.toRequestContext(sketch), fetchResult)!!.decode()
//            }.getOrThrow()
//        }.apply {
//            Assert.assertEquals(
//                "Bitmap(${imageWidth}x${imageHeight},RGB_565)",
//                bitmap.toShortInfoString()
//            )
//            Assert.assertEquals(
//                "ImageInfo(${imageWidth}x${imageHeight},'text/xml')",
//                imageInfo.toShortString()
//            )
//            Assert.assertEquals(LOCAL, dataFrom)
//            Assert.assertNull(transformeds)
//        }

        ImageRequest(
            context,
            newResourceUri(com.github.panpf.sketch.test.utils.core.R.drawable.test)
        ) {
            resize(imageWidth / 2, imageWidth / 2)
        }.run {
            val fetcher = sketch.components.newFetcherOrThrow(this)
            val fetchResult = runBlocking { fetcher.fetch() }.getOrThrow()
            runBlocking {
                factory.create(this@run.toRequestContext(sketch), fetchResult)!!.decode()
            }.getOrThrow()
        }.apply {
            val scale = min(
                (imageWidth / 2) / imageWidth.toFloat(),
                (imageWidth / 2) / imageHeight.toFloat()
            )
            Assert.assertEquals(
                "Bitmap(${(imageWidth * scale).roundToInt()}x${(imageHeight * scale).roundToInt()},ARGB_8888)",
                image.getBitmapOrThrow().toShortInfoString()
            )
            Assert.assertEquals(listOf(createScaledTransformed(scale)), transformeds)
            Assert.assertEquals(
                "ImageInfo(${imageWidth}x${imageHeight},'text/xml')",
                imageInfo.toShortString()
            )
            Assert.assertEquals(LOCAL, dataFrom)
        }

        ImageRequest(
            context,
            newResourceUri(com.github.panpf.sketch.test.utils.core.R.drawable.test)
        ) {
            resize(imageWidth * 2, imageWidth * 2)
        }.run {
            val fetcher = sketch.components.newFetcherOrThrow(this)
            val fetchResult = runBlocking { fetcher.fetch() }.getOrThrow()
            runBlocking {
                factory.create(this@run.toRequestContext(sketch), fetchResult)!!.decode()
            }.getOrThrow()
        }.apply {
            val scale = min(
                (imageWidth * 2) / imageWidth.toFloat(),
                (imageWidth * 2) / imageHeight.toFloat()
            )
            Assert.assertEquals(
                "Bitmap(${(imageWidth * scale).roundToInt()}x${(imageHeight * scale).roundToInt()},ARGB_8888)",
                image.getBitmapOrThrow().toShortInfoString()
            )
            Assert.assertEquals(listOf(createScaledTransformed(2.0f)), transformeds)
            Assert.assertEquals(
                "ImageInfo(${imageWidth}x${imageHeight},'text/xml')",
                imageInfo.toShortString()
            )
            Assert.assertEquals(LOCAL, dataFrom)
        }

        ImageRequest(context, newResourceUri(8801)).run {
            assertThrow(Resources.NotFoundException::class) {
                runBlocking {
                    factory.create(
                        requestContext = this@run.toRequestContext(sketch),
                        fetchResult = FetchResult(
                            dataSource = DrawableDataSource(
                                sketch = sketch,
                                request = this@run,
                                dataFrom = LOCAL,
                                drawableFetcher = ResDrawable(8801)
                            ),
                            mimeType = "image/png"
                        )
                    )!!.decode()
                }.getOrThrow()
            }
        }
    }

    private fun Bitmap.toShortInfoString(): String = "Bitmap(${width}x${height},$config)"
}