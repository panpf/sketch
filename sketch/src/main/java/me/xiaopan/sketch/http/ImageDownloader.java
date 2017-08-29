/*
 * Copyright (C) 2017 Peng fei Pan <sky@xiaopan.me>
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

package me.xiaopan.sketch.http;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.locks.ReentrantLock;

import me.xiaopan.sketch.Identifier;
import me.xiaopan.sketch.SLog;
import me.xiaopan.sketch.cache.DiskCache;
import me.xiaopan.sketch.request.BaseRequest;
import me.xiaopan.sketch.request.DownloadRequest;
import me.xiaopan.sketch.request.DownloadResult;
import me.xiaopan.sketch.request.ImageFrom;
import me.xiaopan.sketch.util.DiskLruCache;
import me.xiaopan.sketch.util.SketchUtils;

public class ImageDownloader implements Identifier {
    private static final String NAME = "ImageDownloader";

    /**
     * 下载，这个方法主要实现缓存锁与检查缓存
     */
    public DownloadResult download(DownloadRequest request) {
        if (request.isCanceled()) {
            if (SLog.isLoggable(SLog.LEVEL_DEBUG | SLog.TYPE_FLOW)) {
                SLog.d(NAME, "canceled. runDownload. start download. %s. %s",
                        Thread.currentThread().getName(), request.getKey());
            }
            return null;
        }

        DiskCache diskCache = request.getConfiguration().getDiskCache();
        String diskCacheKey = request.getUriInfo().getDiskCacheKey();

        // 使用磁盘缓存就必须要上锁
        ReentrantLock diskCacheEditLock = null;
        if (!request.getOptions().isCacheInDiskDisabled()) {
            diskCacheEditLock = diskCache.getEditLock(request.getUriInfo().getDiskCacheKey());
        }
        if (diskCacheEditLock != null) {
            diskCacheEditLock.lock();
        }

        DownloadResult justDownloadResult;
        try {
            if (request.isCanceled()) {
                if (SLog.isLoggable(SLog.LEVEL_DEBUG | SLog.TYPE_FLOW)) {
                    SLog.d(NAME, "canceled. runDownload. get disk cache edit lock after. %s. %s",
                            Thread.currentThread().getName(), request.getKey());
                }
                return null;
            }

            if (diskCacheEditLock != null) {
                request.setStatus(BaseRequest.Status.CHECK_DISK_CACHE);
                DiskCache.Entry diskCacheEntry = diskCache.get(diskCacheKey);
                if (diskCacheEntry != null) {
                    return new DownloadResult(diskCacheEntry, ImageFrom.DISK_CACHE);
                }
            }

            justDownloadResult = loopRetryDownload(request, diskCache, diskCacheKey);
        } finally {
            if (diskCacheEditLock != null) {
                diskCacheEditLock.unlock();
            }
        }

        return justDownloadResult;
    }

    /**
     * 循环重试下载，这个方法主要负责实心错误重试
     */
    private DownloadResult loopRetryDownload(DownloadRequest request, DiskCache diskCache, String diskCacheKey) {
        HttpStack httpStack = request.getConfiguration().getHttpStack();
        int retryCount = 0;
        int maxRetryCount = httpStack.getMaxRetryCount();
        DownloadResult justDownloadResult = null;
        while (true) {
            try {
                justDownloadResult = doDownload(request, httpStack, diskCache, diskCacheKey);
                break;
            } catch (Throwable e) {
                e.printStackTrace();

                request.getConfiguration().getErrorTracker().onDownloadError(request, e);

                if (request.isCanceled()) {
                    if (SLog.isLoggable(SLog.LEVEL_DEBUG | SLog.TYPE_FLOW)) {
                        SLog.d(NAME, "canceled. runDownload. download failed. %s. %s",
                                Thread.currentThread().getName(), request.getKey());
                    }
                    break;
                }

                if (httpStack.canRetry(e) && retryCount < maxRetryCount) {
                    retryCount++;
                    SLog.w(NAME, "download failed. runDownload. retry. %s. %s",
                            Thread.currentThread().getName(), request.getKey());
                } else {
                    SLog.w(NAME, "download failed. runDownload. end. %s. %s",
                            Thread.currentThread().getName(), request.getKey());
                    break;
                }
            }
        }

        return justDownloadResult;
    }

    /**
     * 真正下载的方法
     */
    private DownloadResult doDownload(DownloadRequest request, HttpStack httpStack, DiskCache diskCache, String diskCacheKey)
            throws IOException, DiskLruCache.EditorChangedException, DiskLruCache.ClosedException, DiskLruCache.FileNotExistException {
        request.setStatus(BaseRequest.Status.CONNECTING);

        HttpStack.ImageHttpResponse httpResponse = httpStack.getHttpResponse(request.getUriInfo().getContent());
        if (request.isCanceled()) {
            httpResponse.releaseConnection();
            if (SLog.isLoggable(SLog.LEVEL_DEBUG | SLog.TYPE_FLOW)) {
                SLog.d(NAME, "canceled. runDownload. connect after. %s. %s",
                        Thread.currentThread().getName(), request.getKey());
            }
            return null;
        }

        request.setStatus(BaseRequest.Status.CHECK_RESPONSE);

        // 检查状态码
        int responseCode;
        try {
            responseCode = httpResponse.getResponseCode();
        } catch (IOException e) {
            e.printStackTrace();
            httpResponse.releaseConnection();
            SLog.w(NAME, "get response code failed. runDownload. responseHeaders: %s. %s. %s",
                    httpResponse.getResponseHeadersString(), Thread.currentThread().getName(), request.getKey());
            throw new IllegalStateException("get response code exception", e);
        }
        if (responseCode != 200) {
            httpResponse.releaseConnection();
            SLog.e(NAME, "response code exception. runDownload. responseHeaders: %s. %s. %s",
                    httpResponse.getResponseHeadersString(), Thread.currentThread().getName(), request.getKey());
            throw new IllegalStateException("response code exception: " + responseCode);
        }

        // 检查内容长度
        long contentLength = httpResponse.getContentLength();
        if (contentLength <= 0 && !httpResponse.isContentChunked()) {
            httpResponse.releaseConnection();
            SLog.e(NAME, "content length exception. runDownload. contentLength: %d. responseHeaders: %s. %s. %s",
                    contentLength, httpResponse.getResponseHeadersString(), Thread.currentThread().getName(), request.getKey());
            throw new IllegalStateException("contentLength exception: " + contentLength + ", responseHeaders: " + httpResponse.getResponseHeadersString());
        }

        request.setStatus(BaseRequest.Status.READ_DATA);

        // 获取输入流
        InputStream inputStream = httpResponse.getContent();
        if (request.isCanceled()) {
            SketchUtils.close(inputStream);
            if (SLog.isLoggable(SLog.LEVEL_DEBUG | SLog.TYPE_FLOW)) {
                SLog.d(NAME, "canceled. runDownload. get input stream after. %s. %s",
                        Thread.currentThread().getName(), request.getKey());
            }
            return null;
        }

        DiskCache.Editor diskCacheEditor = null;
        if (!request.getOptions().isCacheInDiskDisabled()) {
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
            completedLength = readData(request, inputStream, outputStream, (int) contentLength);

            readFully = contentLength <= 0 || completedLength == contentLength;
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
        } catch (DiskLruCache.ClosedException | DiskLruCache.FileNotExistException e) {
            e.printStackTrace();
            diskCacheEditor.abort();
            throw e;
        } finally {
            SketchUtils.close(outputStream);
            SketchUtils.close(inputStream);
        }

        if (request.isCanceled()) {
            if (SLog.isLoggable(SLog.LEVEL_DEBUG | SLog.TYPE_FLOW)) {
                SLog.d(NAME, "canceled. runDownload. read data after. %s. %s. %s",
                        readFully ? "read fully" : "not read fully", Thread.currentThread().getName(), request.getKey());
            }
            return null;
        }

        if (SLog.isLoggable(SLog.LEVEL_DEBUG | SLog.TYPE_FLOW)) {
            SLog.d(NAME, "download success. runDownload. fileLength: %d/%d. %s. %s",
                    completedLength, contentLength, Thread.currentThread().getName(), request.getKey());
        }

        // 提交磁盘缓存并返回
        if (diskCacheEditor != null) {
            DiskCache.Entry diskCacheEntry = diskCache.get(diskCacheKey);
            if (diskCacheEntry != null) {
                return new DownloadResult(diskCacheEntry, ImageFrom.NETWORK);
            } else {
                SLog.e(NAME, "not found disk cache. runDownload. download after. %s. %s",
                        Thread.currentThread().getName(), request.getKey());
                throw new IllegalStateException("not found disk cache entry, key is " + diskCacheKey);
            }
        } else {
            return new DownloadResult(((ByteArrayOutputStream) outputStream).toByteArray(), ImageFrom.NETWORK);
        }
    }

    private int readData(DownloadRequest request, InputStream inputStream, OutputStream outputStream, int contentLength) throws IOException {
        int realReadCount;
        int completedLength = 0;
        long lastCallbackTime = 0;
        byte[] buffer = new byte[8 * 1024];
        while (true) {
            if (request.isCanceled()) {
                break;
            }

            realReadCount = inputStream.read(buffer);
            if (realReadCount != -1) {
                outputStream.write(buffer, 0, realReadCount);
                completedLength += realReadCount;

                // 每100毫秒回调一次进度
                long currentTime = System.currentTimeMillis();
                if (currentTime - lastCallbackTime >= 100) {
                    lastCallbackTime = currentTime;
                    request.updateProgress(contentLength, completedLength);
                }
            } else {
                // 结束的时候再次回调一下进度，确保页面上能显示100%
                request.updateProgress(contentLength, completedLength);
                break;
            }
        }
        outputStream.flush();
        return completedLength;
    }

    @Override
    public String getKey() {
        return NAME;
    }
}
