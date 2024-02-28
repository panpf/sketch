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
import com.github.panpf.sketch.sample.appSettingsService
import com.github.panpf.sketch.sample.databinding.FragmentContainerBinding
import com.github.panpf.sketch.sample.ui.base.BaseBindingFragment
import com.github.panpf.sketch.sample.ui.gallery.ComposeHomeFragment
import com.github.panpf.sketch.sample.ui.gallery.ErrorStateFragment
import com.github.panpf.sketch.sample.util.collectWithLifecycle

class MainFragment : BaseBindingFragment<FragmentContainerBinding>() {

    override fun getTopInsetsView(binding: FragmentContainerBinding): View? {
        return binding.root
    }

    override fun onViewCreated(
        binding: FragmentContainerBinding,
        savedInstanceState: Bundle?
    ) {
        appSettingsService.composePage.collectWithLifecycle(viewLifecycleOwner) {
            val fragment = if (it) {
                if (Build.VERSION.SDK_INT >= 21) {
                    ComposeHomeFragment()
                } else {
                    ErrorStateFragment.create("This feature requires Android 5.0 or later")
                }
            } else {
                ViewNavHostFragment()
            }
            childFragmentManager.beginTransaction()
                .replace(binding.fragmentContainer.id, fragment)
                .commit()
        }
    }
}