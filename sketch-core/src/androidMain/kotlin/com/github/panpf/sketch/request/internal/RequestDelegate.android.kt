package com.github.panpf.sketch.request.internal

import androidx.annotation.MainThread
import androidx.core.view.ViewCompat
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.target.TargetLifecycle
import com.github.panpf.sketch.target.ViewTarget
import com.github.panpf.sketch.target.removeAndAddObserver
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job

/** A request delegate for restartable requests with a [ViewTarget]. */
class ViewTargetRequestDelegate(
    internal val sketch: Sketch,
    internal val initialRequest: ImageRequest,
    private val target: ViewTarget<*>,
    private val job: Job
) : RequestDelegate, TargetLifecycle.EventObserver {

    private var lifecycle: TargetLifecycle? = null

    override fun assertActive() {
        val view = target.view
            ?: throw CancellationException("'ViewTarget.view' is cleared.")
        if (!ViewCompat.isAttachedToWindow(view)) {
            target.getRequestManager()?.setRequest(this)
            throw CancellationException("'ViewTarget.view' must be attached to a window.")
        }
    }

    override fun start(lifecycle: TargetLifecycle) {
        target.view ?: return
        target.getRequestManager()?.setRequest(this)

        this.lifecycle = lifecycle
        lifecycle.addObserver(this)
        if (target is TargetLifecycle.EventObserver) {
            lifecycle.removeAndAddObserver(target)
        }
    }

    override fun finish() {
        // Do nothing
    }

    override fun dispose() {
        job.cancel()
        if (target is TargetLifecycle.EventObserver) {
            lifecycle?.removeObserver(target)
        }
        lifecycle?.removeObserver(this)
    }

    override fun onStateChanged(source: TargetLifecycle, event: TargetLifecycle.Event) {
        if (event == TargetLifecycle.Event.ON_DESTROY) {
            target.getRequestManager()?.dispose()
        }
    }

    /** Repeat this request with the same [ImageRequest]. */
    @MainThread
    fun restart() {
        sketch.enqueue(initialRequest)
    }
}