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

package com.github.panpf.sketch.core.android.test.decode.internal

import android.content.res.Resources
import android.os.Build
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.decode.internal.DrawableDecoder
import com.github.panpf.sketch.decode.internal.createScaledTransformed
import com.github.panpf.sketch.drawable.ResDrawable
import com.github.panpf.sketch.fetch.FetchResult
import com.github.panpf.sketch.fetch.copy
import com.github.panpf.sketch.fetch.newResourceUri
import com.github.panpf.sketch.images.ResourceImages
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.source.DataFrom.LOCAL
import com.github.panpf.sketch.source.DrawableDataSource
import com.github.panpf.sketch.test.singleton.getTestContextAndSketch
import com.github.panpf.sketch.test.utils.getBitmapOrThrow
import com.github.panpf.sketch.test.utils.shortInfoColorSpace
import com.github.panpf.sketch.test.utils.toDecoder
import com.github.panpf.sketch.test.utils.toRequestContext
import com.github.panpf.sketch.util.Size
import com.github.panpf.sketch.util.toShortInfoString
import com.github.panpf.tools4a.dimen.ktx.dp2px
import kotlinx.coroutines.test.runTest
import org.junit.runner.RunWith
import kotlin.math.min
import kotlin.math.roundToInt
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNotSame
import kotlin.test.assertNull

@RunWith(AndroidJUnit4::class)
class DrawableDecoderTest {

    @Test
    fun testFactory() = runTest {
        val (context, sketch) = getTestContextAndSketch()
        val factory = DrawableDecoder.Factory()

        assertEquals("DrawableDecoder", factory.toString())
        assertEquals("DrawableDecoder", factory.key)

        // normal
        ImageRequest(
            context,
            newResourceUri(com.github.panpf.sketch.test.utils.core.R.drawable.test)
        ).let {
            val fetcher = sketch.components.newFetcherOrThrow(
                it.toRequestContext(sketch, Size.Empty)
            )
            val fetchResult = fetcher.fetch().getOrThrow()
            factory.create(it.toRequestContext(sketch), fetchResult)
        }.apply {
            assertNotNull(this)
        }

        // data error
        ImageRequest(context, ResourceImages.png.uri).let {
            val fetcher = sketch.components.newFetcherOrThrow(
                it.toRequestContext(sketch, Size.Empty)
            )
            val fetchResult = fetcher.fetch().getOrThrow()
            factory.create(it.toRequestContext(sketch), fetchResult)
        }.apply {
            assertNull(this)
        }
    }

    @Test
    fun testFactoryEqualsAndHashCode() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) return

        val element1 = DrawableDecoder.Factory()
        val element11 = DrawableDecoder.Factory()

        assertNotSame(element1, element11)

        assertEquals(element1, element1)
        assertEquals(element1, element11)
        assertNotEquals(element1, null as Any?)
        assertNotEquals(element1, Any())

        assertEquals(element1.hashCode(), element1.hashCode())
        assertEquals(element1.hashCode(), element11.hashCode())
    }

    @Test
    fun testImageInfo() = runTest {
        val (context, sketch) = getTestContextAndSketch()
        val factory = DrawableDecoder.Factory()

        val resourceUri = newResourceUri(com.github.panpf.sketch.test.utils.core.R.drawable.test)
        ImageRequest(context, resourceUri)
            .toDecoder(sketch, factory)
            .imageInfo.apply {
                val imageWidth = 50.dp2px
                val imageHeight = 40.dp2px
                assertEquals(
                    "ImageInfo(${imageWidth}x${imageHeight},'text/xml')",
                    toShortString()
                )
            }

        ImageRequest(context, resourceUri)
            .toDecoder(sketch, factory) {
                it.copy(mimeType = null)
            }.imageInfo.apply {
                val imageWidth = 50.dp2px
                val imageHeight = 40.dp2px
                assertEquals(
                    "ImageInfo(${imageWidth}x${imageHeight},'image/png')",
                    toShortString()
                )
            }

        ImageRequest(context, newResourceUri(8801)).run {
            assertFailsWith(Resources.NotFoundException::class) {
                factory.create(
                    requestContext = this@run.toRequestContext(sketch),
                    fetchResult = FetchResult(
                        dataSource = DrawableDataSource(
                            context = context,
                            dataFrom = LOCAL,
                            drawableFetcher = ResDrawable(8801)
                        ),
                        mimeType = "image/png"
                    )
                )!!.imageInfo
            }
        }
    }

    @Test
    fun testDecode() = runTest {
        val (context, sketch) = getTestContextAndSketch()

        val factory = DrawableDecoder.Factory()
        val imageWidth = 50.dp2px
        val imageHeight = 40.dp2px

        ImageRequest(
            context,
            newResourceUri(com.github.panpf.sketch.test.utils.core.R.drawable.test)
        ) {
            resize(imageWidth / 2, imageWidth / 2)
        }.run {
            val fetcher = sketch.components.newFetcherOrThrow(
                this
                    .toRequestContext(sketch, Size.Empty)
            )
            val fetchResult = fetcher.fetch().getOrThrow()
            factory.create(this@run.toRequestContext(sketch), fetchResult)!!.decode()
        }.apply {
            val scale = min(
                (imageWidth / 2) / imageWidth.toFloat(),
                (imageWidth / 2) / imageHeight.toFloat()
            )
            val scaledSize = Size(
                width = (imageWidth * scale).roundToInt(),
                height = (imageHeight * scale).roundToInt()
            )
            assertEquals(
                "Bitmap(${scaledSize},ARGB_8888${shortInfoColorSpace("SRGB")})",
                image.getBitmapOrThrow().toShortInfoString()
            )
            assertEquals(listOf(createScaledTransformed(scale)), transformeds)
            assertEquals(
                "ImageInfo(${imageWidth}x${imageHeight},'text/xml')",
                imageInfo.toShortString()
            )
            assertEquals(LOCAL, dataFrom)
        }

        ImageRequest(
            context,
            newResourceUri(com.github.panpf.sketch.test.utils.core.R.drawable.test)
        ) {
            resize(imageWidth * 2, imageWidth * 2)
        }.run {
            val fetcher = sketch.components.newFetcherOrThrow(
                this.toRequestContext(sketch, Size.Empty)
            )
            val fetchResult = fetcher.fetch().getOrThrow()
            factory.create(this@run.toRequestContext(sketch), fetchResult)!!.decode()
        }.apply {
            val scale = min(
                (imageWidth * 2) / imageWidth.toFloat(),
                (imageWidth * 2) / imageHeight.toFloat()
            )
            val scaledSize = Size(
                width = (imageWidth * scale).roundToInt(),
                height = (imageHeight * scale).roundToInt()
            )
            assertEquals(
                "Bitmap(${scaledSize},ARGB_8888${shortInfoColorSpace("SRGB")})",
                image.getBitmapOrThrow().toShortInfoString()
            )
            assertEquals(listOf(createScaledTransformed(2.0f)), transformeds)
            assertEquals(
                "ImageInfo(${imageWidth}x${imageHeight},'text/xml')",
                imageInfo.toShortString()
            )
            assertEquals(LOCAL, dataFrom)
        }

        ImageRequest(context, newResourceUri(8801)).run {
            assertFailsWith(Resources.NotFoundException::class) {
                factory.create(
                    requestContext = this@run.toRequestContext(sketch),
                    fetchResult = FetchResult(
                        dataSource = DrawableDataSource(
                            context = context,
                            dataFrom = LOCAL,
                            drawableFetcher = ResDrawable(8801)
                        ),
                        mimeType = "image/png"
                    )
                )!!.decode()
            }
        }
    }
}