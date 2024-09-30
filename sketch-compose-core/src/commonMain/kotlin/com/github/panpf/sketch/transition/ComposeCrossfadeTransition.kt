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

@file:Suppress("RedundantConstructorKeyword")

package com.github.panpf.sketch.transition

import androidx.compose.ui.graphics.painter.Painter
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.asImage
import com.github.panpf.sketch.asPainter
import com.github.panpf.sketch.painter.CrossfadePainter
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.ImageResult
import com.github.panpf.sketch.source.DataFrom.MEMORY_CACHE
import com.github.panpf.sketch.util.asOrNull
import kotlin.jvm.JvmOverloads

/**
 * A [Transition] that crossfades between the previous [Painter] and the new [Painter].
 *
 * @see com.github.panpf.sketch.compose.core.common.test.transition.ComposeCrossfadeTransitionTest
 */
class ComposeCrossfadeTransition constructor(
    private val sketch: Sketch,
    private val request: ImageRequest,
    private val target: TransitionComposeTarget,
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
        val startPainter: Painter? =
            target.painter?.asOrNull<CrossfadePainter>()?.end ?: target.painter
        val endPainter: Painter? = result.image?.asPainter()
        if (startPainter === endPainter) {
            return
        }

        val crossfadePainter = CrossfadePainter(
            start = startPainter,
            end = endPainter,
            fitScale = fitScale,
            durationMillis = durationMillis,
            fadeStart = fadeStart,
            preferExactIntrinsicSize = preferExactIntrinsicSize
        )
        when (result) {
            is ImageResult.Success -> target.onSuccess(
                sketch = sketch,
                request = request,
                result = crossfadePainter.asImage()
            )

            is ImageResult.Error -> target.onError(
                sketch = sketch,
                request = request,
                error = crossfadePainter.asImage()
            )
        }
    }

    class Factory @JvmOverloads constructor(
        val durationMillis: Int = CrossfadeTransition.DEFAULT_DURATION_MILLIS,
        val fadeStart: Boolean = CrossfadeTransition.DEFAULT_FADE_START,
        val preferExactIntrinsicSize: Boolean = CrossfadeTransition.DEFAULT_PREFER_EXACT_INTRINSIC_SIZE,
        val alwaysUse: Boolean = CrossfadeTransition.DEFAULT_ALWAYS_USE,
    ) : Transition.Factory {

        override val key: String = "ComposeCrossfadeTransition.Factory(" +
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
            if (target !is TransitionComposeTarget) {
                return null
            }
            val fromMemoryCache = result.asOrNull<ImageResult.Success>()?.dataFrom == MEMORY_CACHE
            if (!alwaysUse && fromMemoryCache) {
                return null
            }
            val fitScale = target.fitScale
            return ComposeCrossfadeTransition(
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

        override fun toString(): String = "ComposeCrossfadeTransition.Factory(" +
                "durationMillis=$durationMillis, " +
                "fadeStart=$fadeStart, " +
                "preferExactIntrinsicSize=$preferExactIntrinsicSize, " +
                "alwaysUse=$alwaysUse)"
    }
}