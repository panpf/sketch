/*
 * Copyright (C) 2024 panpf <panpfpanpf@outlook.com>
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

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.navigation.fragment.findNavController
import androidx.viewbinding.ViewBinding
import com.github.panpf.sketch.sample.databinding.FragmentToolbarPageBinding

abstract class BaseToolbarBindingFragment<VIEW_BINDING : ViewBinding> : BasePermissionFragment() {

    protected var toolbarPageBinding: FragmentToolbarPageBinding? = null
    protected var binding: VIEW_BINDING? = null

    final override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentToolbarPageBinding.inflate(inflater, container, false).apply {
        this@BaseToolbarBindingFragment.toolbarPageBinding = this
        @Suppress("UNCHECKED_CAST")
        val view = (createViewBinding(inflater, content) as VIEW_BINDING).apply {
            this@BaseToolbarBindingFragment.binding = this
        }.root
        content.addView(view)
    }.root

    final override fun onPermissionsPassed(view: View, savedInstanceState: Bundle?) {
        this.toolbarPageBinding!!.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
        onViewCreated(toolbarPageBinding!!.toolbar, binding!!, savedInstanceState)
    }

    protected open fun onViewCreated(
        toolbar: Toolbar,
        binding: VIEW_BINDING,
        savedInstanceState: Bundle?
    ) {

    }

    final override fun getNavigationBarInsetsView(): View? {
        return getNavigationBarInsetsView(binding!!)
    }

    final override fun getStatusBarInsetsView(): View {
        return toolbarPageBinding!!.root
    }

    open fun getNavigationBarInsetsView(binding: VIEW_BINDING): View? = null

    override fun onDestroyView() {
        this.toolbarPageBinding = null
        this.binding = null
        super.onDestroyView()
    }
}