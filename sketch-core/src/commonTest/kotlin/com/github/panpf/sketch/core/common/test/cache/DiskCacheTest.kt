package com.github.panpf.sketch.core.common.test.cache

import com.github.panpf.sketch.cache.DiskCache
import com.github.panpf.sketch.cache.defaultDiskCacheMaxSize
import com.github.panpf.sketch.test.utils.getTestContext
import com.github.panpf.sketch.util.appCacheDirectory
import com.github.panpf.sketch.util.defaultFileSystem
import okio.Path.Companion.toPath
import kotlin.math.roundToLong
import kotlin.test.Test
import kotlin.test.assertEquals

class DiskCacheTest {

    @Test
    fun testCompanion() {
        assertEquals(expected = "sketch4", actual = DiskCache.DIRECTORY_NAME)
    }

    @Test
    fun testBuilder() {
        assertEquals(expected = 1, actual = DiskCache.Builder.DEFAULT_APP_VERSION)

        val context = getTestContext()
        val fileSystem = defaultFileSystem()

        // directory
        DiskCache.Builder(context, fileSystem).build().apply {
            assertEquals(
                expected = context.appCacheDirectory()
                    ?.resolve(DiskCache.DIRECTORY_NAME)
                    ?.toString()
                    ?: "",
                actual = directory.toString()
            )
        }

        DiskCache.Builder(context, fileSystem, subDirectoryName = "sub").build().apply {
            assertEquals(
                expected = context.appCacheDirectory()
                    ?.resolve(DiskCache.DIRECTORY_NAME)
                    ?.resolve("sub")
                    ?.toString()
                    ?: "",
                actual = directory.toString()
            )
        }

        DiskCache.Builder(context, fileSystem, subDirectoryName = "sub").apply {
            directory("/fake/fakeApp/cache".toPath())
        }.build().apply {
            assertEquals(
                expected = "/fake/fakeApp/cache",
                actual = directory.toString()
            )
        }

        DiskCache.Builder(context, fileSystem, subDirectoryName = "sub").apply {
            appCacheDirectory("/fake/fakeApp/cache".toPath())
        }.build().apply {
            assertEquals(
                expected = "/fake/fakeApp/cache".toPath()
                    .resolve(DiskCache.DIRECTORY_NAME)
                    .resolve("sub")
                    .toString(),
                actual = directory.toString()
            )
        }

        // maxSize
        DiskCache.Builder(context, fileSystem).build().apply {
            val platformDefaultMaxSize = defaultDiskCacheMaxSize(context) ?: 0L
            assertEquals(
                expected = (platformDefaultMaxSize * 1f).roundToLong(),
                actual = maxSize
            )
        }

        DiskCache.Builder(context, fileSystem, maxSizePercent = 0.5f).build().apply {
            val platformDefaultMaxSize = defaultDiskCacheMaxSize(context) ?: 0L
            assertEquals(
                expected = (platformDefaultMaxSize * 0.5f).roundToLong(),
                actual = maxSize
            )
        }

        DiskCache.Builder(context, fileSystem, maxSizePercent = 0.5f).apply {
            maxSize(100L * 1024 * 1024)
        }.build().apply {
            assertEquals(
                expected = 100L * 1024 * 1024,
                actual = maxSize
            )
        }

        // appVersion
        DiskCache.Builder(context, fileSystem).build().apply {
            assertEquals(
                expected = DiskCache.Builder.DEFAULT_APP_VERSION,
                actual = appVersion
            )
        }

        DiskCache.Builder(context, fileSystem).apply {
            appVersion(2011)
        }.build().apply {
            assertEquals(
                expected = 2011,
                actual = appVersion
            )
        }

        // internalVersion
        DiskCache.Builder(context, fileSystem).build().apply {
            assertEquals(
                expected = 1,
                actual = internalVersion
            )
        }

        // options
        DiskCache.Builder(context, fileSystem).apply {
            directory("/fake/fakeApp/cache".toPath())
            maxSize(100L * 1024 * 1024)
            appVersion(101)
        }.build().apply {
            assertEquals("/fake/fakeApp/cache", directory.toString())
            assertEquals(100L * 1024 * 1024, maxSize)
            assertEquals(101, appVersion)
        }

        DiskCache.Builder(context, fileSystem).apply {
            directory("/fake/fakeApp/cache".toPath())
            maxSize(100L * 1024 * 1024)
            appVersion(101)
            options(
                DiskCache.Options(
                    directory = "/fake/fakeApp/cache2".toPath(),
                    maxSize = 200L * 1024 * 1024,
                    appVersion = 102
                )
            )
        }.build().apply {
            assertEquals("/fake/fakeApp/cache2", directory.toString())
            assertEquals(200L * 1024 * 1024, maxSize)
            assertEquals(102, appVersion)
        }

        // mergeOptions
        DiskCache.Builder(context, fileSystem).apply {
            maxSize(100L * 1024 * 1024)
            appVersion(101)
            mergeOptions(
                DiskCache.Options(
                    directory = "/fake/fakeApp/cache2".toPath(),
                    maxSize = 200L * 1024 * 1024,
                    appVersion = 102
                )
            )
        }.build().apply {
            assertEquals("/fake/fakeApp/cache2", directory.toString())
            assertEquals(100L * 1024 * 1024, maxSize)
            assertEquals(101, appVersion)
        }

        DiskCache.Builder(context, fileSystem).apply {
            directory("/fake/fakeApp/cache".toPath())
            appVersion(101)
            mergeOptions(
                DiskCache.Options(
                    directory = "/fake/fakeApp/cache2".toPath(),
                    maxSize = 200L * 1024 * 1024,
                    appVersion = 102
                )
            )
        }.build().apply {
            assertEquals("/fake/fakeApp/cache", directory.toString())
            assertEquals(200L * 1024 * 1024, maxSize)
            assertEquals(101, appVersion)
        }

        DiskCache.Builder(context, fileSystem).apply {
            directory("/fake/fakeApp/cache".toPath())
            maxSize(100L * 1024 * 1024)
            mergeOptions(
                DiskCache.Options(
                    directory = "/fake/fakeApp/cache2".toPath(),
                    maxSize = 200L * 1024 * 1024,
                    appVersion = 102
                )
            )
        }.build().apply {
            assertEquals("/fake/fakeApp/cache", directory.toString())
            assertEquals(100L * 1024 * 1024, maxSize)
            assertEquals(102, appVersion)
        }

        DiskCache.Builder(context, fileSystem).apply {
            maxSize(100L * 1024 * 1024)
            appVersion(101)
            mergeOptions(
                DiskCache.Options(
                    appCacheDirectory = "/fake/fakeApp/cache2".toPath(),
                    maxSize = 200L * 1024 * 1024,
                    appVersion = 102
                )
            )
        }.build().apply {
            assertEquals("/fake/fakeApp/cache2/${DiskCache.DIRECTORY_NAME}", directory.toString())
            assertEquals(100L * 1024 * 1024, maxSize)
            assertEquals(101, appVersion)
        }
    }

