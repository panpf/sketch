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
import android.view.View
import androidx.core.view.WindowCompat
import androidx.fragment.app.Fragment
import com.github.panpf.sketch.sample.ui.base.StatusBarTextStyle.Black
import com.github.panpf.sketch.sample.ui.base.StatusBarTextStyle.White
import com.github.panpf.sketch.sample.ui.common.ActionResult
import com.github.panpf.sketch.sample.ui.theme.getWindowBackgroundColor
import com.github.panpf.sketch.sample.ui.theme.isNightMode
import com.github.panpf.tools4a.toast.ktx.showLongToast

abstract class BaseFragment : Fragment() {

    protected open var statusBarTextStyle: StatusBarTextStyle? = null
    protected open var isPage: Boolean = true
//    protected open var windowInsetStyle: WindowInsetStyle = WindowInsetStyle.NonFullScreen(
//        statusBarMode = Floating,
//        statusBarStyle = Style(
//            backgroundColor = Color.parseColor("#40000000"),
//            textColor = Style.TextColor.White
//        )
//    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (isPage) {
            view.isClickable = true
            if (view.background == null) {
                view.setBackgroundColor(view.context.getWindowBackgroundColor())
            }
        }
    }

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
        setupStatusBarTextStyle()
    }

    private fun setupStatusBarTextStyle() {
//        setupWindowInsetStyle(requireActivity().window, windowInsetStyle)
        val insetsController =
            WindowCompat.getInsetsController(requireActivity().window, requireView())
        val statusBarTextStyle =
            statusBarTextStyle ?: if (requireContext().isNightMode()) White else Black
        requireActivity().window.decorView.apply {
            insetsController.isAppearanceLightStatusBars = statusBarTextStyle == Black
//            val textStyleFlag = if (statusBarTextStyle == Black) {
//                View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
//            } else {
//                View.SYSTEM_UI_FLAG_VISIBLE
//            }
//            systemUiVisibility = systemUiVisibility or textStyleFlag
        }
    }
}