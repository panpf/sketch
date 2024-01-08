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
@file:Suppress("DEPRECATION")

package com.github.panpf.sketch.extensions.core.test.decode

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.github.panpf.sketch.datasource.AssetDataSource
import com.github.panpf.sketch.datasource.DataFrom.LOCAL
import com.github.panpf.sketch.datasource.DrawableDataSource
import com.github.panpf.sketch.decode.AppIconBitmapDecoder
import com.github.panpf.sketch.fetch.AppIconUriFetcher
import com.github.panpf.sketch.fetch.AppIconUriFetcher.AppIconDrawableFetcher
import com.github.panpf.sketch.fetch.FetchResult
import com.github.panpf.sketch.fetch.newAppIconUri
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.resources.AssetImages
import com.github.panpf.sketch.test.singleton.sketch
import com.github.panpf.sketch.test.utils.toRequestContext
import com.github.panpf.tools4j.test.ktx.assertThrow
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
        ImageRequest(context, testAppIconUri).let {
            val fetchResult = FetchResult(
                dataSource = DrawableDataSource(
                    sketch = sketch,
                    request = it,
                    dataFrom = LOCAL,
                    drawableFetcher = AppIconDrawableFetcher(packageName, versionCode),
                ),
                mimeType = AppIconUriFetcher.IMAGE_MIME_TYPE
            )
            factory.create(sketch, it.toRequestContext(sketch), fetchResult)
        }.apply {
            Assert.assertNull(this)
        }

        // mimeType null
        ImageRequest(context, testAppIconUri).let {
            val fetchResult = FetchResult(
                DrawableDataSource(
                    sketch = sketch,
                    request = it,
                    dataFrom = LOCAL,
                    drawableFetcher = AppIconDrawableFetcher(packageName, versionCode)
                ),
                AppIconUriFetcher.IMAGE_MIME_TYPE
            )
            factory.create(sketch, it.toRequestContext(sketch), fetchResult)
        }.apply {
            Assert.assertNull(this)
        }
        ImageRequest(context, testAppIconUri).let {
            val fetchResult = FetchResult(
                DrawableDataSource(
                    sketch = sketch,
                    request = it,
                    dataFrom = LOCAL,
                    drawableFetcher = AppIconDrawableFetcher(packageName, versionCode)
                ),
                AppIconUriFetcher.IMAGE_MIME_TYPE
            )
            factory.create(sketch, it.toRequestContext(sketch), fetchResult)
        }.apply {
            Assert.assertNull(this)
        }

        // dataSource error
        ImageRequest(context, testAppIconUri).let {
            val fetchResult = FetchResult(
                AssetDataSource(sketch, it, AssetImages.jpeg.fileName),
                "application/vnd.android.app-icon"
            )
            factory.create(sketch, it.toRequestContext(sketch), fetchResult)
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
        assertThrow(UnsupportedOperationException::class) {
            runBlocking {
                AppIconBitmapDecoder().decode()
            }.getOrThrow()
        }
    }
}