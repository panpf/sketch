package com.github.panpf.sketch.core.android.test.source

import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.source.AssetDataSource
import com.github.panpf.sketch.source.DataFrom.DOWNLOAD_CACHE
import com.github.panpf.sketch.source.DiskCacheDataSource

@RunWith(AndroidJUnit4::class)
class DiskCacheDataSourceTest {

    @Test
    fun testConstructor() {
        val (context, sketch) = getTestContextAndNewSketch()
        val request = ImageRequest(context, MyImages.jpeg.uri)
        AssetDataSource(
            sketch = sketch,
            request = request,
            assetFileName = MyImages.jpeg.fileName
        ).getFile()
        val diskCache = sketch.resultCache
        val diskCacheSnapshot = diskCache[request.uriString + "_data_source"]!!
        DiskCacheDataSource(
            sketch = sketch,
            request = request,
            dataFrom = DOWNLOAD_CACHE,
            snapshot = diskCacheSnapshot,
        ).apply {
            Assert.assertTrue(sketch === this.sketch)
            Assert.assertTrue(request === this.request)
            Assert.assertTrue(diskCacheSnapshot === this.snapshot)
            Assert.assertEquals(DOWNLOAD_CACHE, this.dataFrom)
        }
    }

    @Test
    fun testNewInputStream() {
        val (context, sketch) = getTestContextAndNewSketch()
        val request = ImageRequest(context, MyImages.jpeg.uri)
        AssetDataSource(
            sketch = sketch,
            request = request,
            assetFileName = MyImages.jpeg.fileName
        ).getFile()
        val diskCache = sketch.resultCache
        val diskCacheSnapshot = diskCache[request.uriString + "_data_source"]!!
        DiskCacheDataSource(
            sketch = sketch,
            request = request,
            dataFrom = DOWNLOAD_CACHE,
            snapshot = diskCacheSnapshot,
        ).apply {
            openSource().close()
        }
    }

    @Test
    fun testFile() {
        val (context, sketch) = getTestContextAndNewSketch()
        val request = ImageRequest(context, MyImages.jpeg.uri)
        AssetDataSource(
            sketch = sketch,
            request = request,
            assetFileName = MyImages.jpeg.fileName
        ).getFile()
        val diskCache = sketch.resultCache
        val diskCacheSnapshot = diskCache[request.uriString + "_data_source"]!!
        DiskCacheDataSource(
            sketch = sketch,
            request = request,
            dataFrom = DOWNLOAD_CACHE,
            snapshot = diskCacheSnapshot,
        ).apply {
            val file = getFile()
            Assert.assertEquals(
                diskCacheSnapshot.file.path,
                file.path
            )
        }
    }

    @Test
    fun testToString() {
        val (context, sketch) = getTestContextAndNewSketch()
        val request = ImageRequest(context, MyImages.jpeg.uri)
        AssetDataSource(
            sketch = sketch,
            request = request,
            assetFileName = MyImages.jpeg.fileName
        ).getFile()
        val diskCache = sketch.resultCache
        val diskCacheSnapshot = diskCache[request.uriString + "_data_source"]!!
        DiskCacheDataSource(
            sketch = sketch,
            request = request,
            dataFrom = DOWNLOAD_CACHE,
            snapshot = diskCacheSnapshot,
        ).apply {
            Assert.assertEquals(
                "DiskCacheDataSource(from=DOWNLOAD_CACHE,file='${diskCacheSnapshot.file.path}')",
                toString()
            )
        }
    }
}