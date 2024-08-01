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
package com.github.panpf.sketch.extensions.core.android.test.decode

import android.graphics.Bitmap
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.github.panpf.sketch.ComponentRegistry
import com.github.panpf.sketch.decode.ApkIconDecoder
import com.github.panpf.sketch.decode.internal.createResizeTransformed
import com.github.panpf.sketch.decode.internal.createScaledTransformed
import com.github.panpf.sketch.decode.supportApkIcon
import com.github.panpf.sketch.fetch.copy
import com.github.panpf.sketch.getBitmapOrThrow
import com.github.panpf.sketch.images.ResourceImages
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.bitmapConfig
import com.github.panpf.sketch.resize.Precision.LESS_PIXELS
import com.github.panpf.sketch.resize.Precision.SAME_ASPECT_RATIO
import com.github.panpf.sketch.resize.Resize
import com.github.panpf.sketch.resize.Scale.CENTER_CROP
import com.github.panpf.sketch.source.DataFrom.LOCAL
import com.github.panpf.sketch.test.singleton.sketch
import com.github.panpf.sketch.test.utils.decode
import com.github.panpf.sketch.test.utils.fetch
import com.github.panpf.sketch.test.utils.toRequestContext
import com.github.panpf.sketch.util.Size
import com.github.panpf.sketch.util.computeScaleMultiplierWithOneSide
import com.github.panpf.sketch.util.times
import com.github.panpf.tools4j.test.ktx.assertThrow
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.math.ceil

@RunWith(AndroidJUnit4::class)
class ApkIconDecoderTest {

