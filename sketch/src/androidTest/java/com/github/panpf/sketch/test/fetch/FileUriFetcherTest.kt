package com.github.panpf.sketch.test.fetch

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.datasource.FileDataSource
import com.github.panpf.sketch.fetch.FileUriFetcher
import com.github.panpf.sketch.fetch.newFileUri
import com.github.panpf.sketch.request.DisplayRequest
import com.github.panpf.sketch.request.DownloadRequest
import com.github.panpf.sketch.request.LoadRequest
import com.github.panpf.sketch.test.utils.getTestContextAndNewSketch
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
            newFileUri("/sdcard/sample.jpg")
        )
        Assert.assertEquals(
            "file:///sdcard1/sample1.jpg",
            newFileUri("/sdcard1/sample1.jpg")
        )
    }

    @Test
    fun testFactory() {
        val (context, sketch) = getTestContextAndNewSketch()
        val fileUri = "file:///sdcard/sample.jpg"
        val filePath = "/sdcard/sample.jpg"
        val ftpUri = "ftp:///sample.com/sample.jpg"
        val contentUri = "content://sample_app/sample"

        val httpUriFetcherFactory = FileUriFetcher.Factory()
        httpUriFetcherFactory.create(sketch, LoadRequest(context, fileUri))!!.apply {
            Assert.assertEquals("/sdcard/sample.jpg", this.file.path)
        }
        httpUriFetcherFactory.create(sketch, LoadRequest(context, filePath))!!.apply {
            Assert.assertEquals("/sdcard/sample.jpg", this.file.path)
        }
        httpUriFetcherFactory.create(sketch, DisplayRequest(context, fileUri))!!.apply {
            Assert.assertEquals("/sdcard/sample.jpg", this.file.path)
        }
        httpUriFetcherFactory.create(sketch, DisplayRequest(context, filePath))!!.apply {
            Assert.assertEquals("/sdcard/sample.jpg", this.file.path)
        }
        httpUriFetcherFactory.create(sketch, DownloadRequest(context, fileUri))!!.apply {
            Assert.assertEquals("/sdcard/sample.jpg", this.file.path)
        }
        httpUriFetcherFactory.create(sketch, DownloadRequest(context, filePath))!!.apply {
            Assert.assertEquals("/sdcard/sample.jpg", this.file.path)
        }
        Assert.assertNull(httpUriFetcherFactory.create(sketch, LoadRequest(context, ftpUri)))
        Assert.assertNull(httpUriFetcherFactory.create(sketch, LoadRequest(context, contentUri)))
    }

    @Test
    fun testFetch() {
        val (context, sketch) = getTestContextAndNewSketch()
        val fetcherFactory = FileUriFetcher.Factory()
        val fileUri = "file:///sdcard/sample.jpg"

        val fetcher = fetcherFactory.create(sketch, LoadRequest(context, fileUri))!!
        val source = runBlocking {
            fetcher.fetch().dataSource
        }
        Assert.assertTrue(source is FileDataSource)
    }
}