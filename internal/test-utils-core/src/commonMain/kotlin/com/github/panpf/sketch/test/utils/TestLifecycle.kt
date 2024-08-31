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

package com.github.panpf.sketch.test.utils

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner

/**
 * A [Lifecycle] implementation that is always resumed and never destroyed.
 */
class TestLifecycle : Lifecycle() {

    private val owner = TestLifecycleOwner(this)

    override var currentState: State = State.INITIALIZED

    private val observers = mutableListOf<LifecycleEventObserver>()

    override fun addObserver(observer: LifecycleObserver) {
        require(observer is LifecycleEventObserver) {
            "Observer must implement LifecycleEventObserver"
        }
        observers.add(observer)
        if (currentState >= State.CREATED) {
            observer.onStateChanged(owner, Event.ON_CREATE)
        }
        if (currentState >= State.STARTED) {
            observer.onStateChanged(owner, Event.ON_START)
        }
        if (currentState >= State.RESUMED) {
            observer.onStateChanged(owner, Event.ON_RESUME)
        }
    }

    override fun removeObserver(observer: LifecycleObserver) {
        require(observer is LifecycleEventObserver) {
            "Observer must implement LifecycleEventObserver"
        }
        observers.remove(observer)
    }
}

class TestLifecycleOwner(lifecycle: TestLifecycle) : LifecycleOwner {
    override val lifecycle: Lifecycle = lifecycle
}