package com.github.panpf.sketch.viewability.internal

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import com.github.panpf.sketch.request.DisplayRequest
import com.github.panpf.sketch.request.DisplayResult.Error
import com.github.panpf.sketch.request.DisplayResult.Success
import com.github.panpf.sketch.request.Listener
import com.github.panpf.sketch.request.ProgressListener

abstract class AbsAbilityImageView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyle: Int = 0
) : AppCompatImageView(context, attrs, defStyle), ViewAbilityContainerOwner {

    private val _viewAbilityContainer: ViewAbilityContainer by lazy {
        ViewAbilityContainerImpl(this, this)
    }

    final override val viewAbilityContainer: ViewAbilityContainer
        get() = _viewAbilityContainer

    final override fun superSetOnClickListener(l: OnClickListener?) {
        super.setOnClickListener(l)
        if (l == null) {
            isClickable = false
        }
    }

    final override fun superSetOnLongClickListener(l: OnLongClickListener?) {
        super.setOnLongClickListener(l)
        if (l == null) {
            isLongClickable = false
        }
    }

    final override fun getListener(): Listener<DisplayRequest, Success, Error>? {
        return _viewAbilityContainer.getRequestListener()
    }

    final override fun getProgressListener(): ProgressListener<DisplayRequest>? {
        return _viewAbilityContainer.getRequestProgressListener()
    }

    final override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        viewAbilityContainer.onAttachedToWindow()
    }

    final override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        viewAbilityContainer.onDetachedFromWindow()
    }

    final override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        viewAbilityContainer.onLayout(changed, left, top, right, bottom)
    }

    final override fun onDraw(canvas: Canvas) {
        viewAbilityContainer.onDrawBefore(canvas)
        super.onDraw(canvas)
        viewAbilityContainer.onDraw(canvas)
    }

    final override fun onDrawForeground(canvas: Canvas) {
        viewAbilityContainer.onDrawForegroundBefore(canvas)
        super.onDrawForeground(canvas)
        viewAbilityContainer.onDrawForeground(canvas)
    }

    final override fun setOnClickListener(l: OnClickListener?) {
        viewAbilityContainer.setOnClickListener(l)
    }

    final override fun setOnLongClickListener(l: OnLongClickListener?) {
        viewAbilityContainer.setOnLongClickListener(l)
    }
}