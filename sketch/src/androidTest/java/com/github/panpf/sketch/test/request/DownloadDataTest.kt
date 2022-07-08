package com.github.panpf.sketch.test.request

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.datasource.DataFrom.DISK_CACHE
import com.github.panpf.sketch.datasource.DataFrom.MEMORY
import com.github.panpf.sketch.request.DownloadData
import com.github.panpf.sketch.test.utils.getTestContextAndNewSketch
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
        sketch.downloadDiskCache.edit(diskCacheKey)!!.apply {
            newOutputStream().buffered().use {
                it.write(diskCacheKey.toByteArray())
            }
            commit()
        }
        val snapshot = sketch.downloadDiskCache[diskCacheKey]!!

        val bytes = snapshot.newInputStream().use { it.readBytes() }
        DownloadData.Bytes(bytes, MEMORY).apply {
            Assert.assertSame(bytes, data)
            Assert.assertEquals(MEMORY, dataFrom)
            Assert.assertTrue(newInputStream().apply { close() } is ByteArrayInputStream)
        }

        DownloadData.Cache(snapshot, DISK_CACHE).apply {
            Assert.assertSame(snapshot, diskCacheSnapshot)
            Assert.assertEquals(DISK_CACHE, dataFrom)
            Assert.assertTrue(newInputStream().apply { close() } is FileInputStream)
        }
    }
}