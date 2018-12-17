/*
 * Copyright (C) 2017 Peng fei Pan <sky@panpf.me>
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

package me.panpf.sketch.http;

import androidx.annotation.NonNull;
import android.text.TextUtils;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.locks.ReentrantLock;

import me.panpf.sketch.SLog;
import me.panpf.sketch.cache.DiskCache;
import me.panpf.sketch.request.BaseRequest;
import me.panpf.sketch.request.CanceledException;
import me.panpf.sketch.request.DownloadRequest;
import me.panpf.sketch.request.DownloadResult;
import me.panpf.sketch.request.ErrorCause;
import me.panpf.sketch.request.ImageFrom;
import me.panpf.sketch.util.DiskLruCache;
import me.panpf.sketch.util.SketchUtils;

/**
 * 负责下载并缓存图片
 */
public class ImageDownloader {
    private static final String NAME = "ImageDownloader";

    /**
     * 此方法是下载图片的入口，并主要负责下载缓存锁的申请和释放
     *
     * @param request {@link DownloadRequest}
     * @return {@link DownloadResult}
     * @throws CanceledException 已取消
     * @throws DownloadException 下载失败
     */
    @NonNull
    public DownloadResult download(@NonNull DownloadRequest request) throws CanceledException, DownloadException {
        DiskCache diskCache = request.getConfiguration().getDiskCache();
        String diskCacheKey = request.getDiskCacheKey();

        // 使用磁盘缓存就必须要上锁
        ReentrantLock diskCacheEditLock = null;
        if (!request.getOptions().isCacheInDiskDisabled()) {
            diskCacheEditLock = diskCache.getEditLock(diskCacheKey);
        }
        if (diskCacheEditLock != null) {
            diskCacheEditLock.lock();
        }

        try {
            if (request.isCanceled()) {
                if (SLog.isLoggable(SLog.LEVEL_DEBUG | SLog.TYPE_FLOW)) {
                    SLog.d(NAME, "Download canceled after get disk cache edit lock. %s. %s", request.getThreadName(), request.getKey());
                }
                throw new CanceledException();
            }

            if (diskCacheEditLock != null) {
                request.setStatus(BaseRequest.Status.CHECK_DISK_CACHE);
                DiskCache.Entry diskCacheEntry = diskCache.get(diskCacheKey);
                if (diskCacheEntry != null) {
                    return new DownloadResult(diskCacheEntry, ImageFrom.DISK_CACHE);
                }
            }

            return loopRetryDownload(request, diskCache, diskCacheKey);
        } finally {
            if (diskCacheEditLock != null) {
                diskCacheEditLock.unlock();
            }
        }
    }

    /**
     * 此方法负责下载的失败重试逻辑
     *
     * @param request      {@link DownloadRequest}
     * @param diskCache    {@link DiskCache}. 用来写出并缓存数据
     * @param diskCacheKey 磁盘缓存 key
     * @return {@link DownloadResult}
     * @throws CanceledException 已取消
     * @throws DownloadException 下载失败
     */
    @NonNull
    private DownloadResult loopRetryDownload(@NonNull DownloadRequest request, @NonNull DiskCache diskCache,
                                             @NonNull String diskCacheKey) throws CanceledException, DownloadException {
        HttpStack httpStack = request.getConfiguration().getHttpStack();
        int retryCount = 0;
        final int maxRetryCount = httpStack.getMaxRetryCount();
        String uri = request.getUri();
        while (true) {
            try {
                return doDownload(request, uri, httpStack, diskCache, diskCacheKey);
            } catch (RedirectsException e) {
                uri = e.getNewUrl();
            } catch (Throwable tr) {
                request.getConfiguration().getErrorTracker().onDownloadError(request, tr);

                if (request.isCanceled()) {
                    String message = String.format("Download exception, but canceled. %s. %s", request.getThreadName(), request.getKey());
                    if (SLog.isLoggable(SLog.LEVEL_DEBUG | SLog.TYPE_FLOW)) {
                        SLog.d(NAME, tr, message);
                    }
                    throw new DownloadException(message, tr, ErrorCause.DOWNLOAD_EXCEPTION_AND_CANCELED);
                } else if (httpStack.canRetry(tr) && retryCount < maxRetryCount) {
                    tr.printStackTrace();
                    retryCount++;
                    String message = String.format("Download exception but can retry. %s. %s", request.getThreadName(), request.getKey());
                    SLog.w(NAME, tr, message);
                } else if (tr instanceof CanceledException) {
                    throw (CanceledException) tr;
                } else if (tr instanceof DownloadException) {
                    throw (DownloadException) tr;
                } else {
                    String message = String.format("Download failed. %s. %s", request.getThreadName(), request.getKey());
                    SLog.w(NAME, tr, message);
                    throw new DownloadException(message, tr, ErrorCause.DOWNLOAD_UNKNOWN_EXCEPTION);
                }
            }
        }
    }

