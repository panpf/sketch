/*
 * Copyright (C) 2022 panpf <panpfpanpf@outlook.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.panpf.sketch.request.internal

import androidx.annotation.MainThread
import androidx.core.view.ViewCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.request.DisplayRequest
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.target.ViewDisplayTarget
import com.github.panpf.sketch.util.removeAndAddObserver
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job

/**
 * Wrap [initialRequest] to automatically dispose and/or restart the [ImageRequest]
 * based on its lifecycle.
 */
internal fun requestDelegate(
    sketch: Sketch,
    initialRequest: ImageRequest,
    lifecycle: Lifecycle,
    job: Job
): RequestDelegate {
    return when (val target = initialRequest.target) {
        is ViewDisplayTarget<*> -> ViewTargetRequestDelegate(
            sketch = sketch,
            initialRequest = initialRequest as DisplayRequest,
            target = target,
            lifecycle = lifecycle,
            job = job
        )

        else -> BaseRequestDelegate(lifecycle, job)
    }
}

sealed interface RequestDelegate : LifecycleEventObserver {

    /** Throw a [CancellationException] if this request should be cancelled before starting. */
    @MainThread
    fun assertActive()

    /** Register all lifecycle observers. */
    @MainThread
    fun start()

    /** Called when this request's job is cancelled or completes successfully/unsuccessfully. */
    @MainThread
    fun finish()

    /** Cancel this request's job and clear all lifecycle observers. */
    @MainThread
    fun dispose()
}

/** A request delegate for a one-shot requests with no target or a non-[ViewDisplayTarget]. */
internal class BaseRequestDelegate(
    private val lifecycle: Lifecycle,
    private val job: Job
) : RequestDelegate {

    override fun assertActive() {
        // Do nothing
    }

    override fun start() {
        lifecycle.addObserver(this)
    }

    override fun finish() {
        lifecycle.removeObserver(this)
    }

    override fun dispose() {
        job.cancel()
    }

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        if (event == Lifecycle.Event.ON_DESTROY) {
            dispose()
        }
    }
}

/** A request delegate for restartable requests with a [ViewDisplayTarget]. */
class ViewTargetRequestDelegate(
    internal val sketch: Sketch,
    internal val initialRequest: DisplayRequest,
    private val target: ViewDisplayTarget<*>,
    private val lifecycle: Lifecycle,
    private val job: Job
) : RequestDelegate {

    override fun assertActive() {
        val view = target.view
            ?: throw CancellationException("'ViewTarget.view' is cleared.")
        if (!ViewCompat.isAttachedToWindow(view)) {
            view.requestManager.setRequest(this)
            throw CancellationException("'ViewTarget.view' must be attached to a window.")
        }
    }

    override fun start() {
        val view = target.view ?: return
        view.requestManager.setRequest(this)

        lifecycle.addObserver(this)
        if (target is LifecycleObserver) {
            lifecycle.removeAndAddObserver(target)
        }
    }

    override fun finish() {
        // Do nothing
    }

    override fun dispose() {
        job.cancel()
        if (target is LifecycleObserver) {
            lifecycle.removeObserver(target)
        }
        lifecycle.removeObserver(this)
    }

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        if (event == Lifecycle.Event.ON_DESTROY) {
            target.view?.requestManager?.dispose()
        }
    }

    /** Repeat this request with the same [ImageRequest]. */
    @MainThread
    fun restart() {
        sketch.enqueue(initialRequest)
    }

    fun onViewDetachedFromWindow() {
        // To trigger setIsDisplayed in the ImageViewTarget
        target.drawable = null
    }
}
