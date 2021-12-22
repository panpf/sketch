///*
// * Copyright (C) 2019 panpf <panpfpanpf@outlook.com>
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *   http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//package com.github.panpf.sketch3.common
//
//import android.app.PendingIntent
//import com.github.panpf.sketch.request.*
//import com.github.panpf.sketch.util.SketchUtils
//import com.github.panpf.sketch3.SLog
//import com.github.panpf.sketch3.common.cache.disk.DiskCache
//import com.github.panpf.sketch3.common.http.HttpStack
//import com.github.panpf.sketch3.download.DownloadResult
//import com.github.panpf.sketch3.util.DiskLruCache
//import java.io.*
//import java.util.*
//import java.util.concurrent.locks.ReentrantLock
//
///**
// * 负责下载并缓存图片
// */
//class ImageDownloader {
//    /**
//     * 此方法是下载图片的入口，并主要负责下载缓存锁的申请和释放
//     *
//     * @param request [DownloadRequest]
//     * @return [DownloadResult]
//     * @throws CanceledException 已取消
//     * @throws DownloadException 下载失败
//     */
//    @Throws(PendingIntent.CanceledException::class, DownloadException::class)
//    fun download(request: DownloadRequest): DownloadResult {
//        val diskCache = request.configuration.diskCache
//        val diskCacheKey = request.diskCacheKey
//
//        // 使用磁盘缓存就必须要上锁
//        var diskCacheEditLock: ReentrantLock? = null
//        if (!request.options.isCacheInDiskDisabled) {
//            diskCacheEditLock = diskCache.getEditLock(diskCacheKey)
//        }
//        diskCacheEditLock?.lock()
//        return try {
//            if (request.isCanceled) {
//                if (SLog.isLoggable(SLog.DEBUG)) {
//                    SLog.dmf(
//                        NAME,
//                        "Download canceled after get disk cache edit lock. %s. %s",
//                        request.threadName,
//                        request.key
//                    )
//                }
//                throw PendingIntent.CanceledException()
//            }
//            if (diskCacheEditLock != null) {
//                request.setStatus(BaseRequest.Status.CHECK_DISK_CACHE)
//                val diskCacheEntry = diskCache[diskCacheKey]
//                if (diskCacheEntry != null) {
//                    return CacheDownloadResult(diskCacheEntry, DataFrom.DISK_CACHE)
//                }
//            }
//            loopRetryDownload(request, diskCache, diskCacheKey)
//        } finally {
//            diskCacheEditLock?.unlock()
//        }
//    }
//
//    /**
//     * 此方法负责下载的失败重试逻辑
//     *
//     * @param request      [DownloadRequest]
//     * @param diskCache    [DiskCache]. 用来写出并缓存数据
//     * @param diskCacheKey 磁盘缓存 key
//     * @return [DownloadResult]
//     * @throws CanceledException 已取消
//     * @throws DownloadException 下载失败
//     */
//    @Throws(PendingIntent.CanceledException::class, DownloadException::class)
//    private fun loopRetryDownload(
//        request: DownloadRequest,
//        diskCache: DiskCache,
//        diskCacheKey: String
//    ): DownloadResult {
//        val httpStack = request.configuration.httpStack
//        var retryCount = 0
//        val maxRetryCount = httpStack.maxRetryCount
//        var uri = request.uri
//        while (true) {
//            try {
//                return doDownload(request, uri, httpStack, diskCache, diskCacheKey)
//            } catch (e: RedirectsException) {
//                uri = e.newUrl
//            } catch (tr: Throwable) {
//                if (request.isCanceled) {
//                    val message = String.format(
//                        "Download exception, but canceled. %s. %s",
//                        request.threadName,
//                        request.key
//                    )
//                    if (SLog.isLoggable(SLog.DEBUG)) {
//                        SLog.emt(NAME, tr, message)
//                    }
//                    val exception = DownloadException(
//                        tr,
//                        request,
//                        message,
//                        ErrorCause.DOWNLOAD_EXCEPTION_AND_CANCELED
//                    )
//                    request.configuration.callback.onError(exception)
//                    throw exception
//                } else if (httpStack.canRetry(tr) && retryCount < maxRetryCount) {
//                    tr.printStackTrace()
//                    retryCount++
//                    SLog.wmt(
//                        NAME,
//                        tr,
//                        String.format(
//                            "Download exception but can retry. %s. %s",
//                            request.threadName,
//                            request.key
//                        )
//                    )
//                } else if (tr is PendingIntent.CanceledException) {
//                    throw tr
//                } else if (tr is DownloadException) {
//                    request.configuration.callback.onError(tr)
//                    throw tr
//                } else {
//                    val message =
//                        String.format("Download failed. %s. %s", request.threadName, request.key)
//                    SLog.wmt(NAME, tr, message)
//                    val exception = DownloadException(
//                        tr,
//                        request,
//                        message,
//                        ErrorCause.DOWNLOAD_UNKNOWN_EXCEPTION
//                    )
//                    request.configuration.callback.onError(exception)
//                    throw exception
//                }
//            }
//        }
//    }
//
//    /**
//     * 真正的下载核心逻辑方法，发送 http 请求并读取响应
//     *
//     * @param request      [DownloadRequest]
//     * @param uri          图片 uri
//     * @param httpStack    [HttpStack]. 用来发送 http 请求并且获取响应
//     * @param diskCache    [DiskCache]. 用来写出并缓存数据
//     * @param diskCacheKey 磁盘缓存 key
//     * @return [DownloadResult]
//     * @throws IOException        发生 IO 异常
//     * @throws CanceledException  已取消
//     * @throws DownloadException  下载失败
//     * @throws RedirectsException 图片地址重定向了
//     */
//    @Throws(
//        IOException::class,
//        PendingIntent.CanceledException::class,
//        DownloadException::class,
//        RedirectsException::class
//    )
//    private fun doDownload(
//        request: DownloadRequest,
//        uri: String,
//        httpStack: HttpStack,
//        diskCache: DiskCache,
//        diskCacheKey: String
//    ): DownloadResult {
//        // Opening http connection
//        request.setStatus(BaseRequest.Status.CONNECTING)
//        val response: HttpStack.Response = try {
//            httpStack.getResponse(uri)
//        } catch (e: IOException) {
//            throw e
//        }
//
//        // Check canceled
//        if (request.isCanceled) {
//            response.releaseConnection()
//            if (SLog.isLoggable(SLog.DEBUG)) {
//                SLog.dmf(
//                    NAME,
//                    "Download canceled after opening the connection. %s. %s",
//                    request.threadName,
//                    request.key
//                )
//            }
//            throw PendingIntent.CanceledException()
//        }
//
//        // Check response code, must be 200
//        val responseCode: Int = try {
//            response.code
//        } catch (e: IOException) {
//            response.releaseConnection()
//            val message = String.format(
//                "Get response code exception. responseHeaders: %s. %s. %s",
//                response.headersString, request.threadName, request.key
//            )
//            SLog.wmt(NAME, e, message)
//            throw DownloadException(
//                e,
//                request,
//                message,
//                ErrorCause.DOWNLOAD_GET_RESPONSE_CODE_EXCEPTION
//            )
//        }
//        if (responseCode != 200) {
//            response.releaseConnection()
//
//            // redirects
//            if (responseCode == 301 || responseCode == 302) {
//                val newUri = response.getHeaderField("Location")
//                if (newUri != null && newUri.isNotEmpty()) {
//                    // To prevent infinite redirection
//                    if (uri == request.uri) {
//                        if (SLog.isLoggable(SLog.DEBUG)) {
//                            SLog.dmf(
//                                NAME,
//                                "Uri redirects. originUri: %s, newUri: %s. %s",
//                                request.uri,
//                                newUri,
//                                request.key
//                            )
//                        }
//                        throw RedirectsException(newUri)
//                    } else {
//                        SLog.emf(
//                            NAME,
//                            "Disable unlimited redirects, originUri: %s, redirectsUri=%s, newUri=%s. %s",
//                            request.uri,
//                            uri,
//                            newUri,
//                            request.key
//                        )
//                    }
//                } else {
//                    SLog.wmf(
//                        NAME,
//                        "Uri redirects failed. newUri is empty, originUri: %s. %s",
//                        request.uri,
//                        request.key
//                    )
//                }
//            }
//            val message = String.format(
//                "Response code exception. responseHeaders: %s. %s. %s",
//                response.headersString, request.threadName, request.key
//            )
//            SLog.em(NAME, message)
//            throw DownloadException(request, message, ErrorCause.DOWNLOAD_RESPONSE_CODE_EXCEPTION)
//        }
//
//        // Get content
//        val inputStream: InputStream = try {
//            response.content
//        } catch (e: IOException) {
//            response.releaseConnection()
//            throw e
//        }
//
//        // Check canceled
//        if (request.isCanceled) {
//            SketchUtils.close(inputStream)
//            if (SLog.isLoggable(SLog.DEBUG)) {
//                SLog.dmf(
//                    NAME,
//                    "Download canceled after get content. %s. %s",
//                    request.threadName,
//                    request.key
//                )
//            }
//            throw PendingIntent.CanceledException()
//        }
//
//        // Ready OutputStream, the ByteArrayOutputStream is used when the disk cache is disabled
//        var diskCacheEditor: DiskCache.Editor? = null
//        if (!request.options.isCacheInDiskDisabled) {
//            diskCacheEditor = diskCache.edit(diskCacheKey)
//        }
//        val outputStream: OutputStream = if (diskCacheEditor != null) {
//            try {
//                BufferedOutputStream(diskCacheEditor.newOutputStream(), 8 * 1024)
//            } catch (e: IOException) {
//                SketchUtils.close(inputStream)
//                diskCacheEditor.abort()
//                val message = String.format(
//                    "Open disk cache exception. %s. %s",
//                    request.threadName,
//                    request.key
//                )
//                SLog.emt(NAME, e, message)
//                throw DownloadException(
//                    e,
//                    request,
//                    message,
//                    ErrorCause.DOWNLOAD_OPEN_DISK_CACHE_EXCEPTION
//                )
//            }
//        } else {
//            ByteArrayOutputStream()
//        }
//        val contentLength = response.contentLength
//
//        // Read data
//        request.setStatus(BaseRequest.Status.READ_DATA)
//        val completedLength: Int = try {
//            readData(request, inputStream, outputStream, contentLength.toInt())
//        } catch (e: IOException) {
//            diskCacheEditor?.abort()
//            val message =
//                String.format("Read data exception. %s. %s", request.threadName, request.key)
//            SLog.emt(NAME, e, message)
//            throw DownloadException(e, request, message, ErrorCause.DOWNLOAD_READ_DATA_EXCEPTION)
//        } catch (e: PendingIntent.CanceledException) {
//            diskCacheEditor?.abort()
//            throw e
//        } finally {
//            SketchUtils.close(outputStream)
//            SketchUtils.close(inputStream)
//        }
//
//        // Check content fully and commit the disk cache
//        if (contentLength <= 0 || completedLength.toLong() == contentLength) {
//            if (diskCacheEditor != null) {
//                try {
//                    diskCacheEditor.commit()
//                } catch (e: IOException) {
//                    val message = String.format(
//                        "Disk cache commit exception. %s. %s",
//                        request.threadName,
//                        request.key
//                    )
//                    SLog.emt(NAME, e, message)
//                    throw DownloadException(
//                        e,
//                        request,
//                        message,
//                        ErrorCause.DOWNLOAD_DISK_CACHE_COMMIT_EXCEPTION
//                    )
//                } catch (e: DiskLruCache.EditorChangedException) {
//                    val message = String.format(
//                        "Disk cache commit exception. %s. %s",
//                        request.threadName,
//                        request.key
//                    )
//                    SLog.emt(NAME, e, message)
//                    throw DownloadException(
//                        e,
//                        request,
//                        message,
//                        ErrorCause.DOWNLOAD_DISK_CACHE_COMMIT_EXCEPTION
//                    )
//                } catch (e: DiskLruCache.ClosedException) {
//                    val message = String.format(
//                        "Disk cache commit exception. %s. %s",
//                        request.threadName,
//                        request.key
//                    )
//                    SLog.emt(NAME, e, message)
//                    throw DownloadException(
//                        e,
//                        request,
//                        message,
//                        ErrorCause.DOWNLOAD_DISK_CACHE_COMMIT_EXCEPTION
//                    )
//                } catch (e: DiskLruCache.FileNotExistException) {
//                    val message = String.format(
//                        "Disk cache commit exception. %s. %s",
//                        request.threadName,
//                        request.key
//                    )
//                    SLog.emt(NAME, e, message)
//                    throw DownloadException(
//                        e,
//                        request,
//                        message,
//                        ErrorCause.DOWNLOAD_DISK_CACHE_COMMIT_EXCEPTION
//                    )
//                }
//            }
//        } else {
//            diskCacheEditor?.abort()
//            val message = String.format(
//                Locale.US,
//                "The data is not fully read. contentLength:%d, completedLength:%d. %s. %s",
//                contentLength,
//                completedLength,
//                request.threadName,
//                request.key
//            )
//            SLog.em(NAME, message)
//            throw DownloadException(request, message, ErrorCause.DOWNLOAD_DATA_NOT_FULLY_READ)
//        }
//
//        // Return DownloadResult
//        return if (diskCacheEditor == null) {
//            if (SLog.isLoggable(SLog.DEBUG)) {
//                SLog.dmf(
//                    NAME,
//                    "Download success. Data is saved to disk cache. fileLength: %d/%d. %s. %s",
//                    completedLength,
//                    contentLength,
//                    request.threadName,
//                    request.key
//                )
//            }
//            BytesDownloadResult(
//                (outputStream as ByteArrayOutputStream).toByteArray(),
//                DataFrom.NETWORK
//            )
//        } else {
//            val diskCacheEntry = diskCache[diskCacheKey]
//            if (diskCacheEntry != null) {
//                if (SLog.isLoggable(SLog.DEBUG)) {
//                    SLog.dmf(
//                        NAME,
//                        "Download success. data is saved to memory. fileLength: %d/%d. %s. %s",
//                        completedLength,
//                        contentLength,
//                        request.threadName,
//                        request.key
//                    )
//                }
//                CacheDownloadResult(diskCacheEntry, DataFrom.NETWORK)
//            } else {
//                val message = String.format(
//                    "Not found disk cache after download success. %s. %s",
//                    request.threadName,
//                    request.key
//                )
//                SLog.em(NAME, message)
//                throw DownloadException(
//                    request,
//                    message,
//                    ErrorCause.DOWNLOAD_NOT_FOUND_DISK_CACHE_AFTER_SUCCESS
//                )
//            }
//        }
//    }
//
//    /**
//     * 读取数据并回调下载进度
//     *
//     * @param request       [DownloadRequest]
//     * @param inputStream   [InputStream]
//     * @param outputStream  [OutputStream]
//     * @param contentLength 数据长度
//     * @return 已读取数据长度
//     * @throws IOException       IO 异常
//     * @throws CanceledException 已取消
//     */
//    @Throws(IOException::class, PendingIntent.CanceledException::class)
//    private fun readData(
//        request: DownloadRequest, inputStream: InputStream,
//        outputStream: OutputStream, contentLength: Int
//    ): Int {
//        var realReadCount: Int
//        var completedLength = 0
//        var lastCallbackTime: Long = 0
//        val buffer = ByteArray(8 * 1024)
//        while (true) {
//            if (request.isCanceled) {
//                if (SLog.isLoggable(SLog.DEBUG)) {
//                    val readFully = contentLength <= 0 || completedLength == contentLength
//                    val readStatus = if (readFully) "read fully" else "not read fully"
//                    SLog.dmf(
//                        NAME,
//                        "Download canceled in read data. %s. %s. %s",
//                        readStatus,
//                        request.threadName,
//                        request.key
//                    )
//                }
//                throw PendingIntent.CanceledException()
//            }
//            realReadCount = inputStream.read(buffer)
//            if (realReadCount != -1) {
//                outputStream.write(buffer, 0, realReadCount)
//                completedLength += realReadCount
//
//                // Update progress every 100 milliseconds
//                val currentTime = System.currentTimeMillis()
//                if (currentTime - lastCallbackTime >= 100) {
//                    lastCallbackTime = currentTime
//                    request.updateProgress(contentLength, completedLength)
//                }
//            } else {
//                // The end of the time to call back the progress of the time to ensure that the page can display 100%
//                request.updateProgress(contentLength, completedLength)
//                break
//            }
//        }
//        outputStream.flush()
//        return completedLength
//    }
//
//    override fun toString(): String {
//        return NAME
//    }
//
//    companion object {
//        private const val NAME = "ImageDownloader"
//    }
//}