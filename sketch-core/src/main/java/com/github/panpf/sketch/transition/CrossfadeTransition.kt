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
package com.github.panpf.sketch.transition

import com.github.panpf.sketch.datasource.DataFrom.MEMORY_CACHE
import com.github.panpf.sketch.drawable.internal.CrossfadeDrawable
import com.github.panpf.sketch.request.DisplayResult
import com.github.panpf.sketch.util.asOrNull

/**
 * A [Transition] that crossfades from the current drawable to a new one.
 *
 * @param durationMillis The duration of the animation in milliseconds.
 * @param preferExactIntrinsicSize See [CrossfadeDrawable.preferExactIntrinsicSize].
 */
class CrossfadeTransition @JvmOverloads constructor(
    private val target: TransitionDisplayTarget,
    private val result: DisplayResult,
    val durationMillis: Int = CrossfadeDrawable.DEFAULT_DURATION,
    val fadeStart: Boolean = true,
    val preferExactIntrinsicSize: Boolean = false,
    val fitScale: Boolean = true,
) : Transition {

    init {
        require(durationMillis > 0) { "durationMillis must be > 0." }
    }

    override fun transition() {
        val startDrawable = target.drawable?.asOrNull<CrossfadeDrawable>()?.end ?: target.drawable
        val endDrawable = result.drawable?.asOrNull<CrossfadeDrawable>()?.end ?: result.drawable
        if (startDrawable === endDrawable) {
            return
        }

        val drawable = CrossfadeDrawable(
            start = startDrawable,
            end = endDrawable,
            fitScale = fitScale,
            fadeStart = fadeStart,
            durationMillis = durationMillis,
            preferExactIntrinsicSize = preferExactIntrinsicSize
        )
        when (result) {
            is DisplayResult.Success -> target.onSuccess(drawable)
            is DisplayResult.Error -> target.onError(drawable)
        }
    }

    class Factory @JvmOverloads constructor(
        val durationMillis: Int = CrossfadeDrawable.DEFAULT_DURATION,
        val fadeStart: Boolean = true,
        val preferExactIntrinsicSize: Boolean = false,
        val alwaysUse: Boolean = false,
    ) : Transition.Factory {

        init {
            require(durationMillis > 0) { "durationMillis must be > 0." }
        }

        override fun create(
            target: TransitionDisplayTarget,
            result: DisplayResult,
            fitScale: Boolean
        ): Transition? {
            val fromMemoryCache = result.asOrNull<DisplayResult.Success>()?.dataFrom == MEMORY_CACHE
            return if (alwaysUse || !fromMemoryCache) {
                CrossfadeTransition(
                    target = target,
                    result = result,
                    durationMillis = durationMillis,
                    fadeStart = fadeStart,
                    preferExactIntrinsicSize = preferExactIntrinsicSize,
                    fitScale = fitScale
                )
            } else {
                null
            }
        }

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
