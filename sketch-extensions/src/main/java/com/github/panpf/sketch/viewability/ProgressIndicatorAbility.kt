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
import androidx.core.view.ViewCompat
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Lifecycle.Event.ON_PAUSE
import androidx.lifecycle.Lifecycle.Event.ON_RESUME
import androidx.lifecycle.Lifecycle.State.RESUMED
import androidx.lifecycle.LifecycleEventObserver
import com.github.panpf.sketch.drawable.MaskProgressDrawable
import com.github.panpf.sketch.drawable.ProgressDrawable
import com.github.panpf.sketch.drawable.RingProgressDrawable
import com.github.panpf.sketch.drawable.SectorProgressDrawable
import com.github.panpf.sketch.request.DisplayRequest
import com.github.panpf.sketch.request.DisplayResult.Error
import com.github.panpf.sketch.request.DisplayResult.Success
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.isSketchGlobalLifecycle
import com.github.panpf.sketch.util.getLifecycle

/**
 * A ViewAbility that displays [ImageRequest] progress indicator functionality on the View surface
 */
class ProgressIndicatorAbility(private val progressDrawable: ProgressDrawable) : ViewAbility,
    LayoutObserver, RequestListenerObserver, RequestProgressListenerObserver,
    DrawObserver, VisibilityChangedObserver, AttachObserver {

    private var lifecycle: Lifecycle? = null
        set(value) {
            if (value != field) {
                unregisterLifecycleObserver()
                field = value
                registerLifecycleObserver()
            }
        }
    override var host: Host? = null
        set(value) {
            field = value
            lifecycle = value?.context.getLifecycle()
        }

    private var requestRunning = false
    private var isAttachedToWindow = false
    private val lifecycleObserver = LifecycleEventObserver { _, event ->
        if (event == ON_RESUME) {
            startAnimation()
        } else if (event == ON_PAUSE) {
            stopAnimation()
        }
    }

    // It must be defined here because Drawable holds the callback with a weak reference,
    // so something other than Drawable needs to hold the callback
    private val drawableCallback = object : Callback {
        override fun invalidateDrawable(who: Drawable) {
            host?.view?.invalidate()
        }

        override fun scheduleDrawable(who: Drawable, what: Runnable, `when`: Long) {
            val delay = `when` - SystemClock.uptimeMillis()
            host?.view?.postDelayed(what, delay)
        }

        override fun unscheduleDrawable(who: Drawable, what: Runnable) {
            host?.view?.removeCallbacks(what)
        }
    }

    init {
        progressDrawable.apply {
            setVisible(false, false)
            callback = drawableCallback
            onProgressEnd = {
                requestRunning = false
                resetDrawableVisible()
            }
        }
    }

    override fun onAttachedToWindow() {
        isAttachedToWindow = true
        registerLifecycleObserver()
        resetDrawableVisible()
    }

    override fun onVisibilityChanged(changedView: View, visibility: Int) {
        resetDrawableVisible()
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        updateDrawableBounds()
    }

    override fun onDrawBefore(canvas: Canvas) {

    }

    override fun onDraw(canvas: Canvas) {
        if (progressDrawable.isVisible) {
            progressDrawable.draw(canvas)
        }
    }

    override fun onDetachedFromWindow() {
        // Because the View.isAttachedToWindow () method still returns true when execute here
        isAttachedToWindow = false
        unregisterLifecycleObserver()
        resetDrawableVisible()
    }

    private fun registerLifecycleObserver() {
        val view = host?.view ?: return
        if (ViewCompat.isAttachedToWindow(view)) {
            lifecycle?.addObserver(lifecycleObserver)
        }
    }

    private fun unregisterLifecycleObserver() {
        this.lifecycle?.removeObserver(lifecycleObserver)
    }

    override fun onRequestStart(request: DisplayRequest) {
        lifecycle =
            request.lifecycle.takeIf { !it.isSketchGlobalLifecycle() }
                ?: host?.context.getLifecycle()
        requestRunning = true
        progressDrawable.progress = 0f
        resetDrawableVisible()
    }

    override fun onRequestError(request: DisplayRequest, result: Error) {
        requestRunning = false
        resetDrawableVisible()
    }

    override fun onUpdateRequestProgress(
        request: DisplayRequest, totalLength: Long, completedLength: Long
    ) {
        val newProgress = if (totalLength > 0) completedLength.toFloat() / totalLength else 0f
        progressDrawable.progress = newProgress
    }

    override fun onRequestSuccess(request: DisplayRequest, result: Success) {
        progressDrawable.progress = 1f
    }

    private fun resetDrawableVisible() {
        val view = host?.view ?: return
        val visible = isAttachedToWindow && view.isVisible && requestRunning
        progressDrawable.setVisible(visible, false)
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

    private fun startAnimation() {
        val lifecycle = lifecycle
        val progressDrawable = progressDrawable
        if (progressDrawable is Animatable
            && progressDrawable.isVisible
            && (lifecycle == null || lifecycle.currentState >= RESUMED)
        ) {
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

/**
 * Display a progress indicator, [progressDrawable] is responsible for the specific style
 */
fun ViewAbilityContainer.showProgressIndicator(progressDrawable: ProgressDrawable) {
    removeProgressIndicator()
    val indicator = ProgressIndicatorAbility(progressDrawable)
    addViewAbility(indicator)
}

/**
 * Remove progress indicator
 */
fun ViewAbilityContainer.removeProgressIndicator() {
    viewAbilityList
        .find { it is ProgressIndicatorAbility }
        ?.let { removeViewAbility(it) }
}

/**
 * Display a sector progress indicator
 */
fun ViewAbilityContainer.showSectorProgressIndicator(
    size: Int = (50f * Resources.getSystem().displayMetrics.density + 0.5f).toInt(),
    color: Int = Color.WHITE,
    backgroundColor: Int = 0x44000000,
) {
    val progressDrawable = SectorProgressDrawable(size, backgroundColor, color, color, size * 0.02f)
    showProgressIndicator(progressDrawable)
}

/**
 * Displays a mask progress indicator
 */
fun ViewAbilityContainer.showMaskProgressIndicator(
    @ColorInt maskColor: Int = MaskProgressDrawable.DEFAULT_MASK_COLOR,
) = showProgressIndicator(MaskProgressDrawable(maskColor))

/**
 * Display a ring progress indicator
 */
fun ViewAbilityContainer.showRingProgressIndicator(
    size: Int = (50f * Resources.getSystem().displayMetrics.density + 0.5f).toInt(),
    ringWidth: Float = size * 0.1f,
    @ColorInt ringColor: Int = Color.WHITE,
) = showProgressIndicator(RingProgressDrawable(size, ringWidth, ringColor))