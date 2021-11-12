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

import android.graphics.drawable.Drawable
import com.github.panpf.sketch.SLog
import com.github.panpf.sketch.SLog.Companion.dmf
import com.github.panpf.sketch.SLog.Companion.emf
import com.github.panpf.sketch.SLog.Companion.isLoggable
import com.github.panpf.sketch.SLog.Companion.wmf
import com.github.panpf.sketch.drawable.SketchBitmapDrawable
import java.util.*

/**
 * 下载或加载结果分享管理器，用于解决重复下载、重复加载
 *
 *
 * 对于相同的请求（不同类型条件不一样），只要第一个请求执行完毕了，后续请求可以直接使用第一个请求的结果，那么我们可以将所有后续的请求都绑定在第一个请求上，
 * 等第一个请求执行完毕后直接将结果交给后续请求处理即可
 */
class ResultShareManager {

    private val displayShareProviderMapLock = Any()
    private val downloadShareProviderMapLock = Any()
    private val displayShareProviderMap: MutableMap<String, DisplayRequest> = HashMap()
    private val downloadShareProviderMap: MutableMap<String, DownloadRequest> = HashMap()

    fun requestAttachDownloadShare(request: DownloadRequest): Boolean {
        if (!request.canUseDownloadShare()) {
            return false
        }
        synchronized(downloadShareProviderMapLock) {
            val providerRequest = downloadShareProviderMap[request.key] ?: return false
            val waitingShareRequests = providerRequest.waitingDownloadShareRequests
            if (waitingShareRequests != null) {
                waitingShareRequests.add(request)
            } else {
                val newWaitingShareRequests: MutableList<DownloadRequest> = LinkedList()
                newWaitingShareRequests.add(request)
                providerRequest.waitingDownloadShareRequests = newWaitingShareRequests
            }
            if (isLoggable(SLog.DEBUG)) {
                dmf(MODULE, "download. waiting result share. %s", request.key)
            }
            return true
        }
    }

    fun registerDownloadShareProvider(request: DownloadRequest) {
        if (!request.canUseDownloadShare()) {
            return
        }
        synchronized(downloadShareProviderMapLock) {
            val downloadShareKey = request.key
            check(!downloadShareProviderMap.containsKey(downloadShareKey)) { "Repeat registration download share provider" }
            downloadShareProviderMap[downloadShareKey] = request
            if (isLoggable(SLog.DEBUG)) {
                dmf(
                    MODULE, "download. register result share provider. %s",
                    request.key
                )
            }
        }
    }

    fun unregisterDownloadShareProvider(request: DownloadRequest) {
        if (!request.canUseDownloadShare()) {
            return
        }
        synchronized(downloadShareProviderMapLock) {
            val downloadShareKey = request.key
            val downloadRequest = downloadShareProviderMap[downloadShareKey]
            if (downloadRequest === request) {
                downloadShareProviderMap.remove(downloadShareKey)
            } else {
                return
            }
            if (isLoggable(SLog.DEBUG)) {
                dmf(MODULE, "download. unregister result share provider. %s", request.key)
            }
        }

        // 切记这部分代码不能在 synchronized (downloadShareProviderMapLock) 代码块中，因为可能会陷入锁循环
        val waitingShareRequests = request.waitingDownloadShareRequests
        if (waitingShareRequests != null) {
            for (waitingShareRequest in waitingShareRequests) {
                if (!waitingShareRequest.isCanceled) {
                    val diskCache = waitingShareRequest.configuration.diskCache
                    val diskCacheEntry = diskCache[waitingShareRequest.diskCacheKey]
                    if (diskCacheEntry != null) {
                        if (isLoggable(SLog.DEBUG)) {
                            dmf(
                                MODULE, "download. callback result share. %s. %s  <- %s",
                                "success", waitingShareRequest.key, request.key
                            )
                        }
                        waitingShareRequest.onRunDownloadFinished(
                            CacheDownloadResult(
                                diskCacheEntry,
                                ImageFrom.DISK_CACHE
                            )
                        )
                    } else {
                        if (isLoggable(SLog.DEBUG)) {
                            dmf(
                                MODULE, "download. callback result share. %s. %s  <- %s",
                                "failed", waitingShareRequest.key, request.key
                            )
                        }
                        waitingShareRequest.submitDownload()
                    }
                } else {
                    wmf(
                        MODULE, "download. callback result share. %s. %s  <-  %s",
                        "canceled", waitingShareRequest.key, request.key
                    )
                }
            }
            request.waitingDownloadShareRequests = null
        }
    }