    @Test
    fun testDownloadBuilder() {
        assertEquals(expected = "download", actual = DiskCache.DownloadBuilder.SUB_DIRECTORY_NAME)
        assertEquals(expected = 0.6f, actual = DiskCache.DownloadBuilder.MAX_SIZE_PERCENT)
        assertEquals(expected = 1, actual = DiskCache.DownloadBuilder.INTERNAL_VERSION)

        val context = getTestContext()
        val fileSystem = defaultFileSystem()

        DiskCache.DownloadBuilder(context, fileSystem).build().apply {
            assertEquals(
                expected = context.appCacheDirectory()
                    ?.resolve(DiskCache.DIRECTORY_NAME)
                    ?.resolve(DiskCache.DownloadBuilder.SUB_DIRECTORY_NAME)
                    ?.toString()
                    ?: "",
                actual = directory.toString()
            )
            val platformDefaultMaxSize = defaultDiskCacheMaxSize(context) ?: 0L
            assertEquals(
                expected = (platformDefaultMaxSize * DiskCache.DownloadBuilder.MAX_SIZE_PERCENT).roundToLong(),
                actual = maxSize
            )
            assertEquals(
                expected = DiskCache.DownloadBuilder.INTERNAL_VERSION,
                actual = internalVersion
            )
        }
    }

    @Test
    fun testResultBuilder() {
        assertEquals(expected = "result", actual = DiskCache.ResultBuilder.SUB_DIRECTORY_NAME)
        assertEquals(expected = 0.4f, actual = DiskCache.ResultBuilder.MAX_SIZE_PERCENT)
        assertEquals(expected = 1, actual = DiskCache.ResultBuilder.INTERNAL_VERSION)

        val context = getTestContext()
        val fileSystem = defaultFileSystem()

        DiskCache.ResultBuilder(context, fileSystem).build().apply {
            assertEquals(
                expected = context.appCacheDirectory()
                    ?.resolve(DiskCache.DIRECTORY_NAME)
                    ?.resolve(DiskCache.ResultBuilder.SUB_DIRECTORY_NAME)
                    ?.toString()
                    ?: "",
                actual = directory.toString()
            )
            val platformDefaultMaxSize = defaultDiskCacheMaxSize(context) ?: 0L
            assertEquals(
                expected = (platformDefaultMaxSize * DiskCache.ResultBuilder.MAX_SIZE_PERCENT).roundToLong(),
                actual = maxSize
            )
            assertEquals(
                expected = DiskCache.ResultBuilder.INTERNAL_VERSION,
                actual = internalVersion
            )
        }
    }
}