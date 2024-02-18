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

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.navigation.fragment.findNavController
import androidx.paging.PagingData
import com.github.panpf.sketch.request.ImageResult
import com.github.panpf.sketch.sample.NavMainDirections
import com.github.panpf.sketch.sample.model.ImageDetail
import com.github.panpf.sketch.sample.ui.base.BaseComposeFragment
import com.github.panpf.sketch.sample.ui.model.Photo
import com.github.panpf.sketch.sample.ui.screen.PhotoGrid
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

abstract class BasePhotoListComposeFragment : BaseComposeFragment() {

    abstract val animatedPlaceholder: Boolean
    abstract val photoPagingFlow: Flow<PagingData<Photo>>

    @Composable
    override fun DrawContent() {
        PhotoGrid(
            photoPagingFlow = photoPagingFlow,
            animatedPlaceholder = animatedPlaceholder,
            gridCellsMinSize = 100.dp,
            onClick = { items, _, index -> startImageDetail(items, index) },
            onLongClick = { _, _, _, imageResult -> startPhotoInfoDialog(imageResult) }
        )
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