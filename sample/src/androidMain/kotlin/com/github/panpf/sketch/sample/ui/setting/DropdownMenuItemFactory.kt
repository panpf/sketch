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
package com.github.panpf.sketch.sample.ui.setting

import android.app.Activity
import android.content.Context
import android.util.TypedValue
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import com.github.panpf.sketch.sample.databinding.ListItemMenuDropdownBinding
import com.github.panpf.sketch.sample.model.MultiSelectMenu
import com.github.panpf.sketch.sample.ui.base.BaseBindingItemFactory

class DropdownMenuItemFactory(
    private val activity: Activity,
    private val compactModel: Boolean = false
) :
    BaseBindingItemFactory<MultiSelectMenu, ListItemMenuDropdownBinding>(MultiSelectMenu::class) {

    override fun initItem(
        context: Context,
        binding: ListItemMenuDropdownBinding,
        item: BindingItem<MultiSelectMenu, ListItemMenuDropdownBinding>
    ) {
        binding.root.setOnClickListener {
            val data = item.dataOrThrow
            showDialog(data) {
                binding.infoText.text = data.getValue()
            }
        }

        if (compactModel) {
            binding.titleText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
            binding.descText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10f)
            binding.infoText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f)
        }
    }

    override fun bindItemData(
        context: Context,
        binding: ListItemMenuDropdownBinding,
        item: BindingItem<MultiSelectMenu, ListItemMenuDropdownBinding>,
        bindingAdapterPosition: Int,
        absoluteAdapterPosition: Int,
        data: MultiSelectMenu
    ) {
        binding.titleText.text = data.title
        binding.infoText.text = data.getValue()
        binding.descText.text = data.desc
        binding.descText.isVisible = data.desc?.isNotEmpty() == true
    }

    private fun showDialog(data: MultiSelectMenu, after: () -> Unit) {
        AlertDialog.Builder(activity).apply {
            setItems(data.values.toTypedArray()) { _, which ->
                data.onSelect(which, data.values[which])
                after()
            }
        }.show()
    }
}
