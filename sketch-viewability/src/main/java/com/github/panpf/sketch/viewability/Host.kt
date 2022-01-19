package com.github.panpf.sketch.viewability

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.View
import androidx.lifecycle.Lifecycle
import com.github.panpf.sketch.request.DisplayRequest
import com.github.panpf.sketch.util.getLifecycle

class Host(val view: View, private val owner: ViewAbilityOwner) {

    val context: Context = view.context

    val lifecycle: Lifecycle? = view.context.getLifecycle()

    val drawable: Drawable?
        get() = owner.getDrawable()

    fun postInvalidate() = view.postInvalidate()

    fun invalidate() = view.invalidate()

    fun submitRequest(request: DisplayRequest) = owner.submitRequest(request)
}