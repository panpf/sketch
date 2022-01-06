package com.github.panpf.sketch.test.fetch

import androidx.test.InstrumentationRegistry
import androidx.test.runner.AndroidJUnit4
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.datasource.DiskCacheDataSource
import com.github.panpf.sketch.fetch.AppIconUriFetcher
import com.github.panpf.sketch.fetch.newAppIconUri
import com.github.panpf.sketch.request.DataFrom
import com.github.panpf.sketch.request.DisplayRequest
import com.github.panpf.sketch.request.DownloadRequest
import com.github.panpf.sketch.request.LoadRequest
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AppIconUriFetcherTest {

    @Test
    fun testNewUri() {
        Assert.assertEquals(
            "app.icon://packageName/12412",
            newAppIconUri("packageName", 12412).toString()
        )
        Assert.assertEquals(
            "app.icon://packageName1/12413",
            newAppIconUri("packageName1", 12413).toString()
        )
    }

    @Test
    fun testFactory() {
        val context = InstrumentationRegistry.getContext()
        val sketch = Sketch.new(context)
        val fetcherFactory = AppIconUriFetcher.Factory()
        val appIconUri = "app.icon://packageName/12412"
        val contentUri = "content://sample_app/sample"

        fetcherFactory.create(sketch, LoadRequest.new(appIconUri))!!.apply {
            Assert.assertEquals("packageName", packageName)
            Assert.assertEquals(12412, versionCode)
        }
        fetcherFactory.create(sketch, DisplayRequest.new(appIconUri))!!.apply {
            Assert.assertEquals("packageName", packageName)
            Assert.assertEquals(12412, versionCode)
        }
        Assert.assertNull(fetcherFactory.create(sketch, DownloadRequest.new(appIconUri)))
        Assert.assertNull(fetcherFactory.create(sketch, LoadRequest.new(contentUri)))
    }

    @Test
    fun testFetch() {
        val context = InstrumentationRegistry.getContext()
        val sketch = Sketch.new(context)
        val fetcherFactory = AppIconUriFetcher.Factory()
        val appIconUri = newAppIconUri(context.packageName, context.packageManager.getPackageInfo(context.packageName, 0).versionCode)

        val fetcher = fetcherFactory.create(sketch, LoadRequest.new(appIconUri))!!
        val diskCache = sketch.diskCache
        val existDiskCacheEntry = diskCache.exist(diskCache.encodeKey(fetcher.getDiskCacheKey()))
        val source = runBlocking {
            fetcher.fetch().source
        }
        Assert.assertEquals(
            if (existDiskCacheEntry) DataFrom.DISK_CACHE else DataFrom.LOCAL,
            source.from
        )
        Assert.assertTrue(source is DiskCacheDataSource)
    }
}