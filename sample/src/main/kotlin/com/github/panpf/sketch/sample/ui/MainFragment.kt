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

import android.Manifest.permission
import android.os.Build
import android.os.Build.VERSION_CODES
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.Toolbar
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.panpf.assemblyadapter.recycler.AssemblyRecyclerAdapter
import com.github.panpf.sketch.sample.BuildConfig
import com.github.panpf.sketch.sample.NavMainDirections
import com.github.panpf.sketch.sample.R
import com.github.panpf.sketch.sample.databinding.FragmentRecyclerBinding
import com.github.panpf.sketch.sample.model.Link
import com.github.panpf.sketch.sample.model.ListSeparator
import com.github.panpf.sketch.sample.ui.base.BaseToolbarBindingFragment
import com.github.panpf.sketch.sample.ui.common.link.LinkItemFactory
import com.github.panpf.sketch.sample.ui.common.list.ListSeparatorItemFactory

class MainFragment : BaseToolbarBindingFragment<FragmentRecyclerBinding>() {

    private var pendingStartLink: Link? = null
    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { grantedMap ->
            val pendingStartLink = pendingStartLink ?: return@registerForActivityResult
            this@MainFragment.pendingStartLink = null
            requestLinkPermissionsResult(grantedMap, pendingStartLink)
        }

    override fun onViewCreated(
        toolbar: Toolbar,
        binding: FragmentRecyclerBinding,
        savedInstanceState: Bundle?
    ) {
        toolbar.title = getString(R.string.app_name)
        toolbar.subtitle = BuildConfig.VERSION_NAME

        toolbar.menu.add(0, 0, 0, "Settings").apply {
            setIcon(R.drawable.ic_settings)
            setOnMenuItemClickListener {
                findNavController().navigate(NavMainDirections.actionSettingsFragment())
                true
            }
            setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
        }

        binding.recycler.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = AssemblyRecyclerAdapter(
                itemFactoryList = listOf(LinkItemFactory().setOnItemClickListener { _, _, _, _, data ->
                    startLink(data)
                }, ListSeparatorItemFactory()),
                initDataList = pageList()
            )
        }
    }

    private fun pageList(): List<Any> = listOf(
        ListSeparator("View"),
        Link(
            title = "Local Photos",
            navDirections = NavMainDirections.actionLocalPhotoListFragment(),
            permissions = listOf(permission.READ_EXTERNAL_STORAGE)
        ),
        Link(
            title = "Pexels Photos",
            navDirections = NavMainDirections.actionPexelsPhotoListFragment()
        ),
        Link(
            title = "Giphy GIFs",
            navDirections = NavMainDirections.actionGiphyGifListFragment()
        ),

        ListSeparator("Jetpack Compose"),
        Link(
            title = "Local Photos (Compose)",
            navDirections = NavMainDirections.actionLocalPhotoListComposeFragment(),
            minSdk = VERSION_CODES.LOLLIPOP,
            permissions = listOf(permission.READ_EXTERNAL_STORAGE)
        ),
        Link(
            title = "Pexels Photos (Compose)",
            navDirections = NavMainDirections.actionPexelsPhotoListComposeFragment(),
            minSdk = VERSION_CODES.LOLLIPOP
        ),
        Link(
            title = "Giphy GIFs (Compose)",
            navDirections = NavMainDirections.actionGiphyGifListComposeFragment(),
            minSdk = VERSION_CODES.LOLLIPOP
        ),

        ListSeparator("Test"),
        Link(
            title = "Local Videos",
            navDirections = NavMainDirections.actionLocalVideoListFragment(),
            permissions = listOf(permission.READ_EXTERNAL_STORAGE)
        ),
        Link(
            title = "RemoteViews",
            navDirections = NavMainDirections.actionRemoteViewsFragment()
        ),
        Link(
            title = "Fetcher",
            navDirections = NavMainDirections.actionFetcherTestFragment(),
            permissions = listOf(permission.READ_EXTERNAL_STORAGE)
        ),
        Link(
            title = "Decoder",
            navDirections = NavMainDirections.actionDecoderTestPagerFragment()
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
            title = "ProgressIndicator (Compose)",
            navDirections = NavMainDirections.actionProgressIndicatorTestComposeFragment()
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
        ),
        Link(
            title = "Animatable Placeholder (Compose)",
            navDirections = NavMainDirections.actionAnimatablePlaceholderCompose(),
        ),
        Link(
            title = "Share Element",
            navDirections = NavMainDirections.actionShareElementTestFragment(),
        ),
    ).let {
        if (BuildConfig.DEBUG) {
            it.plus(debugPageList())
        } else {
            it
        }
    }

    private fun debugPageList(): List<Link> = listOf(
        Link(
            title = "Temp Test (Compose)",
            navDirections = NavMainDirections.actionTempTestComposeFragment(),
        ),
    )

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