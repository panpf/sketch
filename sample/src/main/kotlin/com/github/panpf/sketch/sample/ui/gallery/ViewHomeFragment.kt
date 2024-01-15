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
import com.github.panpf.assemblyadapter.pager2.ArrayFragmentStateAdapter
import com.github.panpf.sketch.sample.databinding.FragmentTabPagerBinding
import com.github.panpf.sketch.sample.ui.base.BaseBindingFragment
import com.google.android.material.tabs.TabLayoutMediator

class ViewHomeFragment : BaseBindingFragment<FragmentTabPagerBinding>() {

    private val fragmentMap = mapOf(
        "Local" to LocalPhotoListViewFragment(),
        "Pexels" to PexelsPhotoListViewFragment(),
        "Giphy" to GifPhotoListViewFragment()
    )

    override fun onViewCreated(
        binding: FragmentTabPagerBinding,
        savedInstanceState: Bundle?
    ) {
        val titles = fragmentMap.keys.toList()
        val fragments = fragmentMap.values.toList()

        binding.pager.apply {
            adapter = ArrayFragmentStateAdapter(
                fragmentManager = childFragmentManager,
                lifecycle = viewLifecycleOwner.lifecycle,
                templateFragmentList = fragments
            )
            offscreenPageLimit = 1
        }

        TabLayoutMediator(binding.tabLayout, binding.pager) { tab, position ->
            tab.text = titles[position]
        }.attach()
    }
}
