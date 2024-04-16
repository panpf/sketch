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
package com.github.panpf.sketch.extensions.core.test.fetch

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.ComponentRegistry
import com.github.panpf.sketch.source.DataFrom
import com.github.panpf.sketch.source.DrawableDataSource
import com.github.panpf.sketch.fetch.AppIconUriFetcher
import com.github.panpf.sketch.fetch.newAppIconUri
import com.github.panpf.sketch.fetch.supportAppIcon
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.UriInvalidException
import com.github.panpf.sketch.test.utils.getTestContextAndNewSketch
import com.github.panpf.tools4j.test.ktx.assertThrow
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AppIconUriFetcherTest {

    @Test
    fun testSupportAppIcon() {
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

            supportAppIcon()
            build().apply {
                Assert.assertEquals(
                    "ComponentRegistry(" +
                            "fetcherFactoryList=[AppIconUriFetcher]," +
                            "decoderFactoryList=[]," +
                            "requestInterceptorList=[]," +
                            "decodeInterceptorList=[]" +
                            ")",
                    toString()
                )
            }

            supportAppIcon()
            build().apply {
                Assert.assertEquals(
                    "ComponentRegistry(" +
                            "fetcherFactoryList=[AppIconUriFetcher,AppIconUriFetcher]," +
                            "decoderFactoryList=[]," +
                            "requestInterceptorList=[]," +
                            "decodeInterceptorList=[]" +
                            ")",
                    toString()
                )
            }
        }
    }

    @Test
    fun testNewAppIconUri() {
        Assert.assertEquals(
            "app.icon://packageName/12412",
            newAppIconUri("packageName", 12412)
        )
        Assert.assertEquals(
            "app.icon://packageName1/12413",
            newAppIconUri("packageName1", 12413)
        )
    }

    @Test
    fun testFactory() {
        val (context, sketch) = getTestContextAndNewSketch()
        val fetcherFactory = AppIconUriFetcher.Factory()

        Assert.assertEquals("AppIconUriFetcher", fetcherFactory.toString())

        fetcherFactory.create(sketch, ImageRequest(context, "app.icon://packageName1/12412"))!!
            .apply {
                Assert.assertEquals("packageName1", packageName)
                Assert.assertEquals(12412, versionCode)
            }
        fetcherFactory.create(sketch, ImageRequest(context, "app.icon://packageName1/12412"))!!
            .apply {
                Assert.assertEquals("packageName1", packageName)
                Assert.assertEquals(12412, versionCode)
            }
        fetcherFactory.create(sketch, ImageRequest(context, "app.icon://packageName1/12412"))!!
            .apply {
                Assert.assertEquals("packageName1", packageName)
                Assert.assertEquals(12412, versionCode)
            }

        Assert.assertNull(
            fetcherFactory.create(
                sketch,
                ImageRequest(context, "content://sample_app/sample")
            )
        )

        assertThrow(UriInvalidException::class) {
            fetcherFactory.create(sketch, ImageRequest(context, "app.icon:///12412"))
        }
        assertThrow(UriInvalidException::class) {
            fetcherFactory.create(sketch, ImageRequest(context, "app.icon:// /12412"))
        }
        assertThrow(UriInvalidException::class) {
            fetcherFactory.create(sketch, ImageRequest(context, "app.icon://packageName1/"))
        }
        assertThrow(UriInvalidException::class) {
            fetcherFactory.create(sketch, ImageRequest(context, "app.icon://packageName1/ "))
        }
        assertThrow(UriInvalidException::class) {
            fetcherFactory.create(
                sketch,
                ImageRequest(context, "app.icon://packageName1/errorCode")
            )
        }
    }

    @Test
    fun testFactoryEqualsAndHashCode() {
        val element1 = AppIconUriFetcher.Factory()
        val element11 = AppIconUriFetcher.Factory()

        Assert.assertNotSame(element1, element11)

        Assert.assertEquals(element1, element1)
        Assert.assertEquals(element1, element11)

        Assert.assertNotEquals(element1, Any())
        Assert.assertNotEquals(element1, null)

        Assert.assertEquals(element1.hashCode(), element1.hashCode())
        Assert.assertEquals(element1.hashCode(), element11.hashCode())
    }

    @Test
    fun testFetch() {
        val (context, sketch) = getTestContextAndNewSketch()
        val fetcherFactory = AppIconUriFetcher.Factory()

        val packageName = context.packageName

        @Suppress("DEPRECATION")
        val versionCode = context.packageManager.getPackageInfo(packageName, 0).versionCode
        val appIconUri = newAppIconUri(packageName, versionCode)

        val fetcher = fetcherFactory.create(sketch, ImageRequest(context, appIconUri))!!
        (runBlocking { fetcher.fetch() }.getOrThrow().dataSource as DrawableDataSource).apply {
            Assert.assertEquals(DataFrom.LOCAL, dataFrom)

            Assert.assertEquals(
                "AppIconDrawableFetcher(packageName='$packageName',versionCode=$versionCode)",
                drawableFetcher.toString()
            )
        }
    }
}