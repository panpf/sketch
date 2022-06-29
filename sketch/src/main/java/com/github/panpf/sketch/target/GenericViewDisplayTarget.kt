package com.github.panpf.sketch.target

import android.graphics.drawable.Animatable
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.github.panpf.sketch.transition.TransitionDisplayTarget
import com.github.panpf.sketch.util.asOrNull

/**
 * An opinionated [ViewDisplayTarget] that simplifies updating the [Drawable] attached to a [View]
 * and supports automatically starting and stopping animated [Drawable]s.
 *
 * If you need custom behaviour that this class doesn't support it's recommended
 * to implement [ViewDisplayTarget] directly.
 */
abstract class GenericViewDisplayTarget<T : View> : ViewDisplayTarget<T>, TransitionDisplayTarget,
    DefaultLifecycleObserver {

    private var isStarted = false

    /**
     * The current [Drawable] attached to [view].
     */
    abstract override var drawable: Drawable?

    override fun onStart(placeholder: Drawable?) = updateDrawable(placeholder)

    override fun onError(error: Drawable?) = updateDrawable(error)

    override fun onSuccess(result: Drawable) = updateDrawable(result)

    override fun onStart(owner: LifecycleOwner) {
        isStarted = true
        updateAnimation()
    }

    override fun onStop(owner: LifecycleOwner) {
        isStarted = false
        updateAnimation()
    }

    /** Replace the [ImageView]'s current drawable with [drawable]. */
    private fun updateDrawable(drawable: Drawable?) {
        this.drawable.asOrNull<Animatable>()?.stop()
        this.drawable = drawable
        updateAnimation()
    }

    /** Start/stop the current [Drawable]'s animation based on the current lifecycle state. */
    private fun updateAnimation() {
        val animatable = this.drawable.asOrNull<Animatable>() ?: return
        if (isStarted) animatable.start() else animatable.stop()
    }
}
