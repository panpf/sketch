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
package com.github.panpf.sketch.decode

import android.graphics.Bitmap
import com.github.panpf.sketch.cache.DiskCache
import com.github.panpf.sketch.datasource.DiskCacheDataSource
import com.github.panpf.sketch.request.ImageFrom
import com.github.panpf.sketch.request.LoadOptions
import com.github.panpf.sketch.request.LoadRequest
import com.github.panpf.sketch.util.DiskLruCache
import com.github.panpf.sketch.util.DiskLruCache.EditorChangedException
import com.github.panpf.sketch.util.DiskLruCache.FileNotExistException
import com.github.panpf.sketch.util.SketchUtils
import java.io.BufferedOutputStream
import java.io.IOException

/**
 * Cache transform results, no need to transform again next time, can speed up the loading speed
 */
class TransformCacheManager {
    fun canUse(loadOptions: LoadOptions): Boolean {
        if (!loadOptions.isCacheProcessedImageInDisk) {
            return false
        }
        if (loadOptions.maxSize != null || loadOptions.resize != null) {
            return true
        }
        if (loadOptions.processor != null) {
            return true
        }
        if (loadOptions.isThumbnailMode && loadOptions.resize != null) {
            return true
        }
        return !loadOptions.isCorrectImageOrientationDisabled
    }

    fun canUseByInSampleSize(inSampleSize: Int): Boolean {
        return inSampleSize >= 8
    }

    fun checkDiskCache(request: LoadRequest): Boolean {
        val diskCache = request.configuration.diskCache
        val processedImageDiskCacheKey = request.transformCacheKey
        val diskCacheKey = request.diskCacheKey
        if (diskCacheKey == processedImageDiskCacheKey) {
            return false
        }
        val editLock = diskCache.getEditLock(processedImageDiskCacheKey)
        editLock.lock()
        return try {
            diskCache.exist(processedImageDiskCacheKey)
        } finally {
            editLock.unlock()
        }
    }

    fun getDiskCache(request: LoadRequest): DiskCacheDataSource? {
        val diskCache = request.configuration.diskCache
        val processedImageDiskCacheKey = request.transformCacheKey
        val diskCacheKey = request.diskCacheKey
        if (diskCacheKey == processedImageDiskCacheKey) {
            return null
        }
        val editLock = diskCache.getEditLock(processedImageDiskCacheKey)
        editLock.lock()
        val diskCacheEntry: DiskCache.Entry? = try {
            diskCache[processedImageDiskCacheKey]
        } finally {
            editLock.unlock()
        }
        return if (diskCacheEntry == null) {
            null
        } else DiskCacheDataSource(
            diskCacheEntry,
            ImageFrom.DISK_CACHE
        ).setFromProcessedCache(true)
    }

    fun saveToDiskCache(request: LoadRequest, bitmap: Bitmap) {
        val diskCache = request.configuration.diskCache
        val processedImageDiskCacheKey = request.transformCacheKey
        val diskCacheKey = request.diskCacheKey
        if (diskCacheKey == processedImageDiskCacheKey) {
            return
        }
        val editLock = diskCache.getEditLock(processedImageDiskCacheKey)
        editLock.lock()
        try {
            val diskCacheEntry = diskCache[processedImageDiskCacheKey]
            diskCacheEntry?.delete()
            val diskCacheEditor = diskCache.edit(processedImageDiskCacheKey)
            if (diskCacheEditor != null) {
                var outputStream: BufferedOutputStream? = null
                try {
                    outputStream = BufferedOutputStream(diskCacheEditor.newOutputStream(), 8 * 1024)
                    bitmap.compress(SketchUtils.bitmapConfigToCompressFormat(bitmap.config), 100, outputStream)
                    diskCacheEditor.commit()
                } catch (e: EditorChangedException) {
                    e.printStackTrace()
                    diskCacheEditor.abort()
                } catch (e: IOException) {
                    e.printStackTrace()
                    diskCacheEditor.abort()
                } catch (e: DiskLruCache.ClosedException) {
                    e.printStackTrace()
                    diskCacheEditor.abort()
                } catch (e: FileNotExistException) {
                    e.printStackTrace()
                    diskCacheEditor.abort()
                } finally {
                    SketchUtils.close(outputStream)
                }
            }
        } finally {
            editLock.unlock()
        }
    }

    override fun toString(): String {
        return "TransformCache"
    }
}