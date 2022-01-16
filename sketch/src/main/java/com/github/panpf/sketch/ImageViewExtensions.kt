package com.github.panpf.sketch

import android.net.Uri
import android.widget.ImageView
import com.github.panpf.sketch.request.DisplayRequest
import com.github.panpf.sketch.request.DisplayResult
import com.github.panpf.sketch.request.Disposable

fun ImageView.displayImage(
    uri: String?,
    configBlock: (DisplayRequest.Builder.() -> Unit)? = null
): Disposable<DisplayResult> {
    val request = DisplayRequest
        .newBuilder(uri).apply {
            target(this@displayImage)
            configBlock?.invoke(this)
        }.build()
    return context.sketch.enqueueDisplay(request)
}

fun ImageView.displayImage(
    uri: Uri?,
    configBlock: (DisplayRequest.Builder.() -> Unit)? = null
): Disposable<DisplayResult> {
    val request = DisplayRequest
        .newBuilder(uri).apply {
            target(this@displayImage)
            configBlock?.invoke(this)
        }.build()
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