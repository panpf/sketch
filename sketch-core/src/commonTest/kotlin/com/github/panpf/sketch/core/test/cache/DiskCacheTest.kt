package com.github.panpf.sketch.core.test.cache

import com.github.panpf.sketch.cache.DiskCache
import com.github.panpf.sketch.cache.internal.EmptyDiskCache
import com.github.panpf.sketch.cache.platformDefaultDiskCacheMaxSize
import com.github.panpf.sketch.test.utils.getTestContext
import com.github.panpf.sketch.util.appCacheDirectory
import com.github.panpf.sketch.util.defaultFileSystem
import com.github.panpf.sketch.util.formatFileSize
import okio.Path.Companion.toPath
import okio.use
import kotlin.math.roundToLong
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class DiskCacheTest {

    @Test
    fun testDownloadBuilder() {
        val context = getTestContext()
        val fileSystem = defaultFileSystem()
        val platformDefaultDiskCacheMaxSize = platformDefaultDiskCacheMaxSize(context)
        val defaultAppCacheDirectory = context.appCacheDirectory()
        val appCacheDir = "/fake/fakeApp/cache".toPath()
        val appCacheDir2 = "/fake/fakeApp/cache2".toPath()
        val appCacheDir3 = "/fake/fakeApp/cache3".toPath()
        val appCacheDir4 = "/fake/fakeApp/cache4".toPath()

        DiskCache.DownloadBuilder(context, fileSystem).apply {
            // for desktop platform
            if (defaultAppCacheDirectory == null) {
                appCacheDirectory(appCacheDir)
            }
        }.build().use {
            if (platformDefaultDiskCacheMaxSize != null) {
                assertEquals(
                    (defaultAppCacheDirectory ?: appCacheDir)
                        .resolve(DiskCache.DIRECTORY_NAME)
                        .resolve(DiskCache.DownloadBuilder.SUB_DIRECTORY_NAME),
                    it.directory
                )
                assertEquals(
                    (platformDefaultDiskCacheMaxSize * DiskCache.DownloadBuilder.MAX_SIZE_PERCENT).roundToLong()
                        .formatFileSize(),
                    it.maxSize.formatFileSize()
                )
                assertEquals(DiskCache.Builder.DEFAULT_APP_VERSION, it.appVersion)
                assertEquals(DiskCache.DownloadBuilder.INTERNAL_VERSION, it.internalVersion)
            } else {
                assertEquals(EmptyDiskCache(fileSystem), it)
            }
        }

        DiskCache.DownloadBuilder(context, fileSystem).apply {
            directory(appCacheDir2)
            maxSize(100L * 1024 * 1024)
            appVersion(101)
        }.build().use {
            if (platformDefaultDiskCacheMaxSize != null) {
                assertEquals(appCacheDir2, it.directory)
                assertEquals("100MB", it.maxSize.formatFileSize())
                assertEquals(101, it.appVersion)
            } else {
                assertEquals(EmptyDiskCache(fileSystem), it)
            }
        }

        DiskCache.DownloadBuilder(context, fileSystem).apply {
            appCacheDirectory(appCacheDir3)
        }.build().use {
            if (platformDefaultDiskCacheMaxSize != null) {
                assertEquals(
                    appCacheDir3.resolve(DiskCache.DIRECTORY_NAME)
                        .resolve(DiskCache.DownloadBuilder.SUB_DIRECTORY_NAME),
                    it.directory
                )
            } else {
                assertEquals(EmptyDiskCache(fileSystem), it)
            }
        }
        DiskCache.DownloadBuilder(context, fileSystem).apply {
            appCacheDirectory(appCacheDir3)
            directory(appCacheDir4)
        }.build().use {
            if (platformDefaultDiskCacheMaxSize != null) {
                assertEquals(appCacheDir4, it.directory)
            } else {
                assertEquals(EmptyDiskCache(fileSystem), it)
            }
        }

        assertFailsWith(IllegalArgumentException::class) {
            DiskCache.DownloadBuilder(context, fileSystem).maxSize(0)
        }
        assertFailsWith(IllegalArgumentException::class) {
            DiskCache.DownloadBuilder(context, fileSystem).maxSize(-1)
        }
        assertFailsWith(IllegalArgumentException::class) {
            DiskCache.DownloadBuilder(context, fileSystem).appVersion(0)
        }
        assertFailsWith(IllegalArgumentException::class) {
            DiskCache.DownloadBuilder(context, fileSystem).appVersion(-1)
        }
    }

    @Test
    fun testResultBuilder() {
        val context = getTestContext()
        val fileSystem = defaultFileSystem()
        val platformDefaultDiskCacheMaxSize = platformDefaultDiskCacheMaxSize(context)
        val defaultAppCacheDirectory = context.appCacheDirectory()
        val appCacheDir = "/fake/fakeApp/cache".toPath()
        val appCacheDir2 = "/fake/fakeApp/cache2".toPath()
        val appCacheDir3 = "/fake/fakeApp/cache3".toPath()
        val appCacheDir4 = "/fake/fakeApp/cache4".toPath()

        DiskCache.ResultBuilder(context, fileSystem).apply {
            // for desktop platform
            if (defaultAppCacheDirectory == null) {
                appCacheDirectory(appCacheDir)
            }
        }.build().use {
            if (platformDefaultDiskCacheMaxSize != null) {
                assertEquals(
                    (defaultAppCacheDirectory ?: appCacheDir)
                        .resolve(DiskCache.DIRECTORY_NAME)
                        .resolve(DiskCache.ResultBuilder.SUB_DIRECTORY_NAME),
                    it.directory
                )
                assertEquals(
                    (platformDefaultDiskCacheMaxSize * DiskCache.ResultBuilder.MAX_SIZE_PERCENT).roundToLong()
                        .formatFileSize(),
                    it.maxSize.formatFileSize()
                )
                assertEquals(DiskCache.Builder.DEFAULT_APP_VERSION, it.appVersion)
                assertEquals(DiskCache.ResultBuilder.INTERNAL_VERSION, it.internalVersion)
            } else {
                assertEquals(EmptyDiskCache(fileSystem), it)
            }
        }

        DiskCache.ResultBuilder(context, fileSystem).apply {
            directory(appCacheDir2)
            maxSize(100L * 1024 * 1024)
            appVersion(101)
        }.build().use {
            if (platformDefaultDiskCacheMaxSize != null) {
                assertEquals(appCacheDir2, it.directory)
                assertEquals("100MB", it.maxSize.formatFileSize())
                assertEquals(101, it.appVersion)
            } else {
                assertEquals(EmptyDiskCache(fileSystem), it)
            }
        }

        DiskCache.ResultBuilder(context, fileSystem).apply {
            appCacheDirectory(appCacheDir3)
        }.build().use {
            if (platformDefaultDiskCacheMaxSize != null) {
                assertEquals(
                    appCacheDir3.resolve(DiskCache.DIRECTORY_NAME)
                        .resolve(DiskCache.ResultBuilder.SUB_DIRECTORY_NAME),
                    it.directory
                )
            } else {
                assertEquals(EmptyDiskCache(fileSystem), it)
            }
        }
        DiskCache.ResultBuilder(context, fileSystem).apply {
            appCacheDirectory(appCacheDir3)
            directory(appCacheDir4)
        }.build().use {
            if (platformDefaultDiskCacheMaxSize != null) {
                assertEquals(appCacheDir4, it.directory)
            } else {
                assertEquals(EmptyDiskCache(fileSystem), it)
            }
        }

        assertFailsWith(IllegalArgumentException::class) {
            DiskCache.ResultBuilder(context, fileSystem).maxSize(0)
        }
        assertFailsWith(IllegalArgumentException::class) {
            DiskCache.ResultBuilder(context, fileSystem).maxSize(-1)
        }
        assertFailsWith(IllegalArgumentException::class) {
            DiskCache.ResultBuilder(context, fileSystem).appVersion(0)
        }
        assertFailsWith(IllegalArgumentException::class) {
            DiskCache.ResultBuilder(context, fileSystem).appVersion(-1)
        }
    }
}