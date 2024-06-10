package com.github.panpf.sketch.request.internal

import androidx.core.view.ViewCompat
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.target.ViewTarget
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job

/** A request delegate for restartable requests with a [ViewTarget]. */
class ViewTargetRequestDelegate(
    sketch: Sketch,
    initialRequest: ImageRequest,
    private val viewTarget: ViewTarget<*>,
    job: Job
) : BaseRequestDelegate(sketch, initialRequest, viewTarget, job) {

    override fun assertActive() {
        val view = viewTarget.view
            ?: throw CancellationException("'ViewTarget.view' is cleared.")
        if (!ViewCompat.isAttachedToWindow(view)) {
            viewTarget.getRequestManager()?.setRequest(this)
            throw CancellationException("'ViewTarget.view' must be attached to a window.")
        }
    }

    override fun finish() {
        // Monitoring of TargetLifecycle cannot be removed here.
        // Because GenericViewTarget needs to stop or start animation by listening to TargetLifecycle Image
    }
}