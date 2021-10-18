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

package com.github.panpf.sketch.request;

import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.github.panpf.sketch.SLog;
import com.github.panpf.sketch.cache.DiskCache;
import com.github.panpf.sketch.cache.MemoryCache;
import com.github.panpf.sketch.drawable.SketchBitmapDrawable;
import com.github.panpf.sketch.drawable.SketchRefBitmap;

/**
 * 下载或加载结果分享管理器，用于解决重复下载、重复加载
 * <p>
 * 对于相同的请求（不同类型条件不一样），只要第一个请求执行完毕了，后续请求可以直接使用第一个请求的结果，那么我们可以将所有后续的请求都绑定在第一个请求上，
 * 等第一个请求执行完毕后直接将结果交给后续请求处理即可
 */
@SuppressWarnings("WeakerAccess")
public class ResultShareManager {
    private static final String MODULE = "ResultShareManager";

    @NonNull
    private final Object displayShareProviderMapLock = new Object();
    @NonNull
    private final Object downloadShareProviderMapLock = new Object();
    @NonNull
    private final Map<String, DisplayRequest> displayShareProviderMap = new HashMap<>();
    @NonNull
    private final Map<String, DownloadRequest> downloadShareProviderMap = new HashMap<>();

    public boolean requestAttachDownloadShare(@NonNull DownloadRequest request) {
        if (!request.canUseDownloadShare()) {
            return false;
        }

        synchronized (downloadShareProviderMapLock) {
            DownloadRequest providerRequest = downloadShareProviderMap.get(request.getKey());
            if (providerRequest == null) {
                return false;
            }

            List<DownloadRequest> waitingShareRequests = providerRequest.getWaitingDownloadShareRequests();
            if (waitingShareRequests != null) {
                waitingShareRequests.add(request);
            } else {
                List<DownloadRequest> newWaitingShareRequests = new LinkedList<>();
                newWaitingShareRequests.add(request);
                providerRequest.setWaitingDownloadShareRequests(newWaitingShareRequests);
            }

            if (SLog.isLoggable(SLog.DEBUG)) {
                SLog.dmf(MODULE, "download. waiting result share. %s", request.getKey());
            }
            return true;
        }
    }

    public void registerDownloadShareProvider(@NonNull DownloadRequest request) {
        if (!request.canUseDownloadShare()) {
            return;
        }

        synchronized (downloadShareProviderMapLock) {
            String downloadShareKey = request.getKey();
            if (downloadShareProviderMap.containsKey(downloadShareKey)) {
                throw new IllegalStateException("Repeat registration download share provider");
            }

            downloadShareProviderMap.put(downloadShareKey, request);
            if (SLog.isLoggable(SLog.DEBUG)) {
                SLog.dmf(MODULE, "download. register result share provider. %s",
                        request.getKey());
            }
        }
    }

    public void unregisterDownloadShareProvider(@NonNull DownloadRequest request) {
        if (!request.canUseDownloadShare()) {
            return;
        }

        synchronized (downloadShareProviderMapLock) {
            String downloadShareKey = request.getKey();
            DownloadRequest downloadRequest = downloadShareProviderMap.get(downloadShareKey);
            if (downloadRequest == request) {
                downloadShareProviderMap.remove(downloadShareKey);
            } else {
                return;
            }

            if (SLog.isLoggable(SLog.DEBUG)) {
                SLog.dmf(MODULE, "download. unregister result share provider. %s", request.getKey());
            }
        }

        // 切记这部分代码不能在 synchronized (downloadShareProviderMapLock) 代码块中，因为可能会陷入锁循环
        List<DownloadRequest> waitingShareRequests = request.getWaitingDownloadShareRequests();
        if (waitingShareRequests != null) {
            for (DownloadRequest waitingShareRequest : waitingShareRequests) {
                if (!waitingShareRequest.isCanceled()) {
                    DiskCache diskCache = waitingShareRequest.getConfiguration().getDiskCache();
                    DiskCache.Entry diskCacheEntry = diskCache.get(waitingShareRequest.getDiskCacheKey());
                    if (diskCacheEntry != null) {
                        if (SLog.isLoggable(SLog.DEBUG)) {
                            SLog.dmf(MODULE, "download. callback result share. %s. %s  <- %s",
                                    "success", waitingShareRequest.getKey(), request.getKey());
                        }
                        waitingShareRequest.onRunDownloadFinished(new CacheDownloadResult(diskCacheEntry, ImageFrom.DISK_CACHE));
                    } else {
                        if (SLog.isLoggable(SLog.DEBUG)) {
                            SLog.dmf(MODULE, "download. callback result share. %s. %s  <- %s",
                                    "failed", waitingShareRequest.getKey(), request.getKey());
                        }
                        waitingShareRequest.submitDownload();
                    }
                } else {
                    SLog.wmf(MODULE, "download. callback result share. %s. %s  <-  %s",
                            "canceled", waitingShareRequest.getKey(), request.getKey());
                }
            }
            request.setWaitingDownloadShareRequests(null);
        }
    }

