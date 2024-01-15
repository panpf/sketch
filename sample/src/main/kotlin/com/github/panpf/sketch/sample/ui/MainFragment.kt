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
package com.github.panpf.sketch.sample.ui

import android.os.Build
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle.State
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.github.panpf.assemblyadapter.pager2.ArrayFragmentStateAdapter
import com.github.panpf.sketch.sample.R
import com.github.panpf.sketch.sample.databinding.FragmentMainBinding
import com.github.panpf.sketch.sample.ui.base.BaseToolbarBindingFragment
import com.github.panpf.sketch.sample.ui.gallery.ComposeHomeFragment
import com.github.panpf.sketch.sample.ui.gallery.ErrorStateFragment
import com.github.panpf.sketch.sample.ui.gallery.ViewHomeFragment
import com.github.panpf.sketch.sample.ui.setting.ToolbarMenuViewModel
import com.github.panpf.sketch.sample.ui.test.TestHomeFragment
import com.github.panpf.sketch.sample.util.repeatCollectWithLifecycle

class MainFragment : BaseToolbarBindingFragment<FragmentMainBinding>() {

    private val toolbarMenuViewModel by viewModels<ToolbarMenuViewModel> {
        ToolbarMenuViewModel.Factory(
            requireActivity().application,
            showLayoutModeMenu = true,
            showPlayMenu = true
        )
    }

    override fun onViewCreated(
        toolbar: Toolbar,
        binding: FragmentMainBinding,
        savedInstanceState: Bundle?
    ) {
        toolbar.apply {
            toolbarMenuViewModel.menuFlow
                .repeatCollectWithLifecycle(viewLifecycleOwner, State.STARTED) { list ->
                    menu.clear()
                    list.forEachIndexed { groupIndex, group ->
                        group.items.forEachIndexed { index, menuItemInfo ->
                            menu.add(groupIndex, index, index, menuItemInfo.title).apply {
                                menuItemInfo.iconResId?.let { iconResId ->
                                    setIcon(iconResId)
                                }
                                setOnMenuItemClickListener {
                                    menuItemInfo.onClick(this@MainFragment)
                                    true
                                }
                                setShowAsAction(menuItemInfo.showAsAction)
                            }
                        }
                    }
                }
        }

        binding.pager.apply {
            val composeFragment = if (Build.VERSION.SDK_INT >= 21) {
                ComposeHomeFragment()
            } else {
                ErrorStateFragment.create("This feature requires Android 5.0 or later")
            }

            adapter = ArrayFragmentStateAdapter(
                childFragmentManager,
                viewLifecycleOwner.lifecycle,
                listOf(
                    ViewHomeFragment(),
                    composeFragment,
                    TestHomeFragment()
                )
            )
            registerOnPageChangeCallback(object : OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    when (position) {
                        0 -> binding.navigation.selectedItemId = R.id.view
                        1 -> binding.navigation.selectedItemId = R.id.compose
                        2 -> binding.navigation.selectedItemId = R.id.test
                    }
                }
            })
            isUserInputEnabled = false
        }

        binding.navigation.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.view -> binding.pager.setCurrentItem(0, false)
                R.id.compose -> binding.pager.setCurrentItem(1, false)
                R.id.test -> binding.pager.setCurrentItem(2, false)
            }
            true
        }
    }
}