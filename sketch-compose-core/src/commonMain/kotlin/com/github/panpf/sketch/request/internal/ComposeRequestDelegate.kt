package com.github.panpf.sketch.request.internal

import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.target.ComposeTarget
import kotlinx.coroutines.Job

class ComposeRequestDelegate(
    sketch: Sketch,
    initialRequest: ImageRequest,
    target: ComposeTarget,
    job: Job
) : BaseRequestDelegate(sketch, initialRequest, target, job) {

    override fun assertActive() {
        // AsyncImageState will only execute the request when it is remembered, so there is no need to judge whether it has been remembered.
    }

    override fun finish() {
        // Monitoring of Lifecycle cannot be removed here.
        // Because GenericComposeTarget needs to stop or start animation by listening to Lifecycle Image
    }
}