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
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.ImageResult
import com.github.panpf.sketch.source.DataFrom.MEMORY_CACHE
import com.github.panpf.sketch.util.asOrNull
import kotlin.jvm.JvmOverloads

/**
 * A [Transition] that crossfades between the previous and new image.
 *
 * @see com.github.panpf.sketch.core.common.test.transition.CrossfadeTransitionTest
 */
class CrossfadeTransition(
    val wrappedTransition: Transition
) : Transition by wrappedTransition {

    companion object {
        const val DEFAULT_DURATION_MILLIS: Int = Transition.DEFAULT_DURATION
        const val DEFAULT_FADE_START: Boolean = true
        const val DEFAULT_PREFER_EXACT_INTRINSIC_SIZE: Boolean = false
        const val DEFAULT_ALWAYS_USE: Boolean = false
    }

    class Factory @JvmOverloads constructor(
        val durationMillis: Int = DEFAULT_DURATION_MILLIS,
        val fadeStart: Boolean = DEFAULT_FADE_START,
        val preferExactIntrinsicSize: Boolean = DEFAULT_PREFER_EXACT_INTRINSIC_SIZE,
        val alwaysUse: Boolean = DEFAULT_ALWAYS_USE,
    ) : Transition.Factory {

        init {
            require(durationMillis > 0) { "durationMillis must be > 0." }
        }

        override fun create(
            sketch: Sketch,
            request: ImageRequest,
            target: TransitionTarget,
            result: ImageResult,
        ): Transition? {
            val fromMemoryCache = result.asOrNull<ImageResult.Success>()?.dataFrom == MEMORY_CACHE
            if (!alwaysUse && fromMemoryCache) return null
            val targetTransitionFactory = target.convertTransition(this) ?: return null
            val targetTransition = targetTransitionFactory
                .create(sketch, request, target, result) ?: return null
            return CrossfadeTransition(targetTransition)
        }

        override val key: String = "CrossfadeTransition.Factory(" +
                "durationMillis=$durationMillis," +
                "fadeStart=$fadeStart," +
                "preferExactIntrinsicSize=$preferExactIntrinsicSize," +
                "alwaysUse=$alwaysUse)"

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other == null || this::class != other::class) return false
            other as Factory
            if (durationMillis != other.durationMillis) return false
            if (fadeStart != other.fadeStart) return false
            if (preferExactIntrinsicSize != other.preferExactIntrinsicSize) return false
            if (alwaysUse != other.alwaysUse) return false
            return true
        }

        override fun hashCode(): Int {
            var result = durationMillis
            result = 31 * result + fadeStart.hashCode()
            result = 31 * result + preferExactIntrinsicSize.hashCode()
            result = 31 * result + alwaysUse.hashCode()
            return result
        }

        override fun toString(): String = "CrossfadeTransition.Factory(" +
                "durationMillis=$durationMillis, " +
                "fadeStart=$fadeStart, " +
                "preferExactIntrinsicSize=$preferExactIntrinsicSize, " +
                "alwaysUse=$alwaysUse)"
    }
}