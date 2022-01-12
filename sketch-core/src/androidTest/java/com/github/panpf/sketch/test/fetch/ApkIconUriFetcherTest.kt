package com.github.panpf.sketch.test.fetch

import androidx.test.InstrumentationRegistry
import androidx.test.runner.AndroidJUnit4
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.datasource.DiskCacheDataSource
import com.github.panpf.sketch.fetch.ApkIconUriFetcher
import com.github.panpf.sketch.fetch.newApkIconUri
import com.github.panpf.sketch.request.DataFrom
import com.github.panpf.sketch.request.DisplayRequest
import com.github.panpf.sketch.request.DownloadRequest
import com.github.panpf.sketch.request.LoadRequest
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ApkIconUriFetcherTest {

    @Test
    fun testNewUri() {
        Assert.assertEquals(
            "apk.icon:///sdcard/sample.apk",
            newApkIconUri("/sdcard/sample.apk").toString()
        )
        Assert.assertEquals(
            "apk.icon:///sdcard/sample1.apk",
            newApkIconUri("/sdcard/sample1.apk").toString()
        )
    }

    @Test
    fun testFactory() {
        val context = InstrumentationRegistry.getContext()
        val sketch = Sketch.new(context)
        val fetcherFactory = ApkIconUriFetcher.Factory()
        val apkIconUri = "apk.icon:///sdcard/sample.apk"
        val contentUri = "content://sample_app/sample"

        fetcherFactory.create(sketch, LoadRequest.new(apkIconUri))!!.apply {
            Assert.assertEquals("/sdcard/sample.apk", apkFilePath)
        }
        fetcherFactory.create(sketch, DisplayRequest.new(apkIconUri))!!.apply {
            Assert.assertEquals("/sdcard/sample.apk", apkFilePath)
        }
        Assert.assertNull(fetcherFactory.create(sketch, DownloadRequest.new(apkIconUri)))
        Assert.assertNull(fetcherFactory.create(sketch, LoadRequest.new(contentUri)))
    }

    @Test
    fun testFetch() {
        val context = InstrumentationRegistry.getContext()
        val sketch = Sketch.new(context)
        val fetcherFactory = ApkIconUriFetcher.Factory()
        val apkIconUri = newApkIconUri(context.packageManager.getApplicationInfo(context.packageName, 0).publicSourceDir)

        val fetcher = fetcherFactory.create(sketch, LoadRequest.new(apkIconUri))!!
        val diskCache = sketch.diskCache
        val existDiskCacheEntry = diskCache.exist(diskCache.encodeKey(fetcher.getDiskCacheKey()))
        val source = runBlocking {
            fetcher.fetch().dataSource
        }
        Assert.assertEquals(
            if (existDiskCacheEntry) DataFrom.DISK_CACHE else DataFrom.LOCAL,
            source.from
        )
        Assert.assertTrue(source is DiskCacheDataSource)
    }
}