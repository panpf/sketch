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
public class DefaultDownloadRequest implements DownloadRequest, Runnable {
    private static final int WHAT_CALLBACK_COMPLETED = 302;
    private static final int WHAT_CALLBACK_FAILED = 303;
    private static final int WHAT_CALLBACK_CANCELED = 304;
    private static final int WHAT_CALLBACK_PROGRESS = 305;
    private static final String NAME = "DefaultDownloadRequest";

    private Sketch sketch;  // Sketch
    private String uri;    // 图片地址
    private String name;    // 名称，用于在输出LOG的时候区分不同的请求
    private UriScheme uriScheme;    // Uri协议格式

    private DownloadOptions options;
    private DownloadProgressListener downloadProgressListener;  // 下载进度监听器
    private DownloadListener downloadListener;  // 下载监听器

    private DiskCache.Entry diskCacheEntry;
    private byte[] resultBytes;
    private RunStatus runStatus = RunStatus.DISPATCH;    // 运行状态，用于在执行run方法时知道该干什么
    private FailCause failCause;    // 失败原因
    private ImageFrom imageFrom;    // 图片来源
    private RequestStatus requestStatus = RequestStatus.WAIT_DISPATCH;  // 状态

    public DefaultDownloadRequest(Sketch sketch, String uri, UriScheme uriScheme, DownloadOptions options, DownloadListener downloadListener) {
        this.sketch = sketch;
        this.uri = uri;
        this.uriScheme = uriScheme;
        this.options = options;
        this.downloadListener = downloadListener;
    }

    @Override
    public Sketch getSketch() {
        return sketch;
    }

