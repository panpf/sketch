/*
 * Copyright (C) 2024 panpf <panpfpanpf@outlook.com>
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

import android.content.Context
import android.content.ContextWrapper
import android.view.View
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.findViewTreeLifecycleOwner
import java.lang.ref.WeakReference

/**
 * Find the Lifecycle of the Context
 *
 * @see com.github.panpf.sketch.view.core.test.request.ViewLifecycleResolverTest.testFindLifecycle
 */
internal fun Context.findLifecycle(): Lifecycle? = when (this) {
    is LifecycleOwner -> this.lifecycle
    is ContextWrapper -> this.baseContext.findLifecycle()
    else -> null
}

/**
 * Resolve the Lifecycle of the View
 *
 * @see com.github.panpf.sketch.view.core.test.request.ViewLifecycleResolverTest
 */
class ViewLifecycleResolver constructor(
    val viewReference: WeakReference<View>
) : LifecycleResolver {

    constructor(view: View) : this(WeakReference(view))

    override suspend fun lifecycle(): Lifecycle {
        // There is no need to judge whether to attach to the window here,
        // because lifecycle() will only be executed after attached.
        val view1 = viewReference.get()
        val lifecycle = resolveLifecycle(view1)
        return lifecycle ?: GlobalLifecycle
    }

    private fun resolveLifecycle(view: View?): Lifecycle? {
        // findViewTreeLifecycleOwner can only return the correct Lifecycle after the view is attached to the window
        return view?.findViewTreeLifecycleOwner()?.lifecycle
            ?: view?.context?.findLifecycle()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
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