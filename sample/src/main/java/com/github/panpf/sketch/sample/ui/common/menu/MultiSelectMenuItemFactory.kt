package com.github.panpf.sketch.sample.ui.common.menu

import android.content.Context
import android.util.TypedValue
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import com.github.panpf.activity.monitor.ActivityMonitor
import com.github.panpf.sketch.sample.databinding.MultiSelectMenuItemBinding
import com.github.panpf.sketch.sample.model.MultiSelectMenu
import com.github.panpf.sketch.sample.ui.common.list.MyBindingItemFactory

class MultiSelectMenuItemFactory(private val compactModel: Boolean = false) :
    MyBindingItemFactory<MultiSelectMenu, MultiSelectMenuItemBinding>(MultiSelectMenu::class) {

    override fun initItem(
        context: Context,
        binding: MultiSelectMenuItemBinding,
        item: BindingItem<MultiSelectMenu, MultiSelectMenuItemBinding>
    ) {
        binding.root.setOnClickListener {
            val data = item.dataOrThrow
            showDialog(data) {
                binding.multiSelectMenuItemInfoText.text = data.getValue()
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
        binding: MultiSelectMenuItemBinding,
        item: BindingItem<MultiSelectMenu, MultiSelectMenuItemBinding>,
        bindingAdapterPosition: Int,
        absoluteAdapterPosition: Int,
        data: MultiSelectMenu
    ) {
        binding.multiSelectMenuItemTitleText.text = data.title
        binding.multiSelectMenuItemInfoText.text = data.getValue()
        binding.multiSelectMenuItemDescText.text = data.desc
        binding.multiSelectMenuItemDescText.isVisible =
            !compactModel && data.desc?.isNotEmpty() == true
    }

    private fun showDialog(data: MultiSelectMenu, after: () -> Unit) {
        val activity = ActivityMonitor.getLastResumedActivity() ?: return
        AlertDialog.Builder(activity).apply {
            setItems(data.values.toTypedArray()) { _, which ->
                data.onSelect(which, data.values[which])
                after()
            }
        }.show()
    }
}
