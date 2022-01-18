package com.github.panpf.sketch.viewability.internal

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.graphics.Canvas
import com.github.panpf.sketch.request.DisplayRequest
import com.github.panpf.sketch.request.DisplayResult.Error
import com.github.panpf.sketch.request.DisplayResult.Success
import com.github.panpf.sketch.viewability.internal.ViewAbility.DrawObserver
import com.github.panpf.sketch.viewability.internal.ViewAbility.LayoutObserver
import com.github.panpf.sketch.viewability.internal.ViewAbility.RequestListenerObserver
import com.github.panpf.sketch.viewability.internal.ViewAbility.RequestProgressListenerObserver

abstract class AbsProgressViewAbility : ViewAbility, LayoutObserver, RequestListenerObserver,
    RequestProgressListenerObserver, DrawObserver {

    private var show: Boolean = false
    private var progress: Float = -1F
        set(value) {
            field = value
            host?.postInvalidate()
        }
    private var progressAnimator: ValueAnimator? = null

    override var host: Host? = null
        set(value) {
            field = value
            value?.postInvalidate()
        }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        host?.postInvalidate()
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
        val host = host ?: return
        val progress = progress.takeIf { it >= 0f } ?: return
        canvas.save()
        drawIndicator(canvas, host, progress)
        canvas.restore()
    }

    override fun onDrawForegroundBefore(canvas: Canvas) {

    }

    override fun onDrawForeground(canvas: Canvas) {

    }

    abstract fun drawIndicator(canvas: Canvas, host: Host, progress: Float)
}