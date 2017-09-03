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
import me.xiaopan.sketch.Sketch;
import me.xiaopan.sketch.cache.DiskCache;
import me.xiaopan.sketch.http.DownloadException;
import me.xiaopan.sketch.uri.UriModel;

/**
 * 下载请求
 */
public class DownloadRequest extends AsyncRequest {
    protected DownloadResult downloadResult;

    private DownloadOptions options;
    private DownloadListener downloadListener;
    private DownloadProgressListener downloadProgressListener;

    public DownloadRequest(Sketch sketch, String uri, UriModel uriModel, String key, DownloadOptions options,
                           DownloadListener downloadListener, DownloadProgressListener downloadProgressListener) {
        super(sketch, uri, uriModel, key);

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
            if (SLog.isLoggable(SLog.LEVEL_DEBUG | SLog.TYPE_FLOW)) {
                SLog.d(getLogName(), "canceled. runDispatch. download request just start. %s. %s",
                        getThreadName(), getKey());
            }
            return;
        }

        // 从磁盘中找缓存文件
        if (!options.isCacheInDiskDisabled()) {
            setStatus(Status.CHECK_DISK_CACHE);

            DiskCache diskCache = getConfiguration().getDiskCache();
            DiskCache.Entry diskCacheEntry = diskCache.get(getDiskCacheKey());
            if (diskCacheEntry != null) {
                if (SLog.isLoggable(SLog.LEVEL_DEBUG | SLog.TYPE_FLOW)) {
                    SLog.d(getLogName(), "from diskCache. runDispatch. %s. %s",
                            getThreadName(), getKey());
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
        if (SLog.isLoggable(SLog.LEVEL_DEBUG | SLog.TYPE_FLOW)) {
            SLog.d(getLogName(), "download. runDispatch. %s. %s", getThreadName(), getKey());
        }
        submitRunDownload();
    }

    /**
     * 处理RequestLevel是LOCAL
     */
    void requestLevelIsLocal() {
        boolean isPauseDownload = options.getRequestLevelFrom() == RequestLevelFrom.PAUSE_DOWNLOAD;
        if (SLog.isLoggable(SLog.LEVEL_DEBUG)) {
            SLog.d(getLogName(), "canceled. runDispatch. %s. %s. %s",
                    isPauseDownload ? "pause download" : "requestLevel is local", getThreadName(), getKey());
        }
        canceled(isPauseDownload ? CancelCause.PAUSE_DOWNLOAD : CancelCause.REQUEST_LEVEL_IS_LOCAL);
    }

    @Override
    protected void runDownload() {
        if (isCanceled()) {
            if (SLog.isLoggable(SLog.LEVEL_DEBUG | SLog.TYPE_FLOW)) {
                SLog.d(getLogName(), "canceled. runDownload. start download. %s. %s",
                        getThreadName(), getKey());
            }
            return;
        }

        try {
            downloadResult = getConfiguration().getDownloader().download(this);
        } catch (CanceledException e) {
            return;
        } catch (DownloadException e) {
            e.printStackTrace();
            error(e.getErrorCause());
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
            SLog.e(getLogName(), "Not found data after download completed. %s. %s",
                    getThreadName(), getKey());
            error(ErrorCause.DATA_LOST_AFTER_DOWNLOAD_COMPLETED);
        }
    }

    @Override
    protected void runUpdateProgressInMainThread(int totalLength, int completedLength) {
        if (isFinished()) {
            if (SLog.isLoggable(SLog.LEVEL_DEBUG | SLog.TYPE_FLOW)) {
                SLog.d(getLogName(), "finished. runUpdateProgressInMainThread. %s. %s",
                        getThreadName(), getKey());
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
            if (SLog.isLoggable(SLog.LEVEL_DEBUG | SLog.TYPE_FLOW)) {
                SLog.d(getLogName(), "canceled. runCompletedInMainThread. %s. %s",
                        getThreadName(), getKey());
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
            if (SLog.isLoggable(SLog.LEVEL_DEBUG | SLog.TYPE_FLOW)) {
                SLog.d(getLogName(), "canceled. runErrorInMainThread. %s. %s",
                        getThreadName(), getKey());
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