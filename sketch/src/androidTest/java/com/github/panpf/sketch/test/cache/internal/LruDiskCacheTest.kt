package com.github.panpf.sketch.test.cache.internal

import androidx.test.InstrumentationRegistry
import androidx.test.runner.AndroidJUnit4
import com.github.panpf.sketch.cache.DiskCache
import com.github.panpf.sketch.cache.internal.LruDiskCache
import com.github.panpf.sketch.util.Logger
import com.github.panpf.sketch.util.MD5Utils
import com.github.panpf.sketch.util.formatFileSize
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import java.io.FileInputStream

@RunWith(AndroidJUnit4::class)
class LruDiskCacheTest {

    @Test
    fun testMaxSize() {
        val context = InstrumentationRegistry.getContext()
        val logger = Logger()
        LruDiskCache(context, logger).use {
            Assert.assertEquals("512MB", it.maxSize.formatFileSize())
        }

        LruDiskCache(context, logger, maxSize = 100L * 1024 * 1024).use {
            Assert.assertEquals("100MB", it.maxSize.formatFileSize())
        }
    }

    @Test
    fun testVersion() {
        val context = InstrumentationRegistry.getContext()
        val logger = Logger()

        LruDiskCache(context, logger).use {
            it.clear()
            Assert.assertEquals(0L, it.size)
        }

        LruDiskCache(context, logger).use {
            Assert.assertEquals(1, it.version)
            Assert.assertNull(it["file1"])
            Assert.assertNull(it["file2"])
            it.putFile("file1", 1)
            it.putFile("file2", 1)
            Assert.assertNotNull(it["file1"])
            Assert.assertNotNull(it["file2"])
        }

        LruDiskCache(context, logger).use {
            Assert.assertEquals(1, it.version)
            Assert.assertNotNull(it["file1"])
            Assert.assertNotNull(it["file2"])
        }

        LruDiskCache(context, logger, version = 2).use {
            Assert.assertEquals(2, it.version)
            Assert.assertNull(it["file1"])
            Assert.assertNull(it["file2"])
        }
    }

    @Test
    fun testDirectory() {
        val context = InstrumentationRegistry.getContext()
        val logger = Logger()

        LruDiskCache(context, logger).use {
            val defaultCacheDir =
                File(context.externalCacheDir ?: context.cacheDir, DiskCache.DEFAULT_DIR_NAME)
            Assert.assertEquals(
                defaultCacheDir.path,
                it.directory.path
            )
        }

        val cacheDir = File("/sdcard/testDir")
        LruDiskCache(context, logger, _directory = cacheDir).use {
            Assert.assertEquals(
                cacheDir.path,
                it.directory.path
            )
        }
    }

    @Test
    fun testSize() {
        val context = InstrumentationRegistry.getContext()
        val logger = Logger()

        LruDiskCache(context, logger).use {
            it.clear()
            Assert.assertEquals(0L, it.size)
        }

        LruDiskCache(context, logger).use {
            Assert.assertEquals("0B", it.size.formatFileSize())

            it.putFile("file1", 1)
            Assert.assertEquals("1MB", it.size.formatFileSize())

            it.putFile("file2", 2)
            Assert.assertEquals("3MB", it.size.formatFileSize())
        }
    }

    @Test
    fun testPutRemoveGetExist() {
        val context = InstrumentationRegistry.getContext()
        val logger = Logger()

        LruDiskCache(context, logger).use {
            it.clear()
            Assert.assertEquals(0L, it.size)
        }

        LruDiskCache(context, logger, 10L * 1024 * 1024).use {
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
        val context = InstrumentationRegistry.getContext()
        val logger = Logger()

        LruDiskCache(context, logger).use {
            it.clear()
            Assert.assertEquals(0L, it.size)
        }

        LruDiskCache(context, logger, 10L * 1024 * 1024).use {
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
        val context = InstrumentationRegistry.getContext()
        val logger = Logger()

        LruDiskCache(context, logger).use {
            it.clear()
            Assert.assertEquals(0L, it.size)
        }

        LruDiskCache(context, logger).use {
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
        val context = InstrumentationRegistry.getContext()
        val logger = Logger()
        LruDiskCache(context, logger, 10L * 1024 * 1024).use {
            Assert.assertNotNull(it.editLock("file1"))
            Assert.assertNotNull(it.editLock("file2"))
            Assert.assertNotNull(it.editLock("file3"))
            Assert.assertNotNull(it.editLock("file4"))
        }
    }

    @Test
    fun testToString() {
        val context = InstrumentationRegistry.getContext()
        val logger = Logger()

        val defaultCacheDir =
            File(context.externalCacheDir ?: context.cacheDir, DiskCache.DEFAULT_DIR_NAME)
        LruDiskCache(context, logger).use {
            Assert.assertEquals(
                "LruDiskCache(maxSize=512MB,version=1,directory='${defaultCacheDir.path}')",
                it.toString()
            )
        }

        val cacheDir = File("/sdcard/testDir")
        LruDiskCache(
            context,
            logger,
            100L * 1024 * 1024,
            version = 2,
            _directory = cacheDir
        ).use {
            Assert.assertEquals(
                "LruDiskCache(maxSize=100MB,version=2,directory='${cacheDir.path}')",
                it.toString()
            )
        }
    }

    @Test
    fun testSnapshot() {
        val context = InstrumentationRegistry.getContext()
        val logger = Logger()
        val defaultCacheDir =
            File(context.externalCacheDir ?: context.cacheDir, DiskCache.DEFAULT_DIR_NAME)

        LruDiskCache(context, logger).use {
            it.clear()
            Assert.assertEquals(0L, it.size)
        }

        LruDiskCache(context, logger).use {
            it.putFile("file1", 1)
            val file1Snapshot = it["file1"]!!

            Assert.assertEquals(
                "file1",
                file1Snapshot.key
            )

            Assert.assertEquals(
                File(defaultCacheDir, "${MD5Utils.md5("file1")}.0").path,
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
        val context = InstrumentationRegistry.getContext()
        val logger = Logger()

        LruDiskCache(context, logger).use {
            it.clear()
            Assert.assertEquals(0L, it.size)
        }

        LruDiskCache(context, logger).use {
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