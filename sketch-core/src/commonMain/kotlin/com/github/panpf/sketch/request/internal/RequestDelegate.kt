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
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.target.TargetLifecycle
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job

/**
 * Wrap [initialRequest] to automatically dispose and/or restart the [ImageRequest]
 * based on its lifecycle.
 */
internal fun requestDelegate(
    sketch: Sketch,
    initialRequest: ImageRequest,
    job: Job
): RequestDelegate {
    val targetRequestDelegate =
        initialRequest.target?.getRequestDelegate(sketch, initialRequest, job)
    return targetRequestDelegate ?: BaseRequestDelegate(job)
}

interface RequestDelegate {

    /** Throw a [CancellationException] if this request should be cancelled before starting. */
    @MainThread
    fun assertActive()

    /** Register all lifecycle observers. */
    @MainThread
    fun start(lifecycle: TargetLifecycle)

    /** Called when this request's job is cancelled or completes successfully/unsuccessfully. */
    @MainThread
    fun finish()

    /** Cancel this request's job and clear all lifecycle observers. */
    @MainThread
    fun dispose()
}

/** A request delegate for a one-shot requests with no target or a non-[ViewTarget]. */
class BaseRequestDelegate(
    private val job: Job
) : RequestDelegate, TargetLifecycle.EventObserver {

    private var lifecycle: TargetLifecycle? = null

    override fun assertActive() {
        // Do nothing
    }

    override fun start(lifecycle: TargetLifecycle) {
        this.lifecycle = lifecycle
        lifecycle.addObserver(this)
    }

    override fun finish() {
        lifecycle?.removeObserver(this)
    }

    override fun dispose() {
        job.cancel()
    }

    override fun onStateChanged(source: TargetLifecycle, event: TargetLifecycle.Event) {
        if (event == TargetLifecycle.Event.ON_DESTROY) {
            dispose()
        }
    }
}