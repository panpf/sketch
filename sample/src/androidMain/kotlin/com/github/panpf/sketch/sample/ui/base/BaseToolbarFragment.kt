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

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.navigation.fragment.findNavController
import com.github.panpf.sketch.sample.databinding.FragmentToolbarPageBinding

abstract class BaseToolbarFragment : BaseFragment() {

    protected var toolbarPageBinding: FragmentToolbarPageBinding? = null

    final override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentToolbarPageBinding.inflate(inflater, container, false).apply {
        this@BaseToolbarFragment.toolbarPageBinding = this
        val view = createView(toolbar, inflater, content)
        content.addView(view)
    }.root

    protected abstract fun createView(
        toolbar: Toolbar,
        inflater: LayoutInflater,
        container: ViewGroup
    ): View

    final override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        this.toolbarPageBinding!!.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
        onViewCreated(toolbarPageBinding!!.toolbar, savedInstanceState)
    }

    protected open fun onViewCreated(
        toolbar: Toolbar,
        savedInstanceState: Bundle?
    ) {

    }

    override fun getStatusBarInsetsView(): View? {
        return toolbarPageBinding!!.root
    }

    override fun onDestroyView() {
        this.toolbarPageBinding = null
        super.onDestroyView()
    }
}