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

import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import me.xiaopan.sketch.Sketch;
import me.xiaopan.sketch.cache.DiskCache;
import me.xiaopan.sketch.http.HttpStack;
import me.xiaopan.sketch.util.DiskLruCache;
import me.xiaopan.sketch.util.SketchUtils;

/**
 * 下载请求
 */
public class DownloadRequest extends BaseRequest {
    private DownloadOptions options;
    private DownloadListener downloadListener;
    private DownloadProgressListener progressListener;

    private DownloadResult downloadResult;

    public DownloadRequest(
            Sketch sketch, RequestAttrs requestAttrs,
            DownloadOptions options, DownloadListener downloadListener,
            DownloadProgressListener progressListener) {
        super(sketch, requestAttrs);

        this.options = options;
        this.downloadListener = downloadListener;
        this.progressListener = progressListener;

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
    public DownloadResult getDownloadResult() {
        return downloadResult;
    }

    /**
     * 设置下载结果
     */
    void setDownloadResult(DownloadResult downloadResult) {
        this.downloadResult = downloadResult;
    }

    @Override
    public void failed(FailedCause failedCause) {
        super.failed(failedCause);

        if (downloadListener != null) {
            postRunFailed();
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
        setStatus(Status.DISPATCHING);

        // 然后从磁盘缓存中找缓存文件
        if (options.isCacheInDisk()) {
            DiskCache diskCache = getSketch().getConfiguration().getDiskCache();
            DiskCache.Entry diskCacheEntry = diskCache.get(getRequestAttrs().getDiskCacheKey());
            if (diskCacheEntry != null) {
                if (Sketch.isDebugMode()) {
                    Log.d(Sketch.TAG, SketchUtils.concat(getLogName(),
                            " - ", "runDispatch",
                            " - ", "diskCache",
                            " - ", getRequestAttrs().getName()));
                }
                downloadResult = new DownloadResult(diskCacheEntry, false);
                downloadComplete();
                return;
            }
        }

        // 在下载之前判断如果请求Level限制只能从本地加载的话就取消了
        if (options.getRequestLevel() == RequestLevel.LOCAL) {
            requestLevelIsLocal();
            return;
        }

        // 执行下载
        if (Sketch.isDebugMode()) {
            Log.d(Sketch.TAG, SketchUtils.concat(getLogName(),
                    " - ", "runDispatch",
                    " - ", "download",
                    " - ", getRequestAttrs().getName()));
        }
        submitRunDownload();
    }

    /**
     * 处理RequestLevel是LOCAL
     */
    @SuppressWarnings("WeakerAccess")
    void requestLevelIsLocal() {
        boolean isPauseDownload = options.getRequestLevelFrom() == RequestLevelFrom.PAUSE_DOWNLOAD;

        if (Sketch.isDebugMode()) {
            Log.w(Sketch.TAG, SketchUtils.concat(getLogName(),
                    " - ", "runDispatch",
                    " - ", "canceled",
                    " - ", isPauseDownload ? "pause download" : "requestLevel is local",
                    " - ", getRequestAttrs().getName()));
        }

        canceled(isPauseDownload ? CancelCause.PAUSE_DOWNLOAD : CancelCause.LEVEL_IS_LOCAL);
    }

    @Override
    protected void runDownload() {
        if (isCanceled()) {
            if (Sketch.isDebugMode()) {
                Log.w(Sketch.TAG, SketchUtils.concat(getLogName(),
                        " - ", "runDownload",
                        " - ", "canceled",
                        " - ", "startDownload",
                        " - ", getRequestAttrs().getName()));
            }
            return;
        }

        setStatus(BaseRequest.Status.DOWNLOADING);

        // 调用下载器下载
        DownloadResult justDownloadResult = executeDownload();

        if (isCanceled()) {
            if (Sketch.isDebugMode()) {
                Log.w(Sketch.TAG, SketchUtils.concat(getLogName(),
                        " - ", "runDownload",
                        " - ", "canceled",
                        " - ", "downloadAfter",
                        " - ", getRequestAttrs().getName()));
            }
            return;
        }

        // 都是空的就算下载失败
        if (justDownloadResult == null
                || (justDownloadResult.getDiskCacheEntry() == null && justDownloadResult.getImageData() == null)) {
            failed(FailedCause.DOWNLOAD_FAIL);
            return;
        }

        // 下载成功了
        downloadResult = justDownloadResult;
        downloadComplete();
    }

    private DownloadResult executeDownload() {
        HttpStack httpStack = getSketch().getConfiguration().getHttpStack();
        DiskCache diskCache = getSketch().getConfiguration().getDiskCache();
        int retryCount = 0;
        int maxRetryCount = httpStack.getMaxRetryCount();
        DownloadResult result = null;

        while (true) {
            if (isCanceled()) {
                if (Sketch.isDebugMode()) {
                    Log.w(Sketch.TAG, SketchUtils.concat(getLogName(),
                            " - ", "runDownload",
                            " - ", "canceled",
                            " - ", "get lock after",
                            " - ", getRequestAttrs().getName()));
                }
                break;
            }

            // 如果缓存文件已经存在了就直接返回缓存文件
            if (getOptions().isCacheInDisk()) {
                DiskCache.Entry diskCacheEntry = diskCache.get(getRequestAttrs().getDiskCacheKey());
                if (diskCacheEntry != null) {
                    result = new DownloadResult(diskCacheEntry, false);
                    break;
                }
            }

            try {
                result = realDownload(httpStack, diskCache);
                break;
            } catch (Throwable e) {
                e.printStackTrace();

                if (httpStack.canRetry(e) && retryCount < maxRetryCount) {
                    retryCount++;
                    if (Sketch.isDebugMode()) {
                        Log.w(Sketch.TAG, SketchUtils.concat(getLogName(),
                                " - ", "runDownload",
                                " - ", "download failed",
                                " - ", "retry",
                                " - ", getRequestAttrs().getName()));
                    }
                } else {
                    if (Sketch.isDebugMode()) {
                        Log.e(Sketch.TAG, SketchUtils.concat(getLogName(),
                                " - ", "runDownload",
                                " - ", "download failed",
                                " - ", "end",
                                " - ", getRequestAttrs().getName()));
                    }
                    break;
                }
            }
        }

        return result;
    }

    private DownloadResult realDownload(HttpStack httpStack, DiskCache diskCache) throws IOException, DiskLruCache.EditorChangedException {
        HttpStack.ImageHttpResponse httpResponse = httpStack.getHttpResponse(getRequestAttrs().getRealUri());

        if (isCanceled()) {
            httpResponse.releaseConnection();
            if (Sketch.isDebugMode()) {
                Log.w(Sketch.TAG, SketchUtils.concat(getLogName(),
                        " - ", "runDownload",
                        " - ", "canceled",
                        " - ", "connect after",
                        " - ", getRequestAttrs().getName()));
            }
            return null;
        }

        // 检查状态码
        int responseCode;
        try {
            responseCode = httpResponse.getResponseCode();
        } catch (IOException e) {
            e.printStackTrace();
            httpResponse.releaseConnection();
            if (Sketch.isDebugMode()) {
                Log.w(Sketch.TAG, SketchUtils.concat(getLogName(),
                        " - ", "runDownload",
                        " - ", "get response code failed",
                        " - ", getRequestAttrs().getName(),
                        " - ", "ResponseHeaders:", httpResponse.getResponseHeadersString()));
            }
            return null;
        }
        String responseMessage;
        try {
            responseMessage = httpResponse.getResponseMessage();
        } catch (IOException e) {
            e.printStackTrace();
            httpResponse.releaseConnection();
            if (Sketch.isDebugMode()) {
                Log.w(Sketch.TAG, SketchUtils.concat(getLogName(),
                        " - ", "runDownload",
                        " - ", "get response message failed",
                        " - ", getRequestAttrs().getName(),
                        " - ", "ResponseHeaders:", httpResponse.getResponseHeadersString()));
            }
            return null;
        }
        if (responseCode != 200) {
            httpResponse.releaseConnection();
            if (Sketch.isDebugMode()) {
                Log.e(Sketch.TAG, SketchUtils.concat(getLogName(),
                        " - ", "runDownload",
                        " - ", "response code exception",
                        " - ", "responseCode:", String.valueOf(responseCode),
                        " - ", "responseMessage:", responseMessage,
                        " - ", "ResponseHeaders:", httpResponse.getResponseHeadersString(),
                        " - ", getRequestAttrs().getName()));
            }
            return null;
        }

        // 检查内容长度
        long contentLength = httpResponse.getContentLength();
        if (contentLength <= 0) {
            httpResponse.releaseConnection();
            if (Sketch.isDebugMode()) {
                Log.w(Sketch.TAG, SketchUtils.concat(getLogName(),
                        " - ", "runDownload",
                        " - ", "content length exception",
                        " - ", "contentLength:" + contentLength,
                        " - ", "ResponseHeaders:", httpResponse.getResponseHeadersString(),
                        " - ", getRequestAttrs().getName()));
            }
            return null;
        }

        // 获取输入流
        InputStream inputStream = httpResponse.getContent();

        if (isCanceled()) {
            SketchUtils.close(inputStream);
            if (Sketch.isDebugMode()) {
                Log.w(Sketch.TAG, SketchUtils.concat(getLogName(),
                        " - ", "runDownload",
                        " - ", "canceled",
                        " - ", "get input stream after",
                        " - ", getRequestAttrs().getName()));
            }
            return null;
        }

        DiskLruCache.Editor diskCacheEditor = null;
        if (getOptions().isCacheInDisk()) {
            diskCacheEditor = diskCache.edit(getRequestAttrs().getDiskCacheKey());
        }
        OutputStream outputStream;
        if (diskCacheEditor != null) {
            try {
                outputStream = new BufferedOutputStream(diskCacheEditor.newOutputStream(0), 8 * 1024);
            } catch (FileNotFoundException e) {
                SketchUtils.close(inputStream);
                try {
                    diskCacheEditor.abort();
                } catch (DiskLruCache.EditorChangedException e1) {
                    e1.printStackTrace();
                }
                throw e;
            }
        } else {
            // 不需要将数据缓存到本地或本地缓存不可用的时候就使用ByteArrayOutputStream来存储数据
            outputStream = new ByteArrayOutputStream();
        }

        // 读取数据
        int completedLength = 0;
        try {
            completedLength = readData(inputStream, outputStream, (int) contentLength);
        } catch (IOException e) {
            if (diskCacheEditor != null) {
                try {
                    diskCacheEditor.abort();
                } catch (DiskLruCache.EditorChangedException e1) {
                    e1.printStackTrace();
                }
            }
            throw e;
        } finally {
            SketchUtils.close(outputStream);
            SketchUtils.close(inputStream);
        }

        if (isCanceled()) {
            if (Sketch.isDebugMode()) {
                Log.w(Sketch.TAG, SketchUtils.concat(getLogName(),
                        " - ", "runDownload",
                        " - ", "canceled",
                        " - ", "read data after",
                        " - ", getRequestAttrs().getName()));
            }
            return null;
        }

        if (Sketch.isDebugMode()) {
            Log.i(Sketch.TAG, SketchUtils.concat(getLogName(),
                    " - ", "runDownload",
                    " - ", "download success",
                    " - ", "fileLength:", completedLength, "/", contentLength,
                    " - ", getRequestAttrs().getName()));
        }

        // 返回结果
        if (getOptions().isCacheInDisk() && diskCacheEditor != null) {
            diskCacheEditor.commit();
            return new DownloadResult(diskCache.get(getRequestAttrs().getDiskCacheKey()), true);
        } else if (outputStream instanceof ByteArrayOutputStream) {
            return new DownloadResult(((ByteArrayOutputStream) outputStream).toByteArray());
        } else {
            return null;
        }
    }

    private int readData(InputStream inputStream, OutputStream outputStream, int contentLength) throws IOException {
        int realReadCount;
        int completedLength = 0;
        long lastCallbackTime = 0;
        byte[] buffer = new byte[8 * 1024];
        while (true) {
            if (isCanceled()) {
                break;
            }

            realReadCount = inputStream.read(buffer);
            if (realReadCount != -1) {
                outputStream.write(buffer, 0, realReadCount);
                completedLength += realReadCount;

                // 每秒钟回调一次进度
                long currentTime = System.currentTimeMillis();
                if (currentTime - lastCallbackTime >= 1000) {
                    lastCallbackTime = currentTime;
                    updateProgress(contentLength, completedLength);
                }
            } else {
                // 结束的时候再次回调一下进度，确保页面上能显示100%
                updateProgress(contentLength, completedLength);
                break;
            }
        }
        outputStream.flush();
        return completedLength;
    }

    @Override
    protected void runLoad() {

    }

    /**
     * 更新进度
     *
     * @param totalLength     文件总长度
     * @param completedLength 已完成长度
     */
    private void updateProgress(int totalLength, int completedLength) {
        if (progressListener != null) {
            postRunUpdateProgress(totalLength, completedLength);
        }
    }

    /**
     * 下载完成后续处理
     */
    protected void downloadComplete() {
        postRunCompleted();
    }

    @Override
    protected void runUpdateProgressInMainThread(int totalLength, int completedLength) {
        if (isFinished()) {
            if (Sketch.isDebugMode()) {
                Log.w(Sketch.TAG, SketchUtils.concat(getLogName(),
                        " - ", "runUpdateProgressInMainThread",
                        " - ", "finished",
                        " - ", getRequestAttrs().getName()));
            }
            return;
        }

        if (progressListener != null) {
            progressListener.onUpdateDownloadProgress(totalLength, completedLength);
        }
    }

    @Override
    protected void runCanceledInMainThread() {
        if (downloadListener != null) {
            downloadListener.onCanceled(getCancelCause());
        }
    }

    @Override
    protected void runCompletedInMainThread() {
        if (isCanceled()) {
            if (Sketch.isDebugMode()) {
                Log.w(Sketch.TAG, SketchUtils.concat(getLogName(),
                        " - ", "runCompletedInMainThread",
                        " - ", "canceled",
                        " - ", getRequestAttrs().getName()));
            }
            return;
        }

        setStatus(Status.COMPLETED);

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
                Log.w(Sketch.TAG, SketchUtils.concat(getLogName(),
                        " - ", "runFailedInMainThread",
                        " - ", "canceled",
                        " - ", getRequestAttrs().getName()));
            }
            return;
        }

        if (downloadListener != null) {
            downloadListener.onFailed(getFailedCause());
        }
    }
}