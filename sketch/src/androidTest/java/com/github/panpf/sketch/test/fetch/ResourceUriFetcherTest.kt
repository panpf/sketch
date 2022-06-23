package com.github.panpf.sketch.test.fetch

import android.net.Uri
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.datasource.ResourceDataSource
import com.github.panpf.sketch.fetch.ResourceUriFetcher
import com.github.panpf.sketch.fetch.newResourceUri
import com.github.panpf.sketch.request.DisplayRequest
import com.github.panpf.sketch.request.DownloadRequest
import com.github.panpf.sketch.request.LoadRequest
import com.github.panpf.sketch.test.R.drawable
import com.github.panpf.sketch.test.utils.getTestContext
import com.github.panpf.sketch.test.utils.getTestContextAndNewSketch
import com.github.panpf.tools4j.test.ktx.assertThrow
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import java.io.FileNotFoundException

@RunWith(AndroidJUnit4::class)
class ResourceUriFetcherTest {

    @Test
    fun testNewUri() {
        val context = getTestContext()

        Assert.assertEquals(
            "android.resource://testPackage/drawable/ic_launcher",
            newResourceUri("testPackage", "drawable", "ic_launcher")
        )
        Assert.assertEquals(
            "android.resource://testPackage1/drawable1/ic_launcher1",
            newResourceUri("testPackage1", "drawable1", "ic_launcher1")
        )

        Assert.assertEquals(
            "android.resource://testPackage/55345",
            newResourceUri("testPackage", 55345)
        )
        Assert.assertEquals(
            "android.resource://testPackage1/55346",
            newResourceUri("testPackage1", 55346)
        )

        Assert.assertEquals(
            "android.resource://${context.packageName}/drawable/ic_launcher",
            context.newResourceUri("drawable", "ic_launcher")
        )
        Assert.assertEquals(
            "android.resource://${context.packageName}/drawable1/ic_launcher1",
            context.newResourceUri("drawable1", "ic_launcher1")
        )

        Assert.assertEquals(
            "android.resource://${context.packageName}/55345",
            context.newResourceUri(55345)
        )
        Assert.assertEquals(
            "android.resource://${context.packageName}/55346",
            context.newResourceUri(55346)
        )
    }

    @Test
    fun testFactory() {
        val (context, sketch) = getTestContextAndNewSketch()
        val testAppPackage = context.packageName
        val fetcherFactory = ResourceUriFetcher.Factory()
        val androidResUriByName =
            newResourceUri(testAppPackage, "drawable", "ic_launcher")
        val androidResUriById = newResourceUri(testAppPackage, drawable.ic_launcher)
        val httpUri = "http://sample.com/sample.jpg"
        val contentUri = "content://sample_app/sample"

        fetcherFactory.create(sketch, LoadRequest(context, androidResUriByName))!!.apply {
            Assert.assertEquals(androidResUriByName, this.contentUri.toString())
        }
        fetcherFactory.create(sketch, LoadRequest(context, androidResUriById))!!.apply {
            Assert.assertEquals(androidResUriById, this.contentUri.toString())
        }
        fetcherFactory.create(sketch, DisplayRequest(context, androidResUriByName))!!.apply {
            Assert.assertEquals(androidResUriByName, this.contentUri.toString())
        }
        fetcherFactory.create(sketch, DisplayRequest(context, androidResUriById))!!.apply {
            Assert.assertEquals(androidResUriById, this.contentUri.toString())
        }
        fetcherFactory.create(sketch, DownloadRequest(context, androidResUriByName))!!.apply {
            Assert.assertEquals(androidResUriByName, this.contentUri.toString())
        }
        fetcherFactory.create(sketch, DownloadRequest(context, androidResUriById))!!.apply {
            Assert.assertEquals(androidResUriById, this.contentUri.toString())
        }
        Assert.assertNull(fetcherFactory.create(sketch, LoadRequest(context, httpUri)))
        Assert.assertNull(fetcherFactory.create(sketch, LoadRequest(context, contentUri)))
    }

    @Test
    fun testFactoryEqualsAndHashCode() {
        val element1 = ResourceUriFetcher.Factory()
        val element11 = ResourceUriFetcher.Factory()

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
        val testAppPackage = context.packageName
        val fetcherFactory = ResourceUriFetcher.Factory()

        val androidResUriByName =
            newResourceUri(testAppPackage, "drawable", "ic_launcher")
        val fetcherByName =
            fetcherFactory.create(sketch, LoadRequest(context, androidResUriByName))!!
        val sourceByName = runBlocking {
            fetcherByName.fetch().dataSource
        }
        Assert.assertTrue(sourceByName is ResourceDataSource)

        val androidResUriById = newResourceUri(testAppPackage, drawable.ic_launcher)
        val fetcherById = fetcherFactory.create(sketch, LoadRequest(context, androidResUriById))!!
        val sourceById = runBlocking {
            fetcherById.fetch().dataSource
        }
        Assert.assertTrue(sourceById is ResourceDataSource)

        assertThrow(FileNotFoundException::class) {
            runBlocking {
                ResourceUriFetcher(
                    sketch,
                    LoadRequest(context, androidResUriByName),
                    Uri.parse("${ResourceUriFetcher.SCHEME}://")
                ).fetch()
            }
        }

        assertThrow(FileNotFoundException::class) {
            runBlocking {
                ResourceUriFetcher(
                    sketch,
                    LoadRequest(context, androidResUriByName),
                    Uri.parse("${ResourceUriFetcher.SCHEME}://fakePackageName")
                ).fetch()
            }
        }

        assertThrow(FileNotFoundException::class) {
            runBlocking {
                ResourceUriFetcher(
                    sketch,
                    LoadRequest(context, androidResUriByName),
                    Uri.parse("${ResourceUriFetcher.SCHEME}://${context.packageName}/errorResId")
                ).fetch()
            }
        }

        assertThrow(FileNotFoundException::class) {
            runBlocking {
                ResourceUriFetcher(
                    sketch,
                    LoadRequest(context, androidResUriByName),
                    Uri.parse("${ResourceUriFetcher.SCHEME}://${context.packageName}/drawable/34/error")
                ).fetch()
            }
        }

        assertThrow(FileNotFoundException::class) {
            runBlocking {
                ResourceUriFetcher(
                    sketch,
                    LoadRequest(context, androidResUriByName),
                    Uri.parse("${ResourceUriFetcher.SCHEME}://${context.packageName}/drawable/0")
                ).fetch()
            }
        }
    }
}