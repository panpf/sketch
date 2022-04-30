package com.github.panpf.sketch.sample.ui.common.list

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.github.panpf.assemblyadapter.BindingItemFactory
import com.github.panpf.sketch.sample.util.instanceViewBinding
import kotlin.reflect.KClass

abstract class MyBindingItemFactory<DATA : Any, VIEW_BINDING : ViewBinding>(dataClass: KClass<DATA>) :
    BindingItemFactory<DATA, VIEW_BINDING>(dataClass) {

    override fun createItemViewBinding(
        context: Context,
        inflater: LayoutInflater,
        parent: ViewGroup
    ) = this@MyBindingItemFactory::class.java
        .instanceViewBinding(1, inflater, parent) as VIEW_BINDING
}
