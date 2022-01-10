package com.github.panpf.sketch

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import androidx.annotation.ColorInt
import androidx.appcompat.widget.AppCompatImageView
import com.github.panpf.sketch.internal.CircleProgressIndicator
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

    private var progressIndicator: ProgressIndicator? = null
    private var mimeTypeLogoHelper: MimeTypeLogoHelper? = null

    private val displayRequestListener: DisplayRequestListener by lazy {
        DisplayRequestListener(WeakReference(this@SketchImageView))
    }
    private val needListener: Boolean
        get() = progressIndicator != null || mimeTypeLogoHelper != null

    override fun getListener(): Listener<DisplayRequest, Success, Error>? =
        if (needListener) displayRequestListener else null

    override fun getProgressListener(): ProgressListener<DisplayRequest>? =
        if (needListener) displayRequestListener else null

    override fun onDrawForeground(canvas: Canvas) {
        super.onDrawForeground(canvas)
        mimeTypeLogoHelper?.onDraw(canvas)
        progressIndicator?.onDraw(canvas)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        mimeTypeLogoHelper?.onLayout(this)
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

    fun setMimeTypeLogo(mimeTypeLogoHelper: MimeTypeLogoHelper?) {
        this.mimeTypeLogoHelper = mimeTypeLogoHelper
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

    private class DisplayRequestListener(private val view: WeakReference<SketchImageView>) :
        Listener<DisplayRequest, Success, Error>,
        ProgressListener<DisplayRequest> {

        override fun onStart(request: DisplayRequest) {
            super.onStart(request)
            val view = view.get() ?: return
            view.mimeTypeLogoHelper?.onRequestStart(view, request)
            view.progressIndicator?.onRequestStart(view, request)
        }

        override fun onError(request: DisplayRequest, result: Error) {
            super.onError(request, result)
            val view = view.get() ?: return
            view.mimeTypeLogoHelper?.onRequestError(view, request, result)
            view.progressIndicator?.onRequestError(view, request, result)
        }

        override fun onSuccess(request: DisplayRequest, result: Success) {
            super.onSuccess(request, result)
            val view = view.get() ?: return
            view.mimeTypeLogoHelper?.onRequestSuccess(view, request, result)
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