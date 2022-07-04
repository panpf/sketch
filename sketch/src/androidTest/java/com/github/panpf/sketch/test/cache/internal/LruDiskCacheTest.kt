package com.github.panpf.sketch.test.cache.internal

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.cache.DiskCache
import com.github.panpf.sketch.cache.internal.LruDiskCache
import com.github.panpf.sketch.test.utils.getTestContext
import com.github.panpf.sketch.test.utils.newTestDiskCacheDirectory
import com.github.panpf.sketch.util.formatFileSize
import com.github.panpf.sketch.util.md5
import com.github.panpf.tools4j.test.ktx.assertThrow
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import java.io.FileInputStream

@RunWith(AndroidJUnit4::class)
class LruDiskCacheTest {

    @Test
    fun testMaxSize() {
        val context = getTestContext()

        LruDiskCache.ForDownloadBuilder(context).build().use {
            Assert.assertEquals("300MB", it.maxSize.formatFileSize())
        }
        LruDiskCache.ForDownloadBuilder(context).apply {
            maxSize(100L * 1024 * 1024)
        }.build().use {
            Assert.assertEquals("100MB", it.maxSize.formatFileSize())
        }
        assertThrow(IllegalArgumentException::class) {
            LruDiskCache.ForDownloadBuilder(context).apply {
                maxSize(0)
            }.build()
        }

        LruDiskCache.ForResultBuilder(context).build().use {
            Assert.assertEquals("200MB", it.maxSize.formatFileSize())
        }
        LruDiskCache.ForResultBuilder(context).apply {
            maxSize(100L * 1024 * 1024)
        }.build().use {
            Assert.assertEquals("100MB", it.maxSize.formatFileSize())
        }
        assertThrow(IllegalArgumentException::class) {
            LruDiskCache.ForResultBuilder(context).apply {
                maxSize(0)
            }.build()
        }
    }

    @Test
    fun testDirectory() {
        val context = getTestContext()

        LruDiskCache.ForDownloadBuilder(context).build().use {
            Assert.assertEquals(
                File(
                    context.externalCacheDir ?: context.cacheDir,
                    DiskCache.DEFAULT_DIR_NAME + File.separator + "download_cache"
                ).path,
                it.directory.path
            )
        }

        val cacheDir = File("/sdcard/testDir")
        LruDiskCache.ForDownloadBuilder(context).apply {
            directory(cacheDir)
        }.build().use {
            Assert.assertEquals(cacheDir.path, it.directory.path)
        }
    }

    @Test
    fun testVersion() {
        val context = getTestContext()

        LruDiskCache.ForDownloadBuilder(context).build().apply {
            Assert.assertEquals(1, appVersion)
            Assert.assertEquals(1, internalVersion)
        }
        LruDiskCache.ForDownloadBuilder(context).apply {
            appVersion(2)
        }.build().apply {
            Assert.assertEquals(2, appVersion)
            Assert.assertEquals(1, internalVersion)
        }

        assertThrow(IllegalArgumentException::class) {
            LruDiskCache.ForDownloadBuilder(context).apply {
                appVersion(0)
            }.build()
        }
        assertThrow(IllegalArgumentException::class) {
            LruDiskCache.ForDownloadBuilder(context).apply {
                appVersion(Short.MAX_VALUE + 1)
            }.build()
        }

        LruDiskCache.ForResultBuilder(context).build().apply {
            Assert.assertEquals(1, appVersion)
            Assert.assertEquals(1, internalVersion)
        }
        LruDiskCache.ForResultBuilder(context).apply {
            appVersion(2)
        }.build().apply {
            Assert.assertEquals(2, appVersion)
            Assert.assertEquals(1, internalVersion)
        }

        assertThrow(IllegalArgumentException::class) {
            LruDiskCache.ForResultBuilder(context).apply {
                appVersion(0)
            }.build()
        }
        assertThrow(IllegalArgumentException::class) {
            LruDiskCache.ForResultBuilder(context).apply {
                appVersion(Short.MAX_VALUE + 1)
            }.build()
        }

        val directory = File(context.newTestDiskCacheDirectory(), "download_cache")
        LruDiskCache.ForDownloadBuilder(context).apply {
            directory(directory)
        }.build().use {
            it.clear()
            Assert.assertEquals(0L, it.size)
        }

        LruDiskCache.ForDownloadBuilder(context).apply {
            directory(directory)
        }.build().use {
            Assert.assertEquals(1, it.appVersion)
            Assert.assertEquals(1, it.internalVersion)
            Assert.assertNull(it["file1"])
            Assert.assertNull(it["file2"])
            it.putFile("file1", 1)
            it.putFile("file2", 1)
            Assert.assertNotNull(it["file1"])
            Assert.assertNotNull(it["file2"])
        }

        LruDiskCache.ForDownloadBuilder(context).apply {
            directory(directory)
        }.build().use {
            Assert.assertEquals(1, it.appVersion)
            Assert.assertEquals(1, it.internalVersion)
            Assert.assertNotNull(it["file1"])
            Assert.assertNotNull(it["file2"])
        }

        LruDiskCache.ForDownloadBuilder(context).apply {
            directory(directory)
            appVersion(2)
        }.build().use {
            Assert.assertEquals(2, it.appVersion)
            Assert.assertEquals(1, it.internalVersion)
            Assert.assertNull(it["file1"])
            Assert.assertNull(it["file2"])
        }

        LruDiskCache.ForDownloadBuilder(context).apply {
            directory(directory)
            appVersion(2)
        }.build().use {
            Assert.assertEquals(2, it.appVersion)
            Assert.assertEquals(1, it.internalVersion)
            Assert.assertNull(it["file1"])
            Assert.assertNull(it["file2"])
            it.putFile("file1", 1)
            it.putFile("file2", 1)
            Assert.assertNotNull(it["file1"])
            Assert.assertNotNull(it["file2"])
        }
    }

