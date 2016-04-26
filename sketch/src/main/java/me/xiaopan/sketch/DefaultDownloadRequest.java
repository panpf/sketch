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

package me.xiaopan.sketch;

import android.util.Log;

import me.xiaopan.sketch.cache.DiskCache;
import me.xiaopan.sketch.util.SketchUtils;

/**
 * 下载请求
 */
public class DefaultDownloadRequest extends SketchRequest implements DownloadRequest {
    private static final String NAME = "DefaultDownloadRequest";

    private RequestAttrs attrs;
    private DownloadOptions options;
    private DownloadListener downloadListener;
    private DownloadProgressListener downloadProgressListener;

    private DownloadResult downloadResult;
    private FailCause failCause;
    private CancelCause cancelCause;
    private RequestStatus requestStatus = RequestStatus.WAIT_DISPATCH;

    public DefaultDownloadRequest(RequestAttrs attrs, DownloadOptions options, DownloadListener downloadListener) {
        super(attrs.getConfiguration().getRequestExecutor());
        this.attrs = attrs;
        this.options = options;
        this.downloadListener = downloadListener;
    }

    @Override
    public RequestAttrs getAttrs() {
        return attrs;
    }

    @Override
    public DownloadOptions getOptions() {
        return options;
    }

    @Override
    public DownloadResult getDownloadResult() {
        return downloadResult;
    }

    public void setDownloadProgressListener(DownloadProgressListener downloadProgressListener) {
        this.downloadProgressListener = downloadProgressListener;
    }

    @Override
    public RequestStatus getRequestStatus() {
        return requestStatus;
    }

    @Override
    public void setRequestStatus(RequestStatus requestStatus) {
        this.requestStatus = requestStatus;
    }

    @Override
    public FailCause getFailCause() {
        return failCause;
    }

    @Override
    public CancelCause getCancelCause() {
        return cancelCause;
    }

    @Override
    public boolean isFinished() {
        return requestStatus == RequestStatus.COMPLETED || requestStatus == RequestStatus.CANCELED || requestStatus == RequestStatus.FAILED;
    }

    @Override
    public boolean isCanceled() {
        return requestStatus == RequestStatus.CANCELED;
    }

    @Override
    public boolean cancel() {
        if (isFinished()) {
            return false;
        }
        toCanceledStatus(CancelCause.NORMAL);
        return true;
    }

    @Override
    public void toFailedStatus(FailCause failCause) {
        this.failCause = failCause;
        postRunFailed();
    }

    @Override
    public void toCanceledStatus(CancelCause cancelCause) {
        this.cancelCause = cancelCause;
        setRequestStatus(RequestStatus.CANCELED);
        postRunCanceled();
    }

    @Override
    protected void submitRunDispatch() {
        setRequestStatus(RequestStatus.WAIT_DISPATCH);
        super.submitRunDispatch();
    }

    @Override
    protected void submitRunDownload() {
        setRequestStatus(RequestStatus.WAIT_DOWNLOAD);
        super.submitRunDownload();
    }

    @Override
    protected void submitRunLoad() {
        setRequestStatus(RequestStatus.WAIT_LOAD);
        super.submitRunLoad();
    }

    @Override
    protected void runDispatch() {
        setRequestStatus(RequestStatus.DISPATCHING);

        // 先过滤掉不支持的URI协议
        if (attrs.getUriScheme() != UriScheme.HTTP && attrs.getUriScheme() != UriScheme.HTTPS) {
            if (Sketch.isDebugMode()) {
                Log.e(Sketch.TAG, SketchUtils.concat(NAME, " - ", "runDispatch", " - ", "not support uri:", attrs.getUri(), " - ", attrs.getName()));
            }
            toFailedStatus(FailCause.URI_NO_SUPPORT);
            return;
        }

        // 然后从磁盘缓存中找缓存文件
        if (options.isCacheInDisk()) {
            DiskCache.Entry diskCacheEntry = attrs.getConfiguration().getDiskCache().get(attrs.getUri());
            if (diskCacheEntry != null) {
                if (Sketch.isDebugMode()) {
                    Log.d(Sketch.TAG, SketchUtils.concat(NAME, " - ", "runDispatch", " - ", "diskCache", " - ", attrs.getName()));
                }
                downloadResult = new DownloadResult(diskCacheEntry, false);
                postRunCompleted();
                return;
            }
        }

        // 在下载之前判断如果请求Level限制只能从本地加载的话就取消了
        if (options.getRequestLevel() == RequestLevel.LOCAL) {
            toCanceledStatus(options.getRequestLevelFrom() == RequestLevelFrom.PAUSE_DOWNLOAD ? CancelCause.PAUSE_DOWNLOAD : CancelCause.LEVEL_IS_LOCAL);
            if (Sketch.isDebugMode()) {
                Log.w(Sketch.TAG, SketchUtils.concat(NAME, " - ", "runDispatch", " - ", "canceled", " - ", options.getRequestLevelFrom() == RequestLevelFrom.PAUSE_DOWNLOAD ? "pause download" : "requestLevel is local", " - ", attrs.getName()));
            }
            return;
        }

        // 执行下载
        if (Sketch.isDebugMode()) {
            Log.d(Sketch.TAG, SketchUtils.concat(NAME, " - ", "runDispatch", " - ", "download", " - ", attrs.getName()));
        }
        submitRunDownload();
    }

