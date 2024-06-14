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

class EmptyDiskCache constructor(override val fileSystem: FileSystem) : DiskCache {

    // DiskCache is usually used in the decoding stage, and the concurrency of the decoding stage is controlled at 4, so 200 is definitely enough.
    private val mutexMap = LruCache<String, Mutex>(200)
    private val keyMapperCache = KeyMapperCache { it.md5() }

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
        val encodedKey = keyMapperCache.mapKey(key)
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
        return other is EmptyDiskCache
    }

    override fun hashCode(): Int {
        return this@EmptyDiskCache::class.hashCode()
    }

    override fun toString(): String = "EmptyDiskCache"
}