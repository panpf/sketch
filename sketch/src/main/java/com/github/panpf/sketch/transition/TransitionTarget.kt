package com.github.panpf.sketch.transition

import android.graphics.drawable.Drawable
import android.view.View
import com.github.panpf.sketch.target.Target

/**
 * A [Target] that supports applying [Transition]s.
 */
interface TransitionTarget : Target {

    /**
     * The [View] used by this [Target].
     */
    val view: View

    /**
     * The [view]'s current [Drawable].
     */
    val drawable: Drawable?
}
