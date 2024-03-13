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

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.view.WindowCompat
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.Fragment
import com.github.panpf.sketch.sample.ui.theme.getWindowBackgroundColor
import com.github.panpf.sketch.sample.ui.theme.isNightMode
import com.github.panpf.tools4a.display.ktx.getStatusBarHeight
import com.github.panpf.tools4a.toast.ktx.showLongToast

abstract class BaseFragment : Fragment() {

    protected open var statusBarTextStyle: StatusBarTextStyle? = null
        set(value) {
            field = value
            if (isResumed) {
                setupStatusBarStyle()
            }
        }
    protected open var isPage: Boolean = true

    private var resumeCount = 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setTopInsets()
        if (isPage) {
            view.isClickable = true
            if (view.background == null) {
                view.setBackgroundColor(view.context.getWindowBackgroundColor())
            }
        }
    }

    open fun getTopInsetsView(): View? = null

    fun handleActionResult(result: ActionResult): Boolean =
        when (result) {
            is ActionResult.Success -> {
                result.message?.let { showLongToast(it) }
                true
            }

            is ActionResult.Error -> {
                showLongToast(result.message)
                false
            }
        }

    override fun onResume() {
        super.onResume()
        setupStatusBarStyle()

        resumeCount++
        if (resumeCount == 1) {
            onFirstResume()
        }
    }

    protected open fun onFirstResume() {

    }

    private fun setTopInsets() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getTopInsetsView()?.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                topMargin += requireContext().getStatusBarHeight()
            }
        }
    }

    private fun setupStatusBarStyle() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) return
        val window = requireActivity().window
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                window.statusBarColor = Color.TRANSPARENT
                val insetsController =
                    WindowCompat.getInsetsController(window, requireView())
                val statusBarTextStyle =
                    statusBarTextStyle
                        ?: if (requireContext().isNightMode()) StatusBarTextStyle.White else StatusBarTextStyle.Black
                window.decorView.apply {
                    insetsController.isAppearanceLightStatusBars =
                        statusBarTextStyle == StatusBarTextStyle.Black
                }
            } else {
                window.statusBarColor = Color.parseColor("#60000000")
            }
        } else {
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        }
    }
}