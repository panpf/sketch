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

import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.asImage
import com.github.panpf.sketch.asPainter
import com.github.panpf.sketch.painter.CrossfadePainter
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.ImageResult
import com.github.panpf.sketch.source.DataFrom.MEMORY_CACHE
import com.github.panpf.sketch.target.ComposeTarget
import com.github.panpf.sketch.target.Target
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
    private val target: ComposeTarget,
    private val result: ImageResult,
    val contentScale: ContentScale = ContentScale.Fit,
    val alignment: Alignment = Alignment.Center,
    val durationMillis: Int = CrossfadeTransition.DEFAULT_DURATION_MILLIS,
    val fadeStart: Boolean = CrossfadeTransition.DEFAULT_FADE_START,
    val preferExactIntrinsicSize: Boolean = CrossfadeTransition.DEFAULT_PREFER_EXACT_INTRINSIC_SIZE,
) : Transition {

    @Deprecated("Please use a constructor containing the contentScale and alignment parameter instead")
    constructor(
        sketch: Sketch,
        request: ImageRequest,
        target: ComposeTarget,
        result: ImageResult,
        durationMillis: Int = CrossfadeTransition.DEFAULT_DURATION_MILLIS,
        fadeStart: Boolean = CrossfadeTransition.DEFAULT_FADE_START,
        preferExactIntrinsicSize: Boolean = CrossfadeTransition.DEFAULT_PREFER_EXACT_INTRINSIC_SIZE,
        fitScale: Boolean,
    ) : this(
        sketch = sketch,
        request = request,
        target = target,
        result = result,
        durationMillis = durationMillis,
        fadeStart = fadeStart,
        preferExactIntrinsicSize = preferExactIntrinsicSize,
        contentScale = if (fitScale) ContentScale.Fit else ContentScale.Crop
    )

    @Deprecated("Use contentScale and alignment instead.")
    val fitScale: Boolean = contentScale == ContentScale.Fit

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
            contentScale = contentScale,
            alignment = alignment,
            durationMillis = durationMillis,
            fadeStart = fadeStart,
            preferExactIntrinsicSize = preferExactIntrinsicSize
        )
        when (result) {
            is ImageResult.Success -> target.onSuccess(
                sketch = sketch,
                request = request,
                result = result,
                image = crossfadePainter.asImage()
            )

            is ImageResult.Error -> target.onError(
                sketch = sketch,
                request = request,
                error = result,
                image = crossfadePainter.asImage()
            )
        }
    }

    class Factory @JvmOverloads constructor(
        val durationMillis: Int = CrossfadeTransition.DEFAULT_DURATION_MILLIS,
        val fadeStart: Boolean = CrossfadeTransition.DEFAULT_FADE_START,
        val preferExactIntrinsicSize: Boolean = CrossfadeTransition.DEFAULT_PREFER_EXACT_INTRINSIC_SIZE,
        val alwaysUse: Boolean = CrossfadeTransition.DEFAULT_ALWAYS_USE,
    ) : Transition.Factory {

        override val key: String =
            "ComposeCrossfade($durationMillis,$fadeStart,$preferExactIntrinsicSize,$alwaysUse)"

        init {
            require(durationMillis > 0) { "durationMillis must be > 0." }
        }

        override fun create(
            sketch: Sketch,
            request: ImageRequest,
            target: Target,
            result: ImageResult,
        ): Transition? {
            if (target !is ComposeTarget) {
                return null
            }
            val fromMemoryCache = result.asOrNull<ImageResult.Success>()?.dataFrom == MEMORY_CACHE
            if (!alwaysUse && fromMemoryCache) {
                return null
            }
            return ComposeCrossfadeTransition(
                sketch = sketch,
                request = request,
                target = target,
                result = result,
                contentScale = target.contentScale,
                alignment = target.alignment,
                durationMillis = durationMillis,
                fadeStart = fadeStart,
                preferExactIntrinsicSize = preferExactIntrinsicSize,
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