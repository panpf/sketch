package com.github.panpf.sketch.compose.request.internal

import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.compose.target.ComposeTarget
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.internal.AttachObserver
import com.github.panpf.sketch.request.internal.RequestDelegate
import com.github.panpf.sketch.target.TargetLifecycle
import com.github.panpf.sketch.target.removeAndAddObserver
import kotlinx.coroutines.Job

// TODO 和 ViewTargetRequestDelegate 抽出一个 Base 现在的 Base 改成 Empty
class ComposeTargetRequestDelegate(
    override val sketch: Sketch,
    override val initialRequest: ImageRequest,
    private val target: ComposeTarget,
    private val job: Job
) : RequestDelegate, TargetLifecycle.EventObserver {

    private var lifecycle: TargetLifecycle? = null

    override fun assertActive() {
        // AsyncImageState will only execute the request when it is remembered, so there is no need to judge whether it has been remembered.
    }

    override fun start(lifecycle: TargetLifecycle) {
        target.getRequestManager().setRequest(this)

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
            target.getRequestManager().dispose()
        }
    }

    override fun onAttachedChanged(attached: Boolean) {
        if (target is AttachObserver) {
            target.onAttachedChanged(attached)
        }
    }
}