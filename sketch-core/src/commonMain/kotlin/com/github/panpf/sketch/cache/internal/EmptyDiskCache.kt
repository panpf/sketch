package com.github.panpf.sketch.cache.internal

import com.github.panpf.sketch.cache.DiskCache
import com.github.panpf.sketch.cache.DiskCache.Editor
import com.github.panpf.sketch.cache.DiskCache.Snapshot
import com.github.panpf.sketch.util.LruCache
import com.github.panpf.sketch.util.formatFileSize
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import okio.ByteString.Companion.encodeUtf8
import okio.FileSystem
import okio.Path

class EmptyDiskCache(
    override val fileSystem: FileSystem,
    override val maxSize: Long,
    override val directory: Path
) : DiskCache {

    companion object {
        private const val MODULE = "EmptyDiskCache"
    }

    // DiskCache is usually used in the decoding stage, and the concurrency of the decoding stage is controlled at 4, so 200 is definitely enough.
    private val mutexMap = LruCache<String, Mutex>(200)
    private val keyMapperCache = KeyMapperCache { it.encodeUtf8().sha256().hex() }

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

    override fun toString(): String = "${MODULE}(${maxSize.formatFileSize()})"
}