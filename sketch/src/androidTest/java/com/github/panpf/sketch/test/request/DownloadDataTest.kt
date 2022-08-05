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
package com.github.panpf.sketch.test.request

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.datasource.DataFrom.DOWNLOAD_CACHE
import com.github.panpf.sketch.datasource.DataFrom.MEMORY
import com.github.panpf.sketch.request.DownloadData
import com.github.panpf.sketch.test.utils.getTestContextAndNewSketch
import com.github.panpf.sketch.util.asOrThrow
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import java.io.ByteArrayInputStream
import java.io.FileInputStream

@RunWith(AndroidJUnit4::class)
class DownloadDataTest {

    @Test
    fun test() {
        val (_, sketch) = getTestContextAndNewSketch()
        val diskCacheKey = "testDiskCacheKey"
        sketch.downloadCache.edit(diskCacheKey)!!.apply {
            newOutputStream().buffered().use {
                it.write(diskCacheKey.toByteArray())
            }
            commit()
        }
        val snapshot = sketch.downloadCache[diskCacheKey]!!

        val bytes = snapshot.newInputStream().use { it.readBytes() }
        DownloadData(bytes, MEMORY).apply {
            Assert.assertSame(bytes, data.asOrThrow<DownloadData.ByteArrayData>().bytes)
            Assert.assertEquals(MEMORY, dataFrom)
            Assert.assertTrue(data.newInputStream().apply { close() } is ByteArrayInputStream)
        }

        DownloadData(snapshot, DOWNLOAD_CACHE).apply {
            Assert.assertSame(snapshot, data.asOrThrow<DownloadData.DiskCacheData>().snapshot)
            Assert.assertEquals(DOWNLOAD_CACHE, dataFrom)
            Assert.assertTrue(data.newInputStream().apply { close() } is FileInputStream)
        }
    }
}