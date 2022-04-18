package com.github.panpf.sketch.sample.ui.setting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.panpf.assemblyadapter.recycler.AssemblyRecyclerAdapter
import com.github.panpf.sketch.sample.databinding.FragmentRecyclerBinding
import com.github.panpf.sketch.sample.ui.base.ToolbarBindingFragment
import com.github.panpf.sketch.sample.ui.common.list.ListSeparatorItemFactory
import com.github.panpf.sketch.sample.ui.common.menu.InfoMenuItemFactory
import com.github.panpf.sketch.sample.ui.common.menu.MultiSelectMenuItemFactory
import com.github.panpf.sketch.sample.ui.common.menu.SwitchMenuItemFactory

class SettingsFragment : ToolbarBindingFragment<FragmentRecyclerBinding>() {

    private val viewModel by viewModels<SettingsViewModel>()

    override fun createViewBinding(
        inflater: LayoutInflater,
        parent: ViewGroup?
    ) = FragmentRecyclerBinding.inflate(inflater, parent, false)

    override fun onInitData(
        toolbar: Toolbar,
        binding: FragmentRecyclerBinding,
        savedInstanceState: Bundle?
    ) {
        toolbar.title = "Settings"

        binding.refreshRecyclerFragment.isEnabled = false

        binding.hintRecyclerFragment.hidden()

        binding.recyclerRecyclerFragmentContent.apply {
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