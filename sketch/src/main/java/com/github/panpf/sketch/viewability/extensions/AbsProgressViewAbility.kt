package com.github.panpf.sketch.viewability.extensions

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.graphics.Canvas
import android.view.View
import android.widget.ImageView
import com.github.panpf.sketch.request.DisplayRequest
import com.github.panpf.sketch.request.DisplayResult.Error
import com.github.panpf.sketch.request.DisplayResult.Success
import com.github.panpf.sketch.viewability.DrawAbility
import com.github.panpf.sketch.viewability.LayoutAbility
import com.github.panpf.sketch.viewability.RequestListenerAbility
import com.github.panpf.sketch.viewability.RequestProgressListenerAbility
import com.github.panpf.sketch.viewability.ViewAbility

abstract class AbsProgressViewAbility : ViewAbility, LayoutAbility, RequestListenerAbility,
    RequestProgressListenerAbility, DrawAbility {

    private var show: Boolean = false
    override var view: ImageView? = null
    private var progress: Float = -1F
        set(value) {
            field = value
            view?.postInvalidate()
        }
    private var progressAnimator: ValueAnimator? = null

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        this.progress = progress
    }

    override fun onRequestStart(request: DisplayRequest) {
        this.show = request.uriString.startsWith("http")
        this.progress = -1f
        this.progressAnimator?.cancel()
    }

    override fun onRequestError(request: DisplayRequest, result: Error) {
        this.progress = -1f
        this.progressAnimator?.cancel()
    }

    override fun onRequestSuccess(request: DisplayRequest, result: Success) {
        val progressAnimator = progressAnimator
        if (progressAnimator?.isRunning == true) {
            progressAnimator.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    this@AbsProgressViewAbility.progress = -1f
                }
            })
        } else {
            this.progress = -1f
        }
    }

    override fun onUpdateRequestProgress(
        request: DisplayRequest, totalLength: Long, completedLength: Long
    ) {
        val lastProgress = progress.takeIf { it > 0 } ?: 0f
        val newProgress = if (totalLength > 0) completedLength.toFloat() / totalLength else 0f
        progressAnimator?.cancel()
        progressAnimator = ValueAnimator.ofFloat(lastProgress, newProgress).apply {
            addUpdateListener {
                progress = animatedValue as Float
            }
            duration = 150
        }
        progressAnimator?.start()
    }

    override fun onDrawBefore(canvas: Canvas) {

    }

    override fun onDraw(canvas: Canvas) {
        if (!show) return
        val view = view ?: return
        val progress = progress.takeIf { it >= 0f } ?: return
        canvas.save()
        drawIndicator(canvas, view, progress)
        canvas.restore()
    }

    override fun onDrawForegroundBefore(canvas: Canvas) {

    }

    override fun onDrawForeground(canvas: Canvas) {

    }

    abstract fun drawIndicator(canvas: Canvas, view: View, progress: Float)
}