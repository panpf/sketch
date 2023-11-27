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
package com.github.panpf.sketch.sample.ui.test.exif

import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.viewModels
import com.github.panpf.assemblyadapter.pager2.ArrayFragmentStateAdapter
import com.github.panpf.sketch.decode.internal.exifOrientationName
import com.github.panpf.sketch.sample.databinding.TabPagerFragmentBinding
import com.github.panpf.sketch.sample.ui.base.ToolbarBindingFragment
import com.github.panpf.sketch.sample.ui.test.transform.ExifOrientationTestFragment
import com.github.panpf.sketch.sample.ui.test.transform.ExifOrientationTestPagerViewModel
import com.google.android.material.tabs.TabLayoutMediator

class ExifOrientationTestPagerFragment : ToolbarBindingFragment<TabPagerFragmentBinding>() {

    private val viewModel by viewModels<ExifOrientationTestPagerViewModel>()

    override fun onViewCreated(
        toolbar: Toolbar,
        binding: TabPagerFragmentBinding,
        savedInstanceState: Bundle?
    ) {
        toolbar.title = "ExifOrientation"

        viewModel.data.observe(viewLifecycleOwner) { list ->
            val titles = list.map { exifOrientationName(it.exifOrientation) }
            val fragments = list.map { ExifOrientationTestFragment.create(it.file) }

            binding.tabPagerPager.adapter = ArrayFragmentStateAdapter(this, fragments)

            TabLayoutMediator(binding.tabPagerTabLayout, binding.tabPagerPager) { tab, position ->
                tab.text = titles[position]
            }.attach()
        }
    }
}
