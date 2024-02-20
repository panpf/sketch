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
package com.github.panpf.sketch.sample.ui.gallery

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.fragment.findNavController
import com.github.panpf.sketch.request.ImageResult
import com.github.panpf.sketch.sample.NavMainDirections
import com.github.panpf.sketch.sample.ui.base.BaseComposeFragment
import com.github.panpf.sketch.sample.ui.model.ImageDetail
import com.github.panpf.sketch.sample.ui.model.Photo
import com.github.panpf.sketch.sample.ui.screen.GiphyPhotoListScreenModel
import com.github.panpf.sketch.sample.ui.screen.LocalPhotoListScreenModel
import com.github.panpf.sketch.sample.ui.screen.MainMenu
import com.github.panpf.sketch.sample.ui.screen.PexelsPhotoListScreenModel
import com.github.panpf.sketch.sample.ui.screen.PhotoGrid
import com.github.panpf.sketch.sample.ui.screen.PhotoTab
import com.github.panpf.sketch.sketch
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class ComposeHomeFragment : BaseComposeFragment() {

    private val photoTabs by lazy {
        listOf(
            PhotoTab(
                title = "Local",
                animatedPlaceholder = false,
                photoPagingFlow = LocalPhotoListScreenModel(
                    requireContext(),
                    requireContext().sketch
                ).pagingFlow
            ),
            PhotoTab(
                title = "Pexels",
                animatedPlaceholder = false,
                photoPagingFlow = PexelsPhotoListScreenModel().pagingFlow
            ),
            PhotoTab(
                title = "Giphy",
                animatedPlaceholder = true,
                photoPagingFlow = GiphyPhotoListScreenModel().pagingFlow
            ),
        )
    }

    @OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
    @Composable
    override fun ComposeContent() {
        val coroutineScope = rememberCoroutineScope()
        val pagerState = rememberPagerState { photoTabs.size }
        Column {
            Box(modifier = Modifier.fillMaxWidth()) {
                TopAppBar(title = {
                    Column {
                        Text(text = "Sketch3")
                        Text(text = "Compose", fontSize = 15.sp)
                    }
                })
                TabRow(
                    selectedTabIndex = pagerState.currentPage,
                    modifier = Modifier
                        .fillMaxWidth(0.5f)
                        .align(Alignment.CenterEnd),
                    divider = {}
                ) {
                    photoTabs.forEachIndexed { index, page ->
                        Tab(
                            selected = index == pagerState.currentPage,
                            onClick = {
                                coroutineScope.launch {
                                    pagerState.animateScrollToPage(index)
                                }
                            }
                        ) {
                            Text(text = page.title, Modifier.padding(vertical = 10.dp))
                        }
                    }
                }
            }
            Box(modifier = Modifier.fillMaxSize()) {
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxSize(),
                ) { pageIndex ->
                    val page = photoTabs[pageIndex]
                    PhotoGrid(
                        photoPagingFlow = page.photoPagingFlow,
                        animatedPlaceholder = page.animatedPlaceholder,
                        gridCellsMinSize = 100.dp,
                        onClick = { photos, _, index ->
                            startPhotoPager(photos, index)
                        },
                        onLongClick = { _, _, _, imageResult ->
                            startPhotoInfoDialog(imageResult)
                        }
                    )
                }

                MainMenu(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(20.dp)
                )
            }
        }
    }

    private fun startPhotoPager(items: List<Photo>, position: Int) {
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
            NavMainDirections.actionPhotoPagerComposeFragment(
                imageDetailJsonArray = Json.encodeToString(imageList),
                totalCount = totalCount,
                startPosition = startPosition,
                initialPosition = position
            ),
        )
    }

    private fun startPhotoInfoDialog(imageResult: ImageResult?) {
        findNavController()
            .navigate(PhotoInfoDialogFragment.createNavDirections(imageResult))
    }
}
