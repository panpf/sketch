package com.github.panpf.sketch.request.internal

import androidx.annotation.MainThread
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.decode.MaxSize
import com.github.panpf.sketch.decode.Resize
import com.github.panpf.sketch.request.DisplayRequest
import com.github.panpf.sketch.request.DisplayResult
import com.github.panpf.sketch.target.ViewTarget
import com.github.panpf.sketch.util.SketchException
import com.github.panpf.sketch.util.asOrNull
import com.github.panpf.sketch.util.fixedHeight
import com.github.panpf.sketch.util.fixedWidth
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.job
import kotlinx.coroutines.withContext
import kotlin.coroutines.coroutineContext

class DisplayExecutor(private val sketch: Sketch) {

    companion object {
        const val MODULE = "DisplayExecutor"
    }

    @MainThread
    suspend fun execute(request: DisplayRequest): DisplayResult {
        // Wrap the request to manage its lifecycle.
        val requestDelegate = requestDelegate(sketch, request, coroutineContext.job)
        requestDelegate.assertActive()
        val target = request.target
        val listenerDelegate = request.listener?.run {
            ListenerDelegate(this)
        }

        try {
            if (request.uriString.isEmpty() || request.uriString.isBlank()) {
                throw UriEmptyException(request)
            }

            // todo onLayout restart
            val fixedSizeRequest = withContext(Dispatchers.Main) {
                convertFixedSize(request)
            }

            // Set up the request's lifecycle observers.
            requestDelegate.start()

            // todo readMemoryCache
//            readMemoryCache()

            sketch.logger.d(MODULE) {
                "Request started. ${request.key}"
            }
            listenerDelegate?.onStart(request)
            val loadingDrawable =
                request.placeholderImage?.getDrawable(sketch.appContext, sketch, request, null)
            withContext(Dispatchers.Main) {
                target?.onStart(loadingDrawable)
            }

            val displayData = withContext(sketch.decodeTaskDispatcher) {
                DisplayInterceptorChain(
                    initialRequest = fixedSizeRequest,
                    interceptors = sketch.displayInterceptors,
                    index = 0,
                    sketch = sketch,
                    request = fixedSizeRequest,
                ).proceed(fixedSizeRequest)
            }

            withContext(Dispatchers.Main) {
                target?.onSuccess(displayData.drawable)
            }
            val successResult = DisplayResult.Success(request, displayData)
            listenerDelegate?.onSuccess(request, successResult)
            sketch.logger.d(MODULE) {
                "Request Successful. ${request.key}"
            }
            return successResult
        } catch (throwable: Throwable) {
            if (throwable is CancellationException) {
                sketch.logger.d(MODULE) {
                    "Request canceled. ${request.key}"
                }
                listenerDelegate?.onCancel(request)
                throw throwable
            } else {
                throwable.printStackTrace()
                sketch.logger.e(
                    MODULE,
                    throwable,
                    "Request error. ${throwable.message}. ${request.key}"
                )
                val exception = throwable.asOrNull<SketchException>()
                    ?: SketchException(request, null, throwable)
                val errorDrawable =
                    request.errorImage?.getDrawable(sketch.appContext, sketch, request, exception)
                        ?: request.placeholderImage?.getDrawable(
                            sketch.appContext,
                            sketch,
                            request,
                            null
                        )
                val errorResult = DisplayResult.Error(request, exception, errorDrawable)
                withContext(Dispatchers.Main) {
                    target?.onError(errorDrawable)
                }
                listenerDelegate?.onError(request, errorResult)
                return errorResult
            }
        } finally {
            requestDelegate.complete()
        }
    }

