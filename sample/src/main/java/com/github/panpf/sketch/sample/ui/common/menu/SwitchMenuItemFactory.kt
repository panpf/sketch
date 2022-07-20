package com.github.panpf.sketch.sample.ui.common.menu

import android.content.Context
import android.util.TypedValue
import androidx.core.view.isVisible
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
