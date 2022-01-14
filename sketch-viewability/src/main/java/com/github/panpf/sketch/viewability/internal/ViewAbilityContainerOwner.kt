package com.github.panpf.sketch.viewability.internal

import android.graphics.drawable.Drawable
import android.view.View.OnClickListener
import android.view.View.OnLongClickListener
import com.github.panpf.sketch.request.DisplayRequest
import com.github.panpf.sketch.target.ListenerProvider

interface ViewAbilityContainerOwner : ListenerProvider {
    val viewAbilityContainer: ViewAbilityContainer
    fun superSetOnClickListener(l: OnClickListener?)
    fun superSetOnLongClickListener(l: OnLongClickListener?)
    fun getDrawable(): Drawable?
    fun submitRequest(request: DisplayRequest)
}