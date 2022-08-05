@file:JvmName("ViewSizeResolvers")

/*
 * Copyright (C) 2022 panpf <panpfpanpf@outlook.com>
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
package com.github.panpf.sketch.resize

import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.ViewTreeObserver.OnPreDrawListener
import com.github.panpf.sketch.util.Size
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

/**
 * Create a [ViewSizeResolver] using the default [View] measurement implementation.
 *
 * @param view The view to measure.
 * @param subtractPadding If true, the view's padding will be subtracted from its size.
 */
@Suppress("FunctionName")
@JvmOverloads
@JvmName("create")
fun <T : View> ViewSizeResolver(view: T, subtractPadding: Boolean = true): ViewSizeResolver<T> =
    RealViewSizeResolver(view, subtractPadding)

internal data class RealViewSizeResolver<T : View>(
    override val view: T,
    override val subtractPadding: Boolean
) : ViewSizeResolver<T>

/**
 * A [SizeResolver] that measures the size of a [View].
 */
interface ViewSizeResolver<T : View> : SizeResolver {

    /** The [View] to measure. This field should be immutable. */
    val view: T

    /** If true, the [view]'s padding will be subtracted from its size. */
    val subtractPadding: Boolean

    override suspend fun size(): Size {
        // Fast path: the view is already measured.
        getSize()?.let { return it }

        // Slow path: wait for the view to be measured.
        return suspendCancellableCoroutine { continuation ->
            val viewTreeObserver = view.viewTreeObserver

            val preDrawListener = object : OnPreDrawListener {
                private var isResumed = false

                override fun onPreDraw(): Boolean {
                    val size = getSize()
                    if (size != null) {
                        viewTreeObserver.removePreDrawListenerSafe(this)

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
                viewTreeObserver.removePreDrawListenerSafe(preDrawListener)
            }
        }
    }

    private fun getSize(): Size? {
        val width = getWidth() ?: return null
        val height = getHeight() ?: return null
        return Size(width, height)
    }

    private fun getWidth(): Int? = getDimension(
        paramSize = view.layoutParams?.width ?: ViewGroup.LayoutParams.WRAP_CONTENT,
        viewSize = view.width,
        paddingSize = if (subtractPadding) view.paddingLeft + view.paddingRight else 0,
        wrapMaxSize = view.resources.displayMetrics.widthPixels
    )

    private fun getHeight(): Int? = getDimension(
        paramSize = view.layoutParams?.height ?: ViewGroup.LayoutParams.WRAP_CONTENT,
        viewSize = view.height,
        paddingSize = if (subtractPadding) view.paddingTop + view.paddingBottom else 0,
        wrapMaxSize = view.resources.displayMetrics.heightPixels
    )

    private fun getDimension(
        paramSize: Int,
        viewSize: Int,
        paddingSize: Int,
        wrapMaxSize: Int
    ): Int? {
        // If the dimension is set to WRAP_CONTENT, use the display dimension.
        if (paramSize == ViewGroup.LayoutParams.WRAP_CONTENT) {
            return wrapMaxSize
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

    private fun ViewTreeObserver.removePreDrawListenerSafe(victim: OnPreDrawListener) {
        if (isAlive) {
            removeOnPreDrawListener(victim)
        } else {
            view.viewTreeObserver.removeOnPreDrawListener(victim)
        }
    }
}
