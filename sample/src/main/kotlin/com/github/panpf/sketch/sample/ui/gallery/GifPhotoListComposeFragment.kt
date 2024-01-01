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
package com.github.panpf.sketch.sample.ui.gallery

import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.viewModels
import androidx.paging.PagingData
import com.github.panpf.sketch.sample.model.Photo
import kotlinx.coroutines.flow.Flow

class GifPhotoListComposeFragment : BasePhotoListComposeFragment() {

    override val showPlayMenu: Boolean
        get() = true
    override val animatedPlaceholder: Boolean
        get() = true
    override val photoPagingFlow: Flow<PagingData<Photo>>
        get() = gifPhotoListViewModel.pagingFlow

    private val gifPhotoListViewModel by viewModels<GifPhotoListViewModel>()

    override fun onViewCreated(toolbar: Toolbar, savedInstanceState: Bundle?) {
        super.onViewCreated(toolbar, savedInstanceState)
        toolbar.apply {
            title = "Giphy GIFs"
            subtitle = "Compose"
        }
    }
}