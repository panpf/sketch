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

package com.github.panpf.sketch.compose.sample.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.compose.ui.platform.ComposeView
import com.github.panpf.sketch.cache.CachePolicy.DISABLED
import com.github.panpf.sketch.compose.AsyncImage
import com.github.panpf.sketch.compose.sample.base.ToolbarFragment
import com.github.panpf.sketch.compose.sample.R
import com.github.panpf.sketch.compose.sample.R.drawable

class ComposeFragment : ToolbarFragment() {
    override fun createView(toolbar: Toolbar, inflater: LayoutInflater, parent: ViewGroup?): View =
        ComposeView(requireContext()).apply {
            setContent {
                AsyncImage(
                    "https://i0.hdslb.com/bfs/album/c8cf4ec6849dff7115a368e980b6fd52878cf213.jpg@1036w.webp",
                    contentDescription = ""
                ) {
                    placeholder(R.drawable.im_placeholder)
                    error(drawable.im_error)
                    bitmapMemoryCachePolicy(DISABLED)
                    bitmapResultDiskCachePolicy(DISABLED)
                    networkContentDiskCachePolicy(DISABLED)
                }
            }
        }
}