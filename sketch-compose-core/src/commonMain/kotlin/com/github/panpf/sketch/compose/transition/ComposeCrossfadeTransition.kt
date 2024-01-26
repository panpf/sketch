/*
 * Copyright (C) 2023 panpf <panpfpanpf@outlook.com>
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
package com.github.panpf.sketch.compose.transition

import androidx.compose.ui.graphics.painter.Painter
import com.github.panpf.sketch.compose.asPainter
import com.github.panpf.sketch.compose.asSketchImage
import com.github.panpf.sketch.compose.painter.CrossfadePainter
import com.github.panpf.sketch.compose.internal.asOrNull
import com.github.panpf.sketch.datasource.DataFrom.MEMORY_CACHE
import com.github.panpf.sketch.request.ImageResult
import com.github.panpf.sketch.request.internal.RequestContext
import com.github.panpf.sketch.transition.Transition
import com.github.panpf.sketch.transition.TransitionTarget

class ComposeCrossfadeTransition @JvmOverloads constructor(
    private val requestContext: RequestContext,
    private val target: TransitionComposeTarget,
    private val result: ImageResult,
    val durationMillis: Int = Transition.DEFAULT_DURATION,
    val fadeStart: Boolean = true,
    val preferExactIntrinsicSize: Boolean = false,
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
                requestContext,
                crossfadePainter.asSketchImage()
            )

            is ImageResult.Error -> target.onError(requestContext, crossfadePainter.asSketchImage())
        }
    }

    class Factory @JvmOverloads constructor(
        val durationMillis: Int = Transition.DEFAULT_DURATION,
        val fadeStart: Boolean = true,
        val preferExactIntrinsicSize: Boolean = false,
        val alwaysUse: Boolean = false,
    ) : Transition.Factory {

        init {
            require(durationMillis > 0) { "durationMillis must be > 0." }
        }

        override fun create(
            requestContext: RequestContext,
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
                requestContext = requestContext,
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
            return "ComposeCrossfadeTransition.Factory(durationMillis=$durationMillis, fadeStart=$fadeStart, preferExactIntrinsicSize=$preferExactIntrinsicSize, alwaysUse=$alwaysUse)"
        }
    }
}