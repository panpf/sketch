package com.github.panpf.sketch.core.android.test.cache

import com.github.panpf.sketch.cache.DiskCache
import com.github.panpf.sketch.cache.internal.EmptyDiskCache
import com.github.panpf.sketch.cache.platformDefaultDiskCacheMaxSize
import com.github.panpf.sketch.test.utils.getTestContext
import com.github.panpf.sketch.util.appCacheDirectory
import com.github.panpf.sketch.util.formatFileSize
import com.github.panpf.tools4j.test.ktx.assertThrow
import okio.FileSystem
import okio.Path.Companion.toOkioPath
import org.junit.Assert
import org.junit.Test
import java.io.File
import kotlin.math.roundToLong

class DiskCacheTest {

    @Test
    fun testDownloadBuilder() {
        val context = getTestContext()
        val fileSystem = FileSystem.SYSTEM
        val platformDefaultDiskCacheMaxSize = platformDefaultDiskCacheMaxSize(context)
        val defaultAppCacheDirectory = context.appCacheDirectory()
        val appCacheDir = File("/fake/fakeApp/cache").toOkioPath()
        val appCacheDir2 = File("/fake/fakeApp/cache2").toOkioPath()
        val appCacheDir3 = File("/fake/fakeApp/cache3").toOkioPath()
        val appCacheDir4 = File("/fake/fakeApp/cache4").toOkioPath()

        DiskCache.DownloadBuilder(context, fileSystem).apply {
            // for desktop platform
            if (defaultAppCacheDirectory != null) {
                appCacheDirectory(appCacheDir)
            }
        }.build().use {
            if (platformDefaultDiskCacheMaxSize != null) {
                Assert.assertEquals(
                    (defaultAppCacheDirectory
                        ?: appCacheDir).resolve(DiskCache.DownloadBuilder.DIRECTORY_NAME),
                    it.directory
                )
                Assert.assertEquals(
                    (platformDefaultDiskCacheMaxSize * DiskCache.DownloadBuilder.DEFAULT_MAX_SIZE_PERCENT).roundToLong()
                        .formatFileSize(),
                    it.maxSize.formatFileSize()
                )
                Assert.assertEquals(DiskCache.DownloadBuilder.DEFAULT_APP_VERSION, it.appVersion)
                Assert.assertEquals(DiskCache.DownloadBuilder.INTERNAL_VERSION, it.internalVersion)
            } else {
                Assert.assertEquals(EmptyDiskCache(fileSystem), it)
            }
        }

        DiskCache.DownloadBuilder(context, fileSystem).apply {
            directory(appCacheDir2)
            maxSize(100L * 1024 * 1024)
            appVersion(101)
        }.build().use {
            if (platformDefaultDiskCacheMaxSize != null) {
                Assert.assertEquals(appCacheDir2, it.directory)
                Assert.assertEquals("100MB", it.maxSize.formatFileSize())
                Assert.assertEquals(101, it.appVersion)
            } else {
                Assert.assertEquals(EmptyDiskCache(fileSystem), it)
            }
        }

        DiskCache.DownloadBuilder(context, fileSystem).apply {
            appCacheDirectory(appCacheDir3)
        }.build().use {
            if (platformDefaultDiskCacheMaxSize != null) {
                Assert.assertEquals(
                    appCacheDir3.resolve(DiskCache.DownloadBuilder.DIRECTORY_NAME),
                    it.directory
                )
            } else {
                Assert.assertEquals(EmptyDiskCache(fileSystem), it)
            }
        }
        DiskCache.DownloadBuilder(context, fileSystem).apply {
            appCacheDirectory(appCacheDir3)
            directory(appCacheDir4)
        }.build().use {
            if (platformDefaultDiskCacheMaxSize != null) {
                Assert.assertEquals(appCacheDir4, it.directory)
            } else {
                Assert.assertEquals(EmptyDiskCache(fileSystem), it)
            }
        }

        assertThrow(IllegalArgumentException::class) {
            DiskCache.DownloadBuilder(context, fileSystem).maxSize(0)
        }
        assertThrow(IllegalArgumentException::class) {
            DiskCache.DownloadBuilder(context, fileSystem).maxSize(-1)
        }
        assertThrow(IllegalArgumentException::class) {
            DiskCache.DownloadBuilder(context, fileSystem).appVersion(0)
        }
        assertThrow(IllegalArgumentException::class) {
            DiskCache.DownloadBuilder(context, fileSystem).appVersion(-1)
        }
    }

