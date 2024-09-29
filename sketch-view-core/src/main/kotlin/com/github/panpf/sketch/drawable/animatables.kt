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

package com.github.panpf.sketch.drawable

import android.graphics.drawable.Animatable
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver


/**
 * Start the animation when the LifecycleOwner is in the [Lifecycle.Event.ON_START] state, and stop the animation when the LifecycleOwner is in the [Lifecycle.Event.ON_STOP] state
 *
 * @see com.github.panpf.sketch.view.core.test.drawable.AnimatablesTest.testStartWithLifecycle
 * @see com.github.panpf.sketch.view.core.test.drawable.AnimatablesTest.testStartWithLifecycle2
 */
fun Animatable.startWithLifecycle(lifecycle: Lifecycle) {
    val observer = LifecycleEventObserver { _, event ->
        if (event == Lifecycle.Event.ON_START) {
            start()
        } else if (event == Lifecycle.Event.ON_STOP) {
            stop()
        }
    }
    // if the LifecycleOwner is in [State.STARTED] state, the given observer * will receive [Event.ON_CREATE], [Event.ON_START] events.
    lifecycle.addObserver(observer)
}