package com.github.panpf.sketch.test.fetch

import android.widget.ImageView
import androidx.test.InstrumentationRegistry
import androidx.test.runner.AndroidJUnit4
import com.github.panpf.sketch.datasource.ContentDataSource
import com.github.panpf.sketch.fetch.ContentUriFetcher
import com.github.panpf.sketch.request.DisplayRequest
import com.github.panpf.sketch.request.DownloadRequest
import com.github.panpf.sketch.request.LoadRequest
import com.github.panpf.sketch.sketch
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ContentUriFetcherTest {

    @Test
    fun testFactory() {
        val context = InstrumentationRegistry.getContext()
        val sketch = context.sketch
        val fetcherFactory = ContentUriFetcher.Factory()
        val contentUri = "content://sample_app/sample"
        val httpUri = "http://sample.com/sample.jpg"

        fetcherFactory.create(sketch, LoadRequest(context, contentUri))!!.apply {
            Assert.assertEquals(contentUri, this.contentUri.toString())
        }
        fetcherFactory.create(sketch, DisplayRequest(context, contentUri))!!.apply {
            Assert.assertEquals(contentUri, this.contentUri.toString())
        }
        fetcherFactory.create(sketch, DownloadRequest(context, contentUri))!!.apply {
            Assert.assertEquals(contentUri, this.contentUri.toString())
        }
        Assert.assertNull(fetcherFactory.create(sketch, LoadRequest(context, httpUri)))
    }

    @Test
    fun testFetch() {
        val context = InstrumentationRegistry.getContext()
        val sketch = context.sketch
        val fetcherFactory = ContentUriFetcher.Factory()
        val contentUri = "content://sample_app/sample"

        val fetcher = fetcherFactory.create(sketch, LoadRequest(context, contentUri))!!
        val source = runBlocking {
            fetcher.fetch().dataSource
        }
        Assert.assertTrue(source is ContentDataSource)
    }
}