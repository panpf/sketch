/*
 * Copyright (C) 2019 Peng fei Pan <panpfpanpf@outlook.me>
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

package me.panpf.sketch.request;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.HashSet;
import java.util.Set;

import me.panpf.sketch.SLog;
import me.panpf.sketch.Sketch;
import me.panpf.sketch.cache.DiskCache;
import me.panpf.sketch.uri.UriModel;
import me.panpf.sketch.util.SketchUtils;

/**
 * 支持顺风车功能的下载请求
 */
@SuppressWarnings("WeakerAccess")
public class ResultShareDownloadRequest extends DownloadRequest implements ResultShareDownload {
    @Nullable
    private Set<ResultShareDownload> downloadResultShareSet;

    public ResultShareDownloadRequest(@NonNull Sketch sketch, @NonNull String uri, @NonNull UriModel uriModel, @NonNull String key, @NonNull DownloadOptions options,
                                      @Nullable DownloadListener downloadListener, @Nullable DownloadProgressListener downloadProgressListener) {
        super(sketch, uri, uriModel, key, options, downloadListener, downloadProgressListener);
    }

    @NonNull
    @Override
    public String getDownloadResultShareKey() {
        return getUri();
    }

    @NonNull
    @Override
    public String getDownloadResultShareLog() {
        return String.format("%s@%s", SketchUtils.toHexString(this), getKey());
    }

    @Override
    public boolean canUseResultShare() {
        DiskCache diskCache = getConfiguration().getDiskCache();
        return !diskCache.isClosed() && !diskCache.isDisabled()
                && !getOptions().isCacheInDiskDisabled()
                && !isSync() && !getConfiguration().getExecutor().isShutdown();
    }

    @Override
    protected void submitRunDownload() {
        // 可以坐顺风车的话，就先尝试坐别人的，坐不上就自己成为顺风车主让别人坐
        if (canUseResultShare()) {
            ResultShareManager resultShareManager = getConfiguration().getExecutor().getResultShareManager();
            if (resultShareManager.byDownloadResultShare(this)) {
                return;
            } else {
                resultShareManager.registerDownloadResultShareProvider(this);
            }
        }

        super.submitRunDownload();
    }

    @Override
    protected void runDownload() {
        super.runDownload();

        // 由于在submitRunDownload中会将自己注册成为顺风车主，因此一定要保证在这里取消注册
        if (canUseResultShare()) {
            ResultShareManager resultShareManager = getConfiguration().getExecutor().getResultShareManager();
            resultShareManager.unregisterDownloadResultShareProvider(this);
        }
    }

    @Override
    public synchronized void byDownloadResultShare(ResultShareDownload request) {
        if (downloadResultShareSet == null) {
            synchronized (this) {
                if (downloadResultShareSet == null) {
                    downloadResultShareSet = new HashSet<>();
                }
            }
        }

        downloadResultShareSet.add(request);
    }

    @Nullable
    @Override
    public Set<ResultShareDownload> getDownloadResultShareSet() {
        return downloadResultShareSet;
    }

    @Override
    public synchronized boolean processDownloadResultShare() {
        DiskCache diskCache = getConfiguration().getDiskCache();
        DiskCache.Entry diskCacheEntry = diskCache.get(getDiskCacheKey());

        if (diskCacheEntry != null) {
            if (SLog.isLoggable(SLog.DEBUG)) {
                SLog.dmf(getLogName(), "from diskCache. processDownloadResultShare. %s. %s", getThreadName(), getKey());
            }
            downloadResult = new DownloadResult(diskCacheEntry, ImageFrom.DISK_CACHE);
            downloadCompleted();
            return true;
        } else {
            submitRunDownload();
            return false;
        }
    }

    @Override
    public void updateProgress(int totalLength, int completedLength) {
        super.updateProgress(totalLength, completedLength);

        if (downloadResultShareSet != null && !downloadResultShareSet.isEmpty()) {
            for (ResultShareDownload resultShareDownload : downloadResultShareSet) {
                if (resultShareDownload instanceof DownloadRequest) {
                    ((DownloadRequest) resultShareDownload).updateProgress(totalLength, completedLength);
                }
            }
        }
    }
}