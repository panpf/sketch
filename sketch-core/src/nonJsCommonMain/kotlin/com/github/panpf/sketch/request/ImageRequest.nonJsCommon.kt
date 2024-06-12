package com.github.panpf.sketch.request

import androidx.lifecycle.Lifecycle
import com.github.panpf.sketch.lifecycle.RealPlatformLifecycle


/**
 * Set the [Lifecycle] for this request.
 *
 * Requests are queued while the lifecycle is not at least [Lifecycle.State.STARTED].
 * Requests are cancelled when the lifecycle reaches [Lifecycle.State.DESTROYED].
 *
 * If this is null or is not set the will attempt to find the lifecycle
 * for this request through its context.
 */
fun ImageRequest.Builder.lifecycle(lifecycle: Lifecycle): ImageRequest.Builder =
    lifecycle(RealPlatformLifecycle(lifecycle))