/*
 * Copyright (C) 2019 panpf <panpfpanpf@outlook.com>
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

package com.github.panpf.sketch.sample.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.Image
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.painterResource
import com.github.panpf.sketch.sample.base.ToolbarFragment
import com.github.panpf.sketch.sample.compose.R

class ComposeFragment : ToolbarFragment() {
    override fun createView(inflater: LayoutInflater, parent: ViewGroup?): View {
        return ComposeView(requireContext()).apply {
            setContent {
                Image(painter = painterResource(id = R.mipmap.ic_launcher), contentDescription = "")
            }
        }
    }
}