package com.github.panpf.sketch.viewability

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import com.github.panpf.sketch.request.DisplayRequest
import com.github.panpf.sketch.request.DisplayResult.Error
import com.github.panpf.sketch.request.DisplayResult.Success
import com.github.panpf.sketch.request.Listener
import com.github.panpf.sketch.request.ProgressListener

open class AbilityImageView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : AppCompatImageView(context, attrs), ViewAbilityContainerOwner {

    private val _viewAbilityContainer: ViewAbilityContainer by lazy {
        ViewAbilityContainerImpl(this, this)
    }

    override val viewAbilityContainer: ViewAbilityContainer
        get() = _viewAbilityContainer

    override fun superSetOnClickListener(l: OnClickListener?) {
        super.setOnClickListener(l)
        if (l == null) {
            isClickable = false
        }
    }

    override fun superSetOnLongClickListener(l: OnLongClickListener?) {
        super.setOnLongClickListener(l)
        if (l == null) {
            isLongClickable = false
        }
    }

    override fun getListener(): Listener<DisplayRequest, Success, Error>? {
        return _viewAbilityContainer.getListener()
    }

    override fun getProgressListener(): ProgressListener<DisplayRequest>? {
        return _viewAbilityContainer.getProgressListener()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        viewAbilityContainer.onAttachedToWindow()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        viewAbilityContainer.onDetachedFromWindow()
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        viewAbilityContainer.onLayout(changed, left, top, right, bottom)
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

    override fun setOnClickListener(l: OnClickListener?) {
        viewAbilityContainer.setOnClickListener(l)
    }

    override fun setOnLongClickListener(l: OnLongClickListener?) {
        viewAbilityContainer.setOnLongClickListener(l)
    }
}