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
package com.github.panpf.sketch.test.utils

import com.github.panpf.sketch.lifecycle.PlatformLifecycle
import com.github.panpf.sketch.lifecycle.PlatformLifecycleEventObserver
import com.github.panpf.sketch.lifecycle.PlatformLifecycleObserver
import com.github.panpf.sketch.lifecycle.PlatformLifecycleOwner

/**
 * A [PlatformLifecycle] implementation that is always resumed and never destroyed.
 */
data object TestGlobalPlatformLifecycle : PlatformLifecycle() {

    private val owner = TestPlatformLifecycleOwner(this)

    override val currentState: State
        get() = State.RESUMED

    override fun addObserver(observer: PlatformLifecycleObserver) {
        require(observer is PlatformLifecycleEventObserver) {
            "Observer must implement PlatformLifecycleEventObserver"
        }
        // Call the PlatformLifecycle methods in order and do not hold a reference to the observer.
        observer.onStateChanged(owner, Event.ON_CREATE)
        observer.onStateChanged(owner, Event.ON_START)
        observer.onStateChanged(owner, Event.ON_RESUME)
    }

    override fun removeObserver(observer: PlatformLifecycleObserver) {
        require(observer is PlatformLifecycleEventObserver) {
            "Observer must implement PlatformLifecycleEventObserver"
        }
    }

}

class TestPlatformLifecycleOwner(lifecycle: TestGlobalPlatformLifecycle) : PlatformLifecycleOwner {
    override val lifecycle: PlatformLifecycle = lifecycle
}