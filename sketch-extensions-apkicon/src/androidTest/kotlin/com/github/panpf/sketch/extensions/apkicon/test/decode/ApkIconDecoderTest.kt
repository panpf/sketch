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

package com.github.panpf.sketch.extensions.apkicon.test.decode

import android.graphics.Bitmap
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.ComponentRegistry
import com.github.panpf.sketch.decode.ApkIconDecoder
import com.github.panpf.sketch.decode.internal.createResizeTransformed
import com.github.panpf.sketch.decode.internal.createScaledTransformed
import com.github.panpf.sketch.decode.supportApkIcon
import com.github.panpf.sketch.images.ResourceImages
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.colorType
import com.github.panpf.sketch.resize.Precision.LESS_PIXELS
import com.github.panpf.sketch.resize.Precision.SAME_ASPECT_RATIO
import com.github.panpf.sketch.resize.Resize
import com.github.panpf.sketch.resize.Scale.CENTER_CROP
import com.github.panpf.sketch.source.DataFrom.LOCAL
import com.github.panpf.sketch.test.singleton.getTestContextAndSketch
import com.github.panpf.sketch.test.utils.createDecoderOrNull
import com.github.panpf.sketch.test.utils.decode
import com.github.panpf.sketch.test.utils.getBitmapOrThrow
import com.github.panpf.sketch.test.utils.intrinsicSize
import com.github.panpf.sketch.test.utils.shortInfoColorSpace
import com.github.panpf.sketch.test.utils.toRequestContext
import com.github.panpf.sketch.util.Size
import com.github.panpf.sketch.util.calculateScaleMultiplierWithOneSide
import com.github.panpf.sketch.util.times
import com.github.panpf.sketch.util.toShortInfoString
import kotlinx.coroutines.test.runTest
import org.junit.runner.RunWith
import java.io.File
import kotlin.math.ceil
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

@RunWith(AndroidJUnit4::class)
class ApkIconDecoderTest {

