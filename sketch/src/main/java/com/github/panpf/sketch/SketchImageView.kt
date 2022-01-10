package com.github.panpf.sketch

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import androidx.annotation.ColorInt
import androidx.appcompat.widget.AppCompatImageView
import com.github.panpf.sketch.internal.CircleProgressIndicator
import com.github.panpf.sketch.internal.MaskProgressIndicator
import com.github.panpf.sketch.internal.ProgressIndicator
import com.github.panpf.sketch.request.DisplayRequest
import com.github.panpf.sketch.request.DisplayResult.Error
import com.github.panpf.sketch.request.DisplayResult.Success
import com.github.panpf.sketch.request.Listener
import com.github.panpf.sketch.request.ProgressListener
import com.github.panpf.sketch.target.ListenerProvider
import java.lang.ref.WeakReference

open class SketchImageView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : AppCompatImageView(context, attrs), ListenerProvider {

    // todo 待实现 图片类型标识、圆形进度、点击播放/暂停 gif、图片来源

    private var progressIndicator: ProgressIndicator? = null
    private val displayRequestListener: DisplayRequestListener by lazy {
        DisplayRequestListener(WeakReference(this@SketchImageView))
    }
    private val needListener: Boolean
        get() = progressIndicator != null

    override fun getListener(): Listener<DisplayRequest, Success, Error>? =
        if (needListener) displayRequestListener else null

    override fun getProgressListener(): ProgressListener<DisplayRequest>? =
        if (needListener) displayRequestListener else null

    override fun onDrawForeground(canvas: Canvas) {
        super.onDrawForeground(canvas)
        progressIndicator?.onDraw(canvas)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        progressIndicator?.onLayout(this)
    }

    fun getProgressIndicator(): ProgressIndicator? = progressIndicator

    fun setProgressIndicator(progressIndicator: ProgressIndicator?) {
        if (progressIndicator?.key != this.progressIndicator?.key) {
            this.progressIndicator = progressIndicator
            postInvalidate()
        }
    }

    fun showMaskProgressIndicator(@ColorInt maskColor: Int = MaskProgressIndicator.DEFAULT_MASK_COLOR) {
        val progressIndicator = progressIndicator
        if (progressIndicator !is MaskProgressIndicator || progressIndicator.maskColor != maskColor) {
            setProgressIndicator(MaskProgressIndicator(maskColor))
        }
    }

    fun showCircleProgressIndicator(sizeDp: Float = CircleProgressIndicator.DEFAULT_SIZE_DP) {
        val progressIndicator = progressIndicator
        if (progressIndicator !is CircleProgressIndicator || progressIndicator.sizeDp != sizeDp) {
            setProgressIndicator(CircleProgressIndicator(sizeDp))
        }
    }

    private class DisplayRequestListener(private val view: WeakReference<SketchImageView>) :
        Listener<DisplayRequest, Success, Error>,
        ProgressListener<DisplayRequest> {

        override fun onStart(request: DisplayRequest) {
            super.onStart(request)
            val view = view.get() ?: return
            view.progressIndicator?.onRequestStart(view, request)
        }

        override fun onError(request: DisplayRequest, result: Error) {
            super.onError(request, result)
            val view = view.get() ?: return
            view.progressIndicator?.onRequestError(view, request, result)
        }

        override fun onSuccess(request: DisplayRequest, result: Success) {
            super.onSuccess(request, result)
            val view = view.get() ?: return
            view.progressIndicator?.onRequestSuccess(view, request, result)
        }

        override fun onUpdateProgress(
            request: DisplayRequest,
            totalLength: Long,
            completedLength: Long
        ) {
            val view = view.get() ?: return
            view.progressIndicator?.onProgressChanged(view, request, totalLength, completedLength)
        }
    }
}