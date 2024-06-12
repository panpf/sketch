package com.github.panpf.sketch.lifecycle

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner

class RealPlatformLifecycle constructor(val lifecycle: Lifecycle) : PlatformLifecycle() {

    private val owner = RealPlatformLifecycleOwner(this)

    override val currentState: State
        get() = lifecycle.currentState.toPlatformLifecycleState()

    private val wrapperMap =
        mutableMapOf<PlatformLifecycleObserver, LifecycleEventObserverWrapper>()

    override fun addObserver(observer: PlatformLifecycleObserver) {
        require(observer is PlatformLifecycleEventObserver) {
            "Observer must implement PlatformLifecycleEventObserver"
        }
        val previous = wrapperMap[observer]
        if (previous != null) return
        val wrapper = LifecycleEventObserverWrapper(owner, observer)
        lifecycle.addObserver(wrapper)
        wrapperMap[observer] = wrapper
    }

    override fun removeObserver(observer: PlatformLifecycleObserver) {
        require(observer is PlatformLifecycleEventObserver) {
            "Observer must implement PlatformLifecycleEventObserver"
        }
        val wrapper = wrapperMap.remove(observer)
        if (wrapper != null) {
            lifecycle.removeObserver(wrapper)
        }
    }

    private class LifecycleEventObserverWrapper(
        private val owner: PlatformLifecycleOwner,
        private val observer: PlatformLifecycleEventObserver,
    ) : LifecycleEventObserver {

        override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
            observer.onStateChanged(owner, event.toPlatformLifecycleEvent())
        }

        private fun Lifecycle.Event.toPlatformLifecycleEvent(): Event {
            return when (this) {
                Lifecycle.Event.ON_CREATE -> Event.ON_CREATE
                Lifecycle.Event.ON_START -> Event.ON_START
                Lifecycle.Event.ON_RESUME -> Event.ON_RESUME
                Lifecycle.Event.ON_PAUSE -> Event.ON_PAUSE
                Lifecycle.Event.ON_STOP -> Event.ON_STOP
                Lifecycle.Event.ON_DESTROY -> Event.ON_DESTROY
                Lifecycle.Event.ON_ANY -> Event.ON_ANY
            }
        }
    }

    private fun Lifecycle.State.toPlatformLifecycleState(): State {
        return when (this) {
            Lifecycle.State.INITIALIZED -> State.INITIALIZED
            Lifecycle.State.CREATED -> State.CREATED
            Lifecycle.State.STARTED -> State.STARTED
            Lifecycle.State.RESUMED -> State.RESUMED
            Lifecycle.State.DESTROYED -> State.DESTROYED
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is RealPlatformLifecycle) return false
        return lifecycle == other.lifecycle
    }

    override fun hashCode(): Int {
        return lifecycle.hashCode()
    }

    override fun toString(): String {
        return "RealPlatformLifecycle($lifecycle)"
    }
}

private class RealPlatformLifecycleOwner(
    lifecycle: RealPlatformLifecycle
) : PlatformLifecycleOwner {
    override val lifecycle: PlatformLifecycle = lifecycle
}