/*
 * Copyright (C) 2019 panpf <panpfpanpf@outlook.com>
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
package com.github.panpf.sketch.request

import com.github.panpf.sketch.SLog
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.cache.BitmapPoolUtils
import com.github.panpf.sketch.datasource.DataSource
import com.github.panpf.sketch.decode.BitmapDecodeResult
import com.github.panpf.sketch.decode.DecodeException
import com.github.panpf.sketch.decode.DecodeResult
import com.github.panpf.sketch.decode.GifDecodeResult
import com.github.panpf.sketch.uri.GetDataSourceException
import com.github.panpf.sketch.uri.UriModel
import com.github.panpf.sketch.util.SketchUtils

open class LoadRequest @JvmOverloads constructor(
    sketch: Sketch,
    uri: String,
    uriModel: UriModel,
    key: String,
    loadOptions: LoadOptions,
    private val loadListener: LoadListener?,
    downloadProgressListener: DownloadProgressListener?,
    logModule: String = "LoadRequest"
) : DownloadRequest(
    sketch,
    uri,
    uriModel,
    key,
    loadOptions,
    null,
    downloadProgressListener,
    logModule
) {
    private var downloadResult: DownloadResult? = null
    private var loadResult: LoadResult? = null
    override val options: LoadOptions
        get() = super.options as LoadOptions
    val transformCacheKey: String
        get() = key

    @Throws(GetDataSourceException::class)
    fun getDataSource(disableTransformCache: Boolean): DataSource {
        if (!disableTransformCache) {
            val transformCacheManager = configuration.transformCacheManager
            if (transformCacheManager.canUse(options)) {
                val dataSource: DataSource? = transformCacheManager.getDiskCache(this)
                if (dataSource != null) {
                    return dataSource
                }
            }
        }
        val downloadResult = if (uriModel.isFromNet) downloadResult else null
        return uriModel.getDataSource(context, uri, downloadResult)
    }

    override fun doError(errorCause: ErrorCause) {
        super.doError(errorCause)
        if (loadListener != null) {
            postToMainRunError()
        }
    }

    override fun doCancel(cancelCause: CancelCause) {
        super.doCancel(cancelCause)
        if (loadListener != null) {
            postToMainRunCanceled()
        }
    }

    override fun runDispatch(): DispatchResult? {
        if (isCanceled) {
            if (SLog.isLoggable(SLog.DEBUG)) SLog.dmf(
                logName,
                "Request end before dispatch. %s. %s",
                threadName,
                key
            )
            return null
        }
        setStatus(Status.INTERCEPT_LOCAL_TASK)
        val transformCacheManager = configuration.transformCacheManager
        return if (!uriModel.isFromNet) {
            if (SLog.isLoggable(SLog.DEBUG)) SLog.dmf(
                logName,
                "Dispatch. Local image. %s. %s",
                threadName,
                key
            )
            RunLoadResult()
        } else if (transformCacheManager.canUse(options) && transformCacheManager.checkDiskCache(
                this
            )
        ) {
            // 网络图片但是本地已经有缓存好的且经过处理的缓存图片可以直接用
            if (SLog.isLoggable(SLog.DEBUG)) SLog.dmf(
                logName,
                "Dispatch. Processed disk cache. %s. %s",
                threadName,
                key
            )
            RunLoadResult()
        } else {
            super.runDispatch()
        }
    }

    override fun onRunDownloadFinished(result: DownloadResult?) {
        downloadResult = result
        if (result != null) {
            submitLoad()
        }
    }

    override fun runLoad(): LoadResult? {
        if (isCanceled) {
            if (SLog.isLoggable(SLog.DEBUG)) {
                SLog.dmf(logName, "Request end before decode. %s. %s", threadName, key)
            }
            return null
        }
        setStatus(Status.DECODING)
        val decodeResult: DecodeResult = try {
            configuration.decoder.decode(this)
        } catch (e: DecodeException) {
            e.printStackTrace()
            doError(e.errorCause)
            return null
        }
        return when (decodeResult) {
            is BitmapDecodeResult -> {
                val bitmap = decodeResult.bitmap
                if (bitmap.isRecycled) {
                    val imageAttrs = decodeResult.imageAttrs
                    val imageInfo = SketchUtils.makeImageInfo(
                        null,
                        imageAttrs.width,
                        imageAttrs.height,
                        imageAttrs.mimeType,
                        imageAttrs.exifOrientation,
                        bitmap,
                        SketchUtils.getByteCount(bitmap).toLong(),
                        null
                    )
                    SLog.emf(
                        logName,
                        "Decode failed because bitmap recycled. bitmapInfo: %s. %s. %s",
                        imageInfo,
                        threadName,
                        key
                    )
                    doError(ErrorCause.BITMAP_RECYCLED)
                    return null
                }
                if (SLog.isLoggable(SLog.DEBUG)) {
                    val imageAttrs = decodeResult.imageAttrs
                    val imageInfo = SketchUtils.makeImageInfo(
                        null,
                        imageAttrs.width,
                        imageAttrs.height,
                        imageAttrs.mimeType,
                        imageAttrs.exifOrientation,
                        bitmap,
                        SketchUtils.getByteCount(bitmap).toLong(),
                        null
                    )
                    SLog.dmf(
                        logName,
                        "Decode success. bitmapInfo: %s. %s. %s",
                        imageInfo,
                        threadName,
                        key
                    )
                }
                if (isCanceled) {
                    BitmapPoolUtils.freeBitmapToPool(bitmap, configuration.bitmapPool)
                    if (SLog.isLoggable(SLog.DEBUG)) {
                        SLog.dmf(logName, "Request end after decode. %s. %s", threadName, key)
                    }
                    return null
                }
                BitmapLoadResult(bitmap, decodeResult.imageAttrs, decodeResult.imageFrom)
            }
            is GifDecodeResult -> {
                val gifDrawable = decodeResult.gifDrawable
                if (gifDrawable.isRecycled()) {
                    SLog.emf(
                        logName, "Decode failed because gif drawable recycled. gifInfo: %s. %s. %s",
                        gifDrawable.info!!, threadName, key
                    )
                    doError(ErrorCause.GIF_DRAWABLE_RECYCLED)
                    return null
                }
                if (SLog.isLoggable(SLog.DEBUG)) {
                    SLog.dmf(
                        logName,
                        "Decode gif success. gifInfo: %s. %s. %s",
                        gifDrawable.info!!,
                        threadName,
                        key
                    )
                }
                if (isCanceled) {
                    gifDrawable.recycle()
                    if (SLog.isLoggable(SLog.DEBUG)) {
                        SLog.dmf(logName, "Request end after decode. %s. %s", threadName, key)
                    }
                    return null
                }
                GifLoadResult(gifDrawable, decodeResult.imageAttrs, decodeResult.imageFrom)
            }
            else -> {
                SLog.emf(
                    logName,
                    "Unknown DecodeResult type. %S. %s. %s",
                    decodeResult.javaClass.name,
                    threadName,
                    key
                )
                doError(ErrorCause.DECODE_UNKNOWN_RESULT_TYPE)
                null
            }
        }
    }

    override fun onRunLoadFinished(result: LoadResult?) {
        loadResult = result
        if (result != null) {
            postRunCompleted()
        }
    }

    override fun runCompletedInMain() {
        if (isCanceled) {
            if (loadResult is BitmapLoadResult) {
                BitmapPoolUtils.freeBitmapToPool(
                    (loadResult as BitmapLoadResult).bitmap,
                    configuration.bitmapPool
                )
            } else if (loadResult is GifLoadResult) {
                (loadResult as GifLoadResult).gifDrawable.recycle()
            }
            if (SLog.isLoggable(SLog.DEBUG)) {
                SLog.dmf(logName, "Request end before call completed. %s. %s", threadName, key)
            }
            return
        }
        setStatus(Status.COMPLETED)
        if (loadListener != null && loadResult != null) {
            loadListener.onCompleted(loadResult!!)
        }
    }

    override fun runErrorInMain() {
        if (isCanceled) {
            if (SLog.isLoggable(SLog.DEBUG)) {
                SLog.dmf(logName, "Request end before call err. %s. %s", threadName, key)
            }
            return
        }
        if (loadListener != null && errorCause != null) {
            loadListener.onError(errorCause!!)
        }
    }

    override fun runCanceledInMain() {
        if (loadListener != null && cancelCause != null) {
            loadListener.onCanceled(cancelCause!!)
        }
    }
}