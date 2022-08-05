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
package com.github.panpf.sketch.extensions.test.decode

import android.graphics.Bitmap
import android.graphics.Bitmap.Config.RGB_565
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.github.panpf.sketch.datasource.AssetDataSource
import com.github.panpf.sketch.datasource.DataFrom.LOCAL
import com.github.panpf.sketch.decode.AppIconBitmapDecoder
import com.github.panpf.sketch.decode.internal.createResizeTransformed
import com.github.panpf.sketch.extensions.test.intrinsicSize
import com.github.panpf.sketch.extensions.test.samplingByTarget
import com.github.panpf.sketch.fetch.AppIconUriFetcher.AppIconDataSource
import com.github.panpf.sketch.fetch.FetchResult
import com.github.panpf.sketch.fetch.newAppIconUri
import com.github.panpf.sketch.request.LoadRequest
import com.github.panpf.sketch.request.internal.RequestContext
import com.github.panpf.sketch.resize.Precision.LESS_PIXELS
import com.github.panpf.sketch.resize.Resize
import com.github.panpf.sketch.resize.Scale.CENTER_CROP
import com.github.panpf.sketch.sketch
import com.github.panpf.sketch.util.Size
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AppIconBitmapDecoderTest {

    @Test
    fun testFactory() {
        val context = InstrumentationRegistry.getInstrumentation().context
        val sketch = context.sketch
        val factory = AppIconBitmapDecoder.Factory()
        val packageName = context.packageName
        @Suppress("DEPRECATION") val versionCode =
            context.packageManager.getPackageInfo(context.packageName, 0).versionCode
        val testAppIconUri = newAppIconUri(packageName, versionCode)

        Assert.assertEquals("AppIconBitmapDecoder", factory.toString())

        // normal
        LoadRequest(context, testAppIconUri).let {
            val fetchResult = FetchResult(
                AppIconDataSource(sketch, it, LOCAL, packageName, versionCode),
                "application/vnd.android.app-icon"
            )
            factory.create(sketch, it, RequestContext(it), fetchResult)
        }.apply {
            Assert.assertNotNull(this)
        }

        // mimeType null
        LoadRequest(context, testAppIconUri).let {
            val fetchResult = FetchResult(
                AppIconDataSource(sketch, it, LOCAL, packageName, versionCode),
                null
            )
            factory.create(sketch, it, RequestContext(it), fetchResult)
        }.apply {
            Assert.assertNull(this)
        }
        LoadRequest(context, testAppIconUri).let {
            val fetchResult = FetchResult(
                AppIconDataSource(sketch, it, LOCAL, packageName, versionCode),
                "application/vnd.android.package-archive"
            )
            factory.create(sketch, it, RequestContext(it), fetchResult)
        }.apply {
            Assert.assertNull(this)
        }

        // dataSource error
        LoadRequest(context, testAppIconUri).let {
            val fetchResult = FetchResult(
                AssetDataSource(sketch, it, "sample.jpeg"),
                "application/vnd.android.app-icon"
            )
            factory.create(sketch, it, RequestContext(it), fetchResult)
        }.apply {
            Assert.assertNull(this)
        }
    }

    @Test
    fun testFactoryEqualsAndHashCode() {
        val element1 = AppIconBitmapDecoder.Factory()
        val element11 = AppIconBitmapDecoder.Factory()

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
        val factory = AppIconBitmapDecoder.Factory()
        val iconDrawable = context.applicationInfo.loadIcon(context.packageManager)
        val packageName = context.packageName
        @Suppress("DEPRECATION") val versionCode =
            context.packageManager.getPackageInfo(context.packageName, 0).versionCode
        val testAppIconUri = newAppIconUri(packageName, versionCode)

        LoadRequest(context, testAppIconUri).run {
            val fetchResult = FetchResult(
                AppIconDataSource(sketch, this, LOCAL, packageName, versionCode),
                "application/vnd.android.app-icon"
            )
            runBlocking {
                factory.create(sketch, this@run, RequestContext(this@run), fetchResult)!!.decode()
            }
        }.apply {
            Assert.assertEquals(
                "Bitmap(${iconDrawable.intrinsicWidth}x${iconDrawable.intrinsicHeight},ARGB_8888)",
                bitmap.toShortInfoString()
            )
            Assert.assertEquals(
                "ImageInfo(${iconDrawable.intrinsicWidth}x${iconDrawable.intrinsicHeight},'application/vnd.android.app-icon',UNDEFINED)",
                imageInfo.toShortString()
            )
            Assert.assertEquals(LOCAL, dataFrom)
            Assert.assertNull(transformedList)
        }

        LoadRequest(context, testAppIconUri) {
            bitmapConfig(RGB_565)
        }.run {
            val fetchResult = FetchResult(
                AppIconDataSource(sketch, this, LOCAL, packageName, versionCode),
                "application/vnd.android.app-icon"
            )
            runBlocking {
                factory.create(sketch, this@run, RequestContext(this@run), fetchResult)!!.decode()
            }
        }.apply {
            Assert.assertEquals(
                "Bitmap(${iconDrawable.intrinsicWidth}x${iconDrawable.intrinsicHeight},RGB_565)",
                bitmap.toShortInfoString()
            )
            Assert.assertEquals(
                "ImageInfo(${iconDrawable.intrinsicWidth}x${iconDrawable.intrinsicHeight},'application/vnd.android.app-icon',UNDEFINED)",
                imageInfo.toShortString()
            )
            Assert.assertEquals(LOCAL, dataFrom)
            Assert.assertNull(transformedList)
        }

        LoadRequest(context, testAppIconUri) {
            resize(100, 100, LESS_PIXELS)
        }.run {
            val fetchResult = FetchResult(
                AppIconDataSource(sketch, this, LOCAL, packageName, versionCode),
                "application/vnd.android.app-icon"
            )
            runBlocking {
                factory.create(sketch, this@run, RequestContext(this@run), fetchResult)!!.decode()
            }
        }.apply {
            val bitmapSize = samplingByTarget(iconDrawable.intrinsicSize, Size(100, 100))
            Assert.assertEquals(
                "Bitmap(${bitmapSize.height}x${bitmapSize.height},ARGB_8888)",
                bitmap.toShortInfoString()
            )
            Assert.assertEquals(
                "ImageInfo(${iconDrawable.intrinsicWidth}x${iconDrawable.intrinsicHeight},'application/vnd.android.app-icon',UNDEFINED)",
                imageInfo.toShortString()
            )
            Assert.assertEquals(LOCAL, dataFrom)
            Assert.assertEquals(
                listOf(createResizeTransformed(Resize(100, 100, LESS_PIXELS, CENTER_CROP))),
                transformedList
            )
        }
    }

    private fun Bitmap.toShortInfoString(): String = "Bitmap(${width}x${height},$config)"
}