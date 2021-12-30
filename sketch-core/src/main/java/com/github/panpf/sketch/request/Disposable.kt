package com.github.panpf.sketch.request

import com.github.panpf.sketch.Sketch
import kotlinx.coroutines.Deferred

/**
 * Represents the work of an *Request that has been executed by an [Sketch].
 */
interface Disposable<T> {

    /**
     * The most recent image request job.
     * This field is **not immutable** and can change if the request is replayed.
     */
    val job: Deferred<T>

    /**
     * Returns 'true' if this disposable's work is complete or cancelling.
     */
    val isDisposed: Boolean

    /**
     * Cancels this disposable's work and releases any held resources.
     */
    fun dispose()
}

/**
 * A disposable for one-shot image requests.
 */
internal class OneShotDisposable<T>(
    override val job: Deferred<T>
) : Disposable<T> {

    override val isDisposed: Boolean
        get() = !job.isActive

    override fun dispose() {
        if (isDisposed) return
        job.cancel()
    }
}

///**
// * A disposable for requests that are attached to a [View].
// *
// * [ViewTarget] requests are automatically cancelled in when the view is detached
// * and are restarted when the view is attached.
// *
// * [isDisposed] only returns 'true' when this disposable's request is cleared (due to
// * [DefaultLifecycleObserver.onDestroy]) or replaced by a new request attached to the view.
// */
//internal class ViewTargetDisposable<T>(
//    private val view: View,
//    @Volatile override var job: Deferred<T>
//) : Disposable<T> {
//
//    override val isDisposed: Boolean
//        get() = view.requestManager.isDisposed(this)
//
//    override fun dispose() {
//        if (isDisposed) return
//        view.requestManager.dispose()
//    }
//}
