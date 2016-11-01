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

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.locks.ReentrantLock;

import me.xiaopan.sketch.Sketch;
import me.xiaopan.sketch.cache.DiskCache;
import me.xiaopan.sketch.http.HttpStack;
import me.xiaopan.sketch.util.DiskLruCache;
import me.xiaopan.sketch.util.SketchUtils;

/**
 * 下载请求
 */
public class DownloadRequest extends AsyncRequest {
    private DownloadOptions options;
    private DownloadListener downloadListener;
    private DownloadProgressListener downloadProgressListener;

    private DownloadResult downloadResult;

    public DownloadRequest(
            Sketch sketch, DownloadInfo info,
            DownloadOptions options, DownloadListener downloadListener,
            DownloadProgressListener downloadProgressListener) {
        super(sketch, info);

        this.options = options;
        this.downloadListener = downloadListener;
        this.downloadProgressListener = downloadProgressListener;

        setLogName("DownloadRequest");
    }

    /**
     * 获取磁盘缓存key
     */
    public String getDiskCacheKey(){
        return ((DownloadInfo) info).getDiskCacheKey();
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
            if (Sketch.isDebugMode()) {
                printLogW("canceled", "runDispatch", "download request just start");
            }
            return;
        }

        // 从磁盘中找缓存文件
        if (!options.isDisableCacheInDisk()) {
            setStatus(Status.CHECK_DISK_CACHE);

            DiskCache diskCache = getSketch().getConfiguration().getDiskCache();
            DiskCache.Entry diskCacheEntry = diskCache.get(getDiskCacheKey());
            if (diskCacheEntry != null) {
                if (Sketch.isDebugMode()) {
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
        if (Sketch.isDebugMode()) {
            printLogD("download", "runDispatch");
        }
        submitRunDownload();
    }

    /**
     * 处理RequestLevel是LOCAL
     */
    void requestLevelIsLocal() {
        boolean isPauseDownload = options.getRequestLevelFrom() == RequestLevelFrom.PAUSE_DOWNLOAD;
        if (Sketch.isDebugMode()) {
            printLogW("canceled", "runDispatch", isPauseDownload ? "pause download" : "requestLevel is local");
        }
        canceled(isPauseDownload ? CancelCause.PAUSE_DOWNLOAD : CancelCause.REQUEST_LEVEL_IS_LOCAL);
    }

    @Override
    protected void runDownload() {
        if (isCanceled()) {
            if (Sketch.isDebugMode()) {
                printLogW("canceled", "runDownload", "start download");
            }
            return;
        }

        DiskCache diskCache = getSketch().getConfiguration().getDiskCache();

        // 使用磁盘缓存就必须要上锁
        ReentrantLock diskCacheEditLock = null;
        if (!getOptions().isDisableCacheInDisk()) {
            setStatus(Status.GET_DISK_CACHE_EDIT_LOCK);
            diskCacheEditLock = diskCache.getEditLock(getDiskCacheKey());
            if (diskCacheEditLock != null) {
                diskCacheEditLock.lock();
            }
        }

        DownloadResult justDownloadResult = download(diskCache, getDiskCacheKey());

        // 解锁
        if (diskCacheEditLock != null) {
            diskCacheEditLock.unlock();
        }

        if (isCanceled()) {
            if (Sketch.isDebugMode()) {
                printLogW("canceled", "runDownload", "download after");
            }
            return;
        }

        downloadResult = justDownloadResult;
        downloadCompleted();
    }

    private DownloadResult download(DiskCache diskCache, String diskCacheKey) {
        if (isCanceled()) {
            if (Sketch.isDebugMode()) {
                printLogW("canceled", "runDownload", "get disk cache edit lock after");
            }
            return null;
        }

        // 检查磁盘缓存
        if (!getOptions().isDisableCacheInDisk()) {
            setStatus(Status.CHECK_DISK_CACHE);
            DiskCache.Entry diskCacheEntry = diskCache.get(diskCacheKey);
            if (diskCacheEntry != null) {
                return new DownloadResult(diskCacheEntry, ImageFrom.DISK_CACHE);
            }
        }

        // 下载
        HttpStack httpStack = getSketch().getConfiguration().getHttpStack();
        int retryCount = 0;
        int maxRetryCount = httpStack.getMaxRetryCount();
        DownloadResult justDownloadResult = null;
        while (true) {
            try {
                justDownloadResult = realDownload(httpStack, diskCache, diskCacheKey);
                break;
            } catch (Throwable e) {
                e.printStackTrace();

                getSketch().getConfiguration().getExceptionMonitor().onDownloadError(this, e);

                if (isCanceled()) {
                    if (Sketch.isDebugMode()) {
                        printLogW("canceled", "runDownload", "download failed");
                    }
                    break;
                }

                if (httpStack.canRetry(e) && retryCount < maxRetryCount) {
                    retryCount++;
                    if (Sketch.isDebugMode()) {
                        printLogW("download failed", "runDownload", "retry");
                    }
                } else {
                    if (Sketch.isDebugMode()) {
                        printLogE("download failed", "runDownload", "end");
                    }
                    break;
                }
            }
        }

        return justDownloadResult;
    }

    private DownloadResult realDownload(HttpStack httpStack, DiskCache diskCache, String diskCacheKey) throws IOException, DiskLruCache.EditorChangedException, DiskLruCache.ClosedException {
        setStatus(Status.CONNECTING);

        HttpStack.ImageHttpResponse httpResponse = httpStack.getHttpResponse(getRealUri());
        if (isCanceled()) {
            httpResponse.releaseConnection();
            if (Sketch.isDebugMode()) {
                printLogW("canceled", "runDownload", "connect after");
            }
            return null;
        }

        setStatus(Status.CHECK_RESPONSE);

        // 检查状态码
        int responseCode;
        try {
            responseCode = httpResponse.getResponseCode();
        } catch (IOException e) {
            e.printStackTrace();
            httpResponse.releaseConnection();
            if (Sketch.isDebugMode()) {
                printLogE("get response code failed", "runDownload", "responseHeaders: " + httpResponse.getResponseHeadersString());
            }
            return null;
        }
        if (responseCode != 200) {
            httpResponse.releaseConnection();
            if (Sketch.isDebugMode()) {
                printLogE("response code exception", "runDownload", "responseHeaders: " + httpResponse.getResponseHeadersString());
            }
            return null;
        }

        // 检查内容长度
        long contentLength = httpResponse.getContentLength();
        if (contentLength <= 0) {
            httpResponse.releaseConnection();
            if (Sketch.isDebugMode()) {
                printLogE("content length exception", "runDownload", "contentLength: " + contentLength, "responseHeaders: " + httpResponse.getResponseHeadersString());
            }
            return null;
        }

        setStatus(Status.READ_DATA);

        // 获取输入流
        InputStream inputStream = httpResponse.getContent();
        if (isCanceled()) {
            SketchUtils.close(inputStream);
            if (Sketch.isDebugMode()) {
                printLogW("canceled", "runDownload", "get input stream after");
            }
            return null;
        }

        DiskCache.Editor diskCacheEditor = null;
        if (!getOptions().isDisableCacheInDisk()) {
            diskCacheEditor = diskCache.edit(diskCacheKey);
        }
        OutputStream outputStream;
        if (diskCacheEditor != null) {
            try {
                outputStream = new BufferedOutputStream(diskCacheEditor.newOutputStream(), 8 * 1024);
            } catch (FileNotFoundException e) {
                SketchUtils.close(inputStream);
                diskCacheEditor.abort();
                throw e;
            }
        } else {
            outputStream = new ByteArrayOutputStream();
        }

        // 读取数据
        int completedLength = 0;
        boolean readFully;
        try {
            completedLength = readData(inputStream, outputStream, (int) contentLength);

            readFully = completedLength == contentLength;
            if (diskCacheEditor != null) {
                if (readFully) {
                    diskCacheEditor.commit();
                } else {
                    diskCacheEditor.abort();
                }
            }
        } catch (IOException e) {
            if (diskCacheEditor != null) {
                diskCacheEditor.abort();
                diskCacheEditor = null;
            }
            throw e;
        } catch (DiskLruCache.ClosedException e) {
            e.printStackTrace();
            diskCacheEditor.abort();
            throw e;
        } finally {
            SketchUtils.close(outputStream);
            SketchUtils.close(inputStream);
        }

        if (isCanceled()) {
            if (Sketch.isDebugMode()) {
                printLogW("canceled", "runDownload", "read data after", readFully ? "read fully" : "not read fully");
            }
            return null;
        }

        if (Sketch.isDebugMode()) {
            printLogI("download success", "runDownload", "fileLength: " + completedLength + "/" + contentLength);
        }

        // 提交磁盘缓存并返回
        if (diskCacheEditor != null) {
            DiskCache.Entry diskCacheEntry = diskCache.get(diskCacheKey);
            if (diskCacheEntry != null) {
                return new DownloadResult(diskCacheEntry, ImageFrom.NETWORK);
            } else {
                if (Sketch.isDebugMode()) {
                    printLogW("not found disk cache", "runDownload", "download after");
                }
                return null;
            }
        } else {
            return new DownloadResult(((ByteArrayOutputStream) outputStream).toByteArray(), ImageFrom.NETWORK);
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
     */
    private void updateProgress(int totalLength, int completedLength) {
        if (downloadProgressListener != null) {
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
            if (Sketch.isDebugMode()) {
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
            if (Sketch.isDebugMode()) {
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
            if (Sketch.isDebugMode()) {
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