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

import android.app.Dialog
import android.os.Bundle
import android.view.ViewGroup.LayoutParams
import androidx.fragment.app.DialogFragment
import com.github.panpf.sketch.sample.AppSettings
import com.github.panpf.sketch.sample.R
import com.github.panpf.tools4a.display.ktx.getScreenHeight
import com.github.panpf.tools4a.display.ktx.getScreenWidth
import org.koin.android.ext.android.inject
import kotlin.math.roundToInt

abstract class BaseDialogFragment : DialogFragment() {

    protected val appSettings: AppSettings by inject()

    protected var dialogWidthRatio: Float = 0.85f
    protected var dialogHeightRatio: Float? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, 0)
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