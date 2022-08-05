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

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import androidx.fragment.app.DialogFragment
import androidx.viewbinding.ViewBinding
import com.github.panpf.sketch.sample.R
import com.github.panpf.sketch.sample.util.createViewBinding
import com.github.panpf.tools4a.display.ktx.getScreenHeight
import com.github.panpf.tools4a.display.ktx.getScreenWidth
import kotlin.math.roundToInt

abstract class BindingDialogFragment<VIEW_BINDING : ViewBinding> : DialogFragment() {

    protected var binding: VIEW_BINDING? = null
    protected var dialogWidthRatio: Float = 0.85f
    protected var dialogHeightRatio: Float? = null

    final override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = (createViewBinding(inflater, container) as VIEW_BINDING).apply {
        this@BindingDialogFragment.binding = this
    }.root

    final override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onViewCreated(this.binding!!, savedInstanceState)
    }

    protected abstract fun onViewCreated(binding: VIEW_BINDING, savedInstanceState: Bundle?)

    override fun onDestroyView() {
        this.binding = null
        super.onDestroyView()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState).apply {
            window?.setBackgroundDrawableResource(R.drawable.bg_dialog)
        }
    }

    override fun onResume() {
        super.onResume()
        dialog?.window?.apply {
            attributes = attributes.apply {
                width = (requireContext().getScreenWidth() * dialogWidthRatio).roundToInt()
                val dialogHeightRatio = dialogHeightRatio
                height = if (dialogHeightRatio != null) {
                    (requireContext().getScreenHeight() * dialogHeightRatio).roundToInt()
                } else {
                    LayoutParams.WRAP_CONTENT
                }
            }
        }
    }
}