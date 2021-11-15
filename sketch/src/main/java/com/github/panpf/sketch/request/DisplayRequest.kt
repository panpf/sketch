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

import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import com.github.panpf.sketch.SLog
import com.github.panpf.sketch.SLog.Companion.dmf
import com.github.panpf.sketch.SLog.Companion.emf
import com.github.panpf.sketch.SLog.Companion.isLoggable
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.drawable.*
import com.github.panpf.sketch.uri.UriModel

class DisplayRequest(
    sketch: Sketch,
    uri: String,
    uriModel: UriModel,
    key: String,
    displayOptions: DisplayOptions,
    val isUseSmallerThumbnails: Boolean,
    private val requestAndViewBinder: RequestAndViewBinder,
    private val displayListener: DisplayListener?,
    downloadProgressListener: DownloadProgressListener?
) : LoadRequest(
    sketch,
    uri,
    uriModel,
    key,
    displayOptions,
    null,
    downloadProgressListener,
    "DisplayRequest"
) {
    private var displayResult: DisplayResult? = null
    var waitingDisplayShareRequests: MutableList<DisplayRequest>? = null
    override val options: DisplayOptions
        get() = super.options as DisplayOptions
    val memoryCacheKey: String
        get() = key
    override val isCanceled: Boolean
        get() {
            if (super.isCanceled) {
                return true
            }
            if (requestAndViewBinder.isBroken) {
                if (isLoggable(SLog.DEBUG)) {
                    dmf(
                        logName,
                        "The request and the connection to the view are interrupted. %s. %s",
                        threadName,
                        key
                    )
                }
                doCancel(CancelCause.BIND_DISCONNECT)
                return true
            }
            return false
        }

    override fun doError(errorCause: ErrorCause) {
        if (displayListener != null || options.errorImage != null) {
            setErrorCause(errorCause)
            postToMainRunError()
        } else {
            super.doError(errorCause)
        }
    }

    override fun doCancel(cancelCause: CancelCause) {
        super.doCancel(cancelCause)
        if (displayListener != null) {
            postToMainRunCanceled()
        }
    }

    override fun postToMainRunError() {
        setStatus(Status.WAIT_DISPLAY)
        super.postToMainRunError()
    }

    override fun postRunCompleted() {
        setStatus(Status.WAIT_DISPLAY)
        super.postRunCompleted()
    }

    override fun runLoad(): LoadResult? {
        if (isCanceled) {
            if (isLoggable(SLog.DEBUG)) {
                dmf(logName, "Request end before decode. %s. %s", threadName, key)
            }
            return null
        }

        // Check memory cache
        val displayOptions = options
        if (!displayOptions.isCacheInDiskDisabled) {
            setStatus(Status.CHECK_MEMORY_CACHE)
            val memoryCache = configuration.memoryCache
            val cachedRefBitmap = memoryCache[memoryCacheKey]
            if (cachedRefBitmap != null) {
                // 当 isDecodeGifImage 为 true 时是要播放 gif 的，而内存缓存里的 gif 图都是第一帧静态图片，所以不能用
                if (!(options.isDecodeGifImage && "image/gif".equals(
                        cachedRefBitmap.attrs.mimeType,
                        ignoreCase = true
                    ))
                ) {
                    if (!cachedRefBitmap.isRecycled) {
                        if (isLoggable(SLog.DEBUG)) {
                            dmf(
                                logName, "From memory get drawable. bitmap=%s. %s. %s",
                                cachedRefBitmap.info, threadName, key
                            )
                        }
                        cachedRefBitmap.setIsWaitingUse(
                            String.format(
                                "%s:waitingUse:fromMemory",
                                logName
                            ), true
                        ) // 立马标记等待使用，防止被回收
                        return CacheBitmapLoadResult(
                            cachedRefBitmap,
                            cachedRefBitmap.attrs,
                            ImageFrom.MEMORY_CACHE
                        )
                    } else {
                        memoryCache.remove(memoryCacheKey)
                        emf(
                            logName,
                            "Memory cache drawable recycled. bitmap=%s. %s. %s",
                            cachedRefBitmap.info,
                            threadName,
                            key
                        )
                    }
                }
            }
        }
        return super.runLoad()
    }

    override fun onRunLoadFinished(result: LoadResult?) {
        when (result) {
            is BitmapLoadResult -> {
                val bitmapPool = configuration.bitmapPool
                val bitmap = result.bitmap
                val refBitmap = SketchRefBitmap(bitmap, key, uri, result.imageAttrs, bitmapPool)
                refBitmap.setIsWaitingUse(
                    String.format("%s:waitingUse:new", logName),
                    true
                ) // 立马标记等待使用，防止刚放入内存缓存就被挤出去回收掉
                val displayOptions = options
                if (!displayOptions.isCacheInMemoryDisabled) {
                    configuration.memoryCache.put(memoryCacheKey, refBitmap)
                }
                val drawable: Drawable = SketchBitmapDrawable(refBitmap, result.imageFrom)
                onDisplayFinished(DisplayResult(drawable, result.imageFrom, result.imageAttrs))
            }
            is GifLoadResult -> {
                // GifDrawable 不能放入内存缓存中，因为GifDrawable需要依赖Callback才能播放，
                // 如果缓存的话就会出现一个GifDrawable被显示在多个ImageView上的情况，这时候就只有最后一个能正常播放
                val gifDrawable = result.gifDrawable
                onDisplayFinished(
                    DisplayResult(
                        (gifDrawable as Drawable),
                        result.imageFrom,
                        result.imageAttrs
                    )
                )
            }
            is CacheBitmapLoadResult -> {
                val drawable: Drawable = SketchBitmapDrawable(result.refBitmap, result.imageFrom)
                onDisplayFinished(DisplayResult(drawable, result.imageFrom, result.imageAttrs))
            }
            else -> {
                emf(logName, "Not found data after load completed. %s. %s", threadName, key)
                doError(ErrorCause.DATA_LOST_AFTER_LOAD_COMPLETED)
                onDisplayFinished(null)
            }
        }
    }

    fun onDisplayFinished(result: DisplayResult?) {
        displayResult = result
        if (result != null) {
            postRunCompleted()
        }
    }

    override fun runCompletedInMain() {
        val drawable = if (displayResult != null) displayResult!!.drawable else null
        if (drawable == null) {
            if (isLoggable(SLog.DEBUG)) {
                dmf(logName, "Drawable is null before call completed. %s. %s", threadName, key)
            }
            return
        }
        displayImage(displayResult!!, drawable)

        // 使用完毕更新等待使用的引用计数
        if (drawable is SketchRefDrawable) {
            (drawable as SketchRefDrawable).setIsWaitingUse(
                String.format(
                    "%s:waitingUse:finish",
                    logName
                ), false
            )
        }
    }

    private fun displayImage(displayResult: DisplayResult, drawable: Drawable) {
        var newDrawable = drawable
        val sketchView = requestAndViewBinder.view
        if (isCanceled || sketchView == null) {
            if (isLoggable(SLog.DEBUG)) {
                dmf(logName, "Request end before call completed. %s. %s", threadName, key)
            }
            return
        }

        // 过滤可能已回收的图片
        if (newDrawable is BitmapDrawable) {
            if (newDrawable.bitmap.isRecycled) {
                // 这里应该不会再出问题了
                emf(
                    logName, "Bitmap recycled on display. imageUri=%s, drawable=%s",
                    uri, (newDrawable as SketchDrawable).info!!
                )
                configuration.callback.onError(
                    BitmapRecycledOnDisplayException(
                        this,
                        (newDrawable as SketchDrawable)
                    )
                )

                // 图片不可用
                if (isLoggable(SLog.DEBUG)) {
                    dmf(
                        logName,
                        "Display image exception. bitmap recycled. %s. %s. %s. %s",
                        (newDrawable as SketchDrawable).info!!,
                        displayResult.imageFrom,
                        threadName,
                        key
                    )
                }
                runErrorInMain()
                return
            }
        }

        // 显示图片
        val displayOptions = options
        if ((displayOptions.shapeSize != null || displayOptions.shaper != null) && newDrawable is BitmapDrawable) {
            newDrawable = SketchShapeBitmapDrawable(
                configuration.context, newDrawable,
                displayOptions.shapeSize, displayOptions.shaper
            )
        }
        if (isLoggable(SLog.DEBUG)) {
            var drawableInfo: String? = "unknown"
            if (newDrawable is SketchRefDrawable) {
                drawableInfo = (newDrawable as SketchRefDrawable).info
            }
            dmf(
                logName,
                "Display image completed. %s. %s. view(%s). %s. %s",
                displayResult.imageFrom.name,
                drawableInfo!!,
                Integer.toHexString(sketchView.hashCode()),
                threadName,
                key
            )
        }

        // 一定要在 ImageDisplayer().display 之前执行
        setStatus(Status.COMPLETED)
        var imageDisplayer = displayOptions.displayer
        if (imageDisplayer == null) {
            imageDisplayer = configuration.defaultDisplayer
        }
        imageDisplayer.display(sketchView, newDrawable)
        displayListener?.onCompleted(
            displayResult.drawable,
            displayResult.imageFrom,
            displayResult.imageAttrs
        )
    }

    override fun runErrorInMain() {
        val sketchView = requestAndViewBinder.view
        if (isCanceled || sketchView == null) {
            if (isLoggable(SLog.DEBUG)) {
                dmf(logName, "Request end before call error. %s. %s", threadName, key)
            }
            return
        }
        setStatus(Status.FAILED)
        val displayOptions = options
        val displayer = displayOptions.displayer
        val errorImage = displayOptions.errorImage
        if (displayer != null && errorImage != null) {
            val errorDrawable = errorImage.getDrawable(context, sketchView, displayOptions)
            if (errorDrawable != null) {
                displayer.display(sketchView, errorDrawable)
            }
        }
        if (displayListener != null && errorCause != null) {
            displayListener.onError(errorCause!!)
        }
    }

    override fun runCanceledInMain() {
        if (displayListener != null && cancelCause != null) {
            displayListener.onCanceled(cancelCause!!)
        }
    }

    /* ************************************** Display Share ************************************ */
    fun canUseDisplayShare(): Boolean {
        val memoryCache = configuration.memoryCache
        return (!memoryCache.isClosed && !memoryCache.isDisabled
                && !options.isCacheInMemoryDisabled
                && !options.isDecodeGifImage
                && !isSync && !configuration.executor.isShutdown)
    }

    init {
        requestAndViewBinder.setDisplayRequest(this)
    }
}