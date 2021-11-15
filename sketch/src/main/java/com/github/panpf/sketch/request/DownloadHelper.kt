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
package com.github.panpf.sketch.request

import android.text.TextUtils
import com.github.panpf.sketch.SLog
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.uri.UriModel
import com.github.panpf.sketch.util.SketchUtils

class DownloadHelper(
    private val sketch: Sketch,
    private val uri: String,
    private val downloadListener: DownloadListener?
) {

    companion object {
        private const val NAME = "DownloadHelper"
    }

    private val downloadOptions: DownloadOptions = DisplayOptions()
    private var sync = false
    private var downloadProgressListener: DownloadProgressListener? = null

    // todo 补充测试 options
    /**
     * Limit request processing depth
     */
    fun requestLevel(requestLevel: RequestLevel?): DownloadHelper {
        if (requestLevel != null) {
            downloadOptions.requestLevel = requestLevel
        }
        return this
    }

    fun disableCacheInDisk(): DownloadHelper {
        downloadOptions.isCacheInDiskDisabled = true
        return this
    }

    /**
     * Batch setting download parameters, all reset
     */
    fun options(newOptions: DownloadOptions?): DownloadHelper {
        downloadOptions.copy(newOptions!!)
        return this
    }

    fun downloadProgressListener(downloadProgressListener: DownloadProgressListener?): DownloadHelper {
        this.downloadProgressListener = downloadProgressListener
        return this
    }

    /**
     * Synchronous execution
     */
    fun sync(): DownloadHelper {
        sync = true
        return this
    }

    fun commit(): DownloadRequest? {
        // Cannot run on UI threads
        check(!(sync && SketchUtils.isMainThread)) { "Cannot sync perform the download in the UI thread " }

        // Uri cannot is empty
        if (TextUtils.isEmpty(uri)) {
            SLog.em(NAME, "Uri is empty")
            CallbackHandler.postCallbackError(downloadListener, ErrorCause.URI_INVALID, sync)
            return null
        }

        // Uri type must be supported
        val uriModel = UriModel.match(sketch, uri)
        if (uriModel == null) {
            SLog.emf(NAME, "Unsupported uri type. %s", uri)
            CallbackHandler.postCallbackError(downloadListener, ErrorCause.URI_NO_SUPPORT, sync)
            return null
        }

        // Only support http ot https
        if (!uriModel.isFromNet) {
            SLog.emf(NAME, "Only support http ot https. %s", uri)
            CallbackHandler.postCallbackError(downloadListener, ErrorCause.URI_NO_SUPPORT, sync)
            return null
        }
        processOptions()
        val key = SketchUtils.makeRequestKey(uri, uriModel, downloadOptions.makeKey())
        return if (!checkDiskCache(key, uriModel)) {
            null
        } else submitRequest(key, uriModel)
    }

    private fun processOptions() {
        sketch.configuration.optionsFilterManager.filter(downloadOptions)
    }

    private fun checkDiskCache(key: String, uriModel: UriModel): Boolean {
        if (!downloadOptions.isCacheInDiskDisabled) {
            val diskCache = sketch.configuration.diskCache
            val diskCacheEntry = diskCache[uriModel.getDiskCacheKey(
                uri
            )]
            if (diskCacheEntry != null) {
                if (SLog.isLoggable(SLog.DEBUG)) {
                    SLog.dmf(NAME, "Download image completed. %s", key)
                }
                downloadListener?.onCompleted(
                    CacheDownloadResult(
                        diskCacheEntry,
                        ImageFrom.DISK_CACHE
                    )
                )
                return false
            }
        }
        return true
    }

    private fun submitRequest(key: String, uriModel: UriModel): DownloadRequest {
        CallbackHandler.postCallbackStarted(downloadListener, sync)
        val request = DownloadRequest(
            sketch, uri, uriModel, key,
            downloadOptions, downloadListener, downloadProgressListener
        )
        request.isSync = sync
        if (SLog.isLoggable(SLog.DEBUG)) {
            SLog.dmf(NAME, "Run dispatch submitted. %s", key)
        }
        request.submitDispatch()
        return request
    }
}