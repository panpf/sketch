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

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.appcompat.widget.Toolbar
import androidx.core.view.updateLayoutParams
import com.github.panpf.sketch.sample.R
import com.github.panpf.tools4a.display.ktx.getStatusBarHeight

@Suppress("MemberVisibilityCanBePrivate")
abstract class BaseToolbarFragment : BaseFragment() {

    protected var toolbar: Toolbar? = null

    final override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.toolbar_fragment, container, false).apply {
        val toolbar = findViewById<Toolbar>(R.id.toolbarToolbar)
        val contentContainer = findViewById<FrameLayout>(R.id.toolbarContent)

        setTransparentStatusBar(toolbar)

        val view = createView(toolbar, inflater, contentContainer)
        contentContainer.addView(view)

        this@BaseToolbarFragment.toolbar = toolbar
    }

    @SuppressLint("ObsoleteSdkInt")
    private fun setTransparentStatusBar(toolbar: Toolbar) {
        val window = requireActivity().window
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
            && window.decorView.systemUiVisibility == View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        ) {
            toolbar.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                topMargin += requireContext().getStatusBarHeight()
            }
        }
    }

    protected abstract fun createView(
        toolbar: Toolbar,
        inflater: LayoutInflater,
        parent: ViewGroup?
    ): View

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