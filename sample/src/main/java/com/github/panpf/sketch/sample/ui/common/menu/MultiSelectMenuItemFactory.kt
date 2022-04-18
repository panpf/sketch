package com.github.panpf.sketch.sample.ui.common.menu

import android.content.Context
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import com.github.panpf.activity.monitor.ActivityMonitor
import com.github.panpf.assemblyadapter.BindingItemFactory
import com.github.panpf.sketch.sample.databinding.ItemMenuMultiSelectBinding
import com.github.panpf.sketch.sample.model.MultiSelectMenu

class MultiSelectMenuItemFactory(private val compactModel: Boolean = false) :
    BindingItemFactory<MultiSelectMenu, ItemMenuMultiSelectBinding>(MultiSelectMenu::class) {

    override fun createItemViewBinding(
        context: Context,
        inflater: LayoutInflater,
        parent: ViewGroup
    ) = ItemMenuMultiSelectBinding.inflate(inflater, parent, false)

    override fun initItem(
        context: Context,
        binding: ItemMenuMultiSelectBinding,
        item: BindingItem<MultiSelectMenu, ItemMenuMultiSelectBinding>
    ) {
        binding.root.setOnClickListener {
            val data = item.dataOrThrow
            showDialog(data) {
                binding.multiSelectMenuItemInfoText.text = data.value()
            }
        }

        if (compactModel) {
            binding.multiSelectMenuItemTitleText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
            binding.multiSelectMenuItemDescText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10f)
            binding.multiSelectMenuItemInfoText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f)
        }
    }

    override fun bindItemData(
        context: Context,
        binding: ItemMenuMultiSelectBinding,
        item: BindingItem<MultiSelectMenu, ItemMenuMultiSelectBinding>,
        bindingAdapterPosition: Int,
        absoluteAdapterPosition: Int,
        data: MultiSelectMenu
    ) {
        binding.multiSelectMenuItemTitleText.text = data.title
        binding.multiSelectMenuItemInfoText.text = data.value()
        binding.multiSelectMenuItemDescText.text = data.desc
        binding.multiSelectMenuItemDescText.isVisible =
            !compactModel && data.desc?.isNotEmpty() == true
    }

    private fun showDialog(data: MultiSelectMenu, after: () -> Unit) {
        val activity = ActivityMonitor.getLastResumedActivity() ?: return
        AlertDialog.Builder(activity).apply {
            setItems(data.values.toTypedArray()) { _, which ->
                data.onSelect(which)
                after()
            }
        }.show()
    }
}
