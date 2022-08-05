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
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.github.panpf.sketch.sample.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.launch

fun <T> Flow<T>.observeWithLifecycle(
    owner: LifecycleOwner,
    minState: Lifecycle.State,
    collector: FlowCollector<T>
) {
    owner.lifecycleScope.launch {
        owner.repeatOnLifecycle(minState) {
            collect(collector)
        }
    }
}

fun <T> Flow<T>.observeWithStartedLifecycle(
    owner: LifecycleOwner,
    collector: FlowCollector<T>
) {
    observeWithLifecycle(owner, Lifecycle.State.STARTED, collector)
}

fun <T> Flow<T>.observeWithFragmentView(
    fragment: Fragment,
    collector: FlowCollector<T>
) {
    observeWithLifecycle(fragment.viewLifecycleOwner, Lifecycle.State.STARTED, collector)
}

fun <T> Flow<T>.observeWithViewLifecycle(view: View, collector: FlowCollector<T>) {
    val scope = (view.getTag(R.id.tagId_viewCoroutineScope) as CoroutineScope?)
        ?: CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate).apply {
            view.setTag(R.id.tagId_viewCoroutineScope, this)
        }
    if (ViewCompat.isAttachedToWindow(view)) {
        scope.launch {
            this@observeWithViewLifecycle.collect(collector)
        }
    }
    view.addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
        override fun onViewAttachedToWindow(v: View?) {
            scope.launch {
                this@observeWithViewLifecycle.collect(collector)
            }
        }

        override fun onViewDetachedFromWindow(v: View?) {
            scope.cancel()
            view.setTag(R.id.tagId_viewCoroutineScope, null)
        }
    })
}