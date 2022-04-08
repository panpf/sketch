package com.github.panpf.sketch.test.fetch

import android.widget.ImageView
import androidx.test.InstrumentationRegistry
import androidx.test.runner.AndroidJUnit4
import com.github.panpf.sketch.datasource.ResourceDataSource
import com.github.panpf.sketch.fetch.ResourceUriFetcher
import com.github.panpf.sketch.fetch.newResourceUri
import com.github.panpf.sketch.request.DisplayRequest
import com.github.panpf.sketch.request.DownloadRequest
import com.github.panpf.sketch.request.LoadRequest
import com.github.panpf.sketch.sketch
import com.github.panpf.sketch.test.R.drawable
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ResourceUriFetcherTest {

    @Test
    fun testNewUri() {
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
    }

    @Test
    fun testFactory() {
        val context = InstrumentationRegistry.getContext()
        val testAppPackage = context.packageName
        val sketch = context.sketch
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
    fun testFetch() {
        val context = InstrumentationRegistry.getContext()
        val sketch = context.sketch
        val testAppPackage = context.packageName
        val fetcherFactory = ResourceUriFetcher.Factory()
        val androidResUriByName =
            newResourceUri(testAppPackage, "drawable", "ic_launcher")
        val androidResUriById = newResourceUri(testAppPackage, drawable.ic_launcher)

        val fetcherByName =
            fetcherFactory.create(sketch, LoadRequest(context, androidResUriByName))!!
        val sourceByName = runBlocking {
            fetcherByName.fetch().dataSource
        }
        Assert.assertTrue(sourceByName is ResourceDataSource)

        val fetcherById = fetcherFactory.create(sketch, LoadRequest(context, androidResUriById))!!
        val sourceById = runBlocking {
            fetcherById.fetch().dataSource
        }
        Assert.assertTrue(sourceById is ResourceDataSource)
    }
}