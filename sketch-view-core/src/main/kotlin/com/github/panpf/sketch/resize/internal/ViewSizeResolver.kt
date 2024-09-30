@file:JvmName("ViewSizeResolvers")

/*
 * Copyright (C) 2024 panpf <panpfpanpf@outlook.com>
 * Copyright 2023 Coil Contributors
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

package com.github.panpf.sketch.resize.internal

import android.util.DisplayMetrics
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.ViewTreeObserver.OnPreDrawListener
import com.github.panpf.sketch.resize.SizeResolver
import com.github.panpf.sketch.util.Size
import com.github.panpf.sketch.util.toHexString
import kotlinx.coroutines.suspendCancellableCoroutine
import java.lang.ref.WeakReference

/**
 * Create a [ViewSizeResolver] using the default [View] measurement implementation.
 *
 * @param view The view to measure.
 * @param subtractPadding If true, the view's padding will be subtracted from its size.
 *
 * @see com.github.panpf.sketch.view.core.test.resize.internal.ViewSizeResolverTest.testViewSizeResolver
 */
@JvmOverloads
@JvmName("create")
fun <T : View> ViewSizeResolver(view: T, subtractPadding: Boolean = true): ViewSizeResolver<T> =
    RealViewSizeResolver(view, subtractPadding)

/**
 * A [SizeResolver] that measures the size of a [View].
 *
 * @see com.github.panpf.sketch.view.core.test.resize.internal.ViewSizeResolverTest
 */
internal class RealViewSizeResolver<T : View>(
    view1: T,
    override val subtractPadding: Boolean
) : ViewSizeResolver<T> {

    private val viewReference: WeakReference<T> = WeakReference(view1)

    private val viewKey = "${view1::class.simpleName}@${view1.toHexString()}"

    override val key: String = "ViewSize($viewKey,$subtractPadding)"

    override val view: T?
        get() = viewReference.get()

    override val displayMetrics: DisplayMetrics = view1.resources.displayMetrics

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as RealViewSizeResolver<*>
        if (view != other.view) return false
        if (subtractPadding != other.subtractPadding) return false
        return true
    }

    override fun hashCode(): Int {
        var result = view.hashCode()
        result = 31 * result + subtractPadding.hashCode()
        return result
    }

    override fun toString(): String {
        return "ViewSizeResolver(view=$viewKey, subtractPadding=$subtractPadding)"
    }
}

/**
 * A [SizeResolver] that measures the size of a [View].
 *
 * @see com.github.panpf.sketch.view.core.test.resize.internal.ViewSizeResolverTest#testViewSizeResolver
 */
interface ViewSizeResolver<T : View> : SizeResolver {

    /** The [View] to measure. This field should be immutable. */
    val view: T?

    /** If true, the [view]'s padding will be subtracted from its size. */
    val subtractPadding: Boolean

    val displayMetrics: DisplayMetrics

    override suspend fun size(): Size {
        if (view?.isAttachedToWindow == true) {
            // Fast path: the view is already measured.
            getSize().let { return it }
        } else {
            getSizeOrNull()?.let { return it }
        }

        // Slow path: wait for the view to be measured.
        return suspendCancellableCoroutine { continuation ->
            val view = view ?: return@suspendCancellableCoroutine
            val viewTreeObserver = view.viewTreeObserver
            val preDrawListener = object : OnPreDrawListener {
                private var isResumed = false

                override fun onPreDraw(): Boolean {
                    if (!isResumed) {
                        isResumed = true
                        viewTreeObserver.removePreDrawListenerSafe(this)
                        val size: Size = getSize()
                        continuation.resumeWith(Result.success(size))
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

    private fun getSizeOrNull(): Size? {
        val view = view ?: return null
        val width = getWidth(view) ?: return null
        val height = getHeight(view) ?: return null
        return Size(width, height)
    }

    private fun getSize(): Size {
        val view = view ?: return Size(displayMetrics.widthPixels, displayMetrics.heightPixels)
        val width = getWidth(view) ?: displayMetrics.widthPixels
        val height = getHeight(view) ?: displayMetrics.heightPixels
        return Size(width, height)
    }

    private fun getWidth(view: View): Int? = getDimension(
        paramSize = view.layoutParams?.width ?: ViewGroup.LayoutParams.WRAP_CONTENT,
        viewSize = view.width,
        paddingSize = if (subtractPadding) view.paddingLeft + view.paddingRight else 0,
        displaySize = displayMetrics.widthPixels
    )

    private fun getHeight(view: View): Int? = getDimension(
        paramSize = view.layoutParams?.height ?: ViewGroup.LayoutParams.WRAP_CONTENT,
        viewSize = view.height,
        paddingSize = if (subtractPadding) view.paddingTop + view.paddingBottom else 0,
        displaySize = displayMetrics.heightPixels
    )

    private fun getDimension(
        paramSize: Int,
        viewSize: Int,
        paddingSize: Int,
        displaySize: Int
    ): Int? = when (paramSize) {
        ViewGroup.LayoutParams.WRAP_CONTENT -> {
            displaySize
        }

        ViewGroup.LayoutParams.MATCH_PARENT -> {
            val insetViewSize = viewSize - paddingSize
            insetViewSize.takeIf { it > 0 }
        }

        else -> {
            val insetParamSize = paramSize - paddingSize
            insetParamSize.takeIf { it > 0 }
        }
    }

    private fun ViewTreeObserver.removePreDrawListenerSafe(victim: OnPreDrawListener) {
        if (isAlive) {
            removeOnPreDrawListener(victim)
        } else {
            view?.viewTreeObserver?.removeOnPreDrawListener(victim)
        }
    }
}
