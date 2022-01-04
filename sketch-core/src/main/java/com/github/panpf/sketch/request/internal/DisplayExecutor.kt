package com.github.panpf.sketch.request.internal

import android.net.Uri
import androidx.annotation.WorkerThread
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.request.DisplayException
import com.github.panpf.sketch.request.DisplayRequest
import com.github.panpf.sketch.request.DisplayResult
import com.github.panpf.sketch.request.ExecuteResult
import com.github.panpf.sketch.request.MaxSize
import com.github.panpf.sketch.request.Resize
import com.github.panpf.sketch.target.ViewTarget
import com.github.panpf.sketch.util.calculateFixedSize
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DisplayExecutor(private val sketch: Sketch) {

    companion object {
        const val MODULE = "DisplayExecutor"
    }

    @WorkerThread
    suspend fun execute(
        request: DisplayRequest,
    ): ExecuteResult<DisplayResult> {
        val listenerDelegate = request.listener?.run {
            ListenerDelegate(this)
        }
        val newRequest = withContext(Dispatchers.Main) {
            convertFixedSize(request)
        }
        val target = newRequest.target
//        val memoryCache = sketch.memoryCache
//        val memoryCacheKey = newRequest.memoryCacheKey
//        val memoryCachePolicy = newRequest.memoryCachePolicy

        try {
//            todo
//            if (memoryCachePolicy.readEnabled) {
//                val cachedRefBitmap = memoryCache[memoryCacheKey]
//                if (cachedRefBitmap != null) {
//                    if (SLog.isLoggable(SLog.DEBUG)) {
//                        SLog.dmf(
//                            DisplayEngineInterceptor.MODULE,
//                            "From memory get bitmap. bitmap=%s. %s",
//                            cachedRefBitmap.info, request.key
//                        )
//                    }
//                    cachedRefBitmap.setIsWaitingUse("${DisplayEngineInterceptor.MODULE}:waitingUse:fromMemory", true)
//                    val drawable = SketchBitmapDrawable(cachedRefBitmap, MEMORY_CACHE)
//                    return DisplayResult(drawable, cachedRefBitmap.imageInfo, MEMORY_CACHE)
//                } else if (request.depth >= RequestDepth.MEMORY) {
//                    throw DisplayException("Request depth only to MEMORY")
//                }
//            }
            sketch.logger.d(MODULE) {
                "Request started. ${request.uriString}"
            }
            listenerDelegate?.onStart(newRequest)
            val loadingDrawable =
                newRequest.loadingImage?.getDrawable(sketch.appContext, sketch, request)
            withContext(Dispatchers.Main) {
                target?.onStart(loadingDrawable)
            }

            if (request.uri !== Uri.EMPTY && request.uriString.isNotEmpty() && request.uriString.isNotBlank()) {
                val displayResult = DisplayInterceptorChain(
                    initialRequest = newRequest,
                    interceptors = sketch.displayInterceptors,
                    index = 0,
                    request = newRequest,
                ).proceed(sketch, newRequest)

                withContext(Dispatchers.Main) {
                    target?.onSuccess(displayResult.drawable)
                }
                listenerDelegate?.onSuccess(newRequest, displayResult)
                sketch.logger.d(MODULE) {
                    "Request Successful. ${request.uriString}"
                }
                return ExecuteResult.Success(displayResult)
            } else {
                val emptyDrawable = (newRequest.emptyImage ?: newRequest.errorImage)?.getDrawable(
                    sketch.appContext,
                    sketch,
                    request
                )
                withContext(Dispatchers.Main) {
                    target?.onError(emptyDrawable)
                }
                val throwable = DisplayException("Request uri is empty or blank")
                listenerDelegate?.onError(newRequest, throwable)
                sketch.logger.e(MODULE, throwable, throwable.message.orEmpty())
                return ExecuteResult.Error(throwable)
            }
        } catch (throwable: Throwable) {
            if (throwable is CancellationException) {
                sketch.logger.d(MODULE) {
                    "Request canceled. ${request.uriString}"
                }
                listenerDelegate?.onCancel(newRequest)
                throw throwable
            } else {
                throwable.printStackTrace()
                sketch.logger.e(MODULE, throwable, throwable.message.orEmpty())
                val errorDrawable =
                    newRequest.errorImage?.getDrawable(sketch.appContext, sketch, request)
                withContext(Dispatchers.Main) {
                    target?.onError(errorDrawable)
                }
                listenerDelegate?.onError(newRequest, throwable)
                return ExecuteResult.Error(throwable)
            }
        }
    }

    private fun convertFixedSize(request: DisplayRequest): DisplayRequest {
        val view = (request.target as ViewTarget<*>?)?.view
        val viewFixedSizeLazy by lazy {
            if (view == null) {
                throw DisplayException("target cannot be null and must be ViewTarget because you are using *FixedSize")
            }
            view.calculateFixedSize()
        }
        val maxSize = request.maxSize
        val fixedSizeFlag = DisplayRequest.SIZE_BY_VIEW_FIXED_SIZE
        val newMaxSize = if (
            maxSize != null && (maxSize.width == fixedSizeFlag || maxSize.height == fixedSizeFlag)
        ) {
            val viewFixedSize = viewFixedSizeLazy
                ?: throw DisplayException("View's width and height are not fixed, can not be applied with the maxSizeByViewFixedSize() function")
            MaxSize(
                maxSize.width.takeIf { it != fixedSizeFlag } ?: viewFixedSize.x,
                maxSize.height.takeIf { it != fixedSizeFlag } ?: viewFixedSize.y
            )
        } else {
            null
        }

        val resize = request.resize
        val newResize =
            if (resize != null && (resize.width == fixedSizeFlag || resize.height == fixedSizeFlag)) {
                val viewFixedSize = viewFixedSizeLazy
                    ?: throw DisplayException("View's width and height are not fixed, can not be applied with the resizeByViewFixedSize() function")
                Resize(
                    width = resize.width.takeIf { it != fixedSizeFlag } ?: viewFixedSize.x,
                    height = resize.height.takeIf { it != fixedSizeFlag } ?: viewFixedSize.y,
                    mode = resize.mode,
                    scaleType = resize.scaleType,
                    minAspectRatio = resize.minAspectRatio
                )
            } else {
                null
            }

        return if (newMaxSize != null || newResize != null) {
            request.newDisplayRequestBuilder() {
                if (newMaxSize != null) {
                    maxSize(newMaxSize)
                }
                if (newResize != null) {
                    resize(newResize)
                }
            }.build()
        } else {
            request
        }
    }
}