/*
 * Copyright (C) 2022 panpf <panpfpanpf@outlook.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.panpf.sketch.sample.ui.base

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.github.panpf.sketch.sample.ui.common.list.MyBindingItemFactory
import com.github.panpf.sketch.sample.util.getSuperGenericParamClass

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