/*
 * Copyright (C) 2022 panpf <panpfpanpf@outlook.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.panpf.sketch.request

import android.view.View
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.findViewTreeLifecycleOwner
import com.github.panpf.sketch.util.findLifecycle
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
        // There is no need to judge whether to attach to the window here,
        // because lifecycle() will only be executed after attached.
        val view1 = viewReference.get()
        return resolveLifecycle(view1)
    }

    private fun resolveLifecycle(view: View?): Lifecycle {
        // findViewTreeLifecycleOwner can only return the correct Lifecycle after the view is attached to the window
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