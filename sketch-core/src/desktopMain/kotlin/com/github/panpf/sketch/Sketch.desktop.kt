package com.github.panpf.sketch

import com.github.panpf.sketch.request.Disposable
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.ImageResult
import com.github.panpf.sketch.request.OneShotDisposable
import kotlinx.coroutines.Deferred


internal actual fun platformComponents(): ComponentRegistry {
    return ComponentRegistry.Builder().apply {
        // TODO add desktop components
        // TODO ResultCache
    }.build()
}

internal actual fun getDisposable(
    request: ImageRequest,
    job: Deferred<ImageResult>,
): Disposable = OneShotDisposable(job)