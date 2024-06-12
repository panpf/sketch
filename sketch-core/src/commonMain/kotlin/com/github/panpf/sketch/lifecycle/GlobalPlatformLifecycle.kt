package com.github.panpf.sketch.lifecycle

/**
 * A [PlatformLifecycle] implementation that is always resumed and never destroyed.
 */
data object GlobalPlatformLifecycle : PlatformLifecycle() {

    val owner = GlobalPlatformLifecycleOwner(this)

    override val currentState: State
        get() = State.RESUMED

    override fun addObserver(observer: PlatformLifecycleObserver) {
        require(observer is PlatformLifecycleEventObserver) {
            "Observer must implement PlatformLifecycleEventObserver"
        }
        // Call the lifecycle methods in order and do not hold a reference to the observer.
        observer.onStateChanged(owner, Event.ON_CREATE)
        observer.onStateChanged(owner, Event.ON_START)
        observer.onStateChanged(owner, Event.ON_RESUME)
    }

    override fun removeObserver(observer: PlatformLifecycleObserver) {
        require(observer is PlatformLifecycleEventObserver) {
            "Observer must implement PlatformLifecycleEventObserver"
        }
    }

    override fun toString() = "GlobalPlatformLifecycle"
}

class GlobalPlatformLifecycleOwner(
    lifecycle: GlobalPlatformLifecycle
) : PlatformLifecycleOwner {
    override val lifecycle: PlatformLifecycle = lifecycle
}