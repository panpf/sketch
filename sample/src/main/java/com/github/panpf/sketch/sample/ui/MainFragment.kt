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
import android.view.MenuItem
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
                listOf(LinkItemFactory(), ListSeparatorItemFactory()),
                listOf(
                    ListSeparator("Samples"),
                    Link(
                        "Pexels Photos",
                        MainFragmentDirections.actionPexelsPhotoListFragment()
                    ),
                    Link("Giphy GIF", MainFragmentDirections.actionGiphyGifListFragment()),
                    Link(
                        "Local Photos",
                        MainFragmentDirections.actionLocalPhotoListFragment()
                    ),
                    Link(
                        "Local Video",
                        MainFragmentDirections.actionLocalVideoListFragment()
                    ),
                    Link("Huge Image", MainFragmentDirections.actionHugeImageHomeFragment()),
                    Link("RemoteViews", MainFragmentDirections.actionRemoteViewsTestFragment()),

                    ListSeparator("Jetpack Compose"),
                    Link(
                        "Photos On Compose",
                        MainFragmentDirections.actionPexelsPhotoListComposeFragment(),
                        Build.VERSION_CODES.LOLLIPOP
                    ),
                    Link(
                        "GIF On Compose",
                        MainFragmentDirections.actionGiphyGifListComposeFragment(),
                        Build.VERSION_CODES.LOLLIPOP
                    ),
                    Link(
                        "Insanity Test On Compose",
                        MainFragmentDirections.actionInsanityTestComposeFragment(),
                        Build.VERSION_CODES.LOLLIPOP
                    ),

                    ListSeparator("Test"),
                    Link("Fetcher", MainFragmentDirections.actionFetcherTestFragment()),
                    Link("Decoder", MainFragmentDirections.actionDecoderTestFragment()),
                    Link(
                        "Transformation",
                        MainFragmentDirections.actionTransformationTestPagerFragment()
                    ),
                    Link(
                        "ExifOrientation",
                        MainFragmentDirections.actionExifOrientationTestPagerFragment()
                    ),
                    Link(
                        "ProgressIndicator",
                        MainFragmentDirections.actionProgressIndicatorTestFragment()
                    ),
                    Link("Insanity Test", MainFragmentDirections.actionInsanityTestFragment()),
                    Link("Other Test", MainFragmentDirections.actionTestFragment()),
                )
            )
        }
    }
}