package com.github.panpf.sketch.extensions.appicon.test.fetch

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.ComponentRegistry
import com.github.panpf.sketch.fetch.AppIconUriFetcher
import com.github.panpf.sketch.fetch.isAppIconUri
import com.github.panpf.sketch.fetch.newAppIconUri
import com.github.panpf.sketch.fetch.supportAppIcon
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.UriInvalidException
import com.github.panpf.sketch.source.DataFrom
import com.github.panpf.sketch.source.DrawableDataSource
import com.github.panpf.sketch.test.singleton.getTestContextAndSketch
import com.github.panpf.sketch.test.utils.getTestContext
import com.github.panpf.sketch.test.utils.toRequestContext
import com.github.panpf.sketch.util.Size
import com.github.panpf.sketch.util.toUri
import kotlinx.coroutines.test.runTest
import org.junit.runner.RunWith
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

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
                            "requestInterceptorList=[]" +
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
                            "requestInterceptorList=[]" +
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
                            "requestInterceptorList=[]" +
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
        assertFalse(actual = isAppIconUri("app.icon1://packageName1/12413".toUri()))
        assertTrue(actual = isAppIconUri("app.icon://packageName1/12413".toUri()))
    }

    @Test
    fun testCompanion() {
        assertEquals("app.icon", AppIconUriFetcher.SCHEME)
        assertEquals("image/png", AppIconUriFetcher.IMAGE_MIME_TYPE)
    }

    @Test
    fun testFetch() = runTest {
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
        (fetcher.fetch().getOrThrow().dataSource as DrawableDataSource).apply {
            assertEquals(DataFrom.LOCAL, dataFrom)

            assertEquals(
                "AppIconDrawableFetcher(packageName='$packageName', versionCode=$versionCode)",
                drawableFetcher.toString()
            )
        }
    }

    @Test
    fun testEqualsAndHashCode() {
        val context = getTestContext()
        val element1 = AppIconUriFetcher(context, "com.github.panpf.sketch.sample", 1001)
        val element11 = AppIconUriFetcher(context, "com.github.panpf.sketch.sample", 1001)
        val element2 = AppIconUriFetcher(context, "com.github.panpf.sketch.sample1", 1001)
        val element3 = AppIconUriFetcher(context, "com.github.panpf.sketch.sample", 1002)

        assertEquals(element1, element11)
        assertNotEquals(element1, element2)
        assertNotEquals(element1, element3)
        assertNotEquals(element2, element3)
        assertNotEquals(element1, Any())
        assertNotEquals(element1, null as Any?)

        assertEquals(element1.hashCode(), element11.hashCode())
        assertNotEquals(element1.hashCode(), element2.hashCode())
        assertNotEquals(element1.hashCode(), element3.hashCode())
        assertNotEquals(element2.hashCode(), element3.hashCode())
    }

    @Test
    fun testToString() {
        val context = getTestContext()
        assertEquals(
            expected = "AppIconUriFetcher(packageName='com.github.panpf.sketch.sample', versionCode=1001)",
            actual = AppIconUriFetcher(context, "com.github.panpf.sketch.sample", 1001).toString()
        )
    }

    @Test
    fun testFactoryCreate() {
        val (context, sketch) = getTestContextAndSketch()
        val fetcherFactory = AppIconUriFetcher.Factory()

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

        assertFailsWith(UriInvalidException::class) {
            fetcherFactory.create(
                ImageRequest(context, "app.icon:///12412")
                    .toRequestContext(sketch, Size.Empty)
            )
        }
        assertFailsWith(UriInvalidException::class) {
            fetcherFactory.create(
                ImageRequest(context, "app.icon:// /12412")
                    .toRequestContext(sketch, Size.Empty)
            )
        }
        assertFailsWith(UriInvalidException::class) {
            fetcherFactory.create(
                ImageRequest(context, "app.icon://packageName1/")
                    .toRequestContext(sketch, Size.Empty)
            )
        }
        assertFailsWith(UriInvalidException::class) {
            fetcherFactory.create(
                ImageRequest(context, "app.icon://packageName1/ ")
                    .toRequestContext(sketch, Size.Empty)
            )
        }
        assertFailsWith(UriInvalidException::class) {
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


        assertEquals(element1, element11)

        assertNotEquals(element1, Any())
        assertNotEquals(element1, null as Any?)

        assertEquals(element1.hashCode(), element11.hashCode())
    }

    @Test
    fun testFactoryToString() {
        assertEquals(
            expected = "AppIconUriFetcher",
            actual = AppIconUriFetcher.Factory().toString()
        )
    }
}