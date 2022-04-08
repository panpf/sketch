package com.github.panpf.sketch.viewability

import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.view.MotionEvent
import android.view.View
import android.view.View.OnClickListener
import android.view.View.OnLongClickListener
import android.widget.ImageView.ScaleType
import com.github.panpf.sketch.request.DisplayRequest
import com.github.panpf.sketch.request.DisplayResult
import com.github.panpf.sketch.request.Listener
import com.github.panpf.sketch.request.ProgressListener

interface ViewAbilityContainer {

    val viewAbilityList: List<ViewAbility>

    fun addViewAbility(viewAbility: ViewAbility): ViewAbilityContainer

    fun removeViewAbility(viewAbility: ViewAbility): ViewAbilityContainer

    fun getRequestListener(): Listener<DisplayRequest, DisplayResult.Success, DisplayResult.Error>?

    fun getRequestProgressListener(): ProgressListener<DisplayRequest>?

    fun onAttachedToWindow()

    fun onDetachedFromWindow()

    fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int)

    fun onSizeChanged(width: Int, height: Int, oldWidth: Int, oldHeight: Int)

    fun onVisibilityChanged(changedView: View, visibility: Int)

    fun onDrawBefore(canvas: Canvas)

    fun onDraw(canvas: Canvas)

    fun onDrawForegroundBefore(canvas: Canvas)

    fun onDrawForeground(canvas: Canvas)

    fun onDrawableChanged(oldDrawable: Drawable?, newDrawable: Drawable?)

    fun onTouchEvent(event: MotionEvent): Boolean

    fun setOnClickListener(l: OnClickListener?)

    fun setOnLongClickListener(l: OnLongClickListener?)

    fun setScaleType(scaleType: ScaleType): Boolean

    fun getScaleType(): ScaleType?
}