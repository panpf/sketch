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
import android.view.View
import androidx.lifecycle.Lifecycle.State
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.github.panpf.assemblyadapter.pager2.ArrayFragmentStateAdapter
import com.github.panpf.sketch.sample.NavMainDirections
import com.github.panpf.sketch.sample.R
import com.github.panpf.sketch.sample.appSettings
import com.github.panpf.sketch.sample.databinding.FragmentViewHomeBinding
import com.github.panpf.sketch.sample.ui.base.BaseBindingFragment
import com.github.panpf.sketch.sample.ui.setting.Page
import com.github.panpf.sketch.sample.ui.test.TestHomeFragment
import com.github.panpf.sketch.sample.util.repeatCollectWithLifecycle

class ViewHomeFragment : BaseBindingFragment<FragmentViewHomeBinding>() {

    private val fragmentMap = mapOf(
        "Local" to LocalPhotoListViewFragment(),
        "Pexels" to PexelsPhotoListViewFragment(),
        "Giphy" to GiphyPhotoListViewFragment(),
        "Test" to TestHomeFragment(),
    )

    override fun getStatusBarInsetsView(binding: FragmentViewHomeBinding): View {
        return binding.root
    }

    override fun onViewCreated(binding: FragmentViewHomeBinding, savedInstanceState: Bundle?) {
        binding.toolbar.subtitle = "View"

        binding.playImage.apply {
            appSettings.disallowAnimatedImageInList
                .repeatCollectWithLifecycle(viewLifecycleOwner, State.STARTED) {
                    val iconResId = if (it) R.drawable.ic_play else R.drawable.ic_pause
                    setImageResource(iconResId)
                }
            setOnClickListener {
                appSettings.disallowAnimatedImageInList.value =
                    !appSettings.disallowAnimatedImageInList.value
            }
        }

        binding.layoutImage.apply {
            val appSettings = context.appSettings
            appSettings.staggeredGridMode
                .repeatCollectWithLifecycle(viewLifecycleOwner, State.STARTED) {
                    val iconResId =
                        if (it) R.drawable.ic_layout_grid else R.drawable.ic_layout_grid_staggered
                    setImageResource(iconResId)
                }
            setOnClickListener {
                appSettings.staggeredGridMode.value = !appSettings.staggeredGridMode.value
            }
        }

        binding.composePageIconLayout.setOnClickListener {
            appSettings.composePage.value = true
        }

        binding.settingsImage.setOnClickListener {
            findNavController().navigate(NavMainDirections.actionSettingsDialogFragment(Page.LIST.name))
        }

        binding.pager.apply {
            adapter = ArrayFragmentStateAdapter(
                fragmentManager = childFragmentManager,
                lifecycle = viewLifecycleOwner.lifecycle,
                templateFragmentList = fragmentMap.values.toList()
            )
            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    appSettings.currentPageIndex.value = position
                    when (position) {
                        0 -> binding.navigation.selectedItemId = R.id.local
                        1 -> binding.navigation.selectedItemId = R.id.pexels
                        2 -> binding.navigation.selectedItemId = R.id.giphy
                        3 -> binding.navigation.selectedItemId = R.id.test
                    }
                }
            })
            setCurrentItem(
                appSettings.currentPageIndex.value.coerceIn(
                    0,
                    fragmentMap.size - 1
                ), false
            )
        }

        binding.navigation.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.local -> binding.pager.setCurrentItem(0, false)
                R.id.pexels -> binding.pager.setCurrentItem(1, false)
                R.id.giphy -> binding.pager.setCurrentItem(2, false)
                R.id.test -> binding.pager.setCurrentItem(3, false)
            }
            true
        }
    }
}