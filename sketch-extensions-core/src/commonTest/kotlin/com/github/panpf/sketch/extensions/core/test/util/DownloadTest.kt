package com.github.panpf.sketch.extensions.core.test.util

import com.github.panpf.sketch.cache.DiskCache
import com.github.panpf.sketch.images.MyImages
import com.github.panpf.sketch.test.singleton.getTestContextAndSketch
import com.github.panpf.sketch.test.utils.MyImagesHttpStack
import com.github.panpf.sketch.test.utils.getTestContextAndNewSketch
import com.github.panpf.sketch.util.enqueueDownload
import com.github.panpf.sketch.util.executeDownload
import com.github.panpf.sketch.util.toUri
import kotlinx.coroutines.test.runTest
import okio.use
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class DownloadTest {

    @Test
    fun test() = runTest {
        val (_, defaultSketch) = getTestContextAndSketch()
        val (_, newSketch) = getTestContextAndNewSketch {
            httpStack(MyImagesHttpStack(defaultSketch))
        }
        try {
            val downloadCache = newSketch.downloadCache
            downloadCache.clear()
            assertEquals(expected = 0L, actual = downloadCache.size)

            val imageUri = "http://${MyImages.jpeg.uri.toUri().authority}"
            assertFalse(downloadCache.existWithLock(imageUri))
            val imageUri2 = "http://${MyImages.png.uri.toUri().authority}"
            assertFalse(downloadCache.existWithLock(imageUri2))

            newSketch.enqueueDownload(imageUri).job.await()
            assertTrue(downloadCache.existWithLock(imageUri))
            assertFalse(downloadCache.existWithLock(imageUri2))

            newSketch.executeDownload(imageUri2)
            assertTrue(downloadCache.existWithLock(imageUri))
            assertTrue(downloadCache.existWithLock(imageUri2))
        } finally {
            newSketch.downloadCache.fileSystem.deleteRecursively(newSketch.downloadCache.directory)
            newSketch.resultCache.fileSystem.deleteRecursively(newSketch.resultCache.directory)
        }
    }

    private suspend fun DiskCache.existWithLock(key: String): Boolean {
        return withLock(key) {
            openSnapshot(key)?.use { } != null
        }
    }
}