    public void updateDownloadProgress(@NonNull DownloadRequest request, int totalLength, int completedLength) {
        if (!request.canUseDownloadShare()) {
            return;
        }

        synchronized (downloadShareProviderMapLock) {
            List<DownloadRequest> downloadRequests = request.getWaitingDownloadShareRequests();
            if (downloadRequests != null) {
                for (DownloadRequest waitingShareRequest : downloadRequests) {
                    waitingShareRequest.onUpdateProgress(totalLength, completedLength);
                }
            }
        }
    }


    public boolean requestAttachDisplayShare(@NonNull DisplayRequest request) {
        if (!request.canUseDisplayShare()) {
            return false;
        }

        synchronized (displayShareProviderMapLock) {
            DisplayRequest providerRequest = displayShareProviderMap.get(request.getKey());
            if (providerRequest == null) {
                return false;
            }

            List<DisplayRequest> waitingDisplayShareRequests = providerRequest.getWaitingDisplayShareRequests();
            if (waitingDisplayShareRequests != null) {
                waitingDisplayShareRequests.add(request);
            } else {
                List<DisplayRequest> newWaitingDisplayShareRequests = new LinkedList<>();
                newWaitingDisplayShareRequests.add(request);
                providerRequest.setWaitingDisplayShareRequests(newWaitingDisplayShareRequests);
            }

            if (SLog.isLoggable(SLog.DEBUG)) {
                SLog.dmf(MODULE, "display. waiting result share. %s", request.getKey());
            }
            return true;
        }
    }

    public void registerDisplayResultShareProvider(@NonNull DisplayRequest request) {
        if (!request.canUseDisplayShare()) {
            return;
        }

        synchronized (displayShareProviderMapLock) {
            String displayShareKey = request.getKey();
            if (displayShareProviderMap.containsKey(displayShareKey)) {
                throw new IllegalStateException("Repeat registration display share provider");
            }

            displayShareProviderMap.put(displayShareKey, request);
            if (SLog.isLoggable(SLog.DEBUG)) {
                SLog.dmf(MODULE, "display. register result share provider. %s", request.getKey());
            }
        }
    }

    public void unregisterDisplayResultShareProvider(@NonNull DisplayRequest request) {
        if (!request.canUseDisplayShare()) {
            return;
        }

        synchronized (displayShareProviderMapLock) {
            DisplayRequest displayRequest = displayShareProviderMap.get(request.getKey());
            if (displayRequest == request) {
                displayShareProviderMap.remove(request.getKey());
            } else {
                return;
            }
            if (SLog.isLoggable(SLog.DEBUG)) {
                SLog.dmf(MODULE, "display. unregister result share provider. %s",
                        request.getKey());
            }
        }

        // 切记这部分代码不能在 synchronized (downloadShareProviderMapLock) 代码块中，因为可能会陷入锁循环
        List<DisplayRequest> waitingDisplayShareRequests = request.getWaitingDisplayShareRequests();
        if (waitingDisplayShareRequests != null) {
            for (DisplayRequest waitingRequest : waitingDisplayShareRequests) {
                if (!waitingRequest.isCanceled()) {
                    MemoryCache memoryCache = waitingRequest.getConfiguration().getMemoryCache();
                    SketchRefBitmap cachedRefBitmap = memoryCache.get(waitingRequest.getMemoryCacheKey());
                    if (cachedRefBitmap != null && cachedRefBitmap.isRecycled()) {
                        memoryCache.remove(waitingRequest.getMemoryCacheKey());
                        SLog.emf(MODULE, "memory cache drawable recycled. processResultShareRequests. bitmap=%s. %s. %s",
                                cachedRefBitmap.getInfo(), waitingRequest.getThreadName(), waitingRequest.getKey());
                        cachedRefBitmap = null;
                    }
                    if (cachedRefBitmap != null) {
                        cachedRefBitmap.setIsWaitingUse(String.format("%s:waitingUse:fromMemory", waitingRequest.getLogName()), true);  // 立马标记等待使用，防止被回收

                        Drawable drawable = new SketchBitmapDrawable(cachedRefBitmap, ImageFrom.MEMORY_CACHE);
                        waitingRequest.onDisplayFinished(new DisplayResult(drawable, ImageFrom.MEMORY_CACHE, cachedRefBitmap.getAttrs()));

                        if (SLog.isLoggable(SLog.DEBUG)) {
                            SLog.dmf(MODULE, "display. callback result share. %s. %s  <-  %s",
                                    "success", waitingRequest.getKey(), request.getKey());
                        }
                    } else {
                        if (SLog.isLoggable(SLog.DEBUG)) {
                            SLog.dmf(MODULE, "display. callback result share. %s. %s  <-  %s",
                                    "failed", waitingRequest.getKey(), request.getKey());
                        }
                        waitingRequest.submitLoad();
                    }
                } else {
                    SLog.wmf(MODULE, "display. callback result share. %s. %s  <-  %s",
                            "canceled", waitingRequest.getKey(), request.getKey());
                }
            }
            request.setWaitingDisplayShareRequests(null);
        }
    }

    @NonNull
    @Override
    public String toString() {
        return MODULE;
    }
}