    @Test
    fun testSupportApkIcon() {
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

            supportApkIcon()
            build().apply {
                assertEquals(
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
                assertEquals(
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
    fun testConstructor() = runTest {
        val (context, sketch) = getTestContextAndSketch()

        val request = ImageRequest(context, "/sdcard/sample.apk")
        val requestContext = request.toRequestContext(sketch)

        ApkIconDecoder(requestContext, LOCAL, File("/sdcard/sample.apk"))
        ApkIconDecoder(
            requestContext = requestContext,
            dataFrom = LOCAL,
            file = File("/sdcard/sample.apk")
        )
    }

    @Test
    fun testDecode() = runTest {
        val (context, sketch) = getTestContextAndSketch()
        val factory = ApkIconDecoder.Factory()
        val apkFilePath = context.applicationInfo.publicSourceDir
        val iconDrawable = context.applicationInfo.loadIcon(context.packageManager)!!

        val screenSize = context.resources.displayMetrics.let {
            Size(it.widthPixels, it.heightPixels)
        }
        ImageRequest(context, apkFilePath)
            .decode(sketch, factory)
            .apply {
                val sizeMultiplier = calculateScaleMultiplierWithOneSide(imageInfo.size, screenSize)
                val bitmapSize = imageInfo.size.times(sizeMultiplier)
                assertEquals(
                    expected = "Bitmap(${bitmapSize},ARGB_8888${shortInfoColorSpace("SRGB")})",
                    actual = image.getBitmapOrThrow().toShortInfoString()
                )
                assertEquals(
                    expected = "ImageInfo(${iconDrawable.intrinsicSize},'image/png')",
                    actual = imageInfo.toShortString()
                )
                assertEquals(expected = LOCAL, actual = dataFrom)
                assertEquals(
                    expected = listOf(createScaledTransformed(sizeMultiplier)),
                    actual = transformeds
                )
            }

        ImageRequest(context, apkFilePath) {
            colorType(Bitmap.Config.RGB_565)
        }.decode(sketch, factory).apply {
            val sizeMultiplier = calculateScaleMultiplierWithOneSide(imageInfo.size, screenSize)
            val bitmapSize = imageInfo.size.times(sizeMultiplier)
            assertEquals(
                expected = "Bitmap(${bitmapSize},RGB_565${shortInfoColorSpace("SRGB")})",
                actual = image.getBitmapOrThrow().toShortInfoString()
            )
            assertEquals(
                expected = "ImageInfo(${iconDrawable.intrinsicSize},'image/png')",
                actual = imageInfo.toShortString()
            )
            assertEquals(expected = LOCAL, actual = dataFrom)
            assertEquals(
                expected = listOf(createScaledTransformed(sizeMultiplier)),
                actual = transformeds
            )
        }

        ImageRequest(context, apkFilePath) {
            size(Size.Origin)
        }.decode(sketch, factory).apply {
            val iconSize = iconDrawable.intrinsicSize
            assertEquals(
                expected = "Bitmap(${iconSize},ARGB_8888${shortInfoColorSpace("SRGB")})",
                actual = image.getBitmapOrThrow().toShortInfoString()
            )
            assertNull(actual = transformeds)
            assertEquals(
                expected = "ImageInfo(${iconSize},'image/png')",
                actual = imageInfo.toShortString()
            )
            assertEquals(expected = LOCAL, actual = dataFrom)
        }

        ImageRequest(context, apkFilePath) {
            resize(100, 100, LESS_PIXELS)
        }.decode(sketch, factory)
            .apply {
                val sizeMultiplier =
                    calculateScaleMultiplierWithOneSide(imageInfo.size, Size(100, 100))
                val bitmapSize = imageInfo.size.times(sizeMultiplier)
                assertEquals(
                    expected = "Bitmap(${bitmapSize},ARGB_8888${shortInfoColorSpace("SRGB")})",
                    actual = image.getBitmapOrThrow().toShortInfoString()
                )
                assertEquals(listOf(createScaledTransformed(sizeMultiplier)), transformeds)
                assertEquals(
                    expected = "ImageInfo(${iconDrawable.intrinsicSize},'image/png')",
                    actual = imageInfo.toShortString()
                )
                assertEquals(expected = LOCAL, actual = dataFrom)
            }

        ImageRequest(context, apkFilePath) {
            resize(iconDrawable.intrinsicWidth, iconDrawable.intrinsicHeight * 2, SAME_ASPECT_RATIO)
        }.decode(sketch, factory)
            .apply {
                val bitmapSize = Size(
                    width = ceil(iconDrawable.intrinsicWidth / 2f).toInt(),
                    height = iconDrawable.intrinsicHeight
                )
                assertEquals(
                    expected = "Bitmap(${bitmapSize},ARGB_8888${shortInfoColorSpace("SRGB")})",
                    actual = image.getBitmapOrThrow().toShortInfoString()
                )
                assertEquals(
                    expected = "ImageInfo(${iconDrawable.intrinsicSize},'image/png')",
                    actual = imageInfo.toShortString()
                )
                assertEquals(expected = LOCAL, actual = dataFrom)
                assertEquals(
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

        assertFailsWith(NullPointerException::class) {
            val request = ImageRequest(context, ResourceImages.png.uri)
            val requestContext = request.toRequestContext(sketch)
            val fetcher = sketch.components.newFetcherOrThrow(requestContext)
            val fetchResult = fetcher.fetch().getOrThrow()
            factory.create(requestContext, fetchResult)!!.decode()
        }
    }

    @Test
    fun testEqualsAndHashCode() = runTest {
        val (context, sketch) = getTestContextAndSketch()
        val request = ImageRequest(context, "/sdcard/sample.apk")
        val requestContext = request.toRequestContext(sketch)
        val element1 = ApkIconDecoder(requestContext, LOCAL, File("/sdcard/sample.apk"))
        val element11 = ApkIconDecoder(requestContext, LOCAL, File("/sdcard/sample.apk"))

        assertNotEquals(illegal = element1, actual = element11)
        assertNotEquals(illegal = element1, actual = null as Any?)
        assertNotEquals(illegal = element1, actual = Any())
        assertNotEquals(illegal = element1.hashCode(), actual = element11.hashCode())
    }

    @Test
    fun testToString() = runTest {
        val (context, sketch) = getTestContextAndSketch()
        val request = ImageRequest(context, "/sdcard/sample.apk")
        val requestContext = request.toRequestContext(sketch)
        val decoder = ApkIconDecoder(requestContext, LOCAL, File("/sdcard/sample.apk"))
        assertTrue(
            actual = decoder.toString().contains("ApkIconDecoder"),
            message = decoder.toString()
        )
        assertTrue(actual = decoder.toString().contains("@"), message = decoder.toString())
    }

    @Test
    fun testFactoryConstructor() {
        ApkIconDecoder.Factory()
    }

    @Test
    fun testFactoryKey() {
        assertEquals(
            expected = "ApkIconDecoder",
            actual = ApkIconDecoder.Factory().key
        )
    }

    @Test
    fun testFactoryCreate() = runTest {
        val (context, sketch) = getTestContextAndSketch()
        val factory = ApkIconDecoder.Factory()

        // mimeType normal
        ImageRequest(context, ResourceImages.svg.uri)
            .createDecoderOrNull(sketch, factory) {
                it.copy(mimeType = "application/vnd.android.package-archive")
            }.apply {
                assertTrue(this is ApkIconDecoder)
            }

        // mimeType null
        ImageRequest(context, ResourceImages.png.uri)
            .createDecoderOrNull(sketch, factory) {
                it.copy(mimeType = null)
            }.apply {
                assertNull(this)
            }

        // mimeType error
        ImageRequest(context, ResourceImages.png.uri)
            .createDecoderOrNull(sketch, factory) {
                it.copy(mimeType = "image/svg+xml")
            }.apply {
                assertNull(this)
            }
    }

    @Test
    fun testFactoryEqualsAndHashCode() {
        val element1 = ApkIconDecoder.Factory()
        val element11 = ApkIconDecoder.Factory()

        assertEquals(expected = element1, actual = element11)
        assertNotEquals(illegal = element1, actual = null as Any?)
        assertNotEquals(illegal = element1, actual = Any())

        assertEquals(expected = element1.hashCode(), actual = element11.hashCode())
    }

    @Test
    fun testFactoryToString() = runTest {
        assertEquals(
            expected = "ApkIconDecoder",
            actual = ApkIconDecoder.Factory().toString()
        )
    }
}