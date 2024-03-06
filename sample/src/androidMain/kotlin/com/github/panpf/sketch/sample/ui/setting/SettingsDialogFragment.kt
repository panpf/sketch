/*
 * Copyright (C) 2022 panpf <panpfpanpf@outlook.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.panpf.sketch.sample.ui.setting

import android.os.Bundle
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle.State
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.panpf.assemblyadapter.recycler.AssemblyRecyclerAdapter
import com.github.panpf.sketch.sample.databinding.FragmentRecyclerBinding
import com.github.panpf.sketch.sample.ui.base.BaseBindingDialogFragment
import com.github.panpf.sketch.sample.ui.common.list.ListSeparatorItemFactory
import com.github.panpf.sketch.sample.ui.setting.Page
import com.github.panpf.sketch.sample.util.repeatCollectWithLifecycle

class SettingsDialogFragment : BaseBindingDialogFragment<FragmentRecyclerBinding>() {

    private val args by navArgs<SettingsDialogFragmentArgs>()
    private val viewModel by viewModels<SettingsViewModel> {
        SettingsViewModel.Factory(requireActivity().application, Page.valueOf(args.page))
    }

    init {
        dialogHeightRatio = 0.7f
    }

    override fun onViewCreated(binding: FragmentRecyclerBinding, savedInstanceState: Bundle?) {
        binding.recycler.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = AssemblyRecyclerAdapter<Any>(
                listOf(
                    SwitchMenuItemFactory(compactModel = true),
                    InfoMenuItemFactory(compactModel = true),
                    MultiSelectMenuItemFactory(compactModel = true),
                    ListSeparatorItemFactory(),
                )
            ).apply {
                viewModel.menuListData.repeatCollectWithLifecycle(
                    owner = viewLifecycleOwner,
                    state = State.STARTED
                ) {
                    submitList(it)
                }
            }
        }
    }
}