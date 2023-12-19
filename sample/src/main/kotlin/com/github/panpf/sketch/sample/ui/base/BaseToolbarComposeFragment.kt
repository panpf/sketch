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

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView
import androidx.core.view.updateLayoutParams
import com.github.panpf.sketch.sample.databinding.FragmentToolbarPageBinding
import com.github.panpf.tools4a.display.ktx.getStatusBarHeight

abstract class BaseToolbarComposeFragment : BaseFragment() {

    protected var toolbar: Toolbar? = null

    final override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentToolbarPageBinding.inflate(inflater, container, false).apply {
        setTransparentStatusBar(toolbar)
        this@BaseToolbarComposeFragment.toolbar = toolbar

        content.addView(ComposeView(inflater.context).apply {
            setContent {
                DrawContent()
            }
        })
    }.root

    @Composable
    abstract fun DrawContent()

    private fun setTransparentStatusBar(toolbar: Toolbar) {
        val window = requireActivity().window
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
            && window.decorView.systemUiVisibility == View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        ) {
            toolbar.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                topMargin += requireContext().getStatusBarHeight()
            }
        }
    }

    final override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onViewCreated(this.toolbar!!, savedInstanceState)
    }

    protected open fun onViewCreated(
        toolbar: Toolbar,
        savedInstanceState: Bundle?
    ) {

    }

    override fun onDestroyView() {
        this.toolbar = null
        super.onDestroyView()
    }
}