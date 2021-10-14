package me.panpf.sketch.sample.item

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.github.panpf.assemblyadapter.BindingItemFactory
import me.panpf.sketch.sample.bean.InfoMenu
import me.panpf.sketch.sample.databinding.ItemInfoMenuBinding

class InfoMenuItemFactory(private val onClick: (() -> Unit)? = null) :
    BindingItemFactory<InfoMenu, ItemInfoMenuBinding>(InfoMenu::class) {

    override fun createItemViewBinding(
        context: Context,
        inflater: LayoutInflater,
        parent: ViewGroup
    ) = ItemInfoMenuBinding.inflate(inflater, parent, false)

    override fun initItem(
        context: Context,
        binding: ItemInfoMenuBinding,
        item: BindingItem<InfoMenu, ItemInfoMenuBinding>
    ) {
        binding.root.setOnClickListener {
            item.dataOrThrow.onClick()
            onClick?.invoke()
        }
    }

    override fun bindItemData(
        context: Context,
        binding: ItemInfoMenuBinding,
        item: BindingItem<InfoMenu, ItemInfoMenuBinding>,
        bindingAdapterPosition: Int,
        absoluteAdapterPosition: Int,
        data: InfoMenu
    ) {
        binding.infoMenuItemTitleText.text = data.title
        binding.infoMenuItemInfoText.text = data.getInfo()
    }
}
