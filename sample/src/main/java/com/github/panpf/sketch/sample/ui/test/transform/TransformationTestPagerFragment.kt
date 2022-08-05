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
package com.github.panpf.sketch.sample.ui.test.transform

import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import com.github.panpf.assemblyadapter.pager2.ArrayFragmentStateAdapter
import com.github.panpf.sketch.sample.databinding.TabPagerFragmentBinding
import com.github.panpf.sketch.sample.ui.base.ToolbarBindingFragment
import com.google.android.material.tabs.TabLayoutMediator

class TransformationTestPagerFragment : ToolbarBindingFragment<TabPagerFragmentBinding>() {

    override fun onViewCreated(
        toolbar: Toolbar,
        binding: TabPagerFragmentBinding,
        savedInstanceState: Bundle?
    ) {
        toolbar.title = "Transformation"

        binding.tabPagerPager.adapter = ArrayFragmentStateAdapter(
            fragment = this,
            arrayOf(
                RoundedCornersTransformationTestFragment(),
                CircleCropTransformationTestFragment(),
                RotateTransformationTestFragment(),
                BlurTransformationTestFragment(),
                MaskTransformationTestFragment(),
                MultiTransformationTestFragment(),
            ),
        )

        val titles = arrayOf("ROUNDED_CORNERS", "CIRCLE", "ROTATE", "BLUR", "MASK", "MULTI")
        TabLayoutMediator(binding.tabPagerTabLayout, binding.tabPagerPager) { tab, position ->
            tab.text = titles[position]
        }.attach()
    }
}