    @Test
    fun testSize() {
        val context = getTestContext()

        val defaultCacheDir = context.newTestDiskCacheDirectory()
        LruDiskCache.ForDownloadBuilder(context).apply {
            directory(defaultCacheDir)
        }.build().use {
            it.clear()
            Assert.assertEquals(0L, it.size)
        }

        LruDiskCache.ForDownloadBuilder(context).apply {
            directory(defaultCacheDir)
        }.build().use {
            Assert.assertEquals("0B", it.size.formatFileSize())

            it.putFile("file1", 1)
            Assert.assertEquals("1MB", it.size.formatFileSize())

            it.putFile("file2", 2)
            Assert.assertEquals("3MB", it.size.formatFileSize())
        }
    }

    @Test
    fun testPutRemoveGetExist() {
        val context = getTestContext()

        val defaultCacheDir = context.newTestDiskCacheDirectory()
        LruDiskCache.ForDownloadBuilder(context).apply {
            directory(defaultCacheDir)
        }.build().use {
            it.clear()
            Assert.assertEquals(0L, it.size)
        }
        LruDiskCache.ForDownloadBuilder(context).apply {
            directory(defaultCacheDir)
            maxSize(10L * 1024 * 1024)
        }.build().use {
            Assert.assertNull(it["file1"])
            Assert.assertFalse(it.exist("file1"))
            it.putFile("file1", 1)
            Assert.assertNotNull(it["file1"])
            Assert.assertTrue(it.exist("file1"))

            Assert.assertNull(it["file2"])
            Assert.assertFalse(it.exist("file2"))
            it.putFile("file2", 2)
            Assert.assertNotNull(it["file1"])
            Assert.assertTrue(it.exist("file1"))
            Assert.assertNotNull(it["file2"])
            Assert.assertTrue(it.exist("file2"))

            it.remove("file1")
            Assert.assertNull(it["file1"])
            Assert.assertFalse(it.exist("file1"))
            Assert.assertNotNull(it["file2"])
            Assert.assertTrue(it.exist("file2"))

            it.remove("file2")
            Assert.assertNull(it["file1"])
            Assert.assertFalse(it.exist("file1"))
            Assert.assertNull(it["file2"])
            Assert.assertFalse(it.exist("file2"))
        }
    }