    @Test
    fun testResultBuilder() {
        val context = getTestContext()
        val fileSystem = FileSystem.SYSTEM
        val platformDefaultDiskCacheMaxSize = platformDefaultDiskCacheMaxSize(context)
        val defaultAppCacheDirectory = context.appCacheDirectory()
        val appCacheDir = File("/fake/fakeApp/cache").toOkioPath()
        val appCacheDir2 = File("/fake/fakeApp/cache2").toOkioPath()
        val appCacheDir3 = File("/fake/fakeApp/cache3").toOkioPath()
        val appCacheDir4 = File("/fake/fakeApp/cache4").toOkioPath()

        DiskCache.ResultBuilder(context, fileSystem).apply {
            // for desktop platform
            if (defaultAppCacheDirectory != null) {
                appCacheDirectory(appCacheDir)
            }
        }.build().use {
            if (platformDefaultDiskCacheMaxSize != null) {
                Assert.assertEquals(
                    (defaultAppCacheDirectory
                        ?: appCacheDir).resolve(DiskCache.ResultBuilder.DIRECTORY_NAME),
                    it.directory
                )
                Assert.assertEquals(
                    (platformDefaultDiskCacheMaxSize * DiskCache.DownloadBuilder.DEFAULT_MAX_SIZE_PERCENT).roundToLong()
                        .formatFileSize(),
                    it.maxSize.formatFileSize()
                )
                Assert.assertEquals(DiskCache.ResultBuilder.DEFAULT_APP_VERSION, it.appVersion)
                Assert.assertEquals(DiskCache.ResultBuilder.INTERNAL_VERSION, it.internalVersion)
            } else {
                Assert.assertEquals(EmptyDiskCache(fileSystem), it)
            }
        }

        DiskCache.ResultBuilder(context, fileSystem).apply {
            directory(appCacheDir2)
            maxSize(100L * 1024 * 1024)
            appVersion(101)
        }.build().use {
            if (platformDefaultDiskCacheMaxSize != null) {
                Assert.assertEquals(appCacheDir2, it.directory)
                Assert.assertEquals("100MB", it.maxSize.formatFileSize())
                Assert.assertEquals(101, it.appVersion)
            } else {
                Assert.assertEquals(EmptyDiskCache(fileSystem), it)
            }
        }

        DiskCache.ResultBuilder(context, fileSystem).apply {
            appCacheDirectory(appCacheDir3)
        }.build().use {
            if (platformDefaultDiskCacheMaxSize != null) {
                Assert.assertEquals(
                    appCacheDir3.resolve(DiskCache.ResultBuilder.DIRECTORY_NAME),
                    it.directory
                )
            } else {
                Assert.assertEquals(EmptyDiskCache(fileSystem), it)
            }
        }
        DiskCache.ResultBuilder(context, fileSystem).apply {
            appCacheDirectory(appCacheDir3)
            directory(appCacheDir4)
        }.build().use {
            if (platformDefaultDiskCacheMaxSize != null) {
                Assert.assertEquals(appCacheDir4, it.directory)
            } else {
                Assert.assertEquals(EmptyDiskCache(fileSystem), it)
            }
        }

        assertThrow(IllegalArgumentException::class) {
            DiskCache.ResultBuilder(context, fileSystem).maxSize(0)
        }
        assertThrow(IllegalArgumentException::class) {
            DiskCache.ResultBuilder(context, fileSystem).maxSize(-1)
        }
        assertThrow(IllegalArgumentException::class) {
            DiskCache.ResultBuilder(context, fileSystem).appVersion(0)
        }
        assertThrow(IllegalArgumentException::class) {
            DiskCache.ResultBuilder(context, fileSystem).appVersion(-1)
        }
    }
}