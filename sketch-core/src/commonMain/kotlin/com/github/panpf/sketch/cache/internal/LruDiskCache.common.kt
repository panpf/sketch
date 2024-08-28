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
package com.github.panpf.sketch.cache.internal

import com.github.panpf.sketch.PlatformContext
import com.github.panpf.sketch.cache.DiskCache
import com.github.panpf.sketch.cache.DiskCache.Editor
import com.github.panpf.sketch.cache.DiskCache.Snapshot
import com.github.panpf.sketch.util.LruCache
import com.github.panpf.sketch.util.formatFileSize
import com.github.panpf.sketch.util.intMerged
import com.github.panpf.sketch.util.ioCoroutineDispatcher
import com.github.panpf.sketch.util.md5
import kotlinx.atomicfu.locks.SynchronizedObject
import kotlinx.atomicfu.locks.synchronized
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import okio.FileSystem
import okio.Path

/**
 * Check whether the disk cache directory meets the requirements of the platform.
 * If it does not meet the requirements, it will try to repair it.
 *
 * @see com.github.panpf.sketch.core.android.test.cache.internal.LruDiskCacheAndroidTest.testCheckDiskCacheDirectory
 * @see com.github.panpf.sketch.core.nonandroid.test.cache.internal.LruDiskCacheNonAndroidTest.testCheckDiskCacheDirectory
 */
expect fun checkDiskCacheDirectory(context: PlatformContext, directory: Path): Path

/**
 * A disk cache that manages the cache according to a least-used rule
 *
 * @see com.github.panpf.sketch.core.test.cache.internal.LruDiskCacheTest
 */
@Suppress("FoldInitializerAndIfToElvis")
class LruDiskCache(
    context: PlatformContext,
    override val fileSystem: FileSystem,
    override val maxSize: Long,
    directory: Path,
    override val appVersion: Int,
    override val internalVersion: Int,
) : DiskCache {

    companion object {
        private const val ENTRY_DATA = 0
        private const val ENTRY_METADATA = 1
    }

    override val directory: Path by lazy { checkDiskCacheDirectory(context, directory) }

    // DiskCache is usually used in the decoding stage,
    //  and the concurrency of the decoding stage is controlled at 4, so 200 is definitely enough.
    private val mutexMap = LruCache<String, Mutex>(200)
    private val mutexMapLock = SynchronizedObject()

    private val cache: DiskLruCache by lazy {
        val unionVersion = intMerged(appVersion, internalVersion)
        DiskLruCache(
            fileSystem = fileSystem,
            directory = this@LruDiskCache.directory,
            cleanupDispatcher = ioCoroutineDispatcher(),
            maxSize = maxSize,
            appVersion = unionVersion,
            valueCount = 2,  // data and metadata
        )
    }

    override val size: Long get() = cache.size()

    override fun openSnapshot(key: String): Snapshot? {
        val encodedKey = key.md5()
        val snapshot = cache[encodedKey]
        if (snapshot == null) return null   // for debug
        return MySnapshot(snapshot)
    }

    override fun openEditor(key: String): Editor? {
        val encodedKey = key.md5()
        val editor = cache.edit(encodedKey)
        if (editor == null) return null   // for debug
        return MyEditor(editor)
    }

    override fun remove(key: String): Boolean {
        val encodedKey = key.md5()
        val removed = cache.remove(encodedKey)   // for debug
        return removed
    }

    override fun clear() {
        runCatching {
            cache.evictAll()
        }.onFailure {
            it.printStackTrace()
        }
    }

    override suspend fun <R> withLock(key: String, action: suspend DiskCache.() -> R): R {
        val encodedKey = key.md5()
        val lock = synchronized(mutexMapLock) {
            mutexMap[encodedKey] ?: Mutex().apply {
                this@LruDiskCache.mutexMap.put(encodedKey, this)
            }
        }
        return lock.withLock {
            action(this@LruDiskCache)
        }
    }

    /**
     * It can still be used after closing, and will reopen a new DiskLruCache
     */
    override fun close() {
        runCatching {
            cache.close()
        }.onFailure {
            it.printStackTrace()
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is LruDiskCache) return false
        if (maxSize != other.maxSize) return false
        if (directory != other.directory) return false
        if (appVersion != other.appVersion) return false
        if (internalVersion != other.internalVersion) return false
        return true
    }

    override fun hashCode(): Int {
        var result = maxSize.hashCode()
        result = 31 * result + directory.hashCode()
        result = 31 * result + appVersion
        result = 31 * result + internalVersion
        return result
    }

    override fun toString(): String = buildString {
        append("LruDiskCache(")
        append("maxSize=${maxSize.formatFileSize()},")
        append("appVersion=${appVersion},")
        append("internalVersion=${internalVersion},")
        append("directory='${directory}")
        append("')")
    }

    private class MySnapshot(private val snapshot: DiskLruCache.Snapshot) : Snapshot {

        override val data: Path = snapshot.file(ENTRY_DATA)

        override val metadata: Path = snapshot.file(ENTRY_METADATA)

        override fun close() {
            snapshot.close()
        }

        override fun closeAndOpenEditor(): Editor? {
            val editor = snapshot.closeAndEdit()
            if (editor == null) return null
            return MyEditor(editor)
        }
    }

    private class MyEditor(private val editor: DiskLruCache.Editor) : Editor {

        override val data: Path = editor.file(ENTRY_DATA)

        override val metadata: Path = editor.file(ENTRY_METADATA)

        override fun commit() = editor.commit()

        override fun commitAndOpenSnapshot(): Snapshot? {
            val snapshot = editor.commitAndGet()
            if (snapshot == null) return null
            return MySnapshot(snapshot)
        }

        override fun abort() = editor.abort()
    }
}