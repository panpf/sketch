package com.github.panpf.sketch.sample.util

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.github.panpf.sketch.sample.ui.base.BaseBindingActivity
import com.github.panpf.sketch.sample.ui.base.BindingDialogFragment
import com.github.panpf.sketch.sample.ui.base.BindingFragment
import com.github.panpf.sketch.sample.ui.base.ToolbarBindingFragment
import com.github.panpf.sketch.sample.ui.common.list.MyBindingItemFactory

fun Class<ViewBinding>.instanceViewBinding1(
    inflater: LayoutInflater,
    parent: ViewGroup?
): ViewBinding {
    val method = this.getMethod(
        "inflate",
        LayoutInflater::class.java,
        ViewGroup::class.java,
        Boolean::class.java
    )
    return method.invoke(null, inflater, parent, false) as ViewBinding
}

fun BaseBindingActivity<*>.createViewBinding(
    inflater: LayoutInflater,
    parent: ViewGroup?
): ViewBinding {
    val firstGenericParamClass = this::class.java.getSuperGenericParamClass(0) as Class<ViewBinding>
    return firstGenericParamClass.instanceViewBinding1(inflater, parent)
}

fun BindingDialogFragment<*>.createViewBinding(
    inflater: LayoutInflater,
    parent: ViewGroup?
): ViewBinding {
    val firstGenericParamClass = this::class.java.getSuperGenericParamClass(0) as Class<ViewBinding>
    return firstGenericParamClass.instanceViewBinding1(inflater, parent)
}

fun BindingFragment<*>.createViewBinding(
    inflater: LayoutInflater,
    parent: ViewGroup?
): ViewBinding {
    val firstGenericParamClass = this::class.java.getSuperGenericParamClass(0) as Class<ViewBinding>
    return firstGenericParamClass.instanceViewBinding1(inflater, parent)
}

fun ToolbarBindingFragment<*>.createViewBinding(
    inflater: LayoutInflater,
    parent: ViewGroup?
): ViewBinding {
    val firstGenericParamClass = this::class.java.getSuperGenericParamClass(0) as Class<ViewBinding>
    return firstGenericParamClass.instanceViewBinding1(inflater, parent)
}

fun MyBindingItemFactory<*, *>.createViewBinding(
    inflater: LayoutInflater,
    parent: ViewGroup?
): ViewBinding {
    val firstGenericParamClass = this::class.java.getSuperGenericParamClass(1) as Class<ViewBinding>
    return firstGenericParamClass.instanceViewBinding1(inflater, parent)
}