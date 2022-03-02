package com.github.panpf.sketch.viewability

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.net.Uri
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import com.github.panpf.sketch.request.DisplayRequest
import com.github.panpf.sketch.request.DisplayResult.Error
import com.github.panpf.sketch.request.DisplayResult.Success
import com.github.panpf.sketch.request.Listener
import com.github.panpf.sketch.request.ProgressListener
import com.github.panpf.sketch.viewability.internal.RealViewAbilityContainer

abstract class AbsAbilityImageView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyle: Int = 0
) : AppCompatImageView(context, attrs, defStyle), ViewAbilityOwner {

    private val viewAbilityContainer: ViewAbilityContainer by lazy {
        RealViewAbilityContainer(this, this)
    }

    override val viewAbilityList: List<ViewAbility>
        get() = viewAbilityContainer.viewAbilityList

    final override fun addViewAbility(viewAbility: ViewAbility) {
        viewAbilityContainer.addViewAbility(viewAbility)
    }

    override fun removeViewAbility(viewAbility: ViewAbility) {
        viewAbilityContainer.removeViewAbility(viewAbility)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        viewAbilityContainer.onAttachedToWindow()
    }

    override fun onVisibilityChanged(changedView: View, visibility: Int) {
        super.onVisibilityChanged(changedView, visibility)
        viewAbilityContainer.onVisibilityChanged(changedView, visibility)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        viewAbilityContainer.onLayout(changed, left, top, right, bottom)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        viewAbilityContainer.onSizeChanged(w, h, oldw, oldh)
    }

    override fun onDraw(canvas: Canvas) {
        viewAbilityContainer.onDrawBefore(canvas)
        super.onDraw(canvas)
        viewAbilityContainer.onDraw(canvas)
    }

    override fun onDrawForeground(canvas: Canvas) {
        viewAbilityContainer.onDrawForegroundBefore(canvas)
        super.onDrawForeground(canvas)
        viewAbilityContainer.onDrawForeground(canvas)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        return viewAbilityContainer.onTouchEvent(event) || super.onTouchEvent(event)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        viewAbilityContainer.onDetachedFromWindow()
    }

    override fun setImageDrawable(drawable: Drawable?) {
        val oldDrawable = this.drawable
        super.setImageDrawable(drawable)
        val newDrawable = this.drawable
        if (oldDrawable !== newDrawable) {
            viewAbilityContainer.onDrawableChanged(oldDrawable, newDrawable)
        }
    }

    override fun setImageURI(uri: Uri?) {
        val oldDrawable = this.drawable
        super.setImageURI(uri)
        val newDrawable = this.drawable
        if (oldDrawable !== newDrawable) {
            viewAbilityContainer.onDrawableChanged(oldDrawable, newDrawable)
        }
    }

    final override fun setOnClickListener(l: OnClickListener?) {
        viewAbilityContainer.setOnClickListener(l)
    }

    final override fun setOnLongClickListener(l: OnLongClickListener?) {
        viewAbilityContainer.setOnLongClickListener(l)
    }

    final override fun superSetOnClickListener(listener: OnClickListener?) {
        super.setOnClickListener(listener)
        if (listener == null) {
            isClickable = false
        }
    }

    final override fun superSetOnLongClickListener(listener: OnLongClickListener?) {
        super.setOnLongClickListener(listener)
        if (listener == null) {
            isLongClickable = false
        }
    }

    final override fun superSetScaleType(scaleType: ScaleType) {
        super.setScaleType(scaleType)
    }

    final override fun superGetScaleType(): ScaleType {
        return super.getScaleType()
    }

    final override fun setScaleType(scaleType: ScaleType) {
        if (!viewAbilityContainer.setScaleType(scaleType)) {
            super.setScaleType(scaleType)
        }
    }

    final override fun getScaleType(): ScaleType {
        return viewAbilityContainer.getScaleType() ?: super.getScaleType()
    }

    final override fun superGetDrawable(): Drawable? {
        return super.getDrawable()
    }

    override fun getListener(): Listener<DisplayRequest, Success, Error>? {
        return viewAbilityContainer.getRequestListener()
    }

    override fun getProgressListener(): ProgressListener<DisplayRequest>? {
        return viewAbilityContainer.getRequestProgressListener()
    }
}