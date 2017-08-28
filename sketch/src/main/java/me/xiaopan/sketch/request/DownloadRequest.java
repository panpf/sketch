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

import me.xiaopan.sketch.SLog;
import me.xiaopan.sketch.SLogType;
import me.xiaopan.sketch.Sketch;
import me.xiaopan.sketch.cache.DiskCache;

/**
 * 下载请求
 */
public class DownloadRequest extends AsyncRequest {
    protected DownloadResult downloadResult;

    private DownloadOptions options;
    private DownloadListener downloadListener;
    private DownloadProgressListener downloadProgressListener;

    public DownloadRequest(Sketch sketch, UriInfo uriInfo, String key, DownloadOptions options,
                           DownloadListener downloadListener, DownloadProgressListener downloadProgressListener) {
        super(sketch, uriInfo, key);

        this.options = options;
        this.downloadListener = downloadListener;
        this.downloadProgressListener = downloadProgressListener;

        setLogName("DownloadRequest");
    }

    /**
     * 获取下载选项
     */
    public DownloadOptions getOptions() {
        return options;
    }

    /**
     * 获取下载结果
     */
    @SuppressWarnings("unused")
    public DownloadResult getDownloadResult() {
        return downloadResult;
    }

    @Override
    public void error(ErrorCause errorCause) {
        super.error(errorCause);

        if (downloadListener != null) {
            postRunError();
        }
    }

    @Override
    public void canceled(CancelCause cancelCause) {
        super.canceled(cancelCause);

        if (downloadListener != null) {
            postRunCanceled();
        }
    }

    @Override
    protected void submitRunDispatch() {
        setStatus(Status.WAIT_DISPATCH);
        super.submitRunDispatch();
    }

    @Override
    protected void submitRunDownload() {
        setStatus(Status.WAIT_DOWNLOAD);
        super.submitRunDownload();
    }

    @Override
    protected void submitRunLoad() {
        setStatus(Status.WAIT_LOAD);
        super.submitRunLoad();
    }

    @Override
    protected void runDispatch() {
        if (isCanceled()) {
            if (SLog.isLoggable(SLog.DEBUG) && SLogType.REQUEST.isEnabled()) {
                SLog.d(getLogName(), "canceled. runDispatch. download request just start. %s. %s",
                        Thread.currentThread().getName(), getKey());
            }
            return;
        }

        // 从磁盘中找缓存文件
        if (!options.isCacheInDiskDisabled()) {
            setStatus(Status.CHECK_DISK_CACHE);

            DiskCache diskCache = getConfiguration().getDiskCache();
            DiskCache.Entry diskCacheEntry = diskCache.get(getUriInfo().getDiskCacheKey());
            if (diskCacheEntry != null) {
                if (SLog.isLoggable(SLog.DEBUG) && SLogType.REQUEST.isEnabled()) {
                    SLog.d(getLogName(), "from diskCache. runDispatch. %s. %s",
                            Thread.currentThread().getName(), getKey());
                }
                downloadResult = new DownloadResult(diskCacheEntry, ImageFrom.DISK_CACHE);
                downloadCompleted();
                return;
            }
        }

        // 在下载之前判断如果请求Level限制只能从本地加载的话就取消了
        if (options.getRequestLevel() == RequestLevel.LOCAL) {
            requestLevelIsLocal();
            return;
        }

        // 下载
        if (SLog.isLoggable(SLog.DEBUG) && SLogType.REQUEST.isEnabled()) {
            SLog.d(getLogName(), "download. runDispatch. %s. %s", Thread.currentThread().getName(), getKey());
        }
        submitRunDownload();
    }

    /**
     * 处理RequestLevel是LOCAL
     */
    void requestLevelIsLocal() {
        boolean isPauseDownload = options.getRequestLevelFrom() == RequestLevelFrom.PAUSE_DOWNLOAD;
        if (SLog.isLoggable(SLog.DEBUG)) {
            SLog.d(getLogName(), "canceled. runDispatch. %s. %s. %s",
                    isPauseDownload ? "pause download" : "requestLevel is local", Thread.currentThread().getName(), getKey());
        }
        canceled(isPauseDownload ? CancelCause.PAUSE_DOWNLOAD : CancelCause.REQUEST_LEVEL_IS_LOCAL);
    }

    @Override
    protected void runDownload() {
        downloadResult = getConfiguration().getDownloader().download(this);

        if (isCanceled()) {
            if (SLog.isLoggable(SLog.DEBUG) && SLogType.REQUEST.isEnabled()) {
                SLog.d(getLogName(), "canceled. runDownload. download after. %s. %s",
                        Thread.currentThread().getName(), getKey());
            }
            return;
        }

        downloadCompleted();
    }

    @Override
    protected void runLoad() {

    }

    /**
     * 更新进度
     */
    public void updateProgress(int totalLength, int completedLength) {
        if (downloadProgressListener != null && totalLength > 0) {
            postRunUpdateProgress(totalLength, completedLength);
        }
    }

    /**
     * 下载完成后续处理
     */
    protected void downloadCompleted() {
        if (downloadResult != null && downloadResult.hasData()) {
            postRunCompleted();
        } else {
            if (SLog.isLoggable(SLog.ERROR)) {
                SLog.e(getLogName(), "Not found data after download completed. %s. %s",
                        Thread.currentThread().getName(), getKey());
            }
            error(ErrorCause.DATA_LOST_AFTER_DOWNLOAD_COMPLETED);
        }
    }

    @Override
    protected void runUpdateProgressInMainThread(int totalLength, int completedLength) {
        if (isFinished()) {
            if (SLog.isLoggable(SLog.DEBUG) && SLogType.REQUEST.isEnabled()) {
                SLog.d(getLogName(), "finished. runUpdateProgressInMainThread. %s. %s",
                        Thread.currentThread().getName(), getKey());
            }
            return;
        }

        if (downloadProgressListener != null) {
            downloadProgressListener.onUpdateDownloadProgress(totalLength, completedLength);
        }
    }

    @Override
    protected void runCompletedInMainThread() {
        if (isCanceled()) {
            if (SLog.isLoggable(SLog.DEBUG) && SLogType.REQUEST.isEnabled()) {
                SLog.d(getLogName(), "canceled. runCompletedInMainThread. %s. %s",
                        Thread.currentThread().getName(), getKey());
            }
            return;
        }

        setStatus(Status.COMPLETED);

        if (downloadListener != null && downloadResult != null && downloadResult.hasData()) {
            downloadListener.onCompleted(downloadResult);
        }
    }

    @Override
    protected void runErrorInMainThread() {
        if (isCanceled()) {
            if (SLog.isLoggable(SLog.DEBUG) && SLogType.REQUEST.isEnabled()) {
                SLog.d(getLogName(), "canceled. runErrorInMainThread. %s. %s",
                        Thread.currentThread().getName(), getKey());
            }
            return;
        }

        if (downloadListener != null) {
            downloadListener.onError(getErrorCause());
        }
    }

    @Override
    protected void runCanceledInMainThread() {
        if (downloadListener != null) {
            downloadListener.onCanceled(getCancelCause());
        }
    }
}