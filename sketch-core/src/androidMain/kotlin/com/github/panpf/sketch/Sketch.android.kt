package com.github.panpf.sketch

import com.github.panpf.sketch.request.Disposable
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.ImageResult
import com.github.panpf.sketch.request.OneShotDisposable
import com.github.panpf.sketch.request.internal.requestManager
import com.github.panpf.sketch.target.ViewTarget
import kotlinx.coroutines.Deferred

internal actual fun getDisposable(
    request: ImageRequest,
    job: Deferred<ImageResult>,
): Disposable {
    val target = request.target
    return if (target is ViewTarget<*>) {
        target.view?.requestManager?.getDisposable(job) ?: OneShotDisposable(job)
    } else {
        OneShotDisposable(job)
    }
}