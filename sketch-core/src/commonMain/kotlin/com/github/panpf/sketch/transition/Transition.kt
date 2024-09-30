/*
 * Copyright (C) 2024 panpf <panpfpanpf@outlook.com>
 * Copyright 2023 Coil Contributors
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

package com.github.panpf.sketch.transition

import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.annotation.MainThread
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.ImageResult
import com.github.panpf.sketch.target.Target
import com.github.panpf.sketch.util.Key

/**
 * A class to animate between a [Target]'s current drawable and the result of an image request.
 *
 * NOTE: A [Target] must implement [TransitionTarget] to support applying [Transition]s.
 * If the [Target] does not implement [TransitionTarget], any [Transition]s will be ignored.
 */
fun interface Transition {

    companion object {
        const val DEFAULT_DURATION = 200
    }

    /**
     * Start the transition animation.
     *
     * Implementations are responsible for calling the correct [Target] lifecycle callback.
     */
    @MainThread
    fun transition()

    /**
     * IMPORTANT: It is necessary to ensure compliance with the consistency principle,
     * that is, the equals() and hashCode() methods of instances created with the same
     * construction parameters return consistent results. This is important in Compose
     */
    interface Factory : Key {

        fun create(
            sketch: Sketch,
            request: ImageRequest,
            target: TransitionTarget,
            result: ImageResult,
        ): Transition?

        override fun equals(other: Any?): Boolean

        override fun hashCode(): Int

        override fun toString(): String
    }
}