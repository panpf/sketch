package com.github.panpf.sketch.compose.transition

import androidx.compose.ui.graphics.painter.Painter
import com.github.panpf.sketch.compose.internal.CrossfadePainter
import com.github.panpf.sketch.compose.internal.asOrNull
import com.github.panpf.sketch.compose.internal.toPainter
import com.github.panpf.sketch.compose.request.asSketchImage
import com.github.panpf.sketch.datasource.DataFrom.MEMORY_CACHE
import com.github.panpf.sketch.drawable.internal.CrossfadeDrawable
import com.github.panpf.sketch.request.ImageResult
import com.github.panpf.sketch.request.asDrawable
import com.github.panpf.sketch.request.internal.RequestContext
import com.github.panpf.sketch.transition.Transition
import com.github.panpf.sketch.transition.TransitionTarget

class CrossfadeComposeTransition @JvmOverloads constructor(
    private val requestContext: RequestContext,
    private val target: TransitionComposeTarget,
    private val result: ImageResult,
    val durationMillis: Int = CrossfadeDrawable.DEFAULT_DURATION,
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
        val endPainter: Painter? =
            result.image?.asDrawable(requestContext.request.context.resources)?.toPainter()
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
        val durationMillis: Int = CrossfadeDrawable.DEFAULT_DURATION,
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
            return CrossfadeComposeTransition(
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
            return "CrossfadeTransition.Factory(durationMillis=$durationMillis, fadeStart=$fadeStart, preferExactIntrinsicSize=$preferExactIntrinsicSize, alwaysUse=$alwaysUse)"
        }
    }
}