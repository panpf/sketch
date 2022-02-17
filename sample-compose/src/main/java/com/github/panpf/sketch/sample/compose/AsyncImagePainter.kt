package com.github.panpf.sketch.sample.compose

import android.graphics.drawable.Drawable
import androidx.compose.runtime.RememberObserver
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.painter.Painter
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.request.DisplayRequest
import com.github.panpf.sketch.request.DisplayResult
import com.github.panpf.sketch.request.Disposable
import com.github.panpf.sketch.sample.compose.State.Empty

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
    val request = DisplayRequest(imageUri, target, configBlock)
    var disposable: Disposable<DisplayResult>? = null

    /** The current [AsyncImagePainter.State]. */
    var state: State by mutableStateOf(Empty)
        private set

    override fun onRemembered() {
        disposable?.dispose()
        disposable = null

        if (isPreview) {
            state = State.Loading(
                request.placeholderImage?.getDrawable(sketch, request, null)
                    ?.toPainter(filterQuality)
            )
        } else {
            disposable = sketch.enqueueDisplay(request)
        }
    }

    override fun onForgotten() {
        disposable?.dispose()
        disposable = null
    }

    override fun onAbandoned() = onForgotten()

    override val intrinsicSize: Size
        get() = TODO("Not yet implemented")

    override fun DrawScope.onDraw() {
        TODO("Not yet implemented")
    }
}