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

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.Toolbar
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.panpf.assemblyadapter.recycler.AssemblyRecyclerAdapter
import com.github.panpf.sketch.sample.R
import com.github.panpf.sketch.sample.databinding.MainFragmentBinding
import com.github.panpf.sketch.sample.model.Link
import com.github.panpf.sketch.sample.model.ListSeparator
import com.github.panpf.sketch.sample.ui.base.ToolbarBindingFragment
import com.github.panpf.sketch.sample.ui.common.link.LinkItemFactory
import com.github.panpf.sketch.sample.ui.common.list.ListSeparatorItemFactory

class MainFragment : ToolbarBindingFragment<MainFragmentBinding>() {

    private var pendingStartLink: Link? = null
    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { grantedMap ->
            val pendingStartLink = pendingStartLink ?: return@registerForActivityResult
            this@MainFragment.pendingStartLink = null
            requestLinkPermissionsResult(grantedMap, pendingStartLink)
        }

    override fun onViewCreated(
        toolbar: Toolbar,
        binding: MainFragmentBinding,
        savedInstanceState: Bundle?
    ) {
        toolbar.menu.add(0, 0, 0, "Settings").apply {
            setIcon(R.drawable.ic_settings)
            setOnMenuItemClickListener {
                findNavController().navigate(MainFragmentDirections.actionSettingsFragment())
                true
            }
            setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
        }

        binding.mainRecycler.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = AssemblyRecyclerAdapter(
                listOf(LinkItemFactory().setOnItemClickListener { _, _, _, _, data ->
                    startLink(data)
                }, ListSeparatorItemFactory()),
                listOf(
                    ListSeparator("Samples"),
                    Link(
                        title = "Pexels Photos",
                        navDirections = MainFragmentDirections.actionPexelsPhotoListFragment()
                    ),
                    Link(
                        title = "Giphy GIF",
                        navDirections = MainFragmentDirections.actionGiphyGifListFragment()
                    ),
                    Link(
                        title = "Local Photos",
                        navDirections = MainFragmentDirections.actionLocalPhotoListFragment(),
                        permissions = listOf(Manifest.permission.READ_EXTERNAL_STORAGE)
                    ),
                    Link(
                        title = "Local Video",
                        navDirections = MainFragmentDirections.actionLocalVideoListFragment(),
                        permissions = listOf(Manifest.permission.READ_EXTERNAL_STORAGE)
                    ),
                    Link(
                        title = "Huge Image",
                        navDirections = MainFragmentDirections.actionHugeImageHomeFragment()
                    ),
                    Link(
                        title = "RemoteViews",
                        navDirections = MainFragmentDirections.actionRemoteViewsTestFragment()
                    ),

                    ListSeparator("Jetpack Compose"),
                    Link(
                        title = "Photos On Compose",
                        navDirections = MainFragmentDirections.actionPexelsPhotoListComposeFragment(),
                        minSdk = Build.VERSION_CODES.LOLLIPOP
                    ),
                    Link(
                        title = "GIF On Compose",
                        navDirections = MainFragmentDirections.actionGiphyGifListComposeFragment(),
                        minSdk = Build.VERSION_CODES.LOLLIPOP
                    ),
                    Link(
                        title = "Insanity Test On Compose",
                        navDirections = MainFragmentDirections.actionInsanityTestComposeFragment(),
                        minSdk = Build.VERSION_CODES.LOLLIPOP
                    ),

                    ListSeparator("Test"),
                    Link(
                        title = "Fetcher",
                        navDirections = MainFragmentDirections.actionFetcherTestFragment(),
                        permissions = listOf(Manifest.permission.READ_EXTERNAL_STORAGE)
                    ),
                    Link(
                        title = "Decoder",
                        navDirections = MainFragmentDirections.actionDecoderTestFragment()
                    ),
                    Link(
                        title = "Transformation",
                        navDirections = MainFragmentDirections.actionTransformationTestPagerFragment()
                    ),
                    Link(
                        title = "ExifOrientation",
                        navDirections = MainFragmentDirections.actionExifOrientationTestPagerFragment()
                    ),
                    Link(
                        title = "ProgressIndicator",
                        navDirections = MainFragmentDirections.actionProgressIndicatorTestFragment()
                    ),
                    Link(
                        title = "Insanity Test",
                        navDirections = MainFragmentDirections.actionInsanityTestFragment()
                    ),
                    Link(
                        title = "Other Test",
                        navDirections = MainFragmentDirections.actionTestFragment()
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