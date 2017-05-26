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
            if (SLogType.REQUEST.isEnabled()) {
                printLogW("canceled", "runDispatch", "download request just start");
            }
            return;
        }

        // 从磁盘中找缓存文件
        if (!options.isCacheInDiskDisabled()) {
            setStatus(Status.CHECK_DISK_CACHE);

            DiskCache diskCache = getConfiguration().getDiskCache();
            DiskCache.Entry diskCacheEntry = diskCache.get(getUriInfo().getDiskCacheKey());
            if (diskCacheEntry != null) {
                if (SLogType.REQUEST.isEnabled()) {
                    printLogD("from diskCache", "runDispatch");
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
        if (SLogType.REQUEST.isEnabled()) {
            printLogD("download", "runDispatch");
        }
        submitRunDownload();
    }

    /**
     * 处理RequestLevel是LOCAL
     */
    void requestLevelIsLocal() {
        boolean isPauseDownload = options.getRequestLevelFrom() == RequestLevelFrom.PAUSE_DOWNLOAD;
        if (SLogType.REQUEST.isEnabled()) {
            printLogW("canceled", "runDispatch", isPauseDownload ? "pause download" : "requestLevel is local");
        }
        canceled(isPauseDownload ? CancelCause.PAUSE_DOWNLOAD : CancelCause.REQUEST_LEVEL_IS_LOCAL);
    }

    @Override
    protected void runDownload() {
        downloadResult = getConfiguration().getImageDownloader().download(this);

        if (isCanceled()) {
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
            error(ErrorCause.DOWNLOAD_FAIL);
        }
    }

    @Override
    protected void runUpdateProgressInMainThread(int totalLength, int completedLength) {
        if (isFinished()) {
            if (SLogType.REQUEST.isEnabled()) {
                printLogW("finished", "runUpdateProgressInMainThread");
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
            if (SLogType.REQUEST.isEnabled()) {
                printLogW("canceled", "runCompletedInMainThread");
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
            if (SLogType.REQUEST.isEnabled()) {
                printLogW("canceled", "runErrorInMainThread");
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