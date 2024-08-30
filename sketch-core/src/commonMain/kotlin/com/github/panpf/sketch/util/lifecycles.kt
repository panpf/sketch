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

package com.github.panpf.sketch.util

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleObserver
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

/**
 * Suspend until [Lifecycle.currentState] is at least [Lifecycle.State.STARTED]
 *
 * @see com.github.panpf.sketch.core.common.test.util.LifecyclesTest.testAwaitStarted
 */
internal suspend fun Lifecycle.awaitStarted() {
    // Fast path: we're already started.
    Lifecycle.State.STARTED
    if (currentState.isAtLeast(Lifecycle.State.STARTED)) return

    // Slow path: observe the lifecycle until we're started.
    var observer: LifecycleEventObserver? = null
    try {
        suspendCancellableCoroutine { continuation ->
            observer = LifecycleEventObserver { _, event ->
                if (event == Lifecycle.Event.ON_START) {
                    continuation.resume(Unit)
                }
            }
            addObserver(observer!!)
        }
    } finally {
        // 'observer' will always be null if this method is marked as 'inline'.
        observer?.let(::removeObserver)
    }
}

/**
 * Remove and re-add the observer to ensure all its lifecycle callbacks are invoked.
 *
 * @see com.github.panpf.sketch.core.common.test.util.LifecyclesTest.testRemoveAndAddObserver
 */
fun Lifecycle.removeAndAddObserver(observer: LifecycleObserver) {
    removeObserver(observer)
    addObserver(observer)
}