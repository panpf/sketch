package com.github.panpf.sketch

import android.widget.ImageView
import com.github.panpf.sketch.request.DisplayRequest
import com.github.panpf.sketch.request.DisplayResult
import com.github.panpf.sketch.request.Disposable

fun ImageView.displayImage(
    uri: String?,
    configBlock: (DisplayRequest.Builder.() -> Unit)? = null
): Disposable<DisplayResult> =
    context.sketch.enqueueDisplay(DisplayRequest.new(uri, this@displayImage, configBlock))

/**
 * Dispose the request that's attached to this view (if there is one).
 */
inline fun ImageView.dispose() {
//    CoilUtils.dispose(this)
    TODO("Not yet implementation")
}

///**
// * Get the [ImageResult] of the most recently executed image request that's attached to this view.
// */
//inline val ImageView.result: ImageResult?
//    get() = {
//        TODO("Not yet implementation")
//        CoilUtils.result(this)
//    }