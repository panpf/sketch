package com.github.panpf.sketch.sample.ui.setting

import android.os.Bundle
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.panpf.assemblyadapter.recycler.AssemblyRecyclerAdapter
import com.github.panpf.sketch.sample.databinding.RecyclerFragmentBinding
import com.github.panpf.sketch.sample.ui.base.BindingDialogFragment
import com.github.panpf.sketch.sample.ui.common.list.ListSeparatorItemFactory
import com.github.panpf.sketch.sample.ui.common.menu.InfoMenuItemFactory
import com.github.panpf.sketch.sample.ui.common.menu.MultiSelectMenuItemFactory
import com.github.panpf.sketch.sample.ui.common.menu.SwitchMenuItemFactory

class SettingsDialogFragment : BindingDialogFragment<RecyclerFragmentBinding>() {

    private val viewModel by viewModels<SettingsViewModel>()

    init {
        dialogHeightRatio = 0.7f
    }

    override fun onViewCreated(binding: RecyclerFragmentBinding, savedInstanceState: Bundle?) {
        binding.recyclerRefresh.isEnabled = false

        binding.recyclerHint.hidden()

        binding.recyclerRecycler.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = AssemblyRecyclerAdapter<Any>(
                listOf(
                    SwitchMenuItemFactory(compactModel = true),
                    InfoMenuItemFactory(compactModel = true),
                    MultiSelectMenuItemFactory(compactModel = true),
                    ListSeparatorItemFactory(),
                )
            ).apply {
                viewModel.menuListData.observe(viewLifecycleOwner) {
                    submitList(it)
                }
            }
        }
    }
}