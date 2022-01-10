package com.github.panpf.sketch

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import androidx.annotation.ColorInt
import androidx.appcompat.widget.AppCompatImageView
import com.github.panpf.sketch.request.DisplayRequest
import com.github.panpf.sketch.request.DisplayResult.Error
import com.github.panpf.sketch.request.DisplayResult.Success
import com.github.panpf.sketch.request.Listener
import com.github.panpf.sketch.request.ProgressListener
import com.github.panpf.sketch.target.ListenerProvider
import java.lang.ref.WeakReference
import kotlin.math.roundToInt

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
        }
    }

    fun showMaskProgressIndicator(@ColorInt maskColor: Int = 0x22000000) {
        val progressIndicator = progressIndicator
        if (progressIndicator !is MaskProgressIndicator || progressIndicator.maskColor != maskColor) {
            setProgressIndicator(MaskProgressIndicator(maskColor))
        }
    }

    override fun getListener(): Listener<DisplayRequest, Success, Error>? =
        if (needListener) displayRequestListener else null

    override fun getProgressListener(): ProgressListener<DisplayRequest>? =
        if (needListener) displayRequestListener else null

    interface ProgressIndicator {
        val key: String

        fun onDraw(canvas: Canvas)

        fun onLayout(view: SketchImageView)

        fun onProgressChanged(
            view: SketchImageView,
            request: DisplayRequest,
            totalLength: Long,
            completedLength: Long
        )

        fun onRequestStart(
            view: SketchImageView,
            request: DisplayRequest,
        )

        fun onRequestError(
            view: SketchImageView,
            request: DisplayRequest,
            result: Error,
        )

        fun onRequestSuccess(
            view: SketchImageView,
            request: DisplayRequest,
            result: Success,
        )
    }

    class MaskProgressIndicator(
        @ColorInt val maskColor: Int,
    ) : ProgressIndicator {

        private var show: Boolean = false
        private var progress: Float = -1F
        private val paint = Paint().apply {
            color = maskColor
            isAntiAlias = true
        }
        private val rect = Rect()

        override val key: String by lazy {
            "MaskProgressIndicator(maskColor=$maskColor)"
        }

        override fun onLayout(view: SketchImageView) {
            refreshRect(view)
            view.postInvalidate()
        }

        override fun onRequestStart(view: SketchImageView, request: DisplayRequest) {
            show = request.uriString.startsWith("http")
            progress = 0f
            refreshRect(view)
            view.postInvalidate()
        }

        override fun onRequestError(view: SketchImageView, request: DisplayRequest, result: Error) {
            show = false
            view.postInvalidate()
        }

        override fun onRequestSuccess(
            view: SketchImageView,
            request: DisplayRequest,
            result: Success
        ) {
            show = false
            view.postInvalidate()
        }

        override fun onProgressChanged(
            view: SketchImageView,
            request: DisplayRequest,
            totalLength: Long,
            completedLength: Long
        ) {
            progress = if (totalLength > 0) {
                completedLength.toFloat() / totalLength
            } else {
                0f
            }
            refreshRect(view)
            // todo 动画方式更新进度
            view.postInvalidate()
        }

        private fun refreshRect(view: SketchImageView) {
            if (show) {
                rect.set(
                    view.left + view.paddingLeft,
                    view.top + view.paddingTop,
                    view.right - view.paddingRight,
                    view.bottom - view.paddingTop,
                )
                rect.top = (progress * rect.height()).roundToInt()
            } else {
                rect.setEmpty()
            }
        }

        override fun onDraw(canvas: Canvas) {
            if (!show) return
            val rect = rect.takeIf { !it.isEmpty } ?: return
            canvas.save()
            canvas.drawRect(rect, paint)
            canvas.restore()
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