    private fun convertFixedSize(request: DisplayRequest): DisplayRequest {
        // todo 有 byFixedSize 时可延迟到 layout 时再发起请求，这样可以解决有时 imageview 的大小受限于父 view 的动态分配
        val view = request.target.asOrNull<ViewTarget<*>>()?.view
        val createErrorMessage: (widthOrHeight: String, funName: String) -> String =
            { widthOrHeight, funName ->
                "View's $widthOrHeight are not fixed, can not be applied with the $funName function"
            }

        val maxSize = request.maxSize
        val fixedSizeFlag = DisplayRequest.VIEW_FIXED_SIZE
        val newMaxSize = if (maxSize != null) {
            if (maxSize.width == fixedSizeFlag || maxSize.height == fixedSizeFlag) {
                require(view != null) {
                    val message =
                        "target cannot be null and must be ViewTarget because you are using maxsizeByViewFixedSize()"
                    throw FixedSizeException(request, message)
                }
                MaxSize(
                    maxSize.width.takeIf { it != fixedSizeFlag }
                        ?: view.fixedWidth()
                        ?: throw FixedSizeException(
                            request, createErrorMessage("width", "maxsizeByViewFixedSize()")
                        ),
                    maxSize.height.takeIf { it != fixedSizeFlag }
                        ?: view.fixedHeight()
                        ?: throw FixedSizeException(
                            request, createErrorMessage("height", "maxsizeByViewFixedSize()")
                        )
                )
            } else {
                maxSize
            }
        } else {
            val displayMetrics = sketch.appContext.resources.displayMetrics
            MaxSize(
                view?.fixedWidth() ?: displayMetrics.widthPixels,
                view?.fixedHeight() ?: displayMetrics.heightPixels
            )
        }

        val resize = request.resize
        val newResize =
            if (resize != null && (resize.width == fixedSizeFlag || resize.height == fixedSizeFlag)) {
                require(view != null) {
                    val message =
                        "target cannot be null and must be ViewTarget because you are using resizeByViewFixedSize()"
                    throw FixedSizeException(request, message)
                }
                Resize(
                    width = resize.width.takeIf { it != fixedSizeFlag }
                        ?: view.fixedWidth()
                        ?: throw FixedSizeException(
                            request, createErrorMessage("width", "resizeByViewFixedSize()")
                        ),
                    height = resize.height.takeIf { it != fixedSizeFlag }
                        ?: view.fixedHeight()
                        ?: throw FixedSizeException(
                            request, createErrorMessage("height", "resizeByViewFixedSize()")
                        ),
                    scope = resize.scope,
                    scale = resize.scale,
                    precision = resize.precision,
                )
            } else {
                resize
            }

        return if (newMaxSize != maxSize || newResize != resize) {
            request.newDisplayRequestBuilder() {
                if (newMaxSize != maxSize) {
                    maxSize(newMaxSize)
                }
                if (newResize != resize) {
                    resize(newResize)
                }
            }.build()
        } else {
            request
        }
    }

    private fun readMemoryCache() {

//        val memoryCache = sketch.memoryCache
//        val memoryCacheKey = newRequest.memoryCacheKey
//        val memoryCachePolicy = newRequest.memoryCachePolicy
//            todo read memory cache
//            if (memoryCachePolicy.readEnabled) {
//                val cachedCountBitmap = memoryCache[memoryCacheKey]
//                if (cachedCountBitmap != null) {
//                    if (SLog.isLoggable(SLog.DEBUG)) {
//                        SLog.dmf(
//                            DisplayEngineInterceptor.MODULE,
//                            "From memory get bitmap. bitmap=%s. %s",
//                            cachedCountBitmap.info, request.key
//                        )
//                    }
//                    cachedCountBitmap.setIsWaitingUse("${DisplayEngineInterceptor.MODULE}:waitingUse:fromMemory", true)
//                    val drawable = SketchBitmapDrawable(cachedCountBitmap, MEMORY_CACHE)
//                    return DisplayResult(drawable, cachedCountBitmap.imageInfo, MEMORY_CACHE)
//                } else if (request.depth >= RequestDepth.MEMORY) {
//                    throw DisplayException("Request depth only to MEMORY")
//                }
//            }
    }
}