package com.github.panpf.sketch.test.fetch

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.datasource.AssetDataSource
import com.github.panpf.sketch.fetch.AssetUriFetcher
import com.github.panpf.sketch.fetch.newAssetUri
import com.github.panpf.sketch.request.DisplayRequest
import com.github.panpf.sketch.request.DownloadRequest
import com.github.panpf.sketch.request.LoadRequest
import com.github.panpf.sketch.test.utils.getContextAndSketch
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AssetUriFetcherTest {

    @Test
    fun testNewUri() {
        Assert.assertEquals(
            "asset://sample.jpeg",
            newAssetUri("sample.jpeg")
        )
        Assert.assertEquals(
            "asset://fd5717876ab046b8aa889c9aaac4b56c.png",
            newAssetUri("fd5717876ab046b8aa889c9aaac4b56c.png")
        )
    }

    @Test
    fun testFactory() {
        val (context, sketch) = getContextAndSketch()
        val fetcherFactory = AssetUriFetcher.Factory()
        val assetUri = newAssetUri("sample.jpeg")
        val httpUri = "http://sample.com/sample.jpg"
        val contentUri = "content://sample_app/sample"

        fetcherFactory.create(sketch, LoadRequest(context, assetUri))!!.apply {
            Assert.assertEquals("sample.jpeg", assetFileName)
        }
        fetcherFactory.create(sketch, DisplayRequest(context, assetUri))!!.apply {
            Assert.assertEquals("sample.jpeg", assetFileName)
        }
        fetcherFactory.create(sketch, DownloadRequest(context, assetUri))!!.apply {
            Assert.assertEquals("sample.jpeg", assetFileName)
        }
        Assert.assertNull(fetcherFactory.create(sketch, LoadRequest(context, httpUri)))
        Assert.assertNull(fetcherFactory.create(sketch, LoadRequest(context, contentUri)))
    }

    @Test
    fun testFetch() {
        val (context, sketch) = getContextAndSketch()
        val fetcherFactory = AssetUriFetcher.Factory()
        val assetUri = newAssetUri("sample.jpeg")

        val fetcher = fetcherFactory.create(sketch, LoadRequest(context, assetUri))!!
        val source = runBlocking {
            fetcher.fetch().dataSource
        }
        Assert.assertTrue(source is AssetDataSource)
    }
}