    @Override
    public String getUri() {
        return uri;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public UriScheme getUriScheme() {
        return uriScheme;
    }

    @Override
    public DownloadOptions getOptions() {
        return options;
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
        return null;
    }

    @Override
    public ImageFrom getImageFrom() {
        return imageFrom;
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
        sketch.getConfiguration().getHandler().obtainMessage(WHAT_CALLBACK_FAILED, this).sendToTarget();
    }

    @Override
    public void toCanceledStatus(CancelCause cancelCause) {
        this.requestStatus = RequestStatus.CANCELED;
        setRequestStatus(RequestStatus.CANCELED);
        sketch.getConfiguration().getHandler().obtainMessage(WHAT_CALLBACK_CANCELED, this).sendToTarget();
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
    public void postRunDispatch() {
        setRequestStatus(RequestStatus.WAIT_DISPATCH);
        this.runStatus = RunStatus.DISPATCH;
        sketch.getConfiguration().getRequestExecutor().getRequestDispatchExecutor().execute(this);
    }

    @Override
    public void postRunDownload() {
        setRequestStatus(RequestStatus.WAIT_DOWNLOAD);
        this.runStatus = RunStatus.DOWNLOAD;
        sketch.getConfiguration().getRequestExecutor().getNetRequestExecutor().execute(this);
    }

    @Override
    public void postRunLoad() {
        setRequestStatus(RequestStatus.WAIT_LOAD);
        this.runStatus = RunStatus.LOAD;
        sketch.getConfiguration().getRequestExecutor().getLocalRequestExecutor().execute(this);
    }

    @Override
    public void updateProgress(int totalLength, int completedLength) {
        if (downloadProgressListener != null) {
            sketch.getConfiguration().getHandler().obtainMessage(WHAT_CALLBACK_PROGRESS, totalLength, completedLength, this).sendToTarget();
        }
    }

    @Override
    public void run() {
        switch (runStatus) {
            case DISPATCH:
                executeDispatch();
                break;
            case DOWNLOAD:
                executeDownload();
                break;
            default:
                new IllegalArgumentException("unknown runStatus: " + runStatus.name()).printStackTrace();
                break;
        }
    }

    /**
     * 分发请求
     */
    private void executeDispatch() {
        setRequestStatus(RequestStatus.DISPATCHING);
        if (uriScheme == UriScheme.HTTP || uriScheme == UriScheme.HTTPS) {
            DiskCache.Entry diskCacheEntry = options.isCacheInDisk() ? sketch.getConfiguration().getDiskCache().get(uri) : null;
            if (diskCacheEntry != null) {
                if (Sketch.isDebugMode()) {
                    Log.d(Sketch.TAG, SketchUtils.concat(NAME, " - ", "executeDispatch", " - ", "diskCache", " - ", name));
                }
                this.imageFrom = ImageFrom.DISK_CACHE;
                this.diskCacheEntry = diskCacheEntry;
                sketch.getConfiguration().getHandler().obtainMessage(WHAT_CALLBACK_COMPLETED, this).sendToTarget();
            } else {
                if (options.getRequestLevel() == RequestLevel.LOCAL) {
                    if (options.getRequestLevelFrom() == RequestLevelFrom.PAUSE_DOWNLOAD) {
                        toCanceledStatus(CancelCause.PAUSE_DOWNLOAD);
                        if (Sketch.isDebugMode()) {
                            Log.w(Sketch.TAG, SketchUtils.concat(NAME, " - ", "canceled", " - ", "pause download", " - ", name));
                        }
                    } else {
                        toCanceledStatus(CancelCause.LEVEL_IS_LOCAL);
                        if (Sketch.isDebugMode()) {
                            Log.w(Sketch.TAG, SketchUtils.concat(NAME, " - ", "canceled", " - ", "requestLevel is local", " - ", name));
                        }
                    }
                    return;
                }

                postRunDownload();
                if (Sketch.isDebugMode()) {
                    Log.d(Sketch.TAG, SketchUtils.concat(NAME, " - ", "executeDispatch", " - ", "download", " - ", name));
                }
            }
        } else {
            if (Sketch.isDebugMode()) {
                Log.e(Sketch.TAG, SketchUtils.concat(NAME, " - ", "executeDispatch", " - ", "not support uri:", uri, " - ", name));
            }
            toFailedStatus(FailCause.URI_NO_SUPPORT);
        }
    }

    /**
     * 执行下载
     */
    private void executeDownload() {
        if (isCanceled()) {
            if (Sketch.isDebugMode()) {
                Log.w(Sketch.TAG, SketchUtils.concat(NAME, " - ", "executeDownload", " - ", "canceled", " - ", "startDownload", " - ", name));
            }
            return;
        }

        DownloadResult downloadResult = sketch.getConfiguration().getImageDownloader().download(this);

        if (isCanceled()) {
            if (Sketch.isDebugMode()) {
                Log.w(Sketch.TAG, SketchUtils.concat(NAME, " - ", "executeDownload", " - ", "canceled", " - ", "downloadAfter", " - ", name));
            }
            return;
        }

        if (downloadResult != null && (downloadResult.diskCacheEntry != null || downloadResult.imageData != null)) {
            this.imageFrom = downloadResult.fromNetwork ? ImageFrom.NETWORK : ImageFrom.DISK_CACHE;
            this.requestStatus = RequestStatus.COMPLETED;
            this.diskCacheEntry = downloadResult.diskCacheEntry;
            this.resultBytes = downloadResult.imageData;

            sketch.getConfiguration().getHandler().obtainMessage(WHAT_CALLBACK_COMPLETED, this).sendToTarget();
        } else {
            toFailedStatus(FailCause.DOWNLOAD_FAIL);
        }
    }

    private void handleCompletedOnMainThread() {
        if (isCanceled()) {
            if (Sketch.isDebugMode()) {
                Log.w(Sketch.TAG, SketchUtils.concat(NAME, " - ", "handleCompletedOnMainThread", " - ", "canceled", " - ", name));
            }
            return;
        }

        setRequestStatus(RequestStatus.COMPLETED);
        if (downloadListener != null) {
            if (diskCacheEntry != null) {
                downloadListener.onCompleted(diskCacheEntry.getFile(), imageFrom == ImageFrom.NETWORK);
            } else if (resultBytes != null) {
                downloadListener.onCompleted(resultBytes);
            }
        }
    }

    private void handleFailedOnMainThread() {
        if (isCanceled()) {
            if (Sketch.isDebugMode()) {
                Log.w(Sketch.TAG, SketchUtils.concat(NAME, " - ", "handleFailedOnMainThread", " - ", "canceled", " - ", name));
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
                Log.w(Sketch.TAG, SketchUtils.concat(NAME, " - ", "updateProgressOnMainThread", " - ", "finished", " - ", name));
            }
            return;
        }
        if (downloadProgressListener != null) {
            downloadProgressListener.onUpdateDownloadProgress(totalLength, completedLength);
        }
    }
}