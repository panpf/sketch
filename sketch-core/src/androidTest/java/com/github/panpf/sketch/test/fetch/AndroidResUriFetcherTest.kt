package com.github.panpf.sketch.test.fetch

import androidx.test.InstrumentationRegistry
import androidx.test.runner.AndroidJUnit4
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.core.test.R
import com.github.panpf.sketch.datasource.ContentDataSource
import com.github.panpf.sketch.fetch.AndroidResUriFetcher
import com.github.panpf.sketch.fetch.newAndroidResUriById
import com.github.panpf.sketch.fetch.newAndroidResUriByName
import com.github.panpf.sketch.request.DisplayRequest
import com.github.panpf.sketch.request.DownloadRequest
import com.github.panpf.sketch.request.LoadRequest
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AndroidResUriFetcherTest {

    @Test
    fun testNewUri() {
        Assert.assertEquals(
            "android.resource://testPackage/drawable/ic_launcher",
            newAndroidResUriByName("testPackage", "drawable", "ic_launcher").toString()
        )
        Assert.assertEquals(
            "android.resource://testPackage1/drawable1/ic_launcher1",
            newAndroidResUriByName("testPackage1", "drawable1", "ic_launcher1").toString()
        )

        Assert.assertEquals(
            "android.resource://testPackage/55345",
            newAndroidResUriById("testPackage", 55345).toString()
        )
        Assert.assertEquals(
            "android.resource://testPackage1/55346",
            newAndroidResUriById("testPackage1", 55346).toString()
        )
    }

    @Test
    fun testFactory() {
        val context = InstrumentationRegistry.getContext()
        val testAppPackage = context.packageName
        val sketch = Sketch.new(context)
        val fetcherFactory = AndroidResUriFetcher.Factory()
        val androidResUriByName = newAndroidResUriByName(testAppPackage, "drawable", "ic_launcher")
        val androidResUriById = newAndroidResUriById(testAppPackage, R.drawable.ic_launcher)
        val httpUri = "http://sample.com/sample.jpg"
        val contentUri = "content://sample_app/sample"

        fetcherFactory.create(sketch, LoadRequest.new(androidResUriByName))!!.apply {
            Assert.assertEquals(androidResUriByName.toString(), this.contentUri.toString())
        }
        fetcherFactory.create(sketch, LoadRequest.new(androidResUriById))!!.apply {
            Assert.assertEquals(androidResUriById.toString(), this.contentUri.toString())
        }
        fetcherFactory.create(sketch, DisplayRequest.new(androidResUriByName))!!.apply {
            Assert.assertEquals(androidResUriByName.toString(), this.contentUri.toString())
        }
        fetcherFactory.create(sketch, DisplayRequest.new(androidResUriById))!!.apply {
            Assert.assertEquals(androidResUriById.toString(), this.contentUri.toString())
        }
        Assert.assertNull(fetcherFactory.create(sketch, DownloadRequest.new(androidResUriByName)))
        Assert.assertNull(fetcherFactory.create(sketch, DownloadRequest.new(androidResUriById)))
        Assert.assertNull(fetcherFactory.create(sketch, LoadRequest.new(httpUri)))
        Assert.assertNull(fetcherFactory.create(sketch, LoadRequest.new(contentUri)))
    }

    @Test
    fun testFetch() {
        val context = InstrumentationRegistry.getContext()
        val sketch = Sketch.new(context)
        val testAppPackage = context.packageName
        val fetcherFactory = AndroidResUriFetcher.Factory()
        val androidResUriByName = newAndroidResUriByName(testAppPackage, "drawable", "ic_launcher")
        val androidResUriById = newAndroidResUriById(testAppPackage, R.drawable.ic_launcher)

        val fetcherByName = fetcherFactory.create(sketch, LoadRequest.new(androidResUriByName))!!
        val sourceByName = runBlocking {
            fetcherByName.fetch().dataSource
        }
        Assert.assertTrue(sourceByName is ContentDataSource)

        val fetcherById = fetcherFactory.create(sketch, LoadRequest.new(androidResUriById))!!
        val sourceById = runBlocking {
            fetcherById.fetch().dataSource
        }
        Assert.assertTrue(sourceById is ContentDataSource)
    }
}