package com.github.panpf.sketch

import android.widget.ImageView
import com.github.panpf.sketch.request.DisplayResult
import com.github.panpf.sketch.request.Disposable
import com.github.panpf.sketch.request.ExecuteResult
import com.github.panpf.sketch.request.DisplayRequest

fun ImageView.displayImage(
    url: String?,
    configBlock: (DisplayRequest.Builder.() -> Unit)? = null
): Disposable<ExecuteResult<DisplayResult>> {
    val request = DisplayRequest
        .newBuilder(url, configBlock)
        .target(this@displayImage)
        .build()
    return context.sketch.enqueueDisplay(request)
}

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