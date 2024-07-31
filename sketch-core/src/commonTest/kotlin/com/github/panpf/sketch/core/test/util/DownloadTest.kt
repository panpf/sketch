package com.github.panpf.sketch.core.test.util

import com.github.panpf.sketch.cache.CachePolicy
import com.github.panpf.sketch.cache.DiskCache
import com.github.panpf.sketch.images.ResourceImages
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.test.singleton.getTestContextAndSketch
import com.github.panpf.sketch.test.utils.MyImagesHttpStack
import com.github.panpf.sketch.test.utils.getTestContextAndNewSketch
import com.github.panpf.sketch.util.DownloadData
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
        val (context, defaultSketch) = getTestContextAndSketch()
        val (_, newSketch) = getTestContextAndNewSketch {
            httpStack(MyImagesHttpStack(defaultSketch))
        }
        try {
            val downloadCache = newSketch.downloadCache
            downloadCache.clear()
            assertEquals(expected = 0L, actual = downloadCache.size)

            val imageUri1 = "http://${ResourceImages.jpeg.resourceName}"
            assertFalse(downloadCache.existWithLock(imageUri1))
            val imageUri2 = "http://${ResourceImages.png.resourceName}"
            assertFalse(downloadCache.existWithLock(imageUri2))
            val imageUri3 = "http://${ResourceImages.webp.resourceName}"
            assertFalse(downloadCache.existWithLock(imageUri3))
            val imageUri4 = "http://${ResourceImages.bmp.resourceName}"
            assertFalse(downloadCache.existWithLock(imageUri4))

            val result1 = newSketch.enqueueDownload(ImageRequest(context, imageUri1)).await()
            assertTrue(result1.getOrThrow() is DownloadData.Cache)
            assertTrue(downloadCache.existWithLock(imageUri1))
            assertFalse(downloadCache.existWithLock(imageUri2))

            val result2 = newSketch.enqueueDownload(ImageRequest(context, imageUri2) {
                downloadCachePolicy(CachePolicy.ENABLED)
            }).await()
            assertTrue(result2.getOrThrow() is DownloadData.Cache)
            assertTrue(downloadCache.existWithLock(imageUri1))
            assertTrue(downloadCache.existWithLock(imageUri2))

            val result3 = newSketch.executeDownload(ImageRequest(context, imageUri3) {
                downloadCachePolicy(CachePolicy.DISABLED)
            })
            assertTrue(result3.getOrThrow() is DownloadData.Bytes)
            assertTrue(downloadCache.existWithLock(imageUri1))
            assertTrue(downloadCache.existWithLock(imageUri2))
            assertFalse(downloadCache.existWithLock(imageUri3))

            val result21 = newSketch.executeDownload(ImageRequest(context, imageUri2) {
                downloadCachePolicy(CachePolicy.WRITE_ONLY)
            })
            assertTrue(result21.getOrThrow() is DownloadData.Bytes)
            assertTrue(downloadCache.existWithLock(imageUri1))
            assertTrue(downloadCache.existWithLock(imageUri2))
            assertFalse(downloadCache.existWithLock(imageUri3))

            val result11 = newSketch.executeDownload(ImageRequest(context, imageUri1) {
                downloadCachePolicy(CachePolicy.READ_ONLY)
            })
            assertTrue(result11.getOrThrow() is DownloadData.Cache)
            assertTrue(downloadCache.existWithLock(imageUri1))
            assertTrue(downloadCache.existWithLock(imageUri2))
            assertFalse(downloadCache.existWithLock(imageUri3))

            val result4 = newSketch.executeDownload(ImageRequest(context, imageUri4) {
                downloadCachePolicy(CachePolicy.READ_ONLY)
            })
            assertTrue(result4.getOrThrow() is DownloadData.Bytes)
            assertTrue(downloadCache.existWithLock(imageUri1))
            assertTrue(downloadCache.existWithLock(imageUri2))
            assertFalse(downloadCache.existWithLock(imageUri3))
            assertFalse(downloadCache.existWithLock(imageUri4))
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