    fun updateDownloadProgress(request: DownloadRequest, totalLength: Int, completedLength: Int) {
        if (!request.canUseDownloadShare()) {
            return
        }
        synchronized(downloadShareProviderMapLock) {
            val downloadRequests = request.waitingDownloadShareRequests
            if (downloadRequests != null) {
                for (waitingShareRequest in downloadRequests) {
                    waitingShareRequest.onUpdateProgress(totalLength, completedLength)
                }
            }
        }
    }

    fun requestAttachDisplayShare(request: DisplayRequest): Boolean {
        if (!request.canUseDisplayShare()) {
            return false
        }
        synchronized(displayShareProviderMapLock) {
            val providerRequest = displayShareProviderMap[request.key] ?: return false
            val waitingDisplayShareRequests = providerRequest.waitingDisplayShareRequests
            if (waitingDisplayShareRequests != null) {
                waitingDisplayShareRequests.add(request)
            } else {
                val newWaitingDisplayShareRequests: MutableList<DisplayRequest> = LinkedList()
                newWaitingDisplayShareRequests.add(request)
                providerRequest.waitingDisplayShareRequests = newWaitingDisplayShareRequests
            }
            if (isLoggable(SLog.DEBUG)) {
                dmf(MODULE, "display. waiting result share. %s", request.key)
            }
            return true
        }
    }

    fun registerDisplayResultShareProvider(request: DisplayRequest) {
        if (!request.canUseDisplayShare()) {
            return
        }
        synchronized(displayShareProviderMapLock) {
            val displayShareKey = request.key
            check(!displayShareProviderMap.containsKey(displayShareKey)) { "Repeat registration display share provider" }
            displayShareProviderMap[displayShareKey] = request
            if (isLoggable(SLog.DEBUG)) {
                dmf(MODULE, "display. register result share provider. %s", request.key)
            }
        }
    }

    fun unregisterDisplayResultShareProvider(request: DisplayRequest) {
        if (!request.canUseDisplayShare()) {
            return
        }
        synchronized(displayShareProviderMapLock) {
            val displayRequest = displayShareProviderMap[request.key]
            if (displayRequest === request) {
                displayShareProviderMap.remove(request.key)
            } else {
                return
            }
            if (isLoggable(SLog.DEBUG)) {
                dmf(
                    MODULE, "display. unregister result share provider. %s",
                    request.key
                )
            }
        }

        // 切记这部分代码不能在 synchronized (downloadShareProviderMapLock) 代码块中，因为可能会陷入锁循环
        val waitingDisplayShareRequests = request.waitingDisplayShareRequests
        if (waitingDisplayShareRequests != null) {
            for (waitingRequest in waitingDisplayShareRequests) {
                if (!waitingRequest.isCanceled) {
                    val memoryCache = waitingRequest.configuration.memoryCache
                    var cachedRefBitmap = memoryCache[waitingRequest.memoryCacheKey]
                    if (cachedRefBitmap != null && cachedRefBitmap.isRecycled) {
                        memoryCache.remove(waitingRequest.memoryCacheKey)
                        emf(
                            MODULE,
                            "memory cache drawable recycled. processResultShareRequests. bitmap=%s. %s. %s",
                            cachedRefBitmap.info,
                            waitingRequest.threadName,
                            waitingRequest.key
                        )
                        cachedRefBitmap = null
                    }
                    if (cachedRefBitmap != null) {
                        cachedRefBitmap.setIsWaitingUse(
                            String.format(
                                "%s:waitingUse:fromMemory",
                                waitingRequest.logName
                            ), true
                        ) // 立马标记等待使用，防止被回收
                        val drawable: Drawable =
                            SketchBitmapDrawable(cachedRefBitmap, ImageFrom.MEMORY_CACHE)
                        waitingRequest.onDisplayFinished(
                            DisplayResult(
                                drawable,
                                ImageFrom.MEMORY_CACHE,
                                cachedRefBitmap.attrs
                            )
                        )
                        if (isLoggable(SLog.DEBUG)) {
                            dmf(
                                MODULE, "display. callback result share. %s. %s  <-  %s",
                                "success", waitingRequest.key, request.key
                            )
                        }
                    } else {
                        if (isLoggable(SLog.DEBUG)) {
                            dmf(
                                MODULE, "display. callback result share. %s. %s  <-  %s",
                                "failed", waitingRequest.key, request.key
                            )
                        }
                        waitingRequest.submitLoad()
                    }
                } else {
                    wmf(
                        MODULE, "display. callback result share. %s. %s  <-  %s",
                        "canceled", waitingRequest.key, request.key
                    )
                }
            }
            request.waitingDisplayShareRequests = null
        }
    }

    override fun toString(): String {
        return MODULE
    }

    companion object {
        private const val MODULE = "ResultShareManager"
    }
}