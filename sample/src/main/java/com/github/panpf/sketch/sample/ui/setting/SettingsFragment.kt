package com.github.panpf.sketch.sample.ui.setting

import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.panpf.assemblyadapter.recycler.AssemblyRecyclerAdapter
import com.github.panpf.sketch.sample.databinding.RecyclerFragmentBinding
import com.github.panpf.sketch.sample.ui.base.ToolbarBindingFragment
import com.github.panpf.sketch.sample.ui.common.list.ListSeparatorItemFactory
import com.github.panpf.sketch.sample.ui.common.menu.InfoMenuItemFactory
import com.github.panpf.sketch.sample.ui.common.menu.MultiSelectMenuItemFactory
import com.github.panpf.sketch.sample.ui.common.menu.SwitchMenuItemFactory

class SettingsFragment : ToolbarBindingFragment<RecyclerFragmentBinding>() {

    private val viewModel by viewModels<SettingsViewModel>()

    override fun onViewCreated(
        toolbar: Toolbar,
        binding: RecyclerFragmentBinding,
        savedInstanceState: Bundle?
    ) {
        toolbar.title = "Settings"

        binding.recyclerRefresh.isEnabled = false

        binding.recyclerState.gone()

        binding.recyclerRecycler.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = AssemblyRecyclerAdapter<Any>(
                listOf(
                    SwitchMenuItemFactory(),
                    InfoMenuItemFactory(),
                    MultiSelectMenuItemFactory(),
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