/*
 * Copyright (C) 2024 panpf <panpfpanpf@outlook.com>
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

package com.github.panpf.sketch.core.common.test.cache.internal

import com.github.panpf.sketch.cache.DiskCache
import com.github.panpf.sketch.test.utils.block
import com.github.panpf.sketch.test.utils.getTestContext
import com.github.panpf.sketch.test.utils.newAloneTestDiskCacheDirectory
import com.github.panpf.sketch.util.defaultFileSystem
import com.github.panpf.sketch.util.formatFileSize
import com.github.panpf.sketch.util.md5
import kotlinx.coroutines.test.runTest
import okio.Path.Companion.toPath
import okio.buffer
import okio.use
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNotSame
import kotlin.test.assertNull
import kotlin.test.assertTrue

class LruDiskCacheTest {

    // TODO testMaxSize
    // TODO testDirectory

    @Test
    fun testVersion() = runTest {
        val context = getTestContext()
        val fileSystem = defaultFileSystem()

        val directory = context.newAloneTestDiskCacheDirectory()!!.resolve("download")
        try {
            DiskCache.DownloadBuilder(context, fileSystem).apply {
                directory(directory)
            }.build().use {
                it.clear()
                assertEquals(0L, it.size)
            }

            DiskCache.DownloadBuilder(context, fileSystem).apply {
                directory(directory)
            }.build().use {
                assertEquals(1, it.appVersion)
                assertNull(it.openSnapshot("file1").use { it })
                assertNull(it.openSnapshot("file2").use { it })
                it.putFile("file1", 1)
                it.putFile("file2", 1)
                assertNotNull(it.openSnapshot("file1").use { it })
                assertNotNull(it.openSnapshot("file2").use { it })
            }

            DiskCache.DownloadBuilder(context, fileSystem).apply {
                directory(directory)
            }.build().use {
                assertEquals(1, it.appVersion)
                assertNotNull(it.openSnapshot("file1").use { it })
                assertNotNull(it.openSnapshot("file2").use { it })
            }

            DiskCache.DownloadBuilder(context, fileSystem).apply {
                directory(directory)
                appVersion(2)
            }.build().use {
                assertEquals(2, it.appVersion)
                assertNull(it.openSnapshot("file1").use { it })
                assertNull(it.openSnapshot("file2").use { it })
            }

            DiskCache.DownloadBuilder(context, fileSystem).apply {
                directory(directory)
                appVersion(2)
            }.build().use {
                assertEquals(2, it.appVersion)
                assertNull(it.openSnapshot("file1").use { it })
                assertNull(it.openSnapshot("file2").use { it })
                it.putFile("file1", 1)
                it.putFile("file2", 1)
                assertNotNull(it.openSnapshot("file1").use { it })
                assertNotNull(it.openSnapshot("file2").use { it })
            }
        } finally {
            fileSystem.deleteRecursively(directory)
        }
    }

    @Test
    fun testSize() = runTest {
        val context = getTestContext()
        val fileSystem = defaultFileSystem()

        val defaultCacheDir = context.newAloneTestDiskCacheDirectory()
        try {
            DiskCache.DownloadBuilder(context, fileSystem).apply {
                directory(defaultCacheDir)
            }.build().use {
                it.clear()
                assertEquals(0L, it.size)
            }

            DiskCache.DownloadBuilder(context, fileSystem).apply {
                directory(defaultCacheDir)
            }.build().use {
                assertEquals("0B", it.size.formatFileSize())

                it.putFile("file1", 1)
                assertEquals("1MB", it.size.formatFileSize())

                it.putFile("file2", 2)
                assertEquals("3MB", it.size.formatFileSize())
            }
        } finally {
            defaultCacheDir?.let { fileSystem.deleteRecursively(it) }
        }
    }

    @Test
    fun testPutRemoveGetExist() = runTest {
        val context = getTestContext()
        val fileSystem = defaultFileSystem()

        val defaultCacheDir = context.newAloneTestDiskCacheDirectory()
        try {
            DiskCache.DownloadBuilder(context, fileSystem).apply {
                directory(defaultCacheDir)
            }.build().use {
                it.clear()
                assertEquals(0L, it.size)
            }
            DiskCache.DownloadBuilder(context, fileSystem).apply {
                directory(defaultCacheDir)
                maxSize(10L * 1024 * 1024)
            }.build().use {
                assertNull(it.openSnapshot("file1").use { it })
                it.putFile("file1", 1)
                assertNotNull(it.openSnapshot("file1").use { it })

                assertNull(it.openSnapshot("file2").use { it })
                it.putFile("file2", 2)
                assertNotNull(it.openSnapshot("file1").use { it })
                assertNotNull(it.openSnapshot("file2").use { it })

                it.remove("file1")
                assertNull(it.openSnapshot("file1").use { it })
                assertNotNull(it.openSnapshot("file2").use { it })

                it.remove("file2")
                assertNull(it.openSnapshot("file1").use { it })
                assertNull(it.openSnapshot("file2").use { it })
            }
        } finally {
            defaultCacheDir?.let { fileSystem.deleteRecursively(it) }
        }
    }

    @Test
    fun testLRU() = runTest {
        val context = getTestContext()
        val fileSystem = defaultFileSystem()

        val defaultCacheDir = context.newAloneTestDiskCacheDirectory()
        try {
            DiskCache.DownloadBuilder(context, fileSystem).apply {
                directory(defaultCacheDir)
            }.build().use {
                it.clear()
                assertEquals(0L, it.size)
            }

            DiskCache.DownloadBuilder(context, fileSystem).apply {
                directory(defaultCacheDir)
                maxSize(10L * 1024 * 1024)
            }.build().use {
                assertEquals("0B", it.size.formatFileSize())

                it.putFile(fileName = "file1", sizeMB = 1)
                assertEquals("1MB", it.size.formatFileSize())
                assertNotNull(it.openSnapshot("file1").use { it })

                it.putFile(fileName = "file2", sizeMB = 2)
                assertEquals("3MB", it.size.formatFileSize())
                assertNotNull(it.openSnapshot("file1").use { it })
                assertNotNull(it.openSnapshot("file2").use { it })

                it.putFile(fileName = "file3", sizeMB = 3)
                assertEquals("6MB", it.size.formatFileSize())
                assertNotNull(it.openSnapshot("file1").use { it })
                assertNotNull(it.openSnapshot("file2").use { it })
                assertNotNull(it.openSnapshot("file3").use { it })

                it.putFile(fileName = "file4", sizeMB = 4)
                assertEquals("10MB", it.size.formatFileSize())
                assertNotNull(it.openSnapshot("file1").use { it })
                assertNotNull(it.openSnapshot("file2").use { it })
                assertNotNull(it.openSnapshot("file3").use { it })
                assertNotNull(it.openSnapshot("file4").use { it })

                it.putFile(fileName = "file5", sizeMB = 5)
                assertEquals("9MB", it.size.formatFileSize())   // TODO Often fail
                assertNull(it.openSnapshot("file1").use { it })
                assertNull(it.openSnapshot("file2").use { it })
                assertNull(it.openSnapshot("file3").use { it })
                assertNotNull(it.openSnapshot("file4").use { it })
                assertNotNull(it.openSnapshot("file5").use { it })

                it.putFile("file6", 6)
                assertEquals("6MB", it.size.formatFileSize())
                assertNull(it.openSnapshot("file1").use { it })
                assertNull(it.openSnapshot("file2").use { it })
                assertNull(it.openSnapshot("file3").use { it })
                assertNull(it.openSnapshot("file4").use { it })
                assertNull(it.openSnapshot("file5").use { it })
                assertNotNull(it.openSnapshot("file6").use { it })

                it.putFile(fileName = "file7", sizeMB = 7)
                assertEquals("7MB", it.size.formatFileSize())
                assertNull(it.openSnapshot("file1").use { it })
                assertNull(it.openSnapshot("file2").use { it })
                assertNull(it.openSnapshot("file3").use { it })
                assertNull(it.openSnapshot("file4").use { it })
                assertNull(it.openSnapshot("file5").use { it })
                assertNull(it.openSnapshot("file6").use { it })
                assertNotNull(it.openSnapshot("file7").use { it })
            }
        } finally {
            defaultCacheDir?.let { fileSystem.deleteRecursively(it) }
        }
    }

    @Test
    fun testClear() = runTest {
        val context = getTestContext()
        val fileSystem = defaultFileSystem()

        val directory = context.newAloneTestDiskCacheDirectory()
        try {
            DiskCache.DownloadBuilder(context, fileSystem).apply {
                directory(directory)
            }.build().use {
                it.clear()
                assertEquals(0L, it.size)
            }

            DiskCache.DownloadBuilder(context, fileSystem).apply {
                directory(directory)
            }.build().use {
                assertEquals("0B", it.size.formatFileSize())
                it.putFile("file1", 1)
                it.putFile("file2", 2)
                it.putFile("file3", 3)
                it.putFile("file4", 4)
                assertNotNull(it.openSnapshot("file1").use { it })
                assertNotNull(it.openSnapshot("file2").use { it })
                assertNotNull(it.openSnapshot("file3").use { it })
                assertNotNull(it.openSnapshot("file4").use { it })
                assertEquals("10MB", it.size.formatFileSize())

                it.clear()
                assertNull(it.openSnapshot("file1").use { it })
                assertNull(it.openSnapshot("file2").use { it })
                assertNull(it.openSnapshot("file3").use { it })
                assertNull(it.openSnapshot("file4").use { it })
                assertEquals("0B", it.size.formatFileSize())
            }
        } finally {
            directory?.let { fileSystem.deleteRecursively(it) }
        }
    }

    // TODO testWithLock

    @Test
    fun testToString() {
        val context = getTestContext()
        val fileSystem = defaultFileSystem()

        val defaultCacheDir = context.newAloneTestDiskCacheDirectory()
        try {
            DiskCache.DownloadBuilder(context, fileSystem).apply {
                directory(defaultCacheDir)
            }.build().use {
                assertEquals(
                    "LruDiskCache(maxSize=300MB,appVersion=1,internalVersion=${DiskCache.DownloadBuilder.INTERNAL_VERSION},directory='${defaultCacheDir}')",
                    it.toString()
                )
            }

            val cacheDir = "/sdcard/testDir".toPath()
            DiskCache.DownloadBuilder(context, fileSystem).apply {
                directory(cacheDir)
                maxSize(100L * 1024 * 1024)
                appVersion(2)
            }.build().use {
                assertEquals(
                    "LruDiskCache(maxSize=100MB,appVersion=2,internalVersion=${DiskCache.DownloadBuilder.INTERNAL_VERSION},directory='${cacheDir}')",
                    it.toString()
                )
            }
        } finally {
            defaultCacheDir?.let { fileSystem.deleteRecursively(it) }
        }
    }

    @Test
    fun testSnapshot() = runTest {
        val context = getTestContext()
        val fileSystem = defaultFileSystem()
        val defaultCacheDir = context.newAloneTestDiskCacheDirectory()!!

        try {
            DiskCache.DownloadBuilder(context, fileSystem).apply {
                directory(defaultCacheDir)
            }.build().use {
                it.clear()
                assertEquals(0L, it.size)
            }

            DiskCache.DownloadBuilder(context, fileSystem).apply {
                directory(defaultCacheDir)
            }.build().use {
                it.putFile("file1", 1)
                it.openSnapshot("file1")!!.use { file1Snapshot ->
                    assertEquals(
                        defaultCacheDir.resolve("${"file1".md5()}.0"),
                        file1Snapshot.data
                    )
                }

                it.openSnapshot("file1")!!.closeAndOpenEditor()!!.commit()

                assertTrue(it.remove("file1"))
                assertNull(it.openSnapshot("file1"))
            }
        } finally {
            defaultCacheDir.let { fileSystem.deleteRecursively(it) }
        }
    }

    @Test
    fun testEditor() {
        val context = getTestContext()
        val fileSystem = defaultFileSystem()

        val directory = context.newAloneTestDiskCacheDirectory()
        try {
            DiskCache.DownloadBuilder(context, fileSystem).apply {
                directory(directory)
            }.build().use {
                it.clear()
                assertEquals(0L, it.size)
            }

            DiskCache.DownloadBuilder(context, fileSystem).apply {
                directory(directory)
            }.build().use {
                val file1Editor = it.openEditor("file1")!!
                fileSystem.sink(file1Editor.data).buffer().use { outputStream ->
                    outputStream.writeInt(1)
                }
                file1Editor.commit()
                assertNotNull(it.openSnapshot("file1").use { it })

                val file2Editor = it.openEditor("file2")!!
                fileSystem.sink(file2Editor.data).buffer().use { outputStream ->
                    outputStream.writeInt(2)
                }
                file2Editor.abort()
                assertNull(it.openSnapshot("file2").use { it })
            }
        } finally {
            directory?.let { fileSystem.deleteRecursively(it) }
        }
    }

    @Test
    fun testEqualsAndHashCode() {
        val context = getTestContext()
        val fileSystem = defaultFileSystem()
        val element1 = DiskCache.DownloadBuilder(context, defaultFileSystem()).build()
        val element11 = DiskCache.DownloadBuilder(context, defaultFileSystem()).build()
        val element2 = DiskCache.DownloadBuilder(context, fileSystem).maxSize(100).build()
        val element3 =
            DiskCache.DownloadBuilder(context, fileSystem)
                .directory("/sdcard/test".toPath()).build()
        val element4 = DiskCache.DownloadBuilder(context, fileSystem).appVersion(3).build()

        assertNotSame(element1, element11)
        assertNotSame(element1, element2)
        assertNotSame(element2, element11)

        assertEquals(element1, element1)
        assertEquals(element1, element11)
        assertNotEquals(element1, element2)
        assertNotEquals(element1, element3)
        assertNotEquals(element1, element4)
        assertNotEquals(element2, element11)
        assertNotEquals(element1, null as Any?)
        assertNotEquals(element1, Any())

        assertEquals(element1.hashCode(), element1.hashCode())
        assertEquals(element1.hashCode(), element11.hashCode())
        assertNotEquals(element1.hashCode(), element2.hashCode())
        assertNotEquals(element2.hashCode(), element11.hashCode())
    }

    private fun DiskCache.putFile(fileName: String, sizeMB: Int) {
        val sizeBytes = sizeMB * 1024 * 1024
        openEditor(fileName)?.apply {
            try {
                fileSystem.sink(data).buffer().use {
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
        }
        block(100)   // Waiting to calculate size
    }
}