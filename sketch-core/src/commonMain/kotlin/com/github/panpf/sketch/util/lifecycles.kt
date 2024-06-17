package com.github.panpf.sketch.util

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleObserver
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

/** Suspend until [Lifecycle.currentState] is at least [Lifecycle.State.STARTED] */
internal suspend fun Lifecycle.awaitStarted() {
    // Fast path: we're already started.
    Lifecycle.State.STARTED
    if (currentState.isAtLeast(Lifecycle.State.STARTED)) return

    // Slow path: observe the lifecycle until we're started.
    var observer: LifecycleEventObserver? = null
    try {
        suspendCancellableCoroutine { continuation ->
            observer = LifecycleEventObserver { _, event ->
                if (event == Lifecycle.Event.ON_START) {
                    continuation.resume(Unit)
                }
            }
            addObserver(observer!!)
        }
    } finally {
        // 'observer' will always be null if this method is marked as 'inline'.
        observer?.let(::removeObserver)
    }
}

/** Remove and re-add the observer to ensure all its lifecycle callbacks are invoked. */
fun Lifecycle.removeAndAddObserver(observer: LifecycleObserver) {
    removeObserver(observer)
    addObserver(observer)
}