package com.github.panpf.sketch.sample.item

import android.content.Context
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.github.panpf.assemblyadapter.BindingItemFactory
import com.github.panpf.sketch.sample.bean.SwitchMenu
import com.github.panpf.sketch.sample.databinding.ItemMenuSwitchBinding

class SwitchMenuItemFactory(
    private val compactModel: Boolean = false,
) : BindingItemFactory<SwitchMenu, ItemMenuSwitchBinding>(SwitchMenu::class) {

    override fun createItemViewBinding(
        context: Context,
        inflater: LayoutInflater,
        parent: ViewGroup
    ) = ItemMenuSwitchBinding.inflate(inflater, parent, false)

    override fun initItem(
        context: Context,
        binding: ItemMenuSwitchBinding,
        item: BindingItem<SwitchMenu, ItemMenuSwitchBinding>
    ) {
        binding.root.setOnClickListener {
            binding.switchMenuItemSwitch.isChecked = !binding.switchMenuItemSwitch.isChecked
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
        binding: ItemMenuSwitchBinding,
        item: BindingItem<SwitchMenu, ItemMenuSwitchBinding>,
        bindingAdapterPosition: Int,
        absoluteAdapterPosition: Int,
        data: SwitchMenu
    ) {
        binding.switchMenuItemTitleText.text = data.title
        binding.switchMenuItemSwitch.isChecked = data.isChecked
        binding.switchMenuItemDescText.text = data.desc
        binding.switchMenuItemDescText.isVisible = !compactModel && data.desc?.isNotEmpty() == true
    }
}
