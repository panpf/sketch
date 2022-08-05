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
import com.github.panpf.sketch.sample.databinding.SwitchMenuItemBinding
import com.github.panpf.sketch.sample.model.SwitchMenu
import com.github.panpf.sketch.sample.ui.common.list.MyBindingItemFactory

class SwitchMenuItemFactory(
    private val compactModel: Boolean = false,
) : MyBindingItemFactory<SwitchMenu, SwitchMenuItemBinding>(SwitchMenu::class) {

    override fun initItem(
        context: Context,
        binding: SwitchMenuItemBinding,
        item: BindingItem<SwitchMenu, SwitchMenuItemBinding>
    ) {
        binding.root.setOnClickListener {
            binding.switchMenuItemSwitch.isChecked = !binding.switchMenuItemSwitch.isChecked
        }
        binding.root.setOnLongClickListener {
            val data = item.dataOrThrow
            data.onLongClick?.invoke()
            true
        }
        binding.switchMenuItemSwitch.setOnCheckedChangeListener { _, isChecked ->
            val data = item.dataOrThrow
            if (data.isChecked != isChecked) {
                data.isChecked = isChecked
            }
        }

        if (compactModel) {
            binding.switchMenuItemTitleText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
            binding.switchMenuItemDescText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10f)
        }
    }

    override fun bindItemData(
        context: Context,
        binding: SwitchMenuItemBinding,
        item: BindingItem<SwitchMenu, SwitchMenuItemBinding>,
        bindingAdapterPosition: Int,
        absoluteAdapterPosition: Int,
        data: SwitchMenu
    ) {
        binding.switchMenuItemTitleText.text = data.title
        binding.switchMenuItemSwitch.isChecked = data.isChecked
        binding.switchMenuItemDescText.text = data.desc
    }
}
