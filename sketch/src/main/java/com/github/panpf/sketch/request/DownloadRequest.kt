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

import com.github.panpf.sketch.SLog
import com.github.panpf.sketch.SLog.Companion.dmf
import com.github.panpf.sketch.SLog.Companion.isLoggable
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.http.DownloadException
import com.github.panpf.sketch.uri.UriModel

open class DownloadRequest @JvmOverloads internal constructor(
    sketch: Sketch,
    uri: String,
    uriModel: UriModel,
    key: String,
    open val options: DownloadOptions,
    private val downloadListener: DownloadListener?,
    private val downloadProgressListener: DownloadProgressListener?,
    logModule: String = "DownloadRequest"
) : BaseRequest(sketch, uri, uriModel, key, logModule) {

    private var downloadResult: DownloadResult? = null
    var waitingDownloadShareRequests: MutableList<DownloadRequest>? = null

    override fun doError(errorCause: ErrorCause) {
        super.doError(errorCause)
        if (downloadListener != null) {
            postToMainRunError()
        }
    }

    override fun doCancel(cancelCause: CancelCause) {
        super.doCancel(cancelCause)
        if (downloadListener != null) {
            postToMainRunCanceled()
        }
    }

    override fun runDispatch(): DispatchResult? {
        if (isCanceled) {
            if (isLoggable(SLog.DEBUG)) {
                dmf(logName, "Request end before dispatch. %s. %s", threadName, key)
            }
            return null
        }

        // 从磁盘中找缓存文件
        if (!options.isCacheInDiskDisabled) {
            setStatus(Status.CHECK_DISK_CACHE)
            val diskCache = configuration.diskCache
            val diskCacheEntry = diskCache[diskCacheKey]
            if (diskCacheEntry != null) {
                if (isLoggable(SLog.DEBUG)) {
                    dmf(logName, "Dispatch. Disk cache. %s. %s", threadName, key)
                }
                return DownloadSuccessResult(
                    CacheDownloadResult(
                        diskCacheEntry,
                        ImageFrom.DISK_CACHE
                    )
                )
            }
        }

        // 在下载之前判断如果请求 Level 限制只能从本地加载的话就取消了
        if (options.requestLevel === RequestLevel.LOCAL) {
            doCancel(CancelCause.PAUSE_DOWNLOAD)
            if (isLoggable(SLog.DEBUG)) {
                dmf(
                    logName,
                    "Request end because %s. %s. %s",
                    CancelCause.PAUSE_DOWNLOAD,
                    threadName,
                    key
                )
            }
            return null
        }
        if (isLoggable(SLog.DEBUG)) {
            dmf(logName, "Dispatch. Download. %s. %s", threadName, key)
        }
        return RunDownloadResult()
    }

    override fun runDownload(): DownloadResult? {
        if (isCanceled) {
            if (isLoggable(SLog.DEBUG)) {
                dmf(logName, "Request end before download. %s. %s", threadName, key)
            }
            return null
        }
        return try {
            configuration.downloader.download(this)
        } catch (e: CanceledException) {
            null
        } catch (e: DownloadException) {
            e.printStackTrace()
            doError(e.errorCause)
            null
        }
    }

    override fun runLoad(): LoadResult? {
        return null
    }

    override fun onRunLoadFinished(result: LoadResult?) {}
    override fun onUpdateProgress(totalLength: Int, completedLength: Int) {
        if (downloadProgressListener != null && totalLength > 0) {
            postToMainRunUpdateProgress(totalLength, completedLength)
        }
    }

    override fun onRunDownloadFinished(result: DownloadResult?) {
        downloadResult = result
        if (result != null) {
            postRunCompleted()
        }
    }

    override fun runUpdateProgressInMain(totalLength: Int, completedLength: Int) {
        if (!isFinished && downloadProgressListener != null) {
            downloadProgressListener.onUpdateDownloadProgress(totalLength, completedLength)
        }
    }

    override fun runCompletedInMain() {
        if (isCanceled) {
            if (isLoggable(SLog.DEBUG)) {
                dmf(logName, "Request end before call completed. %s. %s", threadName, key)
            }
            return
        }
        setStatus(Status.COMPLETED)
        val downloadResult = downloadResult
        if (downloadListener != null && downloadResult != null) {
            downloadListener.onCompleted(downloadResult)
        }
    }

    override fun runErrorInMain() {
        if (isCanceled) {
            if (isLoggable(SLog.DEBUG)) {
                dmf(logName, "Request end before call error. %s. %s", threadName, key)
            }
            return
        }
        val errorCause = errorCause
        if (downloadListener != null && errorCause != null) {
            downloadListener.onError(errorCause)
        }
    }

    override fun runCanceledInMain() {
        val cancelCause = cancelCause
        if (downloadListener != null && cancelCause != null) {
            downloadListener.onCanceled(cancelCause)
        }
    }

    /* ************************************** Download Share ************************************ */
    fun canUseDownloadShare(): Boolean {
        val diskCache = configuration.diskCache
        return (!diskCache.isClosed && !diskCache.isDisabled
                && !options.isCacheInDiskDisabled
                && !isSync && !configuration.executor.isShutdown)
    }
}