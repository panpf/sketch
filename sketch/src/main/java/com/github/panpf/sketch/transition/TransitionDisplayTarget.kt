package com.github.panpf.sketch.transition

import android.graphics.drawable.Drawable
import com.github.panpf.sketch.target.DisplayTarget
import com.github.panpf.sketch.target.Target

/**
 * A [Target] that supports applying [Transition]s.
 */
interface TransitionDisplayTarget : DisplayTarget {

    /**
     * The current [Drawable].
     */
    val drawable: Drawable?
}
