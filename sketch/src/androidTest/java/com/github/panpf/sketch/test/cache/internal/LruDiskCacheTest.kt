package com.github.panpf.sketch.test.cache.internal

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.cache.internal.LruDiskCache
import com.github.panpf.sketch.test.utils.getContext
import com.github.panpf.sketch.test.utils.newTestDiskCacheDirectory
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
        val context = getContext()

        val defaultCacheDir = context.newTestDiskCacheDirectory()
        LruDiskCache(context, directory = defaultCacheDir).use {
            Assert.assertEquals("512MB", it.maxSize.formatFileSize())
        }

        LruDiskCache(context, maxSize = 100L * 1024 * 1024, directory = defaultCacheDir).use {
            Assert.assertEquals("100MB", it.maxSize.formatFileSize())
        }
    }

    @Test
    fun testVersion() {
        val context = getContext()

        val directory = context.newTestDiskCacheDirectory()
        LruDiskCache(context, directory = directory).use {
            it.clear()
            Assert.assertEquals(0L, it.size)
        }

        LruDiskCache(context, directory = directory).use {
            Assert.assertEquals(1, it.version)
            Assert.assertNull(it["file1"])
            Assert.assertNull(it["file2"])
            it.putFile("file1", 1)
            it.putFile("file2", 1)
            Assert.assertNotNull(it["file1"])
            Assert.assertNotNull(it["file2"])
        }

        LruDiskCache(context, directory = directory).use {
            Assert.assertEquals(1, it.version)
            Assert.assertNotNull(it["file1"])
            Assert.assertNotNull(it["file2"])
        }

        LruDiskCache(context, version = 2, directory = directory).use {
            Assert.assertEquals(2, it.version)
            Assert.assertNull(it["file1"])
            Assert.assertNull(it["file2"])
        }
    }

    @Test
    fun testDirectory() {
        val context = getContext()

        val defaultCacheDir = context.newTestDiskCacheDirectory()
        LruDiskCache(context, directory = defaultCacheDir).use {
            Assert.assertEquals(defaultCacheDir.path, it.directory.path)
        }

        val cacheDir = File("/sdcard/testDir")
        LruDiskCache(context, directory = cacheDir).use {
            Assert.assertEquals(
                cacheDir.path,
                it.directory.path
            )
        }
    }

    @Test
    fun testSize() {
        val context = getContext()

        val defaultCacheDir = context.newTestDiskCacheDirectory()
        LruDiskCache(context, directory = defaultCacheDir).use {
            it.clear()
            Assert.assertEquals(0L, it.size)
        }

        LruDiskCache(context, directory = defaultCacheDir).use {
            Assert.assertEquals("0B", it.size.formatFileSize())

            it.putFile("file1", 1)
            Assert.assertEquals("1MB", it.size.formatFileSize())

            it.putFile("file2", 2)
            Assert.assertEquals("3MB", it.size.formatFileSize())
        }
    }

    @Test
    fun testPutRemoveGetExist() {
        val context = getContext()

        val defaultCacheDir = context.newTestDiskCacheDirectory()
        LruDiskCache(context, directory = defaultCacheDir).use {
            it.clear()
            Assert.assertEquals(0L, it.size)
        }

        LruDiskCache(
            context,
            10L * 1024 * 1024,
            directory = defaultCacheDir
        ).use {
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
        val context = getContext()

        val defaultCacheDir = context.newTestDiskCacheDirectory()
        LruDiskCache(context, directory = defaultCacheDir).use {
            it.clear()
            Assert.assertEquals(0L, it.size)
        }

        LruDiskCache(context, 10L * 1024 * 1024, directory = defaultCacheDir).use {
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
        val context = getContext()

        val directory = context.newTestDiskCacheDirectory()
        LruDiskCache(context, directory = directory).use {
            it.clear()
            Assert.assertEquals(0L, it.size)
        }

        LruDiskCache(context, directory = directory).use {
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
        val context = getContext()
        LruDiskCache(
            context,
            10L * 1024 * 1024,
            directory = context.newTestDiskCacheDirectory()
        ).use {
            Assert.assertNotNull(it.editLock("file1"))
            Assert.assertNotNull(it.editLock("file2"))
            Assert.assertNotNull(it.editLock("file3"))
            Assert.assertNotNull(it.editLock("file4"))
        }
    }

    @Test
    fun testToString() {
        val context = getContext()

        val defaultCacheDir = context.newTestDiskCacheDirectory()
        LruDiskCache(context, directory = defaultCacheDir).use {
            Assert.assertEquals(
                "LruDiskCache(maxSize=512MB,version=1,directory='${defaultCacheDir.path}')",
                it.toString()
            )
        }

        val cacheDir = File("/sdcard/testDir")
        LruDiskCache(context, 100L * 1024 * 1024, version = 2, directory = cacheDir).use {
            Assert.assertEquals(
                "LruDiskCache(maxSize=100MB,version=2,directory='${cacheDir.path}')",
                it.toString()
            )
        }
    }

    @Test
    fun testSnapshot() {
        val context = getContext()
        val defaultCacheDir = context.newTestDiskCacheDirectory()

        LruDiskCache(context, directory = defaultCacheDir).use {
            it.clear()
            Assert.assertEquals(0L, it.size)
        }

        LruDiskCache(context, directory = defaultCacheDir).use {
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
        val context = getContext()

        val directory = context.newTestDiskCacheDirectory()
        LruDiskCache(context, directory = directory).use {
            it.clear()
            Assert.assertEquals(0L, it.size)
        }

        LruDiskCache(context, directory = directory).use {
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