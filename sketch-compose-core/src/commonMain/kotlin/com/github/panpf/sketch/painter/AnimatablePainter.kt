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

package com.github.panpf.sketch.painter

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver

/**
 * Interface that painters supporting animations should implement.
 */
interface AnimatablePainter {
    /**
     * Starts the drawable's animation.
     */
    fun start()

    /**
     * Stops the drawable's animation.
     */
    fun stop()

    /**
     * Indicates whether the animation is running.
     *
     * @return True if the animation is running, false otherwise.
     */
    fun isRunning(): Boolean
}

/**
 * Starts the animation when the lifecycle is in [Lifecycle.Event.ON_START] state and stops it when the lifecycle is in [Lifecycle.Event.ON_STOP] state.
 */
@Composable
fun AnimatablePainter.startWithLifecycle() {
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val animatablePainter =
        remember(this) { this } // SkiaAnimatedImagePainter needs to trigger onRemembered
    DisposableEffect(animatablePainter) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_START) {
                animatablePainter.start()
            } else if (event == Lifecycle.Event.ON_STOP) {
                animatablePainter.stop()
            }
        }
        // if the LifecycleOwner is in [State.STARTED] state, the given observer * will receive [Event.ON_CREATE], [Event.ON_START] events.
        lifecycle.addObserver(observer)
        onDispose {
            lifecycle.removeObserver(observer)
        }
    }
}