    /**
     * 真正的下载核心逻辑方法，发送 http 请求并读取响应
     *
     * @param request      {@link DownloadRequest}
     * @param uri          图片 uri
     * @param httpStack    {@link HttpStack}. 用来发送 http 请求并且获取响应
     * @param diskCache    {@link DiskCache}. 用来写出并缓存数据
     * @param diskCacheKey 磁盘缓存 key
     * @return {@link DownloadResult}
     * @throws IOException        发生 IO 异常
     * @throws CanceledException  已取消
     * @throws DownloadException  下载失败
     * @throws RedirectsException 图片地址重定向了
     */
    @NonNull
    private DownloadResult doDownload(@NonNull DownloadRequest request, @NonNull String uri, @NonNull HttpStack httpStack,
                                      @NonNull DiskCache diskCache, @NonNull String diskCacheKey)
            throws IOException, CanceledException, DownloadException, RedirectsException {
        // Opening http connection
        request.setStatus(BaseRequest.Status.CONNECTING);
        HttpStack.Response response;
        //noinspection CaughtExceptionImmediatelyRethrown
        try {
            response = httpStack.getResponse(uri);
        } catch (IOException e) {
            throw e;
        }

        // Check canceled
        if (request.isCanceled()) {
            response.releaseConnection();
            if (SLog.isLoggable(SLog.LEVEL_DEBUG | SLog.TYPE_FLOW)) {
                SLog.d(NAME, "Download canceled after opening the connection. %s. %s", request.getThreadName(), request.getKey());
            }
            throw new CanceledException();
        }

        // Check response code, must be 200
        int responseCode;
        try {
            responseCode = response.getCode();
        } catch (IOException e) {
            response.releaseConnection();
            String message = String.format("Get response code exception. responseHeaders: %s. %s. %s",
                    response.getHeadersString(), request.getThreadName(), request.getKey());
            SLog.w(NAME, e, message);
            throw new DownloadException(message, e, ErrorCause.DOWNLOAD_GET_RESPONSE_CODE_EXCEPTION);
        }
        if (responseCode != 200) {
            response.releaseConnection();

            // redirects
            if (responseCode == 301 || responseCode == 302) {
                String newUri = response.getHeader("Location");
                if (!TextUtils.isEmpty(newUri)) {
                    // To prevent infinite redirection
                    if (uri.equals(request.getUri())) {
                        if (SLog.isLoggable(SLog.LEVEL_DEBUG | SLog.TYPE_FLOW)) {
                            SLog.d(NAME, "Uri redirects. originUri: %s, newUri: %s. %s", request.getUri(), newUri, request.getKey());
                        }
                        throw new RedirectsException(newUri);
                    } else {
                        SLog.e(NAME, "Disable unlimited redirects, originUri: %s, redirectsUri=%s, newUri=%s. %s", request.getUri(), uri, newUri, request.getKey());
                    }
                } else {
                    SLog.w(NAME, "Uri redirects failed. newUri is empty, originUri: %s. %s", request.getUri(), request.getKey());
                }
            }

            String message = String.format("Response code exception. responseHeaders: %s. %s. %s",
                    response.getHeadersString(), request.getThreadName(), request.getKey());
            SLog.e(NAME, message);
            throw new DownloadException(message, ErrorCause.DOWNLOAD_RESPONSE_CODE_EXCEPTION);
        }

        // Check content length, must be greater than 0 or is chunked
        long contentLength = response.getContentLength();
        if (contentLength <= 0 && !response.isContentChunked()) {
            response.releaseConnection();
            String message = String.format("Content length exception. contentLength: %d, responseHeaders: %s. %s. %s",
                    contentLength, response.getHeadersString(), request.getThreadName(), request.getKey());
            SLog.e(NAME, message);
            throw new DownloadException(message, ErrorCause.DOWNLOAD_CONTENT_LENGTH_EXCEPTION);
        }

        // Get content
        InputStream inputStream;
        try {
            inputStream = response.getContent();
        } catch (IOException e) {
            response.releaseConnection();
            throw e;
        }

        // Check canceled
        if (request.isCanceled()) {
            SketchUtils.close(inputStream);
            if (SLog.isLoggable(SLog.LEVEL_DEBUG | SLog.TYPE_FLOW)) {
                SLog.d(NAME, "Download canceled after get content. %s. %s", request.getThreadName(), request.getKey());
            }
            throw new CanceledException();
        }

        // Ready OutputStream, the ByteArrayOutputStream is used when the disk cache is disabled
        DiskCache.Editor diskCacheEditor = null;
        if (!request.getOptions().isCacheInDiskDisabled()) {
            diskCacheEditor = diskCache.edit(diskCacheKey);
        }
        OutputStream outputStream;
        if (diskCacheEditor != null) {
            try {
                outputStream = new BufferedOutputStream(diskCacheEditor.newOutputStream(), 8 * 1024);
            } catch (IOException e) {
                SketchUtils.close(inputStream);
                diskCacheEditor.abort();
                String message = String.format("Open disk cache exception. %s. %s", request.getThreadName(), request.getKey());
                SLog.e(NAME, e, message);
                throw new DownloadException(message, e, ErrorCause.DOWNLOAD_OPEN_DISK_CACHE_EXCEPTION);
            }
        } else {
            outputStream = new ByteArrayOutputStream();
        }

        // Read data
        request.setStatus(BaseRequest.Status.READ_DATA);
        int completedLength;
        try {
            completedLength = readData(request, inputStream, outputStream, (int) contentLength);
        } catch (IOException e) {
            if (diskCacheEditor != null) {
                diskCacheEditor.abort();
            }
            String message = String.format("Read data exception. %s. %s", request.getThreadName(), request.getKey());
            SLog.e(NAME, e, message);
            throw new DownloadException(message, e, ErrorCause.DOWNLOAD_READ_DATA_EXCEPTION);
        } catch (CanceledException e) {
            if (diskCacheEditor != null) {
                diskCacheEditor.abort();
            }
            throw e;
        } finally {
            SketchUtils.close(outputStream);
            SketchUtils.close(inputStream);
        }

        // Check content fully and commit the disk cache
        boolean readFully = (contentLength <= 0 && response.isContentChunked()) || completedLength == contentLength;
        if (readFully) {
            if (diskCacheEditor != null) {
                try {
                    diskCacheEditor.commit();
                } catch (IOException | DiskLruCache.EditorChangedException | DiskLruCache.ClosedException | DiskLruCache.FileNotExistException e) {
                    String message = String.format("Disk cache commit exception. %s. %s", request.getThreadName(), request.getKey());
                    SLog.e(NAME, e, message);
                    throw new DownloadException(message, e, ErrorCause.DOWNLOAD_DISK_CACHE_COMMIT_EXCEPTION);
                }
            }
        } else {
            if (diskCacheEditor != null) {
                diskCacheEditor.abort();
            }
            String message = String.format("The data is not fully read. contentLength:%d, completedLength:%d, ContentChunked:%s. %s. %s",
                    contentLength, completedLength, response.isContentChunked(), request.getThreadName(), request.getKey());
            SLog.e(NAME, message);
            throw new DownloadException(message, ErrorCause.DOWNLOAD_DATA_NOT_FULLY_READ);
        }

        // Return DownloadResult
        if (diskCacheEditor == null) {
            if (SLog.isLoggable(SLog.LEVEL_DEBUG | SLog.TYPE_FLOW)) {
                SLog.d(NAME, "Download success. Data is saved to disk cache. fileLength: %d/%d. %s. %s",
                        completedLength, contentLength, request.getThreadName(), request.getKey());
            }
            return new DownloadResult(((ByteArrayOutputStream) outputStream).toByteArray(), ImageFrom.NETWORK);
        } else {
            DiskCache.Entry diskCacheEntry = diskCache.get(diskCacheKey);
            if (diskCacheEntry != null) {
                if (SLog.isLoggable(SLog.LEVEL_DEBUG | SLog.TYPE_FLOW)) {
                    SLog.d(NAME, "Download success. data is saved to memory. fileLength: %d/%d. %s. %s",
                            completedLength, contentLength, request.getThreadName(), request.getKey());
                }
                return new DownloadResult(diskCacheEntry, ImageFrom.NETWORK);
            } else {
                String message = String.format("Not found disk cache after download success. %s. %s", request.getThreadName(), request.getKey());
                SLog.e(NAME, message);
                throw new DownloadException(message, ErrorCause.DOWNLOAD_NOT_FOUND_DISK_CACHE_AFTER_SUCCESS);
            }
        }
    }

