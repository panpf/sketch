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
package com.github.panpf.sketch.sample.ui.photo.pexels

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle.State
import androidx.navigation.fragment.findNavController
import com.github.panpf.sketch.sample.NavMainDirections
import com.github.panpf.sketch.sample.model.ImageDetail
import com.github.panpf.sketch.sample.model.Photo
import com.github.panpf.sketch.sample.ui.base.BaseToolbarFragment
import com.github.panpf.sketch.sample.ui.common.menu.ToolbarMenuViewModel
import com.github.panpf.sketch.sample.util.repeatCollectWithLifecycle
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class PexelsPhotoListComposeFragment : BaseToolbarFragment() {

    private val pexelsPhotoListViewModel by viewModels<PexelsPhotoListViewModel>()
    private val toolbarMenuViewModel by viewModels<ToolbarMenuViewModel> {
        ToolbarMenuViewModel.Factory(
            requireActivity().application,
            showLayoutModeMenu = true,
            showPlayMenu = false,
            fromComposePage = true,
        )
    }

    override fun createView(toolbar: Toolbar, inflater: LayoutInflater, parent: ViewGroup?): View {
        return ComposeView(requireContext()).apply {
            setContent {
                PhotoList2(
                    photoPagingFlow = pexelsPhotoListViewModel.pagingFlow,
                ) { items, _, index ->
                    startImageDetail(items, index)
                }
            }
        }
    }

    override fun onViewCreated(toolbar: Toolbar, savedInstanceState: Bundle?) {
        super.onViewCreated(toolbar, savedInstanceState)
        toolbar.apply {
            title = "Pexels Photos"
            subtitle = "Compose"
            toolbarMenuViewModel.menuFlow
                .repeatCollectWithLifecycle(viewLifecycleOwner, State.STARTED) { list ->
                    menu.clear()
                    list.forEachIndexed { groupIndex, group ->
                        group.items.forEachIndexed { index, menuItemInfo ->
                            menu.add(groupIndex, index, index, menuItemInfo.title).apply {
                                menuItemInfo.iconResId?.let { iconResId ->
                                    setIcon(iconResId)
                                }
                                setOnMenuItemClickListener {
                                    menuItemInfo.onClick(this@PexelsPhotoListComposeFragment)
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
        val totalCount = items.size
        val startPosition = (position - 50).coerceAtLeast(0)
        val endPosition = (position + 50).coerceAtMost(totalCount - 1)
        val imageList = items.asSequence()
            .filterIndexed { index, _ -> index in startPosition..endPosition }
            .map {
                ImageDetail(
                    originUrl = it.originalUrl,
                    mediumUrl = it.detailPreviewUrl,
                    thumbnailUrl = it.listThumbnailUrl,
                )
            }.toList()
        findNavController().navigate(
            NavMainDirections.actionGlobalImagePagerComposeFragment(
                imageDetailJsonArray = Json.encodeToString(imageList),
                totalCount = totalCount,
                startPosition = startPosition,
                initialPosition = position
            ),
        )
    }
}