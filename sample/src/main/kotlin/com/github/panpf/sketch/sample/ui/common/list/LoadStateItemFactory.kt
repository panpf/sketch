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
package com.github.panpf.sketch.sample.ui.common.list

import android.content.Context
import androidx.core.view.isVisible
import androidx.paging.LoadState
import com.github.panpf.sketch.sample.databinding.LoadStateItemBinding
import com.github.panpf.sketch.sample.ui.base.BaseBindingItemFactory

class LoadStateItemFactory :
    BaseBindingItemFactory<LoadState, LoadStateItemBinding>(LoadState::class) {

    override fun initItem(
        context: Context,
        binding: LoadStateItemBinding,
        item: BindingItem<LoadState, LoadStateItemBinding>
    ) {
    }

    override fun bindItemData(
        context: Context,
        binding: LoadStateItemBinding,
        item: BindingItem<LoadState, LoadStateItemBinding>,
        bindingAdapterPosition: Int,
        absoluteAdapterPosition: Int,
        data: LoadState
    ) {
        binding.loadStateItemLoadingGroup.isVisible = data is LoadState.Loading
        binding.loadStateItemErrorText.isVisible = data is LoadState.Error
        binding.loadStateItemEndText.isVisible =
            data is LoadState.NotLoading && data.endOfPaginationReached
    }
}
