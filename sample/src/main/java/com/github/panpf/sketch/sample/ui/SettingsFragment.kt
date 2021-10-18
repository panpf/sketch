package com.github.panpf.sketch.sample.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.panpf.assemblyadapter.recycler.AssemblyRecyclerAdapter
import com.github.panpf.sketch.SLog
import com.github.panpf.sketch.sample.appSettingsService
import com.github.panpf.sketch.sample.base.ToolbarBindingFragment
import com.github.panpf.sketch.sample.databinding.FragmentRecyclerBinding
import com.github.panpf.sketch.sample.item.CheckMenuItemFactory
import com.github.panpf.sketch.sample.item.InfoMenuItemFactory
import com.github.panpf.sketch.sample.item.ListSeparatorItemFactory
import com.github.panpf.sketch.sample.vm.SettingsViewModel

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
                    ListSeparatorItemFactory(),
                    CheckMenuItemFactory {
                        viewModel.update()
                    },
                    InfoMenuItemFactory {
                        viewModel.update()
                    }
                )
            ).apply {
                viewModel.menuListData.observe(viewLifecycleOwner) {
                    submitList(it)
                }
            }
        }

        viewModel.showLogLevelDialogEvent.listen(viewLifecycleOwner) {
            AlertDialog.Builder(requireActivity()).apply {
                setTitle("Switch Log Level")
                val items = arrayOf(
                    "VERBOSE" + if (SLog.getLevel() == SLog.VERBOSE) " (*)" else "",
                    "DEBUG" + if (SLog.getLevel() == SLog.DEBUG) " (*)" else "",
                    "INFO" + if (SLog.getLevel() == SLog.INFO) " (*)" else "",
                    "WARNING" + if (SLog.getLevel() == SLog.WARNING) " (*)" else "",
                    "ERROR" + if (SLog.getLevel() == SLog.ERROR) " (*)" else "",
                    "NONE" + if (SLog.getLevel() == SLog.NONE) " (*)" else ""
                )
                setItems(items) { _, which ->
                    when (which) {
                        0 -> appSettingsService.logLevel.postValue(SLog.VERBOSE)
                        1 -> appSettingsService.logLevel.postValue(SLog.DEBUG)
                        2 -> appSettingsService.logLevel.postValue(SLog.INFO)
                        3 -> appSettingsService.logLevel.postValue(SLog.WARNING)
                        4 -> appSettingsService.logLevel.postValue(SLog.ERROR)
                        5 -> appSettingsService.logLevel.postValue(SLog.NONE)
                    }
                    viewModel.update()
                }
                setPositiveButton("Cancel", null)
            }.show()
        }
    }
}