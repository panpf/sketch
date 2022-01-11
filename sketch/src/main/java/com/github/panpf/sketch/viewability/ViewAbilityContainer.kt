package com.github.panpf.sketch.viewability

import android.graphics.Canvas
import android.view.View.OnClickListener
import android.view.View.OnLongClickListener
import com.github.panpf.sketch.request.DisplayRequest
import com.github.panpf.sketch.request.DisplayResult.Error
import com.github.panpf.sketch.request.DisplayResult.Success
import com.github.panpf.sketch.request.Listener
import com.github.panpf.sketch.request.ProgressListener

interface ViewAbilityContainer {

    fun addViewAbility(viewAbility: ViewAbility): ViewAbilityContainer

    fun removeViewAbility(viewAbility: ViewAbility): ViewAbilityContainer

    val viewAbilityList: List<ViewAbility>

    fun getListener(): Listener<DisplayRequest, Success, Error>?

    fun getProgressListener(): ProgressListener<DisplayRequest>?

    fun onAttachedToWindow()

    fun onDetachedFromWindow()

    fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int)

    fun onDrawBefore(canvas: Canvas)

    fun onDraw(canvas: Canvas)

    fun onDrawForegroundBefore(canvas: Canvas)

    fun onDrawForeground(canvas: Canvas)

    fun setOnClickListener(l: OnClickListener?)

    fun setOnLongClickListener(l: OnLongClickListener?)
}