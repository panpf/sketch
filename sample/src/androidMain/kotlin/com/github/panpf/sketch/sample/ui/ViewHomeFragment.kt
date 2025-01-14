/*
 * Copyright (C) 2024 panpf <panpfpanpf@outlook.com>
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

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Lifecycle.State
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.github.panpf.assemblyadapter.pager2.ArrayFragmentStateAdapter
import com.github.panpf.sketch.sample.DarkMode
import com.github.panpf.sketch.sample.NavMainDirections
import com.github.panpf.sketch.sample.R
import com.github.panpf.sketch.sample.appSettings
import com.github.panpf.sketch.sample.applyDarkMode
import com.github.panpf.sketch.sample.databinding.FragmentViewHomeBinding
import com.github.panpf.sketch.sample.platformSupportedDarkModes
import com.github.panpf.sketch.sample.ui.base.BaseBindingFragment
import com.github.panpf.sketch.sample.ui.gallery.GiphyPhotoListFragment
import com.github.panpf.sketch.sample.ui.gallery.LocalPhotoListFragment
import com.github.panpf.sketch.sample.ui.gallery.PexelsPhotoListFragment
import com.github.panpf.sketch.sample.ui.setting.Page
import com.github.panpf.sketch.sample.ui.test.TestHomeFragment
import com.github.panpf.sketch.sample.util.repeatCollectWithLifecycle

class ViewHomeFragment : BaseBindingFragment<FragmentViewHomeBinding>() {

    private val fragments = listOf(
        "Pexels" to PexelsPhotoListFragment(),
        "Giphy" to GiphyPhotoListFragment(),
        "Local" to LocalPhotoListFragment(),
        "Test" to TestHomeFragment(),
    )

    override fun getStatusBarInsetsView(binding: FragmentViewHomeBinding): View {
        return binding.root
    }

    override fun onViewCreated(binding: FragmentViewHomeBinding, savedInstanceState: Bundle?) {
        binding.toolbar.subtitle = "View"

        binding.playImage.apply {
            appSettings.disallowAnimatedImageInList
                .repeatCollectWithLifecycle(viewLifecycleOwner, State.CREATED) {
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
                .repeatCollectWithLifecycle(viewLifecycleOwner, State.CREATED) {
                    val iconResId =
                        if (it) R.drawable.ic_layout_grid else R.drawable.ic_layout_grid_staggered
                    setImageResource(iconResId)
                }
            setOnClickListener {
                appSettings.staggeredGridMode.value = !appSettings.staggeredGridMode.value
            }
        }

        binding.composePageIcon.setOnClickListener {
            appSettings.composePage.value = true
        }

        binding.darkModeIcon.apply {
            val nextDarkMode: () -> DarkMode = {
                val appSettings = binding.darkModeIcon.appSettings
                val darkMode = appSettings.darkMode.value
                val platformSupportedDarkModes = platformSupportedDarkModes()
                val index = platformSupportedDarkModes.indexOf(darkMode)
                val nextDarkModeIndex = (index + 1) % platformSupportedDarkModes.size
                platformSupportedDarkModes[nextDarkModeIndex]
            }
            val setIcon: (DarkMode) -> Unit = {
                val icon = when (it) {
                    DarkMode.SYSTEM -> R.drawable.ic_auto_mode
                    DarkMode.LIGHT -> R.drawable.ic_light_mode
                    DarkMode.DARK -> R.drawable.ic_dark_mode
                }
                setImageResource(icon)
            }
            setIcon(nextDarkMode())

            setOnClickListener {
                appSettings.darkMode.value = nextDarkMode()
                setIcon(nextDarkMode())
                applyDarkMode(requireContext())
            }
        }

        binding.settingsImage.setOnClickListener {
            findNavController().navigate(NavMainDirections.actionSettingsDialogFragment(Page.LIST.name))
        }

        binding.pager.apply {
            adapter = ArrayFragmentStateAdapter(
                fragmentManager = childFragmentManager,
                lifecycle = viewLifecycleOwner.lifecycle,
                templateFragmentList = fragments.map { it.second }
            )
            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    appSettings.currentPageIndex.value = position
                    when (position) {
                        0 -> binding.navigation.selectedItemId = R.id.pexels
                        1 -> binding.navigation.selectedItemId = R.id.giphy
                        2 -> binding.navigation.selectedItemId = R.id.local
                        3 -> binding.navigation.selectedItemId = R.id.test
                    }
                }
            })
            setCurrentItem(
                /* item = */ appSettings.currentPageIndex.value
                    .coerceIn(minimumValue = 0, maximumValue = fragments.size - 1),
                /* smoothScroll = */ false
            )
        }

        binding.navigation.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.pexels -> binding.pager.setCurrentItem(0, false)
                R.id.giphy -> binding.pager.setCurrentItem(1, false)
                R.id.local -> binding.pager.setCurrentItem(2, false)
                R.id.test -> binding.pager.setCurrentItem(3, false)
            }
            true
        }
    }
}