package com.github.panpf.sketch.viewability

import android.content.Context
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.view.View
import androidx.annotation.MainThread
import com.github.panpf.sketch.request.DisplayRequest

class Host(private val view: View, private val owner: ViewAbilityContainerOwner) {

    private val _layoutRect = Rect()
    private val _paddingRect = Rect()

    val context: Context
        get() = view.context

    val drawable: Drawable?
        get() = owner.getDrawable()

    fun postInvalidate() = view.postInvalidate()

    fun invalidate() = view.invalidate()

    fun submitRequest(request: DisplayRequest) = owner.submitRequest(request)

    @get:MainThread
    val layoutRect: Rect
        get() = _layoutRect.apply {
            set(view.left, view.top, view.right, view.bottom)
        }

    @get:MainThread
    val paddingRect: Rect
        get() = _paddingRect.apply {
            set(view.paddingLeft, view.paddingTop, view.paddingRight, view.paddingBottom)
        }
}