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
import java.io.File

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

        Assert.assertEquals(
            "file:///sdcard/sample.jpg",
            newFileUri(File("/sdcard/sample.jpg"))
        )
        Assert.assertEquals(
            "file:///sdcard1/sample1.jpg",
            newFileUri(File("/sdcard1/sample1.jpg"))
        )
    }

    @Test
    fun testFactory() {
        val (context, sketch) = getTestContextAndNewSketch()
        val fileUri = "file:///sdcard/sample.jpg"
        val fileUri2 = "file:///sdcard/sample.png?from=bing"
        val fileUri3 = "file:///sdcard/sample.gif#/main/"
        val filePath = "/sdcard/sample.webp"
        val ftpUri = "ftp:///sample.com/sample.jpg"
        val contentUri = "content://sample_app/sample"

        val httpUriFetcherFactory = FileUriFetcher.Factory()
        httpUriFetcherFactory.create(sketch, LoadRequest(context, fileUri))!!.apply {
            Assert.assertEquals(fileUri, this.request.uriString)
            Assert.assertEquals("/sdcard/sample.jpg", this.file.path)
        }
        httpUriFetcherFactory.create(sketch, LoadRequest(context, fileUri2))!!.apply {
            Assert.assertEquals(fileUri2, this.request.uriString)
            Assert.assertEquals("/sdcard/sample.png", this.file.path)
        }
        httpUriFetcherFactory.create(sketch, LoadRequest(context, fileUri3))!!.apply {
            Assert.assertEquals(fileUri3, this.request.uriString)
            Assert.assertEquals("/sdcard/sample.gif", this.file.path)
        }
        httpUriFetcherFactory.create(sketch, LoadRequest(context, filePath))!!.apply {
            Assert.assertEquals(filePath, this.request.uriString)
            Assert.assertEquals("/sdcard/sample.webp", this.file.path)
        }

        httpUriFetcherFactory.create(sketch, DisplayRequest(context, fileUri))!!.apply {
            Assert.assertEquals(fileUri, this.request.uriString)
            Assert.assertEquals("/sdcard/sample.jpg", this.file.path)
        }
        httpUriFetcherFactory.create(sketch, DisplayRequest(context, fileUri2))!!.apply {
            Assert.assertEquals(fileUri2, this.request.uriString)
            Assert.assertEquals("/sdcard/sample.png", this.file.path)
        }
        httpUriFetcherFactory.create(sketch, DisplayRequest(context, fileUri3))!!.apply {
            Assert.assertEquals(fileUri3, this.request.uriString)
            Assert.assertEquals("/sdcard/sample.gif", this.file.path)
        }
        httpUriFetcherFactory.create(sketch, DisplayRequest(context, filePath))!!.apply {
            Assert.assertEquals(filePath, this.request.uriString)
            Assert.assertEquals("/sdcard/sample.webp", this.file.path)
        }

        httpUriFetcherFactory.create(sketch, DownloadRequest(context, fileUri))!!.apply {
            Assert.assertEquals(fileUri, this.request.uriString)
            Assert.assertEquals("/sdcard/sample.jpg", this.file.path)
        }
        httpUriFetcherFactory.create(sketch, DownloadRequest(context, fileUri2))!!.apply {
            Assert.assertEquals(fileUri2, this.request.uriString)
            Assert.assertEquals("/sdcard/sample.png", this.file.path)
        }
        httpUriFetcherFactory.create(sketch, DownloadRequest(context, fileUri3))!!.apply {
            Assert.assertEquals(fileUri3, this.request.uriString)
            Assert.assertEquals("/sdcard/sample.gif", this.file.path)
        }
        httpUriFetcherFactory.create(sketch, DownloadRequest(context, filePath))!!.apply {
            Assert.assertEquals(filePath, this.request.uriString)
            Assert.assertEquals("/sdcard/sample.webp", this.file.path)
        }

        Assert.assertNull(httpUriFetcherFactory.create(sketch, LoadRequest(context, ftpUri)))
        Assert.assertNull(httpUriFetcherFactory.create(sketch, LoadRequest(context, contentUri)))
    }

    @Test
    fun testFactoryEqualsAndHashCode() {
        val element1 = FileUriFetcher.Factory()
        val element11 = FileUriFetcher.Factory()

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
        val fetcherFactory = FileUriFetcher.Factory()
        val fileUri = "file:///sdcard/sample.jpg"

        val fetcher = fetcherFactory.create(sketch, LoadRequest(context, fileUri))!!
        val source = runBlocking {
            fetcher.fetch().dataSource
        }
        Assert.assertTrue(source is FileDataSource)
    }
}