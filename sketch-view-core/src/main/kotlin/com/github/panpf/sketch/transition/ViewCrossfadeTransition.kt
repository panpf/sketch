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
import com.github.panpf.sketch.asDrawable
import com.github.panpf.sketch.asImage
import com.github.panpf.sketch.drawable.CrossfadeDrawable
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.ImageResult
import com.github.panpf.sketch.source.DataFrom.MEMORY_CACHE
import com.github.panpf.sketch.target.TransitionViewTarget
import com.github.panpf.sketch.util.asOrNull

/**
 * A [Transition] that crossfades from the current drawable to a new one.
 *
 * @param durationMillis The duration of the animation in milliseconds.
 * @param preferExactIntrinsicSize See [CrossfadeDrawable.preferExactIntrinsicSize].
 *
 * @see com.github.panpf.sketch.view.core.test.transition.ViewCrossfadeTransitionTest
 */
class ViewCrossfadeTransition @JvmOverloads constructor(
    private val sketch: Sketch,
    private val request: ImageRequest,
    private val target: TransitionViewTarget,
    private val result: ImageResult,
    val durationMillis: Int = CrossfadeTransition.DEFAULT_DURATION_MILLIS,
    val fadeStart: Boolean = CrossfadeTransition.DEFAULT_FADE_START,
    val preferExactIntrinsicSize: Boolean = CrossfadeTransition.DEFAULT_PREFER_EXACT_INTRINSIC_SIZE,
    val fitScale: Boolean = true,
) : Transition {

    init {
        require(durationMillis > 0) { "durationMillis must be > 0." }
    }

    override fun transition() {
        val startDrawable = target.drawable?.asOrNull<CrossfadeDrawable>()?.end ?: target.drawable
        val endDrawable = result.image?.asDrawable()
        if (startDrawable === endDrawable) {
            return
        }

        val crossfadeDrawable = CrossfadeDrawable(
            start = startDrawable,
            end = endDrawable,
            fitScale = fitScale,
            fadeStart = fadeStart,
            durationMillis = durationMillis,
            preferExactIntrinsicSize = preferExactIntrinsicSize
        )
        when (result) {
            is ImageResult.Success -> target.onSuccess(
                sketch = sketch,
                request = request,
                result = crossfadeDrawable.asImage()
            )

            is ImageResult.Error -> target.onError(
                sketch = sketch,
                request = request,
                error = crossfadeDrawable.asImage()
            )
        }
    }

    class Factory @JvmOverloads constructor(
        val durationMillis: Int = CrossfadeTransition.DEFAULT_DURATION_MILLIS,
        val fadeStart: Boolean = CrossfadeTransition.DEFAULT_FADE_START,
        val preferExactIntrinsicSize: Boolean = CrossfadeTransition.DEFAULT_PREFER_EXACT_INTRINSIC_SIZE,
        val alwaysUse: Boolean = CrossfadeTransition.DEFAULT_ALWAYS_USE,
    ) : Transition.Factory {

        override val key: String = "ViewCrossfadeTransition.Factory(" +
                "durationMillis=$durationMillis," +
                "fadeStart=$fadeStart," +
                "preferExactIntrinsicSize=$preferExactIntrinsicSize," +
                "alwaysUse=$alwaysUse)"

        init {
            require(durationMillis > 0) { "durationMillis must be > 0." }
        }

        override fun create(
            sketch: Sketch,
            request: ImageRequest,
            target: TransitionTarget,
            result: ImageResult,
        ): Transition? {
            if (target !is TransitionViewTarget) {
                return null
            }
            val fromMemoryCache = result.asOrNull<ImageResult.Success>()?.dataFrom == MEMORY_CACHE
            if (!alwaysUse && fromMemoryCache) {
                return null
            }
            val fitScale = target.fitScale
            return ViewCrossfadeTransition(
                sketch = sketch,
                request = request,
                target = target,
                result = result,
                durationMillis = durationMillis,
                fadeStart = fadeStart,
                preferExactIntrinsicSize = preferExactIntrinsicSize,
                fitScale = fitScale
            )
        }

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

        override fun toString(): String = "ViewCrossfadeTransition.Factory(" +
                "durationMillis=$durationMillis, " +
                "fadeStart=$fadeStart, " +
                "preferExactIntrinsicSize=$preferExactIntrinsicSize, " +
                "alwaysUse=$alwaysUse)"
    }
}
