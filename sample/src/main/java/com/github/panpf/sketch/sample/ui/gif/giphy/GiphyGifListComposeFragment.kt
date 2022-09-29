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
package com.github.panpf.sketch.sample.ui.gif.giphy

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.github.panpf.sketch.sample.NavMainDirections
import com.github.panpf.sketch.sample.model.ImageDetail
import com.github.panpf.sketch.sample.model.Photo
import com.github.panpf.sketch.sample.ui.base.ToolbarFragment
import com.github.panpf.sketch.sample.ui.common.menu.ListMenuViewModel
import com.github.panpf.sketch.sample.ui.photo.pexels.PhotoListContent
import com.github.panpf.sketch.sample.util.observeWithFragmentView
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class GiphyGifListComposeFragment : ToolbarFragment() {

    private val viewModel by viewModels<GiphyGifListViewModel>()
    private val listMenuViewModel by viewModels<ListMenuViewModel> {
        ListMenuViewModel.Factory(
            requireActivity().application,
            showLayoutModeMenu = false,
            showPlayMenu = false,
            fromComposePage = true,
        )
    }

    override fun createView(toolbar: Toolbar, inflater: LayoutInflater, parent: ViewGroup?): View {
        return ComposeView(requireContext()).apply {
            setContent {
                PhotoListContent(viewModel.pagingFlow) { items, _, index ->
                    startImageDetail(items, index)
                }
            }
        }

        // todo Listen to various global settings options and refresh the page
    }

    override fun onViewCreated(toolbar: Toolbar, savedInstanceState: Bundle?) {
        super.onViewCreated(toolbar, savedInstanceState)
        toolbar.apply {
            title = "Giphy GIFs"
            subtitle = "Compose"
            listMenuViewModel.menuFlow.observeWithFragmentView(this@GiphyGifListComposeFragment) { list ->
                menu.clear()
                list.forEachIndexed { groupIndex, group ->
                    group.items.forEachIndexed { index, menuItemInfo ->
                        menu.add(groupIndex, index, index, menuItemInfo.title).apply {
                            menuItemInfo.iconResId?.let { iconResId ->
                                setIcon(iconResId)
                            }
                            setOnMenuItemClickListener {
                                menuItemInfo.onClick(this@GiphyGifListComposeFragment)
                                true
                            }
                            setShowAsAction(menuItemInfo.showAsAction)
                        }
                    }
                }
            }
        }
    }

    private fun startImageDetail(items: List<Photo>, position: Int) {
        val imageList = items.mapIndexedNotNull { index, photo ->
            if (index >= position - 50 && index <= position + 50) {
                ImageDetail(
                    position = index,
                    originUrl = photo.originalUrl,
                    mediumUrl = photo.detailPreviewUrl,
                    thumbnailUrl = photo.listThumbnailUrl,
                )
            } else {
                null
            }
        }
        findNavController().navigate(
            NavMainDirections.actionGlobalImageViewerPagerFragment(
                Json.encodeToString(imageList),
                position,
            ),
        )
    }
}