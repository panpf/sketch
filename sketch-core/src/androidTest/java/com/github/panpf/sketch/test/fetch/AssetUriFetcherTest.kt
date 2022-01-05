package com.github.panpf.sketch.test.fetch

import androidx.test.InstrumentationRegistry
import androidx.test.runner.AndroidJUnit4
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.datasource.AssetsDataSource
import com.github.panpf.sketch.fetch.AssetUriFetcher
import com.github.panpf.sketch.fetch.newAssetUri
import com.github.panpf.sketch.request.DisplayRequest
import com.github.panpf.sketch.request.DownloadRequest
import com.github.panpf.sketch.request.LoadRequest
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AssetUriFetcherTest {

    @Test
    fun testNewUri() {
        Assert.assertEquals(
            "asset://fd5717876ab046b8aa889c9aaac4b56c.jpeg",
            newAssetUri("fd5717876ab046b8aa889c9aaac4b56c.jpeg").toString()
        )
        Assert.assertEquals(
            "asset://fd5717876ab046b8aa889c9aaac4b56c.png",
            newAssetUri("fd5717876ab046b8aa889c9aaac4b56c.png").toString()
        )
    }

    @Test
    fun testFactory() {
        val context = InstrumentationRegistry.getContext()
        val sketch = Sketch.new(context)
        val fetcherFactory = AssetUriFetcher.Factory()
        val assetUri = newAssetUri("fd5717876ab046b8aa889c9aaac4b56c.jpeg")
        val httpUri = "http://sample.com/sample.jpg"
        val contentUri = "content://sample_app/sample"

        fetcherFactory.create(sketch, LoadRequest.new(assetUri))!!.apply {
            Assert.assertEquals("fd5717876ab046b8aa889c9aaac4b56c.jpeg", assetFileName)
        }
        fetcherFactory.create(sketch, DisplayRequest.new(assetUri))!!.apply {
            Assert.assertEquals("fd5717876ab046b8aa889c9aaac4b56c.jpeg", assetFileName)
        }
        Assert.assertNull(fetcherFactory.create(sketch, DownloadRequest.new(assetUri)))
        Assert.assertNull(fetcherFactory.create(sketch, LoadRequest.new(httpUri)))
        Assert.assertNull(fetcherFactory.create(sketch, LoadRequest.new(contentUri)))
    }

    @Test
    fun testFetch() {
        val context = InstrumentationRegistry.getContext()
        val sketch = Sketch.new(context)
        val fetcherFactory = AssetUriFetcher.Factory()
        val assetUri = newAssetUri("fd5717876ab046b8aa889c9aaac4b56c.jpeg")

        val fetcher = fetcherFactory.create(sketch, LoadRequest.new(assetUri))!!
        val source = runBlocking {
            fetcher.fetch().source
        }
        Assert.assertTrue(source is AssetsDataSource)
    }
}