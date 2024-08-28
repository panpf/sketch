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

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner

/**
 * A [Lifecycle] implementation that is always resumed and never destroyed.
 *
 * This is used as a fallback if [Lifecycle] is not specified and cannot be obtained from the Target or Context.
 */
data object GlobalLifecycle : Lifecycle() {

    val owner = object : LifecycleOwner {
        override val lifecycle get() = this@GlobalLifecycle
    }

    override val currentState: State
        get() = State.RESUMED

    override fun addObserver(observer: LifecycleObserver) {
        require(observer is LifecycleEventObserver) {
            "Observer must implement LifecycleEventObserver"
        }
        // Call the lifecycle methods in order and do not hold a reference to the observer.
        observer.onStateChanged(owner, Event.ON_CREATE)
        observer.onStateChanged(owner, Event.ON_START)
        observer.onStateChanged(owner, Event.ON_RESUME)
    }

    override fun removeObserver(observer: LifecycleObserver) {
        require(observer is LifecycleEventObserver) {
            "Observer must implement LifecycleEventObserver"
        }
    }

    override fun toString() = "GlobalLifecycle"
}