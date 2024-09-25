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

package com.github.panpf.sketch.cache.internal

import com.github.panpf.sketch.cache.DiskCache
import com.github.panpf.sketch.cache.DiskCache.Editor
import com.github.panpf.sketch.cache.DiskCache.Snapshot
import com.github.panpf.sketch.util.LruCache
import com.github.panpf.sketch.util.md5
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import okio.FileSystem
import okio.Path
import okio.Path.Companion.toPath

/**
 * An empty implementation of [DiskCache].
 *
 * @see com.github.panpf.sketch.core.common.test.cache.internal.EmptyDiskCacheTest
 */
class EmptyDiskCache(override val fileSystem: FileSystem) : DiskCache {

    // DiskCache is usually used in the decoding stage,
    //  and the concurrency of the decoding stage is controlled at 4, so 200 is definitely enough.
    private val mutexMap = LruCache<String, Mutex>(200)

    override val maxSize: Long = 0L

    override val directory: Path = "".toPath()

    override val appVersion: Int get() = 0

    override val internalVersion: Int get() = 0

    override val size: Long = 0L

    override fun openEditor(key: String): Editor? = null

    override fun remove(key: String): Boolean = false

    override fun openSnapshot(key: String): Snapshot? = null

    override fun clear() {

    }

    override suspend fun <R> withLock(key: String, action: suspend DiskCache.() -> R): R {
        val encodedKey = key.md5()
        val lock = mutexMap[encodedKey] ?: Mutex().apply {
            this@EmptyDiskCache.mutexMap.put(encodedKey, this)
        }
        return lock.withLock {
            action(this@EmptyDiskCache)
        }
    }

    override fun close() {

    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as EmptyDiskCache
        if (fileSystem != other.fileSystem) return false
        return true
    }

    override fun hashCode(): Int {
        return fileSystem.hashCode()
    }

    override fun toString(): String = "EmptyDiskCache"
}