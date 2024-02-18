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
package com.github.panpf.sketch.sample.ui.gallery

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle.State
import androidx.navigation.fragment.findNavController
import com.github.panpf.assemblyadapter.pager2.ArrayFragmentStateAdapter
import com.github.panpf.sketch.sample.R
import com.github.panpf.sketch.sample.appSettingsService
import com.github.panpf.sketch.sample.databinding.FragmentSamplesBinding
import com.github.panpf.sketch.sample.model.LayoutMode
import com.github.panpf.sketch.sample.ui.MainFragmentDirections
import com.github.panpf.sketch.sample.ui.base.BaseBindingFragment
import com.github.panpf.sketch.sample.ui.setting.Page
import com.github.panpf.sketch.sample.util.repeatCollectWithLifecycle
import com.google.android.material.tabs.TabLayoutMediator
import com.github.panpf.sketch.sample.appSettings

abstract class BaseHomeFragment : BaseBindingFragment<FragmentSamplesBinding>() {

    abstract val fragmentMap: Map<String, Fragment>

    private var resumedCount = 0
    private var createdCount = 0

    override fun onViewCreated(binding: FragmentSamplesBinding, savedInstanceState: Bundle?) {
        createdCount++
    }

    override fun onResume() {
        super.onResume()

        resumedCount++
        if (resumedCount == 1 && createdCount == 1) {
            onFirstResume()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        createdCount--
        resumedCount = 0
    }

    /*
     * The old version of ViewPager will always load the content of the second screen
     */
    private fun onFirstResume() {
        val binding = binding ?: return
        val titles = fragmentMap.keys.toList()
        val fragments = fragmentMap.values.toList()

        binding.pager.apply {
            adapter = ArrayFragmentStateAdapter(
                fragmentManager = childFragmentManager,
                lifecycle = viewLifecycleOwner.lifecycle,
                templateFragmentList = fragments
            )
        }

        TabLayoutMediator(binding.tabLayout, binding.pager) { tab, position ->
            tab.text = titles[position]
        }.attach()

        binding.playImage.apply {
            appSettingsService.disallowAnimatedImageInList.repeatCollectWithLifecycle(
                viewLifecycleOwner,
                State.STARTED
            ) {
                val iconResId = if (it) R.drawable.ic_play else R.drawable.ic_pause
                setImageResource(iconResId)
            }
            setOnClickListener {
                appSettingsService.disallowAnimatedImageInList.value =
                    !appSettingsService.disallowAnimatedImageInList.value
            }
        }

        binding.layoutImage.apply {
//            val appSettings = appSettingsService
            val appSettings = context.appSettings
            appSettings.photoListLayoutMode.repeatCollectWithLifecycle(
                viewLifecycleOwner,
                State.STARTED
            ) {
                val iconResId =
                    if (it == LayoutMode.GRID.name) R.drawable.ic_layout_grid_staggered else R.drawable.ic_layout_grid
                setImageResource(iconResId)
            }
            setOnClickListener {
                appSettings.photoListLayoutMode.value =
                    if (appSettings.photoListLayoutMode.value == LayoutMode.GRID.name) {
                        LayoutMode.STAGGERED_GRID.name
                    } else {
                        LayoutMode.GRID.name
                    }
            }
        }

        binding.settingsImage.setOnClickListener {
            findNavController().navigate(MainFragmentDirections.actionSettingsDialogFragment(Page.LIST.name))
        }
    }
}