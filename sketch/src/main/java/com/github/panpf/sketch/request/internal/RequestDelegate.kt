package com.github.panpf.sketch.request.internal

import androidx.annotation.MainThread
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.request.DisplayRequest
import com.github.panpf.sketch.target.ViewTarget
import com.github.panpf.sketch.util.isAttachedToWindowCompat
import com.github.panpf.sketch.util.removeAndAddObserver
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job

/**
 * Wrap [initialRequest] to automatically dispose and/or restart the [ImageRequest]
 * based on its lifecycle.
 */
internal fun requestDelegate(sketch: Sketch, initialRequest: DisplayRequest, job: Job): RequestDelegate {
    val lifecycle = initialRequest.lifecycle
    return when (val target = initialRequest.target) {
        is ViewTarget<*> -> ViewTargetRequestDelegate(sketch, initialRequest, target, lifecycle, job)
        else -> BaseRequestDelegate(lifecycle, job)
    }
}

sealed class RequestDelegate : DefaultLifecycleObserver {

    /** Throw a [CancellationException] if this request should be cancelled before starting. */
    @MainThread
    open fun assertActive() {}

    /** Register all lifecycle observers. */
    @MainThread
    open fun start() {}

    /** Called when this request's job is cancelled or completes successfully/unsuccessfully. */
    @MainThread
    open fun complete() {}

    /** Cancel this request's job and clear all lifecycle observers. */
    @MainThread
    open fun dispose() {}
}

/** A request delegate for a one-shot requests with no target or a non-[ViewTarget]. */
internal class BaseRequestDelegate(
    private val lifecycle: Lifecycle?,
    private val job: Job
) : RequestDelegate() {

    override fun start() {
        lifecycle?.addObserver(this)
    }

    override fun complete() {
        lifecycle?.removeObserver(this)
    }

    override fun dispose() {
        job.cancel()
    }

    override fun onDestroy(owner: LifecycleOwner) = dispose()
}

/** A request delegate for restartable requests with a [ViewTarget]. */
class ViewTargetRequestDelegate(
    private val sketch: Sketch,
    private val initialRequest: DisplayRequest,
    private val target: ViewTarget<*>,
    private val lifecycle: Lifecycle?,
    private val job: Job
) : RequestDelegate() {

    /** Repeat this request with the same [ImageRequest]. */
    @MainThread
    fun restart() {
        sketch.enqueueDisplay(initialRequest)
    }

    override fun assertActive() {
        if (!target.view.isAttachedToWindowCompat) {
            target.view.requestManager.setRequest(this)
            throw CancellationException("'ViewTarget.view' must be attached to a window.")
        }
    }

    override fun start() {
        lifecycle?.addObserver(this)
        if (target is LifecycleObserver) {
            lifecycle?.removeAndAddObserver(target)
        }
        target.view.requestManager.setRequest(this)
    }

    override fun dispose() {
        job.cancel()
        if (target is LifecycleObserver) {
            lifecycle?.removeObserver(target)
        }
        lifecycle?.removeObserver(this)
    }

    override fun onDestroy(owner: LifecycleOwner) {
        target.view.requestManager.dispose()
    }
}
