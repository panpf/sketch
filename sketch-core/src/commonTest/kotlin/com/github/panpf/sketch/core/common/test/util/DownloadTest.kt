package com.github.panpf.sketch.core.common.test.util

import com.github.panpf.sketch.cache.CachePolicy
import com.github.panpf.sketch.cache.DiskCache
import com.github.panpf.sketch.images.ComposeResImageFiles
import com.github.panpf.sketch.images.supportComposeResHttpUri
import com.github.panpf.sketch.images.toComposeResHttpUri
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.test.singleton.getTestContextAndSketch
import com.github.panpf.sketch.test.utils.runInNewSketchWithUse
import com.github.panpf.sketch.util.DownloadData
import kotlinx.coroutines.test.runTest
import okio.use
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class DownloadTest {

    @Test
    fun test() = runTest {
        runInNewSketchWithUse({
            val context = getTestContextAndSketch().first
            components {
                supportComposeResHttpUri(context)
            }
        }) { context, sketch ->
            val downloadCache = sketch.downloadCache
            downloadCache.clear()
            assertEquals(expected = 0L, actual = downloadCache.size)

            val imageUri1 = ComposeResImageFiles.jpeg.toComposeResHttpUri()
            assertFalse(downloadCache.existWithLock(imageUri1))
            val imageUri2 = ComposeResImageFiles.png.toComposeResHttpUri()
            assertFalse(downloadCache.existWithLock(imageUri2))
            val imageUri3 = ComposeResImageFiles.webp.toComposeResHttpUri()
            assertFalse(downloadCache.existWithLock(imageUri3))
            val imageUri4 = ComposeResImageFiles.bmp.toComposeResHttpUri()
            assertFalse(downloadCache.existWithLock(imageUri4))

            val result1 = sketch.enqueueDownload(ImageRequest(context, imageUri1)).await()
            assertTrue(result1.getOrThrow() is DownloadData.Cache)
            assertTrue(downloadCache.existWithLock(imageUri1))
            assertFalse(downloadCache.existWithLock(imageUri2))

            val result2 = sketch.enqueueDownload(ImageRequest(context, imageUri2) {
                downloadCachePolicy(CachePolicy.ENABLED)
            }).await()
            assertTrue(result2.getOrThrow() is DownloadData.Cache)
            assertTrue(downloadCache.existWithLock(imageUri1))
            assertTrue(downloadCache.existWithLock(imageUri2))

            val result3 = sketch.executeDownload(ImageRequest(context, imageUri3) {
                downloadCachePolicy(CachePolicy.DISABLED)
            })
            assertTrue(result3.getOrThrow() is DownloadData.Bytes)
            assertTrue(downloadCache.existWithLock(imageUri1))
            assertTrue(downloadCache.existWithLock(imageUri2))
            assertFalse(downloadCache.existWithLock(imageUri3))

            val result21 = sketch.executeDownload(ImageRequest(context, imageUri2) {
                downloadCachePolicy(CachePolicy.WRITE_ONLY)
            })
            assertTrue(result21.getOrThrow() is DownloadData.Bytes)
            assertTrue(downloadCache.existWithLock(imageUri1))
            assertTrue(downloadCache.existWithLock(imageUri2))
            assertFalse(downloadCache.existWithLock(imageUri3))

            val result11 = sketch.executeDownload(ImageRequest(context, imageUri1) {
                downloadCachePolicy(CachePolicy.READ_ONLY)
            })
            assertTrue(result11.getOrThrow() is DownloadData.Cache)
            assertTrue(downloadCache.existWithLock(imageUri1))
            assertTrue(downloadCache.existWithLock(imageUri2))
            assertFalse(downloadCache.existWithLock(imageUri3))

            val result4 = sketch.executeDownload(ImageRequest(context, imageUri4) {
                downloadCachePolicy(CachePolicy.READ_ONLY)
            })
            assertTrue(result4.getOrThrow() is DownloadData.Bytes)
            assertTrue(downloadCache.existWithLock(imageUri1))
            assertTrue(downloadCache.existWithLock(imageUri2))
            assertFalse(downloadCache.existWithLock(imageUri3))
            assertFalse(downloadCache.existWithLock(imageUri4))
        }
    }

    private suspend fun DiskCache.existWithLock(key: String): Boolean {
        return withLock(key) {
            openSnapshot(key)?.use { } != null
        }
    }
}