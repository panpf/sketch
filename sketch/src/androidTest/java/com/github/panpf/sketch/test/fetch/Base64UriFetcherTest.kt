package com.github.panpf.sketch.test.fetch

import android.util.Base64
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.datasource.ByteArrayDataSource
import com.github.panpf.sketch.fetch.Base64UriFetcher
import com.github.panpf.sketch.fetch.newBase64Uri
import com.github.panpf.sketch.request.DisplayRequest
import com.github.panpf.sketch.request.DownloadRequest
import com.github.panpf.sketch.request.LoadRequest
import com.github.panpf.sketch.request.internal.UriInvalidException
import com.github.panpf.sketch.test.utils.getTestContextAndNewSketch
import com.github.panpf.tools4j.test.ktx.assertThrow
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class Base64UriFetcherTest {

    @Test
    fun testNewUri() {
        Assert.assertEquals(
            "data:image/png;base64,4y2u1412421089084901240129",
            newBase64Uri("image/png", "4y2u1412421089084901240129")
        )
        Assert.assertEquals(
            "data:image/jpeg;base64,4y2u1412421089084901240128",
            newBase64Uri("image/jpeg", "4y2u1412421089084901240128")
        )
    }

    @Test
    fun testFactory() {
        val (context, sketch) = getTestContextAndNewSketch()
        val fetcherFactory = Base64UriFetcher.Factory()
        val base64Uri = "data:image/png;base64,4y2u1412421089084901240129"
        val contentUri = "content://sample_app/sample"

        fetcherFactory.create(sketch, LoadRequest(context, base64Uri))!!.apply {
            Assert.assertEquals("image/png", mimeType)
            Assert.assertEquals("4y2u1412421089084901240129", imageDataBase64StringLazy.value)
        }
        fetcherFactory.create(sketch, DisplayRequest(context, base64Uri))!!.apply {
            Assert.assertEquals("image/png", mimeType)
            Assert.assertEquals("4y2u1412421089084901240129", imageDataBase64StringLazy.value)
        }
        fetcherFactory.create(sketch, DownloadRequest(context, base64Uri))!!.apply {
            Assert.assertEquals("image/png", mimeType)
            Assert.assertEquals("4y2u1412421089084901240129", imageDataBase64StringLazy.value)
        }
        Assert.assertNull(fetcherFactory.create(sketch, LoadRequest(context, contentUri)))

        assertThrow(UriInvalidException::class) {
            fetcherFactory.create(sketch, DownloadRequest(context, "data:image/pngbase64,4y2u1412421089084901240129"))
        }
        assertThrow(UriInvalidException::class) {
            fetcherFactory.create(sketch, DownloadRequest(context, "data:image/png;base54,4y2u1412421089084901240129"))
        }
    }

    @Test
    fun testFactoryEqualsAndHashCode() {
        val element1 = Base64UriFetcher.Factory()
        val element11 = Base64UriFetcher.Factory()

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
        val fetcherFactory = Base64UriFetcher.Factory()
        val imageData = "4y2u1412421089084901240129".toByteArray()
        val base64Uri = "data:image/png;base64,${Base64.encodeToString(imageData, Base64.DEFAULT)}"

        val fetcher = fetcherFactory.create(sketch, LoadRequest(context, base64Uri))!!
        val source = runBlocking {
            fetcher.fetch().dataSource
        }
        Assert.assertEquals(imageData.size.toLong(), source.length())
        Assert.assertTrue(source is ByteArrayDataSource)
    }
}