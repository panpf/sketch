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
        val request = LoadRequest(newAssetUri("fd5717876ab046b8aa889c9aaac4b56c.jpeg"))
        runBlocking {
            AssetDataSource(
                sketch = sketch,
                request = request,
                assetFileName = "fd5717876ab046b8aa889c9aaac4b56c.jpeg"
            ).file()
        }
        val diskCache = sketch.diskCache
        val diskCacheEntry = diskCache[diskCache.encodeKey(request.uriString + "_data_source")]!!
        DiskCacheDataSource(
            sketch = sketch,
            request = request,
            from = DataFrom.DISK_CACHE,
            diskCacheEntry = diskCacheEntry,
        ).apply {
            Assert.assertTrue(sketch === this.sketch)
            Assert.assertTrue(request === this.request)
            Assert.assertTrue(diskCacheEntry === this.diskCacheEntry)
            Assert.assertEquals(DataFrom.DISK_CACHE, this.from)
            Assert.assertEquals(540456, length())
        }
    }

    @Test
    fun testNewFileDescriptor() {
        val context = InstrumentationRegistry.getContext()
        val sketch = Sketch.new(context)
        val request = LoadRequest(newAssetUri("fd5717876ab046b8aa889c9aaac4b56c.jpeg"))
        runBlocking {
            AssetDataSource(
                sketch = sketch,
                request = request,
                assetFileName = "fd5717876ab046b8aa889c9aaac4b56c.jpeg"
            ).file()
        }
        val diskCache = sketch.diskCache
        val diskCacheEntry = diskCache[diskCache.encodeKey(request.uriString + "_data_source")]!!
        DiskCacheDataSource(
            sketch = sketch,
            request = request,
            from = DataFrom.DISK_CACHE,
            diskCacheEntry = diskCacheEntry,
        ).apply {
            Assert.assertNull(newFileDescriptor())
        }
    }

    @Test
    fun testNewInputStream() {
        val context = InstrumentationRegistry.getContext()
        val sketch = Sketch.new(context)
        val request = LoadRequest(newAssetUri("fd5717876ab046b8aa889c9aaac4b56c.jpeg"))
        runBlocking {
            AssetDataSource(
                sketch = sketch,
                request = request,
                assetFileName = "fd5717876ab046b8aa889c9aaac4b56c.jpeg"
            ).file()
        }
        val diskCache = sketch.diskCache
        val diskCacheEntry = diskCache[diskCache.encodeKey(request.uriString + "_data_source")]!!
        DiskCacheDataSource(
            sketch = sketch,
            request = request,
            from = DataFrom.DISK_CACHE,
            diskCacheEntry = diskCacheEntry,
        ).apply {
            newInputStream().close()
        }
    }

    @Test
    fun testFile() {
        val context = InstrumentationRegistry.getContext()
        val sketch = Sketch.new(context)
        val request = LoadRequest(newAssetUri("fd5717876ab046b8aa889c9aaac4b56c.jpeg"))
        runBlocking {
            AssetDataSource(
                sketch = sketch,
                request = request,
                assetFileName = "fd5717876ab046b8aa889c9aaac4b56c.jpeg"
            ).file()
        }
        val diskCache = sketch.diskCache
        val diskCacheEntry = diskCache[diskCache.encodeKey(request.uriString + "_data_source")]!!
        DiskCacheDataSource(
            sketch = sketch,
            request = request,
            from = DataFrom.DISK_CACHE,
            diskCacheEntry = diskCacheEntry,
        ).apply {
            val file = runBlocking {
                file()
            }
            Assert.assertEquals(
                diskCacheEntry.file.path,
                file.path
            )
        }
    }

    @Test
    fun testToString() {
        val context = InstrumentationRegistry.getContext()
        val sketch = Sketch.new(context)
        val request = LoadRequest(newAssetUri("fd5717876ab046b8aa889c9aaac4b56c.jpeg"))
        runBlocking {
            AssetDataSource(
                sketch = sketch,
                request = request,
                assetFileName = "fd5717876ab046b8aa889c9aaac4b56c.jpeg"
            ).file()
        }
        val diskCache = sketch.diskCache
        val diskCacheEntry = diskCache[diskCache.encodeKey(request.uriString + "_data_source")]!!
        DiskCacheDataSource(
            sketch = sketch,
            request = request,
            from = DataFrom.DISK_CACHE,
            diskCacheEntry = diskCacheEntry,
        ).apply {
            Assert.assertEquals(
                "DiskCacheDataSource(from=DISK_CACHE,file='${diskCacheEntry.file.path}')",
                toString()
            )
        }
    }
}