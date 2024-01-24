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
import com.github.panpf.sketch.cache.internal.LruDiskCache
import com.github.panpf.sketch.util.Logger
import kotlinx.coroutines.sync.Mutex
import okio.Closeable
import okio.FileSystem
import okio.Path

/**
 * Disk cache for bitmap or uri data
 */
interface DiskCache : Closeable {

    companion object {
        const val DEFAULT_DIR_NAME = "sketch4"
    }

    var logger: Logger?

    val fileSystem: FileSystem

    /**
     * Get the cache directory on disk
     */
    val directory: Path

    /**
     * Maximum allowed sum of the size of the all cache
     */
    val maxSize: Long

    /**
     * Sum of the size of the all cache
     */
    val size: Long

    /**
     * Read the entry associated with [key].
     *
     * IMPORTANT: **You must** call either [Snapshot.close] or [Snapshot.closeAndOpenEditor] when
     * finished reading the snapshot. An open snapshot prevents opening a new [Editor] or deleting
     * the entry on disk.
     */
    fun openSnapshot(key: String): Snapshot?

    /**
     * Write to the entry associated with [key].
     *
     * IMPORTANT: **You must** call one of [Editor.commit], [Editor.commitAndOpenSnapshot], or
     * [Editor.abort] to complete the edit. An open editor prevents opening a new [Snapshot],
     * opening a new [Editor], or deleting the entry on disk.
     */
    fun openEditor(key: String): Editor?

    /**
     * Delete the entry referenced by [key].
     *
     * @return 'true' if [key] was removed successfully. Else, return 'false'.
     */
    fun remove(key: String): Boolean

//    /**
//     * Returns exist of the entry named [key]
//     */
//    fun exist(key: String): Boolean = get(key) != null

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
    interface Snapshot : Closeable {
//        /**
//         * Returns cache key
//         */
//        val key: String
//
//        /**
//         * Returns cache file
//         */
//        val file: File
//
//        /**
//         * Returns the unbuffered stream
//         */
//        @Throws(IOException::class)
//        fun newInputStream(): InputStream
//
//        /**
//         * Returns an editor, or null if another edit is in progress.
//         */
//        fun edit(): Editor?
//
//        /**
//         * Delete cache file
//         *
//         * @return If true is returned, the deletion is successful
//         */
//        fun remove(): Boolean

        /** Get the metadata file path for this entry. */
        val metadata: Path

        /** Get the data file path for this entry. */
        val data: Path

        /** Close the snapshot to allow editing. */
        override fun close()

        /** Close the snapshot and call [openEditor] for this entry atomically. */
        fun closeAndOpenEditor(): Editor?
    }

    /**
     * Edits the values for an entry.
     */
    interface Editor {
//        /**
//         * Returns a new unbuffered output stream.
//         * Call [commit] when you write done, or call [abort] if you don't want it
//         */
//        @Throws(IOException::class)
//        fun newOutputStream(): OutputStream
//
//        /**
//         * Commits this edit so it is visible to readers.  This releases the
//         * edit lock so another edit may be started on the same key.
//         */
//        @Throws(IOException::class)
//        fun commit()
//
//        /**
//         * Aborts this edit. This releases the edit lock so another edit may be
//         * started on the same key.
//         */
//        fun abort()

        /** Get the metadata file path for this entry. */
        val metadata: Path

        /** Get the data file path for this entry. */
        val data: Path

        /** Commit the edit so the changes are visible to readers. */
        fun commit()

        /** Commit the write and call [openSnapshot] for this entry atomically. */
        fun commitAndOpenSnapshot(): Snapshot?

        /** Abort the edit. Any written data will be discarded. */
        fun abort()
    }

    data class Options(
        val directory: Path? = null,
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
        fun create(context: PlatformContext, fileSystem: FileSystem): DiskCache
    }

    class FixedFactory(val diskCache: DiskCache) : Factory {
        override fun create(context: PlatformContext, fileSystem: FileSystem): DiskCache {
            return diskCache
        }
    }

    class OptionsFactory(
        val type: Type,
        val options: Options
    ) : Factory {
        override fun create(context: PlatformContext, fileSystem: FileSystem): DiskCache {
            val defaultOptions = defaultDiskCacheOptions(context, type)
            return LruDiskCache(
                context = context,
                fileSystem = fileSystem,
                maxSize = options.maxSize ?: defaultOptions.maxSize!!,
                directory = options.directory ?: defaultOptions.directory!!,
                appVersion = options.appVersion ?: 1,
                internalVersion = type.internalVersion,
            )
        }
    }

    class LazyFactory(
        val initializer: (PlatformContext) -> DiskCache
    ) : Factory {
        override fun create(context: PlatformContext, fileSystem: FileSystem): DiskCache {
            return initializer(context)
        }
    }

    class LazyOptionsFactory(
        val type: Type,
        val initializer: LazyOptions
    ) : Factory {
        override fun create(context: PlatformContext, fileSystem: FileSystem): DiskCache {
            val options = initializer.get(context)
            val defaultOptions = defaultDiskCacheOptions(context, type)
            return LruDiskCache(
                context = context,
                fileSystem = fileSystem,
                maxSize = options.maxSize ?: defaultOptions.maxSize!!,
                directory = options.directory ?: defaultOptions.directory!!,
                appVersion = options.appVersion ?: 1,
                internalVersion = type.internalVersion,
            )
        }
    }

    fun interface LazyOptions {
        fun get(context: PlatformContext): Options
    }

    class DefaultFactory(val type: Type) : Factory {
        override fun create(context: PlatformContext, fileSystem: FileSystem): DiskCache {
            val defaultOptions = defaultDiskCacheOptions(context, type)
            return LruDiskCache(
                context = context,
                fileSystem = fileSystem,
                maxSize = defaultOptions.maxSize!!,
                directory = defaultOptions.directory!!,
                appVersion = 1,
                internalVersion = type.internalVersion,
            )
        }
    }

    enum class Type(val dirName: String, val internalVersion: Int) {
        DOWNLOAD("download", 1),
        RESULT("result", 1),
    }
}

expect fun defaultDiskCacheOptions(
    context: PlatformContext,
    type: DiskCache.Type,
): Options