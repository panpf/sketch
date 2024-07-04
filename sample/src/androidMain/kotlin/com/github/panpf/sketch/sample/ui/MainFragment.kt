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

import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import com.github.panpf.sketch.sample.appSettings
import com.github.panpf.sketch.sample.databinding.FragmentContainerBinding
import com.github.panpf.sketch.sample.ui.base.BaseBindingFragment
import com.github.panpf.sketch.sample.ui.gallery.ComposeHomeFragment
import com.github.panpf.sketch.sample.util.collectWithLifecycle

class MainFragment : BaseBindingFragment<FragmentContainerBinding>() {

    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { _ ->
            appSettings.composePage.collectWithLifecycle(viewLifecycleOwner) {
                val fragment = if (it) ComposeHomeFragment() else ViewNavHostFragment()
                childFragmentManager.beginTransaction()
                    .replace(binding!!.fragmentContainer.id, fragment)
                    .commit()
            }
        }

    override fun onViewCreated(
        binding: FragmentContainerBinding,
        savedInstanceState: Bundle?
    ) {
        permissionLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
    }
}