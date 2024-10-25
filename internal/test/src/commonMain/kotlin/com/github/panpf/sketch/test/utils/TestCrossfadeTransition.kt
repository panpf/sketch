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

import com.github.panpf.sketch.Image
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.ImageResult
import com.github.panpf.sketch.source.DataFrom.MEMORY_CACHE
import com.github.panpf.sketch.transition.CrossfadeTransition
import com.github.panpf.sketch.transition.Transition
import com.github.panpf.sketch.transition.TransitionTarget

class TestCrossfadeTransition(
    private val sketch: Sketch,
    private val request: ImageRequest,
    val target: TransitionTarget,
    val result: ImageResult
) : Transition {

    override fun transition() {
        when (result) {
            is ImageResult.Success -> target.onSuccess(
                sketch = sketch,
                request = request,
                result = TestCrossfadeImage(result.image)
            )

            is ImageResult.Error -> target.onError(
                sketch = sketch,
                request = request,
                error = result.image?.let { TestCrossfadeImage(it) })
        }
    }

    class Factory(
        val durationMillis: Int = CrossfadeTransition.DEFAULT_DURATION_MILLIS,
        val fadeStart: Boolean = CrossfadeTransition.DEFAULT_FADE_START,
        val preferExactIntrinsicSize: Boolean = CrossfadeTransition.DEFAULT_PREFER_EXACT_INTRINSIC_SIZE,
        val alwaysUse: Boolean = CrossfadeTransition.DEFAULT_ALWAYS_USE,
    ) : Transition.Factory {

        override val key: String = "TestTransition.Factory"

        override fun create(
            sketch: Sketch,
            request: ImageRequest,
            target: TransitionTarget,
            result: ImageResult,
        ): Transition? {
            if (target !is TestTransitionTarget) {
                return null
            }
            val fromMemoryCache = result.asOrNull<ImageResult.Success>()?.dataFrom == MEMORY_CACHE
            if (!alwaysUse && fromMemoryCache) {
                return null
            }
            return TestCrossfadeTransition(sketch, request, target, result)
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other == null || this::class != other::class) return false
            return true
        }

        override fun hashCode(): Int {
            return this::class.hashCode()
        }

        override fun toString(): String {
            return "TestTransition"
        }
    }
}

data class TestCrossfadeImage constructor(val image: Image) : Image by image {
    override fun toString(): String {
        return "TestCrossfadeImage(image=$image)"
    }
}