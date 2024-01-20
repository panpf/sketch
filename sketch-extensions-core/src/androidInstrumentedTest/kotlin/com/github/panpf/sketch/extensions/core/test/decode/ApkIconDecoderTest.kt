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
package com.github.panpf.sketch.extensions.core.test.decode

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.github.panpf.sketch.ComponentRegistry
import com.github.panpf.sketch.datasource.AssetDataSource
import com.github.panpf.sketch.datasource.DataFrom.LOCAL
import com.github.panpf.sketch.decode.ApkIconDecoder
import com.github.panpf.sketch.decode.internal.createInSampledTransformed
import com.github.panpf.sketch.decode.internal.createResizeTransformed
import com.github.panpf.sketch.decode.internal.createScaledTransformed
import com.github.panpf.sketch.decode.supportApkIcon
import com.github.panpf.sketch.fetch.FetchResult
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.getBitmapOrThrow
import com.github.panpf.sketch.resize.Precision.LESS_PIXELS
import com.github.panpf.sketch.resize.Precision.SAME_ASPECT_RATIO
import com.github.panpf.sketch.resize.Resize
import com.github.panpf.sketch.resize.Scale.CENTER_CROP
import com.github.panpf.sketch.resources.AssetImages
import com.github.panpf.sketch.test.singleton.sketch
import com.github.panpf.sketch.test.utils.intrinsicSize
import com.github.panpf.sketch.test.utils.samplingByTarget
import com.github.panpf.sketch.test.utils.toRequestContext
import com.github.panpf.sketch.util.Size
import com.github.panpf.tools4j.test.ktx.assertThrow
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.math.ceil
import kotlin.math.min
import kotlin.math.roundToInt

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
    fun testFactory() {
        val context = InstrumentationRegistry.getInstrumentation().context
        val sketch = context.sketch
        val factory = ApkIconDecoder.Factory()

        Assert.assertEquals("ApkIconDecoder", factory.toString())

        // mimeType normal
        ImageRequest(context, AssetImages.svg.uri).let {
            val fetchResult = FetchResult(
                AssetDataSource(sketch, it, AssetImages.svg.fileName),
                "application/vnd.android.package-archive"
            )
            factory.create(sketch, it.toRequestContext(sketch), fetchResult)
        }.apply {
            Assert.assertNotNull(this)
        }

        // mimeType null
        ImageRequest(context, AssetImages.png.uri).let {
            val fetchResult =
                FetchResult(AssetDataSource(sketch, it, AssetImages.png.fileName), null)
            factory.create(sketch, it.toRequestContext(sketch), fetchResult)
        }.apply {
            Assert.assertNull(this)
        }

        // mimeType error
        ImageRequest(context, AssetImages.png.uri).let {
            val fetchResult =
                FetchResult(AssetDataSource(sketch, it, AssetImages.png.fileName), "image/svg+xml")
            factory.create(sketch, it.toRequestContext(sketch), fetchResult)
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
    fun testDecode() {
        val context = InstrumentationRegistry.getInstrumentation().context
        val sketch = context.sketch
        val factory = ApkIconDecoder.Factory()
        val apkFilePath = context.applicationInfo.publicSourceDir
        val iconDrawable = context.applicationInfo.loadIcon(context.packageManager)!!

        ImageRequest(context, apkFilePath).run {
            val fetcher = sketch.components.newFetcherOrThrow(this)
            val fetchResult = runBlocking { fetcher.fetch() }.getOrThrow()
            runBlocking {
                factory.create(sketch, this@run.toRequestContext(sketch), fetchResult)!!.decode()
            }.getOrThrow()
        }.apply {
            Assert.assertEquals(
                "Bitmap(${iconDrawable.intrinsicWidth}x${iconDrawable.intrinsicHeight},ARGB_8888)",
                image.getBitmapOrThrow().toShortInfoString()
            )
            Assert.assertEquals(
                "ImageInfo(${iconDrawable.intrinsicWidth}x${iconDrawable.intrinsicHeight},'image/png',UNDEFINED)",
                imageInfo.toShortString()
            )
            Assert.assertEquals(LOCAL, dataFrom)
            Assert.assertNull(transformedList)
        }

        ImageRequest(context, apkFilePath) {
            bitmapConfig(Bitmap.Config.RGB_565)
        }.run {
            val fetcher = sketch.components.newFetcherOrThrow(this)
            val fetchResult = runBlocking { fetcher.fetch() }.getOrThrow()
            runBlocking {
                factory.create(sketch, this@run.toRequestContext(sketch), fetchResult)!!.decode()
            }.getOrThrow()
        }.apply {
            Assert.assertEquals(
                "Bitmap(${iconDrawable.intrinsicWidth}x${iconDrawable.intrinsicHeight},RGB_565)",
                image.getBitmapOrThrow().toShortInfoString()
            )
            Assert.assertEquals(
                "ImageInfo(${iconDrawable.intrinsicWidth}x${iconDrawable.intrinsicHeight},'image/png',UNDEFINED)",
                imageInfo.toShortString()
            )
            Assert.assertEquals(LOCAL, dataFrom)
            Assert.assertNull(transformedList)
        }

        ImageRequest(context, apkFilePath) {
            resize(100, 100, LESS_PIXELS)
        }.run {
            val fetcher = sketch.components.newFetcherOrThrow(this)
            val fetchResult = runBlocking { fetcher.fetch() }.getOrThrow()
            runBlocking {
                factory.create(sketch, this@run.toRequestContext(sketch), fetchResult)!!.decode()
            }.getOrThrow()
        }.apply {
            if (iconDrawable is BitmapDrawable) {
                val bitmapSize =
                    samplingByTarget(iconDrawable.intrinsicSize, Size(100, 100), imageInfo.mimeType)
                Assert.assertEquals(
                    "Bitmap(${bitmapSize.height}x${bitmapSize.height},ARGB_8888)",
                    image.getBitmapOrThrow().toShortInfoString()
                )
                Assert.assertEquals(
                    listOf(createInSampledTransformed(2)),
                    transformedList
                )
            } else {
                val scale = min(
                    100 / iconDrawable.intrinsicWidth.toFloat(),
                    100 / iconDrawable.intrinsicHeight.toFloat()
                )
                Assert.assertEquals(
                    "Bitmap(${(iconDrawable.intrinsicWidth * scale).roundToInt()}x${(iconDrawable.intrinsicHeight * scale).roundToInt()},ARGB_8888)",
                    image.getBitmapOrThrow().toShortInfoString()
                )
                Assert.assertEquals(listOf(createScaledTransformed(scale)), transformedList)
            }
            Assert.assertEquals(
                "ImageInfo(${iconDrawable.intrinsicWidth}x${iconDrawable.intrinsicHeight},'image/png',UNDEFINED)",
                imageInfo.toShortString()
            )
            Assert.assertEquals(LOCAL, dataFrom)
        }

        ImageRequest(context, apkFilePath) {
            resize(iconDrawable.intrinsicWidth, iconDrawable.intrinsicHeight * 2, SAME_ASPECT_RATIO)
        }.run {
            val fetcher = sketch.components.newFetcherOrThrow(this)
            val fetchResult = runBlocking { fetcher.fetch() }.getOrThrow()
            runBlocking {
                factory.create(sketch, this@run.toRequestContext(sketch), fetchResult)!!.decode()
            }.getOrThrow()
        }.apply {
            Assert.assertEquals(
                "Bitmap(${ceil(iconDrawable.intrinsicWidth / 2f).toInt()}x${iconDrawable.intrinsicHeight},ARGB_8888)",
                image.getBitmapOrThrow().toShortInfoString()
            )
            Assert.assertEquals(
                "ImageInfo(${iconDrawable.intrinsicWidth}x${iconDrawable.intrinsicHeight},'image/png',UNDEFINED)",
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
                transformedList
            )
        }

        ImageRequest(context, AssetImages.png.uri).run {
            val fetcher = sketch.components.newFetcherOrThrow(this)
            val fetchResult = runBlocking { fetcher.fetch() }.getOrThrow()
            assertThrow(NullPointerException::class) {
                runBlocking {
                    factory.create(sketch, this@run.toRequestContext(sketch), fetchResult)!!.decode()
                }.getOrThrow()
            }
        }
    }

    private fun Bitmap.toShortInfoString(): String = "Bitmap(${width}x${height},$config)"
}