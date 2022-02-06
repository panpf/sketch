package com.github.panpf.sketch.request.internal

import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.ViewTreeObserver.OnPreDrawListener
import com.github.panpf.sketch.decode.MaxSize
import com.github.panpf.sketch.decode.Resize
import com.github.panpf.sketch.request.DisplayData
import com.github.panpf.sketch.request.DisplayRequest
import com.github.panpf.sketch.request.RequestInterceptor
import com.github.panpf.sketch.request.RequestInterceptor.Chain
import com.github.panpf.sketch.target.ViewTarget
import com.github.panpf.sketch.util.Size
import com.github.panpf.sketch.util.asOrNull
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume

class SizeResolverInterceptor : RequestInterceptor<DisplayRequest, DisplayData> {

    override suspend fun intercept(chain: Chain<DisplayRequest, DisplayData>): DisplayData {
        val request = chain.request
        val maxSize = request.maxSize
        val resize = request.resize
        val newRequest =
            if (maxSize?.isNeedConversion() != false || resize?.isNeedConversion() == true) {
                withContext(Dispatchers.Main) {
                    val view = request.target.asOrNull<ViewTarget<*>>()?.view
                        ?: throw IllegalArgumentException("Because you are using ViewBounds, target must be ViewTarget")
                    val size = size(view)
                    request.newDisplayRequest {
                        if (maxSize != null) {
                            maxSize(maxSize.conversion(size))
                        } else {
                            maxSize(MaxSize(size.width, size.height))
                        }
                        resize(resize?.conversion(size))
                    }
                }
            } else {
                request
            }
        return chain.proceed(newRequest)
    }

    private suspend fun size(view: View): Size {
        // Fast path: the view is already measured.
        getSize(view)?.let { return it }

        // Slow path: wait for the view to be measured.
        return suspendCancellableCoroutine { continuation ->
            val viewTreeObserver = view.viewTreeObserver

            val preDrawListener = object : OnPreDrawListener {
                private var isResumed = false

                override fun onPreDraw(): Boolean {
                    val size = getSize(view)
                    if (size != null) {
                        viewTreeObserver.removePreDrawListenerSafe(view, this)

                        if (!isResumed) {
                            isResumed = true
                            continuation.resume(size)
                        }
                    }
                    return true
                }
            }

            viewTreeObserver.addOnPreDrawListener(preDrawListener)

            continuation.invokeOnCancellation {
                viewTreeObserver.removePreDrawListenerSafe(view, preDrawListener)
            }
        }
    }

    private fun getSize(view: View): Size? {
        val width = getWidth(view) ?: return null
        val height = getHeight(view) ?: return null
        return Size(width, height)
    }

    private fun getWidth(view: View) = getDimension(
        paramSize = view.layoutParams?.width ?: -1,
        viewSize = view.width,
        paddingSize = view.paddingLeft + view.paddingRight
    )

    private fun getHeight(view: View) = getDimension(
        paramSize = view.layoutParams?.height ?: -1,
        viewSize = view.height,
        paddingSize = view.paddingTop + view.paddingBottom
    )

    private fun getDimension(paramSize: Int, viewSize: Int, paddingSize: Int): Int? {
        // If the dimension is set to WRAP_CONTENT, use the original dimension of the image.
        if (paramSize == ViewGroup.LayoutParams.WRAP_CONTENT) {
            return null
        }

        // Assume the dimension will match the value in the view's layout params.
        val insetParamSize = paramSize - paddingSize
        if (insetParamSize > 0) {
            return insetParamSize
        }

        // Fallback to the view's current dimension.
        val insetViewSize = viewSize - paddingSize
        if (insetViewSize > 0) {
            return insetViewSize
        }

        // Unable to resolve the dimension's value.
        return null
    }

    private fun ViewTreeObserver.removePreDrawListenerSafe(view: View, victim: OnPreDrawListener) {
        if (isAlive) {
            removeOnPreDrawListener(victim)
        } else {
            view.viewTreeObserver.removeOnPreDrawListener(victim)
        }
    }

    private fun MaxSize.isNeedConversion(): Boolean =
        width == DisplayRequest.VIEW_BOUNDS || height == DisplayRequest.VIEW_BOUNDS

    private fun MaxSize.conversion(size: Size): MaxSize {
        return if (isNeedConversion()) {
            if (width == DisplayRequest.VIEW_BOUNDS && height == DisplayRequest.VIEW_BOUNDS) {
                MaxSize(size.width, size.height)
            } else if (width == DisplayRequest.VIEW_BOUNDS) {
                MaxSize(size.width, height)
            } else {
                MaxSize(width, size.height)
            }
        } else {
            this
        }
    }

    private fun Resize.isNeedConversion(): Boolean =
        width == DisplayRequest.VIEW_BOUNDS || height == DisplayRequest.VIEW_BOUNDS

    private fun Resize.conversion(size: Size): Resize {
        return if (isNeedConversion()) {
            if (width == DisplayRequest.VIEW_BOUNDS && height == DisplayRequest.VIEW_BOUNDS) {
                Resize(size.width, size.height, this.scope, this.scale, this.precision)
            } else if (width == DisplayRequest.VIEW_BOUNDS) {
                Resize(size.width, height, this.scope, this.scale, this.precision)
            } else {
                Resize(width, size.height, this.scope, this.scale, this.precision)
            }
        } else {
            this
        }
    }
}