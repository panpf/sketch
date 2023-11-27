package com.github.panpf.sketch.request

import android.view.View
import android.view.View.OnAttachStateChangeListener
import androidx.core.view.ViewCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.findViewTreeLifecycleOwner
import com.github.panpf.sketch.util.findLifecycle
import kotlinx.coroutines.suspendCancellableCoroutine
import java.lang.ref.WeakReference

fun LifecycleResolver(lifecycle: Lifecycle): LifecycleResolver = FixedLifecycleResolver(lifecycle)

fun interface LifecycleResolver {

    suspend fun lifecycle(): Lifecycle
}

class FixedLifecycleResolver constructor(val lifecycle: Lifecycle) :
    LifecycleResolver {

    override suspend fun lifecycle(): Lifecycle = lifecycle

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as FixedLifecycleResolver
        return lifecycle == other.lifecycle
    }

    override fun hashCode(): Int {
        return lifecycle.hashCode()
    }

    override fun toString(): String = "FixedLifecycleResolver($lifecycle)"
}

class ViewLifecycleResolver constructor(
    val viewReference: WeakReference<View>
) : LifecycleResolver {

    constructor(view: View) : this(WeakReference(view))

    override suspend fun lifecycle(): Lifecycle {
        val view = viewReference.get() ?: return GlobalLifecycle
        if (ViewCompat.isAttachedToWindow(view)) {
            return getLifecycleFromView(view)
        }

        return suspendCancellableCoroutine { continuation ->
            val attachStateChangeListener = object : OnAttachStateChangeListener {
                override fun onViewAttachedToWindow(view1: View) {
                    view1.removeOnAttachStateChangeListener(this)
                    continuation.resumeWith(Result.success(getLifecycleFromView(view1)))
                }

                override fun onViewDetachedFromWindow(view1: View) {}
            }
            view.addOnAttachStateChangeListener(attachStateChangeListener)
            continuation.invokeOnCancellation {
                view.removeOnAttachStateChangeListener(attachStateChangeListener)
            }
        }
    }

    private fun getLifecycleFromView(view: View): Lifecycle {
        return view.findViewTreeLifecycleOwner()?.lifecycle ?: view.context.findLifecycle()
        ?: GlobalLifecycle
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as ViewLifecycleResolver
        return viewReference.get() == other.viewReference.get()
    }

    override fun hashCode(): Int {
        return viewReference.get().hashCode()
    }

    override fun toString(): String {
        return "ViewLifecycleResolver(${viewReference.get()})"
    }
}

open class LifecycleResolverWrapper(
    val wrapped: LifecycleResolver
) : LifecycleResolver by wrapped {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as LifecycleResolverWrapper
        return wrapped == other.wrapped
    }

    override fun hashCode(): Int {
        return wrapped.hashCode()
    }

    override fun toString(): String {
        return "LifecycleResolverWrapper($wrapped)"
    }
}

class DefaultLifecycleResolver(
    wrapped: LifecycleResolver
) : LifecycleResolverWrapper(wrapped) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as DefaultLifecycleResolver
        return wrapped == other.wrapped
    }

    override fun hashCode(): Int {
        return wrapped.hashCode()
    }

    override fun toString(): String {
        return "DefaultLifecycleResolver($wrapped)"
    }
}

fun LifecycleResolver.isDefault() = this is DefaultLifecycleResolver

fun LifecycleResolver.findLeafLifecycleResolver(): LifecycleResolver =
    when (this) {
        is LifecycleResolverWrapper -> this.wrapped.findLeafLifecycleResolver()
        else -> this
    }