    @Override
    protected void runDownload() {
        if (isCanceled()) {
            if (Sketch.isDebugMode()) {
                Log.w(Sketch.TAG, SketchUtils.concat(NAME, " - ", "runDownload", " - ", "canceled", " - ", "startDownload", " - ", attrs.getName()));
            }
            return;
        }

        // 调用下载器下载
        DownloadResult justDownloadResult = attrs.getConfiguration().getImageDownloader().download(this);

        if (isCanceled()) {
            if (Sketch.isDebugMode()) {
                Log.w(Sketch.TAG, SketchUtils.concat(NAME, " - ", "runDownload", " - ", "canceled", " - ", "downloadAfter", " - ", attrs.getName()));
            }
            return;
        }

        // 都是空的就算下载失败
        if (justDownloadResult == null || (justDownloadResult.getDiskCacheEntry() == null && justDownloadResult.getImageData() == null)) {
            toFailedStatus(FailCause.DOWNLOAD_FAIL);
            return;
        }

        // 下载成功了
        downloadResult = justDownloadResult;
        postRunCompleted();
    }

    @Override
    protected void runLoad() {

    }

    @Override
    public void updateProgress(int totalLength, int completedLength) {
        if (downloadProgressListener != null) {
            postRunUpdateProgress(totalLength, completedLength);
        }
    }

    @Override
    protected void runUpdateProgressInMainThread(int totalLength, int completedLength) {
        if (isFinished()) {
            if (Sketch.isDebugMode()) {
                Log.w(Sketch.TAG, SketchUtils.concat(NAME, " - ", "runUpdateProgressInMainThread", " - ", "finished", " - ", attrs.getName()));
            }
            return;
        }

        if (downloadProgressListener != null) {
            downloadProgressListener.onUpdateDownloadProgress(totalLength, completedLength);
        }
    }

    @Override
    protected void runCanceledInMainThread() {
        if (downloadListener != null) {
            downloadListener.onCanceled(cancelCause);
        }
    }

    @Override
    protected void runCompletedInMainThread() {
        if (isCanceled()) {
            if (Sketch.isDebugMode()) {
                Log.w(Sketch.TAG, SketchUtils.concat(NAME, " - ", "runCompletedInMainThread", " - ", "canceled", " - ", attrs.getName()));
            }
            return;
        }

        setRequestStatus(RequestStatus.COMPLETED);

        if (downloadListener != null) {
            if (downloadResult.getDiskCacheEntry() != null) {
                downloadListener.onCompleted(downloadResult.getDiskCacheEntry().getFile(), downloadResult.isFromNetwork());
            } else if (downloadResult.getImageData() != null) {
                downloadListener.onCompleted(downloadResult.getImageData());
            }
        }
    }

    @Override
    protected void runFailedInMainThread() {
        if (isCanceled()) {
            if (Sketch.isDebugMode()) {
                Log.w(Sketch.TAG, SketchUtils.concat(NAME, " - ", "runFailedInMainThread", " - ", "canceled", " - ", attrs.getName()));
            }
            return;
        }

        setRequestStatus(RequestStatus.FAILED);

        if (downloadListener != null) {
            downloadListener.onFailed(failCause);
        }
    }
}