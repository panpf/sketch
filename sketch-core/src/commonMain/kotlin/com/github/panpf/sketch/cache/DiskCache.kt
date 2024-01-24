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
package com.github.panpf.sketch.cache

import com.github.panpf.sketch.PlatformContext
import com.github.panpf.sketch.cache.DiskCache.Options
import com.github.panpf.sketch.util.Logger
import kotlinx.coroutines.sync.Mutex
import java.io.Closeable
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

/**
 * Disk cache for bitmap or uri data
 */
interface DiskCache : Closeable {

    companion object {
        const val DEFAULT_DIR_NAME = "sketch4"
    }

    var logger: Logger?

    /**
     * Get the cache directory on disk
     */
    val directory: File

    /**
     * Maximum allowed sum of the size of the all cache
     */
    val maxSize: Long

    /**
     * Sum of the size of the all cache
     */
    val size: Long

    /**
     * Returns an editor for the entry named [key], or null if another
     * edit is in progress.
     */
    fun edit(key: String): Editor?

    /**
     * Drops the entry for [key] if it exists and can be removed. Entries
     * actively being edited cannot be removed.
     *
     * @return true if an entry was removed.
     */
    fun remove(key: String): Boolean

    /**
     * Returns a snapshot of the entry named [key], or null if it doesn't exist.
     */
    operator fun get(key: String): Snapshot?

    /**
     * Returns exist of the entry named [key]
     */
    fun exist(key: String): Boolean

    /**
     * Clear all cached
     */
    fun clear()

    /**
     * Gets an edit lock bound to the specified [key], or creates a new one if it does not exist
     */
    fun editLock(key: String): Mutex

    /**
     * Snapshot the values for an entry.
     */
    interface Snapshot {
        /**
         * Returns cache key
         */
        val key: String

        /**
         * Returns cache file
         */
        val file: File

        /**
         * Returns the unbuffered stream
         */
        @Throws(IOException::class)
        fun newInputStream(): InputStream

        /**
         * Returns an editor, or null if another edit is in progress.
         */
        fun edit(): Editor?

        /**
         * Delete cache file
         *
         * @return If true is returned, the deletion is successful
         */
        fun remove(): Boolean
    }

    /**
     * Edits the values for an entry.
     */
    interface Editor {
        /**
         * Returns a new unbuffered output stream.
         * Call [commit] when you write done, or call [abort] if you don't want it
         */
        @Throws(IOException::class)
        fun newOutputStream(): OutputStream

        /**
         * Commits this edit so it is visible to readers.  This releases the
         * edit lock so another edit may be started on the same key.
         */
        @Throws(IOException::class)
        fun commit()

        /**
         * Aborts this edit. This releases the edit lock so another edit may be
         * started on the same key.
         */
        fun abort()
    }

    data class Options(
        val directory: File? = null,
        val maxSize: Long? = null,
        val appVersion: Int? = null,
    ) {
        init {
            require(maxSize == null || maxSize > 0) {
                "maxSize must be greater than 0"
            }
            require(appVersion == null || appVersion in 1.rangeTo(Short.MAX_VALUE)) {
                "The value range for 'version' is 1 to ${Short.MAX_VALUE}"
            }
        }
    }

    interface Factory {
        fun create(context: PlatformContext): DiskCache
    }

    class FixedFactory(val diskCache: DiskCache) : Factory {
        override fun create(context: PlatformContext): DiskCache {
            return diskCache
        }
    }

    class OptionsFactory(val type: Type, val options: Options) : Factory {
        override fun create(context: PlatformContext): DiskCache {
            return createDiskCache(context, type, options)
        }
    }

    class LazyFactory(
        val initializer: (PlatformContext) -> DiskCache
    ) : Factory {
        override fun create(context: PlatformContext): DiskCache {
            return initializer(context)
        }
    }

    class LazyOptionsFactory(
        val type: Type,
        val initializer: LazyOptions
    ) : Factory {
        override fun create(context: PlatformContext): DiskCache {
            val options = initializer.get(context)
            return createDiskCache(context, type, options)
        }
    }

    fun interface LazyOptions{
        fun get(context: PlatformContext): Options
    }

    class DefaultFactory(val type: Type) : Factory {
        override fun create(context: PlatformContext): DiskCache {
            return createDiskCache(context, type, null)
        }
    }

    enum class Type(val dirName: String, val internalVersion: Int) {
        DOWNLOAD("download", 1),
        RESULT("result", 1),
    }
}

expect fun createDiskCache(
    context: PlatformContext,
    type: DiskCache.Type,
    options: Options?
): DiskCache