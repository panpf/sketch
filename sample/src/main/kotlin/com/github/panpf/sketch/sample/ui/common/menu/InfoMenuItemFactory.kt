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
package com.github.panpf.sketch.sample.ui.common.menu

import android.content.Context
import android.util.TypedValue
import com.github.panpf.sketch.sample.databinding.InfoMenuItemBinding
import com.github.panpf.sketch.sample.model.InfoMenu
import com.github.panpf.sketch.sample.ui.base.BaseBindingItemFactory

class InfoMenuItemFactory(private val compactModel: Boolean = false) :
    BaseBindingItemFactory<InfoMenu, InfoMenuItemBinding>(InfoMenu::class) {

    override fun initItem(
        context: Context,
        binding: InfoMenuItemBinding,
        item: BindingItem<InfoMenu, InfoMenuItemBinding>
    ) {
        binding.root.setOnClickListener {
            val data = item.dataOrThrow
            data.onClick()
        }
        binding.root.setOnLongClickListener {
            val data = item.dataOrThrow
            data.onLongClick?.invoke()
            true
        }

        if (compactModel) {
            binding.infoMenuItemTitleText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
            binding.infoMenuItemDescText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10f)
            binding.infoMenuItemInfoText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f)
        }
    }

    override fun bindItemData(
        context: Context,
        binding: InfoMenuItemBinding,
        item: BindingItem<InfoMenu, InfoMenuItemBinding>,
        bindingAdapterPosition: Int,
        absoluteAdapterPosition: Int,
        data: InfoMenu
    ) {
        binding.infoMenuItemTitleText.text = data.title
        binding.infoMenuItemInfoText.text = data.info
        binding.infoMenuItemDescText.text = data.desc
    }
}
