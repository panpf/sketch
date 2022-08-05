/*
 * Copyright (C) 2022 panpf <panpfpanpf@outlook.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
        val diskCache = sketch.resultCache
        val diskCacheSnapshot = diskCache[request.uriString + "_data_source"]!!
        DiskCacheDataSource(
            sketch = sketch,
            request = request,
            dataFrom = DataFrom.DOWNLOAD_CACHE,
            snapshot = diskCacheSnapshot,
        ).apply {
            Assert.assertTrue(sketch === this.sketch)
            Assert.assertTrue(request === this.request)
            Assert.assertTrue(diskCacheSnapshot === this.snapshot)
            Assert.assertEquals(DataFrom.DOWNLOAD_CACHE, this.dataFrom)
        }
    }

    @Test
    fun testLength() {
        val (context, sketch) = getTestContextAndNewSketch()
        val request = LoadRequest(context, newAssetUri("sample.jpeg"))
        runBlocking {
            AssetDataSource(
                sketch = sketch,
                request = request,
                assetFileName = "sample.jpeg"
            ).file()
        }
        val diskCache = sketch.resultCache
        val diskCacheSnapshot = diskCache[request.uriString + "_data_source"]!!
        DiskCacheDataSource(
            sketch = sketch,
            request = request,
            dataFrom = DataFrom.DOWNLOAD_CACHE,
            snapshot = diskCacheSnapshot,
        ).apply {
            Assert.assertEquals(540456, length())
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
        val diskCache = sketch.resultCache
        val diskCacheSnapshot = diskCache[request.uriString + "_data_source"]!!
        DiskCacheDataSource(
            sketch = sketch,
            request = request,
            dataFrom = DataFrom.DOWNLOAD_CACHE,
            snapshot = diskCacheSnapshot,
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
        val diskCache = sketch.resultCache
        val diskCacheSnapshot = diskCache[request.uriString + "_data_source"]!!
        DiskCacheDataSource(
            sketch = sketch,
            request = request,
            dataFrom = DataFrom.DOWNLOAD_CACHE,
            snapshot = diskCacheSnapshot,
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
        val diskCache = sketch.resultCache
        val diskCacheSnapshot = diskCache[request.uriString + "_data_source"]!!
        DiskCacheDataSource(
            sketch = sketch,
            request = request,
            dataFrom = DataFrom.DOWNLOAD_CACHE,
            snapshot = diskCacheSnapshot,
        ).apply {
            Assert.assertEquals(
                "DiskCacheDataSource(from=DOWNLOAD_CACHE,file='${diskCacheSnapshot.file.path}')",
                toString()
            )
        }
    }
}