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
import android.text.TextUtils
import com.github.panpf.sketch.SLog
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.datasource.ByteArrayDataSource
import com.github.panpf.sketch.datasource.DataSource
import com.github.panpf.sketch.datasource.DiskCacheDataSource
import com.github.panpf.sketch.request.BytesDownloadResult
import com.github.panpf.sketch.request.CacheDownloadResult
import com.github.panpf.sketch.request.DownloadResult
import com.github.panpf.sketch.request.ImageFrom

open class HttpUriModel : UriModel() {

    companion object {
        const val SCHEME = "http://"
        private const val NAME = "HttpUriModel"
    }

    override fun match(uri: String): Boolean {
        return !TextUtils.isEmpty(uri) && uri.startsWith(SCHEME)
    }

    override val isFromNet: Boolean
        get() = true

    @Throws(GetDataSourceException::class)
    override fun getDataSource(
        context: Context,
        uri: String,
        downloadResult: DownloadResult?
    ): DataSource {
        return if (downloadResult is BytesDownloadResult) {
            ByteArrayDataSource(
                downloadResult.imageData,
                downloadResult.imageFrom
            )
        } else if (downloadResult is CacheDownloadResult) {
            DiskCacheDataSource(
                downloadResult.diskCacheEntry,
                downloadResult.imageFrom
            )
        } else if (downloadResult == null) {
            val diskCache = Sketch.with(context).configuration.diskCache
            val diskCacheEntry = diskCache[getDiskCacheKey(uri)]
            if (diskCacheEntry != null) {
                DiskCacheDataSource(diskCacheEntry, ImageFrom.DISK_CACHE)
            } else {
                val cause = String.format("Not found disk cache. %s", uri)
                SLog.em(NAME, cause)
                throw GetDataSourceException(cause)
            }
        } else {
            val cause = String.format("Not found data from download result. %s", uri)
            SLog.em(NAME, cause)
            throw GetDataSourceException(cause)
        }
    }
}