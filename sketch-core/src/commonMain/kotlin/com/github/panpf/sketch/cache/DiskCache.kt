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

    /**
     * Clear all cached
     */
    fun clear()

    /**
     * Executes the given [action] under this mutex's lock.
     */
    suspend fun <R> withLock(key: String, action: suspend DiskCache.() -> R): R

    /**
     * Snapshot the values for an entry.
     */
    interface Snapshot : Closeable {

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

    enum class Type(val dirName: String, val internalVersion: Int) {
        DOWNLOAD("download", 1),
        RESULT("result", 1),
    }

    data class Options(
        val appCacheDirectory: Path? = null,
        val downloadMaxSize: Long? = null,
        val resultMaxSize: Long? = null,
        val downloadAppVersion: Int? = null,
        val resultAppVersion: Int? = null,
    ) {
        init {
            require(
                downloadMaxSize == null || downloadMaxSize > 0
                        || resultMaxSize == null || resultMaxSize > 0
            ) {
                "downloadMaxSize or resultMaxSize must be greater than 0"
            }
            require(
                downloadAppVersion == null || downloadAppVersion in 1.rangeTo(Short.MAX_VALUE)
                        || resultAppVersion == null || resultAppVersion in 1.rangeTo(Short.MAX_VALUE)
            ) {
                "downloadAppVersion or resultAppVersion must be in 1 to ${Short.MAX_VALUE} range"
            }
        }
    }

    fun interface Factory {
        fun create(context: PlatformContext, fileSystem: FileSystem, type: Type): DiskCache
    }

    class OptionsFactory(
        private val lazyOptions: ((PlatformContext) -> Options?)? = null
    ) : Factory {
        override fun create(
            context: PlatformContext,
            fileSystem: FileSystem,
            type: Type
        ): DiskCache {
            val userOptions = lazyOptions?.invoke(context)
            val platformDefaultOptions = platformDefaultDiskCacheOptions(context)
            requireNotNull(platformDefaultOptions.downloadMaxSize) {
                "The platform must provide a default downloadMaxSize"
            }
            requireNotNull(platformDefaultOptions.resultMaxSize) {
                "The platform must provide a default resultMaxSize"
            }
            val appCacheDirectory = requireNotNull(
                userOptions?.appCacheDirectory ?: platformDefaultOptions.appCacheDirectory
            ) {
                "You must actively configure appCacheDirectory for DiskCache on this platform. Please refer to the documentation https://github.com/panpf/sketch/blob/main/docs/wiki/getting_started.md"
            }
            val directory = appCacheDirectory.resolve(DEFAULT_DIR_NAME).resolve(type.dirName)
            val maxSize = if (type == Type.DOWNLOAD) {
                userOptions?.downloadMaxSize ?: platformDefaultOptions.downloadMaxSize
            } else {
                userOptions?.resultMaxSize ?: platformDefaultOptions.resultMaxSize
            }
            val appVersion = if (type == Type.DOWNLOAD) {
                userOptions?.downloadAppVersion ?: platformDefaultOptions.downloadAppVersion ?: 1
            } else {
                userOptions?.resultAppVersion ?: platformDefaultOptions.resultAppVersion ?: 1
            }
            return LruDiskCache(
                context = context,
                fileSystem = fileSystem,
                maxSize = maxSize,
                directory = directory,
                appVersion = appVersion,
                internalVersion = type.internalVersion,
            )
        }
    }
}

expect fun platformDefaultDiskCacheOptions(context: PlatformContext): Options