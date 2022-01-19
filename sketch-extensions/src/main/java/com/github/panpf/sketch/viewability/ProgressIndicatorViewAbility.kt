package com.github.panpf.sketch.viewability

import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.Animatable
import android.graphics.drawable.Drawable
import android.graphics.drawable.Drawable.Callback
import android.os.SystemClock
import android.view.View
import androidx.annotation.ColorInt
import androidx.lifecycle.Lifecycle.Event.ON_PAUSE
import androidx.lifecycle.Lifecycle.Event.ON_RESUME
import androidx.lifecycle.Lifecycle.State.RESUMED
import androidx.lifecycle.LifecycleEventObserver
import com.github.panpf.sketch.drawable.ArcProgressDrawable
import com.github.panpf.sketch.drawable.MaskProgressDrawable
import com.github.panpf.sketch.drawable.ProgressDrawable
import com.github.panpf.sketch.request.DisplayRequest
import com.github.panpf.sketch.request.DisplayResult.Error
import com.github.panpf.sketch.request.DisplayResult.Success
import com.github.panpf.sketch.util.isAttachedToWindowCompat
import com.github.panpf.sketch.viewability.ViewAbility.AttachObserver
import com.github.panpf.sketch.viewability.ViewAbility.DrawObserver
import com.github.panpf.sketch.viewability.ViewAbility.LayoutObserver
import com.github.panpf.sketch.viewability.ViewAbility.RequestListenerObserver
import com.github.panpf.sketch.viewability.ViewAbility.RequestProgressListenerObserver
import com.github.panpf.sketch.viewability.ViewAbility.VisibilityChangedObserver

class ProgressIndicatorViewAbility(private val progressDrawable: ProgressDrawable) : ViewAbility,
    LayoutObserver, RequestListenerObserver, RequestProgressListenerObserver,
    DrawObserver, VisibilityChangedObserver, AttachObserver {

    override var host: Host? = null

    private var show = false
    private val lifecycleObserver = LifecycleEventObserver { _, event ->
        if (event == ON_RESUME) {
            startAnimation()
        } else if (event == ON_PAUSE) {
            stopAnimation()
        }
    }

    init {
        progressDrawable.callback = object : Callback {
            override fun invalidateDrawable(who: Drawable) {
                host?.invalidate()
            }

            override fun scheduleDrawable(who: Drawable, what: Runnable, `when`: Long) {
                val delay = `when` - SystemClock.uptimeMillis()
                host?.view?.postDelayed(what, delay)
            }

            override fun unscheduleDrawable(who: Drawable, what: Runnable) {
                host?.view?.removeCallbacks(what)
            }
        }
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        updateDrawableBounds()
        host?.postInvalidate()
    }

    private fun updateDrawableBounds() {
        val view = host?.view ?: return
        val availableWidth = view.width - view.paddingLeft - view.paddingRight
        val availableHeight = view.height - view.paddingTop - view.paddingBottom
        val drawableWidth = progressDrawable.intrinsicWidth
        val drawableHeight = progressDrawable.intrinsicHeight
        val left: Int
        val right: Int
        val top: Int
        val bottom: Int
        if (drawableWidth > 0) {
            left = view.paddingLeft + ((availableWidth - drawableWidth) / 2f).toInt()
            right = left + drawableWidth
        } else {
            left = view.paddingLeft
            right = view.width - view.paddingRight
        }
        if (drawableHeight > 0) {
            top = view.paddingTop + ((availableHeight - drawableHeight) / 2f).toInt()
            bottom = top + drawableHeight
        } else {
            top = view.paddingTop
            bottom = view.height - view.paddingBottom
        }
        progressDrawable.setBounds(left, top, right, bottom)
    }

    override fun onRequestStart(request: DisplayRequest) {
        show = true
        progressDrawable.updateProgress(0f)
        startAnimation()
    }

    override fun onRequestError(request: DisplayRequest, result: Error) {
        stopAnimation()
        show = false
        host?.invalidate()
    }

    override fun onUpdateRequestProgress(
        request: DisplayRequest, totalLength: Long, completedLength: Long
    ) {
        val newProgress = if (totalLength > 0) completedLength.toFloat() / totalLength else 0f
        progressDrawable.updateProgress(newProgress)
    }

    override fun onRequestSuccess(request: DisplayRequest, result: Success) {
        progressDrawable.updateProgress(1f) {
            stopAnimation()
            show = false
            host?.invalidate()
        }
    }

    override fun onDrawBefore(canvas: Canvas) {

    }

    override fun onDraw(canvas: Canvas) {
        host?.view
            ?.takeIf { it.isAttachedToWindowCompat && it.visibility == View.VISIBLE }
            ?: return
        if (!show) return
        progressDrawable.draw(canvas)
    }

    override fun onDrawForegroundBefore(canvas: Canvas) {

    }

    override fun onDrawForeground(canvas: Canvas) {

    }

    override fun onVisibilityChanged(changedView: View, visibility: Int) {
        val view = host?.view ?: return
        val visible = visibility == View.VISIBLE && view.visibility == View.VISIBLE
        if (visible) {
            startAnimation()
        } else {
            stopAnimation()
        }
    }

    override fun onAttachedToWindow() {
        val lifecycle = host?.lifecycle
        if (lifecycle != null) {
            if (lifecycle.currentState >= RESUMED) {
                startAnimation()
            }
            lifecycle.addObserver(lifecycleObserver)
        } else {
            startAnimation()
        }
    }

    override fun onDetachedFromWindow() {
        stopAnimation()
        host?.lifecycle?.removeObserver(lifecycleObserver)
    }

    private fun startAnimation() {
        host?.view
            ?.takeIf { it.isAttachedToWindowCompat && it.visibility == View.VISIBLE }
            ?: return
        val progressDrawable = progressDrawable
        if (progressDrawable is Animatable) {
            progressDrawable.start()
        }
    }

    private fun stopAnimation() {
        val progressDrawable = progressDrawable
        if (progressDrawable is Animatable) {
            progressDrawable.stop()
        }
    }
}

fun ViewAbilityContainerOwner.showProgressIndicator(progressDrawable: ProgressDrawable) {
    removeProgressIndicator()
    val indicator = ProgressIndicatorViewAbility(progressDrawable)
    viewAbilityContainer.addViewAbility(indicator)
}

fun ViewAbilityContainerOwner.removeProgressIndicator() {
    val viewAbilityContainer = viewAbilityContainer
    viewAbilityContainer.viewAbilityList
        .find { it is ProgressIndicatorViewAbility }
        ?.let { viewAbilityContainer.removeViewAbility(it) }
}

fun ViewAbilityContainerOwner.showArcProgressIndicator(
    sizeDp: Float = 50f,
    color: Int = Color.WHITE,
    backgroundColor: Int = 0x44000000,
) {
    val size = (sizeDp * Resources.getSystem().displayMetrics.density + 0.5f).toInt()
    val progressDrawable = ArcProgressDrawable(
        size = size,
        backgroundColor = backgroundColor,
        strokeColor = color,
        progressColor = color,
        strokeWidth = size * 0.02f
    )
    showProgressIndicator(progressDrawable)
}

fun ViewAbilityContainerOwner.showMaskProgressIndicator(
    @ColorInt maskColor: Int = MaskProgressDrawable.DEFAULT_MASK_COLOR,
) = showProgressIndicator(MaskProgressDrawable(maskColor))