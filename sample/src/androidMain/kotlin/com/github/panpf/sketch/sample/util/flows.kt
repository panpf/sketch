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

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.coroutineScope
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.launch

fun <T> Flow<T>.collectWithLifecycle(owner: LifecycleOwner, collector: FlowCollector<T>): Job {
    return owner.lifecycleScope.launch {
        collect(collector)
    }
}

fun <T> Flow<T>.repeatCollectWithLifecycle(
    owner: LifecycleOwner,
    state: Lifecycle.State,
    collector: FlowCollector<T>
): Job {
    return owner.lifecycleScope.launch {
        owner.repeatOnLifecycle(state) {
            collect(collector)
        }
    }
}

fun <T> Flow<T>.repeatCollectWithLifecycle(
    lifecycle: Lifecycle,
    state: Lifecycle.State,
    collector: FlowCollector<T>
): Job {
    return lifecycle.coroutineScope.launch {
        lifecycle.repeatOnLifecycle(state) {
            collect(collector)
        }
    }
}