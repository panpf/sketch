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
import android.view.View
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.ViewPager
import com.github.panpf.assemblyadapter.pager.ArrayFragmentStatePagerAdapter
import com.github.panpf.sketch.sample.R
import com.github.panpf.sketch.sample.databinding.FragmentMainBinding
import com.github.panpf.sketch.sample.ui.base.BaseBindingFragment
import com.github.panpf.sketch.sample.ui.gallery.ComposeHomeFragment
import com.github.panpf.sketch.sample.ui.gallery.ErrorStateFragment
import com.github.panpf.sketch.sample.ui.gallery.ViewHomeFragment
import com.github.panpf.sketch.sample.ui.test.TestHomeFragment

class MainFragment : BaseBindingFragment<FragmentMainBinding>() {

    override fun getTopInsetsView(binding: FragmentMainBinding): View? {
        return binding.root
    }

    override fun onViewCreated(
        binding: FragmentMainBinding,
        savedInstanceState: Bundle?
    ) {
        binding.pager.apply {
            val composeFragment = if (Build.VERSION.SDK_INT >= 21) {
                ComposeHomeFragment()
            } else {
                ErrorStateFragment.create("This feature requires Android 5.0 or later")
            }

            adapter = ArrayFragmentStatePagerAdapter(
                fragmentManager = childFragmentManager,
                behavior = FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT,
                templateFragmentList = listOf(
                    composeFragment,
                    ViewHomeFragment(),
                    TestHomeFragment()
                )
            )
            addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
                override fun onPageScrolled(
                    position: Int,
                    positionOffset: Float,
                    positionOffsetPixels: Int
                ) {
                }

                override fun onPageSelected(position: Int) {
                    when (position) {
                        0 -> binding.navigation.selectedItemId = R.id.view
                        1 -> binding.navigation.selectedItemId = R.id.compose
                        2 -> binding.navigation.selectedItemId = R.id.test
                    }
                }

                override fun onPageScrollStateChanged(state: Int) {
                }
            })
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