    @Test
    fun testSupportApkIcon() {
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

            supportApkIcon()
            build().apply {
                Assert.assertEquals(
                    "ComponentRegistry(" +
                            "fetcherFactoryList=[]," +
                            "decoderFactoryList=[ApkIconDecoder]," +
                            "requestInterceptorList=[]," +
                            "decodeInterceptorList=[]" +
                            ")",
                    toString()
                )
            }

            supportApkIcon()
            build().apply {
                Assert.assertEquals(
                    "ComponentRegistry(" +
                            "fetcherFactoryList=[]," +
                            "decoderFactoryList=[ApkIconDecoder,ApkIconDecoder]," +
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
        val factory = ApkIconDecoder.Factory()

        Assert.assertEquals("ApkIconDecoder", factory.toString())

        // mimeType normal
        ImageRequest(context, ResourceImages.svg.uri).let {
            val fetchResult =
                it.fetch(sketch).copy(mimeType = "application/vnd.android.package-archive")
            factory.create(it.toRequestContext(sketch), fetchResult)
        }.apply {
            Assert.assertNotNull(this)
        }

        // mimeType null
        ImageRequest(context, ResourceImages.png.uri).let {
            val fetchResult = it.fetch(sketch).copy(mimeType = null)
            factory.create(it.toRequestContext(sketch), fetchResult)
        }.apply {
            Assert.assertNull(this)
        }

        // mimeType error
        ImageRequest(context, ResourceImages.png.uri).let {
            val fetchResult = it.fetch(sketch).copy(mimeType = "image/svg+xml")
            factory.create(it.toRequestContext(sketch), fetchResult)
        }.apply {
            Assert.assertNull(this)
        }
    }

    @Test
    fun testFactoryEqualsAndHashCode() {
        val element1 = ApkIconDecoder.Factory()
        val element11 = ApkIconDecoder.Factory()

        Assert.assertNotSame(element1, element11)

        Assert.assertEquals(element1, element1)
        Assert.assertEquals(element1, element11)

        Assert.assertNotEquals(element1, Any())
        Assert.assertNotEquals(element1, null)

        Assert.assertEquals(element1.hashCode(), element1.hashCode())
        Assert.assertEquals(element1.hashCode(), element11.hashCode())
    }

    @Test
    fun testDecode() = runTest {
        val context = InstrumentationRegistry.getInstrumentation().context
        val sketch = context.sketch
        val factory = ApkIconDecoder.Factory()
        val apkFilePath = context.applicationInfo.publicSourceDir
        val iconDrawable = context.applicationInfo.loadIcon(context.packageManager)!!

        val screenSize = context.resources.displayMetrics.let {
            Size(it.widthPixels, it.heightPixels)
        }
        ImageRequest(context, apkFilePath)
            .decode(sketch, factory)
            .apply {
                val sizeMultiplier = computeScaleMultiplierWithOneSide(imageInfo.size, screenSize)
                val bitmapSize = imageInfo.size.times(sizeMultiplier)
                Assert.assertEquals(
                    "Bitmap(${bitmapSize},ARGB_8888)",
                    image.getBitmapOrThrow().toShortInfoString()
                )
                Assert.assertEquals(
                    "ImageInfo(${iconDrawable.intrinsicWidth}x${iconDrawable.intrinsicHeight},'image/png')",
                    imageInfo.toShortString()
                )
                Assert.assertEquals(LOCAL, dataFrom)
                Assert.assertEquals(listOf(createScaledTransformed(sizeMultiplier)), transformeds)
            }

        ImageRequest(context, apkFilePath) {
            bitmapConfig(Bitmap.Config.RGB_565)
        }.decode(sketch, factory)
            .apply {
                val sizeMultiplier = computeScaleMultiplierWithOneSide(imageInfo.size, screenSize)
                val bitmapSize = imageInfo.size.times(sizeMultiplier)
                Assert.assertEquals(
                    "Bitmap(${bitmapSize},RGB_565)",
                    image.getBitmapOrThrow().toShortInfoString()
                )
                Assert.assertEquals(
                    "ImageInfo(${iconDrawable.intrinsicWidth}x${iconDrawable.intrinsicHeight},'image/png')",
                    imageInfo.toShortString()
                )
                Assert.assertEquals(LOCAL, dataFrom)
                Assert.assertEquals(listOf(createScaledTransformed(sizeMultiplier)), transformeds)
            }

        ImageRequest(context, apkFilePath) {
            size(Size.Origin)
        }.decode(sketch, factory)
            .apply {
                Assert.assertEquals(
                    "Bitmap(${iconDrawable.intrinsicWidth}x${iconDrawable.intrinsicHeight},ARGB_8888)",
                    image.getBitmapOrThrow().toShortInfoString()
                )
                Assert.assertNull(null, transformeds)
                Assert.assertEquals(
                    "ImageInfo(${iconDrawable.intrinsicWidth}x${iconDrawable.intrinsicHeight},'image/png')",
                    imageInfo.toShortString()
                )
                Assert.assertEquals(LOCAL, dataFrom)
            }

        ImageRequest(context, apkFilePath) {
            resize(100, 100, LESS_PIXELS)
        }.decode(sketch, factory)
            .apply {
                val sizeMultiplier =
                    computeScaleMultiplierWithOneSide(imageInfo.size, Size(100, 100))
                val bitmapSize = imageInfo.size.times(sizeMultiplier)
                Assert.assertEquals(
                    "Bitmap(${bitmapSize},ARGB_8888)",
                    image.getBitmapOrThrow().toShortInfoString()
                )
                Assert.assertEquals(listOf(createScaledTransformed(sizeMultiplier)), transformeds)
                Assert.assertEquals(
                    "ImageInfo(${iconDrawable.intrinsicWidth}x${iconDrawable.intrinsicHeight},'image/png')",
                    imageInfo.toShortString()
                )
                Assert.assertEquals(LOCAL, dataFrom)
            }

        ImageRequest(context, apkFilePath) {
            resize(iconDrawable.intrinsicWidth, iconDrawable.intrinsicHeight * 2, SAME_ASPECT_RATIO)
        }.decode(sketch, factory)
            .apply {
                Assert.assertEquals(
                    "Bitmap(${ceil(iconDrawable.intrinsicWidth / 2f).toInt()}x${iconDrawable.intrinsicHeight},ARGB_8888)",
                    image.getBitmapOrThrow().toShortInfoString()
                )
                Assert.assertEquals(
                    "ImageInfo(${iconDrawable.intrinsicWidth}x${iconDrawable.intrinsicHeight},'image/png')",
                    imageInfo.toShortString()
                )
                Assert.assertEquals(LOCAL, dataFrom)
                Assert.assertEquals(
                    listOf(
                        createResizeTransformed(
                            Resize(
                                iconDrawable.intrinsicWidth,
                                iconDrawable.intrinsicHeight * 2,
                                SAME_ASPECT_RATIO,
                                CENTER_CROP
                            )
                        )
                    ),
                    transformeds
                )
            }

        ImageRequest(context, ResourceImages.png.uri).run {
            val fetcher = sketch.components.newFetcherOrThrow(this)
            val fetchResult = runBlocking { fetcher.fetch() }.getOrThrow()
            assertThrow(NullPointerException::class) {
                runBlocking {
                    factory.create(this@run.toRequestContext(sketch), fetchResult)!!.decode()
                }.getOrThrow()
            }
        }
    }

    private fun Bitmap.toShortInfoString(): String = "Bitmap(${width}x${height},$config)"
}