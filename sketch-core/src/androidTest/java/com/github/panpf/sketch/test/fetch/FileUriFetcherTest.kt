package com.github.panpf.sketch.test.fetch

import androidx.test.InstrumentationRegistry
import androidx.test.runner.AndroidJUnit4
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.datasource.ContentDataSource
import com.github.panpf.sketch.fetch.FileUriFetcher
import com.github.panpf.sketch.fetch.newFileUri
import com.github.panpf.sketch.request.DisplayRequest
import com.github.panpf.sketch.request.DownloadRequest
import com.github.panpf.sketch.request.LoadRequest
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FileUriFetcherTest {

    @Test
    fun testNewUri() {
        Assert.assertEquals(
            "file:///sdcard/sample.jpg",
            newFileUri("/sdcard/sample.jpg").toString()
        )
        Assert.assertEquals(
            "file:///sdcard1/sample1.jpg",
            newFileUri("/sdcard1/sample1.jpg").toString()
        )
    }

    @Test
    fun testFactory() {
        val context = InstrumentationRegistry.getContext()
        val sketch = Sketch.new(context)
        val fileUri = "file:///sdcard/sample.jpg"
        val filePath = "/sdcard/sample.jpg"
        val ftpUri = "ftp:///sample.com/sample.jpg"
        val contentUri = "content://sample_app/sample"

        val httpUriFetcherFactory = FileUriFetcher.Factory()
        httpUriFetcherFactory.create(sketch, LoadRequest.new(fileUri))!!.apply {
            Assert.assertEquals("file:///sdcard/sample.jpg", this.fileUri.toString())
        }
        httpUriFetcherFactory.create(sketch, LoadRequest.new(filePath))!!.apply {
            Assert.assertEquals("file:///sdcard/sample.jpg", this.fileUri.toString())
        }
        httpUriFetcherFactory.create(sketch, DisplayRequest.new(fileUri))!!.apply {
            Assert.assertEquals("file:///sdcard/sample.jpg", this.fileUri.toString())
        }
        httpUriFetcherFactory.create(sketch, DisplayRequest.new(filePath))!!.apply {
            Assert.assertEquals("file:///sdcard/sample.jpg", this.fileUri.toString())
        }
        Assert.assertNull(httpUriFetcherFactory.create(sketch, DownloadRequest.new(fileUri)))
        Assert.assertNull(httpUriFetcherFactory.create(sketch, DownloadRequest.new(filePath)))
        Assert.assertNull(httpUriFetcherFactory.create(sketch, LoadRequest.new(ftpUri)))
        Assert.assertNull(httpUriFetcherFactory.create(sketch, LoadRequest.new(contentUri)))
    }

    @Test
    fun testFetch() {
        val context = InstrumentationRegistry.getContext()
        val sketch = Sketch.new(context)
        val fetcherFactory = FileUriFetcher.Factory()
        val fileUri = "file:///sdcard/sample.jpg"

        val fetcher = fetcherFactory.create(sketch, LoadRequest.new(fileUri))!!
        val source = runBlocking {
            fetcher.fetch().dataSource
        }
        Assert.assertTrue(source is ContentDataSource)
    }
}