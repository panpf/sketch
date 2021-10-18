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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

import com.github.panpf.sketch.SLog;
import com.github.panpf.sketch.Sketch;
import com.github.panpf.sketch.cache.DiskCache;
import com.github.panpf.sketch.http.DownloadException;
import com.github.panpf.sketch.uri.UriModel;

@SuppressWarnings("WeakerAccess")
public class DownloadRequest extends BaseRequest {

    @NonNull
    private DownloadOptions options;
    @Nullable
    private DownloadListener downloadListener;
    @Nullable
    private DownloadProgressListener downloadProgressListener;

    @Nullable
    private DownloadResult downloadResult;
    @Nullable
    private List<DownloadRequest> waitingDownloadShareRequests;

    DownloadRequest(@NonNull Sketch sketch, @NonNull String uri, @NonNull UriModel uriModel, @NonNull String key, @NonNull DownloadOptions options,
                    @Nullable DownloadListener downloadListener, @Nullable DownloadProgressListener downloadProgressListener, @NonNull String logModule) {
        super(sketch, uri, uriModel, key, logModule);

        this.options = options;
        this.downloadListener = downloadListener;
        this.downloadProgressListener = downloadProgressListener;
    }

    DownloadRequest(@NonNull Sketch sketch, @NonNull String uri, @NonNull UriModel uriModel, @NonNull String key, @NonNull DownloadOptions options,
                    @Nullable DownloadListener downloadListener, @Nullable DownloadProgressListener downloadProgressListener) {
        this(sketch, uri, uriModel, key, options, downloadListener, downloadProgressListener, "DownloadRequest");
    }

    @NonNull
    public DownloadOptions getOptions() {
        return options;
    }

    @Override
    protected void doError(@NonNull ErrorCause errorCause) {
        super.doError(errorCause);

        if (downloadListener != null) {
            postToMainRunError();
        }
    }

    @Override
    protected void doCancel(@NonNull CancelCause cancelCause) {
        super.doCancel(cancelCause);

        if (downloadListener != null) {
            postToMainRunCanceled();
        }
    }

    @Nullable
    @Override
    protected DispatchResult runDispatch() {
        if (isCanceled()) {
            if (SLog.isLoggable(SLog.DEBUG)) {
                SLog.dmf(getLogName(), "Request end before dispatch. %s. %s", getThreadName(), getKey());
            }
            return null;
        }

        // 从磁盘中找缓存文件
        if (!options.isCacheInDiskDisabled()) {
            setStatus(Status.CHECK_DISK_CACHE);

            DiskCache diskCache = getConfiguration().getDiskCache();
            DiskCache.Entry diskCacheEntry = diskCache.get(getDiskCacheKey());
            if (diskCacheEntry != null) {
                if (SLog.isLoggable(SLog.DEBUG)) {
                    SLog.dmf(getLogName(), "Dispatch. Disk cache. %s. %s", getThreadName(), getKey());
                }
                return new DownloadSuccessResult(new CacheDownloadResult(diskCacheEntry, ImageFrom.DISK_CACHE));
            }
        }

        // 在下载之前判断如果请求 Level 限制只能从本地加载的话就取消了
        if (options.getRequestLevel() == RequestLevel.LOCAL) {
            doCancel(CancelCause.PAUSE_DOWNLOAD);

            if (SLog.isLoggable(SLog.DEBUG)) {
                SLog.dmf(getLogName(), "Request end because %s. %s. %s", CancelCause.PAUSE_DOWNLOAD, getThreadName(), getKey());
            }
            return null;
        }

        if (SLog.isLoggable(SLog.DEBUG)) {
            SLog.dmf(getLogName(), "Dispatch. Download. %s. %s", getThreadName(), getKey());
        }
        return new RunDownoadResult();
    }

    @Nullable
    @Override
    protected DownloadResult runDownload() {
        if (isCanceled()) {
            if (SLog.isLoggable(SLog.DEBUG)) {
                SLog.dmf(getLogName(), "Request end before download. %s. %s", getThreadName(), getKey());
            }
            return null;
        }

        try {
            return getConfiguration().getDownloader().download(this);
        } catch (CanceledException e) {
            return null;
        } catch (DownloadException e) {
            e.printStackTrace();
            doError(e.getErrorCause());
            return null;
        }
    }

    @Nullable
    @Override
    LoadResult runLoad() {
        return null;
    }

    @Override
    void onRunLoadFinished(@Nullable LoadResult result) {

    }

    @Override
    public void onUpdateProgress(int totalLength, int completedLength) {
        if (downloadProgressListener != null && totalLength > 0) {
            postToMainRunUpdateProgress(totalLength, completedLength);
        }
    }

    @Override
    void onRunDownloadFinished(@Nullable DownloadResult result) {
        this.downloadResult = result;
        if (result != null) {
            postRunCompleted();
        }
    }

    @Override
    protected void runUpdateProgressInMain(int totalLength, int completedLength) {
        if (!isFinished() && downloadProgressListener != null) {
            downloadProgressListener.onUpdateDownloadProgress(totalLength, completedLength);
        }
    }

    @Override
    protected void runCompletedInMain() {
        if (isCanceled()) {
            if (SLog.isLoggable(SLog.DEBUG)) {
                SLog.dmf(getLogName(), "Request end before call completed. %s. %s", getThreadName(), getKey());
            }
            return;
        }

        setStatus(Status.COMPLETED);

        if (downloadListener != null && downloadResult != null) {
            downloadListener.onCompleted(downloadResult);
        }
    }

    @Override
    protected void runErrorInMain() {
        if (isCanceled()) {
            if (SLog.isLoggable(SLog.DEBUG)) {
                SLog.dmf(getLogName(), "Request end before call error. %s. %s", getThreadName(), getKey());
            }
            return;
        }

        if (downloadListener != null && getErrorCause() != null) {
            downloadListener.onError(getErrorCause());
        }
    }

    @Override
    protected void runCanceledInMain() {
        if (downloadListener != null && getCancelCause() != null) {
            downloadListener.onCanceled(getCancelCause());
        }
    }


    /* ************************************** Download Share ************************************ */

    public boolean canUseDownloadShare() {
        DiskCache diskCache = getConfiguration().getDiskCache();
        return !diskCache.isClosed() && !diskCache.isDisabled()
                && !getOptions().isCacheInDiskDisabled()
                && !isSync() && !getConfiguration().getExecutor().isShutdown();
    }

    @Nullable
    public List<DownloadRequest> getWaitingDownloadShareRequests() {
        return waitingDownloadShareRequests;
    }

    public void setWaitingDownloadShareRequests(@Nullable List<DownloadRequest> waitingDownloadShareRequests) {
        this.waitingDownloadShareRequests = waitingDownloadShareRequests;
    }
}