//package com.github.panpf.sketch.sample.util
//
//import android.graphics.drawable.Drawable
//import android.hardware.display.DisplayManager.DisplayListener
//import com.github.panpf.sketch.decode.ImageAttrs
//import com.github.panpf.sketch.request.CancelCause
//import com.github.panpf.sketch.request.DisplayListener
//import com.github.panpf.sketch.request.ErrorCause
//import com.github.panpf.sketch.request.ImageFrom
//
//class CompactDisplayListener(
//    private val onStarted: (() -> Unit)? = null,
//    private val onError: ((cause: ErrorCause) -> Unit)? = null,
//    private val onCanceled: ((cause: CancelCause) -> Unit)? = null,
//    private val onCompleted: ((drawable: Drawable, imageFrom: ImageFrom, imageAttrs: ImageAttrs) -> Unit)? = null,
//) : DisplayListener {
//    override fun onStarted() {
//        onStarted?.invoke()
//    }
//
//    override fun onError(cause: ErrorCause) {
//        onError?.invoke(cause)
//    }
//
//    override fun onCanceled(cause: CancelCause) {
//        onCanceled?.invoke(cause)
//    }
//
//    override fun onCompleted(drawable: Drawable, imageFrom: ImageFrom, imageAttrs: ImageAttrs) {
//        onCompleted?.invoke(drawable, imageFrom, imageAttrs)
//    }
//}