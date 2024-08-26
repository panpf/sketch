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
import com.github.panpf.sketch.sample.util.getSuperGenericParam

/*
 * You need to keep the following 'BaseBinding*' classes unchanged in the 'proguard-rules.pro' configuration
 */

fun BaseBindingActivity<*>.createViewBinding(
    inflater: LayoutInflater,
    parent: ViewGroup?
): ViewBinding = createViewBinding(this::class.java, inflater, parent)

fun BaseBindingDialogFragment<*>.createViewBinding(
    inflater: LayoutInflater,
    parent: ViewGroup?
): ViewBinding = createViewBinding(this::class.java, inflater, parent)

fun BaseBindingFragment<*>.createViewBinding(
    inflater: LayoutInflater,
    parent: ViewGroup?
): ViewBinding = createViewBinding(this::class.java, inflater, parent)

fun BaseToolbarBindingFragment<*>.createViewBinding(
    inflater: LayoutInflater,
    parent: ViewGroup?
): ViewBinding = createViewBinding(this::class.java, inflater, parent)

fun BaseBindingItemFactory<*, *>.createViewBinding(
    inflater: LayoutInflater,
    parent: ViewGroup?
): ViewBinding = createViewBinding(this::class.java, inflater, parent)

private fun createViewBinding(
    clazz: Class<*>,
    inflater: LayoutInflater,
    parent: ViewGroup?
): ViewBinding {
    @Suppress("UNCHECKED_CAST")
    val viewBindingGenericParamClass =
        clazz.getSuperGenericParam(ViewBinding::class.java) as Class<ViewBinding>
    return instanceViewBindingClass(viewBindingGenericParamClass, inflater, parent)
}

private fun instanceViewBindingClass(
    viewBindingClass: Class<ViewBinding>,
    inflater: LayoutInflater,
    parent: ViewGroup?
): ViewBinding {
    val inflateMethod = viewBindingClass.getMethod(
        "inflate",
        LayoutInflater::class.java,
        ViewGroup::class.java,
        Boolean::class.java
    )
    return inflateMethod.invoke(null, inflater, parent, false) as ViewBinding
}