    @Test
    fun testLRU() {
        val context = getTestContext()

        val defaultCacheDir = context.newTestDiskCacheDirectory()
        LruDiskCache.ForDownloadBuilder(context).apply {
            directory(defaultCacheDir)
        }.build().use {
            it.clear()
            Assert.assertEquals(0L, it.size)
        }

        LruDiskCache.ForDownloadBuilder(context).apply {
            directory(defaultCacheDir)
            maxSize(10L * 1024 * 1024)
        }.build().use {
            Assert.assertEquals("0B", it.size.formatFileSize())

            it.putFile("file1", 1)
            Assert.assertEquals("1MB", it.size.formatFileSize())
            Assert.assertNotNull(it["file1"])

            it.putFile("file2", 2)
            Assert.assertEquals("3MB", it.size.formatFileSize())
            Assert.assertNotNull(it["file1"])
            Assert.assertNotNull(it["file2"])

            it.putFile("file3", 3)
            Assert.assertEquals("6MB", it.size.formatFileSize())
            Assert.assertNotNull(it["file1"])
            Assert.assertNotNull(it["file2"])
            Assert.assertNotNull(it["file3"])

            it.putFile("file4", 4)
            Assert.assertEquals("10MB", it.size.formatFileSize())
            Assert.assertNotNull(it["file1"])
            Assert.assertNotNull(it["file2"])
            Assert.assertNotNull(it["file3"])
            Assert.assertNotNull(it["file4"])

            it.putFile("file5", 5)
            Assert.assertEquals("9MB", it.size.formatFileSize())
            Assert.assertNull(it["file1"])
            Assert.assertNull(it["file2"])
            Assert.assertNull(it["file3"])
            Assert.assertNotNull(it["file4"])
            Assert.assertNotNull(it["file5"])

            it.putFile("file6", 6)
            Assert.assertEquals("6MB", it.size.formatFileSize())
            Assert.assertNull(it["file1"])
            Assert.assertNull(it["file2"])
            Assert.assertNull(it["file3"])
            Assert.assertNull(it["file4"])
            Assert.assertNull(it["file5"])
            Assert.assertNotNull(it["file6"])

            it.putFile("file7", 7)
            Assert.assertEquals("7MB", it.size.formatFileSize())
            Assert.assertNull(it["file1"])
            Assert.assertNull(it["file2"])
            Assert.assertNull(it["file3"])
            Assert.assertNull(it["file4"])
            Assert.assertNull(it["file5"])
            Assert.assertNull(it["file6"])
            Assert.assertNotNull(it["file7"])
        }
    }

    @Test
    fun testClear() {
        val context = getTestContext()

        val directory = context.newTestDiskCacheDirectory()
        LruDiskCache.ForDownloadBuilder(context).apply {
            directory(directory)
        }.build().use {
            it.clear()
            Assert.assertEquals(0L, it.size)
        }

        LruDiskCache.ForDownloadBuilder(context).apply {
            directory(directory)
        }.build().use {
            Assert.assertEquals("0B", it.size.formatFileSize())
            it.putFile("file1", 1)
            it.putFile("file2", 2)
            it.putFile("file3", 3)
            it.putFile("file4", 4)
            Assert.assertNotNull(it["file1"])
            Assert.assertNotNull(it["file2"])
            Assert.assertNotNull(it["file3"])
            Assert.assertNotNull(it["file4"])
            Assert.assertEquals("10MB", it.size.formatFileSize())

            it.clear()
            Assert.assertNull(it["file1"])
            Assert.assertNull(it["file2"])
            Assert.assertNull(it["file3"])
            Assert.assertNull(it["file4"])
            Assert.assertEquals("0B", it.size.formatFileSize())
        }
    }

    @Test
    fun testEditLock() {
        val context = getTestContext()
        LruDiskCache.ForDownloadBuilder(context).apply {
            directory(context.newTestDiskCacheDirectory())
            maxSize(10L * 1024 * 1024)
        }.build().use {
            Assert.assertNotNull(it.editLock("file1"))
            Assert.assertNotNull(it.editLock("file2"))
            Assert.assertNotNull(it.editLock("file3"))
            Assert.assertNotNull(it.editLock("file4"))
        }
    }

    @Test
    fun testToString() {
        val context = getTestContext()

        val defaultCacheDir = context.newTestDiskCacheDirectory()
        LruDiskCache.ForDownloadBuilder(context).apply {
            directory(defaultCacheDir)
        }.build().use {
            Assert.assertEquals(
                "LruDiskCache(maxSize=300MB,appVersion=1,internalVersion=1,directory='${defaultCacheDir.path}')",
                it.toString()
            )
        }

        val cacheDir = File("/sdcard/testDir")
        LruDiskCache.ForDownloadBuilder(context).apply {
            directory(cacheDir)
            maxSize(100L * 1024 * 1024)
            appVersion(2)
        }.build().use {
            Assert.assertEquals(
                "LruDiskCache(maxSize=100MB,appVersion=2,internalVersion=1,directory='${cacheDir.path}')",
                it.toString()
            )
        }
    }

