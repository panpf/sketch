package com.github.panpf.sketch.sample.item

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.github.panpf.assemblyadapter.BindingItemFactory
import com.github.panpf.sketch.sample.bean.InfoMenu
import com.github.panpf.sketch.sample.databinding.ItemMenuInfoBinding

class InfoMenuItemFactory :
    BindingItemFactory<InfoMenu, ItemMenuInfoBinding>(InfoMenu::class) {

    override fun createItemViewBinding(
        context: Context,
        inflater: LayoutInflater,
        parent: ViewGroup
    ) = ItemMenuInfoBinding.inflate(inflater, parent, false)

    override fun initItem(
        context: Context,
        binding: ItemMenuInfoBinding,
        item: BindingItem<InfoMenu, ItemMenuInfoBinding>
    ) {
        binding.root.setOnClickListener {
            item.dataOrThrow.onClick()
        }
    }

    override fun bindItemData(
        context: Context,
        binding: ItemMenuInfoBinding,
        item: BindingItem<InfoMenu, ItemMenuInfoBinding>,
        bindingAdapterPosition: Int,
        absoluteAdapterPosition: Int,
        data: InfoMenu
    ) {
        binding.infoMenuItemTitleText.text = data.title
        binding.infoMenuItemInfoText.text = data.getInfo()
    }
}
