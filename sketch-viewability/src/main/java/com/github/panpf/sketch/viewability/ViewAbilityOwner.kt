package com.github.panpf.sketch.viewability

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.View.OnClickListener
import android.view.View.OnLongClickListener
import com.github.panpf.sketch.request.DisplayRequest
import com.github.panpf.sketch.target.ListenerProvider

interface ViewAbilityOwner : ListenerProvider {

    val viewAbilityList: List<ViewAbility>

    fun addViewAbility(viewAbility: ViewAbility)

    fun removeViewAbility(viewAbility: ViewAbility)

    fun getContext(): Context

    fun superSetOnClickListener(l: OnClickListener?)

    fun superSetOnLongClickListener(l: OnLongClickListener?)

    fun getDrawable(): Drawable?

    fun submitRequest(request: DisplayRequest)
}