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
package com.github.panpf.sketch.sample.util

import android.view.View
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.github.panpf.sketch.sample.R
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.launch

fun <T> Flow<T>.repeatObserveWithLifecycle(
    owner: LifecycleOwner,
    minState: Lifecycle.State,
    collector: FlowCollector<T>
): Job {
    require(minState != Lifecycle.State.CREATED)
    return owner.lifecycleScope.launch {
        owner.repeatOnLifecycle(minState) {
            collect(collector)
        }
    }
}

val View.lifecycleOwner: LifecycleOwner
    get() {
        synchronized(this) {
            return (getTag(R.id.tagId_viewLifecycle) as ViewLifecycleOwner?)
                ?: ViewLifecycleOwner(this).apply {
                    setTag(R.id.tagId_viewLifecycle, this)
                }
        }
    }

class ViewLifecycleOwner(view: View) : LifecycleOwner {

    private val lifecycleRegistry = LifecycleRegistry(this)

    init {
        view.addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
            override fun onViewAttachedToWindow(v: View) {
                lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
                lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_START)
                lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
            }

            override fun onViewDetachedFromWindow(v: View) {
                lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_PAUSE)
                lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_STOP)
                lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
                // The LifecycleRegistry that has been destroyed can no longer be used, and a new one must be created
                view.setTag(R.id.tagId_viewLifecycle, null)
            }
        })
    }

    override fun getLifecycle(): Lifecycle {
        return lifecycleRegistry
    }
}