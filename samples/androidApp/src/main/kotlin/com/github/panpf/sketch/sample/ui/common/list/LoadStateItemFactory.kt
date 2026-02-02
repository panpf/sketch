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

package com.github.panpf.sketch.sample.ui.common.list

import android.content.Context
import androidx.core.view.isVisible
import androidx.paging.LoadState
import com.github.panpf.sketch.sample.databinding.ListItemLoadStateBinding
import com.github.panpf.sketch.sample.ui.base.BaseBindingItemFactory

class LoadStateItemFactory :
    BaseBindingItemFactory<LoadState, ListItemLoadStateBinding>(LoadState::class) {

    override fun initItem(
        context: Context,
        binding: ListItemLoadStateBinding,
        item: BindingItem<LoadState, ListItemLoadStateBinding>
    ) {
    }

    override fun bindItemData(
        context: Context,
        binding: ListItemLoadStateBinding,
        item: BindingItem<LoadState, ListItemLoadStateBinding>,
        bindingAdapterPosition: Int,
        absoluteAdapterPosition: Int,
        data: LoadState
    ) {
        binding.loadingLayout.isVisible = data is LoadState.Loading
        binding.errorText.isVisible = data is LoadState.Error
        binding.endText.isVisible =
            data is LoadState.NotLoading && data.endOfPaginationReached
    }
}
