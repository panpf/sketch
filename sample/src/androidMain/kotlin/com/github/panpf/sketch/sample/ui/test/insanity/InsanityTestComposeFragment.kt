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
package com.github.panpf.sketch.sample.ui.test.insanity

import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.fragment.app.viewModels
import com.github.panpf.sketch.sample.ui.base.BaseToolbarComposeFragment
import com.github.panpf.sketch.sample.ui.screen.PhotoGrid

class InsanityTestComposeFragment : BaseToolbarComposeFragment() {

    private val insanityTestViewModel by viewModels<InsanityTestViewModel>()

    @Composable
    override fun DrawContent() {
        PhotoGrid(
            photoPagingFlow = insanityTestViewModel.pagingFlow,
            animatedPlaceholder = false,
            gridCellsMinSize = 100.dp,
            onClick = { _, _, _ -> },
            onLongClick = { _, _, _, _ -> }
        )
    }

    override fun onViewCreated(toolbar: Toolbar, savedInstanceState: Bundle?) {
        super.onViewCreated(toolbar, savedInstanceState)
        toolbar.apply {
            title = "Insanity Test"
            subtitle = "Compose"
        }
    }
}