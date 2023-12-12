/*
 * Copyright (C) 2022 panpf <panpfpanpf@outlook.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.panpf.sketch.viewability

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.Animatable
import android.graphics.drawable.Drawable
import android.graphics.drawable.Drawable.Callback
import android.os.SystemClock
import android.util.Log
import android.view.View
import androidx.annotation.ColorInt
import com.github.panpf.sketch.drawable.AbsProgressDrawable.Companion.DEFAULT_STEP_ANIMATION_DURATION
import com.github.panpf.sketch.drawable.MaskProgressDrawable
import com.github.panpf.sketch.drawable.ProgressDrawable
import com.github.panpf.sketch.drawable.RingProgressDrawable
import com.github.panpf.sketch.drawable.SectorProgressDrawable
import com.github.panpf.sketch.request.DisplayRequest
import com.github.panpf.sketch.request.DisplayResult.Error
import com.github.panpf.sketch.request.DisplayResult.Success
import com.github.panpf.sketch.request.ImageRequest

/**
 * Display a progress indicator, [progressDrawable] is responsible for the specific style
 */
fun ViewAbilityContainer.showProgressIndicator(progressDrawable: ProgressDrawable) {
    removeProgressIndicator()
    addViewAbility(ProgressIndicatorAbility(progressDrawable))
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
    size: Int = SectorProgressDrawable.defaultSize(),
    color: Int = Color.WHITE,
    backgroundColor: Int = 0x44000000,
    stepAnimationDuration: Int = DEFAULT_STEP_ANIMATION_DURATION,
    hideWhenCompleted: Boolean = true,
) = showProgressIndicator(
    SectorProgressDrawable(
        size = size,
        backgroundColor = backgroundColor,
        strokeColor = color,
        progressColor = color,
        strokeWidth = size * 0.02f,
        stepAnimationDuration = stepAnimationDuration,
        hideWhenCompleted = hideWhenCompleted
    )
)

/**
 * Displays a mask progress indicator
 */
fun ViewAbilityContainer.showMaskProgressIndicator(
    @ColorInt maskColor: Int = MaskProgressDrawable.DEFAULT_MASK_COLOR,
    stepAnimationDuration: Int = DEFAULT_STEP_ANIMATION_DURATION,
    hideWhenCompleted: Boolean = true,
) = showProgressIndicator(
    MaskProgressDrawable(
        maskColor = maskColor,
        stepAnimationDuration = stepAnimationDuration,
        hideWhenCompleted = hideWhenCompleted
    )
)

/**
 * Display a ring progress indicator
 */
fun ViewAbilityContainer.showRingProgressIndicator(
    size: Int = RingProgressDrawable.defaultSize(),
    ringWidth: Float = size * 0.1f,
    @ColorInt ringColor: Int = Color.WHITE,
    stepAnimationDuration: Int = DEFAULT_STEP_ANIMATION_DURATION,
    hideWhenCompleted: Boolean = true,
) = showProgressIndicator(
    RingProgressDrawable(
        size = size,
        ringWidth = ringWidth,
        ringColor = ringColor,
        stepAnimationDuration = stepAnimationDuration,
        hideWhenCompleted = hideWhenCompleted
    )
)

/**
 * Returns true if progress indicator feature is enabled
 */
val ViewAbilityContainer.isShowProgressIndicator: Boolean
    get() = viewAbilityList.find { it is ProgressIndicatorAbility } != null

/**
 * A ViewAbility that displays [ImageRequest] progress indicator functionality on the View surface
 */
class ProgressIndicatorAbility(val progressDrawable: ProgressDrawable) : ViewAbility,
    LayoutObserver, RequestListenerObserver, RequestProgressListenerObserver,
    DrawObserver, VisibilityChangedObserver, AttachObserver, Callback {

    //    private var lifecycle: Lifecycle? = null
//        set(value) {
//            if (value != field) {
//                unregisterLifecycleObserver()
//                field = value
//                registerLifecycleObserver()
//            }
//        }
    override var host: Host? = null

    //    private var requestRunning = false
//    private var isAttachedToWindow = false
//    private val lifecycleObserver = LifecycleEventObserver { _, event ->
//        if (event == ON_RESUME) {
//            startAnimation()
//        } else if (event == ON_PAUSE) {
//            stopAnimation()
//        }
//    }

    init {
        progressDrawable.apply {
//            setVisible(false, false)
            callback = this@ProgressIndicatorAbility
//            onProgressEnd = {
//                requestRunning = false
//                resetDrawableVisible()
//            }
        }
    }

    override fun onAttachedToWindow() {
//        isAttachedToWindow = true
//        lifecycle = host?.view?.findViewTreeLifecycleOwner()?.lifecycle
//            ?: host?.context.findLifecycle()
//        registerLifecycleObserver()
//        resetDrawableVisible()
        if (progressDrawable is Animatable) progressDrawable.start()
    }

    override fun onVisibilityChanged(changedView: View, visibility: Int) {
//        resetDrawableVisible()
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
//        isAttachedToWindow = false
//        unregisterLifecycleObserver()
//        resetDrawableVisible()
        if (progressDrawable is Animatable) progressDrawable.stop()
    }

//    private fun registerLifecycleObserver() {
//        val view = host?.view ?: return
//        if (ViewCompat.isAttachedToWindow(view)) {
//            lifecycle?.addObserver(lifecycleObserver)
//        }
//    }
//
//    private fun unregisterLifecycleObserver() {
//        this.lifecycle?.removeObserver(lifecycleObserver)
//    }

    override fun onRequestStart(request: DisplayRequest) {
//        requestRunning = true
        progressDrawable.progress = 0f
//        resetDrawableVisible()
    }

    override fun onRequestError(request: DisplayRequest, result: Error) {
//        requestRunning = false
        progressDrawable.progress = -1f
//        resetDrawableVisible()
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

//    private fun resetDrawableVisible() {
//        val view = host?.view ?: return
//        val visible = isAttachedToWindow && view.isVisible && requestRunning
//        progressDrawable.setVisible(visible, false)
//    }

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

//    private fun startAnimation() {
//        val lifecycle = lifecycle
//        val progressDrawable = progressDrawable
//        if (progressDrawable is Animatable
//            && progressDrawable.isVisible
//            && (lifecycle == null || lifecycle.currentState >= RESUMED)
//        ) {
//            progressDrawable.start()
//        }
//    }
//
//    private fun stopAnimation() {
//        val progressDrawable = progressDrawable
//        if (progressDrawable is Animatable) {
//            progressDrawable.stop()
//        }
//    }

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