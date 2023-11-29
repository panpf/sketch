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
        val view1 = viewReference.get() // Avoid memory leaks
        if (view1 == null || ViewCompat.isAttachedToWindow(view1)) {
            @Suppress("UnnecessaryVariable")    // for debug
            val lifecycle1 = resolveLifecycle(view1)
            return lifecycle1
        }

        return suspendCancellableCoroutine { continuation ->
            val view2 = viewReference.get() // Avoid memory leaks
            if (view2 != null) {
                if (ViewCompat.isAttachedToWindow(view2)) {
                    val lifecycle2 = resolveLifecycle(view2)
                    continuation.resumeWith(Result.success(lifecycle2))
                } else {
                    val attachStateChangeListener = object : OnAttachStateChangeListener {
                        override fun onViewAttachedToWindow(view3: View) {
                            view3.removeOnAttachStateChangeListener(this)
                            val lifecycle3 = resolveLifecycle(view3)
                            continuation.resumeWith(Result.success(lifecycle3))
                        }

                        override fun onViewDetachedFromWindow(view3: View) {}
                    }
                    view2.addOnAttachStateChangeListener(attachStateChangeListener)

                    continuation.invokeOnCancellation {
                        val view4 = viewReference.get() // Avoid memory leaks
                        view4?.removeOnAttachStateChangeListener(attachStateChangeListener)
                    }
                }
            } else {
                continuation.cancel()
            }
        }
    }

    private fun resolveLifecycle(view: View?): Lifecycle {
        return view?.findViewTreeLifecycleOwner()?.lifecycle
            ?: view?.context.findLifecycle()
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