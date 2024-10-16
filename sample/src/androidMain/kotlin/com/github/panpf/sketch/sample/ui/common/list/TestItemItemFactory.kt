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
import com.github.panpf.sketch.sample.databinding.ListItemTestItemBinding
import com.github.panpf.sketch.sample.model.Link
import com.github.panpf.sketch.sample.ui.base.BaseBindingItemFactory

class TestItemItemFactory : BaseBindingItemFactory<Link, ListItemTestItemBinding>(Link::class) {

    override fun initItem(
        context: Context,
        binding: ListItemTestItemBinding,
        item: BindingItem<Link, ListItemTestItemBinding>
    ) {
    }

    override fun bindItemData(
        context: Context,
        binding: ListItemTestItemBinding,
        item: BindingItem<Link, ListItemTestItemBinding>,
        bindingAdapterPosition: Int,
        absoluteAdapterPosition: Int,
        data: Link
    ) {
        binding.titleText.text = data.title
    }
}