package com.github.panpf.sketch.request

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.github.panpf.sketch.util.getLifecycle

/**
 * A [Lifecycle] implementation that is always resumed and never destroyed.
 *
 * This is used as a fallback if [getLifecycle] cannot find a more tightly scoped [Lifecycle].
 */
internal object GlobalLifecycle : Lifecycle() {

    private val owner = LifecycleOwner { this }

    override fun addObserver(observer: LifecycleObserver) {
        require(observer is DefaultLifecycleObserver) {
            "$observer must implement androidx.lifecycle.DefaultLifecycleObserver."
        }

        // Call the lifecycle methods in order and do not hold a reference to the observer.
        observer.onCreate(owner)
        observer.onStart(owner)
        observer.onResume(owner)
    }

    override fun removeObserver(observer: LifecycleObserver) {}

    override fun getCurrentState() = State.RESUMED

    override fun toString() = "coil.request.GlobalLifecycle"
}

fun Lifecycle.isSketchGlobalLifecycle() = this is GlobalLifecycle