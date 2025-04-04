/*
 * Copyright (C) 2024 panpf <panpfpanpf@outlook.com>
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
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.target.Target
import com.github.panpf.sketch.util.removeAndAddObserver
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job

/**
 * Wrap [initialRequest] to automatically dispose and/or restart the [ImageRequest]
 * based on its lifecycle.
 *
 * @see com.github.panpf.sketch.core.common.test.request.internal.RequestDelegateTest.testRequestDelegate
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

/**
 * A delegate that manages the lifecycle of an [ImageRequest] and its [Target].
 *
 * @see com.github.panpf.sketch.request.internal.BaseRequestDelegate
 * @see com.github.panpf.sketch.request.internal.ViewRequestDelegate
 * @see com.github.panpf.sketch.request.internal.ComposeRequestDelegate
 * @see com.github.panpf.sketch.request.internal.RemoteViewsDelegate
 * @see com.github.panpf.sketch.request.internal.NoTargetRequestDelegate
 * @see com.github.panpf.sketch.request.internal.OneShotRequestDelegate
 */
interface RequestDelegate {

    /**
     * The [Sketch] instance
     */
    val sketch: Sketch

    /**
     * The initial [ImageRequest] that this delegate is managing.
     */
    val initialRequest: ImageRequest

    /**
     * Throw a [CancellationException] if this request should be cancelled before starting. */
    @MainThread
    fun assertActive()

    /**
     * Register all lifecycle observers.
     */
    @MainThread
    fun start(lifecycle: Lifecycle)

    /**
     * Called when this request's job is cancelled or completes successfully/unsuccessfully.
     */
    @MainThread
    fun finish()

    /**
     * Cancel this request's job and clear all lifecycle observers.
     */
    @MainThread
    fun dispose()
}

/**
 * A request delegate for requests without a [Target].
 *
 * @see com.github.panpf.sketch.core.common.test.request.internal.RequestDelegateTest.testNoTargetRequestDelegate
 */
class NoTargetRequestDelegate(
    sketch: Sketch,
    initialRequest: ImageRequest,
    job: Job
) : BaseRequestDelegate(sketch, initialRequest, null, job) {

    override fun assertActive() {
        // No Target, there is no need to judge whether it is active or not.
    }

    override fun finish() {
        removeLifecycleObserver()
    }
}

/**
 * A request delegate for requests without a [Target].
 *
 * @see com.github.panpf.sketch.core.common.test.request.internal.RequestDelegateTest.testOneShotRequestDelegate
 */
class OneShotRequestDelegate(
    sketch: Sketch,
    initialRequest: ImageRequest,
    target: Target,
    job: Job
) : BaseRequestDelegate(sketch, initialRequest, target, job) {

    override fun assertActive() {
        // No action
    }

    override fun finish() {
        removeLifecycleObserver()
    }
}

/**
 * A base implementation of [RequestDelegate] that handles attaching to a [Lifecycle] and [Target].
 *
 * @see com.github.panpf.sketch.request.internal.ViewRequestDelegate
 * @see com.github.panpf.sketch.request.internal.ComposeRequestDelegate
 * @see com.github.panpf.sketch.request.internal.RemoteViewsDelegate
 * @see com.github.panpf.sketch.request.internal.NoTargetRequestDelegate
 * @see com.github.panpf.sketch.request.internal.OneShotRequestDelegate
 * @see com.github.panpf.sketch.core.common.test.request.internal.BaseRequestManagerTest
 */
abstract class BaseRequestDelegate(
    override val sketch: Sketch,
    override val initialRequest: ImageRequest,
    protected val target: Target?,
    protected val job: Job
) : RequestDelegate, AttachObserver, LifecycleEventObserver {

    protected var lifecycle: Lifecycle? = null

    abstract override fun assertActive()

    override fun start(lifecycle: Lifecycle) {
        this.lifecycle = lifecycle
        lifecycle.addObserver(this)

        val target = target
        if (target != null) {
            target.getRequestManager()?.setRequest(this)
            if (target is LifecycleEventObserver) {
                lifecycle.removeAndAddObserver(target)
            }
        }
    }

    override fun dispose() {
        job.cancel()
        removeLifecycleObserver()
    }

    abstract override fun finish()

    protected fun removeLifecycleObserver() {
        val target = target
        if (target is LifecycleEventObserver) {
            lifecycle?.removeObserver(target)
        }
        lifecycle?.removeObserver(this)
    }

    override fun onAttachedChanged(attached: Boolean) {
        if (target is AttachObserver) {
            target.onAttachedChanged(attached)
        }
    }

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        if (event == Lifecycle.Event.ON_DESTROY) {
            val target = target
            if (target != null) {
                target.getRequestManager()?.dispose()
            } else {
                dispose()
            }
        }
    }
}