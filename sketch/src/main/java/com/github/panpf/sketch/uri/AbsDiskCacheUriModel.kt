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
package com.github.panpf.sketch.uri

import android.content.Context
import com.github.panpf.sketch.SLog
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.datasource.ByteArrayDataSource
import com.github.panpf.sketch.datasource.DataSource
import com.github.panpf.sketch.datasource.DiskCacheDataSource
import com.github.panpf.sketch.request.DownloadResult
import com.github.panpf.sketch.request.ImageFrom
import com.github.panpf.sketch.util.DiskLruCache
import com.github.panpf.sketch.util.DiskLruCache.EditorChangedException
import com.github.panpf.sketch.util.DiskLruCache.FileNotExistException
import com.github.panpf.sketch.util.SketchUtils
import java.io.BufferedOutputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.OutputStream

/**
 * 为需要磁盘缓存的 UriModel 封装好 getDataSource 部分
 */
abstract class AbsDiskCacheUriModel<CONTENT> : UriModel() {

    @Throws(GetDataSourceException::class)
    override fun getDataSource(
        context: Context,
        uri: String,
        downloadResult: DownloadResult?
    ): DataSource {
        val diskCache = Sketch.with(context).configuration.diskCache
        val diskCacheKey = getDiskCacheKey(uri)
        var cacheEntry = diskCache[diskCacheKey]
        if (cacheEntry != null) {
            return DiskCacheDataSource(cacheEntry, ImageFrom.DISK_CACHE)
        }
        val diskCacheEditLock = diskCache.getEditLock(diskCacheKey)
        diskCacheEditLock.lock()
        return try {
            cacheEntry = diskCache[diskCacheKey]
            if (cacheEntry != null) {
                DiskCacheDataSource(cacheEntry, ImageFrom.DISK_CACHE)
            } else {
                readContent(context, uri, diskCacheKey)
            }
        } finally {
            diskCacheEditLock.unlock()
        }
    }

    @Throws(GetDataSourceException::class)
    private fun readContent(context: Context, uri: String, diskCacheKey: String): DataSource {
        val content = getContent(context, uri)
        val diskCache = Sketch.with(context).configuration.diskCache
        val diskCacheEditor = diskCache.edit(diskCacheKey)
        val outputStream: OutputStream = if (diskCacheEditor != null) {
            try {
                BufferedOutputStream(diskCacheEditor.newOutputStream(), 8 * 1024)
            } catch (e: IOException) {
                diskCacheEditor.abort()
                closeContent(content, context)
                val cause = String.format("Open output stream exception. %s", uri)
                SLog.emt(NAME, e, cause)
                throw GetDataSourceException(cause, e)
            }
        } else {
            ByteArrayOutputStream()
        }
        try {
            outContent(content, outputStream)
        } catch (tr: Throwable) {
            diskCacheEditor?.abort()
            val cause = String.format("Output data exception. %s", uri)
            SLog.emt(NAME, tr, cause)
            throw GetDataSourceException(cause, tr)
        } finally {
            SketchUtils.close(outputStream)
            closeContent(content, context)
        }
        if (diskCacheEditor != null) {
            try {
                diskCacheEditor.commit()
            } catch (e: IOException) {
                diskCacheEditor.abort()
                val cause = String.format("Commit disk cache exception. %s", uri)
                SLog.emt(NAME, e, cause)
                throw GetDataSourceException(cause, e)
            } catch (e: EditorChangedException) {
                diskCacheEditor.abort()
                val cause = String.format("Commit disk cache exception. %s", uri)
                SLog.emt(NAME, e, cause)
                throw GetDataSourceException(cause, e)
            } catch (e: DiskLruCache.ClosedException) {
                diskCacheEditor.abort()
                val cause = String.format("Commit disk cache exception. %s", uri)
                SLog.emt(NAME, e, cause)
                throw GetDataSourceException(cause, e)
            } catch (e: FileNotExistException) {
                diskCacheEditor.abort()
                val cause = String.format("Commit disk cache exception. %s", uri)
                SLog.emt(NAME, e, cause)
                throw GetDataSourceException(cause, e)
            }
        }
        return if (diskCacheEditor == null) {
            ByteArrayDataSource(
                (outputStream as ByteArrayOutputStream).toByteArray(),
                ImageFrom.LOCAL
            )
        } else {
            val cacheEntry = diskCache[diskCacheKey]
            if (cacheEntry != null) {
                DiskCacheDataSource(cacheEntry, ImageFrom.LOCAL)
            } else {
                val cause = String.format("Not found disk cache after save. %s", uri)
                SLog.em(NAME, cause)
                throw GetDataSourceException(cause)
            }
        }
    }

    @Throws(GetDataSourceException::class)
    protected abstract fun getContent(context: Context, uri: String): CONTENT

    @Throws(Exception::class)
    protected abstract fun outContent(content: CONTENT, outputStream: OutputStream)
    protected abstract fun closeContent(content: CONTENT, context: Context)

    companion object {
        private const val NAME = "AbsDiskCacheUriModel"
    }
}