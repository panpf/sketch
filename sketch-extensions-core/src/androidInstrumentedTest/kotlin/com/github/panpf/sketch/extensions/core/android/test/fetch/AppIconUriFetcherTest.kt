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

package com.github.panpf.sketch.extensions.core.android.test.fetch

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.ComponentRegistry
import com.github.panpf.sketch.fetch.AppIconUriFetcher
import com.github.panpf.sketch.fetch.newAppIconUri
import com.github.panpf.sketch.fetch.supportAppIcon
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.UriInvalidException
import com.github.panpf.sketch.source.DataFrom
import com.github.panpf.sketch.source.DrawableDataSource
import com.github.panpf.sketch.test.singleton.getTestContextAndSketch
import com.github.panpf.sketch.test.utils.toRequestContext
import com.github.panpf.sketch.util.Size
import com.github.panpf.tools4j.test.ktx.assertThrow
import kotlinx.coroutines.runBlocking
import org.junit.runner.RunWith
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNotSame
import kotlin.test.assertNull

@RunWith(AndroidJUnit4::class)
class AppIconUriFetcherTest {

    @Test
    fun testSupportAppIcon() {
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

            supportAppIcon()
            build().apply {
                assertEquals(
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
                assertEquals(
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
        assertEquals(
            "app.icon://packageName/12412",
            newAppIconUri("packageName", 12412)
        )
        assertEquals(
            "app.icon://packageName1/12413",
            newAppIconUri("packageName1", 12413)
        )
    }

    @Test
    fun testIsAppIconUri() {
        // TODO test
    }

    @Test
    fun testFactory() {
        val (context, sketch) = getTestContextAndSketch()
        val fetcherFactory = AppIconUriFetcher.Factory()

        assertEquals("AppIconUriFetcher", fetcherFactory.toString())

        fetcherFactory.create(
            ImageRequest(context, "app.icon://packageName1/12412")
                .toRequestContext(sketch, Size.Empty)
        )!!
            .apply {
                assertEquals("packageName1", packageName)
                assertEquals(12412, versionCode)
            }
        fetcherFactory.create(
            ImageRequest(context, "app.icon://packageName1/12412/87467")
                .toRequestContext(sketch, Size.Empty)
        )!!
            .apply {
                assertEquals("packageName1", packageName)
                assertEquals(12412, versionCode)
            }

        assertNull(
            fetcherFactory.create(
                ImageRequest(context, "content://sample_app/sample")
                    .toRequestContext(sketch, Size.Empty)
            )
        )

        assertThrow(UriInvalidException::class) {
            fetcherFactory.create(
                ImageRequest(context, "app.icon:///12412")
                    .toRequestContext(sketch, Size.Empty)
            )
        }
        assertThrow(UriInvalidException::class) {
            fetcherFactory.create(
                ImageRequest(context, "app.icon:// /12412")
                    .toRequestContext(sketch, Size.Empty)
            )
        }
        assertThrow(UriInvalidException::class) {
            fetcherFactory.create(
                ImageRequest(context, "app.icon://packageName1/")
                    .toRequestContext(sketch, Size.Empty)
            )
        }
        assertThrow(UriInvalidException::class) {
            fetcherFactory.create(
                ImageRequest(context, "app.icon://packageName1/ ")
                    .toRequestContext(sketch, Size.Empty)
            )
        }
        assertThrow(UriInvalidException::class) {
            fetcherFactory.create(
                ImageRequest(context, "app.icon://packageName1/errorCode")
                    .toRequestContext(sketch, Size.Empty)
            )
        }
    }

    @Test
    fun testFactoryEqualsAndHashCode() {
        val element1 = AppIconUriFetcher.Factory()
        val element11 = AppIconUriFetcher.Factory()

        assertNotSame(element1, element11)

        assertEquals(element1, element1)
        assertEquals(element1, element11)

        assertNotEquals(element1, Any())
        assertNotEquals(element1, null as Any?)

        assertEquals(element1.hashCode(), element1.hashCode())
        assertEquals(element1.hashCode(), element11.hashCode())
    }

    @Test
    fun testFetch() {
        val (context, sketch) = getTestContextAndSketch()
        val fetcherFactory = AppIconUriFetcher.Factory()

        val packageName = context.packageName

        @Suppress("DEPRECATION")
        val versionCode = context.packageManager.getPackageInfo(packageName, 0).versionCode
        val appIconUri = newAppIconUri(packageName, versionCode)

        val fetcher = fetcherFactory.create(
            ImageRequest(context, appIconUri)
                .toRequestContext(sketch, Size.Empty)
        )!!
        (runBlocking { fetcher.fetch() }.getOrThrow().dataSource as DrawableDataSource).apply {
            assertEquals(DataFrom.LOCAL, dataFrom)

            assertEquals(
                "AppIconDrawableFetcher(packageName='$packageName',versionCode=$versionCode)",
                drawableFetcher.toString()
            )
        }
    }

    // TODO test
}