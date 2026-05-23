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

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.github.panpf.sketch.sample.AppSettings
import com.github.panpf.sketch.sample.ui.util.getWindowBackgroundColor
import com.github.panpf.sketch.sample.ui.util.isDarkTheme
import com.google.android.material.internal.ViewUtils
import org.koin.android.ext.android.inject

abstract class BaseFragment : Fragment() {

    protected val appSettings: AppSettings by inject()
    private var resumeCount = 0

    var screenMode: Boolean = true
        set(value) {
            require(view == null) { "Please set screenMode before onCreateView" }
            field = value
        }

    var lightModeSystemBars: Boolean = true
        set(value) {
            if (value != field) {
                field = value
                if (isResumed) {
                    setupLightModeSystemBars()
                }
            }
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupScreenMode(view)
        setupWindowInsets()
    }

    override fun onResume() {
        super.onResume()
        setupLightModeSystemBars()

        resumeCount++
        if (resumeCount == 1) {
            onFirstResume()
        }
    }

    protected open fun onFirstResume() {

    }

    open fun getStatusBarInsetsView(): View? = null

    open fun getNavigationBarInsetsView(): View? = null

    private fun setupScreenMode(view: View) {
        if (screenMode) {
            view.isClickable = true
            if (view.background == null) {
                view.setBackgroundColor(view.context.getWindowBackgroundColor())
            }
        }
    }

    @SuppressLint("RestrictedApi")
    private fun setupWindowInsets() {
        val statusBarInsetsView = getStatusBarInsetsView()
        if (statusBarInsetsView != null) {
            ViewUtils.doOnApplyWindowInsets(statusBarInsetsView) { _, insets, initialPadding ->
                initialPadding.top = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top
                initialPadding.applyToView(statusBarInsetsView)
                insets
            }
        }

        val navigationBarInsetsLayout = getNavigationBarInsetsView()
        if (navigationBarInsetsLayout != null) {
            ViewUtils.doOnApplyWindowInsets(navigationBarInsetsLayout) { _, insets, initialPadding ->
                initialPadding.bottom =
                    insets.getInsets(WindowInsetsCompat.Type.navigationBars()).bottom
                initialPadding.applyToView(navigationBarInsetsLayout)
                insets
            }
        }
    }

    private fun setupLightModeSystemBars() {
        if (screenMode) {
            val window = requireActivity().window
            val darkTheme = requireContext().isDarkTheme()
            val isLightMode = lightModeSystemBars && !darkTheme
            EdgeToEdgeController.setStatusBarStyle(window = window, isLightMode = isLightMode)
            EdgeToEdgeController.setNavigationBarStyle(window = window, isLightMode = isLightMode)
        }
    }
}