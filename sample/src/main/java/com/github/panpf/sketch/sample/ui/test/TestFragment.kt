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
package com.github.panpf.sketch.sample.ui.test

import android.Manifest.permission
import android.os.Build
import android.os.Build.VERSION_CODES
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.Toolbar
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.panpf.assemblyadapter.recycler.AssemblyRecyclerAdapter
import com.github.panpf.sketch.sample.NavMainDirections
import com.github.panpf.sketch.sample.databinding.MainFragmentBinding
import com.github.panpf.sketch.sample.model.Link
import com.github.panpf.sketch.sample.ui.base.ToolbarBindingFragment
import com.github.panpf.sketch.sample.ui.common.link.LinkItemFactory
import com.github.panpf.sketch.sample.ui.common.list.ListSeparatorItemFactory

class TestFragment : ToolbarBindingFragment<MainFragmentBinding>() {

    private var pendingStartLink: Link? = null
    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { grantedMap ->
            val pendingStartLink = pendingStartLink ?: return@registerForActivityResult
            this@TestFragment.pendingStartLink = null
            requestLinkPermissionsResult(grantedMap, pendingStartLink)
        }

    override fun onViewCreated(
        toolbar: Toolbar,
        binding: MainFragmentBinding,
        savedInstanceState: Bundle?
    ) {
        toolbar.title = "Test"

        binding.mainRecycler.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = AssemblyRecyclerAdapter(
                listOf(LinkItemFactory().setOnItemClickListener { _, _, _, _, data ->
                    startLink(data)
                }, ListSeparatorItemFactory()),
                listOf(
                    Link(
                        title = "Fetcher",
                        navDirections = NavMainDirections.actionFetcherTestFragment(),
                        permissions = listOf(permission.READ_EXTERNAL_STORAGE)
                    ),
                    Link(
                        title = "Decoder",
                        navDirections = NavMainDirections.actionDecoderTestFragment()
                    ),
                    Link(
                        title = "Transformation",
                        navDirections = NavMainDirections.actionTransformationTestPagerFragment()
                    ),
                    Link(
                        title = "ExifOrientation",
                        navDirections = NavMainDirections.actionExifOrientationTestPagerFragment()
                    ),
                    Link(
                        title = "ProgressIndicator",
                        navDirections = NavMainDirections.actionProgressIndicatorTestFragment()
                    ),
                    Link(
                        title = "Display Insanity",
                        navDirections = NavMainDirections.actionInsanityTestFragment()
                    ),
                    Link(
                        title = "Display Insanity (Compose)",
                        navDirections = NavMainDirections.actionInsanityTestComposeFragment(),
                        minSdk = VERSION_CODES.LOLLIPOP
                    ),
                    Link(
                        title = "Animatable Placeholder",
                        navDirections = NavMainDirections.actionAnimatablePlaceholder(),
                        minSdk = VERSION_CODES.LOLLIPOP
                    ),
                    Link(
                        title = "Share Element",
                        navDirections = NavMainDirections.actionShareElementTestFragment(),
                        minSdk = VERSION_CODES.LOLLIPOP
                    ),
                )
            )
        }
    }

    private fun startLink(data: Link) {
        if (data.minSdk == null || Build.VERSION.SDK_INT >= data.minSdk) {
            val permissions = data.permissions
            if (permissions != null) {
                pendingStartLink = data
                permissionLauncher.launch(permissions.toTypedArray())
            } else {
                findNavController().navigate(data.navDirections)
            }
        } else {
            Toast.makeText(
                context,
                "Must be API ${data.minSdk} or above",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun requestLinkPermissionsResult(grantedMap: Map<String, Boolean>, data: Link) {
        if (grantedMap.values.all { it }) {
            findNavController().navigate(data.navDirections)
        } else {
            Toast.makeText(context, "Please grant permission", Toast.LENGTH_LONG).show()
        }
    }
}