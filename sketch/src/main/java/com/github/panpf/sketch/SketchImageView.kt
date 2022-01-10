package com.github.panpf.sketch

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import androidx.annotation.ColorInt
import androidx.appcompat.widget.AppCompatImageView
import com.github.panpf.sketch.internal.CircleProgressIndicator
import com.github.panpf.sketch.internal.DataFromHelper
import com.github.panpf.sketch.internal.MaskProgressIndicator
import com.github.panpf.sketch.internal.MimeTypeLogo
import com.github.panpf.sketch.internal.MimeTypeLogoHelper
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

    // todo 待实现、图片来源

    private var dataFromHelper: DataFromHelper? = null
    private var mimeTypeLogoHelper: MimeTypeLogoHelper? = null
    private var progressIndicator: ProgressIndicator? = null

    private val displayRequestListener: DisplayRequestListener by lazy {
        DisplayRequestListener(WeakReference(this@SketchImageView))
    }
    private val needListener: Boolean
        get() = progressIndicator != null || mimeTypeLogoHelper != null || dataFromHelper != null

    override fun getListener(): Listener<DisplayRequest, Success, Error>? =
        if (needListener) displayRequestListener else null

    override fun getProgressListener(): ProgressListener<DisplayRequest>? =
        if (needListener) displayRequestListener else null

    override fun onDrawForeground(canvas: Canvas) {
        super.onDrawForeground(canvas)
        dataFromHelper?.onDraw(canvas)
        mimeTypeLogoHelper?.onDraw(canvas)
        progressIndicator?.onDraw(canvas)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        dataFromHelper?.onLayout()
        progressIndicator?.onLayout()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        dataFromHelper?.view = this
        mimeTypeLogoHelper?.view = this
        progressIndicator?.view = this
        postInvalidate()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        dataFromHelper?.view = null
        mimeTypeLogoHelper?.view = null
        progressIndicator?.view = null
    }

    fun setProgressIndicator(progressIndicator: ProgressIndicator?) {
        this.progressIndicator = progressIndicator
        this.progressIndicator?.view = this
        postInvalidate()
    }

    fun showMaskProgressIndicator(
        showProgressIndicator: Boolean = true,
        @ColorInt maskColor: Int = MaskProgressIndicator.DEFAULT_MASK_COLOR
    ) {
        setProgressIndicator(if (showProgressIndicator) MaskProgressIndicator(maskColor) else null)
    }

    fun showCircleProgressIndicator(
        showProgressIndicator: Boolean = true,
        sizeDp: Float = CircleProgressIndicator.DEFAULT_SIZE_DP,
        backgroundColor: Int = CircleProgressIndicator.BACKGROUND_COLOR,
        strokeColor: Int = CircleProgressIndicator.STROKE_COLOR,
        progressColor: Int = CircleProgressIndicator.PROGRESS_COLOR
    ) {
        setProgressIndicator(
            if (showProgressIndicator) CircleProgressIndicator(
                sizeDp,
                backgroundColor,
                strokeColor,
                progressColor
            ) else null
        )
    }

    fun setMimeTypeLogo(mimeTypeLogoHelper: MimeTypeLogoHelper?) {
        this.mimeTypeLogoHelper = mimeTypeLogoHelper
        this.mimeTypeLogoHelper?.view = this
        postInvalidate()
    }

    fun setMimeTypeLogo(mimeTypeIconMap: Map<String, MimeTypeLogo>?, margin: Int = 0) {
        setMimeTypeLogo(
            if (mimeTypeIconMap?.isNotEmpty() == true) {
                MimeTypeLogoHelper(mimeTypeIconMap, margin)
            } else {
                null
            }
        )
    }

    fun setMimeTypeLogoWithDrawable(mimeTypeIconMap: Map<String, Drawable>?, margin: Int = 0) {
        setMimeTypeLogo(
            if (mimeTypeIconMap?.isNotEmpty() == true) {
                val newMap = mimeTypeIconMap.mapValues {
                    MimeTypeLogo(it.value)
                }
                MimeTypeLogoHelper(newMap, margin)
            } else {
                null
            }
        )
    }

    fun setMimeTypeLogoWithResId(mimeTypeIconMap: Map<String, Int>?, margin: Int = 0) {
        setMimeTypeLogo(
            if (mimeTypeIconMap?.isNotEmpty() == true) {
                val newMap = mimeTypeIconMap.mapValues {
                    MimeTypeLogo(it.value)
                }
                MimeTypeLogoHelper(newMap, margin)
            } else {
                null
            }
        )
    }

    fun showDataFrom(showDataFrom: Boolean = true, sizeDp: Float = DataFromHelper.DEFAULT_SIZE_DP) {
        this.dataFromHelper = if (showDataFrom) DataFromHelper(sizeDp) else null
        this.dataFromHelper?.view = this
        postInvalidate()
    }

    private class DisplayRequestListener(private val view: WeakReference<SketchImageView>) :
        Listener<DisplayRequest, Success, Error>,
        ProgressListener<DisplayRequest> {

        override fun onStart(request: DisplayRequest) {
            super.onStart(request)
            val view = view.get() ?: return
            view.dataFromHelper?.onRequestStart(request)
            view.mimeTypeLogoHelper?.onRequestStart(request)
            view.progressIndicator?.onRequestStart(request)
        }

        override fun onError(request: DisplayRequest, result: Error) {
            super.onError(request, result)
            val view = view.get() ?: return
            view.dataFromHelper?.onRequestError(request, result)
            view.mimeTypeLogoHelper?.onRequestError(request, result)
            view.progressIndicator?.onRequestError(request, result)
        }

        override fun onSuccess(request: DisplayRequest, result: Success) {
            super.onSuccess(request, result)
            val view = view.get() ?: return
            view.dataFromHelper?.onRequestSuccess(request, result)
            view.mimeTypeLogoHelper?.onRequestSuccess(request, result)
            view.progressIndicator?.onRequestSuccess(request, result)
        }

        override fun onUpdateProgress(
            request: DisplayRequest,
            totalLength: Long,
            completedLength: Long
        ) {
            val view = view.get() ?: return
            view.progressIndicator?.onProgressChanged(request, totalLength, completedLength)
        }
    }
}