package com.github.panpf.sketch.sample.ui.util

import android.view.View
import androidx.core.view.ViewCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import com.github.panpf.sketch.sample.R


val View.lifecycleOwner: LifecycleOwner
    get() {
        synchronized(this) {
            /*
             * After Lifecycle enters the DESTROYED state, its coroutineScope will be canceled and can no longer be used.
             * So you must create a new Lifecycle every time onAttachedToWindow
             */
            check(ViewCompat.isAttachedToWindow(this)) {
                "View.lifecycleOwner can only be called after onAttachedToWindow and before onDetachedFromWindow"
            }
            val tag = getTag(R.id.tagId_viewLifecycle)
            if (tag != null && tag is ViewLifecycleOwner) {
                return tag
            } else {
                val viewLifecycleOwner = ViewLifecycleOwner(this)
                setTag(R.id.tagId_viewLifecycle, viewLifecycleOwner)
                return viewLifecycleOwner
            }
        }
    }

class ViewLifecycleOwner(view: View) : LifecycleOwner {

    private val lifecycleRegistry = LifecycleRegistry(this)

    override val lifecycle: Lifecycle
        get() = lifecycleRegistry

    init {
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_START)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)

        view.addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
            override fun onViewAttachedToWindow(v: View) {
            }

            override fun onViewDetachedFromWindow(v: View) {
                lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_PAUSE)
                lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_STOP)
                lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)

                /*
                 * After Lifecycle enters the DESTROYED state, its coroutineScope will be canceled and can no longer be used.
                 * So you must create a new Lifecycle every time onAttachedToWindow
                 */
                view.setTag(R.id.tagId_viewLifecycle, null)
                view.removeOnAttachStateChangeListener(this)
            }
        })
    }
}