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
            Sketch sketch, RequestAttrs requestAttrs,
            DownloadOptions options, DownloadListener downloadListener,
            DownloadProgressListener downloadProgressListener) {
        super(sketch, requestAttrs);

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
        if (isCanceled()) {
            if (Sketch.isDebugMode()) {
                printLogW("runDispatch", "canceled", "start dispatch");
            }
            return;
        }

        // 从磁盘中找缓存文件
        if (!options.isDisableCacheInDisk()) {
            setStatus(Status.CHECK_DISK_CACHE);

            DiskCache diskCache = getSketch().getConfiguration().getDiskCache();
            String diskCacheKey = getAttrs().getUri();
            DiskCache.Entry diskCacheEntry = diskCache.get(diskCacheKey);
            if (diskCacheEntry != null) {
                if (Sketch.isDebugMode()) {
                    printLogD("runDispatch", "diskCache");
                }
                downloadResult = new DownloadResult(diskCacheEntry, false);
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
            printLogD("runDispatch", "download");
        }
        submitRunDownload();
    }

    /**
     * 处理RequestLevel是LOCAL
     */
    void requestLevelIsLocal() {
        boolean isPauseDownload = options.getRequestLevelFrom() == RequestLevelFrom.PAUSE_DOWNLOAD;
        if (Sketch.isDebugMode()) {
            printLogW("runDispatch", "canceled", isPauseDownload ? "pause download" : "requestLevel is local");
        }
        canceled(isPauseDownload ? CancelCause.PAUSE_DOWNLOAD : CancelCause.REQUEST_LEVEL_IS_LOCAL);
    }

    @Override
    protected void runDownload() {
        if (isCanceled()) {
            if (Sketch.isDebugMode()) {
                printLogW("runDownload", "canceled", "start download");
            }
            return;
        }

        String diskCacheKey = getAttrs().getUri();
        DiskCache diskCache = getSketch().getConfiguration().getDiskCache();

        // 使用磁盘缓存就必须要上锁
        ReentrantLock diskCacheEditLock = null;
        if (!getOptions().isDisableCacheInDisk()) {
            setStatus(Status.GET_DISK_CACHE_EDIT_LOCK);
            diskCacheEditLock = diskCache.getEditLock(diskCacheKey);
            diskCacheEditLock.lock();
        }

        DownloadResult justDownloadResult = download(diskCache, diskCacheKey);

        // 解锁
        if (diskCacheEditLock != null) {
            diskCacheEditLock.unlock();
        }

        if (isCanceled()) {
            if (Sketch.isDebugMode()) {
                printLogW("runDownload", "canceled", "download after");
            }
            return;
        }

        downloadResult = justDownloadResult;
        downloadCompleted();
    }

    private DownloadResult download(DiskCache diskCache, String diskCacheKey) {
        if (isCanceled()) {
            if (Sketch.isDebugMode()) {
                printLogW("runDownload", "canceled", "get disk cache edit lock after");
            }
            return null;
        }

        // 检查磁盘缓存
        if (!getOptions().isDisableCacheInDisk()) {
            setStatus(Status.CHECK_DISK_CACHE);
            DiskCache.Entry diskCacheEntry = diskCache.get(diskCacheKey);
            if (diskCacheEntry != null) {
                return new DownloadResult(diskCacheEntry, false);
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

                if (isCanceled()) {
                    if (Sketch.isDebugMode()) {
                        printLogW("runDownload", "canceled", "download failed");
                    }
                    break;
                }

                if (httpStack.canRetry(e) && retryCount < maxRetryCount) {
                    retryCount++;
                    if (Sketch.isDebugMode()) {
                        printLogW("runDownload", "download failed", "retry");
                    }
                } else {
                    if (Sketch.isDebugMode()) {
                        printLogE("runDownload", "download failed", "end");
                    }
                    break;
                }
            }
        }

        return justDownloadResult;
    }

    private DownloadResult realDownload(HttpStack httpStack, DiskCache diskCache, String diskCacheKey) throws IOException, DiskLruCache.EditorChangedException {
        setStatus(Status.CONNECTING);

        HttpStack.ImageHttpResponse httpResponse = httpStack.getHttpResponse(getAttrs().getRealUri());
        if (isCanceled()) {
            httpResponse.releaseConnection();
            if (Sketch.isDebugMode()) {
                printLogW("runDownload", "canceled", "connect after");
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
                printLogE("runDownload", "get response code failed", "responseHeaders: " + httpResponse.getResponseHeadersString());
            }
            return null;
        }
        if (responseCode != 200) {
            httpResponse.releaseConnection();
            if (Sketch.isDebugMode()) {
                printLogE("runDownload", "response code exception", "responseHeaders: " + httpResponse.getResponseHeadersString());
            }
            return null;
        }

        // 检查内容长度
        long contentLength = httpResponse.getContentLength();
        if (contentLength <= 0) {
            httpResponse.releaseConnection();
            if (Sketch.isDebugMode()) {
                printLogE("runDownload", "content length exception", "contentLength: " + contentLength, "responseHeaders: " + httpResponse.getResponseHeadersString());
            }
            return null;
        }

        setStatus(Status.READ_DATA);

        // 获取输入流
        InputStream inputStream = httpResponse.getContent();
        if (isCanceled()) {
            SketchUtils.close(inputStream);
            if (Sketch.isDebugMode()) {
                printLogW("runDownload", "canceled", "get input stream after");
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
            // 不需要将数据缓存到本地或本地缓存不可用的时候就使用ByteArrayOutputStream来存储数据
            outputStream = new ByteArrayOutputStream();
        }

        // 读取数据
        int completedLength = 0;
        try {
            completedLength = readData(inputStream, outputStream, (int) contentLength);
        } catch (IOException e) {
            if (diskCacheEditor != null) {
                diskCacheEditor.abort();
            }
            throw e;
        } finally {
            SketchUtils.close(outputStream);
            SketchUtils.close(inputStream);
        }

        if (isCanceled()) {
            boolean readFully = completedLength == contentLength;
            if (Sketch.isDebugMode()) {
                printLogW("runDownload", "canceled", "read data after", readFully ? "read fully" : "not read fully");
            }
            if (diskCacheEditor != null) {
                if (readFully) {
                    diskCacheEditor.commit();
                } else {
                    diskCacheEditor.abort();
                }
            }
            return null;
        }

        if (Sketch.isDebugMode()) {
            printLogI("runDownload", "download success", "fileLength: " + completedLength + "/" + contentLength);
        }

        // 返回结果
        if (!getOptions().isDisableCacheInDisk() && diskCacheEditor != null) {
            diskCacheEditor.commit();
            return new DownloadResult(diskCache.get(diskCacheKey), true);
        } else if (outputStream instanceof ByteArrayOutputStream) {
            return new DownloadResult(((ByteArrayOutputStream) outputStream).toByteArray(), true);
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
            failed(FailedCause.DOWNLOAD_FAIL);
        }
    }

    @Override
    protected void runUpdateProgressInMainThread(int totalLength, int completedLength) {
        if (isFinished()) {
            if (Sketch.isDebugMode()) {
                printLogW("runUpdateProgressInMainThread", "finished");
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
                printLogW("runCompletedInMainThread", "canceled");
            }
            return;
        }

        setStatus(Status.COMPLETED);

        if (downloadListener != null && downloadResult != null && downloadResult.hasData()) {
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
                printLogW("runFailedInMainThread", "canceled");
            }
            return;
        }

        if (downloadListener != null) {
            downloadListener.onFailed(getFailedCause());
        }
    }

    @Override
    protected void runCanceledInMainThread() {
        if (downloadListener != null) {
            downloadListener.onCanceled(getCancelCause());
        }
    }
}