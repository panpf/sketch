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
import com.github.panpf.assemblyadapter.recycler.AssemblyGridLayoutManager
import com.github.panpf.assemblyadapter.recycler.AssemblyRecyclerAdapter
import com.github.panpf.assemblyadapter.recycler.ItemSpan
import com.github.panpf.assemblyadapter.recycler.divider.AssemblyGridDividerItemDecoration
import com.github.panpf.assemblyadapter.recycler.divider.Divider
import com.github.panpf.sketch.sample.BuildConfig
import com.github.panpf.sketch.sample.NavMainDirections
import com.github.panpf.sketch.sample.R
import com.github.panpf.sketch.sample.databinding.FragmentRecyclerBinding
import com.github.panpf.sketch.sample.model.Link
import com.github.panpf.sketch.sample.model.ListSeparator
import com.github.panpf.sketch.sample.ui.base.BaseToolbarBindingFragment
import com.github.panpf.sketch.sample.ui.common.link.LinkItemFactory
import com.github.panpf.sketch.sample.ui.common.list.GridSeparatorItemFactory
import com.github.panpf.tools4a.dimen.ktx.dp2px

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
        toolbar.menu.add(0, 0, 0, "Settings").apply {
            setIcon(R.drawable.ic_settings)
            setOnMenuItemClickListener {
                findNavController().navigate(NavMainDirections.actionSettingsFragment())
                true
            }
            setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
        }

        binding.recycler.apply {
            layoutManager = AssemblyGridLayoutManager.Builder(requireContext(), 2).apply {
                itemSpanByItemFactory(GridSeparatorItemFactory::class to ItemSpan.fullSpan())
            }.build()
            adapter = AssemblyRecyclerAdapter(
                itemFactoryList = listOf(
                    LinkItemFactory().setOnItemClickListener { _, _, _, _, data ->
                        startLink(data)
                    },
                    GridSeparatorItemFactory()
                ),
                initDataList = pageList()
            )
            addItemDecoration(AssemblyGridDividerItemDecoration.Builder(requireContext()).apply {
                divider(Divider.space(16.dp2px))
                footerDivider(Divider.space(16.dp2px))
                sideDivider(Divider.space(16.dp2px))
                useSideDividerAsSideHeaderAndFooterDivider()
            }.build())
        }
    }

    private fun pageList(): List<Any> = listOf(
        ListSeparator("Examples"),

        Link(
            title = "Local Photos\n(View)",
            navDirections = NavMainDirections.actionLocalPhotoListFragment(),
            permissions = listOf(permission.READ_EXTERNAL_STORAGE)
        ),
        Link(
            title = "Local Photos\n(Compose)",
            navDirections = NavMainDirections.actionLocalPhotoListComposeFragment(),
            minSdk = VERSION_CODES.LOLLIPOP,
            permissions = listOf(permission.READ_EXTERNAL_STORAGE)
        ),

        Link(
            title = "Pexels Photos\n(View)",
            navDirections = NavMainDirections.actionPexelsPhotoListFragment()
        ),
        Link(
            title = "Pexels Photos\n(Compose)",
            navDirections = NavMainDirections.actionPexelsPhotoListComposeFragment(),
            minSdk = VERSION_CODES.LOLLIPOP
        ),

        Link(
            title = "Giphy GIFs\n(View)",
            navDirections = NavMainDirections.actionGiphyGifListFragment()
        ),

        Link(
            title = "Giphy GIFs\n(Compose)",
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
            title = "ProgressIndicator\n(View)",
            navDirections = NavMainDirections.actionProgressIndicatorTestFragment()
        ),
        Link(
            title = "ProgressIndicator\n(Compose)",
            navDirections = NavMainDirections.actionProgressIndicatorTestComposeFragment(),
            minSdk = VERSION_CODES.LOLLIPOP
        ),
        Link(
            title = "Display Insanity\n(View)",
            navDirections = NavMainDirections.actionInsanityTestFragment()
        ),
        Link(
            title = "Display Insanity\n(Compose)",
            navDirections = NavMainDirections.actionInsanityTestComposeFragment(),
            minSdk = VERSION_CODES.LOLLIPOP
        ),
        Link(
            title = "Animatable Placeholder\n(View)",
            navDirections = NavMainDirections.actionAnimatablePlaceholder(),
        ),
        Link(
            title = "Animatable Placeholder\n(Compose)",
            navDirections = NavMainDirections.actionAnimatablePlaceholderCompose(),
            minSdk = VERSION_CODES.LOLLIPOP
        ),
        Link(
            title = "Share Element\n(View)",
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
            title = "Temp Test\n(Compose)",
            navDirections = NavMainDirections.actionTempTestComposeFragment(),
            minSdk = VERSION_CODES.LOLLIPOP
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