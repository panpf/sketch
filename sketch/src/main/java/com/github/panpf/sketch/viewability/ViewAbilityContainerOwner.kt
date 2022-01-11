package com.github.panpf.sketch.viewability

import android.view.View.OnClickListener
import android.view.View.OnLongClickListener
import com.github.panpf.sketch.target.ListenerProvider

// todo viewability 成立一个单独的模块
interface ViewAbilityContainerOwner : ListenerProvider {
    val viewAbilityContainer: ViewAbilityContainer
    fun superSetOnClickListener(l: OnClickListener?)
    fun superSetOnLongClickListener(l: OnLongClickListener?)
}