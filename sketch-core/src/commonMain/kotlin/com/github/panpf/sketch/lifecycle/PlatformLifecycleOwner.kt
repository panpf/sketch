package com.github.panpf.sketch.lifecycle

/**
 * A class that has an Android lifecycle. These events can be used by custom components to
 * handle lifecycle changes without implementing any code inside the Activity or the Fragment.
 *
 * @see PlatformLifecycle
 * @see ViewTreeLifecycleOwner
 */
interface PlatformLifecycleOwner {
    /**
     * Returns the Lifecycle of the provider.
     *
     * @return The lifecycle of the provider.
     */
    val lifecycle: PlatformLifecycle
}