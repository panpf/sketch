package com.github.panpf.sketch

import android.widget.ImageView
import com.github.panpf.sketch.request.DisplayRequest
import com.github.panpf.sketch.request.DisplayResult
import com.github.panpf.sketch.request.Disposable
import com.github.panpf.sketch.request.RequestManagerUtils

fun ImageView.displayImage(
    uri: String?,
    configBlock: (DisplayRequest.Builder.() -> Unit)? = null
): Disposable<DisplayResult> =
    context.sketch.enqueueDisplay(DisplayRequest(uri, this@displayImage, configBlock))

/**
 * Dispose the request that's attached to this view (if there is one).
 */
@Suppress("NOTHING_TO_INLINE")
inline fun ImageView.dispose() {
    RequestManagerUtils.dispose(this)
}

/**
 * Get the [DisplayResult] of the most recently executed image request that's attached to this view.
 */
inline val ImageView.result: DisplayResult?
    get() = RequestManagerUtils.result(this)