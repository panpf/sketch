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
package com.github.panpf.sketch.sample.ui.test.fetcher

import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle.State
import com.github.panpf.assemblyadapter.pager2.AssemblyFragmentStateAdapter
import com.github.panpf.sketch.sample.databinding.FragmentTabPagerBinding
import com.github.panpf.sketch.sample.ui.model.ImageDetail
import com.github.panpf.sketch.sample.ui.base.BaseToolbarBindingFragment
import com.github.panpf.sketch.sample.ui.test.ImageFragment
import com.github.panpf.sketch.sample.util.repeatCollectWithLifecycle
import com.google.android.material.tabs.TabLayoutMediator

class FetcherTestFragment : BaseToolbarBindingFragment<FragmentTabPagerBinding>() {

    private val viewModel by viewModels<FetcherTestViewModel>()

    override fun onViewCreated(
        toolbar: Toolbar,
        binding: FragmentTabPagerBinding,
        savedInstanceState: Bundle?
    ) {
        toolbar.title = "Fetcher"

        viewModel.data.repeatCollectWithLifecycle(viewLifecycleOwner, State.STARTED) { data ->
            val imageFromData = data ?: return@repeatCollectWithLifecycle
            val images = imageFromData.uris.map { s ->
                ImageDetail(s, s, s)
            }
            val titles = imageFromData.titles

            binding.pager.adapter = AssemblyFragmentStateAdapter(
                fragment = this,
                itemFactoryList = listOf(ImageFragment.ItemFactory()),
                initDataList = images
            )

            TabLayoutMediator(binding.tabLayout, binding.pager) { tab, position ->
                tab.text = titles[position]
            }.attach()
        }
    }
}
