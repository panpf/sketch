package com.github.panpf.sketch.test.datasource

import androidx.test.InstrumentationRegistry
import androidx.test.runner.AndroidJUnit4
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.datasource.AssetDataSource
import com.github.panpf.sketch.datasource.DiskCacheDataSource
import com.github.panpf.sketch.fetch.newAssetUri
import com.github.panpf.sketch.request.DataFrom
import com.github.panpf.sketch.request.LoadRequest
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DiskCacheDataSourceTest {

    @Test
    fun testConstructor() {
        val context = InstrumentationRegistry.getContext()
        val sketch = Sketch.new(context)
        val request = LoadRequest(newAssetUri("sample.jpeg"))
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
            Assert.assertTrue(sketch === this.sketch)
            Assert.assertTrue(request === this.request)
            Assert.assertTrue(diskCacheSnapshot === this.diskCacheSnapshot)
            Assert.assertEquals(DataFrom.DISK_CACHE, this.dataFrom)
            Assert.assertEquals(540456, length())
        }
    }

    @Test
    fun testNewFileDescriptor() {
        val context = InstrumentationRegistry.getContext()
        val sketch = Sketch.new(context)
        val request = LoadRequest(newAssetUri("sample.jpeg"))
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
            Assert.assertNull(newFileDescriptor())
        }
    }

    @Test
    fun testNewInputStream() {
        val context = InstrumentationRegistry.getContext()
        val sketch = Sketch.new(context)
        val request = LoadRequest(newAssetUri("sample.jpeg"))
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
        val context = InstrumentationRegistry.getContext()
        val sketch = Sketch.new(context)
        val request = LoadRequest(newAssetUri("sample.jpeg"))
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
        val context = InstrumentationRegistry.getContext()
        val sketch = Sketch.new(context)
        val request = LoadRequest(newAssetUri("sample.jpeg"))
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