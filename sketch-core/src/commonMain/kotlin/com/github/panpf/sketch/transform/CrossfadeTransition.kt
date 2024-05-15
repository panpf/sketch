/*
 * Copyright 2023 Coil Contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * ------------------------------------------------------------------------
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
package com.github.panpf.sketch.transform

import com.github.panpf.sketch.request.ImageResult
import com.github.panpf.sketch.request.internal.RequestContext
import com.github.panpf.sketch.source.DataFrom.MEMORY_CACHE
import com.github.panpf.sketch.transition.Transition
import com.github.panpf.sketch.transition.TransitionTarget
import com.github.panpf.sketch.util.asOrNull
import kotlin.jvm.JvmOverloads

class CrossfadeTransition(transition: Transition) : Transition by transition {

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
            requestContext: RequestContext,
            target: TransitionTarget,
            result: ImageResult,
        ): Transition? {
            val fromMemoryCache = result.asOrNull<ImageResult.Success>()?.dataFrom == MEMORY_CACHE
            if (!alwaysUse && fromMemoryCache) return null
            val targetCrossfadeTransitionFactory =
                target.getTargetCrossfadeTransitionFactory(this)
            val targetCrossfadeTransition = targetCrossfadeTransitionFactory
                ?.create(requestContext, target, result)
                ?: return null
            return CrossfadeTransition(targetCrossfadeTransition)
        }

        override val key: String =
            "CrossfadeTransition.Factory(durationMillis=$durationMillis,fadeStart=$fadeStart,preferExactIntrinsicSize=$preferExactIntrinsicSize,alwaysUse=$alwaysUse)"

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            return other is Factory
                    && durationMillis == other.durationMillis
                    && fadeStart == other.fadeStart
                    && preferExactIntrinsicSize == other.preferExactIntrinsicSize
                    && alwaysUse == other.alwaysUse
        }

        override fun hashCode(): Int {
            var result = durationMillis
            result = 31 * result + fadeStart.hashCode()
            result = 31 * result + preferExactIntrinsicSize.hashCode()
            result = 31 * result + alwaysUse.hashCode()
            return result
        }

        override fun toString(): String {
            return "CrossfadeTransition.Factory(durationMillis=$durationMillis, fadeStart=$fadeStart, preferExactIntrinsicSize=$preferExactIntrinsicSize, alwaysUse=$alwaysUse)"
        }
    }
}