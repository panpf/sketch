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

package com.github.panpf.sketch.sample.ui.test

import android.app.Application
import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle.State
import androidx.lifecycle.viewModelScope
import com.github.panpf.assemblyadapter.pager2.AssemblyFragmentStateAdapter
import com.github.panpf.sketch.sample.databinding.FragmentTabPagerBinding
import com.github.panpf.sketch.sample.ui.base.BaseToolbarBindingFragment
import com.github.panpf.sketch.sample.ui.base.LifecycleAndroidViewModel
import com.github.panpf.sketch.sample.util.repeatCollectWithLifecycle
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class FetcherTestFragment : BaseToolbarBindingFragment<FragmentTabPagerBinding>() {

    private val viewModel by viewModels<FetcherTestViewModel>()

    override fun getNavigationBarInsetsView(binding: FragmentTabPagerBinding): View {
        return binding.root
    }

    override fun onViewCreated(
        toolbar: Toolbar,
        binding: FragmentTabPagerBinding,
        savedInstanceState: Bundle?
    ) {
        toolbar.title = "Fetcher"

        viewModel.data.repeatCollectWithLifecycle(viewLifecycleOwner, State.STARTED) { data ->
            val imageFromData = data ?: return@repeatCollectWithLifecycle
            val images = imageFromData.map { it.imageUri }

            binding.pager.adapter = AssemblyFragmentStateAdapter(
                fragment = this,
                itemFactoryList = listOf(FetcherTestImageFragment.ItemFactory()),
                initDataList = images
            )

            TabLayoutMediator(binding.tabLayout, binding.pager) { tab, position ->
                tab.text = imageFromData[position].title
            }.attach()
        }
    }

    class FetcherTestViewModel(
        application1: Application
    ) : LifecycleAndroidViewModel(application1) {

        private val _data = MutableStateFlow<List<FetcherTestItem>?>(null)
        val data: StateFlow<List<FetcherTestItem>?> = _data

        init {
            viewModelScope.launch {
                _data.value = buildFetcherTestItems(application1, fromCompose = false)
            }
        }
    }
}