    @Test
    fun testSnapshot() {
        val context = getTestContext()
        val defaultCacheDir = context.newTestDiskCacheDirectory()

        LruDiskCache.ForDownloadBuilder(context).apply {
            directory(defaultCacheDir)
        }.build().use {
            it.clear()
            Assert.assertEquals(0L, it.size)
        }

        LruDiskCache.ForDownloadBuilder(context).apply {
            directory(defaultCacheDir)
        }.build().use {
            it.putFile("file1", 1)
            val file1Snapshot = it["file1"]!!

            Assert.assertEquals(
                "file1",
                file1Snapshot.key
            )

            Assert.assertEquals(
                File(defaultCacheDir, "${md5("file1")}.0").path,
                file1Snapshot.file.path
            )

            file1Snapshot.newInputStream().use { input ->
                Assert.assertTrue(input is FileInputStream)
            }

            file1Snapshot.edit()!!.commit()

            Assert.assertTrue(file1Snapshot.remove())
            Assert.assertFalse(it.exist("file1"))
        }
    }

    @Test
    fun testEditor() {
        val context = getTestContext()

        val directory = context.newTestDiskCacheDirectory()
        LruDiskCache.ForDownloadBuilder(context).apply {
            directory(directory)
        }.build().use {
            it.clear()
            Assert.assertEquals(0L, it.size)
        }

        LruDiskCache.ForDownloadBuilder(context).apply {
            directory(directory)
        }.build().use {
            val file1Editor = it.edit("file1")!!
            file1Editor.newOutputStream().use { outputStream ->
                outputStream.write(1)
            }
            file1Editor.commit()
            Assert.assertTrue(it.exist("file1"))

            val file2Editor = it.edit("file2")!!
            file2Editor.newOutputStream().use { outputStream ->
                outputStream.write(2)
            }
            file2Editor.abort()
            Assert.assertFalse(it.exist("file2"))
        }
    }

    @Test
    fun testEqualsAndHashCode() {
        val context = getTestContext()
        val element1 = LruDiskCache.ForDownloadBuilder(context).build()
        val element11 = LruDiskCache.ForDownloadBuilder(context).build()
        val element2 = LruDiskCache.ForDownloadBuilder(context).maxSize(100).build()
        val element3 = LruDiskCache.ForDownloadBuilder(context).directory(File("/sdcard/test")).build()
        val element4 = LruDiskCache.ForDownloadBuilder(context).appVersion(3).build()

        Assert.assertNotSame(element1, element11)
        Assert.assertNotSame(element1, element2)
        Assert.assertNotSame(element2, element11)

        Assert.assertEquals(element1, element1)
        Assert.assertEquals(element1, element11)
        Assert.assertNotEquals(element1, element2)
        Assert.assertNotEquals(element1, element3)
        Assert.assertNotEquals(element1, element4)
        Assert.assertNotEquals(element2, element11)
        Assert.assertNotEquals(element1, null)
        Assert.assertNotEquals(element1, Any())

        Assert.assertEquals(element1.hashCode(), element1.hashCode())
        Assert.assertEquals(element1.hashCode(), element11.hashCode())
        Assert.assertNotEquals(element1.hashCode(), element2.hashCode())
        Assert.assertNotEquals(element2.hashCode(), element11.hashCode())
    }

    private fun LruDiskCache.putFile(fileName: String, sizeMB: Int) {
        val sizeBytes = sizeMB * 1024 * 1024
        edit(fileName)?.apply {
            try {
                newOutputStream().use {
                    val bytes = ByteArray(8192)
                    var writeLength = 0
                    while (writeLength < sizeBytes) {
                        val end = (writeLength + bytes.size).coerceAtMost(sizeBytes)
                        it.write(bytes, 0, end - writeLength)
                        writeLength = end
                    }
                }
            } finally {
                commit()
            }
            Thread.sleep(100)
        }
    }
}