package com.github.panpf.sketch.test.datasource

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.datasource.AssetDataSource
import com.github.panpf.sketch.datasource.DataFrom
import com.github.panpf.sketch.datasource.DiskCacheDataSource
import com.github.panpf.sketch.fetch.newAssetUri
import com.github.panpf.sketch.request.LoadRequest
import com.github.panpf.sketch.test.utils.getTestContextAndNewSketch
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DiskCacheDataSourceTest {

    @Test
    fun testConstructor() {
        val (context, sketch) = getTestContextAndNewSketch()
        val request = LoadRequest(context, newAssetUri("sample.jpeg"))
        runBlocking {
            AssetDataSource(
                sketch = sketch,
                request = request,
                assetFileName = "sample.jpeg"
            ).file()
        }
        val diskCache = sketch.diskCache
        val diskCacheSnapshot = diskCache[request.uriString + "_data_source"]!!
        DiskCacheDataSource(
            sketch = sketch,
            request = request,
            dataFrom = DataFrom.DISK_CACHE,
            diskCacheSnapshot = diskCacheSnapshot,
        ).apply {
            Assert.assertTrue(request === this.request)
            Assert.assertTrue(diskCacheSnapshot === this.diskCacheSnapshot)
            Assert.assertEquals(DataFrom.DISK_CACHE, this.dataFrom)
            Assert.assertEquals(540456, length())
        }
    }

    @Test
    fun testNewInputStream() {
        val (context, sketch) = getTestContextAndNewSketch()
        val request = LoadRequest(context, newAssetUri("sample.jpeg"))
        runBlocking {
            AssetDataSource(
                sketch = sketch,
                request = request,
                assetFileName = "sample.jpeg"
            ).file()
        }
        val diskCache = sketch.diskCache
        val diskCacheSnapshot = diskCache[request.uriString + "_data_source"]!!
        DiskCacheDataSource(
            sketch = sketch,
            request = request,
            dataFrom = DataFrom.DISK_CACHE,
            diskCacheSnapshot = diskCacheSnapshot,
        ).apply {
            newInputStream().close()
        }
    }

    @Test
    fun testFile() {
        val (context, sketch) = getTestContextAndNewSketch()
        val request = LoadRequest(context, newAssetUri("sample.jpeg"))
        runBlocking {
            AssetDataSource(
                sketch = sketch,
                request = request,
                assetFileName = "sample.jpeg"
            ).file()
        }
        val diskCache = sketch.diskCache
        val diskCacheSnapshot = diskCache[request.uriString + "_data_source"]!!
        DiskCacheDataSource(
            sketch = sketch,
            request = request,
            dataFrom = DataFrom.DISK_CACHE,
            diskCacheSnapshot = diskCacheSnapshot,
        ).apply {
            val file = runBlocking {
                file()
            }
            Assert.assertEquals(
                diskCacheSnapshot.file.path,
                file.path
            )
        }
    }

    @Test
    fun testToString() {
        val (context, sketch) = getTestContextAndNewSketch()
        val request = LoadRequest(context, newAssetUri("sample.jpeg"))
        runBlocking {
            AssetDataSource(
                sketch = sketch,
                request = request,
                assetFileName = "sample.jpeg"
            ).file()
        }
        val diskCache = sketch.diskCache
        val diskCacheSnapshot = diskCache[request.uriString + "_data_source"]!!
        DiskCacheDataSource(
            sketch = sketch,
            request = request,
            dataFrom = DataFrom.DISK_CACHE,
            diskCacheSnapshot = diskCacheSnapshot,
        ).apply {
            Assert.assertEquals(
                "DiskCacheDataSource(from=DISK_CACHE,file='${diskCacheSnapshot.file.path}')",
                toString()
            )
        }
    }
}