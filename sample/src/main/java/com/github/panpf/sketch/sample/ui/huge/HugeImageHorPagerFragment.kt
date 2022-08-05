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
package com.github.panpf.sketch.sample.ui.huge

import android.os.Bundle
import com.github.panpf.assemblyadapter.pager2.AssemblyFragmentStateAdapter
import com.github.panpf.sketch.sample.AssetImages
import com.github.panpf.sketch.sample.databinding.TabPagerFragmentBinding
import com.github.panpf.sketch.sample.ui.base.BindingFragment
import com.google.android.material.tabs.TabLayoutMediator

class HugeImageHorPagerFragment : BindingFragment<TabPagerFragmentBinding>() {

    override fun onViewCreated(binding: TabPagerFragmentBinding, savedInstanceState: Bundle?) {
        val images = AssetImages.HUGES.plus(AssetImages.LONGS).toList()
        val titles = arrayOf("WORLD", "CARD", "QMSHT", "CWB")

        binding.tabPagerPager.adapter = AssemblyFragmentStateAdapter(
            this,
            listOf(HugeImageViewerFragment.ItemFactory()),
            images
        )

        TabLayoutMediator(binding.tabPagerTabLayout, binding.tabPagerPager) { tab, position ->
            tab.text = titles[position]
        }.attach()
    }
}