package com.github.panpf.sketch.viewability

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.View.OnClickListener
import android.view.View.OnLongClickListener
import android.widget.ImageView.ScaleType
import com.github.panpf.sketch.request.DisplayRequest
import com.github.panpf.sketch.target.ListenerProvider

interface ViewAbilityOwner : ListenerProvider {

    val viewAbilityList: List<ViewAbility>

    fun addViewAbility(viewAbility: ViewAbility)

    fun removeViewAbility(viewAbility: ViewAbility)

    fun getContext(): Context

    fun superSetOnClickListener(listener: OnClickListener?)

    fun superSetOnLongClickListener(listener: OnLongClickListener?)

    fun superSetScaleType(scaleType: ScaleType)

    fun superGetScaleType(): ScaleType

    fun superGetDrawable(): Drawable?

    fun submitRequest(request: DisplayRequest)
}