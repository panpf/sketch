package com.github.panpf.sketch.cache.internal

import com.github.panpf.sketch.cache.DiskCache
import com.github.panpf.sketch.cache.DiskCache.Editor
import com.github.panpf.sketch.cache.DiskCache.Snapshot
import com.github.panpf.sketch.util.Logger
import com.github.panpf.sketch.util.formatFileSize
import kotlinx.coroutines.sync.Mutex
import okio.ByteString.Companion.encodeUtf8
import java.io.File
import java.util.WeakHashMap

class EmptyDiskCache(
    override val maxSize: Long,
    override val directory: File
) : DiskCache {

    companion object {
        private const val MODULE = "EmptyDiskCache"

        @JvmStatic
        private val editLockLock = Any()
    }

    private val keyMapperCache = KeyMapperCache { it.encodeUtf8().sha256().hex() }
    private val editLockMap: MutableMap<String, Mutex> = WeakHashMap()

    override var logger: Logger? = null

    override val size: Long = 0L

    override fun edit(key: String): Editor? = null

    override fun remove(key: String): Boolean = false

    override fun get(key: String): Snapshot? = null

    override fun exist(key: String): Boolean = false

    override fun clear() {

    }

    override fun editLock(key: String): Mutex = synchronized(editLockLock) {
        val encodedKey = keyMapperCache.mapKey(key)
        editLockMap[encodedKey] ?: Mutex().apply {
            this@EmptyDiskCache.editLockMap[encodedKey] = this
        }
    }

    override fun close() {
    }

    override fun toString(): String = "${MODULE}(${maxSize.formatFileSize()})"
}