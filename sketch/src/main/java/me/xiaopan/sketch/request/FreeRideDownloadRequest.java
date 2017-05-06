/*
 * Copyright (C) 2013 Peng fei Pan <sky@xiaopan.me>
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

package me.xiaopan.sketch.request;

import java.util.HashSet;
import java.util.Set;

import me.xiaopan.sketch.SLogType;
import me.xiaopan.sketch.Sketch;
import me.xiaopan.sketch.cache.DiskCache;
import me.xiaopan.sketch.util.SketchUtils;

/**
 * 支持顺风车功能的下载请求
 */
public class FreeRideDownloadRequest extends DownloadRequest implements FreeRideManager.DownloadFreeRide {
    private Set<FreeRideManager.DownloadFreeRide> downloadFreeRideSet;

    public FreeRideDownloadRequest(Sketch sketch, DownloadInfo info, DownloadOptions options,
                                   DownloadListener downloadListener, DownloadProgressListener downloadProgressListener) {
        super(sketch, info, options, downloadListener, downloadProgressListener);
    }

    @Override
    public String getDownloadFreeRideKey() {
        return getRealUri();
    }

    @Override
    public String getDownloadFreeRideLog() {
        return String.format("%s@%s", SketchUtils.toHexString(this), getKey());
    }

    @Override
    public boolean canByDownloadFreeRide() {
        DiskCache diskCache = getConfiguration().getDiskCache();
        return !diskCache.isClosed() && !diskCache.isDisabled()
                && !getOptions().isCacheInDiskDisabled()
                && !isSync() && !getConfiguration().getRequestExecutor().isShutdown();
    }

    @Override
    protected void submitRunDownload() {
        // 可以坐顺风车的话，就先尝试坐别人的，坐不上就自己成为顺风车主让别人坐
        if (canByDownloadFreeRide()) {
            FreeRideManager freeRideManager = getConfiguration().getFreeRideManager();
            if (freeRideManager.byDownloadFreeRide(this)) {
                return;
            } else {
                freeRideManager.registerDownloadFreeRideProvider(this);
            }
        }

        super.submitRunDownload();
    }

    @Override
    protected void runDownload() {
        super.runDownload();

        // 由于在submitRunDownload中会将自己注册成为顺风车主，因此一定要保证在这里取消注册
        if (canByDownloadFreeRide()) {
            FreeRideManager freeRideManager = getConfiguration().getFreeRideManager();
            freeRideManager.unregisterDownloadFreeRideProvider(this);
        }
    }

    @Override
    public synchronized void byDownloadFreeRide(FreeRideManager.DownloadFreeRide request) {
        if (downloadFreeRideSet == null) {
            synchronized (this) {
                if (downloadFreeRideSet == null) {
                    downloadFreeRideSet = new HashSet<FreeRideManager.DownloadFreeRide>();
                }
            }
        }

        downloadFreeRideSet.add(request);
    }

    @Override
    public Set<FreeRideManager.DownloadFreeRide> getDownloadFreeRideSet() {
        return downloadFreeRideSet;
    }

    @Override
    public synchronized boolean processDownloadFreeRide() {
        DiskCache diskCache = getConfiguration().getDiskCache();
        DiskCache.Entry diskCacheEntry = diskCache.get(getDiskCacheKey());

        if (diskCacheEntry != null) {
            if (SLogType.REQUEST.isEnabled()) {
                printLogD("from diskCache", "processDownloadFreeRide");
            }
            downloadResult = new DownloadResult(diskCacheEntry, ImageFrom.DISK_CACHE);
            downloadCompleted();
            return true;
        } else {
            submitRunDownload();
            return false;
        }
    }
}