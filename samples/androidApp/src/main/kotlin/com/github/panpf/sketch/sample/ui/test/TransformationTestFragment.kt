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

import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.Toolbar
import com.github.panpf.assemblyadapter.pager2.ArrayFragmentStateAdapter
import com.github.panpf.sketch.sample.databinding.FragmentTabPagerBinding
import com.github.panpf.sketch.sample.ui.base.BaseToolbarBindingFragment
import com.github.panpf.sketch.sample.ui.test.transform.BlurTransformationTestFragment
import com.github.panpf.sketch.sample.ui.test.transform.CircleCropTransformationTestFragment
import com.github.panpf.sketch.sample.ui.test.transform.MaskTransformationTestFragment
import com.github.panpf.sketch.sample.ui.test.transform.MultiTransformationTestFragment
import com.github.panpf.sketch.sample.ui.test.transform.RotateTransformationTestFragment
import com.github.panpf.sketch.sample.ui.test.transform.RoundedCornersTransformationTestFragment
import com.google.android.material.tabs.TabLayoutMediator

class TransformationTestFragment : BaseToolbarBindingFragment<FragmentTabPagerBinding>() {

    override fun getNavigationBarInsetsView(binding: FragmentTabPagerBinding): View {
        return binding.root
    }

    override fun onViewCreated(
        toolbar: Toolbar,
        binding: FragmentTabPagerBinding,
        savedInstanceState: Bundle?
    ) {
        toolbar.title = "Transformation"

        binding.pager.adapter = ArrayFragmentStateAdapter(
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
        TabLayoutMediator(binding.tabLayout, binding.pager) { tab, position ->
            tab.text = titles[position]
        }.attach()
    }
}
