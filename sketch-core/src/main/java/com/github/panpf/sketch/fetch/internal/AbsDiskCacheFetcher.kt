/*
 * Copyright (C) 2019 panpf <panpfpanpf@outlook.com>
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
package com.github.panpf.sketch.fetch.internal

import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.datasource.ByteArrayDataSource
import com.github.panpf.sketch.datasource.DataSource
import com.github.panpf.sketch.datasource.DiskCacheDataSource
import com.github.panpf.sketch.fetch.FetchResult
import com.github.panpf.sketch.fetch.Fetcher
import com.github.panpf.sketch.request.DataFrom
import com.github.panpf.sketch.request.LoadRequest
import kotlinx.coroutines.sync.withLock
import java.io.BufferedOutputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.OutputStream

/**
 * Wrap the disk cache part for Fetcher that needs disk cache
 */
abstract class AbsDiskCacheFetcher(
    val sketch: Sketch,
    val request: LoadRequest,
    val dataFrom: DataFrom
) : Fetcher {

    override suspend fun fetch(): FetchResult {
        return FetchResult(getDataSource())
    }

    abstract fun getDiskCacheKey(): String

    @Throws(Exception::class)
    protected abstract fun outContent(outputStream: OutputStream)

    private suspend fun getDataSource(): DataSource {
        val diskCache = sketch.diskCache
        val encodedDiskCacheKey = diskCache.encodeKey(getDiskCacheKey())
        diskCache.getOrCreateEditMutexLock(encodedDiskCacheKey).withLock {
            val diskCacheEntry = diskCache[encodedDiskCacheKey]
            return if (diskCacheEntry != null) {
                DiskCacheDataSource(diskCacheEntry, DataFrom.DISK_CACHE)
            } else {
                readContent(encodedDiskCacheKey)
            }
        }
    }

    @Throws(IOException::class)
    private fun readContent(encodedDiskCacheKey: String): DataSource {
        val diskCache = sketch.diskCache
        val diskCacheEditor = diskCache.edit(encodedDiskCacheKey)
        val outputStream: OutputStream
        try {
            outputStream = if (diskCacheEditor != null) {
                BufferedOutputStream(diskCacheEditor.newOutputStream(), 8 * 1024)
            } else {
                ByteArrayOutputStream()
            }
            outputStream.use {
                outContent(it)
            }
        } catch (throwable: Throwable) {
            diskCacheEditor?.abort()
            throw IOException("Open output stream exception. ${request.uriString}", throwable)
        }
        if (diskCacheEditor != null) {
            try {
                diskCacheEditor.commit()
            } catch (e: Throwable) {
                diskCacheEditor.abort()
                throw IOException("Commit disk cache exception. ${request.uriString}", e)
            }
        }
        return if (diskCacheEditor != null) {
            val cacheEntry = diskCache[encodedDiskCacheKey]
            if (cacheEntry != null) {
                DiskCacheDataSource(cacheEntry, dataFrom)
            } else {
                throw IOException("Not found disk cache after save. ${request.uriString}")
            }
        } else {
            ByteArrayDataSource(
                (outputStream as ByteArrayOutputStream).toByteArray(),
                dataFrom
            )
        }
    }
}