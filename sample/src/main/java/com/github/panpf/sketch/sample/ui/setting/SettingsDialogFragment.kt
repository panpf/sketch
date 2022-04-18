package com.github.panpf.sketch.sample.ui.setting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.panpf.assemblyadapter.recycler.AssemblyRecyclerAdapter
import com.github.panpf.sketch.sample.R
import com.github.panpf.sketch.sample.databinding.FragmentRecyclerBinding
import com.github.panpf.sketch.sample.ui.base.BindingDialogFragment
import com.github.panpf.sketch.sample.ui.common.list.ListSeparatorItemFactory
import com.github.panpf.sketch.sample.ui.common.menu.InfoMenuItemFactory
import com.github.panpf.sketch.sample.ui.common.menu.MultiSelectMenuItemFactory
import com.github.panpf.sketch.sample.ui.common.menu.SwitchMenuItemFactory
import com.github.panpf.tools4a.display.ktx.getScreenHeight
import com.github.panpf.tools4a.display.ktx.getScreenWidth
import kotlin.math.roundToInt


class SettingsDialogFragment : BindingDialogFragment<FragmentRecyclerBinding>() {

    private val viewModel by viewModels<SettingsViewModel>()

    override fun createViewBinding(
        inflater: LayoutInflater,
        parent: ViewGroup?
    ) = FragmentRecyclerBinding.inflate(inflater, parent, false)

    override fun onInitData(
        binding: FragmentRecyclerBinding,
        savedInstanceState: Bundle?
    ) {
        dialog?.window?.apply {
            attributes = attributes.apply {
                width = (requireContext().getScreenWidth() * 0.9).roundToInt()
                height = (requireContext().getScreenHeight() * 0.7).roundToInt()
            }
        }

        binding.root.apply {
            setBackgroundResource(R.color.windowBackground)
        }

        binding.refreshRecyclerFragment.isEnabled = false

        binding.hintRecyclerFragment.hidden()

        binding.recyclerRecyclerFragmentContent.apply {
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