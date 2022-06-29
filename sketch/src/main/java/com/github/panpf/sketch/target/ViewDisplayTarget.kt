package com.github.panpf.sketch.target

import android.graphics.drawable.Drawable
import android.view.View
import androidx.lifecycle.LifecycleObserver

/**
 * A [Target] with an associated [View]. Prefer this to [Target] if the given drawables will only
 * be used by [view].
 *
 * Optionally, [ViewDisplayTarget]s can implement [LifecycleObserver]. They are automatically registered
 * when the request starts and unregistered when the request completes.
 */
interface ViewDisplayTarget<T : View> : DisplayTarget {

    /**
     * The [View] used by this [Target]. This field should be immutable.
     */
    val view: T

    /**
     * The [view]'s current [Drawable].
     */
    var drawable: Drawable?
}
