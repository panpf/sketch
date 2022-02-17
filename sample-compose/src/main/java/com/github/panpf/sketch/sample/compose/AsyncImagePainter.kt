package com.github.panpf.sketch.sample.compose

import android.graphics.drawable.Drawable
import androidx.compose.runtime.RememberObserver
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.isUnspecified
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.painter.Painter
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.request.DisplayRequest
import com.github.panpf.sketch.request.DisplayResult
import com.github.panpf.sketch.request.Disposable
import com.github.panpf.sketch.resize.SizeResolver
import com.github.panpf.sketch.sample.compose.State.Empty
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.mapNotNull
import kotlin.math.roundToInt

class AsyncImagePainter(
    val sketch: Sketch,
    imageUri: String?,
    configBlock: (DisplayRequest.Builder.() -> Unit)?,
    val isPreview: Boolean,
    val filterQuality: FilterQuality,
) : Painter(), RememberObserver {

    private val target = object : com.github.panpf.sketch.target.Target {
        override fun onStart(placeholder: Drawable?) {
            super.onStart(placeholder)
            state = State.Loading(placeholder?.toPainter(filterQuality))
        }

        override fun onSuccess(result: Drawable) {
            super.onSuccess(result)
            state = State.Success(result.toPainter(filterQuality))
        }

        override fun onError(error: Drawable?) {
            super.onError(error)
            state = State.Error(error?.toPainter(filterQuality))
        }
    }

    private inner class DrawSizeResolver : SizeResolver {

        override suspend fun size() = drawSize
            .mapNotNull { size ->
                when {
                    size.isUnspecified -> com.github.panpf.sketch.util.Size(-1, -1)
                    size.isPositive -> com.github.panpf.sketch.util.Size(
                        size.width.roundToInt(),
                        size.height.roundToInt()
                    )
                    else -> null
                }
            }
            .first()
    }

    val request = DisplayRequest(imageUri, target, configBlock).run {
        // todo Limit the size of the
//        if (resizeSize == null && resizeSizeResolver == null) {
//            newDisplayRequest {
//                resizeSizeResolver(DrawSizeResolver())
//            }
//        } else {
            this
//        }
    }
    var disposable: Disposable<DisplayResult>? = null

    /** The current [AsyncImagePainter.State]. */
    var state: State by mutableStateOf(Empty)
        private set

    override fun onRemembered() {
        if (isPreview) {
            state = State.Loading(
                request.placeholderImage?.getDrawable(sketch, request, null)
                    ?.toPainter(filterQuality)
            )
        } else if (disposable == null) {
            disposable = sketch.enqueueDisplay(request)
        }
    }

    override fun onForgotten() {
        disposable?.dispose()
        disposable = null
    }

    override fun onAbandoned() = onForgotten()

    internal var painter: Painter? by mutableStateOf(null)

    private var drawSize = MutableStateFlow(Size.Zero)
    private var alpha: Float by mutableStateOf(1f)
    private var colorFilter: ColorFilter? by mutableStateOf(null)

    override val intrinsicSize: Size
        get() = painter?.intrinsicSize ?: Size.Unspecified

    override fun DrawScope.onDraw() {
        // Update the draw scope's current size.
        drawSize.value = size

        // Draw the current painter.
        painter?.apply { draw(size, alpha, colorFilter) }
    }
}