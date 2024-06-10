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

import com.github.panpf.sketch.annotation.MainThread
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.target.Target
import com.github.panpf.sketch.target.TargetLifecycle
import com.github.panpf.sketch.target.removeAndAddObserver
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
    val target = initialRequest.target
    val targetRequestDelegate = target?.newRequestDelegate(sketch, initialRequest, job)
    return targetRequestDelegate ?: NoTargetRequestDelegate(sketch, initialRequest, job)
}

interface RequestDelegate {

    val sketch: Sketch

    val initialRequest: ImageRequest

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

class NoTargetRequestDelegate(
    sketch: Sketch,
    initialRequest: ImageRequest,
    job: Job
) : BaseRequestDelegate(sketch, initialRequest, null, job)

open class BaseRequestDelegate(
    override val sketch: Sketch,
    override val initialRequest: ImageRequest,
    protected val target: Target?,
    protected val job: Job
) : RequestDelegate, AttachObserver, TargetLifecycle.EventObserver {

    protected var lifecycle: TargetLifecycle? = null

    override fun assertActive() {
        // Do Nothing.
    }

    override fun start(lifecycle: TargetLifecycle) {
        this.lifecycle = lifecycle
        lifecycle.addObserver(this)

        val target = target
        if (target != null) {
            target.getRequestManager()?.setRequest(this)
            if (target is TargetLifecycle.EventObserver) {
                lifecycle.removeAndAddObserver(target)
            }
        }
    }

    override fun dispose() {
        job.cancel()
        val target = target
        if (target is TargetLifecycle.EventObserver) {
            lifecycle?.removeObserver(target)
        }
        lifecycle?.removeObserver(this)
    }

    override fun finish() {
        val target = target
        if (target is TargetLifecycle.EventObserver) {
            lifecycle?.removeObserver(target)
        }
        lifecycle?.removeObserver(this)
    }

    override fun onAttachedChanged(attached: Boolean) {
        if (target is AttachObserver) {
            target.onAttachedChanged(attached)
        }
    }

    override fun onStateChanged(source: TargetLifecycle, event: TargetLifecycle.Event) {
        if (event == TargetLifecycle.Event.ON_DESTROY) {
            val target = target
            if (target != null) {
                target.getRequestManager()?.dispose()
            } else {
                dispose()
            }
        }
    }
}