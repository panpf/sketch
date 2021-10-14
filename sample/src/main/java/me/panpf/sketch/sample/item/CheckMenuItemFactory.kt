package me.panpf.sketch.sample.item

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.github.panpf.assemblyadapter.BindingItemFactory
import me.panpf.sketch.sample.bean.CheckMenu
import me.panpf.sketch.sample.databinding.ItemCheckBoxMenuBinding

class CheckMenuItemFactory(private val onClick: (() -> Unit)? = null) :
    BindingItemFactory<CheckMenu, ItemCheckBoxMenuBinding>(CheckMenu::class) {
    override fun createItemViewBinding(
        context: Context,
        inflater: LayoutInflater,
        parent: ViewGroup
    ) = ItemCheckBoxMenuBinding.inflate(inflater, parent, false)

    override fun initItem(
        context: Context,
        binding: ItemCheckBoxMenuBinding,
        item: BindingItem<CheckMenu, ItemCheckBoxMenuBinding>
    ) {
        binding.root.setOnClickListener {
            item.dataOrThrow.onClick()
            onClick?.invoke()
        }
    }

    override fun bindItemData(
        context: Context,
        binding: ItemCheckBoxMenuBinding,
        item: BindingItem<CheckMenu, ItemCheckBoxMenuBinding>,
        bindingAdapterPosition: Int,
        absoluteAdapterPosition: Int,
        data: CheckMenu
    ) {
        binding.checkBoxMenuItemTitleText.text = data.title
        binding.checkBoxMenuItemCheckBox.isChecked = data.isChecked
    }
}
