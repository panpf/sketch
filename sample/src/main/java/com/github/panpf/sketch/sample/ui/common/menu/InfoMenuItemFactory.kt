package com.github.panpf.sketch.sample.ui.common.menu

import android.content.Context
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.github.panpf.assemblyadapter.BindingItemFactory
import com.github.panpf.sketch.sample.databinding.ItemMenuInfoBinding
import com.github.panpf.sketch.sample.model.InfoMenu

class InfoMenuItemFactory(private val compactModel: Boolean = false) :
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
            val data = item.dataOrThrow
            data.onClick()
            binding.infoMenuItemInfoText.text = data.getInfo()
        }

        if (compactModel) {
            binding.infoMenuItemTitleText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
            binding.infoMenuItemDescText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10f)
            binding.infoMenuItemInfoText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f)
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
        binding.infoMenuItemDescText.text = data.desc
        binding.infoMenuItemDescText.isVisible = !compactModel && data.desc?.isNotEmpty() == true
    }
}