    /**
     * 读取数据并回调下载进度
     *
     * @param request       {@link DownloadRequest}
     * @param inputStream   {@link InputStream}
     * @param outputStream  {@link OutputStream}
     * @param contentLength 数据长度
     * @return 已读取数据长度
     * @throws IOException       IO 异常
     * @throws CanceledException 已取消
     */
    private int readData(@NonNull DownloadRequest request, @NonNull InputStream inputStream,
                         @NonNull OutputStream outputStream, int contentLength) throws IOException, CanceledException {
        int realReadCount;
        int completedLength = 0;
        long lastCallbackTime = 0;
        byte[] buffer = new byte[8 * 1024];
        while (true) {
            if (request.isCanceled()) {
                if (SLog.isLoggable(SLog.LEVEL_DEBUG | SLog.TYPE_FLOW)) {
                    boolean readFully = contentLength <= 0 || completedLength == contentLength;
                    String readStatus = readFully ? "read fully" : "not read fully";
                    SLog.d(NAME, "Download canceled in read data. %s. %s. %s", readStatus, request.getThreadName(), request.getKey());
                }
                throw new CanceledException();
            }

            realReadCount = inputStream.read(buffer);
            if (realReadCount != -1) {
                outputStream.write(buffer, 0, realReadCount);
                completedLength += realReadCount;

                // Update progress every 100 milliseconds
                long currentTime = System.currentTimeMillis();
                if (currentTime - lastCallbackTime >= 100) {
                    lastCallbackTime = currentTime;
                    request.updateProgress(contentLength, completedLength);
                }
            } else {
                // The end of the time to call back the progress of the time to ensure that the page can display 100%
                request.updateProgress(contentLength, completedLength);
                break;
            }
        }
        outputStream.flush();
        return completedLength;
    }

    @NonNull
    @Override
    public String toString() {
        return NAME;
    }
}
