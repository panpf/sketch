package com.github.panpf.sketch.request

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner

/**
 * A [Lifecycle] implementation that is always resumed and never destroyed.
 *
 * This is used as a fallback if [Lifecycle] is not specified and cannot be obtained from the Target or Context.
 */
data object GlobalLifecycle : Lifecycle() {

    val owner = object : LifecycleOwner {
        override val lifecycle get() = this@GlobalLifecycle
    }

    override val currentState: State
        get() = State.RESUMED

    override fun addObserver(observer: LifecycleObserver) {
        require(observer is LifecycleEventObserver) {
            "Observer must implement LifecycleEventObserver"
        }
        // Call the lifecycle methods in order and do not hold a reference to the observer.
        observer.onStateChanged(owner, Event.ON_CREATE)
        observer.onStateChanged(owner, Event.ON_START)
        observer.onStateChanged(owner, Event.ON_RESUME)
    }

    override fun removeObserver(observer: LifecycleObserver) {
        require(observer is LifecycleEventObserver) {
            "Observer must implement LifecycleEventObserver"
        }
    }

    override fun toString() = "GlobalLifecycle"
}