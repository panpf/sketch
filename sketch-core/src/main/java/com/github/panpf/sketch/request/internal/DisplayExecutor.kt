package com.github.panpf.sketch.request.internal

import android.net.Uri
import androidx.annotation.MainThread
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.request.DisplayRequest
import com.github.panpf.sketch.request.DisplayResult
import com.github.panpf.sketch.request.MaxSize
import com.github.panpf.sketch.request.Resize
import com.github.panpf.sketch.target.ViewTarget
import com.github.panpf.sketch.util.calculateFixedSize
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
            if (request.uri === Uri.EMPTY || request.uriString.isEmpty() || request.uriString.isBlank()) {
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
                "Request started. ${request.uriString}"
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
                    request = fixedSizeRequest,
                ).proceed(sketch, fixedSizeRequest)
            }

            withContext(Dispatchers.Main) {
                target?.onSuccess(displayData.drawable)
            }
            val successResult = DisplayResult.Success(
                request,
                displayData.drawable,
                displayData.info,
                displayData.from
            )
            listenerDelegate?.onSuccess(request, successResult)
            sketch.logger.d(MODULE) {
                "Request Successful. ${request.uriString}"
            }
            return successResult
        } catch (throwable: Throwable) {
            if (throwable is CancellationException) {
                sketch.logger.d(MODULE) {
                    "Request canceled. ${request.uriString}"
                }
                listenerDelegate?.onCancel(request)
                throw throwable
            } else {
                throwable.printStackTrace()
                sketch.logger.e(MODULE, throwable, throwable.message.orEmpty())
                val errorDrawable =
                    request.errorImage?.getDrawable(sketch.appContext, sketch, request, throwable)
                val errorResult = DisplayResult.Error(request, throwable, errorDrawable)
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
        val view = (request.target as ViewTarget<*>?)?.view
        val viewFixedSizeLazy by lazy {
            if (view == null) {
                throw FixedSizeException(
                    request,
                    "target cannot be null and must be ViewTarget because you are using *FixedSize"
                )
            }
            view.calculateFixedSize()
        }
        val maxSize = request.maxSize
        val fixedSizeFlag = DisplayRequest.SIZE_BY_VIEW_FIXED_SIZE
        val newMaxSize = if (
            maxSize != null && (maxSize.width == fixedSizeFlag || maxSize.height == fixedSizeFlag)
        ) {
            val viewFixedSize = viewFixedSizeLazy
                ?: throw FixedSizeException(
                    request,
                    "View's width and height are not fixed, can not be applied with the maxSizeByViewFixedSize() function"
                )
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
                    ?: throw FixedSizeException(
                        request,
                        "View's width and height are not fixed, can not be applied with the resizeByViewFixedSize() function"
                    )
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

    private fun readMemoryCache() {

//        val memoryCache = sketch.memoryCache
//        val memoryCacheKey = newRequest.memoryCacheKey
//        val memoryCachePolicy = newRequest.memoryCachePolicy
//            todo read memory cache
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
    }
}