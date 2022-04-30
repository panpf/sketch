package com.github.panpf.sketch.sample.ui.common.menu

import android.content.Context
import android.util.TypedValue
import androidx.core.view.isVisible
import com.github.panpf.sketch.sample.databinding.InfoMenuItemBinding
import com.github.panpf.sketch.sample.model.InfoMenu
import com.github.panpf.sketch.sample.ui.common.list.MyBindingItemFactory

class InfoMenuItemFactory(private val compactModel: Boolean = false) :
    MyBindingItemFactory<InfoMenu, InfoMenuItemBinding>(InfoMenu::class) {

    override fun initItem(
        context: Context,
        binding: InfoMenuItemBinding,
        item: BindingItem<InfoMenu, InfoMenuItemBinding>
    ) {
        binding.root.setOnClickListener {
            val data = item.dataOrThrow
            data.onClick()
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
        binding.infoMenuItemDescText.isVisible = !compactModel && data.desc?.isNotEmpty() == true
    }
}
