/*
 * Copyright (C) 2022 panpf <panpfpanpf@outlook.com>
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.panpf.sketch.core.android.test.cache.internal

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.cache.DiskCache
import com.github.panpf.sketch.test.utils.getTestContext
import com.github.panpf.sketch.test.utils.newAloneTestDiskCacheDirectory
import com.github.panpf.sketch.util.formatFileSize
import com.github.panpf.sketch.util.sha256String
import okio.FileSystem
import okio.Path.Companion.toOkioPath
import okio.Source
import okio.buffer
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import java.io.FileOutputStream

@RunWith(AndroidJUnit4::class)
class LruDiskCacheTest {

    // TODO testMaxSize
    // TODO testDirectory

    @Test
    fun testVersion() {
        val context = getTestContext()
        val fileSystem = FileSystem.SYSTEM

        val directory = context.newAloneTestDiskCacheDirectory()!!.resolve("download")
        DiskCache.DownloadBuilder(context, fileSystem).apply {
            directory(directory)
        }.build().use {
            it.clear()
            Assert.assertEquals(0L, it.size)
        }

        DiskCache.DownloadBuilder(context, fileSystem).apply {
            directory(directory)
        }.build().use {
            Assert.assertEquals(1, it.appVersion)
            Assert.assertNull(it.openSnapshot("file1").use { it })
            Assert.assertNull(it.openSnapshot("file2").use { it })
            it.putFile("file1", 1)
            it.putFile("file2", 1)
            Assert.assertNotNull(it.openSnapshot("file1").use { it })
            Assert.assertNotNull(it.openSnapshot("file2").use { it })
        }

        DiskCache.DownloadBuilder(context, fileSystem).apply {
            directory(directory)
        }.build().use {
            Assert.assertEquals(1, it.appVersion)
            Assert.assertNotNull(it.openSnapshot("file1").use { it })
            Assert.assertNotNull(it.openSnapshot("file2").use { it })
        }

        DiskCache.DownloadBuilder(context, fileSystem).apply {
            directory(directory)
            appVersion(2)
        }.build().use {
            Assert.assertEquals(2, it.appVersion)
            Assert.assertNull(it.openSnapshot("file1").use { it })
            Assert.assertNull(it.openSnapshot("file2").use { it })
        }

        DiskCache.DownloadBuilder(context, fileSystem).apply {
            directory(directory)
            appVersion(2)
        }.build().use {
            Assert.assertEquals(2, it.appVersion)
            Assert.assertNull(it.openSnapshot("file1").use { it })
            Assert.assertNull(it.openSnapshot("file2").use { it })
            it.putFile("file1", 1)
            it.putFile("file2", 1)
            Assert.assertNotNull(it.openSnapshot("file1").use { it })
            Assert.assertNotNull(it.openSnapshot("file2").use { it })
        }
    }

    @Test
    fun testSize() {
        val context = getTestContext()
        val fileSystem = FileSystem.SYSTEM

        val defaultCacheDir = context.newAloneTestDiskCacheDirectory()
        DiskCache.DownloadBuilder(context, fileSystem).apply {
            directory(defaultCacheDir)
        }.build().use {
            it.clear()
            Assert.assertEquals(0L, it.size)
        }

        DiskCache.DownloadBuilder(context, fileSystem).apply {
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
        val fileSystem = FileSystem.SYSTEM

        val defaultCacheDir = context.newAloneTestDiskCacheDirectory()
        DiskCache.DownloadBuilder(context, fileSystem).apply {
            directory(defaultCacheDir)
        }.build().use {
            it.clear()
            Assert.assertEquals(0L, it.size)
        }
        DiskCache.DownloadBuilder(context, fileSystem).apply {
            directory(defaultCacheDir)
            maxSize(10L * 1024 * 1024)
        }.build().use {
            Assert.assertNull(it.openSnapshot("file1").use { it })
            it.putFile("file1", 1)
            Assert.assertNotNull(it.openSnapshot("file1").use { it })

            Assert.assertNull(it.openSnapshot("file2").use { it })
            it.putFile("file2", 2)
            Assert.assertNotNull(it.openSnapshot("file1").use { it })
            Assert.assertNotNull(it.openSnapshot("file2").use { it })

            it.remove("file1")
            Assert.assertNull(it.openSnapshot("file1").use { it })
            Assert.assertNotNull(it.openSnapshot("file2").use { it })

            it.remove("file2")
            Assert.assertNull(it.openSnapshot("file1").use { it })
            Assert.assertNull(it.openSnapshot("file2").use { it })
        }
    }

    @Test
    fun testLRU() {
        val context = getTestContext()
        val fileSystem = FileSystem.SYSTEM

        val defaultCacheDir = context.newAloneTestDiskCacheDirectory()
        DiskCache.DownloadBuilder(context, fileSystem).apply {
            directory(defaultCacheDir)
        }.build().use {
            it.clear()
            Assert.assertEquals(0L, it.size)
        }

        DiskCache.DownloadBuilder(context, fileSystem).apply {
            directory(defaultCacheDir)
            maxSize(10L * 1024 * 1024)
        }.build().use {
            Assert.assertEquals("0B", it.size.formatFileSize())

            it.putFile("file1", 1)
            Assert.assertEquals("1MB", it.size.formatFileSize())
            Assert.assertNotNull(it.openSnapshot("file1").use { it })

            it.putFile("file2", 2)
            Assert.assertEquals("3MB", it.size.formatFileSize())
            Assert.assertNotNull(it.openSnapshot("file1").use { it })
            Assert.assertNotNull(it.openSnapshot("file2").use { it })

            it.putFile("file3", 3)
            Assert.assertEquals("6MB", it.size.formatFileSize())
            Assert.assertNotNull(it.openSnapshot("file1").use { it })
            Assert.assertNotNull(it.openSnapshot("file2").use { it })
            Assert.assertNotNull(it.openSnapshot("file3").use { it })

            it.putFile("file4", 4)
            Assert.assertEquals("10MB", it.size.formatFileSize())
            Assert.assertNotNull(it.openSnapshot("file1").use { it })
            Assert.assertNotNull(it.openSnapshot("file2").use { it })
            Assert.assertNotNull(it.openSnapshot("file3").use { it })
            Assert.assertNotNull(it.openSnapshot("file4").use { it })

            it.putFile("file5", 5)
            Assert.assertEquals("9MB", it.size.formatFileSize())
            Assert.assertNull(it.openSnapshot("file1").use { it })
            Assert.assertNull(it.openSnapshot("file2").use { it })
            Assert.assertNull(it.openSnapshot("file3").use { it })
            Assert.assertNotNull(it.openSnapshot("file4").use { it })
            Assert.assertNotNull(it.openSnapshot("file5").use { it })

            it.putFile("file6", 6)
            Assert.assertEquals("6MB", it.size.formatFileSize())
            Assert.assertNull(it.openSnapshot("file1").use { it })
            Assert.assertNull(it.openSnapshot("file2").use { it })
            Assert.assertNull(it.openSnapshot("file3").use { it })
            Assert.assertNull(it.openSnapshot("file4").use { it })
            Assert.assertNull(it.openSnapshot("file5").use { it })
            Assert.assertNotNull(it.openSnapshot("file6").use { it })

            it.putFile("file7", 7)
            Assert.assertEquals("7MB", it.size.formatFileSize())
            Assert.assertNull(it.openSnapshot("file1").use { it })
            Assert.assertNull(it.openSnapshot("file2").use { it })
            Assert.assertNull(it.openSnapshot("file3").use { it })
            Assert.assertNull(it.openSnapshot("file4").use { it })
            Assert.assertNull(it.openSnapshot("file5").use { it })
            Assert.assertNull(it.openSnapshot("file6").use { it })
            Assert.assertNotNull(it.openSnapshot("file7").use { it })
        }
    }

    @Test
    fun testClear() {
        val context = getTestContext()
        val fileSystem = FileSystem.SYSTEM

        val directory = context.newAloneTestDiskCacheDirectory()
        DiskCache.DownloadBuilder(context, fileSystem).apply {
            directory(directory)
        }.build().use {
            it.clear()
            Assert.assertEquals(0L, it.size)
        }

        DiskCache.DownloadBuilder(context, fileSystem).apply {
            directory(directory)
        }.build().use {
            Assert.assertEquals("0B", it.size.formatFileSize())
            it.putFile("file1", 1)
            it.putFile("file2", 2)
            it.putFile("file3", 3)
            it.putFile("file4", 4)
            Assert.assertNotNull(it.openSnapshot("file1").use { it })
            Assert.assertNotNull(it.openSnapshot("file2").use { it })
            Assert.assertNotNull(it.openSnapshot("file3").use { it })
            Assert.assertNotNull(it.openSnapshot("file4").use { it })
            Assert.assertEquals("10MB", it.size.formatFileSize())

            it.clear()
            Assert.assertNull(it.openSnapshot("file1").use { it })
            Assert.assertNull(it.openSnapshot("file2").use { it })
            Assert.assertNull(it.openSnapshot("file3").use { it })
            Assert.assertNull(it.openSnapshot("file4").use { it })
            Assert.assertEquals("0B", it.size.formatFileSize())
        }
    }

    // TODO testWithLock

    @Test
    fun testToString() {
        val context = getTestContext()
        val fileSystem = FileSystem.SYSTEM

        val defaultCacheDir = context.newAloneTestDiskCacheDirectory()
        DiskCache.DownloadBuilder(context, fileSystem).apply {
            directory(defaultCacheDir)
        }.build().use {
            Assert.assertEquals(
                "LruDiskCache(maxSize=300MB,appVersion=1,internalVersion=${DiskCache.DownloadBuilder.INTERNAL_VERSION},directory='${defaultCacheDir}')",
                it.toString()
            )
        }

        val cacheDir = File("/sdcard/testDir").toOkioPath()
        DiskCache.DownloadBuilder(context, fileSystem).apply {
            directory(cacheDir)
            maxSize(100L * 1024 * 1024)
            appVersion(2)
        }.build().use {
            Assert.assertEquals(
                "LruDiskCache(maxSize=100MB,appVersion=2,internalVersion=${DiskCache.DownloadBuilder.INTERNAL_VERSION},directory='${cacheDir}')",
                it.toString()
            )
        }
    }

    @Test
    fun testSnapshot() {
        val context = getTestContext()
        val fileSystem = FileSystem.SYSTEM
        val defaultCacheDir = context.newAloneTestDiskCacheDirectory()!!

        DiskCache.DownloadBuilder(context, fileSystem).apply {
            directory(defaultCacheDir)
        }.build().use {
            it.clear()
            Assert.assertEquals(0L, it.size)
        }

        DiskCache.DownloadBuilder(context, fileSystem).apply {
            directory(defaultCacheDir)
        }.build().use {
            it.putFile("file1", 1)
            it.openSnapshot("file1")!!.use { file1Snapshot ->
                Assert.assertEquals(
                    defaultCacheDir.resolve("${"file1".sha256String()}.0"),
                    file1Snapshot.data
                )

                fileSystem.source(file1Snapshot.data).use { input ->
                    Assert.assertEquals(Source::class, input::class)
                }
            }

            it.openSnapshot("file1")!!.closeAndOpenEditor()!!.commit()

            Assert.assertTrue(it.remove("file1"))
            Assert.assertNull(it.openSnapshot("file1"))
        }
    }

    @Test
    fun testEditor() {
        val context = getTestContext()
        val fileSystem = FileSystem.SYSTEM

        val directory = context.newAloneTestDiskCacheDirectory()
        DiskCache.DownloadBuilder(context, fileSystem).apply {
            directory(directory)
        }.build().use {
            it.clear()
            Assert.assertEquals(0L, it.size)
        }

        DiskCache.DownloadBuilder(context, fileSystem).apply {
            directory(directory)
        }.build().use {
            val file1Editor = it.openEditor("file1")!!
            fileSystem.sink(file1Editor.data).buffer().use { outputStream ->
                outputStream.writeInt(1)
            }
            file1Editor.commit()
            Assert.assertNotNull(it.openSnapshot("file1").use { it })

            val file2Editor = it.openEditor("file2")!!
            fileSystem.sink(file2Editor.data).buffer().use { outputStream ->
                outputStream.writeInt(2)
            }
            file2Editor.abort()
            Assert.assertNull(it.openSnapshot("file2").use { it })
        }
    }

    @Test
    fun testEqualsAndHashCode() {
        val context = getTestContext()
        val fileSystem = FileSystem.SYSTEM
        val element1 = DiskCache.DownloadBuilder(context, FileSystem.SYSTEM).build()
        val element11 = DiskCache.DownloadBuilder(context, FileSystem.SYSTEM).build()
        val element2 = DiskCache.DownloadBuilder(context, fileSystem).maxSize(100).build()
        val element3 =
            DiskCache.DownloadBuilder(context, fileSystem)
                .directory(File("/sdcard/test").toOkioPath()).build()
        val element4 = DiskCache.DownloadBuilder(context, fileSystem).appVersion(3).build()

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

    private fun DiskCache.putFile(fileName: String, sizeMB: Int) {
        val sizeBytes = sizeMB * 1024 * 1024
        openEditor(fileName)?.apply {
            try {
                FileOutputStream(data.toFile()).buffered().use {
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