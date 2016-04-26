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

import android.os.Message;
import android.util.Log;

import me.xiaopan.sketch.cache.DiskCache;
import me.xiaopan.sketch.util.SketchUtils;

/**
 * 下载请求
 */
public class DefaultDownloadRequest extends SketchRequest implements DownloadRequest {
    private static final int WHAT_CALLBACK_COMPLETED = 302;
    private static final int WHAT_CALLBACK_FAILED = 303;
    private static final int WHAT_CALLBACK_CANCELED = 304;
    private static final int WHAT_CALLBACK_PROGRESS = 305;
    private static final String NAME = "DefaultDownloadRequest";

    private RequestAttrs attrs;
    private DownloadOptions options;
    private DownloadListener downloadListener;
    private DownloadProgressListener downloadProgressListener;

    private DownloadResult downloadResult;
    private FailCause failCause;    // 失败原因
    private CancelCause cancelCause;  // 取消原因
    private RequestStatus requestStatus = RequestStatus.WAIT_DISPATCH;  // 状态

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
        attrs.getConfiguration().getHandler().obtainMessage(WHAT_CALLBACK_FAILED, this).sendToTarget();
    }

    @Override
    public void toCanceledStatus(CancelCause cancelCause) {
        this.cancelCause = cancelCause;
        setRequestStatus(RequestStatus.CANCELED);
        attrs.getConfiguration().getHandler().obtainMessage(WHAT_CALLBACK_CANCELED, this).sendToTarget();
    }

    @Override
    public void invokeInMainThread(Message msg) {
        switch (msg.what) {
            case WHAT_CALLBACK_COMPLETED:
                handleCompletedOnMainThread();
                break;
            case WHAT_CALLBACK_PROGRESS:
                updateProgressOnMainThread(msg.arg1, msg.arg2);
                break;
            case WHAT_CALLBACK_FAILED:
                handleFailedOnMainThread();
                break;
            case WHAT_CALLBACK_CANCELED:
                handleCanceledOnMainThread();
                break;
            default:
                new IllegalArgumentException("unknown message what: " + msg.what).printStackTrace();
                break;
        }
    }

    @Override
    protected void onPostRunDispatch() {
        super.onPostRunDispatch();
        setRequestStatus(RequestStatus.WAIT_DISPATCH);
    }

    @Override
    protected void onPostRunDownload() {
        super.onPostRunDownload();
        setRequestStatus(RequestStatus.WAIT_DOWNLOAD);
    }

    @Override
    protected void onPostRunLoad() {
        super.onPostRunLoad();
        setRequestStatus(RequestStatus.WAIT_LOAD);
    }

    @Override
    public void updateProgress(int totalLength, int completedLength) {
        if (downloadProgressListener != null) {
            attrs.getConfiguration().getHandler().obtainMessage(WHAT_CALLBACK_PROGRESS, totalLength, completedLength, this).sendToTarget();
        }
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
                attrs.getConfiguration().getHandler().obtainMessage(WHAT_CALLBACK_COMPLETED, this).sendToTarget();
                return;
            }
        }

        // 在下载之前判断如果请求Level限制只能从本地加载的话就取消了
        if (options.getRequestLevel() == RequestLevel.LOCAL) {
            toCanceledStatus(options.getRequestLevelFrom() == RequestLevelFrom.PAUSE_DOWNLOAD ? CancelCause.PAUSE_DOWNLOAD : CancelCause.LEVEL_IS_LOCAL);
            if (Sketch.isDebugMode()) {
                Log.w(Sketch.TAG, SketchUtils.concat(NAME, " - ", "canceled", " - ", options.getRequestLevelFrom() == RequestLevelFrom.PAUSE_DOWNLOAD ? "pause download" : "requestLevel is local", " - ", attrs.getName()));
            }
            return;
        }

        // 执行下载
        if (Sketch.isDebugMode()) {
            Log.d(Sketch.TAG, SketchUtils.concat(NAME, " - ", "runDispatch", " - ", "download", " - ", attrs.getName()));
        }
        postRunDownload();
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
        attrs.getConfiguration().getHandler().obtainMessage(WHAT_CALLBACK_COMPLETED, this).sendToTarget();
    }

    @Override
    protected void runLoad() {

    }

    private void handleCompletedOnMainThread() {
        if (isCanceled()) {
            if (Sketch.isDebugMode()) {
                Log.w(Sketch.TAG, SketchUtils.concat(NAME, " - ", "handleCompletedOnMainThread", " - ", "canceled", " - ", attrs.getName()));
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

    private void handleFailedOnMainThread() {
        if (isCanceled()) {
            if (Sketch.isDebugMode()) {
                Log.w(Sketch.TAG, SketchUtils.concat(NAME, " - ", "handleFailedOnMainThread", " - ", "canceled", " - ", attrs.getName()));
            }
            return;
        }

        setRequestStatus(RequestStatus.FAILED);
        if (downloadListener != null) {
            downloadListener.onFailed(failCause);
        }
    }

    private void handleCanceledOnMainThread() {
        if (downloadListener != null) {
            downloadListener.onCanceled();
        }
    }

    private void updateProgressOnMainThread(int totalLength, int completedLength) {
        if (isFinished()) {
            if (Sketch.isDebugMode()) {
                Log.w(Sketch.TAG, SketchUtils.concat(NAME, " - ", "updateProgressOnMainThread", " - ", "finished", " - ", attrs.getName()));
            }
            return;
        }
        if (downloadProgressListener != null) {
            downloadProgressListener.onUpdateDownloadProgress(totalLength, completedLength);
        }
    }
}